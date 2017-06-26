import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import util.Settings;
/**
 * Klasa odpowiedzialna za obsluge gui
 */
public class SnakeView{
	
	private JFrame frame;
	private JPanel panel;
	private GamePanel gamePanel;
	private GroupLayout layout;
	private SnakeControler controler;
	public SnakeView(SnakeModel model){
		frame = new JFrame("Snake");
		panel = new JPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layout = new GroupLayout(panel);
		frame.setVisible(true);
		frame.setSize(Settings.widthPx+100, Settings.heightPx+25);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		gamePanel = new GamePanel(model);
		JButton button = new JButton("Start");
		button.setSize(100, 30);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				controler.start();
			}
		}); 
		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();
		leftToRight.addComponent(gamePanel);
		leftToRight.addComponent(button);
		layout.setHorizontalGroup(leftToRight);
		GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();
		GroupLayout.ParallelGroup rowTop = layout.createParallelGroup();
		rowTop.addComponent(gamePanel);
		rowTop.addComponent(button);
		topToBottom.addGroup(rowTop);
		
		layout.setVerticalGroup(topToBottom);
		frame.add(panel);
		frame.pack();
		
		frame.setResizable(false);
		panel.repaint();
	}
	/**
	 * Metoda odświeżająca panel gry
	 */
	public void repaint(){
		frame.paintComponents(frame.getGraphics());
	}
	/**
	 * Metoda ustawiająca kontroler do klasy SnakeView
	 * @param controler
	 */
	public void setControler(SnakeControler controler){
		this.controler = controler;
		gamePanel.setControler(controler);
	}
	/**
	 * Metoda wyśwetlająca okno z wynikiem gry
	 */
	public void showResult() {
		String result = new String();
		if(controler.getWin())
			result = "You won!";
		if(controler.getLoose())
			result = "You lost!";
		JOptionPane.showMessageDialog(frame, result);
	}
	class GamePanel extends JPanel{
		private SnakeControler controler;
		private SnakeModel model;
		protected GamePanel(SnakeModel m){
			model = m;
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "move_up");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "move_down");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "move_right");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "move_left");
			this.getActionMap().put("move_up", new UpAction());
			this.getActionMap().put("move_down", new DownAction());
			this.getActionMap().put("move_right", new RightAction());
			this.getActionMap().put("move_left", new LeftAction());
		}
		public void setControler(SnakeControler contr){
			this.controler = contr;
		}
		@Override
		protected void paintComponent(Graphics g){
			fillBackground(g);
			paintSnakes(g);
			paintFood(g);
		}
		private void fillBackground(Graphics g){
			g.setColor(Settings.backgroudColor);
			g.fillRect(0, 0, Settings.widthPx, Settings.heightPx);
		}
		private void paintSnakes(Graphics g){
			g.setColor(Settings.playerSnakeColor);
			for(Point p: model.getMySnake()){
				g.fillRect(Settings.blockSize * p.x, Settings.blockSize * p.y, Settings.blockSize, Settings.blockSize);
			}
			g.setColor(Settings.opponentSnakeColor);
			for(Point p: model.getOpponentSnake()){
				g.fillRect(Settings.blockSize * p.x, Settings.blockSize * p.y, Settings.blockSize, Settings.blockSize);
			}
		}
		private void paintFood(Graphics g){
			g.setColor(Settings.foodColor);
			Point p = model.getFoodPoint();
			g.fillRect(Settings.blockSize * p.x, Settings.blockSize * p.y, Settings.blockSize, Settings.blockSize);
		}
		private class UpAction extends AbstractAction{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controler.moveMeUp();
			}
		}
		private class DownAction extends AbstractAction{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controler.moveMeDown();
			}
		}
		private class RightAction extends AbstractAction{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controler.moveMeRight();
			}
		}
		private class LeftAction extends AbstractAction{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controler.moveMeLeft();
			}
		}
	}
}

