package hr.ericsson.pegasus;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFException;

import hr.ericsson.pegasus.DetermineEntryCount.DetermineNumberOfEntries;
import hr.ericsson.pegasus.backend.ConcurrentBackend;
import hr.ericsson.pegasus.encoders.MessageDecoder;
import hr.ericsson.pegasus.encoders.MessageEncoder;
import hr.ericsson.pegasus.gui.JFrameGui;
import hr.ericsson.pegasus.handler.MessageHandler;
import hr.ericsson.pegasus.multicast.MulticastListener;
import hr.ericsson.pegasus.welcome.JFrameWelcome;

/**
 * <H1>Pegasus</H1>
 * <HR>
 * An Java coded LDAP server implementation. This
 * application has support for the following:
 * <UL>
 *  <LI>LDIF file format</LI>
 *  <LI>LDAP bind, search, add, delete and modify operation</LI>
 *  <LI>Changes are temporary while this application is running</LI>
 *  <LI>Simple authentication with any DN and password</LI>
 *  <LI>Server-side count limiting (size limit)</LI>
 * </UL>
 * Main parts of this implementation are:
 * <OL>
 *  <LI>Client Listener, see {@link ClientListener}</LI>
 *  <LI>LDAP Message decoder and encoder {@link MessageDecoder} and {@link MessageEncoder}</LI>
 *  <LI>LDAP request handler {@link MessageHandler}</LI>
 *  <LI>Backend Service, implementation of {@link ConcurrentBackend} for storing real data in form of 
 *  {@link Entry} instances</LI>
 * </OL>
 * To use this program properly, see {@link Pegasus#usage()} function where other arguments are explained.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class Pegasus {
	
	/**
	 * Version string.
	 */
	public static final String ver = "0.25";

	/**
	 * Kilobyte and Megabyte units.
	 */
	static long kb = 1024, mb = kb * 1024;
	
	/**
	 * Statistic data.
	 */
	public volatile static long clientConnections = 0,
			searchRequestsBase = 0,
			searchRequestsOne = 0,
			searchRequestsSub = 0,
			entryResults = 0,
			failedSearch = 0,
			modifyRequests = 0,
			failedModify = 0,
			addRequest = 0,
			failedAdd = 0,
			deleteRequest = 0,
			failedDelete = 0;
	

	/**
	 * Application window. (GUI)
	 */
	public static JFrameGui gui;
	
	/**
	 * Reference to Backend instance.
	 */
	public static ConcurrentBackend myBackend;

	/**
	 * Schema instance for validation of LDAP operations.
	 */
	public static Schema schema;
	
	/**
	 * Size limit for LDAP search operations (one level, whole subtree). 
	 */
	public static int countLimit = 100;
	
	/**
	 * Determinator instance used to find out how many records LDIF file has.<BR>
	 * See {@link DetermineNumberOfEntries} ref.
	 */
	public static DetermineNumberOfEntries determinator = new DetermineNumberOfEntries();
	
	/**
	 * Placeholder for instances of {@link ClientListener}. Each listener binds to ip interface and port,
	 * and has alias dereferencing on or off, etc. Ip interface might be <I>0.0.0.0</I> to bind to all available.
	 */
	public static List<ClientListener> clientListenerList = new ArrayList<ClientListener>();
	
	/**
	 * Multicast listener and sender for multicast synchronization.
	 * If <I>null</I>, synchronization is disabled.
	 */
	public static MulticastListener multicastSync = null;
	
	/**
	 * Flag to show if user wants print of each LDAP request / message
	 * on console or window.
	 */
	public static boolean debugEnabled = false;
	
	/**
	 * A value set only once when this application has started.
	 * Should be used as:
	 * <PRE>
	 *  currentTime = System.currentTimeMillis() - uptime;
	 * </PRE>
	 * to get uptime of this application in milliseconds. See {@link System#currentTimeMillis()}
	 * for more information.
	 */
	public static long uptime;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		log("Pegasus v" + ver);

		/*
		 * Parse command line arguments.
		 */
		GetOpts op = new GetOpts(args);
		
		/*
		 * Print usage help and quit.
		 */
		if (op.isSwitch("-h") || op.isSwitch("--help")) {
			usage();
			return;
		}
		
		/*
		 * Show Welcome screen when no arguments are passed.
		 */
		if (args.length == 0) {
			Runnable doRun = new Runnable() {

				@Override
				public void run() {
					(new JFrameWelcome()).setVisible(true);
				}
			};

			SwingUtilities.invokeLater(doRun);
			return;
		}

		String bindSockets = "0.0.0.0:389";
		if (op.isSwitch("--bind")) {
			bindSockets = op.getSwitch("--bind");
		}

		String derefSockets = "";
		if (op.isSwitch("--deref")) {
			derefSockets = op.getSwitch("--deref");
		}

		String sslSockets = "";
		if (op.isSwitch("--ssl")) {
			sslSockets = op.getSwitch("--ssl");
		}

		String pkcs12filename = "server.p12";
		if (op.isSwitch("--keyFilename")) {
			pkcs12filename = op.getSwitch("--keyFilename");
		}

		String pkcs12password = "";
		if (op.isSwitch("--keyPassword")) {
			pkcs12password = op.getSwitch("--keyPassword");
		}

		String ldifFiles = "";
		if (op.isSwitch("--ldifFiles")) {
			ldifFiles = op.getSwitch("--ldifFiles");
		}

		boolean stdSchema = false;
		if (op.isSwitch("--std-schema")) {
			stdSchema = true;
		}

		String schemaFiles = "";
		if (op.isSwitch("--schemaFiles")) {
			schemaFiles = op.getSwitch("--schemaFiles");
		}

		if (op.isSwitch("--countLimit")) {
			countLimit = toInteger(op.getSwitch("--countLimit"), 100);
		}
		
		String multicastSyncInterface = null;
		String multicastSyncGroup = "230.100.100.1";
		int multicastSyncPort = 7100;

		if (op.isSwitch("--multicastSyncInterface")) {
			multicastSyncInterface = op.getSwitch("--multicastSyncInterface");
						
			if (op.isSwitch("--multicastSyncGroup")) {			
				multicastSyncGroup = op.getSwitch("--multicastSyncGroup");
			}
			
			if (op.isSwitch("--multicastSyncPort")) {			
				multicastSyncPort = toInteger(op.getSwitch("--multicastSyncPort"), 7100);
			}
			
			/*
			 * Create instance of multicast sync. listener and join to group.
			 */
			multicastSync = new MulticastListener(multicastSyncInterface, multicastSyncPort);
			multicastSync.join(multicastSyncGroup);
			
		}
		
		if (op.isSwitch("--debug")) {
			/*
			 *  Turn on debugging on console or window.
			 */
			debugEnabled = true;
		}
		
		Runnable guiRunnable = new Runnable() {
			public void run() {
				gui = new JFrameGui();
				gui.setVisible(true);
			}
		};
		
		if (op.isSwitch("--gui")) {
			
			if (gui == null) {
				// Use the event dispatch thread for Swing components.
				EventQueue.invokeLater(guiRunnable);
			}
			
			log("Loading ...");
			while (gui == null) {
				// Here wait for Swing components to load.
				Thread.yield();
			}			
		}
		else {
			gui = null;
		}
		
		List<String> fileList = new ArrayList<String>();
		for (String item : ldifFiles.split(",")) {
			fileList.add(item);
		}
		
		/*
		 * Do the LDIF file list dereferencing first.
		 */
		dereferenceFileList(fileList);
		
		/*
		 * Print current settings.
		 */
		log("Current settings");
		log("");
		log("Def.encoding: " + System.getProperty("file.encoding") + " (To change use -Dfile.encoding=[encoding] JVM argument.)");
		log("Bind sockets: " + bindSockets);
		log("Deref. lists: " + derefSockets);
		if (fileList.size() > 1) {
			log("  Ldif files: ");
			for (String file : fileList) {
				File f = new File(file);
				log("   " + file + " " + formatInteger(f.length()) );
			}
		}
		else if (fileList.size() == 1) {
			log("  Ldif files: " + fileList.get(0));
		}
		else {
			log("  Ldif files: - ");
		}
		log("     Root DN: " + findRootDN(fileList));
		log("Schema files: " + schemaFiles);
		log(" Count limit: " + countLimit);
		log("");
		log("     CPU core(s) available: " + Runtime.getRuntime().availableProcessors());
		log("Allocated memory available: " + formatInteger(Runtime.getRuntime().totalMemory()) );
		log("");
		
        if (multicastSync != null) {        
        	log("Multicast Listener synchronization enabled on " + multicastSyncInterface + 
        		" interface.");        
        	log("Multicast group and port are " + multicastSyncGroup + ":" + multicastSyncPort + ".");
        	log("");
        }
        
		/*
		 * Import schema files.
		 */
        
        List<String> invalidSchemaFileList = new ArrayList<String>();
        
        if (stdSchema) {
        	/*
        	 * Load first standard schema definition 
        	 * from UnboundID library.
        	 */
        	try {
				schema = Schema.getDefaultStandardSchema();
			} catch (LDAPException e) {
				Pegasus.debug("ERROR: Standard schema is not loaded.");
				e.printStackTrace();
			}
        }
        
		if (schemaFiles.length() > 0) {

			for (String schemaFile : schemaFiles.split(",")) {
				
				File schema = new File(schemaFile);
				
				if (schema.exists()) {
					if (schema.isFile()) {
			
						/*
						 * Then load into Schema instance.
						 */
						try {
							if (Pegasus.schema == null) {
								Pegasus.schema = Schema.getSchema(schema);
							}
							else {
								Pegasus.schema = Schema.mergeSchemas(Pegasus.schema, Schema.getSchema(schema));
							}
						} catch (LDIFException e) {
							invalidSchemaFileList.add(schemaFile);
						}
						
					}
					else if (schema.isDirectory()) {
						/*
						 * Support for folder with *.schema (OpenLDAP format) and *.ldif (RFC format) schema files.
						 */
						for (String dirItem : schema.list()) {
							if (dirItem.endsWith(".schema") || dirItem.endsWith(".ldif")) {
								
								File schemaItem = new File(schema.getName() + "/" + dirItem);
								if (schemaItem.isFile()) {

									try {
										if (Pegasus.schema == null) {
											Pegasus.schema = Schema.getSchema(schema);
										}
										else {
											Pegasus.schema = Schema.mergeSchemas(Pegasus.schema, Schema.getSchema(schema));
										}
									} catch (LDIFException e) {
										invalidSchemaFileList.add(schemaFile);
									}
									
								}
							}
						}
					}
					else {
						log("Schema file " + schemaFile + " is neither file nor directory.");
					}
				}
				else {
					log("Schema file " + schemaFile + " not found.");
				}
			}
		}
		
		/*
		 * Convert OpenLDAP schema into LDIF file according to RFC 4512.
		 * Ref. http://www.zytrax.com/books/ldap/ch3/
		 */
		if (invalidSchemaFileList.size() > 0) {
			
			/*
			 * Looks like some schema files are not in LDIF format,
			 * try to convert into LDIF every single file in system temp.
			 * folder. Then load it with Schema.getSchema(...) method.
			 */
			for (String filename : invalidSchemaFileList) {
			
				String tmpFilename = convertSchemaFile(filename);

				Pegasus.debug("Schema file " + filename + " converted to LDIF format in " + tmpFilename + ".");
				
				try {
					schema = Schema.getSchema(tmpFilename);
				} catch (LDIFException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}	
		}
        
		/*
		 * Determine initial capacity for backend service based on 
		 *  number of entries in each LDIF file.
		 */		
		long totalEntries = 0;
		for (String ldifFile : fileList) {
			File file = new File(ldifFile);
			if (file.isFile()) {
				long entryCount = determinator.determineEntryCount(file.getAbsolutePath());
				totalEntries = totalEntries + entryCount;
			}
			else if (file.isDirectory()) {
				/*
				 * Support for folder with *.ldif files.
				 */
				for (String dirItem : file.list()) {
					if (dirItem.endsWith(".ldif")) {
						File ldifItem = new File(file.getName() + "/" + dirItem);
						if (ldifItem.isFile()) {
							long entryCount = determinator.determineEntryCount(ldifItem.getAbsolutePath());
							totalEntries = totalEntries + entryCount;
						}
					}
				}
			}
			else {
				log ("LDIF " + file + " is not file or folder.");
			}
		}

		/*
		 * Start backend service.
		 */
		myBackend = new ConcurrentBackend();

		/*
		 * Populate backend with data from LDIF files(s).
		 */
		for (String ldifFile : fileList) {
			File file = new File(ldifFile);
			
			if (file.isFile()) {
				long entryCount = determinator.determineEntryCount(file.getAbsolutePath());
				if (file.length() > 0 && entryCount > 0) {
					/*
					 * Load LDIF on backend only if file has at least 1 entry.
					 */
					myBackend.ldifRead(file);
				}
			}
			else if (file.isDirectory()) {
				/*
				 * Support for folder with *.ldif files.
				 */
				for (String dirItem : file.list()) {
					if (dirItem.endsWith(".ldif")) {
						File ldifItem = new File(file.getName() + "/" + dirItem);
						if (ldifItem.isFile()) {
							long entryCount = determinator.determineEntryCount(ldifItem.getAbsolutePath());
							if (ldifItem.length() > 0 && entryCount > 0) {
								/*
								 * Load LDIF on backend only if file has at least 1 entry.
								 */
								myBackend.ldifRead(ldifItem);
							}
						}
					}
				}
			}
			else {
				log ("LDIF " + file + " is not file or folder.");
			}
			
		}
		
		/*
		 * Print warning if no LDIF files were loaded. 
		 */
		if (totalEntries == 0) {
			log ("");
			log ("*****************************************************************");
			log ("WARNING: Load at least one LDIF file to populate LDAP database.");
			log ("*****************************************************************");
			log ("");
		}
		
		String[] socketList = bindSockets.split(",");

		/*
		 * Count how many interfaces are doing dereferencing.
		 */
		int countDerefInterfaces = 0;
		
		/*
		 * Bind to one of more sockets.
		 */
		for (String socketItem : socketList) {
			/*
			 * Extract address and port.
			 */
			int separatorIndex = socketItem.indexOf(":");
			/*
			 *  Default is 0.0.0.0:389
			 */
			String address = "0.0.0.0";
			int port = 389;
			if (separatorIndex > 0) {
				address = socketItem.substring(0, separatorIndex);
				port = toInteger(socketItem.substring(separatorIndex + 1), 389);
			}
			else if (separatorIndex == 0) {
				port = toInteger(socketItem.substring(1), 389);
			}
			else {
				address = socketItem;
			}

			/*
			 * Indicator if interface should do first alias dereferencing on ldap modify request.
			 */
			boolean aliasDeref = (derefSockets.indexOf(address + ":" + port) >= 0);

			ClientListener listener = new ClientListener(
					address, port, 
					aliasDeref);
			
			/*
			 * Turn on SSL if needed.
			 */
			boolean sslFlag = (sslSockets.indexOf(address + ":" + port) >= 0);
			if (sslFlag) {
			
				/*
				 * For server-side SSL, application needs PKCS12 private key and password
				 * to open key file. Based on PKCS12 file, an Java KeyStore instance is created. 
				 */				
				try {
					KeyStore keyStore = buildKeyStore(pkcs12filename, pkcs12password);
					listener.setSSL(keyStore);
					log ("Socket " + address + ":" + port + " is marked for SSL/TLS. SSL/TLS enabled on this socket.");
				}
				catch (Exception ex) {
					log ("Socket " + address + ":" + port + " failed to enable SSL/TLS. Check server key, PKCS12 format, path to file " + pkcs12filename + ".");
					ex.printStackTrace();
				}
			}
			
			/*
			 * Start listener.
			 */
			new Thread(listener, "Listener [" + address + ":" + port + "]").start();

			log("Listener started on: " + address + ":" + port);

			/*
			 * Save instance in list.
			 */
			clientListenerList.add(listener);
			
			if (aliasDeref) {
				countDerefInterfaces++;
			}
		}

		if (countDerefInterfaces == 0 && multicastSync != null) {
			/*
			 * This is a problem, multicast synchronization won't work over
			 * listeners that are not doing dereferencing of modify requests 
			 * on aliases.
			 */
			log ("");
			log ("**********************************************************************************");
			log ("WARNING: Multicast sync. is turned on, but no sockets are dereferencing.");
			log ("**********************************************************************************");
			log ("");
		}
		
		log("");
		log("Memory usage: " + formatInteger(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " / " +
				formatInteger(Runtime.getRuntime().totalMemory()) );
		log("");
		
		if (countLimit == 0) {
			log("WARNING: Limit is set to 0.");
			log("This may slow down ldap search operations and also indexing of large files will take up more time.");
		}
		
        /*
         * Install hook for Ctrl+C.
         */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log("Ldap service terminated.");
			}
		});
		
		/*
		 * Set uptime to current time.
		 */
		uptime = System.currentTimeMillis();
		
	}	 

	/**
	 * Convert OpenLDAP schema file into LDIF schema file.
	 * 
	 * @param filename input schema file
	 * @return full path to resulting LDIF schema file
	 * 
	 * @throws IOException
	 */
	public static String convertSchemaFile(String filename) throws IOException {
	
		SchemaReader schemaReader = new SchemaReader();
		schemaReader.loadSchemaFile(new File(filename));
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File tmpSchemaFile = new File(tmpDir + "/" + filename);
		FileWriter fileWriter = new FileWriter(tmpSchemaFile);
		fileWriter.write(schemaReader.toLDIF());
		fileWriter.close();
		
		return tmpSchemaFile.getAbsolutePath();
	}
	
	/**
	 * Find an root DN string from LDIF file list.<BR>
	 * Items are LDIF files, and one that has shortest 'dn: ....'
	 * should be base LDIF and should be loaded first.  
	 * @param fileList list array with LDIF files
	 * @return base DN value if list is not empty, or <I>null</I> value
	 * @throws IOException
	 */
	public static String findRootDN(List<String> fileList) throws IOException {
	
		String baseDN = null;
		
		if (fileList == null) {
			log ("LDIF file list is null. Please fill it with valid LDIFs.");
			return null;
		}
		
		for (String filename : fileList) {
			
			try {
			
				BufferedReader br = new BufferedReader(new FileReader(filename));
				
				String line;
				
				int dnCounter = 0;
				
				while ( (line = br.readLine()) != null) {
				
					/*
					 * Look for 'dn: ' in each line.
					 */
					if (line.startsWith("dn: ")) {

						String dn = line.substring("dn: ".length());
						
						/*
						 * If shorter DN value is found, then assign it.
						 */
						if (baseDN == null) {
							baseDN = dn;
						}
						else if (baseDN.length() > dn.length()) {
							baseDN = dn;
						}
						
						if (dnCounter > 10) {
							/*
							 * Don't process more than first 10 DN values in LDIF.
							 */
							break;
						}
						
						dnCounter++;
					}
				}				
				
				br.close();
				
			} catch (FileNotFoundException ex) {
				log ("File " + filename + " not found.");
			}
		}
		
		return baseDN;
		
	}
	
	/**
	 * Dereference items starting with @ char from LDIF file list.<BR>
	 * Items that have @ char in beginning are text files that contain
	 * a file list, so their content should be loaded instead.  
	 * @param fileList list array with LDIF files and text files
	 * @throws IOException
	 */
	public static void dereferenceFileList(List<String> fileList) throws IOException {
		
		if (fileList == null) {
			return;
		}
		
		if (fileList.size() == 0) {
			return;
		}
		
		List<String> newItems = new ArrayList<String>();
		
		boolean recursiveFlag = false;
		
		Iterator<String> it = fileList.iterator();
		
		/*
		 * Search for items like @list.txt which should contain a list of LDIF
		 * files to load.
		 */
		while (it.hasNext()) {
			String item = it.next();
			
			/*
			 * Found item that is a reference to LDIF file list.
			 */
			if (item.charAt(0) == '@') {
				
				File file = new File(item.substring(1));
				if (file.exists()) {
					String path = file.getParent();
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					while ( (line = br.readLine()) != null) {
						
						File ldif = new File(line);
						if (ldif.exists()) {
							newItems.add(line);
						}
						else {
							/*
							 * Try to use base path from list file
							 * and append it to item in list.
							 */
							ldif = new File(path + "/" + line);
							if (ldif.exists()) {
								newItems.add(ldif.getAbsolutePath());
							}
						}
						
						/*
						 * Check that newItems list should be dereferenced too.
						 */
						if (line.charAt(0) == '@') {
							recursiveFlag = true;
						}
					}
					br.close();
				}
				it.remove();
			}
		}
		
		if (recursiveFlag) {
			dereferenceFileList(newItems);
		}
		
		fileList.addAll(newItems);
	}
	
	/**
	 * Write debug to console or main window.
	 * This function will write text only if debugging is enabled.
	 * @param msg a line of text
	 */
	public static void debug(String msg) {
		if (debugEnabled) {
			log (msg);
		}
	}
	
	/**
	 * Print message on console.
	 * @param msg single line of message
	 */
	public static void log(String msg) {
		if (gui != null) {
			gui.log(msg);
		}
		else {
			System.out.println(msg);
		}
	}

	/**
	 * Print message on console without new line (LF) termination.
	 * @param msg single line of message
	 */
	public static void append(String msg) {
		if (gui != null) {
			gui.append(msg);
		}
		else {
			System.out.print(msg);
		}
	}
	
	/**
	 * Print message lines on console.
	 * @param msg multi-line text 
	 */
	public static void log(String[] msg) {
		if (gui != null) {			
			for (String line : msg) {
				gui.log(line);
			}
		} else {
			for (String line : msg) {
				System.out.println(line);
			}
		}
	}
	
	/**
	 * Conversion function to convert number in string into integer.
	 * 
	 * @param number
	 *            string which holds integer number
	 * @param defaultValue
	 *            default value to return if conversion fails
	 * @return converted integer from string
	 */
	public static int toInteger(String number, int defaultValue) {
		int retVal = 0;

		try {
			retVal = Integer.parseInt(number);
		} catch (NumberFormatException exception) {
			retVal = defaultValue;
		}

		return retVal;
	}

	/**
	 * Rounds number to <I>MB</I> or <I>kB</I>.
	 * 
	 * @param number
	 *            number of bytes
	 * @return rounded number with appended unit, eg. <I>24 kB</I>
	 */
	public static String formatInteger(long number) {
		String retVal;
		long unit = kb;
		long result = number / unit;
		String sufix = " kB";
		if (result > 1000) {
			unit = mb;
			result = number / unit;
			sufix = " MB";
		} else if (result == 0) {
			result = number;
			sufix = " byte";
		}
		retVal = String.valueOf(result) + sufix;
		return retVal;
	}

	/**
	 * Formats time in format [hh]:mm:ss if possible.
	 * 
	 * @param timestamp
	 *            UNIX time stamp value, eg. <I>System.currentTime()</I>, etc.
	 * @return formatted time string
	 */
	public static String formatTime(long timestamp) {
		String retVal = "";

		// Convert milisec. to sec.
		timestamp = timestamp / 1000;

		if (timestamp < 60) {
			retVal = String.valueOf(timestamp) + " sec.";
		} else if (timestamp < 3600) {
			long min = timestamp / 60;
			long sec = timestamp % 60;
			if (sec < 10) {
				retVal = String.valueOf(min) + ":0"
						+ String.valueOf(sec);				
			}
			else {
				retVal = String.valueOf(min) + ":"
					+ String.valueOf(sec);
			}
		} else if (timestamp < 24 * 3600) {
			
			long hour = timestamp / 3600;
			long min = (timestamp - (hour * 3600)) / 60;
			long sec = (timestamp - (hour * 3600)) % 60;
			
			if (min > 9 && sec > 9) {
				retVal = String.valueOf(hour) + ":"
					+ String.valueOf(min) + ":"
					+ String.valueOf(sec);
			} 
			else if (min < 10 && sec > 9) {
				retVal = String.valueOf(hour) + ":0"
						+ String.valueOf(min) + ":"
						+ String.valueOf(sec);
				
			}
			else if (min < 10 && sec < 10) {
				retVal = String.valueOf(hour) + ":0"
						+ String.valueOf(min) + ":0"
						+ String.valueOf(sec);
				
			}			
		} else {
			retVal = String.valueOf(timestamp / (24 * 3600)) + " days.";
		}

		return retVal;
	}

	/**
	 * 
	 * Build Java {@link KeyStore} object based on key filename and key password. Key should be in PKCS12 format.
	 * To build a PKCS12 key file using openssl utility, use command:
	 * <PRE>
	 *  openssl pkcs12 -export -inkey server.key -in server.crt -name "server" -certfile ca.crt -caname "Root CA" -out server.p12
	 * </PRE>
	 * where <I>server.key</I> and <I>server.crt</I> are in PEM format generated server key and certificate. Result is <I>server.p12</I>
	 * key file.<BR>
	 * This is typical command for PKI.
	 * More at <A HREF=https://en.wikipedia.org/wiki/Public_key_infrastructure>Public key infrastructure</A> at Wikipedia.
	 * 
	 * @param keyFile a PKCS12 file name, full path or relative path if file is stored in application folder
	 * @param keyPassword optional password for an access to open a PKCS12 key file
	 * @return {@link KeyStore} instance required for {@link ClientListener#setSSL(KeyStore)} method
	 * 
	 * @throws KeyStoreException if wrong format of file is provided, eg. keyFile is not a PKCS12 file
	 * @throws NoSuchAlgorithmException if PKCS12 KeyStore format is unsupported on selected JVM
	 * @throws CertificateException if key in PKCS12 file is expired
	 * @throws FileNotFoundException if key file path is wrong 
	 * @throws IOException read permission for PKCS12 key file is not available, or IO reading error
	 */
	private static KeyStore buildKeyStore (String keyFile, String keyPassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {

    	/*
    	 * Make a KeyStore object from a provided PKCS12 file (key + cert in *.p12 mixed)
    	 */
    	final KeyStore keyStore;
    	
    	keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyFile), keyPassword.toCharArray());
	
        return keyStore;
	}
	
	/**
	 * Print application usage information.
	 */
	private static void usage() {
		String msg = "Usage:\n"
				+ "  --bind  [ip1:port1,ip2:port2,...]   binds to specific ip socket(s). Default: 0.0.0.0:389\n"
				+ "  --deref [ip1:port1,ip2:port2,...]   do alias dereferencing on selected socket(s). Default: none\n"
				+ "  --ssl   [ip1:port1,ip2:port2,...]   mark socket as SSL/TLS. Requires key file. Default: none\n"
				+ "\n"
				+ "  --keyFilename [filename         ]   for --ssl provide a valid PKCS12 (*.p12) key file. Default: 'server.p12'\n"
				+ "  --keyPassword [secret string    ]   for --key provide access password. Default: ''\n"
				+ "\n"
				+ "  --ldifFiles   [file1,file2, ... ]  ldif file list. If not provided, database will be empty.\n"
				+ "\n"
				+ "  --std-schema                       Use standard schema definition. This is recommended to use.\n"
				+ "  --schemaFiles [file1,file2, ... ]  LDIF or OpenLDAP schema file list. If omitted, schemas are not used.\n"
				+ "\n"
				+ "  --modifyFile  [myModify.ldif    ]  ldif file to save modifications on entries. Default: modify.ldif\n"
				+ "\n"
				+ "  --countLimit  [0 .. 100000      ]  max. number of entries to return on search. Default: 0 (disabled, client controled)\n"
				+ "\n"
				+ "  --multicastSyncInterface [name  ]  name of local ethernet interface for multicast synchronization. Eg. eth2 \n"
				+ "  --multicastSyncGroup [ip addr.  ]  multicast group ip address for multicast synchronization. Eg. 230.100.100.1 \n"
				+ "  --multicastSyncPort  [udp port  ]  multicast port number for multicast synchronization. Eg. 7100 \n"
				+ "\n"
				+ "  --gui                              if set, Pegasus will try to open a window (requires Windows/X11 system).\n"
				+ "  --debug                            if set, console will fill up with debug information for every LDAP request / message.\n"
				+ "\n"
				+ "Author: Igor Delac <igor.delac@gmail.com>\n"
				+ "\n";
		log(msg);
	}
	
}
