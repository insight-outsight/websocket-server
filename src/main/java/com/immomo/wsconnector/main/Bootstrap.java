package com.immomo.wsconnector.main;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.immomo.wsconnector.config.WSSConfig;
import com.immomo.wsconnector.context.Context;
import com.immomo.wsconnector.handler.AuthHandler;
import com.immomo.wsconnector.handler.FullHttpRequestHandler;
import com.immomo.wsconnector.handler.MsgHandler;
import com.immomo.wsconnector.handler.PingHandler;
import com.immomo.wsconnector.handler.TailHandler;
import com.immomo.wsconnector.handler.WebSocketFrameHandler;
import com.immomo.wsconnector.util.ConfigUtils;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		String propFileName = "wss.properties";
		if(args.length>0){
			propFileName=args[0]+".properties";
		}
		else{
			System.out.println("using default properties file["+propFileName+"]");
		}
		PropertiesConfiguration config = ConfigUtils.loadConfiguration("/"+propFileName);
		Context.setWSSConfig(new WSSConfig(config));
		ThreadFactory bossGroupThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat("netty-acceptor-%d")
        .setDaemon(false)
        .build();
		
		ThreadFactory workerGroupThreadFactory = new ThreadFactoryBuilder()
				.setNameFormat("netty-worker-%d")
				.setDaemon(false)
				.build();
		
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(10,bossGroupThreadFactory);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,workerGroupThreadFactory);
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			
			/**
			 *  handler方法和childHandler方法区别
				handler存在于AbstractBootstrap，目的是添加一个handler，监听Bootstrap动作，它在初始化时就会执行
				childHandler存在于ServerBootstrap，目的是添加一个handler，监听已经连接的客户端的Channel的动作和状态，在客户端连接成功后才执行。
				option方法和childOption方法区别
				option存在于AbstractBootstrap，提供给NioServerSocketChannel用来接收进入的连接。
				childOption存在于ServerBootstrap ，提供给ServerChannel接收已经建立的连接。
			 */
			bootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(70, 0, 70));
					ch.pipeline().addLast(new HttpServerCodec());//ByteBuf->HttpRequest或HttpContent和HttpResponse或者HttpContent->ByteBuf，即HTTP请求的解码和编码
//					ch.pipeline().addLast(new ChunkedWriteHandler());//大文件支持
					ch.pipeline().addLast(new HttpObjectAggregator(64*1024));//把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
//					ch.pipeline().addLast(new WebSocketServerCompressionHandler());
					/**添加后会报如下错误：
					io.netty.handler.codec.CorruptedFrameException: RSV != 0 and no extension negotiated, RSV:4
	at io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder.protocolViolation(WebSocket08FrameDecoder.java:412)
	at io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder.decode(WebSocket08FrameDecoder.java:188)
	
	stackoverflow.com:
					WebSocket compression is enabled in some browsers by default (at the time of writing for example in Chrome, but
					 not in Firefox). The client has to include the 'Sec-WebSocket-Extensions: permessage-deflate' header for this. 
					 If the server responds with the same extension, the WebSocket communication is compressed on a frame basis. As 
					 far as I know, there is no browser API to enable/disable extensions.

				A good article about the topic is: https://www.igvita.com/2013/11/27/configuring-and-optimizing-websocket-compression/
					*/
					ch.pipeline().addLast(new FullHttpRequestHandler());
					ch.pipeline().addLast(new WebSocketFrameHandler());
					ch.pipeline().addLast(new PingHandler());
					ch.pipeline().addLast(new AuthHandler());
					ch.pipeline().addLast(new MsgHandler());
					ch.pipeline().addLast(new TailHandler());
				} 
			});
			Channel channel = bootstrap.bind(new InetSocketAddress(Context.getWssConfig().getDomain(),Integer.parseInt(Context.getWssConfig().getPort()))).sync().channel();
			System.out.println("run ["+Bootstrap.class.getName()+"] on "+Context.getWssConfig().getDomain()+":"+Context.getWssConfig().getPort()+" OK.");
//            logger.info("The http server powered by netty is listening on " + port);

			channel.closeFuture().sync();//阻塞处理，等待服务端链路关闭之后main函数才退出
		} catch (Exception e) {
			System.err.println("run ["+Bootstrap.class.getName()+"] Error.");
			e.printStackTrace();
		}  
		finally {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
	}
}
