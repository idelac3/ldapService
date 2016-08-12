package hr.ericsson.pegasus.DetermineEntryCount;

/**
 * <H1>Data</H1>
 * <HR>
 * This simple class is used only in {@link DetermineNumberOfEntries} as complex type
 * that holds file path, last modification time stamp and how many LDIF entries file contains.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class Data {
	
	public String filePath;
	
	public Long lastModified;
	
	public Integer entryCount;
	
	/**
	 * New record.
	 * @param filePath absolute or relative path to file
	 * @param lastModified use {@link java.io.File#lastModified()} method to fill this value
	 * @param entryCount set number of LDIF entries
	 */
	public Data(String filePath, Long lastModified, Integer entryCount) {
		this.filePath = filePath;
		this.lastModified = lastModified;
		this.entryCount = entryCount;
	}
	
	public int getDataSize() {
		return (Integer.SIZE / 8) +
				filePath.length() +
				(Long.SIZE / 8) +
				(Integer.SIZE / 8);
	}

	@Override
	public String toString() {
		return "Data [filePath=" + filePath + ", entryCount=" + entryCount
				+ "]";
	}
	
}
