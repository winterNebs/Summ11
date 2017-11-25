//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
//NOTE: CHANGE "RESOLUTION" TO WHAT YOUR MONITOR RESOLUTION IS
//APPLETS ARE MEANT TO RUN IN BROWSER SO THE SIZE IS USUALLY FIXED AND CAN BE RESIZED BY ZOOMING IN/OUT 
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;

interface Observer{
	//Receiver for notifications
	public void keyUpdate(KeyEvent keyevent, boolean pressed);
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked);
}
interface Observable{
	//Sender for notifications
	public void notifyObservers(KeyEvent keyevent, boolean pressed);
	public void notifyObservers(MouseEvent mouseevent, boolean clicked);
}
public class MainClass extends Applet implements ActionListener, KeyListener, MouseListener, MouseMotionListener, Observable{
	final Point2D RESOLUTION = new Point2D.Double(1280,720);
	private static final long serialVersionUID = 1L;
	volatile Timer timer = new Timer(10, this);		//Declare timer and init
	volatile Image offscreen;						//Double buffer image **
	volatile Graphics buffer;						//Double buffer drawer **
	Image menuImage;								//Separate image for menu 
	private static ArrayList<Observer> ObserverList = new ArrayList<Observer>();	//Declare and init Observer list
	static Point2D PLAY_FIELD_SIZE;					//Supposed to be a constant, but can't actually set this until i set the size
	private volatile ArrayList<Entity> entities = new ArrayList<Entity>();			//Creates a list of all entities (players and bosses) for generalization purposes (co-op maybe)
	MainMenu menu;									//Declaring Menu Stuff
	MainMenu end;									//
	MainMenu instructions;							//
	MainMenu options;								//Declaring Menu Stuff
	public static boolean isPlaying;				//Whether the game is running or not
	Image bossState1;								//The images for the boss
	Image bossState2;
	Image bossState3;
	final int pathetic = 400;						//Constant values representing the difficulty
	final int easy = 100;							//
	final int medium = 50;							//Lower is harder
	final int COUNTDOWN = 360;						//Count down constants for when you win
	int difficulty = pathetic;						//Default difficulty
	int countDown = COUNTDOWN;						//Countdown	
	private ArrayList<MainMenu> menus = new ArrayList<MainMenu>();					//Lists of all menus
	public void init(){	
		bossState1 = getImage(getDocumentBase(),"Boss.png");		//Setting the assets for the boss
		bossState2 = getImage(getDocumentBase(),"Boss1.png");		//
		bossState3 = getImage(getDocumentBase(),"Boss2.gif");		//Can only do it here for some reason
		this.addKeyListener(this);					//Adding listeners				
		this.addMouseListener(this);				//
		this.addMouseMotionListener(this);			//
		this.setSize((int)RESOLUTION.getX(),(int)RESOLUTION.getY());					//Setting window size
		PLAY_FIELD_SIZE  = new Point2D.Double(this.getWidth()/4*3, this.getHeight());	//Sets a "constant" for the play field size
		offscreen = createImage(this.getWidth(),this.getHeight());						//Initialized the buffer image
		buffer = offscreen.getGraphics();												//Sets the buffer to draw on offscreen (for double buffering)
		menus.add(menu = new MainMenu(0, menuImage,buffer,!isPlaying));		//Initializing the menus
		menus.add(end = new MainMenu(-1, menuImage,buffer,isPlaying));		//
		menus.add(instructions = new MainMenu(1,menuImage,buffer,false));	//
		menus.add(options = new MainMenu(2, menuImage,buffer,false));		//
		timer.start();								//Starts the timer
		roundStart();								//Starts the game
		pause();									//Pauses the game
		/**Adds gui stuff*/
		menu.addText("Main Menu",  MainMenu.locationFormat(PLAY_FIELD_SIZE, 0), MainMenu.dFont, Color.white);
		menu.addButton(0, "New Game", MainMenu.locationFormat(PLAY_FIELD_SIZE, 1), MainMenu.dFont,Color.red);
		menu.addButton(1, "Resume", MainMenu.locationFormat(PLAY_FIELD_SIZE, 2), MainMenu.dFont, Color.red);
		menu.addButton(2, "Instructions", MainMenu.locationFormat(PLAY_FIELD_SIZE, 3), MainMenu.dFont, Color.red);
		menu.addButton(3, "Options", MainMenu.locationFormat(PLAY_FIELD_SIZE, 4), MainMenu.dFont, Color.red);
		Font intstructionFont = new Font("TimesRoman", Font.PLAIN, (int)(PLAY_FIELD_SIZE.getX()/25));
		instructions.addText("You are the red dot, avoid getting hit. Kill the boss to win", MainMenu.locationFormat(PLAY_FIELD_SIZE, 0), intstructionFont, Color.white);
		instructions.addText("WASD to move, comma to shoot, period to sheild,", MainMenu.locationFormat(PLAY_FIELD_SIZE, 1), intstructionFont, Color.white);
		instructions.addText("slash to use bomb, shift for more percice movement.", MainMenu.locationFormat(PLAY_FIELD_SIZE, 2), intstructionFont, Color.white);
		instructions.addButton(0, "Back", MainMenu.locationFormat(PLAY_FIELD_SIZE, 3), MainMenu.dFont, Color.red);
		options.addText(diffUpdate(), MainMenu.locationFormat(PLAY_FIELD_SIZE, 0), MainMenu.dFont, Color.white);
		options.addButton(0, "pathetic",  MainMenu.locationFormat(PLAY_FIELD_SIZE, 0, 1), MainMenu.dFont, new Color(255,216,216));
		options.addButton(1, "easy", MainMenu.locationFormat(PLAY_FIELD_SIZE, 1, 1), MainMenu.dFont, new Color(255,120,120));
		options.addButton(2, "medium", MainMenu.locationFormat(PLAY_FIELD_SIZE, 2, 1), MainMenu.dFont, new Color(255,30,30));
		options.addButton(3, "Back", MainMenu.locationFormat(PLAY_FIELD_SIZE, 2), MainMenu.dFont, Color.red);
		end.addText("You beat the boss on: ", MainMenu.locationFormat(PLAY_FIELD_SIZE, 0), MainMenu.dFont, Color.white);	
		end.addText(diffUpdate(), MainMenu.locationFormat(PLAY_FIELD_SIZE, 1), MainMenu.dFont, Color.white);	
		end.addButton(0, "New Game", MainMenu.locationFormat(PLAY_FIELD_SIZE, 2), MainMenu.dFont,Color.red);
	}
	public String diffUpdate(){		//Returns the diffiuclty formated nicely
		String s = "Difficulty: ";		
		switch(difficulty){
		case pathetic: s += "pathetic"; break;
		case easy: s += "easy"; break;
		case medium: s += "medium"; break;
		}
		return s;
	}
	public void menuUpdate(){				//Updates the menus and handles the clicking for the buttons
		for(MainMenu m: menus){				//Goes through each menu
			if(m.enabled){					//Checks if they are enabled(shown)
				for(Button b: m.updater()){	//Checks all the buttons in the menu
					if(b.isClicked){		//Checks if it's clicked
						switch(m.number){	//If it's clicked do whatever action corrisponds to the button
						case -1:
							switch(b.number){
							case 0: menu.enabled = true; end.enabled = false;	
							}
						case 0:
							switch(b.number){
							case 0: roundStart();break;
							case 1: resume(); break;
							case 2: menu.enabled = false; instructions.enabled = true;break;
							case 3: menu.enabled = false; options.enabled = true; break;
							}
							break;
						case 1:
							switch(b.number){
							case 0: instructions.enabled = false; menu.enabled = true;break;
							}
							break;
						case 2:
							switch(b.number){
							case 0: difficulty = pathetic; newRound(); break;
							case 1: difficulty = easy; newRound(); break;
							case 2: difficulty = medium; newRound(); break;
							case 3: options.enabled = false; menu.enabled = true; break;
							}
							m.updateText(0, diffUpdate());
							break;
						}
					}
				}
			}
		}
	}
	public void paint(Graphics g){		//Graphics stuff
		buffer.setFont(new Font("TimesRoman", Font.PLAIN, (int)(PLAY_FIELD_SIZE.getX()/35)));			//Sets a default font
		buffer.setColor(Color.black);																	//Sets color of playfield to black
		buffer.fillRect(0, 0, (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY());				//Draws the playfeild
		buffer.setColor(Color.white);																	//Sets the color of the little bar on the right to white
		buffer.fillRect((int)PLAY_FIELD_SIZE.getX(), 0,  this.getWidth(), (int)PLAY_FIELD_SIZE.getY());	//Draws the background for the right panel
		buffer.setColor(Color.black);
		buffer.drawString("Isaac Wen 2017",(int)PLAY_FIELD_SIZE.getX(),(int)PLAY_FIELD_SIZE.getY()*99/100);//Name and stuff
		for(int i = 0; i < entities.size(); i++){					
			if(entities.get(i).getClass().equals(Player.class)){
				buffer.drawImage(entities.get(i).draw(offscreen, buffer), 0, 0, this);					//Draws the proper circle with color for each player/boss		
			}		//Doesn't draw boss because it has its own image
		}		
		for(int i = 0; i < entities.size(); i++){
			//Draws the bullets for the players and bosses
			//2 loops because player and boss have separate instances of bulletLists
			//Super class doesn't have a bullet list because bullet inherits it
			if(entities.get(i).getClass().equals(Player.class)){
				((Player)entities.get(i)).shieldDraw();		//Draws the shield if active
				buffer.setColor(Color.black);				//Draws the cooldown for the sheild
				buffer.drawString(((Player)entities.get(i)).shield.coolDownFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()/10);
				buffer.drawString(((Player)entities.get(i)).coolDownFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()*3/20);
				for(Bullet b: ((Player)entities.get(i)).bullets){									
					buffer.setColor(b.color);				//Draw bullets
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
			else if(entities.get(i).getClass().equals(Boss.class)){
				if(((Boss)entities.get(i)).health <=0){		//Countdown timer for defeating the boss
					countDown--;
				}
				if(countDown <= 0){
					isPlaying = false;
					end.enabled = true;
				}
				buffer.setColor(Color.black);							//Displaying boss's health
				buffer.drawString("Boss health: " + ((Boss)entities.get(i)).hpFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()/20);
				buffer.drawImage(((Boss)entities.get(i)).currentState, //Draws the boss's current state
						(int)((Boss)entities.get(i)).location.getX(),(int)((Boss)entities.get(i)).location.getY(),
						(int)((Boss)entities.get(i)).size.getX(),(int)((Boss)entities.get(i)).size.getY(),this);
				for(Bullet b: ((Boss)entities.get(i)).bullets){			//Draws bullets						
					buffer.setColor(((Boss)entities.get(i)).color);
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
		}
		for(MainMenu m: menus){
			if(m.enabled){
				m.draw();					//Draws the enabled menus
			}
		}	
		g.drawImage(offscreen,0,0,this);	//Draws the buffered image onto the screen
	}
	public void bomb(){						
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getClass().equals(Boss.class)){
				((Boss)entities.get(i)).health-=difficulty*2;	//Does damage to boss based on difficulty
				for(int j = ((Boss)entities.get(i)).bullets.size()-1; j >= 0; j--){
					((Boss)entities.get(i)).bullets.remove(0);	//Deletes all boss's bullets
				}
			}
		}
	}
	public void collideCheck(){
		//(Checks only for player vs boss bullets) and (boss vs player bullets)
		//If hit end game/damage boss
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
							if(((Player)entities.get(k)).shield.enabled){
								if(collide(((Boss)entities.get(i)).bullets.get(j),((Player)entities.get(k)).shield)){
									((Boss)entities.get(i)).bullets.remove(((Boss)entities.get(i)).bullets.get(j));
									((Player)entities.get(k)).shield.hit();
								}
							}
							else{
								if(collide(((Boss)entities.get(i)).bullets.get(j),((Player)entities.get(k)))){
									((Boss)entities.get(i)).bullets.remove(((Boss)entities.get(i)).bullets.get(j));
									roundStart();
									pause();
									break;
								}
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
		//Updates everything (I think this is also called when repaint is called)
		if(isPlaying){
			//When the game is supposed to be running update everything
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
		}
		paint(g);
	}
	public void roundStart(){
		newRound();
		resume();						//resume the game
	}
	public void newRound(){
		entities.clear();				//Deletes everything
		entities.add(new Player(this));	//Adds boss and players
		entities.add(new Boss(bossState1,bossState2,bossState3, difficulty));
		countDown = COUNTDOWN;
	}
	public void resume(){
		isPlaying = true;		//Resume the game
		for(MainMenu m: menus){
			m.enabled = false;	//Disables all menus
		}
	}
	public void pause(){
		isPlaying = false;		//Pauses game
		menu.enabled = true;	//Enables the main menu
	}
	public static void addObserver(Observer o){
		ObserverList.add(o);	//Adds observers 
	}
	public void removeObserver(Observer o){
		ObserverList.remove(o);	//Removes observers
	}
	public void actionPerformed(ActionEvent e) {
		//Makes sure the entities aren't out of bounds
		if(isPlaying){
			for(int i = 0; i < entities.size(); i++){
				if(!outOfBounds(entities.get(i))){
					//Players can only move if they are not moving out of bounds
					if(entities.get(i).getClass().equals(Player.class)){
						((Player) entities.get(i)).move();	//Smoother movement.
					}
					else if(entities.get(i).getClass().equals(Boss.class)){
						((Boss)entities.get(i)).fight();	//This is all the bosses movement and shooting
					}
				}
			}
		}
		repaint();		//Redrawing stuff
	}
	public static boolean outOfBounds(Entity e){
		//Checks if the entity is within the play field
		//Entities "bounce" off of walls
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
		//Toggles pause/play when escape is press
		if(e.getKeyCode() == 27){
			if(isPlaying){
				pause();
			}
			else{
				resume();
			}
		}
		//Notifies all Observers that a key is pressed
		notifyObservers(e, true);
	}
	public void keyReleased(KeyEvent e) {
		//Notifies all Observers that a key is released
		notifyObservers(e, false);
	}
	public void notifyObservers(KeyEvent keyevent, boolean pressed) {
		//Notifies all Observers with keyevent and keystate
		for(Observer o: ObserverList){
			o.keyUpdate(keyevent, pressed);
		}
	}
	public void notifyObservers(MouseEvent mouseevent, boolean clicked) {
		//Same thing but for mouse stuff
		for(Observer o: ObserverList){
			o.mouseUpdate(mouseevent, clicked);
		}
	}
	public void mousePressed(MouseEvent e) {
		notifyObservers(e,true);
		menuUpdate();
	}
	public void mouseReleased(MouseEvent e) {
		notifyObservers(e,false);
	}
	public void mouseMoved(MouseEvent e) {
		notifyObservers(e, false);
	}
	/**Junk methods that come with the listeners*/
	public void keyTyped(KeyEvent arg0){}
	public void mouseDragged(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}	
}