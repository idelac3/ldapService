package hr.ericsson.pegasus.test;

import java.util.concurrent.CountDownLatch;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

class LdapModifyRunnable implements Runnable
{

	private int id, subId;
	private CountDownLatch latch;
	private LDAPConnection ldapConnection;
	
	/**
	 * Should be <I>false</I> if job finish successfully.
	 */
	public boolean fail;
	
	public LdapModifyRunnable(LDAPConnection ldapConnection, int id, int subId, CountDownLatch latch)
	{
		this.id = id;
		this.subId = subId;
		this.latch = latch;
		this.ldapConnection = ldapConnection;
	}
	
	@Override
	public void run() {

		LDAPResult result;
		try {
			result = ModifyEntry.modifySubentryAddAttributes(ldapConnection, id, subId);
			
			if (result.getResultCode().intValue() == 0)
			{
				fail = false;
				
				result = ModifyEntry.modifySubentryDelAttribute(ldapConnection, id, subId);
				
				if (result.getResultCode().intValue() == 0)
				{
					fail = false;
					
					result = ModifyEntry.modifySubentryReplaceAttributes(ldapConnection, id, subId);
					
					if (result.getResultCode().intValue() == 0)
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

