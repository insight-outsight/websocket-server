package com.selffun.wsconnector.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.selffun.wsconnector.constant.WsConstants;
import com.selffun.wsconnector.protocol.GeneralRequest;
import com.selffun.wsconnector.util.JacksonJSONUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>{

	private final  Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName()+" channel actived");
		super.channelActive(ctx);
    }
    
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName()+" channel inactived");
		super.channelInactive(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
		// 判断是否关闭链路的指令
		if (webSocketFrame instanceof CloseWebSocketFrame) {
			System.out.println("is CloseWebSocketFrame");
			WebSocketServerHandshaker handshaker = null;//ctx.channel().attr(WsConstants.HANDSHAKER_ATTR_KEY).get();
			if(handshaker!=null){
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
			}
			else{
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                return;
			}
		}
		// 判断是否ping消息
		if (webSocketFrame instanceof PingWebSocketFrame) {
			System.out.println("is PingWebSocketFrame");
			ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
			return;
		}
		// 本例仅支持文本消息，不支持二进制消息
		if (!(webSocketFrame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", webSocketFrame.getClass().getName()));
		}
		String receivedText = ((TextWebSocketFrame) webSocketFrame).text();
		System.out.println("recieved  TextWebSocketFrame text:"+receivedText);

		GeneralRequest receivedData = parseRequest(receivedText);
		if(receivedData!=null){
			ctx.fireChannelRead(receivedData);
		}
		else{
			ctx.channel().writeAndFlush(this.getClass().getSimpleName()+" unkown msg format["+receivedText+"]");
			ctx.channel().close();
		}
		
	}

	

	private GeneralRequest parseRequest(String textWebSocketFrameText) {
		try {
			return JacksonJSONUtils.fromJSON(textWebSocketFrameText, GeneralRequest.class);
		} catch (Exception e) {
			logger.error("parseRequest: "+textWebSocketFrameText+" Error", e);
			return null;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}
}
