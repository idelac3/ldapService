package hr.ericsson.pegasus.multicast;

import hr.ericsson.pegasus.Pegasus;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <H1>Multicast Listener</H1>
 * <HR>
 * A multicast listener joins a multicast
 * group (multicast ip address) and receives udp datagram.
 * Multicast listener might or might not send multicast datagrams too.
 * This implementation does send multicast datagrams. See {@link #send(ByteBuf)} method.<BR>
 * <BR>
 * Received data is handled by handler. See {@link MulticastDatagramHandler} class for detail.<BR>
 * <BR>
 * To send a test multicast datagram packet, use <I>netcat</I> package:<BR>
 * <PRE>
 *  netcat -u -s 192.168.56.101 239.195.255.255 7686
 * </PRE>
 * where:<BR>
 *  <I>192.168.56.101</I> is source interface ip address, <BR>
 *  <I>239.195.255.255</I> is multicast group and <BR>
 *  <I>7686</I> is udp port number.<BR>
 * <BR>
 * On some Java implementations, this is required:<BR>
 * <PRE>
 * -Djava.net.preferIPv4Stack=true
 * </PRE>
 * as argument to force IPv4 stack instead of IPv6.<BR>
 * <BR>
 * Instance of this class can be also used to send multicast datagrams.
 * Use {@link #send(ByteBuf)} method. An extra udp socket will be open 
 * for sending udp datagrams, since some systems have trouble with
 * receiving multicast datagrams on same udp socket which is used for sending.<BR>
 * <BR>
 * This is the main reason why listening is always done on <I>0.0.0.0</I> ip interface.<BR> 
 * <BR>
 * This class is known to be working on all operating systems (Solaris / Linux / Windows).
 * <HR>
 * @author eigorde
 *
 */
public class MulticastListener {

    private int port;
    private String groupAddress;
    private boolean joinGroupFlag;
    
    private DatagramChannel sendChannel;
    private DatagramChannel receiveChannel;
    
    private NetworkInterface multicastInterface;

    private InetSocketAddress localInetAddress;
    
    private EventLoopGroup eventLoopGroup1;
    private EventLoopGroup eventLoopGroup2;
    
    /**
     * Instance of Multicast listener.<BR>
     * <B>NOTE:</B> This instance will work only on single ethernet interface. Multiple instances of this class
     * might not work on some systems.
     * 
     * @param localInterfaceName a name of local ethernet interface to join group and send datagrams,
     * eg. <I>eth3</I>
     * @param port udp port number to receive and send multicast datagrams
     * @throws InterruptedException
     * @throws SocketException
     */
    public MulticastListener(String localInterfaceName, int port) throws InterruptedException, SocketException {

    	this.joinGroupFlag = false;
    	
    	this.port = port;
    	this.multicastInterface = (NetworkInterface.getByName(localInterfaceName)); 

    	Enumeration<InetAddress> enumInetAddress = this.multicastInterface.getInetAddresses();
    	while (enumInetAddress.hasMoreElements()) {
    		InetAddress inetAddr = enumInetAddress.nextElement();
    		InetSocketAddress socketAddr = new InetSocketAddress(inetAddr, this.port);
    		this.localInetAddress = (socketAddr);
    	}

    	/*
    	 * Channel for sending multicast datagrams.
    	 */
        this.eventLoopGroup1 = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup1)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(new LoggingHandler());

        this.sendChannel = (DatagramChannel) b.bind(this.localInetAddress.getAddress().getHostAddress(), 0).sync().channel();

        /*
         * Channel to receive multicast datagrams.
         */
        this.eventLoopGroup2 = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(eventLoopGroup1)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            public void initChannel(DatagramChannel ch) throws Exception {
            	ChannelPipeline channelPipeline = ch.pipeline();
            	channelPipeline.addLast(new MulticastDatagramHandler());                    		 
            }
        });

        b.localAddress(this.port);
        
        this.receiveChannel = (DatagramChannel) b.bind().sync().channel();
        
    }
    
    /**
     * Close this multicast listener.
     */
    public void close() {
    	eventLoopGroup1.shutdownGracefully();
    	eventLoopGroup2.shutdownGracefully();
    }
    
    /**
     * Join multicast group.
     * @param groupAddress multicast ip address
     * @throws InterruptedException
     */
    public void join(String groupAddress) throws InterruptedException {
    	InetSocketAddress groupAddressSocket = new InetSocketAddress(groupAddress, port);
    	this.receiveChannel.joinGroup(groupAddressSocket, multicastInterface).sync();
    	this.groupAddress = groupAddress;
    	this.joinGroupFlag = true;
    	
    	Pegasus.debug("Multicast group join sent to " + groupAddress + ".");
    }
    
    /**
     * Leave specific multicast group.
     * @param groupAddress multicast ip address
     * @throws InterruptedException
     */
    public void leave(String groupAddress) throws InterruptedException {
    	InetSocketAddress groupAddressSocket = new InetSocketAddress(groupAddress, port);
    	this.receiveChannel.leaveGroup(groupAddressSocket, multicastInterface).sync();
    	this.groupAddress = groupAddress;
    	this.joinGroupFlag = false;
    	
    	Pegasus.debug("Multicast group leave sent to " + groupAddress + ".");
    }
    
    /**
     * Join again to multicast group. For the first time, use {@link #join(String)} method.
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
    	if (groupAddress != null) {
    		join(groupAddress);
    	}
    }

    /**
     * Leave multicast group. Before leave, use {@link #join(String)} method to join group.
     * @throws InterruptedException
     */
    public void leave() throws InterruptedException {
    	if (groupAddress != null) {
    		leave(groupAddress);
    	}
    }

	/**
	 * Broadcast datagram to a group of multicast receivers.
	 * @param data {@link ByteBuf} array
	 */
	public void send(ByteBuf data) {
	    InetSocketAddress recipient = new InetSocketAddress(groupAddress, port);
        DatagramPacket datagram = new DatagramPacket(data, recipient);
        sendChannel.writeAndFlush(datagram);   
	}
	
	/**
	 * Determine if this instance has already done join IGMP request.
	 * @return <I>true</I> if join was performed, or <I>false</I>
	 */
	public boolean isJoinPerformed() {
		return joinGroupFlag;
	}
	
    /**
     * Build {@link NetworkInterface} list. Criteria here is that interfaces is not
     * loopback or point to point, and interface supports multicast and has MAC address.
     * @return list of ethernet interfaces
     * @throws SocketException
     */
    public static List<NetworkInterface> getNetworkInterfaceList() throws SocketException {
    	
    	Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    	
    	List<NetworkInterface> retVal = new ArrayList<NetworkInterface>();
    	
    	while (networkInterfaces.hasMoreElements()) {
    		NetworkInterface networkInterface = networkInterfaces.nextElement();
    		if (networkInterface.isLoopback()) {
    			// Skip loopback interfaces.
    		}
    		else if (networkInterface.isPointToPoint()) {
    			// Skip point to point interfaces.    			
    		}
    		else if (networkInterface.supportsMulticast() &&
    				networkInterface.getHardwareAddress() != null) {
    			// Make sure interface is ethernet and supports multicast.
    			retVal.add(networkInterface);
    		}
    	}
 
    	return retVal;
    }

}
