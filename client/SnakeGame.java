/**
 * Klinet gry snake. Przed uruchomeniem klienta wymagane jest uruchomienie serwera.
 * @author Michał Turski
 *
 */
public class SnakeGame {
	public static SnakeGame game;
	public SnakeGame(){
		SnakeModel model = new SnakeModel();
		SnakeView view = new SnakeView(model);
		SnakeControler controler  = new SnakeControler(model, view);
		view.setControler(controler);
		controler.connectServer();
	}
	
	public static void main(String[] args) {
		game = new SnakeGame();
	}

}
