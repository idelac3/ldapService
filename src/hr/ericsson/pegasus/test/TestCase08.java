package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import com.unboundid.ldap.sdk.schema.Schema;

/**
 * <H1>TestCase08</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>SEARCH with base DN set to <I>cn=Subschema</I> value</LI>
 * </UL>
 * Test level: BASIC
 * @author igor.delac@gmail.com
 *
 */
public class TestCase08 extends LdapTestCase {

	/**
	 * {@link TestCase08}
	 */
	public TestCase08() {
		id = 8;
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
		 * Get schema instance. 
		 */
		Schema schema = ldapConnection.getSchema();
		
		if (schema != null)
		{

			/*
			 * Counters for objectClass and attributes.
			 */
			int objectClassCount = 0, mandatoryAttributeCount = 0, optionalAttributeCount = 0;
			
			for (ObjectClassDefinition objClassDef : schema.getObjectClasses())
			{
				objectClassCount++;
				mandatoryAttributeCount = mandatoryAttributeCount + objClassDef.getRequiredAttributes().length;					
				optionalAttributeCount = optionalAttributeCount + objClassDef.getOptionalAttributes().length;
			}
			
			if (objectClassCount > 0 &&
					mandatoryAttributeCount > 0 &&
					optionalAttributeCount > 0)
			{
				setVerdict(PASS);
			}
			else
			{
				setVerdict(FAIL);
			}
		}
		else
		{
			setVerdict(ERROR);
		}
		
	}
}
