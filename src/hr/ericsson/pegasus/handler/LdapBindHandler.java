package hr.ericsson.pegasus.handler;

import java.util.List;

import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;

import hr.ericsson.pegasus.Pegasus;

/**
 * <H1>Ldap Bind Handler</H1>
 * <HR>
 * This handler is used for LDAP bind requests.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class LdapBindHandler {
	 
	/**
	 * Default message to indicate end of LDAP operation.
	 */
	private final BindResponseProtocolOp bindResponseProtocolOp;
	
	/**
	 * New instance of this handler.
	 */
	public LdapBindHandler() {
		
		final int rc = ResultCode.SUCCESS.intValue();
		final String matchedDN = null;
		final String diagnosticMessage = null;
		final List<String> referralURLs = null;
		
		bindResponseProtocolOp = new BindResponseProtocolOp(rc, matchedDN,
				diagnosticMessage, referralURLs, null);
	}
	
    /**
     * LDAP Bind request handling.
     *  
     * @param messageID message ID of request
     * @param request {@link BindRequestProtocolOp} instance
     * @return {@link LDAPMessage} response to request
     */
	public LDAPMessage processBindRequest(int messageID,
			BindRequestProtocolOp request) {
		
		Pegasus.clientConnections++;
		
	    return new LDAPMessage(messageID, bindResponseProtocolOp,
	    		StaticUtils.NO_CONTROLS);
	}

}
