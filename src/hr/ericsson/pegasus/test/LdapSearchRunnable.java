package hr.ericsson.pegasus.test;

import java.util.concurrent.CountDownLatch;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;

class LdapSearchRunnable implements Runnable
{

	private int id, subId;
	private CountDownLatch latch;
	private LDAPConnection ldapConnection;
	
	/**
	 * Should be <I>false</I> if job finish successfully.
	 */
	public boolean fail;
	
	public LdapSearchRunnable(LDAPConnection ldapConnection, int id, int subId, CountDownLatch latch)
	{
		this.id = id;
		this.subId = subId;
		this.latch = latch;
		this.ldapConnection = ldapConnection;
	}
	
	@Override
	public void run() {

		boolean result;
		try {
			result = CheckEntry.checkEntry(ldapConnection, id);
			if (result)
			{
				fail = false;
				
				result = CheckEntry.checkEntryAndSubEntry(ldapConnection, id, subId);
				
				if (result)
				{
					fail = false;
				}
				else
				{
					fail = true;
				}
			}
			else
			{
				fail = true;
			}
		} catch (LDAPException e) {
			e.printStackTrace();
			fail = true;
		}

		latch.countDown();
	}
	
}

