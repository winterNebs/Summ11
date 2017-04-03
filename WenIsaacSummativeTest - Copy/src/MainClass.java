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
	- Main Menu
 **/
public class MainClass extends Applet implements ActionListener, KeyListener, Observable{
	Timer timer = new Timer(10, this);									//Declare timer and init
	Image offscreen;													//Double buffer image **
	Graphics buffer;													//Double buffer drawer **
	private static ArrayList<Observer> ObserverList = new ArrayList<Observer>();	//Declare and init observer list
	static Point2D PLAY_FIELD_SIZE;										//Supposed to be a constant, but can't actually set this until i set the size)
	private ArrayList<Entity> entities = new ArrayList();				//Creates a list of all entities (players and bosses) for generalization purposes (co-op maybe)
	public void init(){		
		this.addKeyListener(this);										//Adds a keylistener
		this.setSize(2500,1500);											//Set window size
		PLAY_FIELD_SIZE  = new Point2D.Double(this.getWidth()/4*3, this.getHeight());	//Sets a "constant" (not really because things)
		offscreen = createImage(this.getWidth(),this.getHeight());		//Initialized the buffer image
		buffer = offscreen.getGraphics();								//Sets the buffer to draw on offscreen
		roundStart();													//Starts the game
	}
	public void paint(Graphics g){
		buffer.setColor(Color.black);									//Sets color of playfield to black
		buffer.fillRect(0, 0, (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY());	//Draws the playfeild
		for(int i = 0; i < entities.size(); i++){
			//Draws the proper circle with color for each player/boss
			buffer.setColor(entities.get(i).color);
			buffer.fillOval((int)entities.get(i).location.getX(),(int) entities.get(i).location.getY(), (int)entities.get(i).size.getX(), (int)entities.get(i).size.getY());
		}		
		for(int i = 0; i < entities.size(); i++){
			//Draws the bullets for the players and bosses
			//2 loops because player and boss have separate instances of bulletLists
			//Super class doesn't have a bullet list because bullet inherits it
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
		if(!timer.isRunning()){
			buffer.setColor(Color.white);
			buffer.setFont(new Font("TimesRoman", Font.PLAIN, 300)); 
			buffer.drawString("Paused", 500, 500);
		}
		g.drawImage(offscreen,0,0,this);								//Draws the buffered image onto the screen
	}
	public void collideCheck(){
		//Checks only for player vs boss bullets and boss vs player bullets
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
		//BASICALLY: checks if the distance between 2 objects is less than the radius of both objects combined (are touching)
		//e is being collided with e1	
		if(distance(e,e1) < (e.size.getX()/2) + (e1.size.getX()/2)){
			return true;
		}
		return false;
	}
	public double distance(Entity e, Entity e1){
		//Does pythagoras theorem to find the distance between 2 points
		double dist =  Math.sqrt(Math.pow((e.getCenter().getX() - e1.getCenter().getX()),2)
				+ Math.pow((e.getCenter().getY() - e1.getCenter().getY()),2));
		return dist;
	}
	public void update(Graphics g){
		//Updates everything
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getClass().equals(Player.class)){
				((Player) entities.get(i)).update(); //For smoother movement.
			}
			else if(entities.get(i).getClass().equals(Boss.class)){
				((Boss)entities.get(i)).update();
			}
		}
		//Checks for collisions and then redraws everything
		collideCheck();
		paint(g);
	}
	public void roundStart(){
		//Removes everything
		entities.clear();
		//Inits our boss and player
		entities.add(new Player());
		entities.add(new Boss());
		//Starts our timer
		timer.start();
	}
	public void resume(){
		timer.start();
	}
	public void pause(){
		timer.stop();

		//paint(buffer);
		repaint();
	}
	public static void addObserver(Observer o){
		ObserverList.add(o);
	}
	public void removeObserver(Observer o){
		ObserverList.remove(o);
	}
	public void actionPerformed(ActionEvent e) {
		//Makes sure the entities aren't out of bounds
		for(int i = 0; i < entities.size(); i++){
			if(!outOfBounds(entities.get(i))){
				if(entities.get(i).getClass().equals(Player.class)){
					((Player) entities.get(i)).move(); //Smoother movement.
				}
				else if(entities.get(i).getClass().equals(Boss.class)){
					//Does some random shooty patterns for now
					((Boss)entities.get(i)).randomMove();
					((Boss)entities.get(i)).spiral(2);
				}
			}
		}
		//Redraws (repaint calls update)
		repaint();
	}
	public static boolean outOfBounds(Entity e){
		//Checks if the entity is within the play field
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
		//Escape
		if(e.getKeyCode() == 27){
			if(timer.isRunning()){
				pause();
			}
			else{
				resume();
			}
		}
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
