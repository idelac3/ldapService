package hr.ericsson.pegasus.handler;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.CustomStr;

import java.util.Collections;
import java.util.List;

import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ResultCode;


/**
 * <H1>Ldap Add Handler</H1>
 * <HR>
 * This handler is used for LDAP bind requests.
 * <HR>
 * @author eigorde
 *
 */
public class LdapAddHandler {
	 
	/**
	 * Default message to indicate end of LDAP operation.
	 */
	private final AddResponseProtocolOp addResponseProtocolOp;
	
	/**
	 * New instance of this handler.
	 */
	public LdapAddHandler() {
		
		final int rc = ResultCode.SUCCESS.intValue();
		final String matchedDN = null;
		final String diagnosticMessage = null;
		final List<String> referralURLs = null;
		
		addResponseProtocolOp = new AddResponseProtocolOp(rc, matchedDN,
				diagnosticMessage, referralURLs);
	}
	
    /**
     * LDAP Add request handling.
     *  
     * @param messageID message ID of request
     * @param request {@link AddRequestProtocolOp} instance
     * @return {@link LDAPMessage} response to request
     */
	public LDAPMessage processAddRequest(int messageID,
			AddRequestProtocolOp request) {
		
		CustomStr dn = new CustomStr(request.getDN());
		
		if (Pegasus.myBackend.getEntry(dn) != null) {
			
			Pegasus.failedAdd++;
			
	        return new LDAPMessage(messageID, new AddResponseProtocolOp(
	                ResultCode.ENTRY_ALREADY_EXISTS_INT_VALUE, request.getDN(),
	                "Entry already exist.", null));			
		}

		Entry entry = new Entry(request.getDN(), request.getAttributes());
		
		if (Pegasus.myBackend.addEntry(dn, entry)) {

			Pegasus.addRequest++;
		}
		else {
			
			Pegasus.failedAdd++;
			
	        return new LDAPMessage(messageID, new AddResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Invalid DN value.", null));			
		}
		
		return new LDAPMessage(messageID, addResponseProtocolOp,
	            Collections.<Control>emptyList());
	}

}
