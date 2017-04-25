import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
//TODO Line shooty 
public class Boss extends Entity{
	private final int slow = 5;
	public int health;
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
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, 0);
		size = new Point2D.Double(200,200);
		states[0] = a;
		states[1] = b;
		states[2] = c;
		currentState = states[0];
	}
	public void randomMove(){
		if(alive()){
			spiral(2);
			int Move = (int)(Math.random()*40)-20;
			int angle = (int)(Math.random()*360);
			move(angle, speed);
			if(Move == 10){
				ring(2);
			}
			line(4,20,3,new Point2D.Double(5,5));
			horizontal();
			straightShot(5,(int)(Math.random()*180)-180);
		}
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
	private void ring(int s){
		int j = (int) (Math.random()*10);
		for(int i = j; i < 360; i+=10){
			shoot(s,i,new Point2D.Double(20,20));
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
	}
	public void spiral(int s){
		spiral +=4;
		shoot(s,spiral,new Point2D.Double(10,10));
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
	public static void potato(){
	}
}

