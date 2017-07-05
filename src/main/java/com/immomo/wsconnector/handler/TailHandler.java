package com.immomo.wsconnector.handler;

import com.immomo.wsconnector.constant.WsConstants;
import com.immomo.wsconnector.session.WebClientSessionManager;
import com.immomo.wsconnector.session.WebClientSession;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;

public class TailHandler extends ChannelDuplexHandler{

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName()+" channel actived");
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(this.getClass().getSimpleName()+" channel inactived");
		Attribute<WebClientSession> session = ctx.channel().attr(WsConstants.ACTIVITY_SESSIONS);
		if(session!=null && session.get()!=null) {
			WebClientSessionManager.INSTANCE.unRegister(session.get().getMomoid());
		}
		super.channelInactive(ctx);
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent idleEvent = (IdleStateEvent)evt;
			switch (idleEvent.state()) {
			case ALL_IDLE:
				ctx.channel().close();
				System.out.println(this.getClass().getSimpleName()+" channel closed for all idle");
				break;
			case READER_IDLE:
				ctx.channel().close(); 
				System.out.println(this.getClass().getSimpleName()+" channel closed for read idle");
			case WRITER_IDLE:
				ctx.channel().close(); 
				System.out.println(this.getClass().getSimpleName()+" channel closed for write idle");
			default:
				break;
			}
		}
		//ctx.fireUserEventTriggered(evt);
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		//TODO FIXED ME
		if(msg instanceof String) {
			System.out.println("write to client:"+msg);
			msg = new TextWebSocketFrame((String)msg);
		}else {
			System.out.println("--error msg:" + msg+"--over!");
		}
		super.write(ctx, msg, promise);
		System.out.println("write to client finished:"+msg);
	}
	
}
