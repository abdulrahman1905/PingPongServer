package pongserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Server implements Runnable{
	final int PORT = 9900;
	public ServerSocket server;
	Socket player;
	public ArrayList<Socket> players;
	int playerNumber;
	Thread serverThread;
	public Boolean player2Ready = false;
	public Boolean serverStarted = false;
	public Boolean player1Connected = false;
	public Boolean player2Connected = false;
	
	RequestHandler player1Handler;
	RequestHandler player2Handler;
	
	int gamePoint;
	int gameState = 0; //0-stopped, 1-playing, 2-paused 
	
	int player1PaddleXPosIn1;
	int player2PaddleXPosIn1;
	int player1PaddleXPosIn2;
	int player2PaddleXPosIn2;
	
	int player1PaddleYPosIn1;
	int player2PaddleYPosIn1;
	
	int ballXPosIn1,ballYPosIn1,dx,dy;
	int ballXPosIn2,ballYPosIn2;
	
	int player1Score, player2Score;
	int winner; //0-game playing, 1-player 1, 2-player 2
	
	public Server(){
		players = new ArrayList<Socket>();
	}
	public void startServer(){
		serverThread = new Thread(this);
		serverThread.start();
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			server = new ServerSocket(PORT);
			serverStarted = true;
			ServerWindow.startButton.setEnabled(false);
			ServerWindow.stopButton.setEnabled(true);
			ServerWindow.serverStatus.setText("<html><font color='green'>Server Online - Port 9900</font>");
			while(true){
				/*listens for new player connections and accept the 
				 * connection if number of connected players is not up to 2*/
				if(players.size() < 2){
					player = server.accept();
					//add the player to the list
					players.add(player);
					if(players.size() == 1){
						playerNumber = 1;
						ServerWindow.player1Status.setText("<html><font color='green'>Player 1 Online</font>");
						ServerWindow.player1PCName.setText("PC: "+ player.getInetAddress().getLocalHost().getHostName());
						player1Connected = true;
						
						ServerWindow.player1Point.setText("Point: "+player1Score);
						//start player 1 request handler
						player1Handler = new RequestHandler(player, this, playerNumber);
						player1Handler.start();
					}
					else if(players.size() == 2){
						playerNumber = 2;
						ServerWindow.player2Status.setText("<html><font color='green'>Player 2 Online</font>");
						ServerWindow.player2PCName.setText("PC: "+ player.getInetAddress().getLocalHost().getHostName());
						player2Connected = true;
						
						ServerWindow.player2Point.setText("Point: "+player2Score);
						//start player request handler
						player2Handler = new RequestHandler(player, this, playerNumber);
						player2Handler.start();
					}
				}
				else{
					System.out.println("BothPayers Connected, No more connections");
					break;
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(ServerMain.serverWindow, "Server Stopped", "Server", JOptionPane.INFORMATION_MESSAGE);
		}
		finally{
			
		}
		return;
	}
	public void stopServer(){
		try {
			if(player1Connected){
				writeToPlayer1("stopped");
			}
			if(player2Connected){
				writeToPlayer2("stopped");
			}
			gameState = 0;

			if(player1Connected){
				player1Handler.input.close();
				player1Handler.output.close();
				player1Handler.player.close();
				if(player1Handler.out != null)
				player1Handler.out.close();
				player1Handler.timer.stop();
			}
			if(player2Connected){
				player2Handler.input.close();
				player2Handler.output.close();
				player2Handler.player.close();
				if(player2Handler.out != null)
				player2Handler.out.close();
				player2Handler.timer.stop();
			}
			if(player1Connected || player2Connected){
				player.close();
			}
			
			players.clear();
			server.close();
			
			serverStarted = false;
			player1Connected = false;
			player2Connected = false;
			player2Ready = false;
			
			ServerWindow.startButton.setEnabled(true);
			ServerWindow.stopButton.setEnabled(false);
			ServerWindow.serverStatus.setText("<html><font color='red'>Server Offline</font>");
			ServerWindow.player1Status.setText("<html><font color='red'>Player 1 Offline</font>");
			ServerWindow.player2Status.setText("<html><font color='red'>Player 2 Offline</font>");
			ServerWindow.player1PCName.setText("PC:");
			ServerWindow.player2PCName.setText("PC:");
			ServerWindow.player1Point.setText("Point:");
			ServerWindow.player2Point.setText("Point:");
			ServerWindow.gameStatus.setText("Game Status: Stopped");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeToPlayer1(String message){
		try {
			PrintWriter out = new PrintWriter(players.get(0).getOutputStream());
			out.println(message);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeToPlayer2(String message){
		try {
			PrintWriter out = new PrintWriter(players.get(1).getOutputStream());
			out.println(message);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
