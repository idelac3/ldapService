package hr.ericsson.pegasus.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.schema.EntryValidator;
import com.unboundid.util.StaticUtils;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.CustomStr;

/**
 * <H1>Ldap Modify Handler</H1>
 * <HR>
 * This handler is used for LDAP modify requests.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class LdapModifyHandler {
	 
	/**
	 * Default message to indicate end of LDAP operation.
	 */
	private final ModifyResponseProtocolOp modifyResponseProtocolOp;
	
	/**
	 * Flag for dereferencing alias entry before apply of LDAP modification.
	 */
	private boolean aliasDeref;
	
	/**
	 * New instance of this handler.
	 * 
	 * @param aliasDeref set alias dereferencing flag here
	 */
	public LdapModifyHandler(boolean aliasDeref) {
		
		final int rc = ResultCode.SUCCESS.intValue();
		final String matchedDN = null;
		final String diagnosticMessage = null;
		final List<String> referralURLs = null;
		
		modifyResponseProtocolOp = new ModifyResponseProtocolOp(rc, matchedDN,
				diagnosticMessage, referralURLs);
		
		this.aliasDeref = aliasDeref;
		
	}
	

    /**
     * LDAP Modify request handling.
     *  
     * @param messageID message ID of request
     * @param request {@link ModifyRequestProtocolOp} instance
     * @return {@link LDAPMessage} response to request
     */
	public LDAPMessage processModifyRequest(int messageID,
			ModifyRequestProtocolOp request) {

		CustomStr dn = new CustomStr(request.getDN());
		
		/*
		 * Verify that DN in request has valid root DN at the end.
		 */
		CustomStr rootDN = Pegasus.myBackend.getRootDN();
		
		/*
		 * Case when invalid DN is in ADD request.
		 */
		if ( !dn.endsWith(rootDN) ) {
			
			/*
			 * Break on any attempt with wrong DN.
			 */
			
			Pegasus.failedModify++;
			
	        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Invalid DN.", null), StaticUtils.NO_CONTROLS);
	    
		}
		
		/*
		 * Get entry here. Might be alias not a real entry.
		 */
		Entry entry = Pegasus.myBackend.getEntry(dn); 
		
		/*
		 * Fail immediately with error code 32 if entry is not found on backend.
		 */
		if (entry == null) {
			
			Pegasus.failedModify++;
			
	        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Entry does not exist.", null), StaticUtils.NO_CONTROLS);
	        
		}
		
		/*
		 * Perform alias dereferencing if flag is set.
		 */
		if (aliasDeref) {
			
			/*
			 * List that holds alias DNs.
			 */
			Set<CustomStr> aliasDNs = new TreeSet<CustomStr>();
			
			/*
			 * Check that entry is really alias.
			 */
			while (entry.hasAttributeValue("objectClass", "alias") &&
					entry.hasAttribute("aliasedObjectName")) {

				//  Get aliasedObjectName attribute value.
				String aliasedObjectName = entry.getAttributeValue("aliasedObjectName");
				
				// Change DN to path where alias points to.
				dn = new CustomStr(aliasedObjectName);

				// Check that DN is not already in list. If it is then we have a infinite loop.
				if (aliasDNs.contains(dn)) {
					entry = null;
					break;
				}
				
				// Add it to list.
				aliasDNs.add(dn);
								
				// Update entry instance with (potentially) dereferenced entry from backend.
				entry = Pegasus.myBackend.getEntry(dn);

				if (entry == null) {
					break;
				}
			}
						
			// Update Modify Request instance.
			request = new ModifyRequestProtocolOp(dn.toString(), request.getModifications());

		}		

		/*
		 * Check again for valid entry instance.
		 */
		if (entry == null) {
			
			Pegasus.failedModify++;
			
	        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
	                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
	                "Entry does not exist.", null), StaticUtils.NO_CONTROLS);
	        
		}
		
		Entry modifiedEntry = null;
		
		try {

			/*
			 *  Build custom list of modifications, due limitation that
			 *  it is not allowed to change attribute which is present in DN. 
			 */
			List<Modification> modifications = new ArrayList<Modification>();
			for (Modification mod : request.getModifications()) {
				if (dn.toString().toUpperCase().startsWith(mod.getAttributeName().toUpperCase() + "=")) {
					// Do not add such attribute to list.
				}
				else {
					// Ok, attribute is not part of DN, then add it to modification list.
					modifications.add(mod);
				}
			}
			
			// Check that we have modifications at all, and apply them to entry.
			if (modifications.size() > 0) {
				modifiedEntry = Entry.applyModifications(entry, true, modifications);

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
					
					if (validator.entryIsValid(modifiedEntry, invalidReasons)) {
						// Ok, entry pass schema validation.
					}
					else {
						// Entry did not pass schema validation.
						Pegasus.debug("Modification of '" + modifiedEntry.getDN() + "' did not pass schema validation.");
						for (Modification modification : modifications) {
							
							if (modification.getValues().length == 1) {
								Pegasus.debug(modification.getAttributeName() + ": " + modification.getValues()[0]);
							}
							else {
								Pegasus.debug(modification.getAttributeName() + ": ");
								for (String value : modification.getValues()) {
									Pegasus.debug(" " + value);
								}
							}
						}
						
						Pegasus.debug("Invalid entry reasons:");
						for (String reason : invalidReasons) {
							Pegasus.debug(reason);
						}
						
				        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
				                ResultCode.NAMING_VIOLATION_INT_VALUE, dn.toString(),
				                invalidReasons.get(0), null), StaticUtils.NO_CONTROLS);
					}
				}
			}

		} catch (LDAPException e) {
			
			e.printStackTrace();
			
			Pegasus.failedModify++;
			
	        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
	                e.getResultCode().intValue(), dn.toString(),
	                e.getMessage(), null), StaticUtils.NO_CONTROLS);
		}
		
		Pegasus.modifyRequests++;
		
		// If the modification list is empty, then this object is null.
		if (modifiedEntry != null) {
			if (Pegasus.myBackend.modifyEntry(dn, modifiedEntry)) {

				// Result is positive, return default return message.
				return new LDAPMessage(messageID, modifyResponseProtocolOp,
						StaticUtils.NO_CONTROLS);
						
			}
			else {
				
				// Modification failed.
		        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(
		                ResultCode.NO_SUCH_OBJECT_INT_VALUE, request.getDN(),
		                "Entry does not exist.", null), StaticUtils.NO_CONTROLS);
		        	
			}
		}
		
		return new LDAPMessage(messageID, modifyResponseProtocolOp,
				StaticUtils.NO_CONTROLS);
		
	}

}
