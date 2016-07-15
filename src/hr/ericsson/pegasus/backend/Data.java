package hr.ericsson.pegasus.backend;

import java.util.TreeMap;

import com.unboundid.ldap.sdk.Entry;

/**
 * <H1>Data</H1>
 * <HR>
 * Hold {@link Entry} instance, parent DN, and map of first level sub entries.
 * <HR>
 * @author eigorde
 *
 */
public class Data {

	/**
	 * Instance of {@link Entry} class. Never <I>null</I> value.<BR>
	 * Every entry in LDAP database should have at least some attributes and values.
	 */
	public Entry entry;

	/**
	 * Map of sub entries. Never <I>null</I> value but might be empty
	 * when no child entry exist.<BR>
	 */
	public TreeMap<CustomStr, Data> map;

	/**
	 * Always provide {@link Entry} instance.
	 * @param entry {@link Entry} instance, never <I>null</I> 
	 */
	public Data(Entry entry) {
		this.entry = entry;
		this.map = new TreeMap<CustomStr, Data>();		
	}

}
