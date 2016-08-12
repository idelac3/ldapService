package hr.ericsson.pegasus.handler;

import java.util.List;

import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.TransferBackendData;
import hr.ericsson.pegasus.multicast.MulticastDatagramHandler;
import hr.ericsson.pegasus.multicast.MulticastListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/**
 * This is sub-class of {@link MessageHandler} class.<BR>
 * <HR>
 * It will do multicast synchronization after message (LDAP request) being processed:<BR>
 * - send a datagram message to a multicast group to other servers/listeners<BR>
 * - a received message will be handled by multicast listener handler separately,
 * see {@link MulticastDatagramHandler}<BR>
 * <BR>
 * Note that only LDAP ADD, DELETE and MODIFY requests should be broadcasted to other
 * peers in same multicast group.<BR>
 * <BR>
 * Since request is first handled in {@link MessageHandler} class, this instance won't check
 * if request failed or was successful. It will be sent out to multicast group anyway.<BR>
 * <BR>
 * <B>NOTE:</B> Multicast synchronization can be turned off without unloading this message handler,
 * see {@link Pegasus#multicastSync}, method {@link MulticastListener#isJoinPerformed()}. Boolean value 
 * is also considered when broadcasting multicast announcement to other peer LDAP servers. <BR>
 * <BR>
 * <B>NOTE2:</B> This class has protection against unwanted backend data transfer procedure for fall-back and
 * recovery of broken peers. It is not allowed to use message handler with multicast sync. enabled, and if that
 * happens, LDAP BIND request will be denied. 
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class MessageHandlerMulticastSync extends MessageHandler {

	private ASN1Buffer asn1buffer;
	
	public MessageHandlerMulticastSync(boolean aliasDeref) {
		super(aliasDeref);
		asn1buffer = new ASN1Buffer();
	}
	
	
    @Override
    public void messageReceived(ChannelHandlerContext ctx, LDAPMessage ldapMessage) {
    	
    	/*
    	 * Check that administrator did not accidently try to do
    	 * backend data transfer to a multicast sync. message handler instance.
    	 * 
    	 * In that case, refuse LDAP BIND operation.
    	 */
    	if (ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_BIND_REQUEST) {
    		
    		int messageID = ldapMessage.getMessageID();
    		
    		if (ldapMessage.getBindRequestProtocolOp().getBindDN().equalsIgnoreCase(TransferBackendData.bindDN) &&
    				ldapMessage.getBindRequestProtocolOp().getSimplePassword().stringValue().equalsIgnoreCase(TransferBackendData.bindPassword) ) {

    			String matchedDN = "";
    			String diagnosticMessage = 
    					"Bind to a multicast sync. enabled socket not allowed for a backend data transfer.";
    			List<String> referralURLs = null;
    			
    			ProtocolOp bindResponseProtocolOp = new BindResponseProtocolOp(
    				ResultCode.INVALID_CREDENTIALS_INT_VALUE, matchedDN,
    				diagnosticMessage, referralURLs, null);

    			LDAPMessage ldapResponse = new LDAPMessage(messageID, bindResponseProtocolOp,
    					StaticUtils.NO_CONTROLS);
    		
    			ctx.writeAndFlush(ldapResponse);

    			Pegasus.log ("");
    			Pegasus.log ("*****************************************************************");
    			Pegasus.log ("WARNING: An attempt to transfer backend data from remote peer ");
    			Pegasus.log ("          was initiated on socket where multicast sync. is enabled.");
    			Pegasus.log ("*****************************************************************");
    			Pegasus.log ("");
    			
    			return;
    		}
    	}
    	
    	/*
    	 * Process message as usual. See parent class.
    	 */
    	super.messageReceived(ctx, ldapMessage);

    	/*
    	 * Do multicast announcement for LDAP ADD/MODIFY/DELETE requests.
    	 * First define criteria. 
    	 */
    	boolean multicastCriteria = (
    			ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST ||
    			
    			ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST ||
    			
    			ldapMessage.getProtocolOpType() == 
    			LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST);
    	
    	/*
    	 * Take into account that multicast join is performed, otherwise
    	 * it's safe to consider that multicast synchronization is disabled (by user).
    	 */
    	multicastCriteria = multicastCriteria & Pegasus.multicastSync.isJoinPerformed();
    	
    	/*
    	 * Send LDAP message request to multicast group of receivers.
    	 */
    	if ( multicastCriteria ) {
   
    		asn1buffer.clear();
    		ldapMessage.writeTo(asn1buffer);
    		
    		ByteBuf data = Unpooled.copiedBuffer(asn1buffer.toByteArray());    		

    		Pegasus.multicastSync.send(data);

    		Pegasus.debug("LDAP request announced to multicast group.");

    	}
    	
    }

}
