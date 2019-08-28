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
 * @version		: 1.3
 */
public class PServer extends Thread {
	
	private int pnum;								// 포트 번호
	private boolean isFirst;						// 플레이어 선플레이어 여부 결정
	private String pname;							// 유저 이름
	private int winNum;							// 이긴 횟수
	public byte[] result = new byte[9];		// 플레이 결과
	private Socket socket;						// 소켓
	private InputStream in;						// 소켓 InputStream
	private OutputStream out;				// 소켓 OutputStream
	public DataInputStream din;			// DataInputStream
	public DataOutputStream dou;		// DataOutputStream
//	private JSONObject jsonIn;				// 크로스랭귀지 구현을 위한 코드 - 삭제예정
//	private JSONObject jsonOu;			// 마찬가지
	
	// 상수 선언부
	
	public static final byte WIN = 1;			// 이김
	public static final byte DRAW = 0;		// 비김
	public static final byte LOSE = -1;		// 짐
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 메소드 선언부
	
	public PServer(int pnum, Socket socket) {
		this.pnum = pnum;
		this.socket = socket;
		this.winNum = 0;
	}
	
	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());				// 수신 스트림
			dou = new DataOutputStream(socket.getOutputStream()); 	// 발신 스트림
			
			pname = din.readUTF();																			// 플레이어 이름 수신
			System.out.println("Player " + pname +  " 연결 완료 - " + pnum + "번 플레이어");			// 연결완료 메세지
			
			// 어떠한 메세지(준비완료)를 받을 때까지 무한정 대기한다 (ver1.3)
			String isready = din.readUTF();
																																	
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
	public synchronized void saveResult(int round, byte num) {
		result[round-1] = num;
	}
	
	// round번째 값을 로드함
	public byte loadResult(int round) {
		return result[round-1];
	}
	
	// 플레이어로부터 카드 값을 받음
	public byte recvCard() throws IOException {
		return din.readByte();
	}
	
/*	public void sendCard() throws IOException {
//		
//		jsonOu = new JSONObject();
//				jsonOu.put("Card" , new Integer(num));
//		
//		dou.writeUTF(jsonOu.toJSONString());
//	}*/
	
	// 이 플레이어에게 타일의 색깔을 전송함 (홀수:하양 - true, 짝수: 검정 - false)
	public void sendColor(int num) throws IOException {
		
		dou.writeBoolean(num % 2 == 1);
		
	}
	
	// 이 플레이어에게 선플레이어 여부를 전송함 (선플레이어 : true, 후플레이어 : false)
	public void sendFirst(Boolean first) throws IOException {
		dou.writeBoolean(first);
	}
	
	// 이 플레이어에게 상대방의 닉네임을 전송함
	public void sendPName(String pname)throws IOException {
		dou.writeUTF(pname);
	}
	
	// 이 플레이어에게 라운드의 최종 결과를 전송함
	public void sendRoundResult(int result) throws IOException {
		dou.writeByte(result);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////// ver1.0 code
	
	// 이 플레이어에게 이겼다는 결과를 전송함
//	public void sendWin() throws IOException {
//		
//		jsonOu = new JSONObject();
//		
//		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(1));
//		
//		dou.writeUTF(jsonOu.toJSONString());
//	}
//	
//	// 이 플레이어에게 비겼다는 결과를 전송함
//	public void sendDraw() throws IOException {
//		
//		jsonOu = new JSONObject();
//		
//		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(0));
//		
//		dou.writeUTF(jsonOu.toJSONString());
//	}
//	
//	// 이 플레이어에게 비겼다는 결과를 전송함
//	public void sendLose() throws IOException {
//		
//		jsonOu = new JSONObject();
//		
//		jsonOu= (JSONObject) jsonOu.put("result" , new Integer(-1));
//		
//		dou.writeUTF(jsonOu.toJSONString());
//	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getPName() {
		return pname;
	}

	// 플레이어에게 결과를 보냄
	public void sendResult(PServer player, int finalRound) throws IOException {

		dou.write(result, 0, result.length);
			
	}

	// 모든 close가 필요한 것들을 close함
	public void close() throws IOException {
		in.close();
		out.close();
		din.close();
		dou.close();
		socket.close();
	}
}
