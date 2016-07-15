package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase11</H1>
 * This test case will test LDAP operations with invalid DN:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>MODIFY</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: NEGATIVE
 * @author eigorde
 *
 */
public class TestCase11 extends LdapTestCase {

	/**
	 * {@link TestCase11}
	 */
	public TestCase11() {
		id = 11;
		setName("TestCase" + id);
	}
	
	@Override
	public void run() {
			try {
				test();
			}
			catch (LDAPException ex) {
				setVerdict(FAIL);
				ex.printStackTrace();
			}
	}

	private void test() throws LDAPException {

		/*
		 * Try to add entry with invalid DN. 
		 */
		try {

			AddEntry.addInvalidEntry(ldapConnection);
			setVerdict(FAIL);
			return;
		}
		catch (LDAPException ex)
		{
			// OK, resume here.
			// System.out.println(ex.getDiagnosticMessage());
		}

		/*
		 * Try to modify entry with invalid DN. 
		 */
		try {

			ModifyEntry.modifyInvalidEntry(ldapConnection);
			setVerdict(FAIL);
			return;
		}
		catch (LDAPException ex)
		{
			// OK, resume here.
			// System.out.println(ex.getDiagnosticMessage());
		}

		/*
		 * Try to delete entry with invalid DN. 
		 */
		try {

			DeleteEntry.delInvalidEntry(ldapConnection);
			setVerdict(FAIL);
			return;
		}
		catch (LDAPException ex)
		{
			// OK, resume here.
			// System.out.println(ex.getDiagnosticMessage());
		}


		setVerdict(PASS);
	}
}
