package pongserver;

import java.awt.Rectangle;
import java.util.Random;

public class CommonBall implements Runnable{
	Server server;
	Random random;
	
	final int WINDOW_WIDTH = 500;
	final int WINDOW_HEIGHT = 500;
	final int GAME_PANEL_BOTTOM_WALL = 490;
	final int GAME_PANEL_TOP_WALL = 55;
	final int GAME_PANEL_HEIGHT = 435;
	final int PADDLE_WIDTH = 120;
	final int PADDLE_HEIGHT = 20;
	final int BALL_SPEED = 7;
	final int BALL_DIAMETER = 20;
	
	final int TIMER_DELAY = 20;
	
	Rectangle player1Rect;
	Rectangle player2Rect;
	Rectangle ballRect;
	
	int tempHitCounts;
	
	String regex = "<:&!%#}>";
	
	Thread ballThread;
	
	public CommonBall(Server server){
		this.server = server;
		random = new Random();
		player1Rect = new Rectangle();
		player2Rect = new Rectangle();
		ballRect =  new Rectangle();
		server.player1PaddleYPosIn1 = GAME_PANEL_BOTTOM_WALL - PADDLE_HEIGHT;
		server.player2PaddleYPosIn1 = GAME_PANEL_TOP_WALL;
		
		server.player1PaddleXPosIn1 = (WINDOW_WIDTH/2) - (PADDLE_WIDTH/2);
		server.player2PaddleXPosIn1 = (WINDOW_WIDTH/2) - (PADDLE_WIDTH/2);
		server.player1PaddleXPosIn2 = (WINDOW_WIDTH/2) - (PADDLE_WIDTH/2);
		server.player2PaddleXPosIn2 = (WINDOW_WIDTH/2) - (PADDLE_WIDTH/2);
		
		resetBall();
		ballThread = new Thread(this);
		ballThread.start();
	}
	
	@Override
	public void run() {
		while(true){
			if(server.gameState == 1 && server.players.size() == 2){
				updateBall();
				if(server.player1Score == server.gamePoint){
					server.winner = 1;
					ServerWindow.gameStatus.setText("Game Status: Ended | Player 1 Wins!");
				}else if(server.player2Score == server.gamePoint){
					server.winner = 2;
					ServerWindow.gameStatus.setText("Game Status: Ended | Player 2 Wins!");
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(server.gameState == 0){
				break;
			}
		}
		return;
	}
	
	private void updateBall() {
		//ball calculations relative to player 1 screen, converted for player 2 screen
		server.ballXPosIn1 = server.ballXPosIn1 + server.dx * BALL_SPEED;
		server.ballYPosIn1 = server.ballYPosIn1 + server.dy * BALL_SPEED;
		//side walls
		if(server.ballXPosIn1 + server.dx  < 0 || server.ballXPosIn1 + BALL_DIAMETER - server.dx > WINDOW_WIDTH){
			if(server.dx < 0){
				//moving left
				server.ballXPosIn1 = 0;
				server.dx = random.nextInt(4);
				if(server.dx == 0){
					server.dx = 1;
				}	
			}
			else{
				//moving right
				server.ballXPosIn1 = WINDOW_WIDTH - BALL_DIAMETER;
				server.dx = -random.nextInt(4);
				if(server.dx == 0){
					server.dx = -1;
				}
			}
		}
		//top wall (player 2 base)
		if(server.ballYPosIn1 + server.dy < GAME_PANEL_TOP_WALL){
			server.dy = 1 + (tempHitCounts/10);
			server.dx = -2 + random.nextInt(4);
			if(server.dx == 0){
				server.dx = 1;
			}
			//player 1 point
			server.player1Score++;
			tempHitCounts = 0;
			ServerWindow.player1Point.setText("Point: "+ server.player1Score);
		}
		//bottom wall (player 1 base)
		if(server.ballYPosIn1 + BALL_DIAMETER + server.dy > GAME_PANEL_BOTTOM_WALL){
			server.dy = -1 - (tempHitCounts/10);
			server.dx = -2 + random.nextInt(4);
			if(server.dx == 0){
				server.dx = 1;
			}
			//player 2 point
			server.player2Score++;
			tempHitCounts = 0;
			ServerWindow.player2Point.setText("Point: "+ server.player2Score);
		}
		server.ballXPosIn2 = WINDOW_WIDTH - (server.ballXPosIn1 + BALL_DIAMETER);
		server.ballYPosIn2 = GAME_PANEL_HEIGHT - (server.ballYPosIn1 + BALL_DIAMETER) + GAME_PANEL_TOP_WALL + GAME_PANEL_TOP_WALL;
				
		ballRect.setBounds(server.ballXPosIn1, server.ballYPosIn1, BALL_DIAMETER, BALL_DIAMETER);
		player1Rect.setBounds(server.player1PaddleXPosIn1, server.player1PaddleYPosIn1, 
				PADDLE_WIDTH, PADDLE_HEIGHT);
		player2Rect.setBounds(server.player2PaddleXPosIn1, server.player2PaddleYPosIn1, 
				PADDLE_WIDTH, PADDLE_HEIGHT);
		//player 2 paddle and ball collision
		if(player2Rect.intersects(ballRect)){
			server.dy = 1 + (tempHitCounts/10);
			server.dx = -2 + random.nextInt(4);
			if(server.dx == 0){
				server.dx = 1;
			}
			tempHitCounts++;
		}
		//player 1 paddle and ball collision
		if(player1Rect.intersects(ballRect)){
			server.dy = -1 - (tempHitCounts/10);
			server.dx = -2 + random.nextInt(4);
			if(server.dx == 0){
				server.dx = 1;
			}
			tempHitCounts++;
		}
	}
	private void resetBall() {
		//ball in the middle of the park
		tempHitCounts = 0;
		server.ballXPosIn1 = (WINDOW_WIDTH/2)-(BALL_DIAMETER/2);
		server.ballYPosIn1 = ((GAME_PANEL_BOTTOM_WALL + GAME_PANEL_TOP_WALL)/2)-(BALL_DIAMETER/2);
		
		//calculate for player 2 screen
		server.ballXPosIn2 = WINDOW_WIDTH - (server.ballXPosIn1 + BALL_DIAMETER);
		server.ballYPosIn2 = GAME_PANEL_HEIGHT - (server.ballYPosIn1 + BALL_DIAMETER) + GAME_PANEL_TOP_WALL  + GAME_PANEL_TOP_WALL;
		
		//random numbers between 0 and 3, gives -2,-1,0,or 1
		server.dy = -2 + random.nextInt(4);
		if(server.dy == 0){
			server.dy = 1;
		}
		//returns true or false
		if(random.nextBoolean()){
			server.dx = 1;
		}
		else{
			server.dx = -1;
		}
	}
}
