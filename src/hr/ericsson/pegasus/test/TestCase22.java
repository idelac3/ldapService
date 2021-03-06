package hr.ericsson.pegasus.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

/**
 * <H1>TestCase22</H1>
 * This test case will test simultaneous LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>SEARCH with scope BASE and SUB</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: SIMULTANEOUS
 * @author igor.delac@gmail.com
 *
 */
public class TestCase22 extends LdapTestCase {

	/**
	 * {@link TestCase22}
	 */
	public TestCase22() {
		id = 22;
		setName("TestCase" + id);
	}
	
	@Override
	public void run() {
			try {
				test();
			}
			catch (LDAPException | InterruptedException ex) {
				setVerdict(FAIL);
				ex.printStackTrace();
			}
	}

	private void test() throws LDAPException, InterruptedException {

		/*
		 * Add first entry.
		 */		
		LDAPResult result = AddEntry.addEntry(ldapConnection, id);
		if (result.getResultCode().intValue() != 0)
		{
			setVerdict(FAIL);
			return;
		}


		final int THREAD_COUNT = 500;
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
	
		/*
		 * Start jobs that create sub entries.
		 */
		List<Runnable> jobList = new ArrayList<Runnable>(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++)
		{
			/*
			 * Populate list with jobs.
			 */
			LdapAddRunnable job = new LdapAddRunnable(ldapConnection, id, i, latch);
			jobList.add(job);

			/*
			 * Start each jobs.
			 */
			Thread ldapAddThread = new Thread(job, "ldapAddJob" + i);
			ldapAddThread.start();
			
		}		
		latch.await();

		for (Runnable job : jobList)
		{
			/*
			 * Check if there are job(s) that failed.
			 */
			if ( ((LdapAddRunnable)job).fail)
			{
				setVerdict(FAIL);
			}
		}

		/*
		 * Test LDAP SEARCH operations with scope BASE and SUB.
		 */
		latch = new CountDownLatch(THREAD_COUNT);
		jobList.clear();
		for (int i = 0; i < THREAD_COUNT; i++)
		{
			LdapSearchRunnable job = new LdapSearchRunnable(ldapConnection, id, i, latch);
			jobList.add(job);
			
			Thread ldapSearchThread = new Thread(job, "ldapSearchJob" + i);
			ldapSearchThread.start();
		}
		latch.await();

		for (Runnable job : jobList)
		{
			/*
			 * Check if there are job(s) that failed.
			 */
			if ( ((LdapSearchRunnable)job).fail)
			{
				setVerdict(FAIL);
			}
		}

		
		/*
		 * Start jobs that delete sub entries.
		 */
		latch = new CountDownLatch(THREAD_COUNT);
		jobList.clear();
		for (int i = 0; i < THREAD_COUNT; i++)
		{
			LdapDeleteRunnable job = new LdapDeleteRunnable(ldapConnection, id, i, latch);
			jobList.add(job);
			
			Thread ldapDeleteThread = new Thread(job, "ldapDeleteJob" + i);
			ldapDeleteThread.start();
		}
		latch.await();
		
		for (Runnable job : jobList)
		{
			/*
			 * Check if there are job(s) that failed.
			 */
			if ( ((LdapDeleteRunnable)job).fail)
			{
				setVerdict(FAIL);
			}
		}

		/*
		 * Try to delete test entry.
		 */
		result = DeleteEntry.delEntry(ldapConnection, id);
		if (result.getResultCode().intValue() != 0)
		{
			setVerdict(FAIL);
			return;
		}

		setVerdict(PASS);
	}
}
