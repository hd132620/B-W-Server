/**
 * 
 */
package org.LEC.BWServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <pre>
 * org.LEC.BWServer
 *	 	|_ test
 * 
 * 1. 개요 : 
 * 2. 작성일 : 2015. 8. 27.
 * </pre>
 * 
 * @author		: 이은찬
 * @version		: 1.0
 */
public class test {
	
	public static boolean coinToss() {
		return ((int)(Math.random() * 1000 % 2)) == 1;
	}

	public static void main(String[] args) throws IOException {
		
		ServerSocket serversocket = new ServerSocket(8080);
		Socket socket = serversocket.accept(); 
		
		InputStream in = socket.getInputStream();
		DataInputStream din = new DataInputStream(in);
		OutputStream out = socket.getOutputStream(); 
		DataOutputStream dou = new DataOutputStream(out); 
		
		String name = din.readUTF();
		System.out.println("Player " + " : " + name + " 연결 완료");
		
		while(din.readBoolean() != true);
		
		System.out.println("Player " + " : " + name + " 준비 완료");
	}

}
