package com.selffun.wsconnector.session;



import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RoomSession {

	private String roomId;
	
	private ChannelGroup group;
	
	public RoomSession(String roomID) {
		group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}


	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public ChannelGroup getGroup() {
		return group;
	}

	public void setGroup(ChannelGroup group) {
		this.group = group;
	}
	
}
