import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.net.URL;
public class Entity{
	public Point2D size;
	public Point2D location;
	public String name;
	public Color color;
	protected Image image;				
	protected Graphics buffer;
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
	public Image draw(Image i, Graphics b){
		image = i;
		buffer = b;
		b.setColor(color);
		b.fillOval((int)location.getX(), (int)location.getY(), (int)size.getX(), (int)size.getY());
		return i;
	}
	public Image draw1(Image i, Graphics b){
		image = i;
		buffer = b;
		b.setColor(color);
		b.fillOval((int)location.getX(), (int)location.getY(), (int)size.getX(), (int)size.getY());
		return i;
	}
}
