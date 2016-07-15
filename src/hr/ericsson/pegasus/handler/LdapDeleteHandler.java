package hr.ericsson.pegasus.handler;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.CustomStr;
import hr.ericsson.pegasus.backend.Data;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;

/**
 * <H1>Ldap Delete Handler</H1>
 * <HR>
 * This handler is used for LDAP delete requests.
 * <HR>
 * @author eigorde
 *
 */
public class LdapDeleteHandler {
	
	/**
	 * Flag which controls how LDAP DELETE operation will behave:<BR>
	 *  If true, this handler will ignore child entries and delete branch.<BR>
	 *  If false, this handler will not allow removal of entry with child entries.<BR>
	 *  <B>NOTE:</B> Some buggy clients might expect that LDAP DELETE operation works regardless
	 *  if child entries exist. 
	 */
	final boolean ALLOW_NON_LEAF_ENTRY_DELETE = true;
	
	/**
	 * Default message to indicate end of LDAP operation.
	 */
	private final DeleteResponseProtocolOp deleteResponseProtocolOp;
	
	/**
	 * New instance of this handler.
	 */
	public LdapDeleteHandler() {
		
		final int rc = ResultCode.SUCCESS.intValue();
		final String matchedDN = null;
		final String diagnosticMessage = null;
		final List<String> referralURLs = null;
		
		deleteResponseProtocolOp = new DeleteResponseProtocolOp(rc, matchedDN,
				diagnosticMessage, referralURLs);
	}
	

    /**
     * LDAP Delete request handling.
     *  
     * @param messageID message ID of request
     * @param request {@link DeleteRequestProtocolOp} instance
     * @return {@link LDAPMessage} response to request
     */
	@SuppressWarnings("unused")
	public LDAPMessage processDeleteRequest(int messageID,
			DeleteRequestProtocolOp request) {

		CustomStr dn = new CustomStr(request.getDN());
		
		SortedMap<CustomStr, Data> map = Pegasus.myBackend.getSubEntries(dn);
		
		if (map == null) {

			Pegasus.failedDelete++;
			
			// Do not allow delete operation on invalid DN.			
	        return new LDAPMessage(messageID, new DeleteResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "DN not found.", null));
			
		}
		
		if (map.size() > 0 && !(ALLOW_NON_LEAF_ENTRY_DELETE) ) {
			
			Pegasus.failedDelete++;
			
			// Do not allow delete operation of entries that have child elements.			
	        return new LDAPMessage(messageID, new DeleteResponseProtocolOp(
	                ResultCode.NOT_ALLOWED_ON_NONLEAF_INT_VALUE, request.getDN(),
	                "Child element(s) still exist.", null));
	        
		}
		
		boolean retVal = Pegasus.myBackend.deleteEntry(dn);

		if (retVal) {
			Pegasus.deleteRequest++;
		}
		else {
			Pegasus.failedDelete++;
	        
			// Entry probably not found in backend.
			return new LDAPMessage(messageID, new DeleteResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "DN not found.", null));
			
		}
		
		return new LDAPMessage(messageID, deleteResponseProtocolOp,
	            Collections.<Control>emptyList());
	}


}
