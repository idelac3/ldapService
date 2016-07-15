package hr.ericsson.pegasus.backend;

import hr.ericsson.pegasus.Pegasus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

/**
 * <H1>Backend</H1>
 * <HR>
 * LDAP Backend to hold LDAP data.<BR>
 * Data are usually loaded by {@link ConcurrentBackend#ldifRead(File)} function.<BR>
 * <BR>
 * This class is has to be <B>thread-safe</B>, since this application has to support high level
 * of concurrency. It is very probably that at least few different threads will try to modify data
 * on this (single) backend instance. 
 * <HR>
 * @author eigorde
 *
 */
public class ConcurrentBackend {

	/**
	 * Map that holds root data.<BR>
	 * <B>NOTE:</B> Map has to be concurrent or synchronized.<BR>
	 * Ref.<BR>
	 * <A HREF="http://stackoverflow.com/questions/510632/whats-the-difference-between-concurrenthashmap-and-collections-synchronizedmap">Difference between concurrent and synchronized map</A>
	 */
	private TreeMap<CustomStr, Data> map;

	/**
	 * Root DN string.
	 */
	private CustomStr rootDN;

	/**
	 * A list of loaded LDIF files.
	 */
	private List<File> loadedLDIFs;
	
	/**
	 * Initialize {@link ConcurrentBackend} instance.<BR>
	 * For more information about {@link SortedMap} implementations, see
	 * {@link TreeMap}.
	 * 
	 */
	public ConcurrentBackend() {		
		map = new TreeMap<CustomStr, Data>();
		loadedLDIFs = new ArrayList<File>();
	}

	/**
	 * Load data in map from LDIF source. If root DN is <I>null</I> it will be
	 * set to first loaded entry from LDIF source.
	 * 
	 * @param file
	 *            valid instance of {@link File} object which points to real
	 *            file on local file system
	 * @return number of entries added to backend
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public int ldifRead(File file) throws IOException {
		LDIFReader ldifReader = new LDIFReader(file);

		int entriesRead = 0;
		int entriesAdded = 0;
		int errorsEncountered = 0;

		Pegasus.append("Loading " + file.getName() + " ...  ");
		long estimatedEntries = Pegasus.determinator.determineEntryCount(file.getAbsolutePath());
		
		if (estimatedEntries < 1) {
			// Put maximum value to avoid arithmetic division by 0.
			estimatedEntries = Long.MAX_VALUE;
		}
		
		// Save in list file.
		if (!loadedLDIFs.contains(file)) {
			loadedLDIFs.add(file);
		}
		
		// Calculate current percent of load done, start with value 0.
		long currentPercent = 0;
		
		while (true) {
			Entry entry;
			CustomStr dn;

			try {
				entry = ldifReader.readEntry();
				if (entry == null) {
					// All entries have been read.
					break;
				}

				dn = new CustomStr(entry.getDN());
				if (rootDN == null) {
					rootDN = dn;
				}

				entriesRead++;
				
				long newPercent = 100 * entriesRead / estimatedEntries;
				if (newPercent >= currentPercent + 5) {
					// Only update percentage indicator in step of 5%.
					currentPercent = newPercent;
					Pegasus.append(newPercent + "%  ");
				}
				
			} catch (LDIFException le) {
				errorsEncountered++;
				if (le.mayContinueReading()) {
					/*
					 *  A recoverable error occurred while attempting to read a
					 *  change record, at or near line number le.getLineNumber().
					 *  The entry will be skipped, but we'll try to keep reading from the LDIF file.
					 */
					continue;
				} else {
					/*
					 *  An unrecoverable error occurred while attempting to read
					 *  an entry at or near line number le.getLineNumber().
					 *  No further LDIF processing will be performed.
					 */
					break;
				}
			} catch (IOException ioe) {
				/*
				 *  An I/O error occurred while attempting to read from the LDIF file.
				 *  No further LDIF processing will be performed.
				 */
				errorsEncountered++;
				break;
			}

			if (getEntry(dn) != null) {
				/*
				 * Entry already exist, skip adding.
				 */
				errorsEncountered++;
			}
			else {
				/*
				 * Store in backend map.
				 */
				if (addEntry(dn, entry)) {
					entriesAdded++;
				}
			}

		}

		ldifReader.close();

		Pegasus.log("");
		Pegasus.log("Entries loaded: " + entriesAdded + " / " + entriesRead + "  ( " + String.format("%d", 100 * entriesAdded / entriesRead) + "% )");
		Pegasus.log("");
		
		return entriesAdded;
	}

	/**
	 * Add new entry. If entry already exist, it is updated.
	 * 
	 * @param dn
	 *            full DN where to add new entry
	 * @param entry
	 *            instance of {@link Entry} object
	 * @return <I>true</I> if successful, otherwise <I>false</I>
	 */
	public synchronized boolean addEntry(CustomStr dn, Entry entry) {
		SortedMap<CustomStr, Data> map = getParentSubmap(dn);
		if (map == null) {
			return false;
		}

		// Store new entry.
		CustomStr rdn = getRDN(dn);
		Data oldData = map.get(rdn);
		if (oldData == null) {
			// Store new data.
			map.put(rdn, new Data(entry));
		} else {
			// Update just entry if data already exist.
			oldData.entry = entry;
		}

		return true;
	}

	/**
	 * Get existing entry.
	 * 
	 * @param dn
	 *            full dn where to look for the entry.
	 * @return {@link Entry} instance of <I>null</I> if not found
	 */
	public Entry getEntry(CustomStr dn) {
		SortedMap<CustomStr, Data> map = getParentSubmap(dn);
		if (map == null) {
			return null;
		}

		// Get entry.
		CustomStr rdn = getRDN(dn);
		Data data = map.get(rdn);
		if (data == null) {
			return null;
		}

		return data.entry;
	}

	/**
	 * Get map with sub elements.<BR>
	 * <B>NOTE:</B> This function may return null value or empty map !
	 * 
	 * @param dn
	 *            full DN where to look for the sub entries.
	 * @return {@link SortedMap} instance with sub elements, or <I>null</I> if
	 *         no sub elements exist
	 */
	public SortedMap<CustomStr, Data> getSubEntries(CustomStr dn) {
		if (dn.equals(rootDN)) {
			return this.map.get(rootDN).map;
		}

		SortedMap<CustomStr, Data> map = this.map;
		for (CustomStr branch : getBranchArray(new CustomStr(dn))) {

			if (map == null) {
				return null;
			}

			// Get reference to sub-map.
			map = map.get(branch).map;

		}

		// Get sub map.
		CustomStr rdn = getRDN(dn);

		if (map == null) {
			return null;
		}
		Data data = map.get(rdn);

		if (data == null) {
			return null;
		}
		map = data.map;

		// Return sub map.
		return map;
	}

	/**
	 * Modify existing entry with new data.
	 * 
	 * @param dn
	 *            full DN to existing entry
	 * @param newEntry
	 *            new entry data to be stored
	 * @return <I>true</I> if successful, otherwise <I>false</I>
	 */
	public boolean modifyEntry(CustomStr dn, Entry newEntry) {
		SortedMap<CustomStr, Data> map = getParentSubmap(dn);
		if (map == null) {
			return false;
		}

		// Get data which holds entry.
		CustomStr rdn = getRDN(dn);
		Data data = map.get(rdn);
		if (data == null) {
			return false;
		}

		// Store new/updated entry here.
		data.entry = newEntry;

		return true;
	}

	/**
	 * Delete existing entry and all of its sub-entries.
	 * 
	 * @param dn
	 *            full DN to existing entry
	 * @return <I>true</I> if successful, otherwise <I>false</I>
	 */
	public synchronized boolean deleteEntry(CustomStr dn) {
		SortedMap<CustomStr, Data> map = getParentSubmap(dn);
		if (map == null) {
			return false;
		}

		// Get RDN to be deleted.
		CustomStr rdn = getRDN(dn);

		return (map.remove(rdn) != null);
	}

	/**
	 * Get root DN string. This value is set after loading first entry from
	 * first LDIF source {@link ConcurrentBackend#ldifRead(File)}.
	 * 
	 * @return root DN
	 */
	public CustomStr getRootDN() {
		return rootDN;
	}

	/**
	 * This method will erase all records and set root DN to <I>null</I> value.
	 */
	public void eraseEntries() {
		map.clear();
		rootDN = null;
	}
	
	/**
	 * Method will try to reload already loaded LDIFs from file system. Maybe
	 * {@link #eraseEntries()} method call should be called first.
	 * @throws IOException 
	 */
	public void reloadLDIFs() throws IOException {
		for (File file : loadedLDIFs) {
			ldifRead(file);
		}
	}
	
	/**
	 * Fetch list of loaded LDIF files.
	 * @return LDIF file list or empty list
	 */
	public List<File> getLoadedLDIFList() {
		return loadedLDIFs;
	}
	
	/**
	 * Access the parent concurrent map instance, from which it is possible to
	 * modify, delete, add, etc. sub-entries.
	 * 
	 * @param dn
	 *            full DN
	 * @return concurrent sub-map or <I>null</I> value if DN is invalid or does not
	 *         exist
	 */
	private SortedMap<CustomStr, Data> getParentSubmap(CustomStr dn) {
		SortedMap<CustomStr, Data> map = this.map;
		for (CustomStr branch : getBranchArray(dn)) {
			// Get reference to sub-map.
			Data data = map.get(branch);
			if (data == null) {
				return null;
			}
			map = data.map;
		}

		return map;
	}

	/**
	 * Build array of branch elements. Eg:<BR>
	 * <UL>
	 * <LI>root</LI>
	 * <LI>a</LI>
	 * <LI>b</LI>
	 * </UL>
	 * 
	 * @param dn
	 *            distinguished name, eg. <I>c,b,a,root</I>
	 * @return a list in order as in example, or empty list if dn is <I>root
	 *         dn</I>
	 */
	private List<CustomStr> getBranchArray(CustomStr dn) {
		List<CustomStr> retVal = new ArrayList<CustomStr>();
		while (!dn.equals(rootDN)) {
			dn = getParentDN(dn);
			retVal.add(0, getRDN(dn));
		}
		return retVal;
	}

	/**
	 * Return relative part of DN.
	 * 
	 * @param dn
	 *            request DN
	 * @return first part of DN, or <I>root DN</I>
	 */
	private CustomStr getRDN(CustomStr dn) {
		if (dn.equals(rootDN)) {
			return rootDN;
		}

		int idx = dn.indexOf(',');
		if (idx < 1) {
			return dn;
		}

		byte[] rdn = new byte[idx];
		byte[] value = dn.getValue();
		System.arraycopy(value, 0, rdn, 0, idx);

		return new CustomStr(rdn);
	}

	/**
	 * Return parent DN.
	 * 
	 * @param dn
	 *            request DN, or <I>root DN</I>
	 * @return parent DN
	 */
	private CustomStr getParentDN(CustomStr dn) {
		if (dn.equals(rootDN)) {
			return rootDN;
		}

		int idx = dn.indexOf(',');
		if (idx > 0) {
			return dn.substring(idx + 1);
		}
		return dn;
	}

}
