import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Boss extends Entity{
	private final int slow = 5;
	private int health;
	private int spiral;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public Boss(){
		color = Color.blue;
		name = "boss";
		health = 10000;
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, 0);
		size = new Point2D.Double(200,200);
	}
	public void randomMove(){
		int Move = (int)(Math.random()*40)-20;
		//System.out.println(Move);
		//location = new Point2D((int)(location.getX() - (Move)), location.getY());
		if(Move == 10){
			ring(2);
		}
	}
	private void ring(int s){
		int j = (int) (Math.random()*10);
		for(int i = j; i < 360; i+=10){
			shoot(s,i,new Point2D.Double(20,20));
		}
	}
	public void spiral(int s){
		spiral +=4;
		shoot(s,spiral,new Point2D.Double(10,10));
	}
	private void shoot(int s, int a, Point2D size){
		///	int deltaX = (speed * cos )
		Bullet b = new Bullet(getCenter(), size, Color.red, s, a);
		bullets.add(b);
	}
	public void update(){
		for(int i = bullets.size()-1; i >= 0; i--){
			bullets.get(i).move();
			if(MainClass.outOfBounds(bullets.get(i))){
				bullets.remove(bullets.get(i));
			}
		}
	}
	public void hit(){
		health -= 1;
		//color = new Color(color.getRed(),color.getGreen()+1,color.getBlue());
		//System.out.println(health);
	}
}

