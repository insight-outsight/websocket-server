package com.selffun.wsconnector.handler;


import com.selffun.wsconnector.protocol.GeneralRequest;
import com.selffun.wsconnector.session.RoomSessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MsgHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if(msg instanceof GeneralRequest) {
			GeneralRequest request = (GeneralRequest) msg;
			if(request.get_().equals("msg")){
				String roomId = request.getTo();
				if(roomId == null) {
					ctx.channel().writeAndFlush("roomId is null");
					ctx.channel().close();
				}
				RoomSessionManager.INSTANCE.pubMsg(roomId, request.getText());
				
				ctx.channel().writeAndFlush("msg Ok");
			}else {
				System.out.println(this.getClass().getSimpleName()+" skipped unmatch msg["+msg+"]");
				super.channelRead(ctx, msg);
			}
		}else {
			ctx.channel().writeAndFlush(this.getClass().getSimpleName()+" unkown msg format["+msg+"]");
			ctx.channel().close();
		}
		
	}
}
