package com.immomo.wsconnector.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RoomSessionManager {

	public static RoomSessionManager INSTANCE = new RoomSessionManager();

	private ConcurrentHashMap<String, RoomSession> roomSessionMap = new ConcurrentHashMap<String, RoomSession>();
	
	public RoomSessionManager() {
		super();
	}

	public void register(String momoid,String roomid,Channel channel) {
		RoomSession session = getRoomSessionByRoomId(roomid);
		session.getGroup().add(channel);
	}
	
	public void unRegister(String momoid,String roomid,Channel channel) {
		RoomSession session = getRoomSessionByRoomId(roomid);
		session.getGroup().remove(channel);
	}

	public void pubMsg(String roomId,String msg) {
		RoomSession session = getRoomSessionByRoomId(roomId);
		session.getGroup().writeAndFlush(msg);
	}     
	
	private RoomSession getRoomSessionByRoomId(String roomid) {
		RoomSession session = roomSessionMap.get(roomid);
		if(session == null) {
			roomSessionMap.putIfAbsent(roomid,new RoomSession(roomid));
			session = roomSessionMap.get(roomid);
		}
		return session;
	}
}
