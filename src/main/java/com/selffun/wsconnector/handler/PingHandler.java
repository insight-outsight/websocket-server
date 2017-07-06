package com.selffun.wsconnector.handler;

import com.selffun.wsconnector.protocol.GeneralRequest;
import com.selffun.wsconnector.session.WebClientSessionManager;
import com.selffun.wsconnector.util.JacksonJSONUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PingHandler extends ChannelInboundHandlerAdapter{

	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof GeneralRequest) {
			GeneralRequest request = (GeneralRequest) msg;
			if(request.get_().equals("ping")){
				ctx.channel().writeAndFlush(JacksonJSONUtils.toJSON(new GeneralRequest(request.getBt(),"pong")));
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
