//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Player extends Entity implements Observer{
	private final int FAST = 4;			//Constants for the players movement speed
	private final int SLOW = 1;			//Fast and slow
	private final Point2D BULLET_SIZE = new Point2D.Double(10,10);					//Size for players bullets
	private boolean controls[] = {false,false,false,false,false,false,false,false};	//Array for player's active controls
	private int keyBinding[];														//List of keybindings
	private double cooldown;			//Cooldown for bomb
	private double COOLDOWN;			//"Constant" for bomb cooldown
	private int speed;					//Player's current speed
	Color COLOR;						//Player's original color
	private Color cooldownColor;		//Player's color when the bomb is on cooldown
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();	//List of all the player's bullets
	Shield shield;						//Player's shield
	MainClass main;						//Passing the instance of the main class through
	public Player(MainClass m){
		name = "player";				//Constructor stuff
		speed = FAST;					//
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()+10);	//Starts the player at the bottom center of the screen
		size = new Point2D.Double(15,15);//
		COLOR = Color.red;				//
		color = COLOR;					// More constructor stuff
		cooldownColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());				//Inverts color for player
		MainClass.addObserver(this);	//Adds the player as an observer(for key presses)
		keyBinding = new int[controls.length];	//Initializes the keybindings	
		keyBinding[0]=KeyEvent.VK_W;		//Forward
		keyBinding[1]=KeyEvent.VK_S;		//Back
		keyBinding[2]=KeyEvent.VK_A;		//Left
		keyBinding[3]=KeyEvent.VK_D;		//Right
		keyBinding[4]=KeyEvent.VK_PERIOD;	//Shoot
		keyBinding[5]=KeyEvent.VK_COMMA;	//Shield
		keyBinding[6]=KeyEvent.VK_SHIFT;	//Bomb
		keyBinding[7]=KeyEvent.VK_SLASH;	//Percise movement
		COOLDOWN = (int)(1500000/m.difficulty);//The cooldown of the bomb is based on the difficulty
		cooldown = 0;
		shield = new Shield(this,20,200);		//Declares sheild with cooldown and size
		main = m;								//MainClass instance
	}
	public void bomb(){
		if(cooldown == 0){					//If the bomb isn't on cooldown use it
			cooldown = COOLDOWN;			//Then set it on cooldown
			main.bomb();
		}
	}
	public void keyUpdate(KeyEvent keyevent, boolean pressed) {
		for(int i = 0; i < keyBinding.length; i++){
			if(keyevent.getKeyCode() == keyBinding[i]){
				controls[i] = pressed;		//If the keypressed is relevant set it's state to pressed/not
			}
		}
	}
	public void move(){
		if(cooldown > 0 && MainClass.isPlaying){	//if it's on cooldown
			color=cooldownColor;					//Change color to the cooldown color
			cooldown--;								//Count down the cooldown
		}
		for(int i = 0; i < controls.length; i++){
			if(controls[i]){	//If the key is pressed do the thing that's supposed to be done
				switch(i){
				case 0: location = new Point2D.Double(location.getX(),location.getY()-speed); break;
				case 1: location = new Point2D.Double(location.getX(),location.getY()+speed); break;
				case 2: location = new Point2D.Double(location.getX()-speed,location.getY()); break;
				case 3: location = new Point2D.Double(location.getX()+speed,location.getY()); break;
				case 4: shield.activate(true); break;
				case 5: shoot(); break;
				case 6: speed = SLOW; break;
				case 7: bomb(); break;
				}
			}
		}
		//Some things for specific keys that do things when released
		if(cooldown == 0){
			color = COLOR;
		}
		if(!controls[4]){
			shield.activate(false);
		}
		if(!controls[6]){
			speed = FAST;
		}
	}
	private void shoot(){	//Creates bullets for shooting
		Bullet b = new Bullet(new Point2D.Double(getCenter().getX()-(BULLET_SIZE.getX()/2),getCenter().getY()-(BULLET_SIZE.getY()/2)), BULLET_SIZE, COLOR, 10, 90);
		bullets.add(b);
	}
	public void update(){	//Updates bullets and makes sure they aren't out of bounds
		for(int i = bullets.size()-1; i >= 0; i--){
			bullets.get(i).move();
			if(MainClass.outOfBounds(bullets.get(i))){
				bullets.remove(bullets.get(i));
			}
		}
	}
	public Image shieldDraw(){ 
		shield.update();	//Draws and updates the shield
		if(shield.enabled){
			buffer.drawImage(shield.draw(image,buffer),0,0,null);
		}
		return image;
	}
	public String coolDownFormat(){
		if(cooldown > 0){	//Formats the cooldown for the bomb nicely
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			return "Bomb Cooldown: " + df.format(cooldown/100) + " s";
		}
		else{
			return "Bomb Ready";
		}
	}
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked) {	}
}
