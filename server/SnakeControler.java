import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Settings;

/**
 * Klasa obslugujaca logikę programu, to jest obsługująca wykonywanie kolejnych kroków oraz komunikację z klientem.
 */
public class SnakeControler{
	private SnakeModel model;
	private SnakeView view;
	private Direction myDirection;
	private Direction opponentDirection;
	private Server server;
	private boolean loose;
	private boolean win;
	private boolean isGameOn = false;
	public SnakeControler(SnakeModel model, SnakeView view){
		this.model = model;
		this.view = view;
		myDirection = new Direction(model.getMyDirection());
		opponentDirection = new Direction(model.getOpponentDirection());
		server = new Server(this, model);
	}
	/**
	 * Metoda rozpoczynająca nową grę o ile rozgrywka nie jest w trakcie.
	 */
	public void start(){
		if(!isGameOn){
			server.ready();
			model.newGame();
			myDirection = new Direction(model.getMyDirection());
			opponentDirection = new Direction(model.getOpponentDirection());
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
		long start;
		long wait;
		long elapsed;
		do{
			start = System.nanoTime();
			step();
			server.sendGameState();
			view.repaint();
			elapsed =  System.nanoTime() - start;
			wait = TimeUnit.NANOSECONDS.toMillis((TimeUnit.SECONDS.toNanos(1)/Settings.ticksPerSecond) - elapsed);
			if (wait < 0)
				wait = 5;
			try {
				TimeUnit.MILLISECONDS.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			server.receiveDirectionChange();
		}while(!(win||loose));
		view.showResult(win, loose);
		isGameOn = false;
	}
	private void step(){
		ArrayList<Point> mySnake = model.getMySnake();
		Point myHead = mySnake.get(0);
		if(myHead.equals(model.getFoodPoint())){
			Point myNewTail = new Point(mySnake.get(mySnake.size() -1));
			mySnake.add(myNewTail);
			model.replaceFood();
		}
		Point myNewHead = new Point(myHead);
		myNewHead.x += myDirection.getX();
		myNewHead.y += myDirection.getY();
		if(myNewHead.x > Settings.width){
			myNewHead.x = 0;
		}
		else if (myNewHead.x < 0){
			myNewHead.x = Settings.width;
		}
		else if(myNewHead.y > Settings.height){
			myNewHead.y = 0;
		}
		else if(myNewHead.y < 0){
			myNewHead.y = Settings.height;
		}
		mySnake.add(0, myNewHead);
		myDirection.update();
		mySnake.remove(mySnake.size()-1);
		////////////////////////////////////////////
		ArrayList<Point> opponentSnake = model.getOpponentSnake();
		Point opponentHead = opponentSnake.get(0);
		if(opponentHead.equals(model.getFoodPoint())){
			Point opponentNewTail = new Point(opponentSnake.get(opponentSnake.size() -1));
			opponentSnake.add(opponentNewTail);
			model.replaceFood();
		}
		Point opponentNewHead = new Point(opponentHead);
		opponentNewHead.x += opponentDirection.getX();
		opponentNewHead.y += opponentDirection.getY();
		if(opponentNewHead.x > Settings.width){
			opponentNewHead.x = 0;
		}
		else if (opponentNewHead.x < 0){
			opponentNewHead.x = Settings.width;
		}
		else if(opponentNewHead.y > Settings.height){
			opponentNewHead.y = 0;
		}
		else if(opponentNewHead.y < 0){
			opponentNewHead.y = Settings.height;
		}
		opponentSnake.add(0, opponentNewHead);
		opponentDirection.update();
		opponentSnake.remove(opponentSnake.size()-1);
		for(int i = 0; i<opponentSnake.size(); i++){
			if(i!=0){
				if(opponentNewHead.equals(opponentSnake.get(i))){
					win = true;
					return;
				}
			}
			if(myNewHead.equals(opponentSnake.get(i))){
				loose = true;
				return;
			}
		}
		for(int i = 0; i<mySnake.size(); i++){
			if(i != 0){
				if(myNewHead.equals(mySnake.get(i))){
					loose = true;
					return;
				}
			}
			if(opponentNewHead.equals(mySnake.get(i))){
				win = true;
				return;
			}
		}
		
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w górę.
	 */
	public boolean moveMeUp(){
		return myDirection.setUp();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w dół.
	 */
	public boolean moveMeDown(){
		return myDirection.setDown();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w prawo.
	 */
	public boolean moveMeRight(){
		return myDirection.setRight();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża gracza będzie wykonany w lewo.
	 */
	public boolean moveMeLeft(){
		return myDirection.setLeft();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża przeciwnika będzie wykonany w górę.
	 */
	public boolean moveOpponentUp(){
		return opponentDirection.setUp();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża przeciwnika będzie wykonany w dół.
	 */
	public boolean moveOpponentDown(){
		return opponentDirection.setDown();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża przeciwnika będzie wykonany w prawo.
	 */
	public boolean moveOpponentRight(){
		return opponentDirection.setRight();
	}
	/**
	 * Metoda ustalająca że kolejny krok węża przeciwnika będzie wykonany w lewo.
	 */
	public boolean moveOpponentLeft(){
		return opponentDirection.setLeft();
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
	 * Metoda inicjująca połączenie serwera z klientem.
	 */
	public void connectServer() {
		server.connect();
	}
	class Server {
		private SnakeControler controler;
		private SnakeModel model;
		private PrintWriter out;
		private BufferedReader in;
		private ServerSocket serverSocket;
		private Socket clientSocket;
		private final Lock lock = new ReentrantLock();
		private final Condition notConnected  = lock.newCondition();
		private boolean isConnected = false;
		//private Object startLock;
		public Server(SnakeControler controler, SnakeModel model){
			this.controler = controler;
			this.model = model;
			try {
		            serverSocket = new ServerSocket(5557);
		            
			}
			catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + "5557" + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
		}
		public void connect(){
			try {
					clientSocket = serverSocket.accept();
			        out = new PrintWriter(clientSocket.getOutputStream(), true);
			        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				}
			catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port " + "5557" + " or listening for a connection");
			    System.out.println(e.getMessage());
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
				}
				while(!(inputLine.equals("ready")));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			finally{
				lock.unlock();
			}
		}
		public void sendGameState(){
			for(Point p: model.getMySnake()){
				out.println(p.x);
				out.println(p.y);
			}
			out.println("end");
			for(Point p: model.getOpponentSnake()){
				out.println(p.x);
				out.println(p.y);
			}
			out.println("end");
			Point p = model.getFoodPoint();
			out.println(p.x);
			out.println(p.y);
			out.println("end");
		}
		public void receiveDirectionChange(){
			String inString = new String();
			int direction;
			try {
				out.println("sendDir");
				inString = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(inString == null)
				System.exit(1);
			direction = Integer.parseInt(inString);
			switch(direction){
			case 1:
				controler.moveOpponentUp();
				break;
			case 2:
				controler.moveOpponentDown();
				break;
			case 3:
				controler.moveOpponentRight();
				break;
			case 4:
				controler.moveOpponentLeft();
				break;
			}
		}
	}
	class Direction{
		private short currentX, currentY;
		private short nextX, nextY;
		public Direction(int dir){
			switch(dir){
				case 1:
					currentX = -1;
					currentY = 0;
					nextX = -1;
					nextY = 0;
					break;
				case 2:
					currentX = 0;
					currentY = -1;
					nextX = 0;
					nextY = -1;
					break;
				case 3:
					currentX = 1;
					currentY = 0;
					nextX = 1;
					nextY = 0;
					break;
				case 4:
					currentX = 0;
					currentY = 1;
					nextX = 0;
					nextY = 1;
					break;
			}
		}
		public void update(){
			currentX = nextX;
			currentY = nextY;
		}
		public boolean setUp(){
			if(currentY != 1){
				nextX = 0;
				nextY = -1;
				return true;
			}
			return false;
		}
		public boolean setDown(){
			if(currentY != -1){
				nextX = 0;
				nextY = 1;
				return true;
			}
			return false;
		}
		public boolean setRight(){
			if(currentX != -1){
				nextX = 1;
				nextY = 0;
				return true;
			}
			return false;
		}
		public boolean setLeft(){
			if(currentX != 1){
				nextX = -1;
				nextY = 0;
				return true;
			}
			return false;
		}
		public short getX(){return nextX;}
		public short getY(){return nextY;}
	}

}
