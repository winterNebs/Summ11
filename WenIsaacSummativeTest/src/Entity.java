//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.*;
import java.awt.geom.Point2D;
public class Entity{
	public Point2D size;			//its size
	public Point2D location;		//its location
	public String name;				//name
	public Color color;				//color
	protected Image image;			//Image to draw on	
	protected Graphics buffer;		//Thing to draw the image
	public Entity(){				//Default constructor
	}
	public String toString(){		//toString method
		String s = "";
		s += name + "@(" + location.getX() + ", " + location.getY() + ")"; 
		return s;
	}
	public Point2D getCenter(){
		Point2D center = new Point2D.Double(location.getX() + (size.getX() / 2), location.getY() + (size.getY() / 2));
		return center;				//Returns the center of the entity
	}
	public Image draw(Image i, Graphics b){	//Draws the entity
		image = i;
		buffer = b;
		b.setColor(color);
		b.fillOval((int)location.getX(), (int)location.getY(), (int)size.getX(), (int)size.getY());
		return i;
	}
}
