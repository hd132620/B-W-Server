/**
 * 
 */
package org.LEC.BWServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * <pre>
 * org.LEC.BWServer
 *	 	|_ PServer
 * 
 * 1. 개요 : MainServer에서 주요하게 컨트롤하는 플레이어 객체이다.
 * 2. 작성일 : 2015. 8. 25.
 * </pre>
 * 
 * @author		: 이은찬
 * @version		: 1.2
 */
public class PServer_Json extends Thread {
	
	private int pnum;								// 포트 번호
	private boolean isFirst;						// 플레이어 선플레이어 여부 결정
	private String name;							// 유저 이름
	private int winNum;							// 이긴 횟수
	public int[] result = new int[9];		// 플레이 결과
	private Socket socket;						// 소켓
	private InputStream in;						// 소켓 InputStream
	private OutputStream out;				// 소켓 OutputStream
	public DataInputStream din;			// DataInputStream
	public DataOutputStream dou;		// DataOutputStream
	private JSONObject jsonIn;				// 크로스랭귀지 구현을 위한 코드 - 삭제예정
	private JSONObject jsonOu;			// 마찬가지
	
	// 상수 선언부
	
	public static final int WIN = 1;			// 이김
	public static final int DRAW = 0;		// 비김
	public static final int LOSE = -1;		// 짐
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 메소드 선언부
	
	public PServer_Json(int pnum, Socket socket) {
		this.pnum = pnum;
		this.socket = socket;
		this.winNum = 0;
	}
	
	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());				// 수신 스트림
			dou = new DataOutputStream(socket.getOutputStream()); 	// 발신 스트림
			
			//name = din.readUTF();
			System.out.println("Player " + pnum + " : " +  " 연결 완료");			// 연결완료 메세지
			
			//while(din.readUTF().equals("READY") != true);
			
			jsonIn = (JSONObject) JSONValue.parse(din.readUTF());
			
			String  isready = (String) jsonIn.get("READY");
			
			System.out.println("Player " + pnum + " is " + isready); 				// 준비완료 메세지 (서버 콘솔창)
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	// 선 플레이어 getter
	public boolean isFirst() {
		return isFirst;
	}

	// 선 플레이어 setter
	public synchronized void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
	// 이긴 횟수 getter
	public int getWinNum() {
		return winNum;
	}
	
	// 이긴 횟수를 1 올려줌
	public synchronized void upWinNum() {
		winNum += 1;
	}
	
	// round번째의 결과에 num을 저장함
	public synchronized void saveResult(int round, int num) {
		result[round-1] = num;
	}
	
	// round번째 값을 로드함
	public int loadResult(int round) {
		return result[round-1];
	}
	
	// 서버로부터 카드 값을 받음
	public  int recvCard() throws IOException {
		
		jsonIn = (JSONObject) JSONValue.parse(din.readUTF());
		
		Integer isready = (Integer) jsonIn.get("Tile");
		
		return isready;
	}
	
/*	public void sendCard() throws IOException {
//		
//		jsonOu = new JSONObject();
//				jsonOu.put("Card" , new Integer(num));
//		
//		dou.writeUTF(jsonOu.toJSONString());
//	}*/
	
	// 이 플레이어에게 타일의 색깔을 전송함 (짝수: 검정, 홀수:하양)
	public void sendColor(int num) throws IOException {
		
		jsonOu = new JSONObject();
		
		jsonOu= (JSONObject) jsonOu.put("Color", new Boolean(num % 2 == 1));
		
		dou.writeUTF(jsonOu.toJSONString());
	}
	
	// 이 플레이어에게 선플레이어 여부를 전송함 (선플레이어 : true, 후플레이어 : false)
	public void sendFirst(Boolean first) throws IOException {
		
		jsonOu = new JSONObject();
		
		jsonOu= (JSONObject) jsonOu.put("isFirst", new Boolean(first));
		
		dou.writeUTF(jsonOu.toJSONString());
	}
	
	// 이 플레이어에게 라운드의 최종 결과를 전송함
	public void sendRoundResult(int result) throws IOException {
		dou.writeInt(result);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////// ver1.0 code
	
	// 이 플레이어에게 이겼다는 결과를 전송함
	public void sendWin() throws IOException {
		
		jsonOu = new JSONObject();
		
		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(1));
		
		dou.writeUTF(jsonOu.toJSONString());
	}
	
	// 이 플레이어에게 비겼다는 결과를 전송함
	public void sendDraw() throws IOException {
		
		jsonOu = new JSONObject();
		
		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(0));
		
		dou.writeUTF(jsonOu.toJSONString());
	}
	
	// 이 플레이어에게 비겼다는 결과를 전송함
	public void sendLose() throws IOException {
		
		jsonOu = new JSONObject();
		
		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(-1));
		
		dou.writeUTF(jsonOu.toJSONString());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 플레이어에게 결과를 보냄
	public void sendResult(PServer_Json player, int finalRound) throws IOException {
		
		jsonOu = new JSONObject();
		
		for(int i = 1; i<=finalRound; i++) 
			jsonOu= (JSONObject) jsonOu.put("FinalResult" + i , new Integer(player.loadResult(i)));

		
		this.dou.writeUTF(jsonOu.toJSONString());
			
	}

	// 모든 close가 필요한 것들을 close함
	public void close() throws IOException {
		din.close();
		dou.close();
		in.close();
		out.close();
		socket.close();
	}
}
