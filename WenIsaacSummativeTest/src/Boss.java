//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Entity{
	public int health;						//Health
	private int maxHealth;					//Max health
	private int spiral;						//Current number for spiral (TODO: Arraylist for exapndablility)
	private int speed = 10;					//Speed
	private int straightShotBullets =  0;	//Bullets left in the straight shot
	private int straightShotAngle = 0;		//Current angle
	int difficulty;							//Current difficulty
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();	//All the bosses bullets
	List<List<Point2D>> lineLocation = new ArrayList<List<Point2D>>();	//Locations for the vertial line shots
	private Image states[] = new Image[3];	//Image states for boss
	public Image currentState;				//The current image state
	public Boss(Image a, Image b, Image c, int d){
		color = Color.white;				//Constructor stuff
		name = "boss";						//
		health = (int)(1000000/d);			//Heath is based on difficulty
		maxHealth = health;					//
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, 0);	//Spawns boss in top center of screen
		size = new Point2D.Double(200,200);	//Size
		states[0] = a;						//States
		states[1] = b;						//
		states[2] = c;						//
		currentState = states[0];			//
		difficulty = d;						//
	}
	public void fight(){
		if(alive()){									//Does stuff if alive
			int angle = 0;		//Direction to move
			int rnd = (int)(Math.random()*difficulty);	//Random number for attack
			if(location.getY() > (MainClass.PLAY_FIELD_SIZE.getY()/4)){					
				angle = (int)(Math.random()*180);		//Moves upwards if its too close to the bottom
			}
			else{
				angle = (int)(Math.random()*360);		//Otherwise move randomly
			}
			if(hpLeft() >= 90){							//If full heath move slowly and use easy attacks
				speed = 5;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(2,10);break;
				case 3: ring(2,10);break;
				}
			}
			else if(hpLeft() >= 80){					//If 90-80 move faster and use more attacks
				speed = 7;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(1,15);break;
				case 3: ring(2,10);break;
				case 4: ring(1,15);break;
				}
			}
			else if(hpLeft() >= 75){					//etc etc
				speed = 10;
				switch(rnd){
				case 1: ring(1,80);break;
				case 2: ring(2,15);break;
				}
				line(1,30,3,5);
			}
			else if(hpLeft() >= 70){
				speed = 10;
				switch(rnd){
				case 1: ring(1,20);break;
				case 2: ring(2,15);break;
				}
				line(1,30,3,5);
			}
			else if(hpLeft() >= 60){
				speed = 10;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(1,15);break;
				case 3: ring(2,20);break;
				}
				line(2,20,3,5);
				spiral(2,10,0);
			}
			else if(hpLeft() >= 40){
				speed = 12;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(2,15);break;
				case 3: ring(1,20);break;
				}
				spiral(2,10,0);
				line(4,20,3,5);
				straightShot(5,(int)(Math.random()*180)-180,500/difficulty,30);
			}
			else if(hpLeft() >= 5){
				speed = 15;
				switch(rnd){
				case 1: ring(2,15);break;
				case 2: ring(1,20);break;
				case 3: ring(1,25);break;
				}
				spiral(2,20,0);
				spiral(2,20,180);
				straightShot(5,(int)(Math.random()*180)-180,30,5);
				line(5,20,3,5);
			}
			else{
				speed = 25;
				switch(rnd){
				case 1: ring(2,40);break;
				case 2: ring(1,30);break;
				case 3: ring(1,30);break;
				case 4: ring(3,20);break;
				case 5: ring(3,20);break;
				case 6: ring(4,15);break;
				}
				spiral(2,20,0);
				spiral(2,20,120);
				spiral(2,20,240);
				straightShot(1,(int)(Math.random()*180)-180,60,1);
				line(6,20,3,10);
			}
			move(angle, speed);					//Move the boss. 
		}
	}
	private float hpLeft(){	
		return ((float)health)/((float)maxHealth)*100;		//Returns current health as a percentage
	}
	public String hpFormat(){
		DecimalFormat df = new DecimalFormat("#.##");		//Formats the percentage nicely
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(hpLeft()) + "%";
	}
	private void straightShot(int s,int angle, int size, int n){
		if(straightShotBullets <= 0){						//Shoots bullets in a straight line
			straightShotBullets = n;						//(Can be its own object, however I decided to
			straightShotAngle = angle;						//Limit the number of classes I have)
		}													//So shoots a bullet in a straight line, if there's no bullets left in the
		shoot(s,straightShotAngle,new Point2D.Double(size,size));//"Queue" then use the angle given
		straightShotBullets -= 1;
	}
	private void move(double angle, int speed){				//Does some trig to get the x and y components of movement
		location = new Point2D.Double((location.getX() - (speed * Math.cos(Math.toRadians(angle)))),
				(location.getY() - (speed * Math.sin(Math.toRadians(angle)))));
	}
	private void ring(int s, int size){					//Shoots a bunch (36) bullets in a ring around the boss
		int j = (int) (Math.random()*10);
		for(int i = j; i < 360; i+=10){
			shoot(s,i,new Point2D.Double(size,size));
		}
	}
	public void horizontal(){							//Shoots horizontally (left and right)
		shoot(3,0,new Point2D.Double(10,10));
		shoot(3,180,new Point2D.Double(10,10));
	}
	public void line(int number, int amount, int s, int size){
		if(lineLocation.isEmpty()){						//2D arraylist of locations (each array has a location, the number of locations is the number of bullets)
			for(int i = 0; i < number; i++){			//Initializes points
				lineLocation.add(new ArrayList<Point2D>());
			}
			for(int j = 0; j < lineLocation.size(); j++){
				double randomX = Math.random()*MainClass.PLAY_FIELD_SIZE.getX();				//Each array has a location
				for(int i = 0; i < amount; i++){												
					lineLocation.get(j).add(new Point2D.Double(randomX,getCenter().getY()));	//Adds the number of bullets to that array
				}
			}
		}
		else{																					//Fires vertical lines from said locations
			for(int i = 0; i < lineLocation.size();i++){
				shoot(s, 270, new Point2D.Double(size,size), lineLocation.get(i).get(0));
				lineLocation.get(i).remove(0);
			}
			if(lineLocation.get(0).isEmpty()){
				lineLocation.clear();
			}
		}
		horizontal();																			//Shoots horizontally because it looks cool
	}
	public void spiral(int s, int size, int offset){
		spiral +=4 + offset;
		shoot(s,spiral,new Point2D.Double(size,size));			//Fires in a circle (similar to ring except instead of all at once)
	}
	private void shoot(int s, int a, Point2D size){				//The main shooting method, creates new bullets and adds them to the list
		Bullet b = new Bullet(getCenter(), size, Color.red, s, a);
		bullets.add(b);
	}
	private void shoot(int s, int a, Point2D size, Point2D l){	//Overloaded shooting method with some more parameters
		Bullet b = new Bullet(l, size, Color.red, s, a);
		bullets.add(b);
	}
	public void update(){										//Updates the boss's image and bullets
		for(int i = bullets.size()-1; i >= 0; i--){
			bullets.get(i).move();
			if(MainClass.outOfBounds(bullets.get(i))){
				bullets.remove(bullets.get(i));					//Removes bullets if they are offscreen
			}
		}
		if(!alive()){
			currentState = states[2];
		}
		else{
			currentState = states[0];
		}
	}
	public void hit(){											//Decreases the boss's heath if hit
		if(alive()){
			currentState = states[1];							//Changes to the hit image briefly
			health -= 1;
		}
	}
	public boolean alive(){										//Returns whether the boss is alive or not
		return health>0;
	}
}

