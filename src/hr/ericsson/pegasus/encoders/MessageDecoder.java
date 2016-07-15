package hr.ericsson.pegasus.encoders;
import hr.ericsson.pegasus.ClientListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>MessageDecoder</H1>
 * <HR>
 * Perform decoding of {@link ByteBuf} byte array into {@link LDAPMessage} object. This is Netty implementation, for more info
 * look at <A HREF=http://netty.io/5.0/api/io/netty/handler/codec/ByteToMessageDecoder.html>Netty, User Guide, ByteToMessageDecoder</A>
 * <P>This class is used by {@link ClientListener} class internally.</P>
 * <HR>
 * @author eigorde
 */
public class MessageDecoder extends ByteToMessageDecoder {
		
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {

		// Wait until the length prefix is available.
		if (in.readableBytes() < 5) {
			return;
		}

		// Save reader index at beginning.
		in.markReaderIndex();
		
		byte magic = in.readByte();
		
		// Should be always 0x30 value.
		if (magic != 0x30) {
			// For now, discard it.
			System.out.printf("Invalid magic value: %02X", magic);
			// And stop processing of request.
			return;
		}
		
		// How many bytes are needed to complete reading of message.
		int requestLen = in.readUnsignedByte();
		
		// How many bytes describe request length (1 or more bytes).
		int countLen = 0;
		
		if (requestLen < 0x80) {			
			// One byte represents the request length.
			countLen = 1;
		}
		else {
			// Number of bytes that represent request length.
			countLen = requestLen - 0x80;
			
			requestLen = 0;
			
			// Compose new length.
			for (int i = 0, j = countLen - 1; i < countLen; i++, j--) {
				requestLen += (in.readUnsignedByte() << (j * 8));
			}
			
			// Make counting to include also first byte after magic.
			countLen++;

		}
		
		
		// Read complete message from buffer.
		byte[] decoded = new byte[1 + countLen + requestLen];
		
		// Move index back.
		in.resetReaderIndex();

		
		// Check that is possible to read complete message in buffer.
		if (in.readableBytes() >= decoded.length) {
			// Read LDAP message request.
			in.readBytes(decoded);
		}
		else {
			// Not enough bytes in buffer, skip reading.
			return;
		}

		// Convert byte array to LDAPMessage instance.
		try {
		
			LDAPMessage ldapMessage = LDAPMessage.readFrom(
						new ASN1StreamReader(
						new ByteArrayInputStream(decoded)), true);
			
			// Add LDAPMessage object to pipeline for further processing. 
			out.add(ldapMessage);
			
		}
		catch (LDAPException ex) {
			print(decoded);
			// ex.printStackTrace();
		}
	}
	
	/**
	 * Print on console byte array. Max. 256 lines, each 16 bytes.
	 * @param buffer byte array
	 */
	private void print(byte[] buffer) {
		
		final int MAX = 256;
		
		System.out.println(Thread.currentThread().getName() + " request received:");
		System.out.println("Buffer len: " + buffer.length);
		
		int line = 0;
		
		for (int i = 0; i < buffer.length; i++) {
			
			if (i % 16 == 0) {
				
				if (line > MAX) {
					break;
				}
				
				System.out.println();
				System.out.printf("%04X ", line);
			
				line += 16;
				
			}
			
			System.out.printf("%02X ", buffer[i]);
		}
		
		System.out.println();
		System.out.println();
		System.out.println();
	}


}
