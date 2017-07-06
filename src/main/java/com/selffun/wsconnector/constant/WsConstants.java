package com.selffun.wsconnector.constant;

import com.selffun.wsconnector.session.WebClientSession;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.AttributeKey;

public class WsConstants {

	  public static final AttributeKey<WebClientSession> ACTIVITY_SESSIONS = AttributeKey.newInstance("sessions_key");
	  public static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY =
	            AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
}
