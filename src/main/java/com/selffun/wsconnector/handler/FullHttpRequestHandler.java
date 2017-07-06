package com.selffun.wsconnector.handler;

import com.selffun.wsconnector.constant.WsConstants;
import com.selffun.wsconnector.context.Context;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class FullHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

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
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		HttpMethod method=req.method();
	    String uri=req.uri();
	     
		if (!req.decoderResult().isSuccess()
				|| (!"websocket".equals(req.headers().get("Upgrade")))) {
			//dispathcer(method,uri);
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		
		 // Allow only GET methods.
        if (!HttpMethod.GET.equals(req.method())) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        String wssURL = String.format("%s://%s:%s/%s", Context.getWssConfig().getSchema(),Context.getWssConfig().getDomain(),Context.getWssConfig().getPort(),Context.getWssConfig().getPath());
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wssURL, null, false);
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}
		else {
			handshaker.handshake(ctx.channel(), req);
			//保存websocket握手上下文信息，以便关闭连接时使用
			ctx.channel().attr(WsConstants.HANDSHAKER_ATTR_KEY).set(handshaker);

		    // 握手成功之后,业务逻辑 注册
//            if (channelFuture.isSuccess()) {
//                if (client.getId() == 0) {
//                    System.out.println(ctx.channel() + " 游客");
//                    return;
//                }
//
//                loginClientMap.put(ctx.channel().id().asShortText(), client);
//            }
        }

	     
	}

	
	private void dispathcer(HttpMethod method,String uri) {
		
	     if(method==HttpMethod.GET&&"/login".equals(uri)){
	         //....处理
	     }else if(method==HttpMethod.POST&&"/register".equals(uri)){
	         //...处理
	     }
	     
	}
	
	private static void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!req.protocolVersion().isKeepAliveDefault() || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}
}
