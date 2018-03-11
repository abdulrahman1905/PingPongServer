package pongserver;

public class ServerMain {
	public static ServerWindow serverWindow;
	public static void main(String[] args) {
		serverWindow = new ServerWindow(new Server());
		serverWindow.setVisible(true);
	}
}
