package com.MysqlLoadTest.Utilities;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class ConfigLoader {
	private static XMLConfiguration config;
	private static Logger log = LogManager.getLogger(ConfigLoader.class); 
	
	static{

			try {
				config = new XMLConfiguration("config.xml");
				config.setThrowExceptionOnMissing(true);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private static void setValue(Field field, Object o, String text){
		
		try {
			String item = config.getString(text);
			switch ( field.getType().getSimpleName()){
			case "int":
				field.set(o, Integer.parseInt(item));
				break;
			case "long":
				field.set(o, Long.parseLong(item));
				break;
			case "String":
				field.set(o, item);
				break;
			default:
				log.fatal("Error in type: " + field.getType().getSimpleName());
				System.exit(1);
			}
			
			
		} catch (IllegalArgumentException | IllegalAccessException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	};
	

	public static void loadFromConfig(Object o){
		try {
			Class<? extends Object> c = o.getClass();
			log.info("working on class: " + c.getName());
			for (Field field: c.getDeclaredFields()){
				log.info("Working on field: " + field.getName());
				field.setAccessible(true);
				if(field.isAnnotationPresent(LoadFromConfig.class)){
					log.info("Annotation find on field: " + field.getName());
					//field.getType();
					if (field.getType().isAssignableFrom(ConnectionInfo.class)){
						log.info("working on field with class: " + field.getName() + " " + ConnectionInfo.class.getName());
						ConnectionInfo connectionInfo = new ConnectionInfo();
	
						field.set(o, connectionInfo);
						for (Field subField: field.getType().getDeclaredFields()){
							subField.setAccessible(true);
							log.info("working on subField: " + subField.getName());
							setValue(subField,connectionInfo,c.getSimpleName() + "." + field.getName() + "." + subField.getName());
							//subField.set(connectionInfo, subField.getType().cast(config.getString(
							//		c.getSimpleName() + "." + field.getName() + "." + subField.getName())));
						}
	
					}else{
							setValue(field,o,c.getSimpleName() + "." + field.getName());
							//+field.set(o, field.getType().cast(config.getProperty(c.getSimpleName() + "." + field.getName())));
	
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException |NoSuchElementException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
	}
}
