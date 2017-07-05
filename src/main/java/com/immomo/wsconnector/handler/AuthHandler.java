package com.immomo.wsconnector.handler;

import com.immomo.wsconnecotor.protocol.GeneralRequest;
import com.immomo.wsconnector.session.WebClientSessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthHandler extends ChannelInboundHandlerAdapter{

	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof GeneralRequest) {
			GeneralRequest request = (GeneralRequest) msg;
			if(request.get_().equals("auth")){
				String momoId = request.getMomoId();
				String roomId = request.getTo();
				
				if(momoId == null) {
					ctx.channel().writeAndFlush("momoId is null");
					ctx.channel().close();
				}
				if(roomId == null) {
					ctx.channel().writeAndFlush("roomId is null");
					ctx.channel().close();
				}
				
				WebClientSessionManager.INSTANCE.register(momoId, roomId, ctx.channel());
				
				ctx.channel().writeAndFlush("auth Ok");
			}else {
				System.out.println(this.getClass().getSimpleName()+" skipped unmatch msg["+msg+"]");
				super.channelRead(ctx, msg);
			}
		}else {
			ctx.channel().writeAndFlush(this.getClass().getSimpleName()+" unkown msg["+msg+"]");
			ctx.channel().close();
		}
	}
}
