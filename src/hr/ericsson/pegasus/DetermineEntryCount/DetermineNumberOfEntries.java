package hr.ericsson.pegasus.DetermineEntryCount;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;

/**
 * <H1>Determine number of entries</H1>
 * <HR>
 * This class is used to count how many records LDIF file has.<BR>
 * Once LDIF file is examined, result is stored in cache for faster
 * reading in future. See {@link #CACHE_FILE} variable.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class DetermineNumberOfEntries {

	/**
	 * Path to cache file.
	 */
	private final String CACHE_FILE = "cache.db";
	
	/**
	 * Cache table, see {@link #readFromFileIntoCacheTable()} method.
	 */
	private final Map<Integer, Data> cacheTable;
	
	/**
	 * Create new instance and read cache information.
	 */
	public DetermineNumberOfEntries() {
		
		cacheTable = new TreeMap<Integer, Data>();
		
		try {
			/*
			 * Read into cache table only if cache file already exist.
			 */
			File cacheFile = new File(CACHE_FILE);
			if (cacheFile.exists()) {
				if (cacheFile.length() > 0) {
					readFromFileIntoCacheTable();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads LDIF file and returns count of dn: occurrences.
	 * 
	 * @param filePath
	 * @return entry count, or 0 value
	 * @throws IOException
	 */
	private int calculate(String filePath) throws IOException {

		int entryCount = 0;
		
		/*
		 * Open file in READ ONLY mode.
		 */
        RandomAccessFile ldifFile = new RandomAccessFile(filePath, "r");
        MappedByteBuffer buffer = ldifFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,  ldifFile.length());
		
        /*
         * Read whole file into buffer.
         */
        int ldifFileSize = (int) ldifFile.length();
        byte[] ldifFileData = new byte[ldifFileSize];
        buffer.get(ldifFileData);
        
        for (int i = 0; i < ldifFileSize - 2; i++) {
        	/*
        	 * Found dn: sequence in buffer.
        	 */
        	if (ldifFileData[i] == 'd' &&
        			ldifFileData[i + 1] == 'n' &&
        			ldifFileData[i + 2] == ':') {
        		entryCount++;
        	}
        }
        
        ldifFile.close();
        
        return entryCount;
	}
	
	/**
	 * Read cache and build table with entries.<BR>
	 * Table has file names and its entries count values.
	 * <TABLE BORDER=2>
	 *  <TR><TH>File path</TH><TH>Entry count</TH></TR>
	 *  <TR><TD>file1.ldif</TD><TD>123</TD></TR>
	 *  <TR><TD>file2.ldif</TD><TD>456</TD></TR>
	 *  <TR><TD>...</TD><TD>...</TD></TR>
	 * </TABLE>
	 * Cache is stored in {@link #CACHE_FILE}
	 * 
	 * @throws IOException
	 */
	private void readFromFileIntoCacheTable() throws IOException {

		/*
		 * Open file in READ ONLY mode.
		 */
        RandomAccessFile cacheFile = new RandomAccessFile(CACHE_FILE, "r");
        MappedByteBuffer buffer = cacheFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,  cacheFile.length());

        /*
         * Get row count.
         */
        int rowCount = buffer.getInt();
        
        /*
         * Get each row value.
         */
        for (int id = 0; id < rowCount; id++) {
        	/*
        	 * Read file path value.
        	 */
        	int filePathLen = buffer.getInt();
        	byte[] filePathBuffer = new byte[filePathLen];
        	buffer.get(filePathBuffer);
        	String filePathValue = new String(filePathBuffer);
        	
        	/*
        	 * Read lastModified value.
        	 */
        	Long lastModified = buffer.getLong();
        	
        	/*
        	 * Read file entry count. 
        	 */
        	int entryCount = buffer.getInt();
        	
        	/*
        	 * Store in cache table.
        	 */
        	Data data = new Data(filePathValue, lastModified, entryCount);
        	cacheTable.put(id, data);
        }
        
        cacheFile.close();
	}
	
	
	/**
	 * Write cache table into file. See {@link #cacheTable} and {@link #CACHE_FILE}.<BR>
	 * Table has file names and its entries count values.
	 * <TABLE BORDER=2>
	 *  <TR><TH>File path</TH><TH>Entry count</TH></TR>
	 *  <TR><TD>file1.ldif</TD><TD>123</TD></TR>
	 *  <TR><TD>file2.ldif</TD><TD>456</TD></TR>
	 *  <TR><TD>...</TD><TD>...</TD></TR>
	 * </TABLE>
	 * Cache is stored in {@link #CACHE_FILE}
	 * 
	 * @throws IOException
	 */
	private void writeCacheTableIntoFile() throws IOException {

		/*
		 * Determine cache table total data size.
		 * This is needed for MappedByteBuffer to set initial size.
		 */
		long mappedByteBufferSize = 0;
		for (Integer row : cacheTable.keySet()) {			
			Data data = cacheTable.get(row);
			mappedByteBufferSize = mappedByteBufferSize + data.getDataSize();
		}
		
		/*
		 * Do not update cache if cache table is empty.
		 */
		if (mappedByteBufferSize == 0) {
			return;
		}
		else {
			/*
			 * Increase buffer size for 1 extra Integer value
			 *  because of rowCount value being stored too.
			 */
			mappedByteBufferSize = mappedByteBufferSize + (Integer.SIZE / 8);
		}
		
		/*
		 * Open file in WRITE mode.
		 */
        RandomAccessFile cacheFile = new RandomAccessFile(CACHE_FILE, "rw");
        MappedByteBuffer buffer = cacheFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, mappedByteBufferSize);

        /*
         * Store row count.
         */
        int rowCount = cacheTable.size(); 
        buffer.putInt(rowCount);
        
        /*
         * Get each row value.
         */
        for (int id = 0; id < rowCount; id++) {
        	
        	Data data = cacheTable.get(id);
        	
        	/*
        	 * Store file path length and value.
        	 */
        	int filePathLen = data.filePath.length();
        	byte[] filePathBuffer = data.filePath.getBytes();
        	buffer.putInt(filePathLen);
        	buffer.put(filePathBuffer);
        	
        	/*
        	 * Store lastModified value.
        	 */
        	Long lastModified = data.lastModified;
        	buffer.putLong(lastModified);
        	
        	/*
        	 * Store file entry count. 
        	 */
        	int entryCount = data.entryCount;
        	buffer.putInt(entryCount);
        	
        }
        
        cacheFile.close();
	}
	
	/**
	 * Determine LDIF file entry number. This method counts how many
	 * occurrences of <I>dn:</I> sequence LDIF file has.
	 * 
	 * @param filePath LDIF file path
	 * @return 0 or entry count
	 * 
	 * @throws IOException
	 */
	public int determineEntryCount(String filePath) throws IOException {
		int retVal = 0;
		
		File file = new File(filePath);
		if (file.exists()) {
			/*
			 * Look in cache first.
			 */
			for (Integer id : cacheTable.keySet()) {
				Data data = cacheTable.get(id);
				
				if (data.filePath.equalsIgnoreCase(filePath)) {
					/*
					 * Found file path in table. Check last modified value.
					 */
					if (data.lastModified < file.lastModified()) {
						/*
						 * Cache has older value, calculate.
						 */
						int entryCount = calculate(filePath);
						retVal = entryCount;
						
						/*
						 * Update row with new values.
						 */
						data.lastModified = file.lastModified();
						data.entryCount = entryCount;
						
						/*
						 * Update cache file.
						 */
						writeCacheTableIntoFile();
						
						/*
						 * Stop loop.
						 */
						break;
					}
					else {
						/*
						 * Cache value is not older that file on file system.
						 */
						retVal = data.entryCount;
					}
				}
			}
			/*
			 * File not in cache.
			 */
			if (retVal == 0) {
				/*
				 * Determine entry count.
				 */
				int entryCount = calculate(filePath);
				
				/*
				 * Store in cache and write cache table to file system.
				 */
				Data data = new Data(filePath, file.lastModified(), entryCount);
				cacheTable.put(cacheTable.size(), data);
				writeCacheTableIntoFile();
				
				retVal = entryCount;
			}
		}
		else {
			/*
			 * Return 0 if file does not exist on file system.
			 */
			retVal = 0;
		}
		
		return retVal;
	}

}
