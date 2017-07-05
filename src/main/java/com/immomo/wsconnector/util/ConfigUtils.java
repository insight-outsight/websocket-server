package com.immomo.wsconnector.util;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Iterator;


public class ConfigUtils {

	private final static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

	public static PropertiesConfiguration loadConfiguration(String fileName){
		try {
			URL url = ConfigUtils.class.getResource(fileName);
			String configFilePath = url.getFile();
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.setEncoding("UTF-8");  
			config.setFileName(configFilePath);  
			config.load();
			Iterator<String> keys = config.getKeys();
			logger.info("============loaded configuration file [{}]============",url.getPath());
			while(keys.hasNext())     {
				String key = keys.next();
				logger.info(key+"="+config.getString(key));
			}
			return config;
		}
		catch (Exception e) {
			throw new RuntimeException("read configuration file [" + fileName +"] Error", e);
		}
	}

	public static void main(String[] args) {
		try {
			String fileName = "/properties/zk-config.properties";

			PropertiesConfiguration config = loadConfiguration(fileName);
			System.out.println(config.getString("connectionStr"));
			System.out.println(config.getInt("connectionTimeoutMs"));

			System.out.println("--------------------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	

}
