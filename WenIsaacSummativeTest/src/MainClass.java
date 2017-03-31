import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Timer;
interface Observer{
	//Receiver for notifications
	public void update(KeyEvent keyevent, boolean pressed);
}
interface Observable{
	//Sender for notifications
	public void notifyObservers(KeyEvent keyevent, boolean pressed);
}
/** TODO:
	- Generalization (comment on it)!
 **/
public class MainClass extends Applet implements ActionListener, KeyListener, Observable{
	Timer timer = new Timer(10, this);									//Declare timer and init
	Image offscreen;													//Double buffer image
	Graphics buffer;													//Double buffer drawer
	private static ArrayList<Observer> ObserverList = new ArrayList<Observer>();	//Declare and init observer list
	static Point2D PLAY_FIELD_SIZE;										//Supposed to be a constant, but can't actually set this until i set the size)
	private ArrayList<Entity> entities = new ArrayList();
	public void init(){		
		this.addKeyListener(this);										//Adds a keylistener
		this.setSize(1600,900);											//Set window size
		PLAY_FIELD_SIZE  = new Point2D.Double(this.getWidth()/4*3, this.getHeight());	//Sets a "constant" (not really because things)
		offscreen = createImage(this.getWidth(),this.getHeight());		//Initialized the buffer image
		buffer = offscreen.getGraphics();								//Sets the buffer to draw on offscreen
		roundStart();													//Starts the game
	}
	public void paint(Graphics g){
		buffer.setColor(Color.black);									//Sets color of playfield to black
		buffer.fillRect(0, 0, (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY());	//Draws the playfeild
		for(int i = 0; i < entities.size(); i++){
			buffer.setColor(entities.get(i).color);									//Same but for boss
			buffer.fillOval((int)entities.get(i).location.getX(),(int) entities.get(i).location.getY(), (int)entities.get(i).size.getX(), (int)entities.get(i).size.getY());
		}
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getClass().equals(Player.class)){
				for(Bullet b: ((Player)entities.get(i)).bullets){									
					buffer.setColor(((Player)entities.get(i)).color);
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
			else if(entities.get(i).getClass().equals(Boss.class)){
				for(Bullet b: ((Boss)entities.get(i)).bullets){									
					buffer.setColor(((Boss)entities.get(i)).color);
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
		}
		g.drawImage(offscreen,0,0,this);								//Draws the buffered image onto the screen
	}
	public void collideCheck(){
		for(int i = 0; i < entities.size(); i++){
			for(int k = 0; k < entities.size(); k++){
				if(entities.get(i).getClass().equals(Player.class)){
					if(entities.get(k).getClass().equals(Boss.class)){
						for(int j = ((Player)entities.get(i)).bullets.size()-1; j >= 0; j--){
							((Player)entities.get(i)).bullets.get(j).move();
							if(collide(((Player)entities.get(i)).bullets.get(j),(Boss)entities.get(k))){
								((Boss)entities.get(k)).hit();
								((Player)entities.get(i)).bullets.remove(((Player)entities.get(i)).bullets.get(j));
							}
						}
					}
				}
				else if(entities.get(i).getClass().equals(Boss.class)){
					if(entities.get(k).getClass().equals(Player.class)){
						for(int j = ((Boss)entities.get(i)).bullets.size()-1; j >= 0; j--){
							((Boss)entities.get(i)).bullets.get(j).move();
							if(collide(((Boss)entities.get(i)).bullets.get(j),((Player)entities.get(k)))){
								roundStart();
								((Boss)entities.get(i)).bullets.remove(((Boss)entities.get(i)).bullets.get(j));
							}
						}
					}
				}
			}
		}
	}
	public boolean collide(Entity e, Entity e1){
		//e is being collided with e1		
		if(distance(e,e1) < (e.size.getX()/2) + (e1.size.getX()/2)){
			//System.out.println(e.name + " " + e.getCenter() + "\n" +e1.name + " " + e1.getCenter());
			return true;
		}
		return false;
	}
	public double distance(Entity e, Entity e1){
		double dist =  Math.sqrt(Math.pow((e.getCenter().getX() - e1.getCenter().getX()),2) + Math.pow((e.getCenter().getY() - e1.getCenter().getY()),2));
		//System.out.println(dist);
		return dist;
	}
	public void update(Graphics g){
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getClass().equals(Player.class)){
				((Player) entities.get(i)).update(); //Smoother movement.
			}
			else if(entities.get(i).getClass().equals(Boss.class)){
				((Boss)entities.get(i)).update();
			}
		}
		collideCheck();
		paint(g);
	}
	public void roundStart(){
		entities.clear();
		entities.add(new Player());
		entities.add(new Boss());
		//		temp = new Player();											//Init player (bad)
		//		boss = new Boss();												//Inits boss (bad)
		timer.start();
	}
	public void roundEnd(){
		timer.stop();
	}
	public static void addObserver(Observer o){
		ObserverList.add(o);
	}
	public void removeObserver(Observer o){
		ObserverList.remove(o);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		for(int i = 0; i < entities.size(); i++){
			if(!outOfBounds(entities.get(i))){
				if(entities.get(i).getClass().equals(Player.class)){
					((Player) entities.get(i)).move(); //Smoother movement.
				}
				else if(entities.get(i).getClass().equals(Boss.class)){
					((Boss)entities.get(i)).randomMove();
					((Boss)entities.get(i)).spiral(2);
				}
			}
		}
		repaint();
	}
	public static boolean outOfBounds(Entity e){
		if(e.location.getX() < 0){
			e.location = new Point2D.Double(e.location.getX()+1,e.location.getY());
			return true;
		}
		if(e.location.getX() + e.size.getX() > PLAY_FIELD_SIZE.getX()){
			e.location = new Point2D.Double(e.location.getX()-1,e.location.getY());
			return true;
		}
		if(e.location.getY() < 0){
			e.location = new Point2D.Double(e.location.getX(),e.location.getY()+1);
			return true;
		}
		if(e.location.getY() + e.size.getY() > PLAY_FIELD_SIZE.getY()){
			e.location = new Point2D.Double(e.location.getX(),e.location.getY()-1);
			return true;
		}
		return false;
	}
	public void keyPressed(KeyEvent e) {
		// Notifies all observers that a key is pressed
		notifyObservers(e, true);
	}
	public void keyReleased(KeyEvent e) {
		// Notifies all observers that a key is released
		notifyObservers(e, false);
	}
	public void keyTyped(KeyEvent arg0){ /*Junk method*/ }
	public void notifyObservers(KeyEvent keyevent, boolean pressed) {
		// Notifies all observers with keyevent and keystate
		for(Observer o: ObserverList){
			o.update(keyevent, pressed);
		}
	}
}
