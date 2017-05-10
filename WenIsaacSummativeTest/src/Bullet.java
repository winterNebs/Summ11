//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.*;
import java.awt.geom.Point2D;
public class Bullet extends Entity {
	private int speed;			//Speed of bullet
	int angle;					//Angle of bullet
	public Bullet(){
		name = "Default";					//Constructor stuff
		location = new Point2D.Double(0,0);	//
		size = new Point2D.Double(10,10);	//
		color = Color.RED;					//
		speed = 0;							//
		angle = 0;							//
	}
	public Bullet(Point2D l, Point2D s, Color c, int spd, int a){
		name = "bullet";					//Constructor stuff
		location = l;						//
		size = s;							//
		color = c;							//
		speed = spd;						//
		angle = a;							//
	}
	public void move(){						//Updates location using trig
		location = new Point2D.Double((location.getX() - (speed * Math.cos(Math.toRadians(angle)))), (location.getY() - (speed * Math.sin(Math.toRadians(angle)))));
	}
}
