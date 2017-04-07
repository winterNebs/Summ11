import java.awt.*;
import java.awt.geom.Point2D;
public class Entity{
	public Point2D size;
	public Point2D location;
	public String name;
	public Color color;
	//private Image image; TODO??
	public Entity(){
	}
	public String toString(){
		String s = "";
		s += name + "@(" + location.getX() + ", " + location.getY() + ")"; 
		return s;
	}
	public Point2D getCenter(){
		Point2D center = new Point2D.Double(location.getX() + (size.getX() / 2), location.getY() + (size.getY() / 2));
		return center;
	}
}
