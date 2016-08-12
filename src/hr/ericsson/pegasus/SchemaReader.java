package hr.ericsson.pegasus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <H1>Schema file Reader for OpenLdap compatible schema files.</H1>
 * <HR>
 * This class implements methods for reading some objects from schema file
 * like object class definitions and attribute types.<BR>
 * <BR>
 * It can read OpenLdap schema files and provided information in LDIF format
 * suitable when replying to LDAP requests related to schema information.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class SchemaReader {
	
	/**
	 * List of attributeTypes objects from schema file(s).
	 * May be empty if schema files are not in use.
	 */
	private List<String> attributeTypes;

	/**
	 * List of objectClasses objects from schema file(s).
	 * May be empty if schema files are not in use.
	 */
	private List<String> objectClasses;
	
	/**
	 * Hold list of loaded schema file(s).
	 */
	private List<String> fileList;
	
    /**
	 * Create new Schema Reader object.
	 */
	public SchemaReader() {
    	if (attributeTypes == null) {
    		attributeTypes = new ArrayList<String>();
    	}
    	if (objectClasses == null) {
    		objectClasses = new ArrayList<String>();
    	}
    	if (fileList == null) {
    		fileList = new ArrayList<String>();
    	}
	}

	/**
	 * Get schema file list of loaded schema files.
	 * 
	 * @return file list, should be *.schema and *.ldif list
	 * of files
	 */
	public List<String> getSchemaFileList() {
		return fileList;
	}
	
	/**
     * Load OpenLDAP compatible schema file.<BR>
     * This function will read schema entries in OpenLDAP format,
     * and later allow fetching and listing of <I>objectClass</I> and
     * <I>attributeType</I> objects.
     * @param schemaFile input schema as File object
     * @throws IOException
     */
    public void loadSchemaFile(File schemaFile) throws IOException {

        String schemaFileName = schemaFile.getName();

        if (schemaFileName.indexOf('.') < 0) {
            throw new IOException("Unknown file format.");
        }
                
        BufferedReader bReader = new BufferedReader(new FileReader(schemaFile));

        String line;
        
        String item = "";

        while ((line = bReader.readLine()) != null) {

            boolean flag = false;

            if (line.trim().startsWith("attributetype")) {
                // Replacement for attributetype.
                flag = true;
                line = line.replaceAll("attributetype", "attributeTypes:");
            }
            else if (line.trim().startsWith("objectclass")) {
                // Replacement for objectclass.
                flag = true;
                line = line.replaceAll("objectclass", "objectClasses:");
            }   
            else if (line.trim().startsWith("objectClasses:") || line.trim().startsWith("attributeTypes:") ) {
            	flag = true;
            } else if (line.trim().startsWith("#")) {
                // Skip comment lines.
            } else if (line.length() == 0) {
                // Skip empty lines.
            } else {
                item = item + line;
            }

            // Process item and add it to result.
            if (flag) {
                if (item.length() > 0) {

                    // Make sure brackets have extra space chars.
                    int i = 0;
                    while (i < item.length()) {
                        if (item.charAt(i) == '(' || item.charAt(i) == ')') {
                            item = item.substring(0, i) + " " + item.charAt(i) + " " + item.substring(i + 1);
                            i = i + 2;
                        }

                        i++;
                    }

                    // Replace tab char with space.
                    while (item.indexOf("\t") >= 0) {
                        int idx = item.indexOf("\t");
                        item = item.substring(0, idx) + " " + item.substring(idx + 1);
                    }

                    // Remove double spaces.
                    while (item.indexOf("  ") >= 0) {
                        int idx = item.indexOf("  ");
                        item = item.substring(0, idx) + item.substring(idx + 1);
                    }

                    // Add trimmed item to result.
                    if (item.startsWith("attributeTypes:")) {
                    	attributeTypes.add(item.trim());
                    }
                    else if (item.startsWith("objectClasses:")) {
                    	objectClasses.add(item.trim());
                    }
                    
                }
                item = line;
            }

        }

        // Process last item.
        if (item.length() > 0) {

            // Make sure brackets have extra space chars.
            int i = 0;
            while (i < item.length()) {
                if (item.charAt(i) == '(' || item.charAt(i) == ')') {
                    item = item.substring(0, i) + " " + item.charAt(i) + " " + item.substring(i + 1);
                    i = i + 2;
                }

                i++;
            }

            // Replace tab char with space.
            while (item.indexOf("\t") >= 0) {
                int idx = item.indexOf("\t");
                item = item.substring(0, idx) + " " + item.substring(idx + 1);
            }

            // Remove double spaces.
            while (item.indexOf("  ") >= 0) {
                int idx = item.indexOf("  ");
                item = item.substring(0, idx) + item.substring(idx + 1);
            }

            // Add trimmed item to result.
            if (item.startsWith("attributeTypes:")) {
            	attributeTypes.add(item.trim());
            }
            else if (item.startsWith("objectClasses:")) {
            	objectClasses.add(item.trim());
            }
            
        }

        bReader.close();

        fileList.add(schemaFileName);
        
    }
    
	/**
	 * Get collection of attributeTypes objects from schema file(s).
	 * @return the attributeTypes
	 */
	public List<String> getAttributeTypes() {
		return attributeTypes;
	}

	/**
	 * Get collection of objectClasses objects from schema file(s).
	 * @return the objectClasses
	 */
	public List<String> getObjectClasses() {
		return objectClasses;
	}

	/**
	 * Return {@link SchemaReader} instance content in LDIF format.
	 * 
	 * @return object class and attributes in this instance
	 */
	public String toLDIF() {
		
		String header = "dn: cn=schema\n" +
				"objectClass: top\n" +
				"objectClass: ldapSubEntry\n" +
				"objectClass: subschema\n" +
				"cn: schema\n";
		
		return header + toString();
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for (String attributeItem : attributeTypes) {
			sb.append(attributeItem);
			sb.append('\n');
		}
		
		for (String objectClassItem : objectClasses) {
			sb.append(objectClassItem);
			sb.append('\n');
		}
		
		return sb.toString();
		
	}
	
}
