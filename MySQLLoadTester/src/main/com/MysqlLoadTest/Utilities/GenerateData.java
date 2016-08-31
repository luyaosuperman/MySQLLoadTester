package com.MysqlLoadTest.Utilities;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.util.StringUtils;

public class GenerateData {
	
	private static Logger log = LogManager.getLogger(GenerateData.class); 
	
	public static int intSeed = 0;
	public static char charSeed =(char) 65;
	
	public static void generateData(Object o){
		for (Field f: o.getClass().getDeclaredFields()){
			if(f.getAnnotation(GeneratedData.class) != null){
				log.info("Generating for " + o.getClass().getSimpleName()+"."+f.getName());
				f.setAccessible(true);
				GeneratedData generatedData = f.getAnnotation(GeneratedData.class);
				switch (f.getType().getSimpleName()){
				case "int":
					try {
						f.set(o, intSeed);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intSeed++;
					intSeed%=65536;//0-65535
					break;
				case "String":
					try {
						f.set(o,StringUtils.repeat(charSeed, generatedData.stringLength()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					charSeed += 1;
					if (charSeed > 90) {charSeed = 65;}
					break;
				default:
					log.fatal("unrecognized variable type " + f.getType().getSimpleName());
					System.exit(1);
						
				}
					
			}
		}
	}
}
