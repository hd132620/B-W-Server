/**
 * 
 */
package org.LEC.BWTestClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
/**
 * <pre>
 * org.LEC.BWTestClient
 *	 	|_ Client
 * 
 * 1. 개요 : 
 * 2. 작성일 : 2015. 8. 27.
 * </pre>
 * 
 * @author		: 이은찬
 * @version		: 1.0
 */
public class Client {

	public static void main(String[] args) throws IOException {
		
		int port;
		String name;
		
		Scanner scan = new Scanner(System.in);
		System.out.print("연결할 포트 번호 입력 : ");
		port = scan.nextInt();
		System.out.print("닉네임 입력 : ");
		name = scan.nextLine();
		
		  Socket socket = new Socket("127.0.0.1", port); 
		  System.out.println("client: 소켓 생성 완료");
		  
		  int winNum = 0;
		  int result;
		  Boolean isWhite;
		  int i;
		  int[] enemyDeck = new int[9];
		  int[] myDeck = new int[9];
		  
		  OutputStream out = socket.getOutputStream(); 
		  DataOutputStream dou = new DataOutputStream(out); 
		  InputStream in = socket.getInputStream();
		  DataInputStream din = new DataInputStream(in);
		  
		  dou.writeUTF(name);
		  
		  dou.writeUTF("READY");
		  System.out.println("상대방의 닉네임 : " + din.readUTF());
		  
		  Boolean isFirst = din.readBoolean();
		  System.out.println("당신은 " + (isFirst == true ? "선플레이어" : "후플레이어") + "입니다.");
		  
		  for(i = 1; i<=9; i++) {
			  if(isFirst) { // 내가 먼저일경우 타일 1 제시, get result
				  dou.writeInt(scan.nextInt());
				  result = din.readInt();
				  
				  if(result > 0) {
					  winNum++;
					  System.out.println("이기셨습니다.");
				  }
				  else if(result == 0)
					  System.out.println("무승부입니다.");
				  else {
					  isFirst = false;
					  System.out.println("지셨습니다.");
				  }
					  
			  }
			  else {
				  isWhite = din.readBoolean();
				  if(isWhite)
					  System.out.println("상대방이 제시한 것은 하양입니다.");
				  else
					  System.out.println("상대방이 제시한 것은 검정입니다.");
				  
				  dou.writeInt(scan.nextInt());
				  result = din.readInt();
				  
				  if(result > 0) {
					  isFirst = true;
					  winNum++;
					  System.out.println("이기셨습니다.");
				  }
				  else if(result == 0)
					  System.out.println("무승부입니다.");
				  else 
					  System.out.println("지셨습니다.");
			  }
			  
			  if(winNum >= 5)
				  break;
			  
		  }
		  
		  System.out.println("나의 패 :");
		  for(int j=1; j< i; j++) {
			  myDeck[j-1] = din.readInt();
			  System.out.print(" " + myDeck[j-1] );
		  }
		  
		  System.out.println("상대방의 패 :");
		  for(int j=1; j< i; j++) {
			  enemyDeck[j-1] = din.readInt();
			  System.out.print(" " + myDeck[j-1] );
		  }
		  
		  if(din.readInt() == 1)
			  System.out.println("최종으로 이기셨습니다!");
		  else if(din.readInt() == -1)
			  System.out.println("최종으로 패배");
		  else
			  System.out.println("무승부입니다.");
		  
		  	din.close();
			dou.close();
			in.close();
			out.close();
			socket.close();
		   scan.close();
	}
	
}

