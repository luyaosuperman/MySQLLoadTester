package com.MysqlLoadTest.SocketController;

import java.io.BufferedReader;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;  
import java.net.Socket;  



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.ExecutionUnit.TestController;
import com.MysqlLoadTest.ExecutionUnit.Runner;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Utilities.TestInfoClient;

public class SocketReceiver implements Runnable{
	
	private static final int PORT = 12345;
	private static Logger log = LogManager.getLogger(SocketReceiver.class); 
	
	private ServerSocket serverSocket ;
	
    public static void main(String[] args) {    
        log.info("SocketReceiver Started");    
        Thread t = new Thread(new SocketReceiver());    
        //socketReceiver.init();    
        t.start();
    }    
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
        try {    
            this.serverSocket = new ServerSocket(PORT);    
            while (!Thread.currentThread().isInterrupted()) {    
                Socket client = serverSocket.accept();    
                new HandlerThread(client);   
                
                Thread.sleep(1000);
                
            }   
            System.out.println("ScoketReceiver stopping");
        } catch (Exception e) {    
            log.debug("Server crashed: " + e.getMessage());    
        }    
		
	}   
    
    /*public void init() {    
        try {    
            ServerSocket serverSocket = new ServerSocket(PORT);    
            while (true) {    
                Socket client = serverSocket.accept();    
                new HandlerThread(client);   
                
                Thread.sleep(1000);
            }    
        } catch (Exception e) {    
            log.debug("Server crashed: " + e.getMessage());    
        }    
    }   */
    
    private class HandlerThread implements Runnable {    
        private Socket socket;    
        public HandlerThread(Socket client) {    
            socket = client;    
            new Thread(this).start();    
        }    
    
        public void run() {    
            try {    

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                TestInfoClient testInfoClient = (TestInfoClient) input.readObject();
                log.info("recevied testInfo with table name: " + testInfoClient.getTableName());
                
                TestInfo testInfo = new TestInfo(testInfoClient);
        		TestController controller = new TestController();
        		//controller.start();
        		
        		controller.startTest(testInfo);
        		while (controller.testStatus() != TestController.NOTRUNNING){
        			log.info("Waiting: " +
        					"testId: " + testInfo.getTestId() +
        					" status: " + testInfo.testStatus +
        					" progress: " + testInfo.getTestProgress()
        					);
        			try {
        				Thread.sleep(10000);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		log.info("test seems to be finished");
    
                int returnValue = testInfo.getTestId();
                //out.writeUTF(s);   
                log.info("sending test ID: " + returnValue);
                out.writeObject(returnValue);
                  
                out.close();    
                input.close();    
            } catch (Exception e) {    
                System.out.println("Server error" + e.getMessage());    
            } finally {    
                if (socket != null) {    
                    try {    
                        socket.close();    
                    } catch (Exception e) {    
                        socket = null;    
                        System.out.println("Server error in \"finally\" block:" + e.getMessage());    
                    }    
                }    
            }   
        }    
    }





}
