# LDAP Service

Code name: Pegasus

An Java LDAP server implementation. This application has support for the following:
  * LDIF file format
  * LDAP bind, search, add, delete and modify operation
  * Changes are temporary while this application is running
  * Simple authentication with any DN and password
  * Server-side count limiting (size limit)
  
Main parts of this implementation are:
  * Client Listener, see **ClientListener** class
  * LDAP Message decoder and encoder **MessageDecoder** and **MessageEncoder**
  * LDAP request handler **MessageHandler**
  * Backend Service, implementation of **ConcurrentBackend** for storing real data in form of **Entry** instances

Third-party libraries used in this project:
  * UnboundID LDAP SDK, Java library, https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html
  * Netty library, asynchronous event-driven network application framework, http://netty.io/
  

## Compile source

To compile code, use build.sh script file. Replace lines like:
<PRE>
/usr/jdk1.8.0_91/bin/javac $ARGS -d $OUTPUT_DIR -sourcepath $SOURCE_DIR $ENTRY
</PRE>
with appropriate path to JDK installation. This is example for **JDK 1.8.0_91** installed in **/usr** folder.
Result is generated jar file (usually **pegaz.jar**) which includes all *.class files from **bin/** folder.
Folders like **lib/** are not packed inside **pegaz.jar** archive.

Run script with:
<PRE>
./build.sh
</PRE>

Make sure script is executable.

## Basic usage

Start with:
<PRE>
java -jar pegaz.jar [options]
</PRE>
where [options] are:

<PRE>
  --bind  [ip1:port1,ip2:port2,...]   binds to specific ip socket(s). Default: 0.0.0.0:389
  --deref [ip1:port1,ip2:port2,...]   do alias dereferencing on selected socket(s). Default: none
  --ssl   [ip1:port1,ip2:port2,...]   mark socket as SSL/TLS. Requires key file. Default: none

  --keyFilename [filename         ]   for --ssl provide a valid PKCS12 (*.p12) key file. Default: 'server.p12'
  --keyPassword [secret string    ]   for --key provide access password. Default: ''

  --ldifFiles   [file1,file2, ... ]  ldif file list. If not provided, database will be empty.
  --schemaFiles [file1,file2, ... ]  OpenLDAP schema file list. If omitted, schemas are not used.
  --modifyFile  [myModify.ldif    ]  ldif file to save modifications on entries. Default: modify.ldif

  --countLimit  [0 .. 100000      ]  max. number of entries to return on search. Default: 0 (disabled, client controled)

  --multicastSyncInterface [name  ]  name of local ethernet interface for multicast synchronization. Eg. eth2 
  --multicastSyncGroup [ip addr.  ]  multicast group ip address for multicast synchronization. Eg. 230.100.100.1 
  --multicastSyncPort  [udp port  ]  multicast port number for multicast synchronization. Eg. 7100 

  --gui                              if set, Pegasus will try to open a window (requires Windows/X11 system).
  --debug                            if set, console will fill up with INFO messages for every ldap request.
  --disableLdapFilter                if set, filter in LDAP SEARCH request will be ignored.
</PRE>

To start an LDAP service on port TCP 1389, without SSL/TLS and any special features, use:

<PRE>
 java -jar --bind "0.0.0.0:1389" --ldifFiles "ldif/base.ldif" --countLimit 4096 --gui
</PRE>

LDAP service will run on TCP port 1389 and use "ldif/base.ldif" as initial directory tree.
All changes in directory tree are in memory, once LDAP service is restarted, changes are lost.
Nothing is permanently saved in LDIF file(s) or any other file.

**NOTE:** Root privileges are required to start LDAP service on TCP ports lower than 1024.

To use this program properly, see Pegasus.usage() function where other arguments are explained.

Author:
igor.delac@gmail.com