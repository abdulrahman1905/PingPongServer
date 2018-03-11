package pongserver;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ServerWindow extends JFrame implements ActionListener, WindowListener{

	private static final long serialVersionUID = 1L;
	//declare components
	private JPanel serverStatusPanel;
	public static JLabel serverStatus;
	public static JButton startButton;
	public static JButton stopButton;
	
	private JPanel player1Panel;
	public static JLabel player1Status;
	public static JLabel player1PCName;
	public static JLabel player1Point;
	
	private JPanel player2Panel;
	public static JLabel player2Status;
	public static JLabel player2PCName;
	public static JLabel player2Point;
	
	private JPanel gameStatusPanel;
	public static JLabel gameStatus;
	
	Border border;
	Server server;
	
	//constructor
	public ServerWindow(Server server){
		setTitle("Ping Pong Server");
		setSize(300, 250);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.server = server;
		
		border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		serverStatusPanel = new JPanel();
		serverStatus = new JLabel("<html><font color='red'>Server Offline</font>");
		startButton = new JButton("Start Server");
		stopButton = new JButton("Stop Server");
		serverStatusPanel.setLayout(null);
		serverStatus.setBounds(5, 5, 275, 25);
		startButton.setBounds(5, 35, 137, 25);
		stopButton.setBounds(143, 35, 137, 25);
		serverStatusPanel.add(startButton);
		serverStatusPanel.add(stopButton);
		serverStatusPanel.add(serverStatus);
		serverStatusPanel.setBounds(5, 5, 285, 65);
		serverStatusPanel.setBorder(border);
		
		player1Panel = new JPanel();
		player1Status = new JLabel("<html><font color='red'>Player 1 Offline</font>");
		player1PCName = new JLabel("PC: ");
		player1Point = new JLabel("Point:");
		player1Panel.setLayout(null);
		player1Status.setBounds(5, 5, 275, 25);
		player1PCName.setBounds(5, 30, 170, 25);
		player1Point.setBounds(175, 30, 105, 25);
		player1Panel.add(player1Status);
		player1Panel.add(player1PCName);
		player1Panel.add(player1Point);
		player1Panel.setBounds(5, 70, 285, 60);
		player1Panel.setBorder(border);
		
		player2Panel = new JPanel();
		player2Status = new JLabel("<html><font color='red'>Player 2 Offline</font>");
		player2PCName = new JLabel("PC: ");
		player2Point = new JLabel("Point:");
		player2Panel.setLayout(null);
		player2Status.setBounds(5, 5, 275, 25);
		player2PCName.setBounds(5, 30, 170, 25);
		player2Point.setBounds(175, 30, 105, 25);
		player2Panel.add(player2Status);
		player2Panel.add(player2PCName);
		player2Panel.add(player2Point);
		player2Panel.setBounds(5, 130, 285, 60);
		player2Panel.setBorder(border);
		
		gameStatusPanel = new JPanel();
		gameStatus = new JLabel("Game Status: Stopped");
		gameStatusPanel.setLayout(null);
		gameStatus.setBounds(5, 0, 275, 25);
		gameStatusPanel.add(gameStatus);
		gameStatusPanel.setBounds(5, 190, 285, 25);
		gameStatusPanel.setBorder(border);
		
		add(serverStatusPanel);
		add(player1Panel);
		add(player2Panel);
		add(gameStatusPanel);
		
		stopButton.setEnabled(false);
		
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		addWindowListener(this);
		
		serverStatus.setBorder(border);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource().equals(startButton)){
			server.startServer();
		}
		if(evt.getSource().equals(stopButton)){
			server.stopServer();
		}
	}
	@Override
	public void windowClosing(WindowEvent e) {
		int rep = JOptionPane.showConfirmDialog(this, "Close the server?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(rep == JOptionPane.YES_OPTION){
			if(server.serverStarted)
			server.stopServer();
			System.exit(0);
		}
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowOpened(WindowEvent e) {
	}
}
