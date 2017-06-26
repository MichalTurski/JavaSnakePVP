package util;

import java.awt.Color;

/**
 * Klasa zawierająca ustawienia gry.
 */
public class Settings {
	public static int width = 20;
	public static int height = 20;
	public static int blockSize = 15;
	public static int widthPx = width * blockSize + 17;
	public static int heightPx = height * blockSize + 41;
	public static Color backgroudColor = Color.white;
	public static Color playerSnakeColor = Color.green;
	public static Color opponentSnakeColor = Color.red;
	public static Color foodColor = Color.gray;
	public static int ticksPerSecond = 5;
	
}
