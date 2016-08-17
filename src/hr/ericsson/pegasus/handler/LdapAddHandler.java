package hr.ericsson.pegasus.handler;

import java.util.ArrayList;
import java.util.List;

import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.schema.EntryValidator;
import com.unboundid.util.StaticUtils;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.CustomStr;


/**
 * <H1>Ldap Add Handler</H1>
 * <HR>
 * This handler is used for LDAP ADD requests.
 * <HR>
 * @author igor.delac@gmail.com
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
		
		/*
		 * Verify that DN in request has valid root DN at the end.
		 */
		CustomStr rootDN = Pegasus.myBackend.getRootDN();
		
		/*
		 * Case when invalid DN is in ADD request.
		 */
		if ( !dn.endsWith(rootDN) ) {
			
			Pegasus.failedAdd++;
			
	        return new LDAPMessage(messageID, new AddResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Invalid DN value.", null), StaticUtils.NO_CONTROLS);
		}
		
		/*
		 * Case when entry already exists.
		 */
		if (Pegasus.myBackend.getEntry(dn) != null) {
			
			Pegasus.failedAdd++;
			
	        return new LDAPMessage(messageID, new AddResponseProtocolOp(
	                ResultCode.ENTRY_ALREADY_EXISTS_INT_VALUE, request.getDN(),
	                "Entry already exist.", null), StaticUtils.NO_CONTROLS);			
		}

		Entry entry = new Entry(request.getDN(), request.getAttributes());
		

		/*
		 * Check entry against schema. Only if schema object instance is present.
		 */
		if (Pegasus.schema != null) {
			
			/*
			 * Entry Validator which uses schema definition(s).
			 */
			EntryValidator validator = new EntryValidator(Pegasus.schema);
			validator.setCheckProhibitedObjectClasses(false);
			
			/*
			 * List of reasons why entry is invalid.
			 */
			List<java.lang.String> invalidReasons = new ArrayList<String>();
			
			if (validator.entryIsValid(entry, invalidReasons)) {
				// Ok, entry pass schema validation.
			}
			else {
				// Entry did not pass schema validation.
				Pegasus.debug("Add of '" + entry.getDN() + "' did not pass schema validation.");				
				Pegasus.debug("Invalid entry reasons:");
				for (String reason : invalidReasons) {
					Pegasus.debug(reason);
				}
				
				Pegasus.failedAdd++;
				
		        return new LDAPMessage(messageID, new AddResponseProtocolOp(
		                ResultCode.OBJECT_CLASS_VIOLATION_INT_VALUE, request.getDN(),
		                invalidReasons.get(0), null), StaticUtils.NO_CONTROLS);
			}
		}
		
		if (Pegasus.myBackend.addEntry(dn, entry)) {

			Pegasus.addRequest++;
		}
		else {
			
			Pegasus.failedAdd++;
			
	        return new LDAPMessage(messageID, new AddResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Invalid DN value.", null), StaticUtils.NO_CONTROLS);			
		}
		
		return new LDAPMessage(messageID, addResponseProtocolOp,
				StaticUtils.NO_CONTROLS);
	}

}
