package hr.ericsson.pegasus.backend;

import hr.ericsson.pegasus.Pegasus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * <H1>Transfer Backend Data</H1>
 * <HR>
 * This class is used to transfer backend data to a peer.
 * <I>The peer</I> here is a LDAP server that has empty backend or
 * a backend with incomplete data which should join a group of loaded
 * LDAP servers.<BR>
 * <BR>
 * Such state mainly happens when a LDAP server drops out and needs to 
 * join again to a group. A typical scenario is a case with <I>fallback</I>
 * LDAP servers/services or server farms where often new servers have to join
 * a group and have backend populated with data.<BR>
 * <BR>
 * Implementation has {@link Runnable} interface and should be started from a thread.
 * A thread should have low priority.
 * <HR>   
 * @author eigorde
 *
 */
public class TransferBackendData implements Runnable {

	public static final String bindDN = "transferSysUser";
	public static final String bindPassword = "transferSECRET";
	
	private ConcurrentBackend backend;
	
	private String remoteHost;
	int port;
	
	/**
	 * Instance of this class should have a reference to a backend instance and remote host socket
	 * for opening a tcp connection and transferring backend data to remote LDAP service.
	 * @param backend local backend instance
	 * @param remoteHost remote ip address or host name
	 * @param port remote port, usually tcp port <I>389</I>
	 */
	public TransferBackendData(ConcurrentBackend backend, String remoteHost, int port) {
		this.backend = backend;
		this.remoteHost = remoteHost;
		this.port = port;
	}

	@Override
	public void run() {
		if (backend != null) {

			LDAPConnection ldapConnection;

			try {
				ldapConnection = new LDAPConnection(remoteHost, port, bindDN, bindPassword);
			} catch (LDAPException e) {
				if (e.getResultCode() == ResultCode.INVALID_CREDENTIALS) {
					Pegasus.log("Connection to " + remoteHost + ":" + port + " socket refused.");
					Pegasus.log("Check on remote side that socket is not marked for dereferencing.");
				}
				else {
					Pegasus.log("Connection to " + remoteHost + ":" + port + " failed.");
				}
				return;
			}

			CustomStr rootDN = backend.getRootDN();
			Entry rootEntry = backend.getEntry(rootDN);
			try {
				ldapConnection.add(rootEntry);
			} catch (LDAPException e) {
				/*
				 *  This is usually if root entry already exist on remote side,
				 *  and is not considered as error.
				 */
				if (e.getResultCode() == ResultCode.ENTRY_ALREADY_EXISTS) {
					/*
					 * OK. Doesn't matter if root entry already exist.
					 */
				}
				else {
					e.printStackTrace();
				}
			}

			long startTime = System.currentTimeMillis();
			Pegasus.log("Transfer of backend data to " + remoteHost + ":" + port + " started.");
			
			/*
			 * Start transfer to remote peer.
			 */
			transferBackendData(ldapConnection, backend.getSubEntries(rootDN), rootDN);
			
			long endTime = System.currentTimeMillis();
			Pegasus.log("Transfer of backend data to " + remoteHost + ":" + port + " finished. Elapsed: " + Pegasus.formatTime(endTime - startTime));
			
			ldapConnection.close();
		}
	}
	
	/**
	 * Start backend data transfer. Note that root entry should be transferred first if possible.
	 * This function will recursively call itself until all entries from backend are transferred to remote.
	 * 
	 * @param ldapConnection remote TCP connection to LDAP server, instance of {@link LDAPConnection}
	 * @param subEntries sub-entries of root entry
	 * @param baseDN starting value is root DN, each call it gets RDN appended at beginning
	 */
	private void transferBackendData(LDAPConnection ldapConnection, SortedMap<CustomStr, Data> subEntries, CustomStr baseDN) {

		if (subEntries != null && subEntries.size() > 0) {
			for (CustomStr key : subEntries.keySet()) {
				Data data = subEntries.get(key);
				if (data != null) {
					
					Thread.yield();
					
					String dn = key.toString() + "," + baseDN.toString();
					
					try {
						SearchResult searchResult = ldapConnection.search(
								dn,							// base DN 
								SearchScope.BASE, 			// search scope should be base
								DereferencePolicy.NEVER, 	// never dereference alias entry
								0, 							// size limit, no need for that
								0, 							// time limit, too
								false, 						// types only
								Filter.create("(objectClass=*)")); // default filter should match any entry
						
						Entry remoteEntry = null;						
						if (searchResult.getSearchEntries().size() == 1) {
							remoteEntry = searchResult.getSearchEntries().get(0);
						}
						
						if (remoteEntry == null) {
							/*
							 * Entry does not exist, add it.
							 */
							ldapConnection.add(data.entry);
						}
						else if (remoteEntry.equals(data.entry)) {
							/*
							 * Ok, no need to update entry.
							 */
						}
						else {
							/*
							 * Entry probably modified, remote side needs update.
							 */
							List<Modification> mods = new ArrayList<Modification>();
							for (Attribute attr : data.entry.getAttributes()) {
								if (!data.entry.getDN().startsWith(attr.getName())) {								
									/*
									 * Add to modification list only attributes that
									 * do not violate RDN constraint. 
									 */
									mods.add(new Modification(
											ModificationType.REPLACE, 
											attr.getName(), 
											attr.getValue()));
								}
							}
							ModifyRequest modifyRequest = new ModifyRequest(data.entry.getDN(), mods);

							ldapConnection.modify(modifyRequest);
								
						}
					} catch (LDAPException e) {	
						
						if (e.getResultCode() == ResultCode.SERVER_DOWN) {
							Pegasus.log("Remote peer down.");
							return;
						}
						else if (e.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
							/*
							 * Entry does not exist, add it.
							 */
							try {
								ldapConnection.add(data.entry);
							} catch (LDAPException e1) {
								Pegasus.log("Error in LDAP ADD operation. Remote peer refused entry " + dn);
								e1.printStackTrace();
							}
						}
						else {
							e.printStackTrace();
						}
					}
					
					transferBackendData(ldapConnection, data.map, new CustomStr(dn));
				}
			}
			
		}
	}

}
