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

## Public key infrastructure (PKI)

Here is an example how to build a test keys and certificates to enable SSL/TLS on LDAP server. **StartTLS** is not yet supported.
This example use **easy-rsa** package and scripts which in turn use **openssl** to build certificates and keys needed for SSL/TLS operation.

First step is to edit **vars** file in easy-rsa software folder.
<PRE>
 nano vars
</PRE>

Edit all variables that start with **KEY**. Here's an example for a **Company d.o.o** located in Croatia (HR), Dalmatia area.

<PRE>
 root@eclipseVM:/tmp/easy-rsa# export|grep KEY
 declare -x KEY_CITY="Split"
 declare -x KEY_CONFIG="/tmp/easy-rsa/openssl-1.0.0.cnf"
 declare -x KEY_COUNTRY="HR"
 declare -x KEY_DIR="/tmp/easy-rsa/keys"
 declare -x KEY_EMAIL="igor.delac@gmail.com"
 declare -x KEY_EXPIRE="3650"
 declare -x KEY_NAME="EasyRSA"
 declare -x KEY_ORG="Company d.o.o."
 declare -x KEY_OU="Company d.o.o, Unit X"
 declare -x KEY_PROVINCE="Dalmatia"
 declare -x KEY_SIZE="2048"
 root@eclipseVM:/tmp/easy-rsa#
</PRE>

Next, CA key and root certificate are needed.

<PRE>
 ./build-ca
</PRE>

It will ask some questions, most should have proper default values in brackets so it's ok to hit Enter for most.

<PRE>
Country Name (2 letter code) [HR]:
State or Province Name (full name) [Dalmatia]:
Locality Name (eg, city) [Split]:
Organization Name (eg, company) [Company d.o.o.]:
Organizational Unit Name (eg, section) [Company d.o.o, Unit X]:
Common Name (eg, your name or your server's hostname) [Company d.o.o. CA]:
Name [EasyRSA]:
Email Address [igor.delac@gmail.com]:
</PRE>

At this point, folder **easy-rsa/keys** should look like:

<PRE>
root@eclipseVM:/tmp/easy-rsa# ll keys/
total 20
drwx------ 2 root root 4096 Srp 27 07:57 ./
drwxr-xr-x 3 root root 4096 Srp 27 07:56 ../
-rw-r--r-- 1 root root 1842 Srp 27 07:57 ca.crt
-rw------- 1 root root 1704 Srp 27 07:57 ca.key
-rw-r--r-- 1 root root    0 Srp 27 07:56 index.txt
-rw-r--r-- 1 root root    3 Srp 27 07:56 serial
root@eclipseVM:/tmp/easy-rsa#
</PRE>

All the server and client keys will be signed by this CA certificate (ca.crt file). CA certificate should be available both to server and clients and it is public (eg. it's safe to copy and move it around).

CA key (ca.key) is however private and should be kept safe.

To build a PKCS12 server file which contains key+cert. all in one, use:

<PRE>
 ./build-key-pkcs12 eclipseVM
</PRE>

Again, it will ask some questions, most should have proper default values in brackets so it's ok to hit Enter for most.

Check server PKCS12 key with:

<PRE>
root@eclipseVM:/tmp/easy-rsa# openssl x509 -noout -text -in keys/eclipseVM.crt 
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 1 (0x1)
    Signature Algorithm: sha256WithRSAEncryption
        Issuer: C=HR, ST=Dalmatia, L=Split, O=Company d.o.o., OU=Company d.o.o, Unit X, CN=Company d.o.o. CA/name=EasyRSA/emailAddress=igor.delac@gmail.com
        Validity
            Not Before: Jul 27 06:03:32 2016 GMT
            Not After : Jul 25 06:03:32 2026 GMT
        Subject: C=HR, ST=Dalmatia, L=Split, O=Company d.o.o., OU=Company d.o.o, Unit X, CN=eclipseVM/name=EasyRSA/emailAddress=igor.delac@gmail.com
            
. . .

</PRE>

Note in line **Subject:** item **CN** which has value 'eclipseVM' in this example. This is host name of server. Some clients are very strict and require exact name of server, otherwise SSL/TLS session won't start.

Server key (in this example 'eclipseVM.p12') in PKCS12 format and CA certificate 'ca.crt' are generated, LDAP server will use server key and LDAP client(s) will use CA certificate file to verify server host name address (authentication).

Package **ldap-utils** contains commands **ldapsearch**, **ldapmodify**, etc. which all use **$HOME/.ldaprc** file. There put full path to CA certificate. Example of **.ldaprc**:
<PRE>
igor@eclipseVM:~$ cat .ldaprc 
TLS_CACERT	/tmp/easy-rsa/keys/ca.crt
igor@eclipseVM:~$
</PRE>

Example of LDAP search request:

<PRE>
igor@eclipseVM:~$ ldapsearch -x -H ldaps://eclipseVM:2777 -b ou=people,dc=padl,dc=com -s one -LLL -z 1
dn: uid=www-data,ou=People,dc=padl,dc=com
uid: www-data
cn: www-data
objectClass: account
objectClass: posixAccount
objectClass: top
objectClass: shadowAccount
userPassword:: e2NyeXB0fSo=
shadowLastChange: 16176
shadowMax: 99999
shadowWarning: 7
loginShell: /usr/sbin/nologin
uidNumber: 33
gidNumber: 33
homeDirectory: /var/www
gecos: www-data

igor@eclipseVM:~$
</PRE>

Here note the host name of LDAP server which is 'eclipseVM'. Otherwise if CN in certificate does not match host name, LDAP operation will fail for SSL/TLS socket. Port value could be any, in this example it is 2777, default is 636 for SSL/TLS LDAP service.

To start LDAP service with SSL/TLS socket enabled, use **--ssl** command switch:

<PRE>
java -jar pegaz.jar --bind "0.0.0.0:2777" --ssl "0.0.0.0:2777" --keyFilename /tmp/easy-rsa/keys/eclipseVM.p12 --ldifFiles "ldif/base.ldif,ldif/group.ldif,ldif/hosts.ldif,ldif/passwd.ldif" --schemaFiles "schema" --countLimit 4096 --gui
</PRE>

Ref.

  * https://en.wikipedia.org/wiki/Public_key_infrastructure
  * https://openvpn.net/index.php/open-source/documentation/miscellaneous/77-rsa-key-management.html
  
  
Author:
igor.delac@gmail.com