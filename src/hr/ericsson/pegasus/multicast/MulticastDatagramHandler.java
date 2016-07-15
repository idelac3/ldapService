package hr.ericsson.pegasus.multicast;
import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.ConcurrentBackend;
import hr.ericsson.pegasus.encoders.MessageDecoder;
import hr.ericsson.pegasus.handler.LdapAddHandler;
import hr.ericsson.pegasus.handler.LdapDeleteHandler;
import hr.ericsson.pegasus.handler.LdapModifyHandler;
import hr.ericsson.pegasus.handler.MessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>MulticastDatagramHandler</H1>
 * <HR>
 * This handler will handle LDAP ADD, DELETE and MODIFY requests,
 * by directly modifying application backend (see {@link ConcurrentBackend}) that holds data.<BR>
 * <BR>
 * This handler directly decodes single udp packet which should hold complete LDAP message request.
 * After decoding, it is processed by one of sub-handlers for LDAP ADD, DELETE and MODIFY requests.
 * Here it is assumed that LDAP requests are smaller packets, and would fit into single udp datagram.<BR> 
 * <BR>
 * This class borrows some code from {@link MessageDecoder} and {@link MessageHandler} classes.
 * <HR>
 * @author eigorde
 */
public class MulticastDatagramHandler extends SimpleChannelInboundHandler<DatagramPacket>  {
	
    /**
     * LDAP Add handler.
     */
    private LdapAddHandler ldapAddHandler;
    
    /**
     * LDAP Delete handler.
     */
    private LdapDeleteHandler ldapDeleteHandler;
    
    /**
     * LDAP Modify handler.
     */
    private LdapModifyHandler ldapModifyHandler;
    
    /**
     * List of local interfaces.
     */
    private List<InetAddress> localInterfaces;
    
    /**
     * New instance of multicast datagram handler.
     */
	public MulticastDatagramHandler() {
		
		/*
		 * Create instances for LDAP ADD/DELETE/MODIFY requests. 
		 */
		ldapAddHandler = new LdapAddHandler();
		ldapDeleteHandler = new LdapDeleteHandler();
		ldapModifyHandler = new LdapModifyHandler(true);
		
		localInterfaces = new ArrayList<InetAddress>();
		
		try {
			for (NetworkInterface iface : MulticastListener.getNetworkInterfaceList()) {
				Enumeration<InetAddress> enumInetAddress = iface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress addr = enumInetAddress.nextElement();
					localInterfaces.add(addr);
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) {

    	/*
    	 * Avoid processing of local packets.
    	 */
    	InetAddress sourceIP = packet.sender().getAddress();
    	if (sourceIP.isLoopbackAddress() || 
    			localInterfaces.contains(sourceIP)) {
    		return;
    	}
    	
    	/*
    	 * This is necessary to convert direct ByteBuf instance into byte array.
    	 */
    	ByteBuf buffer = packet.content();    	
		byte[] data = new byte[buffer.readableBytes()];
		buffer.getBytes(0, data);
		
    	try {
			
    		/*
    		 * Decode byte array into LDAPMessage instance.
    		 */
    		LDAPMessage ldapMessage = LDAPMessage.readFrom(
					new ASN1StreamReader(
					new ByteArrayInputStream(data)), true);
			
    		int messageID = ldapMessage.getMessageID();
    		
    		/*
    		 * Check if message is LDAP ADD, DELETE or MODIFY request.
    		 * Other messages are not considered and peers should never send them.
    		 */
    		if (ldapMessage.getProtocolOpType() == 
        			LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST) {
        		ctx.write(ldapAddHandler.
        				processAddRequest(
        						messageID, ldapMessage.getAddRequestProtocolOp()));
        		
        		Pegasus.debug("LDAP ADD request received from multicast peer.");
        	}
        	else if (ldapMessage.getProtocolOpType() == 
        			LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST) {
        		ctx.write(ldapDeleteHandler.
        				processDeleteRequest(
        						messageID, ldapMessage.getDeleteRequestProtocolOp()));
        		
        		Pegasus.debug("LDAP DELETE request received from multicast peer.");
        	}
        	else if (ldapMessage.getProtocolOpType() == 
        			LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST) {
        		ctx.write(ldapModifyHandler.
        				processModifyRequest(
        						messageID, ldapMessage.getModifyRequestProtocolOp()));
        		
        		Pegasus.debug("LDAP MODIFY request received from multicast peer.");
        	}
        	else {
        	
        		Pegasus.debug("Unsupported request received from multicast peer.");
        	}
    		
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
