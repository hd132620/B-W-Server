package org.LEC.BWServer;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * <pre>
 * org.LEC.BWServer
 *	 	|_ MainServer
 * 
 * 1. 개요 : 흑과 백 메인서버이다.
 * 2. 작성일 : 2015. 8. 25.
 * 3. 수정일 : 2015. 11. 21
 * </pre>
 * 
 * @author		: 이은찬
 * @version		: 1.3
 */
public class MainServer {
	
	public static final int WIN = 1;			// 이김
	public static final int DRAW = 0;		// 비김
	public static final int LOSE = -1;		// 짐
	
	public static boolean coinToss() {	// 코인을 던져 true와 false 둘 중 하나의 값이나오게 한다.
		return ((int)(Math.random() * 1000 % 2)) == 1;
	}

	public static void main(String[] args) throws IOException {
		
		ServerSocket serversocket = new ServerSocket(7700); // 서버소켓 기동
		
		// 차례대로 들어온 두명에 대한 PServer 객체를 만든다.
		PServer p1 = new PServer(1, serversocket.accept());	
		PServer p2 = new PServer(2, serversocket.accept());	
		
		// 레디할 때까지 (준비대기)쓰레드를 작동시킨다.
		p1.start();
		p2.start();	
		
		// 메인쓰레드에서는 동전던지기를 해서 선플레이어를 결정한다
		Boolean forl = coinToss();
		int i; // 라운드 넘버 셋팅
		
		try {
			p1.join();
			p2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 준비완료
		
		// 플레이어 이름 교환
		p1.sendPName(p2.getPName());
		p2.sendPName(p1.getPName());
		
		// 게임시작
		System.out.println("게임 시작");
		
		// 선/후플레이어 결과를 전송한다.
		p1.sendFirst(forl);
		p1.setFirst(forl);
		
		p2.sendFirst(!forl);
		p2.setFirst(!forl);
		System.out.println("선플레이어 결정완료");
		
		for(i = 1; i<=9; i++) { // 총 9라운드 진행, i는 라운드 넘버
			
			// 실제 구현부에서는 p1의 선플레이어 여부만 확인해서 선후순서를 알아낸다.
			// 라운드 시작
			System.out.println(i + "라운드 시작");
			
			if(p1.isFirst()) {			// p1선 
				
				p1.saveResult(i, p1.recvCard());		// p1으로부터 카드를 받아서 저장한다.
				p2.sendColor(p1.loadResult(i));		// p2에게 p1카드의 색깔을 전송한다
				p2.saveResult(i, p2.recvCard());		// p2으로부터 카드를 받아서 저장한다.
				p1.sendColor(p2.loadResult(i));
				
				if(p1.loadResult(i) > p2.loadResult(i)) {				// p1이 이김
					// p1.sendWin();
					// p2.sendLose();
					p1.sendRoundResult(WIN);
					p2.sendRoundResult(LOSE);
					p1.upWinNum();						// p1의 스코어를 1 올린다
																			// 현재p1선
				}
				else if(p1.loadResult(i) < p2.loadResult(i)) {		// p2가 이김
					// p1.sendLose();
					// p2.sendWin();
					p1.sendRoundResult(LOSE);
					p2.sendRoundResult(WIN);
					p2.upWinNum();						// p2의 스코어를 1 올린다
					p1.setFirst(false);						// (현재p1선) -> p1을 후플레이어로
					p2.setFirst(true);							// (현재p1선) -> p2를 선플레이어로
				}
				else {																				// 비김
					// p1.sendDraw();
					// p2.sendDraw();
					p1.sendRoundResult(DRAW);
					p2.sendRoundResult(DRAW);
				}
				
			} ///////////////////////////////////////////////////////////////////////////////////////////////
			
			else {							// p2 선
				
				p2.saveResult(i, p2.recvCard());		// p2으로부터 카드를 받아서 저장한다.
				p1.sendColor(p2.loadResult(i));		// p1에게 p2카드의 색깔을 전송한다
				p1.saveResult(i, p1.recvCard());		// p1으로부터 카드를 받아서 저장한다.
				p2.sendColor(p1.loadResult(i));
				
				if(p1.loadResult(i) > p2.loadResult(i)) {				// p1이 이김
					// p1.sendWin();
					// p2.sendLose();
					p1.sendRoundResult(WIN);
					p2.sendRoundResult(LOSE);
					p1.upWinNum();
					p1.setFirst(true);
					p2.setFirst(false);
				}
				else if(p1.loadResult(i) < p2.loadResult(i)) {
					// p1.sendLose();
					// p2.sendWin();
					p1.sendRoundResult(LOSE);
					p2.sendRoundResult(WIN);
					p2.upWinNum();
				}
				else {
					// p1.sendDraw();
					// p2.sendDraw();
					p1.sendRoundResult(DRAW);
					p2.sendRoundResult(DRAW);
				}
				
			}
			//if(i == 9) {
			//	
			//}
		}
		
		p1.sendResult(p1, i);
		p1.sendResult(p2, i);
		p2.sendResult(p2, i);
		p2.sendResult(p1, i);
		
		if(p1.getWinNum() > p2.getWinNum()) {
			// p1.sendWin();
			// p2.sendLose();
			p1.sendRoundResult(WIN);
			p2.sendRoundResult(LOSE);
		}
		else if(p1.getWinNum() < p2.getWinNum()){
			// p1.sendLose();
			// p2.sendWin();
			p1.sendRoundResult(LOSE);
			p2.sendRoundResult(WIN);
		}
		else {
			// p1.sendDraw();
			// p2.sendDraw();
			p1.sendRoundResult(DRAW);
			p2.sendRoundResult(DRAW);
		}
		
		p1.close();
		p2.close();
		serversocket.close();
		  
	}

}
