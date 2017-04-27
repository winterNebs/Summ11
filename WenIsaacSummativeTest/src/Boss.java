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
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	//ArrayList<Point2D> lineLocation
	List<List<Point2D>> lineLocation = new ArrayList<List<Point2D>>();
	private Image states[] = new Image[3];
	public Image currentState;
	public Boss(Image a, Image b, Image c){
		color = Color.white;
		name = "boss";
		health = 10000;
		maxHealth = 10000;
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, 0);
		size = new Point2D.Double(200,200);
		states[0] = a;
		states[1] = b;
		states[2] = c;
		currentState = states[0];
	}
	public void fight(){
		if(alive()){
			int angle = (int)(Math.random()*360);;
			if(location.getY() > (size.getY())){
				angle = (int)(Math.random()*360);
			}
			else if(location.getY() > (MainClass.PLAY_FIELD_SIZE.getY()/2)){
				angle = (int)(Math.random()*180);
			}
			if(hpLeft() >= 85){
				speed = 5;
				int rnd = (int)(Math.random()*100);
				if(rnd == 10){
					ring(2,10);
				}
			}
			else if(hpLeft() >= 75){
				speed = 7;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,10);break;
				case 20: ring(1,15);break;
				}
			}
			else if(hpLeft() >= 70){
				speed = 10;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,10);break;
				case 20: ring(1,15);break;
				}
				spiral(2,10,0);
			}
			else if(hpLeft() >= 60){
				speed = 10;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,10);break;
				case 20: ring(1,15);break;
				case 30: ring(2,20);break;
				}
				spiral(2,10,0);
			}
			else if(hpLeft() >= 40){
				speed = 12;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,20);break;
				case 20: ring(2,20);break;
				case 30: ring(1,20);break;
				case 40: ring(2,10);break;
				}
				spiral(2,10,0);
				straightShot(5,(int)(Math.random()*180)-180);
				straightShot(5,(int)(Math.random()*180)-180);
			}
			else if(hpLeft() >= 5){
				speed = 15;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,20);break;
				case 20: ring(1,20);break;
				case 30: ring(1,25);break;
				}
				spiral(2,20,0);
				spiral(2,20,180);
				straightShot(5,(int)(Math.random()*180)-180);
				line(4,20,3,new Point2D.Double(5,5));
			}
			else{
				speed = 25;
				int rnd = (int)(Math.random()*100);
				switch(rnd){
				case 10: ring(2,20);break;
				case 20: ring(1,20);break;
				case 30: ring(1,30);break;
				case 40: ring(3,10);break;
				case 50: ring(3,10);break;
				case 60: ring(4,5);break;
				}
				spiral(2,20,0);
				spiral(2,20,120);
				spiral(2,20,240);
				straightShot(5,(int)(Math.random()*180)-180);
				straightShot(3,(int)(Math.random()*180)-180);
				line(4,20,3,new Point2D.Double(5,5));
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
	private void straightShot(int s,int angle){
		if(straightShotBullets <= 0){
			straightShotBullets = 30;
			straightShotAngle = angle;
		}
		shoot(s,straightShotAngle,new Point2D.Double(30,30));
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
	public void line(int number, int amount, int s, Point2D size){
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
				shoot(s, 270, size,lineLocation.get(i).get(0));
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

