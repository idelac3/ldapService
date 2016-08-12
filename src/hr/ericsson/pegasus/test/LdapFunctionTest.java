package hr.ericsson.pegasus.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * This class contains series of test cases that test LDAP operations.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class LdapFunctionTest {

	static PrintStream wr = System.out;
	
	static final boolean SEQUENTIAL_TEST_CASE_EXECUTION = true;
	
	static int passCounter = 0;
	static int failCounter = 0;

	static String host = "localhost";
	static int port = 1389;
	
	static LDAPConnection ldapConnection;
	
	static String bindDN = "cn=Manager,o=ericsson,dc=com";
	static String password = "SECRET";
	
	static String baseDN = null;
	
	/**
	 * Read root DN from LDAP server.
	 * 
	 * @return root DN string (eg. o=company,dc=com)
	 */
	public static String getDN() {
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
	
	public static void main(String[] args) throws InterruptedException {

		try {
			
			/*
			 * Save time stamp.
			 */
			long startTime = System.currentTimeMillis();

			/*
			 * Establish TCP connection.
			 */
			ldapConnection = new LDAPConnection(host, port);

			/*
			 * Bind with user name and password.
			 */
			ldapConnection.bind(bindDN, password);

			/*
			 * Set 15 sec. timeout for every LDAP operation.
			 */
			ldapConnection.getConnectionOptions().setResponseTimeoutMillis(15000);
			
			/*
			 * Test Case list.
			 */
			LdapTestCase[] testCaseList = {
					new TestCase01(),
					new TestCase02(),
					new TestCase03(),
					new TestCase04(),
					new TestCase05(),
					new TestCase06(),
					new TestCase07(),
					new TestCase08(),
					
					new TestCase11(),
					new TestCase12(),
					new TestCase13(),
					
					new TestCase21(),
					new TestCase22(),
					new TestCase23(),

			};
			
			if (SEQUENTIAL_TEST_CASE_EXECUTION)
			{
				/*
				 * Sequential execution of test cases from list.
				 */
				for (LdapTestCase tc : testCaseList)
				{
					tc.ldapConnection = ldapConnection;
					wr.print("Running " + tc.getName() + " ... ");
					tc.run();
					wr.println(tc.getVerdictText());
				}
			}
			else
			{
				/*
				 * Parallel execution of test cases.
				 */
				List<Thread> jobList = new ArrayList<Thread>();
				for (LdapTestCase tc : testCaseList)
				{
					tc.ldapConnection = ldapConnection;
					Thread job = new Thread(tc, "LdapTestCase" + tc.id);
					jobList.add(job);
					job.start();
					wr.println("Job " + job.getName() + " started.");
				}

				/*
				 * Wait for all test cases to finish.
				 */
				for (Thread job : jobList)
				{
					job.join();
					wr.println("Job " + job.getName() + " finished.");
				}
			}

			/*
			 * Calculate execution time.
			 */
			long endTime = System.currentTimeMillis();
			long delta = endTime - startTime;
			
			wr.println();
			wr.println("Elapsed: " + (delta / 1000) + " sec.");
			
		}
		catch (LDAPException ex) {
			ex.printStackTrace();
		}
		
		stop();

	}
	
	private static void stop()
	{
		int total = LdapTestCase.pass.get() + LdapTestCase.fail.get() + LdapTestCase.error.get();
		
		wr.println();
		wr.println();
		wr.println("Summary");
		wr.println("==================================");
		wr.println(" Pass: " + LdapTestCase.pass  + "  ( " + (100 * LdapTestCase.pass.get()  / total) + "% )");
		wr.println(" Fail: " + LdapTestCase.fail  + "  ( " + (100 * LdapTestCase.fail.get()  / total) + "% )");
		wr.println("Error: " + LdapTestCase.error + "  ( " + (100 * LdapTestCase.error.get() / total) + "% )");
		wr.println();
		wr.print  ("Final: ");
		
		if (LdapTestCase.fail.get() == 0 && LdapTestCase.error.get() == 0)
		{
			wr.println("PASS");
		}
		else
		{
			wr.println("FAIL");
		}
		
		wr.println("==================================");
		
		
		ldapConnection.close();
		
		System.exit(0);
	}

}
