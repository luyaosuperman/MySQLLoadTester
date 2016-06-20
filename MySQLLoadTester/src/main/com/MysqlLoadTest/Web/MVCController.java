package com.MysqlLoadTest.Web;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.MysqlLoadTest.SocketController.SocketSender;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Utilities.TestInfoClient;

@Controller
public class MVCController  extends WebMvcConfigurerAdapter {
	
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/get_progress").setViewName("get_progress");
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String landing( TestInfoClient testInfoClient) {
        return "landing";
    }
    
    @RequestMapping(value="/",method=RequestMethod.POST)
    public String start_test(@Valid TestInfoClient testInfoClient, BindingResult bindingResult){
    	
    	if (bindingResult.hasErrors()){
    		return "landing";
    	}
    	
    	//int testId = SocketSender.sendFromSocket(testInfoClient);
    	WebBridge.testInfo = new  TestInfo(testInfoClient);
    	WebBridge.controller.startTest(WebBridge.testInfo);
    	
    	return "test_progress";
    	
    	//return "redirect:/get_graph?testId="+testId;
    	
    }
    
    @RequestMapping(value="/test_progress", method=RequestMethod.GET)
    public String test_prepare(){
    	
    	return "test_progress";
    }
    
    
    
    @RequestMapping(value="/get_graph", method=RequestMethod.GET)
    public String getGraph(@RequestParam(value="testId", required=true) int[] testIdArray, Model model){
    	
    	for (int testId: testIdArray){
    		System.out.println("testId: " + testId);
    	}

    	return "get_graph";
    }
    
    @RequestMapping(value="/existing_tests", method=RequestMethod.GET)
    public String getExistingTests(Model model){
    	ArrayList<Integer> testIdList = new ArrayList<Integer>();
    	testIdList.add(1);
    	testIdList.add(2);
    	testIdList.add(3);
    	
    	model.addAttribute("testIdList", testIdList);
    	
    	return "existing_tests";
    }
    
}