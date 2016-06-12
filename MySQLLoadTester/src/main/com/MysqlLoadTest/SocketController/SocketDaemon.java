package com.MysqlLoadTest.SocketController;

import org.apache.commons.daemon.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * Launch the Engine from a variety of sources, either through a main() or invoked through
 * Apache Daemon.
 */
public class SocketDaemon implements Daemon {
    //private static final Log4J log = Log4J.getLog();
    //private static Logger log = LogManager.getLogger(SocketDaemon.class);
	private static Thread t; 
	
	
    public static void main(String[] args){
    	String mode = args[0];
        //socketReceiver.init();
    	switch (mode){
    	case "start":
	    	SocketDaemon.t = new Thread(new SocketReceiver());
	    	SocketDaemon.t.start();
	        System.out.println("SocketReceiver Daemon Started");
	        break;
    	case "stop":
    		System.exit(0);
    		t.interrupt();
    		System.out.println("SocketReceiver Daemon Stopped");
    		break;
    	
    	}
    }
    
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("destroy daemon");
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		// TODO Auto-generated method stub
		System.out.println("init daemon");
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("start daemon");
        main(null);
        
        Thread.sleep(2000);
        this.stop();
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("stop daemon");
		SocketDaemon.t.interrupt();
		
	} 

    
    
}