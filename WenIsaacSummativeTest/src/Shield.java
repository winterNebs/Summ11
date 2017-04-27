import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Shield extends Entity{
	boolean enabled;
	Player user;
	double shieldSize;
	double coolDown;
	double COOLDOWN;
	public Shield(){

	}
	public Shield(Player p, double s, double c){
		user = p;
		shieldSize = s;
		enabled = false;
		COOLDOWN = c;
	}
	public void update(){
		size = new Point2D.Double(user.size.getX()+shieldSize,user.size.getY()+shieldSize);
		location = new Point2D.Double(user.location.getX()-(shieldSize/2),user.location.getY()-(shieldSize/2));
		if(coolDown > 0){
			coolDown--;
		}
	}
	public Image draw(Image i, Graphics b){
		update();
		b.setColor(user.color);
		b.drawOval((int)location.getX(), (int)location.getY(), (int)size.getX(), (int)size.getY());
		return i;
	}
	public void hit(){
		coolDown = COOLDOWN;
		enabled = false;
		update();
	}
}
