package hr.ericsson.pegasus.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.schema.AttributeSyntaxDefinition;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.ldap.sdk.schema.MatchingRuleDefinition;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import com.unboundid.util.StaticUtils;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.CustomStr;
import hr.ericsson.pegasus.backend.Data;
import io.netty.channel.ChannelHandlerContext;

/**
 * <H1>Ldap Search Handler</H1>
 * <HR>
 * This handler is used for LDAP search requests.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class LdapSearchHandler {

	/**
	 * Default message to indicate end of LDAP operation.
	 */
	private final SearchResultDoneProtocolOp searchResultDoneProtocolOp;
	
	/**
	 * A reference to instance of {@link ChannelHandlerContext}. <BR>
	 * <BR>
	 * Note that this has to be set,
	 * or methods like:<BR>
	 * {@link #sendSearchResultEntry(int, SearchResultEntry, Control...)}<BR>
	 * {@link #processSearchRequest(int, SearchRequestProtocolOp)}<BR>
	 * will fail with:<BR>
	 * {@link NullPointerException}. 
	 */
	private ChannelHandlerContext ctx;
	
	/**
	 * Counting variable that is used in: <BR>
	 * {@link #subLevels(int, SearchRequestProtocolOp, int, boolean)} method call.
	 * Should be always set to 0 before method call. Used internally by method.
	 */
	private int counting;
	
	/**
	 * New instance of this handler.
	 */
	public LdapSearchHandler() {
		
		final int rc = ResultCode.SUCCESS.intValue();
		final String matchedDN = null;
		final String diagnosticMessage = null;
		final List<String> referralURLs = null;
		
		searchResultDoneProtocolOp = new SearchResultDoneProtocolOp(rc, matchedDN,
				diagnosticMessage, referralURLs);

		this.ctx = null;
		
	}
	
	/**
	 * Before {@link #processSearchRequest(int, SearchRequestProtocolOp)} use this method to set
	 * {@link ChannelHandlerContext} instance.
	 * 
	 * @param ctx {@link ChannelHandlerContext} instance, never <I>null</I> value
	 */
	public void setChannelHandler(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	/**
     * LDAP Search request handling.
     *  
     * @param messageID message ID of request
     * @param request {@link SearchRequestProtocolOp} instance
     * @return {@link LDAPMessage} response to request
	 * @throws LDAPException 
     */
	public LDAPMessage processSearchRequest(int messageID,
			SearchRequestProtocolOp request) throws LDAPException {

		/*
		 * Wrap base DN string into CustomStr instance.
		 */
		CustomStr dn = new CustomStr(request.getBaseDN());

		/*
		 * Verify that DN in request has valid root DN at the end.
		 */
		CustomStr rootDN = Pegasus.myBackend.getRootDN();
		
		/*
		 * Here are some special cases when base DN is empty.
		 */
		if (request.getBaseDN().length() == 0) {

			for (String attribute : request.getAttributes()) {
				/*
				 * Special case: root / base DN is empty in LDAP SEARCH request,
				 * but Attribute List contains one attribute: 'namingcontexts'
				 * Should return list of root DNs (for now only 1 root DN). 
				 */
				if ( attribute.equalsIgnoreCase("namingcontexts") ) {
					// Give back to client root DN value.
					Entry rootEntry = new Entry(new DN(""), new Attribute("namingcontexts", rootDN.toString()));

					// Build SearchResultEntry object.
					SearchResultEntry searchEntry = new SearchResultEntry(rootEntry);

					// Increment statistic.
					Pegasus.entryResults++;

					// Send each search entry to channel.
					sendSearchResultEntry(messageID, searchEntry,
							searchEntry.getControls());

					return new LDAPMessage(messageID, searchResultDoneProtocolOp,
							StaticUtils.NO_CONTROLS);					
				}

				/*
				 * Special case: root / base DN is empty in LDAP SEARCH request,
				 * but Attribute List contains one attribute: 'subschemasubentry'
				 * Should return root DN for Schema DIT, usually cn=Subschema. 
				 */
				if (attribute.equalsIgnoreCase("subschemasubentry")) {
					
					// Form built-in entry here.
					Entry subschemaSubentry = new Entry(
							new DN(Pegasus.myBackend.getRootDN().toString()),
							new Attribute("subschemaSubentry", "cn=Subschema"));
					
					// Send built-in entry.
					SearchResultEntry searchEntry = new SearchResultEntry(subschemaSubentry);
					sendSearchResultEntry(messageID, searchEntry, searchEntry.getControls());
					
					// Increment statistic.
					Pegasus.entryResults++;
					
					// Break execution of this method here.
				    return new LDAPMessage(messageID, searchResultDoneProtocolOp,
				            StaticUtils.NO_CONTROLS);
				    
				}

			}

		}
		
		/*
		 * A request with base DN set to "cn=Subschema" is processed as schema request.
		 */
		if (request.getBaseDN().equalsIgnoreCase("cn=Subschema") ) {
			
			/*
			 * Schema requests usually have base DN set to empty string, "" value.
			 */
			return processSchemaRequest(messageID, request.getBaseDN(), request.getAttributes());
			
		}
		
		/*
		 * Case when invalid DN is in SEARCH request, deny such request.
		 */
		if (!dn.endsWith(rootDN) || request.getBaseDN().length() == 0) {

			/*
			 *  Return error code 32 (NO_SUCH_OBJECT) when DN is not found or invalid.
			 */
		    return new LDAPMessage(messageID,
		    		new SearchResultDoneProtocolOp(ResultCode.NO_SUCH_OBJECT_INT_VALUE, "Root DN don't match: '" + dn.toString() + "'", null, null),
		            StaticUtils.NO_CONTROLS);
		}
			
		/*
		 * Adjust limit of returned entries.
		 */
		int countLimit = Pegasus.countLimit;
		
		if (countLimit == 0) {
			// Server side limit not set, try to use limit from client request.
			countLimit = request.getSizeLimit();
		}
		else {
			// Check that client limit is lower then server side limit. Then use it.
			if (request.getSizeLimit() > 0 &&
					request.getSizeLimit() < countLimit) {
				// Use limit in ldap search message if it's lower than
				// server-side configured count limit.
				countLimit = request.getSizeLimit();
			}
		}

		if (request.getScope() == SearchScope.BASE) {
			
			Pegasus.searchRequestsBase++;

			Entry entryData = Pegasus.myBackend.getEntry(dn);
			
			if (entryData != null) {
				
				/*
				 * Base entry is dereferenced if flag is set to ALWAYS or FINDING.
				 * Ref.
				 * http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/aliases.html
				 */
				if (request.getDerefPolicy() == DereferencePolicy.ALWAYS
						|| request.getDerefPolicy() == DereferencePolicy.FINDING) {
					// Try to dereference alias, if possible.
					entryData = derefEntry(entryData);
				}
				
				try {
					Entry entry = entryData;
					// Check that it matches filter.
					if (request.getFilter().matchesEntry(entry)) {
						List<String> requestedAttributes = request.getAttributes();
						Entry newEntry = getEntryWithRequestedAttributes(entry, requestedAttributes);

						// Build SearchResultEntry object.
						SearchResultEntry searchEntry = new SearchResultEntry(newEntry);

						// Increment statistic.
						Pegasus.entryResults++;

						// Send each search entry to channel.
						sendSearchResultEntry(messageID, searchEntry,
								searchEntry.getControls());
						
						// Break execution of this method here. No need to go further.
					    return new LDAPMessage(messageID, searchResultDoneProtocolOp,
					            StaticUtils.NO_CONTROLS);
					}
					
				}
				catch (LDAPException ex) {
					ex.printStackTrace();
				}
				
			}
			else {
	
				/*
				 *  Return error code 32 (NO_SUCH_OBJECT) when DN is not found or invalid.
				 */
			    return new LDAPMessage(messageID,
			    		new SearchResultDoneProtocolOp(ResultCode.NO_SUCH_OBJECT_INT_VALUE, dn.toString(), null, null),
			            StaticUtils.NO_CONTROLS);
			}

		}
		else if (request.getScope() == SearchScope.ONE) { 
			/*
			 * Only sub branches should be returned here. First level.
			 */
			
			Pegasus.searchRequestsOne++;
			
			Entry entry = Pegasus.myBackend.getEntry(dn);
			
			// Check that it exist at all.
			if (entry == null) {
				// Base entry does not exist at all, finish with error result.
			    return new LDAPMessage(messageID,
			    		new SearchResultDoneProtocolOp(ResultCode.NO_SUCH_OBJECT_INT_VALUE, dn.toString(), null, null),
			            StaticUtils.NO_CONTROLS);

			}
			else {
				/*
				 * Base entry is dereferenced if flag is set to ALWAYS, SEARCHING or FINDING.
				 * Ref.
				 * http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/aliases.html
				 */				
				if (request.getDerefPolicy() == DereferencePolicy.ALWAYS
						|| request.getDerefPolicy() == DereferencePolicy.FINDING
						|| request.getDerefPolicy() == DereferencePolicy.SEARCHING) {
					/*
					 * Try to dereference alias, if possible.
					 */
					entry = derefEntry(entry);

					/*
					 * Update DN also.		
					 */
					dn = new CustomStr(entry.getDN());

					/*
					 * Update SearchRequest object also.
					 */
					request = new SearchRequestProtocolOp(
							dn.toString(), request.getScope(), 
							request.getDerefPolicy(), request.getSizeLimit(), request.getTimeLimit(), 
							request.typesOnly(), request.getFilter(), request.getAttributes());
				}

				/*
				 *  Return sub entries and reset counting counter.
				 */
				counting = 0;
				subLevels(messageID, request, countLimit, false);

			}

		}
		else if (request.getScope() == SearchScope.SUB) {
			/*
			 * A base branch plus all sub-branch entries including
			 * their sub-branch entries should be returned.
			 * Ref.
			 * http://www.idevelopment.info/data/LDAP/LDAP_Resources/SEARCH_Setting_the_SCOPE_Parameter.shtml
			 */
			
			Pegasus.searchRequestsSub++;
			
			/*
			 * Get base Entry object from backend.
			 */
			Entry baseEntry = Pegasus.myBackend.getEntry(dn);
			
			// Check that it exist at all.
			if (baseEntry == null) {
				// Base entry does not exist at all, finish with error result.
			    return new LDAPMessage(messageID,
			    		new SearchResultDoneProtocolOp(ResultCode.NO_SUCH_OBJECT_INT_VALUE, dn.toString(), null, null),
			            StaticUtils.NO_CONTROLS);

			}
			else {
				/*
				 * Base entry is dereferenced if flag is set to ALWAYS, SEARCHING or FINDING.
				 * Ref.
				 * http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/aliases.html
				 */				
				if (request.getDerefPolicy() == DereferencePolicy.ALWAYS
						|| request.getDerefPolicy() == DereferencePolicy.FINDING
						|| request.getDerefPolicy() == DereferencePolicy.SEARCHING) {
					/*
					 * Try to dereference alias, if possible.
					 */
					baseEntry = derefEntry(baseEntry);

					/*
					 * Update DN also.		
					 */
					dn = new CustomStr(baseEntry.getDN());

					/*
					 * Update SearchRequest object also.
					 */
					request = new SearchRequestProtocolOp(
							dn.toString(), request.getScope(), 
							request.getDerefPolicy(), request.getSizeLimit(), request.getTimeLimit(), 
							request.typesOnly(), request.getFilter(), request.getAttributes());
				}
				
				/*
				 * Return also base entry. This is main difference when
				 * searching with scope sub level and scope one level.
				 */
				try {
					Entry entry = baseEntry;
					// Check that it matches filter.
					if (request.getFilter().matchesEntry(entry)) {
						List<String> requestedAttributes = request.getAttributes();
						Entry newEntry = getEntryWithRequestedAttributes(entry, requestedAttributes);

						// Build SearchResultEntry object.
						SearchResultEntry searchEntry = new SearchResultEntry(newEntry);

						// Increment statistic.
						Pegasus.entryResults++;

						// Send each search entry to channel.
						sendSearchResultEntry(messageID, searchEntry,
								searchEntry.getControls());
					}
					
				}
				catch (LDAPException e) {
					e.printStackTrace();
				}
				
				/*
				 * Reset counting member variable and return sub entries also.			
				 */
				counting = 0;
				subLevels(messageID, request, countLimit, true);

			}
						
		}

	    return new LDAPMessage(messageID, searchResultDoneProtocolOp,
	            StaticUtils.NO_CONTROLS);
	}

	/**
	 * Go into sub elements on backend side and send each (sub)entry.<BR>
	 * <B>NOTE:</B> Member variable {@link #counting} should be set to 0 before calling this method.
	 * 
	 * @param messageID LDAP request message ID value
	 * @param request original or modified instance of {@link SearchRequestProtocolOp}
	 * @param countLimit limit, when <I>counting</I> value reach <I>countLimit</I> operation is done
	 * @param wholeSubtree flag to indicate if this function needs to do recursive search
	 * 
	 * @throws LDAPException
	 */
	private void subLevels(int messageID, SearchRequestProtocolOp request, int countLimit, boolean wholeSubtree) throws LDAPException {
		
		CustomStr dn = new CustomStr(request.getBaseDN());
		
		/*
		 *  Get sub elements in tree, directly from index service.
		 */
		SortedMap<CustomStr, Data> map = Pegasus.myBackend.getSubEntries(dn);
		if (map != null) {
			/*
			 * Start examining sub entries.
			 */
			for (CustomStr branch : map.keySet()) {
				CustomStr subDN = new CustomStr(branch + "," + dn);
				Entry retVal = Pegasus.myBackend.getEntry(subDN);
				if (retVal != null) {
					/*
					 * Sub-entries are dereferenced if flag is set to ALWAYS or SEARCHING.
					 * Ref.
					 * http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/aliases.html
					 */					
					if (request.getDerefPolicy() == DereferencePolicy.ALWAYS
							|| request.getDerefPolicy() == DereferencePolicy.SEARCHING) {
						/*
						 * Perform dereferencing of entry, and update local DN value.
						 */
						retVal = derefEntry(retVal);
						subDN = new CustomStr(retVal.getDN());
					}
					
					/*
					 * Check if entry match filter from request. Only if true, entry is sent to
					 * socket towards LDAP client.
					 */
					if (request.getFilter().matchesEntry(retVal)) {
						List<String> requestedAttributes = request.getAttributes();
						// Put only attributes from request list, others remove.
						Entry newEntry = getEntryWithRequestedAttributes(retVal, requestedAttributes);

						// Build SearchResultEntry object.
						SearchResultEntry searchEntry = new SearchResultEntry(newEntry);

						// Increment statistic.
						Pegasus.entryResults++;
						
						// Send each search entry to channel.
						sendSearchResultEntry(messageID, searchEntry,
								searchEntry.getControls());
						
						// Keep information of how many entries were returned.
						counting++;
						
						// Check if limit is reached.
						if (counting >= countLimit) {
							break;
						}
					}

					if (wholeSubtree) {

						/*
						 * Build new LDAP search request object with new DN.
						 */
						SearchRequestProtocolOp newRequest = new SearchRequestProtocolOp(
								subDN.toString(), request.getScope(), request.getDerefPolicy(), request.getSizeLimit(), request.getTimeLimit(), request.typesOnly(), request.getFilter(), request.getAttributes());

						/*
						 *  Recursively call itself again with modified LDAP search request having proper DN set. 
						 */
						subLevels(messageID, newRequest, countLimit, wholeSubtree);
					}
					
				}
			}
		}
		else {
			Pegasus.failedSearch++;
		}

	}
	
    /**
     * LDAP Result Entry send method.<BR>
     * <B>NOTE:</B> member variable {@link #ctx} should be already set before
     * calling this method.
     *  
     * @param messageID message ID of request (same value)
     * @param searchEntry {@link SearchResultEntry} instance
     * @param controls usually <I>null</I> value or same value as LDAP request.
     */
	private void sendSearchResultEntry(int messageID, SearchResultEntry searchEntry,
			 Control... controls) {
		
		LDAPMessage resultEntryMessage = new LDAPMessage(messageID,
				new SearchResultEntryProtocolOp(searchEntry.getDN().replaceAll(" ", ""),
						new ArrayList<Attribute>(searchEntry.getAttributes())),
				controls);
		
		// Immediately send entry.
		ctx.writeAndFlush(resultEntryMessage);
		
	}

	/**
	 * Get new <I>Entry</I> with only requested attributes from original <I>Entry</I>. A request is a list
	 * of requested attributes in LDAP search request.
	 * @param requestedAttributes list of attributes, if <I>null</I> original <I>Entry</I> is returned
	 * @param entry Entry object instance, not <I>null</I>
	 * @return entry with requested attributes and values from original entry, 
	 * or original entry if request is <I>null</I> or empty
	 */
	private Entry getEntryWithRequestedAttributes(Entry entry, List<String> requestedAttributes) {
		
		// Check for null values.
		if (requestedAttributes == null || entry == null) {
			return entry;
		}
		
		if (requestedAttributes.size() > 0) {
			// Create new Entry with only requested attributes and values.
			Entry newEntry = new Entry(entry.getDN());
			for (String attributeName : requestedAttributes) {
				String[] attributeValues = entry.getAttributeValues(attributeName);
				// Add only non-null attributes to Entry instance.
				if (attributeValues != null) {				
					newEntry.addAttribute(attributeName, attributeValues);
				}
			}

			return newEntry;
		}	
		else {
			// Return original entry if request list is empty.
			return entry;
		}
	}


	/**
	 * Dereference entry data, by looking at <I>aliasedObjectName</I> attribute and returning
	 * new entry data where <I>aliasedObjectName</I> value is referring to.<BR>
	 * <BR>
	 * If provided entry data is not alias, then entry data itself is returned.<BR>
	 * <BR>
	 * <B>NOTE:</B> This method call with do double alias dereferencing in case that entry is alias to 
	 * another entry that is alias ... but it won't do recursive alias dereferencing.
	 * 
	 * @param entryData entry instance
	 * @return new entry or <I>entryData</I> if reference is broken
	 */
	private Entry derefEntry(Entry entryData) {

		Entry retVal = null;
		
		// Try to perform dereferencing.
		if (entryData.hasAttributeValue("objectClass", "alias") &&
				entryData.hasAttribute("aliasedObjectName")) {

			//  Isolate aliasedObjectName attribute and get value.
			String aliasedObjectName = entryData.getAttributeValue("aliasedObjectName");
			
			retVal = Pegasus.myBackend.getEntry(new CustomStr(aliasedObjectName));
			
			// Check for null value.
			if (retVal != null) {
				// Do again alias dereferencing, since alias might point to another alias.
				if (retVal.hasAttributeValue("objectClass", "alias") &&
						retVal.hasAttribute("aliasedObjectName")) {
					retVal = derefEntry(retVal);
				}
			}
			else {
				// Broken alias pointer. Return original alias entry.
				retVal = entryData;
			}
			
		}
		else {
			// Alias not found, then return entry without dereferencing.
			retVal = entryData;
		}
		
		return retVal;
	}
	
	/**
	 * Schema request will result in list of object classes and attributes (mandatory and optional).
	 * 
	 * @param messageID request <I>MessageID</I> value
	 * @param baseDN should be <I>cn=Subschema</I> value, or empty string, never <I>null</I> value
	 * @param attributes <I>null</I> value of <I>subschemaSubentry</I> value for initial request
	 * @return LDAP message, {@link SearchResultDoneProtocolOp} value
	 */
	private LDAPMessage processSchemaRequest(int messageID, String baseDN, List<String> attributes) {
		
		/*
		 * Process schema request.
		 */

		
		/*
		 * Check if it is request for cn=Subschema.
		 * If it is, return object classes and attributes.
		 * 
		 * NOTE: Attribute list in request actually define what objects LDAP server should return:
		 * object classes, attributes, LDAP syntaxes, matching rules, etc.
		 */
		if (baseDN.equalsIgnoreCase("cn=Subschema")) {
			if (attributes != null) {
				
				Entry subschemaEntry = new Entry("cn=Subschema");
				
				for (String attribute : attributes) {

					if (attribute.equalsIgnoreCase("objectClass")) {

						String name = "objectClass";
						String[] value = { "top", "subentry", "subschema", "extensibleObject" };
						subschemaEntry.addAttribute(name, value);

					} else if (attribute.equalsIgnoreCase("objectClasses")) {

						if (Pegasus.schema != null) {
							for (ObjectClassDefinition objectClass : Pegasus.schema.getObjectClasses()) {

								String name = attribute;
								String value = objectClass.toString();
								subschemaEntry.addAttribute(name, value);
							}
						}

					} else if (attribute.equalsIgnoreCase("attributeTypes")) {

						if (Pegasus.schema != null) {
							for (AttributeTypeDefinition attributeType : Pegasus.schema.getAttributeTypes()) {

								String name = attribute;
								String value = attributeType.toString();
								subschemaEntry.addAttribute(name, value);
							}
						}
					} else if (attribute.equalsIgnoreCase("matchingRules")) {

						if (Pegasus.schema != null) {
							for (MatchingRuleDefinition matchingRule : Pegasus.schema.getMatchingRules()) {

								String name = attribute;
								String value = matchingRule.toString();
								subschemaEntry.addAttribute(name, value);
							}
						}
					} else if (attribute.equalsIgnoreCase("ldapSyntaxes")) {

						if (Pegasus.schema != null) {
							for (AttributeSyntaxDefinition attributeSyntax : Pegasus.schema.getAttributeSyntaxes()) {

								String name = attribute;
								String value = attributeSyntax.toString();
								subschemaEntry.addAttribute(name, value);
							}
						}	
					} else if (attribute.equalsIgnoreCase("*")) {
						// Ups, what to do here ??
					} else {
						Pegasus.debug("INFO: Unsupported request for schema attribute:"
								+ attribute);
					}


				}

				// Build SearchResultEntry object.
				SearchResultEntry searchEntry = new SearchResultEntry(subschemaEntry);

				// Increment statistic.
				Pegasus.entryResults++;
				
				// Send each search entry to channel.
				sendSearchResultEntry(messageID, searchEntry,
						searchEntry.getControls());

			}					
		}
		
	    return new LDAPMessage(messageID, searchResultDoneProtocolOp,
	            StaticUtils.NO_CONTROLS);
	}
}
