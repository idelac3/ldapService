package hr.ericsson.pegasus.test;

import java.util.concurrent.atomic.AtomicInteger;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * <H1>LDAP Test case</H1>
 * Test cases should be independent from each other, runnable in thread, have unique ID value, etc.
 * 
 * @author igor.delac@gmail.com
 *
 */
public abstract class LdapTestCase extends TestCase implements Runnable {
	
	/**
	 * LDAPConnection instance, a TCP connection towards LDAP service capable of sending LDAP search/modify/delete/add
	 * requests.
	 */
	public LDAPConnection ldapConnection;
	
	/**
	 * Test Case ID value. Each test case should set unique value. 
	 */
	protected int id;
	
	/**
	 * Total number of test cases marked as PASS.
	 */
	protected static AtomicInteger pass = new AtomicInteger(0);

	/**
	 * Total number of test cases marked as FAIL.
	 */
	protected static AtomicInteger fail = new AtomicInteger(0);
	
	/**
	 * Total number of test cases marked as ERROR.
	 */
	protected static AtomicInteger error = new AtomicInteger(0);

	private boolean verdictSet = false;
	
	private String baseDN = null;
	
	/**
	 * Read root DN from LDAP server.
	 * 
	 * @return root DN string (eg. o=company,dc=com)
	 */
	public String getBaseDN() {
		if (baseDN == null) {
			try {
				SearchResult searchResult = ldapConnection.search("", SearchScope.BASE, "(objectClass=*)", "namingcontexts");
				baseDN = searchResult.getSearchEntries().get(0).getAttribute("namingcontexts").getValue();
			} catch (LDAPSearchException e) {
				baseDN = "";
				e.printStackTrace();				
			}
		}
			
		return baseDN;
	}
	
	@Override
	public void setVerdict(int verdict) {
		
		if (verdictSet)
		{
			/*
			 *  Test case state already set, then exit.
			 *  This prevents toggling of test case verdict.
			 */
			return;
		}
		
		super.setVerdict(verdict);
		
		if (verdict == PASS)
		{
			pass.incrementAndGet();
		}
		else if (verdict == FAIL)
		{
			fail.incrementAndGet();
		}
		else
		{
			error.incrementAndGet();
		}
		
		verdictSet = true;
		
	}
	
}
