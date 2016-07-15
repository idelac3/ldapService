package hr.ericsson.pegasus.encoders;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.protocol.LDAPMessage;

import hr.ericsson.pegasus.ClientListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <H1>MessageEncoder</H1>
 * <HR>
 * Perform encoding of {@link LDAPMessage} object into {@link ByteBuf} byte array. This is Netty implementation, for more info
 * look at <A HREF=http://netty.io/5.0/api/io/netty/handler/codec/MessageToByteEncoder.html>Netty, User Guide, MessageToByteEncoder</A>
 * <P>This class is used by {@link ClientListener} class internally.</P>
 * <HR>
 * @author eigorde
 */
public class MessageEncoder extends MessageToByteEncoder<LDAPMessage> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, LDAPMessage msg, ByteBuf out) {
		
		ASN1Buffer asn1buffer = new ASN1Buffer();
		msg.writeTo(asn1buffer);
		
		out.writeBytes(asn1buffer.toByteArray());
	}

}
