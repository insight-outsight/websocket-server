package com.selffun.wsconnector.session;

import io.netty.channel.Channel;

public class WebClientSession {

	private Channel channel;
	
	private String momoid;
	
	private String roomId;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getMomoid() {
		return momoid;
	}

	public void setMomoid(String momoid) {
		this.momoid = momoid;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
}
