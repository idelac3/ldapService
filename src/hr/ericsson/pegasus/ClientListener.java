package hr.ericsson.pegasus;

import hr.ericsson.pegasus.encoders.MessageDecoder;
import hr.ericsson.pegasus.encoders.MessageEncoder;
import hr.ericsson.pegasus.handler.MessageHandler;
import hr.ericsson.pegasus.handler.MessageHandlerMulticastSync;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NetUtil;

/**
 * <H1>ClientListener</H1>
 * <HR>
 * Listen to new client connections. This is Netty implementation, for more info
 * look at <A HREF=http://netty.io/wiki/user-guide-for-5.x.html#wiki-h2-5>Netty, User Guide</A>
 * <P>To use this class, run it in separate thread.</P>
 * <HR>
 * @author eigorde
 */
public class ClientListener implements Runnable {

	/**
	 * Host name or ip address of ip interfaces to listen. If not sure, put <I>0.0.0.0</I>
	 */
	private String host;
	
	/**
	 * Available (unused) tcp port. Eg. <I>389</I>
	 */
    private int port;

    /**
     * Alias dereferencing flag for this listening socket.
     */
    private boolean aliasDeref;
    
    /**
     * How many worker thread to run.
     */
    private int threadCount;
    
    /**
     * Channel handler instance for {@link ServerBootstrap}.<BR>
     * See {@link ChannelHandler} doc. for more information.
     */
    private ChannelHandler channelHandler;
    
    /**
     * Channel pipeline that holds LDAP message decoder, encoder and handler.<BR>
     * See {@link ChannelPipeline} for more information.
     */
    private ChannelPipeline channelPipeline;
    
    /**
     * New ClientListener instance.
     * Provide socket information and alias deref. flag for LDAP modify operations.
     * 
     * @param host ip interface to listen, for all use <I>0.0.0.0</I>
     * @param port tcp port number, eg. <I>389</I>
     * 
     * @param aliasDeref perform first alias dereferencing on LDAP modify request,
     * then apply modification(s)
     */
    public ClientListener(String host, int port, 
    		final boolean aliasDeref, final boolean disableLdapFilter) {
    	
    	this.host = host;
        this.port = port;
                
        this.aliasDeref = aliasDeref;
    
        this.threadCount = Runtime.getRuntime().availableProcessors();

       	this.channelHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

            	/*
            	 * Save channel pipeline instance to be accessible for other methods.
            	 */
            	channelPipeline = ch.pipeline();
            	
            	/*
            	 *  Message coders and decoders should be first in pipeline.
            	 */
            	channelPipeline.addLast(new MessageDecoder());                    		 
            	channelPipeline.addLast(new MessageEncoder());
            	
            	/*
            	 *  Main logic. Message handler.
            	 *  
            	 *  NOTE: This order in pipeline is required by Netty.
            	 *  Ref.
            	 *  http://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html
            	 *  
            	 *  NOTE2: Here program select which message handler to initialize depending
            	 *  if multicast sync. is needed or not.
            	 */
            	
            	boolean noMulticastSyncCondition = (Pegasus.multicastSync == null || aliasDeref == false);

            	if (noMulticastSyncCondition) {
            		/*
            		 * Use message handler without multicast sync. feature.
            		 */
                	channelPipeline.addLast(
                			new MessageHandler(aliasDeref, disableLdapFilter));            	

                	Pegasus.debug("MessageHandler instance loaded.");
            	}
            	else {
            		/*
            		 * Use message handler with multicast sync. feature.
            		 */
            		channelPipeline.addLast(
                			new MessageHandlerMulticastSync(aliasDeref, disableLdapFilter));
            		
            		Pegasus.debug("MessageHandler instance with multicast synchronization loaded.");
            	}
            	

            }
            
        };


    }

    /**
     * Find out loaded version of {@link MessageHandler} instance.
     * @return version string, eg. <I>v1.00</I> or empty value <I>""</I>
     */
    public String getMessageHandlerVersion() {
    	
    	if (channelPipeline != null) {
    		if (channelPipeline.last() instanceof MessageHandler) {
    			return ( (MessageHandler)channelPipeline.last()).VERSION;
    		}
    		if (channelPipeline.last() instanceof MessageHandlerMulticastSync) {
    			return ( (MessageHandlerMulticastSync)channelPipeline.last()).VERSION;
    		}
    	}
    	
    	return "";
    }
    
    /**
     * Try to reload {@link MessageHandler} instance. This method will remove current
     * instance from channel pipeline and create a new instance and put it in channel pipeline.
     */
    public void reloadMessageHandlerInstance() {

    	if (channelPipeline != null) {
    		if (channelPipeline.last() instanceof MessageHandler ||
    				channelPipeline.last() instanceof MessageHandlerMulticastSync) {
    			Pegasus.debug("Reloading MessageHandler instance ... ");
    			ChannelHandler lastChannelHandler = channelPipeline.removeLast();
    			channelPipeline.addLast(lastChannelHandler);
    			Pegasus.debug("Done.");
    		}
    	}

    }
    
    @Override
	public String toString() {
		return "ClientListener, " + host + ":" + port
				+ ", alias dereferencing is " + (aliasDeref ? "on" : "off") + ", thread count is " + threadCount
				+ " thread(s)";
	}

	@Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(threadCount);
        try {            
        	ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(channelHandler)  
             // Allow maximum number of client connections.
             .option(ChannelOption.SO_BACKLOG, NetUtil.SOMAXCONN)
             // Allocate 64 kB buffer for tcp messages.
             .childOption(ChannelOption.SO_RCVBUF, 64 * 1024)
             // Turn on tcp keep alive function on socket.
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             // Turn off tcp delay (Nagles alg.)
             .childOption(ChannelOption.TCP_NODELAY, true);
            
            ChannelFuture f = null;
			try {
				
				/*
				 *  Bind and start to accept incoming connections.
				 */
				f = bootstrap.bind(host, port).sync();
				
	            /* Wait until the server socket is closed.
	             * In this example, this does not happen, but you can
	             * do that to gracefully shut down your server. 
	             */
				f.channel().closeFuture().sync();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}


        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}