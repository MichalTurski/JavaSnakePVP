import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Klasa obslugujaca logikę programu, to jest obsługująca wykonywanie kolejnych kroków oraz komunikację z serwerem.
 */
public class SnakeControler{
	
	private SnakeModel model;
	private SnakeView view;
	private int direction;
	private Client client;
	private boolean loose;
	private boolean win;
	private boolean isGameOn = false;
	public SnakeControler(SnakeModel model, SnakeView view){
		this.model = model;
		this.view = view;
		client = new Client(model);
	}
	/**
	 * Metoda rozpoczynająca nową grę o ile rozgrywka nie jest w trakcie.
	 */
	public void start(){
		if(!isGameOn){
			client.ready();
			model.newGame();
			Thread thread = new Thread() {
	        public void run() {
	        	SnakeControler.this.play();
	        }
	    };
	    thread.start();
		}
		isGameOn = true;
		loose = false;
		win = false;
	}
	private void play(){
		do{
			client.receiveGameState();
			view.repaint();
			detectCollision();
			client.sendDiretion(direction);
		}while(!(win||loose));
		view.showResult();
		isGameOn = false;
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w górę.
	 */
	public void moveMeUp(){
		direction = 1;
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w dół.
	 */
	public void moveMeDown(){
		direction = 2;
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w prawo.
	 */
	public void moveMeRight(){
		direction = 3;
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w lewo.
	 */
	public void moveMeLeft(){
		direction = 4;
	}
	/**
	 * Metoda zwracająca wartość pola loose
	 * @return wartość pola loose
	 */
	public boolean getLoose(){return loose;}
	/**
	 * Metoda zwracająca wartość pola win
	 * @return wartość pola win
	 */
	public boolean getWin(){return win;}
	/**
	 * Metoda inicjująca połączenie klienta z serwerem.
	 */
	public void connectServer() {
		client.connect();
	}
	private void detectCollision(){
		ArrayList<Point> opponentSnake = model.getOpponentSnake();
		ArrayList<Point> mySnake = model.getMySnake();
		Point myHead = mySnake.get(0);
		Point oponentHead = opponentSnake.get(0);
		Point current;
		for(int i = 1; i<mySnake.size(); i++){
			current = mySnake.get(i);
			if(myHead.equals(current)){
				loose = true;
				return;
			}
		}
		for(Point p:opponentSnake){
			if(myHead.equals(p)){
				loose = true;
				return;
			}
		}
		for(int i = 1; i<opponentSnake.size(); i++){
			current = opponentSnake.get(i);
			if(oponentHead.equals(current)){
				win = true;
				return;
			}
		}
		for(Point p:mySnake){
			if(oponentHead.equals(p))
			{
				win = true;
				return;
			}
		}
	}
	class Client {
		private PrintWriter out;
		private BufferedReader in;
		private Socket socket;
		private final Lock lock = new ReentrantLock();
		private final Condition notConnected  = lock.newCondition();
		private boolean isConnected = false;
		private SnakeModel model;
		public Client(SnakeModel model){
			this.model = model;
		}
		public void connect(){
			try {
					socket = new Socket("localhost", 5557);
					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				}
			catch (IOException e) {
				System.err.println("Failed to connect with server.");
				System.exit(2);
			}
			lock.lock();
			notConnected.signal();
			isConnected = true;
			lock.unlock();

		}
		public void ready(){
			lock.lock();
			try{
				if(! isConnected)
					notConnected.await();
				String inputLine, outputLine;
				outputLine = new String("ready");
				inputLine = new String();
				out.println(outputLine);
				do{
					try {
						inputLine = in.readLine();
					} catch (IOException e) {
						System.err.println("Connection failed");
						System.exit(2);
					}
					if(inputLine == null)
						System.exit(1);
				}while(!(inputLine.equals("ready")));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			finally{
				lock.unlock();
			}
		}
		public void receiveGameState(){
			ArrayList<Point> opponentSnake = model.getOpponentSnake();
			ArrayList<Point> mySnake = model.getMySnake();
			Point food = model.getFoodPoint();
			opponentSnake.clear();
			mySnake.clear();
			String fromServer;
			Point point;
			try {
				fromServer = in.readLine();
				if(fromServer == null)
					System.exit(1);
				while(!fromServer.equals("end")){
					point = new Point();
					point.x = Integer.parseInt(fromServer);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
					point.y = Integer.parseInt(fromServer);
					opponentSnake.add(point);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
				}
				fromServer = in.readLine();
				if(fromServer == null)
					System.exit(1);
				while(!fromServer.equals("end")){	
					point = new Point();
					point.x = Integer.parseInt(fromServer);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
					point.y = Integer.parseInt(fromServer);
					mySnake.add(point);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
				}
				fromServer = in.readLine();
				if(fromServer == null)
					System.exit(1);
				while(!fromServer.equals("end")){	
					food.x = Integer.parseInt(fromServer);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
					food.y = Integer.parseInt(fromServer);
					fromServer = in.readLine();
					if(fromServer == null)
						System.exit(1);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void sendDiretion(int dir){
			try {
				String fromServer = in.readLine();
				if(fromServer == null)
					System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String direction = new String(Integer.toString(dir));
			out.println(direction);
		}
	}
}
