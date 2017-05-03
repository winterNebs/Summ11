import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Entity{
	private final int slow = 5;
	public int health;
	private int maxHealth;
	private int spiral;
	private int speed = 10;
	private int straightShotBullets =  0;
	private int straightShotAngle = 0;
	int difficulty;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	//ArrayList<Point2D> lineLocation
	List<List<Point2D>> lineLocation = new ArrayList<List<Point2D>>();
	private Image states[] = new Image[3];
	public Image currentState;
	public Boss(Image a, Image b, Image c, int d){
		color = Color.white;
		name = "boss";
		health = 1000000/d;
		maxHealth = health;
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, 0);
		size = new Point2D.Double(200,200);
		states[0] = a;
		states[1] = b;
		states[2] = c;
		currentState = states[0];
		difficulty = d;
	}
	public void fight(){
		if(alive()){
			int angle = (int)(Math.random()*360);
			int rnd = (int)(Math.random()*difficulty);
			if(location.getY() < 0){
				angle = (int)(Math.random()*360);
			}
			else if(location.getY() > (MainClass.PLAY_FIELD_SIZE.getY()/4)){
				angle = (int)(Math.random()*180);
			}
			if(hpLeft() >= 90){
				speed = 5;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(2,10);break;
				case 3: ring(2,10);break;
				}
			}
			else if(hpLeft() >= 80){
				speed = 7;
				switch(rnd){
				case 1: ring(2,10);break;
				case 2: ring(1,15);break;
				case 3: ring(2,10);break;
				case 4: ring(1,15);break;
				}
			}
			else if(hpLeft() >= 75){
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
				straightShot(5,(int)(Math.random()*180)-180,20,30);
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
			move(angle, speed);
		}
	}
	private float hpLeft(){
		return ((float)health)/((float)maxHealth)*100;
	}
	public String hpFormat(){
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(hpLeft()) + "%";
	}
	private void straightShot(int s,int angle, int size, int n){
		if(straightShotBullets <= 0){
			straightShotBullets = n;
			straightShotAngle = angle;
		}
		shoot(s,straightShotAngle,new Point2D.Double(size,size));
		straightShotBullets -= 1;
	}
	private void move(double angle, int speed){
		location = new Point2D.Double((location.getX() - (speed * Math.cos(Math.toRadians(angle)))),
				(location.getY() - (speed * Math.sin(Math.toRadians(angle)))));
	}
	private void ring(int s, int size){
		int j = (int) (Math.random()*10);
		for(int i = j; i < 360; i+=10){
			shoot(s,i,new Point2D.Double(size,size));
		}
	}
	public void horizontal(){
		shoot(3,0,new Point2D.Double(10,10));
		shoot(3,180,new Point2D.Double(10,10));
	}
	public void line(int number, int amount, int s, int size){
		if(lineLocation.isEmpty()){
			for(int i = 0; i < number; i++){
				lineLocation.add(new ArrayList<Point2D>());
			}
			for(int j = 0; j < lineLocation.size(); j++){
				double randomX = Math.random()*MainClass.PLAY_FIELD_SIZE.getX();
				for(int i = 0; i < amount; i++){
					lineLocation.get(j).add(new Point2D.Double(randomX,getCenter().getY()));
				}
			}
		}
		else{
			for(int i = 0; i < lineLocation.size();i++){
				shoot(s, 270, new Point2D.Double(size,size) ,lineLocation.get(i).get(0));
				lineLocation.get(i).remove(0);
			}
			if(lineLocation.get(0).isEmpty()){
				lineLocation.clear();
			}
		}
		horizontal();
	}
	public void spiral(int s, int size, int offset){
		spiral +=4 + offset;
		shoot(s,spiral,new Point2D.Double(size,size));
	}
	private void shoot(int s, int a, Point2D size){
		Bullet b = new Bullet(getCenter(), size, Color.red, s, a);
		bullets.add(b);
	}
	private void shoot(int s, int a, Point2D size, Point2D l){
		Bullet b = new Bullet(l, size, Color.red, s, a);
		bullets.add(b);
	}
	public void update(){
		for(int i = bullets.size()-1; i >= 0; i--){
			bullets.get(i).move();
			if(MainClass.outOfBounds(bullets.get(i))){
				bullets.remove(bullets.get(i));
			}
		}
		if(!alive()){
			currentState = states[2];
		}
		else{
			currentState = states[0];
		}
	}
	public Image bulletDraw(Image i, Graphics g){
		for(Bullet b: bullets){
			g.drawImage(b.draw(i, g), 0, 0, null);
		}
		return i;

	}
	public void hit(){
		if(alive()){
			currentState = states[1];
			health -= 1;
		}
	}
	public boolean alive(){
		if(health>0){
			return true;
		}
		return false;
	}
}

