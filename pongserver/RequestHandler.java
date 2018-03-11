package pongserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.Timer;

public class RequestHandler extends Thread implements ActionListener{

	Socket player;
	PrintWriter output;
	InputStreamReader isr;
	BufferedReader input;
	
	PrintWriter out = null;
	
	Timer timer;
	
	Server server;
	
	String playerRequest;
	String regex = "<:&!%#}>";
	int playerNumber;
	
	final int TIMER_DELAY = 20;
	
	final int PADDLE_WIDTH = 120;
	final int WINDOW_WIDTH = 500;
	
	public RequestHandler(Socket player, Server server, int playerNumber) {
		this.player = player;
		this.server = server;
		this.playerNumber = playerNumber;
		timer = new Timer(TIMER_DELAY, this);
		timer.start();
	}

	@Override
	public void run() {
		try {
			isr = new InputStreamReader(player.getInputStream());
			input = new BufferedReader(isr);
			output = new PrintWriter(player.getOutputStream());
			
			//tell player its number on connection
			output.println(playerNumber);
			output.flush();
			
			//listens to players request and services them here
			while(true){
				if(input.ready()){
					playerRequest = input.readLine();
					System.out.println(playerRequest);
					
					//exclusive to player 2
					if(playerRequest.startsWith("start")){
						if(server.players.size() == 2 && server.player2Ready){
							String[] temp = playerRequest.split(regex);
							server.gamePoint = Integer.parseInt(temp[1]);
							System.out.println("Game Point: "+ server.gamePoint);
							output.println("ok");
							output.flush();
							writeToOtherPlayer("start" + regex + server.gamePoint);
							server.player1Score = 0;
							server.player2Score = 0;
							ServerWindow.player1Point.setText("Point: "+ server.player1Score);
							ServerWindow.player2Point.setText("Point: "+ server.player2Score);
							server.winner = 0;
							server.gameState = 1;
							
							new CommonBall(server);
							
							ServerWindow.gameStatus.setText("Game Status: Playing | Game Point: "+ server.gamePoint);
						}else{
							output.println("wait");
							output.flush();
						}
					}
					//exclusive to player 2
					else if(playerRequest.startsWith("ready")){
						server.player2Ready = true;
						writeToOtherPlayer("ready");
					}
					else if(playerRequest.startsWith("paddle_pos")){
						String[] temp = playerRequest.split(regex);
						int paddlePos = Integer.parseInt(temp[1]);
						int paddlePosInOpp = WINDOW_WIDTH - (PADDLE_WIDTH + paddlePos);
						if(playerNumber == 1){
							server.player1PaddleXPosIn1 = paddlePos;
							server.player1PaddleXPosIn2 = paddlePosInOpp;
							System.out.println("1");
						}else if(playerNumber == 2){
							server.player2PaddleXPosIn1 = paddlePosInOpp;
							server.player2PaddleXPosIn2 = paddlePos;
							System.out.println("2");
						}	
					}
					else if(playerRequest.startsWith("pause")){
						writeToOtherPlayer("pause");
						server.gameState = 2;
						ServerWindow.gameStatus.setText("Game Status: Paused | Game Point: "+ server.gamePoint);
					}
					else if(playerRequest.startsWith("resume")){
						writeToOtherPlayer("resume");
						server.gameState = 1;
						ServerWindow.gameStatus.setText("Game Status: Playing | Game Point: "+ server.gamePoint);
					}
					else if(playerRequest.startsWith("endACK")){
						server.gameState = 0;
					}
						
					else if(playerRequest.startsWith("disconnect")){
						if(server.players.size() == 2){
						writeToOtherPlayer("stopped");
						}
						
						server.stopServer();
						server.startServer();
						
						break;
					}
				}
			} 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return;
	}
	
	public void writeToOtherPlayer(String message){
		try {
			if(playerNumber == 1){
				out = new PrintWriter(server.players.get(1).getOutputStream());
			}else if(playerNumber == 2){
				out = new PrintWriter(server.players.get(0).getOutputStream());
			}
			out.println(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0){
		if(server.gameState == 1 && server.players.size() == 2){
			if(playerNumber == 1){
				writeToOtherPlayer("ballPos"+regex+server.ballXPosIn2+regex+
						server.ballYPosIn2+regex+
						server.player1PaddleXPosIn2+regex+
						server.player1Score+regex+
						server.player2Score+regex+
						server.winner);
			}else if(playerNumber == 2){
				writeToOtherPlayer("ballPos"+regex+server.ballXPosIn1+regex+
						server.ballYPosIn1+regex+
						server.player2PaddleXPosIn1+regex+
						server.player1Score+regex+
						server.player2Score+regex+
						server.winner);
			}	
		}
	}
}
