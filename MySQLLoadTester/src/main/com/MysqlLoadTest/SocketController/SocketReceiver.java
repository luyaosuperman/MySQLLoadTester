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

import com.MysqlLoadTest.ExecutionUnit.Controller;
import com.MysqlLoadTest.ExecutionUnit.Runner;
import com.MysqlLoadTest.Utilities.TestInfo;

public class SocketReceiver {
	
	private static final int PORT = 12345;
	private static Logger log = LogManager.getLogger(SocketReceiver.class); 
	
    public static void main(String[] args) {    
        log.info("SocketReceiver Started");    
        SocketReceiver socketReceiver = new SocketReceiver();    
        socketReceiver.init();    
    }    
    
    public void init() {    
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
    }   
    
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
                TestInfo testInfo = (TestInfo) input.readObject();
                log.info("recevied testInfo with table name: " + testInfo.getTableName());
                
                //Controller.runTest(testInfo);
    
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
