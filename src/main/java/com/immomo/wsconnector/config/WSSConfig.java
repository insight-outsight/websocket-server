package com.immomo.wsconnector.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WSSConfig {

	private final  Logger logger = LoggerFactory.getLogger(WSSConfig.class);

	private final  PropertiesConfiguration config;

	public WSSConfig(PropertiesConfiguration config){
		this.config = config;
	}

	public  String getDomain(){
		return config.getString("domain");
	}
	
	public  String getPort(){
		return config.getString("port");
	}
	
	public  String getSchema(){
		return config.getString("schema");
	}
	
	public  String getPath(){
		return config.getString("path");
	}

	

}
