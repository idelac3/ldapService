# ldapService

Code name: Pegasus

An Java LDAP server implementation. This application has support for the following:
  * LDIF file format
  * LDAP bind, search, add, delete and modify operation
  * Changes are temporary while this application is running
  * Simple authentication with any DN and password
  * Server-side count limiting (size limit)
  
Main parts of this implementation are:
  * Client Listener, see ClientListener
  * LDAP Message decoder and encoder MessageDecoder and MessageEncoder
  * LDAP request handler MessageHandler
  * Backend Service, implementation of ConcurrentBackend for storing real data in form of Entry instances

To use this program properly, see Pegasus.usage() function where other arguments are explained.

Author:
igor.delac@gmail.com