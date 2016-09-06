package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestController;

public class HTester {
	
	private static Logger log = LogManager.getLogger(HTester.class); 
	
	

	public static void main(String[] args){
		
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
		    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.MysqlLoadTest.ExecutionUnit.HibernateVersion"))));
		
		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		
		log.info("allClasses.size(): " +allClasses.size());
		for(Class<?> c: allClasses){
				log.info(c.isAssignableFrom(com.MysqlLoadTest.Interfaces.TestController.class) + " " + c.getName());
		}
		
		Set<String> classes= reflections.getAllTypes();
		classes.forEach(s -> log.info(s));
		
		/*
		
		TestController testController = null;
		try {
			testController = (TestController) Class.forName("com.MysqlLoadTest.ExecutionUnit.HibernateVersion.HTestController").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestConfig testConfig = testController.getConfigObject();
		Map<String,Object> configList = testConfig.getConfigItems();
		for(String key: configList.keySet()){
			//log.info("key: " + key);
			log.info("got "+ key + " "+configList.get(key));
		}
		
		testConfig.setConfigItems(configList);
		
		
		testController.startTest();
		
		*/
		
	}
	
}
