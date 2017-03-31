import java.awt.*;
import java.awt.geom.Point2D;
public class Entity{
	public Point2D size;
	public Point2D location;
	public String name;
	public Color color;
	private Image image;		//Soon^(tm)
	//Graphics g1;

	//speed only for non-players

	public Entity(){
		//Component a = new Component();
		//image = a.createImage((int)size.getX(),(int)size.getY());

		//g1 = image.getGraphics();
	}
	public String toString(){
		String s = "";
		s += name + "@(" + location.getX() + ", " + location.getY() + ")"; 
		return s;
	}
	//	public Graphics draw(Graphics g){
	//		g.setColor(color);
	//		g.drawOval(location.x, location.getY(), size.x, size.getY());
	//		return g;
	//	}
	public Point2D getCenter(){
		Point2D center = new Point2D.Double(location.getX() + (size.getX() / 2), location.getY() + (size.getY() / 2));
		return center;

	}
}
