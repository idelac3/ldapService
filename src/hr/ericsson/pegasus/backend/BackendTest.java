package hr.ericsson.pegasus.backend;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.SortedMap;
import java.util.concurrent.CountDownLatch;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFException;

/**
 * <H1>Backend Test</H1>
 * <HR>
 * Please use this program to test proper functioning of backend. A good practice would be
 * to run this program after each significant change in program code of {@link ConcurrentBackend}
 * class.
 * <HR>
 * @author eigorde
 *
 */
public class BackendTest {

	/**
	 * Use this object to write to console messages.
	 */
	static PrintStream wr = System.out;
	
	/**
	 * 
	 * Main program entry to test {@link ConcurrentBackend} module.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws LDAPException 
	 * @throws LDIFException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, LDAPException, LDIFException, InterruptedException {
	
		int fail = 0;
		int pass = 0;
		
		boolean retVal;
		
		final ConcurrentBackend backend = new ConcurrentBackend();	
		
		final File ldifFile = new File("test-data-subscriber-m2msp.ldif");
		
		// Adjust here how many times to repeat test cases.
		int repeat = 5;
		
		while (repeat-- > 0) {

			wr.println("Test case 1");
			wr.print("Reading LDIF source ... ");
			backend.ldifRead(ldifFile);
			retVal = backend.getRootDN().equals(new CustomStr("o=ericsson,dc=com"));
			if ( retVal )
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 2");
			wr.print("Scanning entries ... ");
			String[] DNs = {
					new String("O=ERICSSON,DC=COM"),
					new String("APPLICATIONNAME=AUCS, O=ERICSSON,DC=COM"),
					new String("USERCONTAINERNAME=USERINFORMATION, APPLICATIONNAME=AUCS, O=ERICSSON,DC=COM"),
					new String("cn=NESTO, USERCONTAINERNAME=USERINFORMATION, APPLICATIONNAME=AUCS, O=ERICSSON,DC=COM")
			};
			retVal = backend.getEntry(new CustomStr(DNs[0])) != null &&
					backend.getEntry(new CustomStr(DNs[1])) != null &&
					backend.getEntry(new CustomStr(DNs[2])) != null &&
					backend.getEntry(new CustomStr(DNs[3])) == null;
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 3");
			wr.print("Scanning sub-entries ... ");
			retVal = backend.getSubEntries(new CustomStr(DNs[0])).size() == 3 &&
					backend.getSubEntries(new CustomStr(DNs[1])).get(
							new CustomStr("USERCONTAINERNAME=USERINFORMATION")).
							entry.getAttribute("objectClass").hasValue("UserContainer");
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 4");
			wr.print("Adding entry to root DN ... ");
			Entry newEntry = new Entry(new DN("cn=nesto, O=ERICSSON,DC=COM"));
			newEntry.addAttribute("objectClass", "top", "cn");
			newEntry.addAttribute("cn", "nesto");
			retVal = backend.addEntry(new CustomStr(newEntry.getDN()), newEntry);
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 5");
			wr.print("Reading added entry ... ");
			newEntry = backend.getEntry(new CustomStr("cn=nesto, O=ERICSSON,DC=COM"));
			retVal = newEntry.getAttribute("objectClass").hasValue("top") &&
					newEntry.getAttribute("objectClass").hasValue("cn") &&
					newEntry.getAttribute("cn").hasValue("nesto") ;
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 6");
			wr.print("Deleting added entry ... ");
			retVal = backend.deleteEntry(new CustomStr("cn=nesto, O=ERICSSON,DC=COM"));
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 7");
			wr.print("Checking deleted entry ... ");
			retVal = !backend.deleteEntry(new CustomStr("cn=nesto, O=ERICSSON,DC=COM"));
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 8");
			wr.print("Add & modify entry ... ");
			retVal = backend.addEntry(new CustomStr(newEntry.getDN()), newEntry) &&
					backend.modifyEntry(new CustomStr(newEntry.getDN()), newEntry);
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 9");
			wr.print("Delete & modify non-existing entry ... ");
			retVal = backend.deleteEntry(new CustomStr(newEntry.getDN())) &&
					!backend.modifyEntry(new CustomStr(newEntry.getDN()), newEntry);
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 10");
			wr.print("Modify & check root entry (" + backend.getRootDN() + ") ... ");
			Entry originalRootEntry = backend.getEntry(backend.getRootDN());
			retVal = backend.modifyEntry(new CustomStr(originalRootEntry.getDN()), newEntry) &&
					backend.getEntry(new CustomStr(originalRootEntry.getDN())).equals(newEntry);
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();

			wr.println("Test case 11");
			wr.print("Check sub-entry size ... ");
			retVal = backend.getSubEntries(backend.getRootDN()).size() == 3;
			for (CustomStr key : backend.getSubEntries(backend.getRootDN()).keySet())
			{
				retVal = retVal &
						(backend.getEntry(new CustomStr(key.toString() + "," + backend.getRootDN().toString())) != null);
			}
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();


			wr.println("Test case 12");
			wr.print("Adding new entry & re-loading LDIF, check that added entry is not lost ... ");
			String[] entryLines = {
					"dn: M2MSP=501,SUBSID=262025000000001,cn=SUBSCRIBERS,UserContainerName=USERINFORMATION,ApplicationName=HLRHSS,o=ericsson,dc=com",
					"M2MSP: 501",
					"aliasedObjectName: M2MSP=500,cn=PROFILES,UserContainerName=USERINFORMATION,ApplicationName=HLRHSS,o=ericsson,dc=com",
					"objectClass: alias",
					"objectClass: extensibleObject"
			};
			newEntry = new Entry(entryLines);
			CustomStr dn = new CustomStr(newEntry.getDN());
			retVal = backend.addEntry(dn, newEntry);
			retVal = retVal & backend.ldifRead(new File("test-data-subscriber-m2msp.ldif")) > 0;
			retVal = retVal & backend.getEntry(dn) != null;
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				fail++;
			}
			wr.println();


			wr.println("Test case 13");
			wr.print("Concurrent Add to root DN ... ");
			class LdapAddTest implements Runnable {

				private int offset;
				private CountDownLatch latch;
				public LdapAddTest(int offset, CountDownLatch latch)
				{
					this.offset = offset;
					this.latch = latch;
				}
				
				@Override
				public void run() {
					int start = offset * 1000;
					int end = start + 1000;
					for (int i = start; i < end; i++)
					{
						Entry newEntry = null;
						try {
							newEntry = new Entry(new DN("cn=nesto" + i + ", O=ERICSSON,DC=COM"));
						} catch (LDAPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newEntry.addAttribute("objectClass", "top", "cn");
						newEntry.addAttribute("cn", "nesto" + i);
						
						backend.addEntry(new CustomStr(newEntry.getDN()), newEntry);
					}
					latch.countDown();
				}
				
			};
			int sizeBefore = backend.getSubEntries(backend.getRootDN()).size();
			final int THREAD_COUNT = 100;
			CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
			for (int i = 0; i < THREAD_COUNT; i++)
			{
				Thread runXX = new Thread(new LdapAddTest(i, latch), "LdapAddTest" + i);
				runXX.start();
			}
			latch.await();
			int sizeAfter = backend.getSubEntries(backend.getRootDN()).size();
			int delta = sizeAfter - sizeBefore;
			retVal = (delta == THREAD_COUNT * 1000);
			if (retVal)
			{
				wr.println("PASS");
				pass++;
			}
			else
			{
				wr.println("FAIL");
				wr.println("Delta = " + delta + ", size before = " + sizeBefore + ", size after = " + sizeAfter);
				fail++;
			}
			backend.deleteEntry(backend.getRootDN());
			backend.ldifRead(ldifFile);
			wr.println();
			
			// printMap(backend.getSubEntries(backend.getRootDN()), 0);

			wr.println("Done. ");
			wr.println("=============================");
			
		}
		
		int total = pass + fail;
		wr.println("Summary");
		wr.println("=============================");
		wr.println("Pass: " + pass + "  " + (pass * 100 / total) + "%");
		wr.println("Fail: " + fail + "  " + (fail * 100 / total) + "%");
	}

	public static void printMap(SortedMap<CustomStr, Data> map, int tab)
	{
		if (map == null)
		{
			System.out.println("null");
			return;
		}
		
		for (CustomStr key : map.keySet()) {
			for (int i = 0; i < tab; i++) {
				System.out.print("   ");
			}
			System.out.println(key);
			Data data = map.get(key);
			printMap(data.map, tab + 1);			
		}	
	}
}
