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
/** TODO:
 	-Assets
 	-Comment
 	-Volatile
 **/
public class MainClass extends Applet implements ActionListener, KeyListener, MouseListener, MouseMotionListener, Observable{
	Timer timer = new Timer(10, this);									//Declare timer and init
	Image offscreen;													//Double buffer image **
	Graphics buffer;													//Double buffer drawer **
	Image menuImage;													//Separate image for menu 
	private static ArrayList<Observer> ObserverList = new ArrayList<Observer>();	//Declare and init Observer list
	static Point2D PLAY_FIELD_SIZE;										//Supposed to be a constant, but can't actually set this until i set the size
	private ArrayList<Entity> entities = new ArrayList<Entity>();				//Creates a list of all entities (players and bosses) for generalization purposes (co-op maybe)
	MainMenu menu;														//Declaring a new menu 
	MainMenu end;
	MainMenu instructions;
	MainMenu options;
	public static boolean isPlaying;									//Also janky, but when the game should be running
	Image bossState1;
	Image bossState2;
	Image bossState3;
	int difficulty = 100;
	final int pathetic = 400;
	final int easy = 100;
	final int medium = 99;
	private ArrayList<MainMenu> menus = new ArrayList<MainMenu>();
	public void init(){		
		//Adding listeners
		bossState1 = getImage(getDocumentBase(),"Boss.png");
		bossState2 = getImage(getDocumentBase(),"Boss1.png");
		bossState3 = getImage(getDocumentBase(),"Boss2.png");
		this.addKeyListener(this);									
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setSize(3000,1800);										//Set window size
		PLAY_FIELD_SIZE  = new Point2D.Double(this.getWidth()/4*3, this.getHeight());	//Sets a "constant" (not really because things)
		offscreen = createImage(this.getWidth(),this.getHeight());		//Initialized the buffer image
		buffer = offscreen.getGraphics();								//Sets the buffer to draw on offscreen
		menus.add(menu = new MainMenu(0, menuImage,buffer,!isPlaying));				//Initializing the menu
		menus.add(end = new MainMenu(-1, menuImage,buffer,isPlaying));					//Initializing the end game
		menus.add(instructions = new MainMenu(1,menuImage,buffer,false));
		menus.add(options = new MainMenu(2, menuImage,buffer,false));
		timer.start();													//Starts the timer
		roundStart();													//Starts the game
		pause();														//Pauses the game
		/**Adds gui stuff*/
		menu.addText("Main Menu",  new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/8), MainMenu.dFont, Color.white);
		menu.addButton(0, "New Game", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/4), MainMenu.dFont,Color.red);
		menu.addButton(1, "Resume", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()*3/8), MainMenu.dFont, Color.red);
		menu.addButton(2, "Instructions", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/2), MainMenu.dFont, Color.red);
		menu.addButton(3, "Options", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()*5/8), MainMenu.dFont, Color.red);
		Font intstructionFont = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/25));
		instructions.addText("You are the red dot, avoid getting hit. Kill the boss to win", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/8), intstructionFont, Color.white);
		instructions.addText("I will make rebindable keys later lol,", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/4), intstructionFont, Color.white);
		instructions.addText("Go to options to change difficutly if you are patrick adn dubmb", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()*3/8), intstructionFont, Color.white);
		instructions.addButton(0, "Back", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/2), MainMenu.dFont, Color.red);
		options.addText(diffUpdate(), new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/8), MainMenu.dFont, Color.white);
		options.addButton(0, "pathetic", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 4, MainClass.PLAY_FIELD_SIZE.getY()/4), MainMenu.dFont, new Color(255,216,216));
		options.addButton(1, "easy", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/4), MainMenu.dFont, new Color(255,120,120));
		options.addButton(2, "medium", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX()*3/4, MainClass.PLAY_FIELD_SIZE.getY()/4), MainMenu.dFont, new Color(255,30,30));
		options.addButton(3, "Back", new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()*3/8), MainMenu.dFont, Color.red);
	}
	public String diffUpdate(){
		String s = "Difficulty: ";
		switch(difficulty){
		case pathetic: s += "pathetic"; break;
		case easy: s += "easy"; break;
		case medium: s += "medium"; break;
		}
		return s;
	}
	public void menuUpdate(){
		for(MainMenu m: menus){
			if(m.enabled){
				for(Button b: m.updater()){
					if(b.isClicked){
						switch(m.number){
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
							case 0: difficulty = pathetic; break;
							case 1: difficulty = easy; break;
							case 2: difficulty = medium; break;
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
	public void paint(Graphics g){
		//Does all the graphics stuff
		//Draws everything to the buffer, then draws the buffer on screen
		buffer.setFont(new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/35)));
		buffer.setColor(Color.black);								//Sets color of playfield to black
		buffer.fillRect(0, 0, (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY());	//Draws the playfeild
		buffer.setColor(Color.white);
		buffer.fillRect((int)PLAY_FIELD_SIZE.getX(), 0,  this.getWidth(), (int)PLAY_FIELD_SIZE.getY());
		for(int i = 0; i < entities.size(); i++){
			//Draws the proper circle with color for each player/boss
			buffer.drawImage(entities.get(i).draw(offscreen, buffer), 0, 0, this);
		}		
		for(int i = 0; i < entities.size(); i++){
			//Draws the bullets for the players and bosses
			//2 loops because player and boss have separate instances of bulletLists
			//Super class doesn't have a bullet list because bullet inherits it
			if(entities.get(i).getClass().equals(Player.class)){
				((Player)entities.get(i)).shieldDraw();
				buffer.setColor(Color.black);
				buffer.drawString(((Player)entities.get(i)).shield.coolDownFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()/10);
				buffer.drawString(((Player)entities.get(i)).coolDownFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()*3/20);
				for(Bullet b: ((Player)entities.get(i)).bullets){									
					buffer.setColor(b.color);
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
			else if(entities.get(i).getClass().equals(Boss.class)){
				buffer.setColor(Color.black);
				/**//***//**//***//**//***//**//***//**//***//**//***//**//***//**//***//**//***/
				buffer.drawString("Boss health: " + ((Boss)entities.get(i)).hpFormat(), (int)PLAY_FIELD_SIZE.getX(), (int)PLAY_FIELD_SIZE.getY()/20);
				/**//***//**//***//**//***//**//***//**//***//**//***//**//***//**//***//**//***/
				buffer.drawImage(((Boss)entities.get(i)).currentState,
						(int)((Boss)entities.get(i)).location.getX(),(int)((Boss)entities.get(i)).location.getY(),
						(int)((Boss)entities.get(i)).size.getX(),(int)((Boss)entities.get(i)).size.getY(),this);
				for(Bullet b: ((Boss)entities.get(i)).bullets){									
					buffer.setColor(((Boss)entities.get(i)).color);
					buffer.fillOval((int)b.location.getX(), (int)b.location.getY(), (int)b.size.getX(), (int)b.size.getY());
				}
			}
		}
		for(MainMenu m: menus){
			if(m.enabled){
				m.draw();
			}
		}	
		g.drawImage(offscreen,0,0,this);							//Draws the buffered image onto the screen
	}
	public void bomb(){
		for(int k = 0; k < entities.size(); k++){
			if(entities.get(k).getClass().equals(Player.class)){
				for(int i = 0; i < entities.size(); i++){
					if(entities.get(i).getClass().equals(Boss.class)){
						for(int j = ((Boss)entities.get(i)).bullets.size()-1; j >= 0; j--){
							((Boss)entities.get(i)).bullets.remove(0);
						}
					}
				}
			}
		}
	}
	public void collideCheck(){
		//(Checks only for player vs boss bullets) and (boss vs player bullets)
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
		//Removes everything, inits boss and players, and resumes the game
		entities.clear();
		//Inits our boss and player
		entities.add(new Player(this));
		entities.add(new Boss(bossState1,bossState2,bossState3, difficulty));
		if(difficulty == 99){
			entities.add(new Boss(bossState1,bossState2,bossState3, difficulty));
		}
		resume();
	}
	public void resume(){
		isPlaying = true;
		menu.enabled = false;
	}
	public void pause(){
		isPlaying = false;
		menu.enabled = true;
	}
	public static void addObserver(Observer o){
		ObserverList.add(o);
	}
	public void removeObserver(Observer o){
		ObserverList.remove(o);
	}
	public void actionPerformed(ActionEvent e) {
		//Makes sure the entities aren't out of bounds
		if(isPlaying){
			for(int i = 0; i < entities.size(); i++){
				if(!outOfBounds(entities.get(i))){
					//Players can only move if they are not moving out of bounds
					if(entities.get(i).getClass().equals(Player.class)){
						((Player) entities.get(i)).move(); //Smoother movement.
					}
					else if(entities.get(i).getClass().equals(Boss.class)){
						//Random boss stuff idk for now
						((Boss)entities.get(i)).fight();
						//((Boss)entities.get(i)).spiral(2);
					}
				}
			}
		}
		repaint();		//Redrawing stuff
	}
	public static boolean outOfBounds(Entity e){
		//Checks if the entity is within the play field
		//(wall collision is really bad in java so this is a janky "bump" solution, where it
		//bounces the entity off the wall. (helps prevent entities from getting stuck in walls)
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