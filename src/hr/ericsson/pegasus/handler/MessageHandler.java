package hr.ericsson.pegasus.handler;
import hr.ericsson.pegasus.ClientListener;
import hr.ericsson.pegasus.Pegasus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>MessageHandler</H1>
 * <HR>
 * Perform handling of {@link LDAPMessage} request, using direct access to {@link Pegasus#myBackend} instance and its data.
 * This is Netty implementation, for more info
 * look at <A HREF=http://netty.io/5.0/api/io/netty/channel/SimpleChannelInboundHandler.html>Netty, User Guide, SimpleChannelInboundHandler</A>
 * <P>This class is used by {@link ClientListener} class internally.</P>
 * <HR>
 */
public class MessageHandler extends SimpleChannelInboundHandler<LDAPMessage>  {

	/**
	 * This {@link MessageHandler} version string.
	 */
	public final String VERSION = "v1.01";
	
    /**
     * Alias dereferencing flag for this listening socket.
     */
    private boolean aliasDeref;
    
    /**
     * LDAP Add handler.
     */
    private LdapAddHandler ldapAddHandler;
    
    /**
     * LDAP Bind handler.
     */
    private LdapBindHandler ldapBindHandler;
    
    /**
     * LDAP Delete handler.
     */
    private LdapDeleteHandler ldapDeleteHandler;
    
    /**
     * LDAP Modify handler.
     */
    private LdapModifyHandler ldapModifyHandler;
    
    /**
     * LDAP Search handler.
     */
    private LdapSearchHandler ldapSearchHandler;
    
	/**
	 * Create new instance of MessageHandler.
	 * 
     * @param aliasDeref perform first alias dereferencing on LDAP modify request
     * @param disableLdapFilter <I>true</I> to disable LDAP filter matching and increase speed
     * 
     **/
	public MessageHandler (boolean aliasDeref, boolean disableLdapFilter) {
        
		// Save dereferencing flag.
        this.aliasDeref = aliasDeref;

        // Create handler instances for each LDAP operation.
        ldapAddHandler = new LdapAddHandler();
        ldapBindHandler = new LdapBindHandler();
        ldapDeleteHandler = new LdapDeleteHandler();
        ldapModifyHandler = new LdapModifyHandler(this.aliasDeref);
        ldapSearchHandler = (disableLdapFilter ? new LdapSearchWithoutFilterHandler() : new LdapSearchHandler());

	}
	
    @Override
    public void messageReceived(ChannelHandlerContext ctx, LDAPMessage ldapMessage) {

    	int messageID = ldapMessage.getMessageID();
    	
    	Pegasus.debug("LDAP request " + messageID + " received from " + ctx.channel().remoteAddress() + " ...");
    	
    	if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_ABANDON_REQUEST) {
    		// Abandon request is not handled.
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST) {
    		ctx.write(ldapAddHandler.
    				processAddRequest(
    						messageID, ldapMessage.getAddRequestProtocolOp()));
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_BIND_REQUEST) {
    		ctx.write(ldapBindHandler.
    				processBindRequest(
    						messageID, ldapMessage.getBindRequestProtocolOp()));            	
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST) {
    		ctx.write(ldapDeleteHandler.
    				processDeleteRequest(
    						messageID, ldapMessage.getDeleteRequestProtocolOp()));      
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST) {
    		ctx.write(ldapModifyHandler.
    				processModifyRequest(
    						messageID, ldapMessage.getModifyRequestProtocolOp()));      
    	}
    	else if (ldapMessage.getProtocolOpType() == 
        			LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST) {
    		// Do not handle this type of request yet.
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST) {
    		ldapSearchHandler.setChannelHandler(ctx);
    		try {
				ctx.write(ldapSearchHandler.
						processSearchRequest(
								messageID, ldapMessage.getSearchRequestProtocolOp()));
			} catch (LDAPException e) {
				exceptionCaught(ctx, e);
			}
    	}
    	else if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_UNBIND_REQUEST) {
    		ctx.disconnect();
    		Pegasus.clientConnections--;
    		return;
    	}
    	
    	// Do actual write.
    	ctx.flush();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
