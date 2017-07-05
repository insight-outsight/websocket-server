package com.immomo.wsconnector.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

import com.immomo.wsconnector.constant.WsConstants;

public class WebClientSessionManager {
	
	public static WebClientSessionManager INSTANCE = new WebClientSessionManager();

	private ConcurrentHashMap<String, WebClientSession> webClientSessionMap = new ConcurrentHashMap<String, WebClientSession>();
	
	private WebClientSessionManager(){
		
	}
	
	public void register(String momoid,String roomid,Channel channel) {
		unRegister(momoid);
		WebClientSession session = new WebClientSession();
		session.setChannel(channel);
		session.setMomoid(momoid);
		session.setRoomId(roomid);
		if(roomid != null) {
			RoomSessionManager.INSTANCE.register(momoid, roomid, channel);
		}
		webClientSessionMap.put(momoid, session);
		channel.attr(WsConstants.ACTIVITY_SESSIONS).set(session);
	}
	
	public void unRegister(String momoid) {
		WebClientSession oldSession = webClientSessionMap.remove(momoid);
		if(oldSession !=null && oldSession.getChannel()!=null) {
			oldSession.getChannel().close();
			if(oldSession.getRoomId()!=null) {
				RoomSessionManager.INSTANCE.unRegister(momoid, oldSession.getRoomId(), oldSession.getChannel());
			}
		}
	}
	
	public WebClientSession getSession(String momoid) {
		return webClientSessionMap.get(momoid);
	}
}
