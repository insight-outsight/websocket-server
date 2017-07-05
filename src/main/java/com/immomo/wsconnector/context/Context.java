package com.immomo.wsconnector.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.immomo.wsconnector.config.WSSConfig;


public class Context {

	private final static Logger logger = LoggerFactory.getLogger(Context.class);
	private static WSSConfig wssConfig = null;

	public static void setWSSConfig(WSSConfig wssConfig0) {
		wssConfig = wssConfig0;
	}

	public static WSSConfig getWssConfig() {
		return wssConfig;
	}
	
}
