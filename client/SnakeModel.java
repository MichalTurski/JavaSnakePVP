import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import util.Settings;

/**
 * Klasa przeshowująca stan gry.
 * @author Michał Turski
 *
 */
public class SnakeModel{
	private Snake mySnake;
	private int myDirection;
	private Snake opponentSnake;
	private int opponentDirection;
	private Food food;
	public SnakeModel(){
		newDirections();
		mySnake = new Snake(myDirection);
		opponentSnake = new Snake(opponentDirection);
		food = new Food();
		food.replace(this);
	}
	/**
	 * Metoda zwracająca położenie 
	 * @return polozenie jedzenia
	 */
	public Point getFoodPoint(){
		return food.localization;
	}
	/**
	 * Metoda znajdująca wolne miejsce i umieszczająca tam jedzenienie.
	 */
	public void replaceFood(){
		food.replace(this);
	}
	/**
	 * Metoda losująca parametry dla nowej gry i testująca ich integralność.
	 */
	public void newGame() {
		newDirections();
		mySnake.newSnake(myDirection);
		opponentSnake = new Snake(opponentDirection);
		food = new Food();
		food.replace(this);
	}
	private void newDirections(){
		myDirection = ThreadLocalRandom.current().nextInt(1, 5);
		opponentDirection = ThreadLocalRandom.current().nextInt(1, 5);
	}
	/**
	 * Metoda zwracająca listę elementów węża gracza.
	 * @return lista elementów węża gracza
	 */
	public ArrayList<Point> getMySnake(){
		return mySnake.snakeElements;
	}
	/**
	 * Metoda zwracająca listę elementów węża przeciwnika.
	 * @return lista elementów węża przeciwnika
	 */
	public ArrayList<Point> getOpponentSnake() {
		return opponentSnake.snakeElements;
	}
	/**
	 * Metoda zwracająca kierunek węża gracza.
	 * @return kierunek węża gracza
	 */
	public int getMyDirection() {
		return myDirection;
	}
	/**
	 * Metoda zwracająca kierunek węża przeciwnika.
	 * @return kierunek węża przeciwnika
	 */
	public int getOpponentDirection() {
		return opponentDirection;
	}
	class Food{
		public Point localization;
		public void replace(SnakeModel model){
			Boolean flag = true;
			int x, y;
			Point hipoteticalPlace = new Point();
			while(flag){
				flag = false;
				x = ThreadLocalRandom.current().nextInt(0, Settings.width );
				y = ThreadLocalRandom.current().nextInt(0, Settings.height );
				hipoteticalPlace.move(x, y);
				for(int i = 0; i < model.mySnake.snakeElements.size();i++){
					if(hipoteticalPlace.equals(model.mySnake.snakeElements.get(i))){
						flag = true;
						break;
					}
				}
			}
			localization = hipoteticalPlace;
		}
	}
	class Snake{
		public ArrayList<Point> snakeElements;
		public Snake(int direction){
			int headX = ThreadLocalRandom.current().nextInt(3, Settings.width -3);
			int headY = ThreadLocalRandom.current().nextInt(3, Settings.height -3);
			snakeElements = new ArrayList<Point>();
			Point head = new Point(headX, headY);
			snakeElements.add(head);
			//int direction = ThreadLocalRandom.current().nextInt(1, 5);
			if(direction == 1){
				Point elem = new Point(head);
				elem.x++;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.x++;
				snakeElements.add(elem2);
			}
			if(direction == 2){
				Point elem = new Point(head);
				elem.y++;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.y++;
				snakeElements.add(elem2);
			}
			if(direction == 3){
				Point elem = new Point(head);
				elem.x--;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.x--;
				snakeElements.add(elem2);

			}
			if(direction == 4){
				Point elem = new Point(head);
				elem.y--;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.y--;
				snakeElements.add(elem2);

			}
		}
		public void newSnake(int direction){
			int headX = ThreadLocalRandom.current().nextInt(3, Settings.width -3);
			int headY = ThreadLocalRandom.current().nextInt(3, Settings.height -3);
			snakeElements = new ArrayList<Point>();
			Point head = new Point(headX, headY);
			snakeElements.add(head);
			//int direction = ThreadLocalRandom.current().nextInt(1, 5);
			if(direction == 1){
				Point elem = new Point(head);
				elem.x++;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.x++;
				snakeElements.add(elem2);
			}
			if(direction == 2){
				Point elem = new Point(head);
				elem.y++;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.y++;
				snakeElements.add(elem2);
			}
			if(direction == 3){
				Point elem = new Point(head);
				elem.x--;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.x--;
				snakeElements.add(elem2);

			}
			if(direction == 4){
				Point elem = new Point(head);
				elem.y--;
				snakeElements.add(elem);
				Point elem2 = new Point(elem);
				elem2.y--;
				snakeElements.add(elem2);

			}
		}

	}
}

