//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Shield extends Entity{
	boolean enabled;		//Boolean for if the sheild is active
	Player user;			//Passes the player which is using the shield
	double shieldSize;		//Size of the sheild
	double coolDown;		//Cooldown timer
	double COOLDOWN;		//"Constant" for cooldown
	public Shield(){/**Default Constructor**/}
	public Shield(Player p, double s, double c){
		user = p;			//Constructor
		shieldSize = s;		//
		enabled = false;	//
		COOLDOWN = c;		//Stuff
	}
	public void update(){	//Updates location and cooldown for the shield
		size = new Point2D.Double(user.size.getX()+shieldSize,user.size.getY()+shieldSize);
		location = new Point2D.Double(user.location.getX()-(shieldSize/2),user.location.getY()-(shieldSize/2));
		if(coolDown > 0 && MainClass.isPlaying){
			enabled = false;
			coolDown--;
		}
	}
	public Image draw(Image i, Graphics b){
		if(coolDown==0){		//Only draws the shield when its off cooldown
			b.setColor(user.COLOR);
			b.drawOval((int)location.getX(), (int)location.getY(), (int)size.getX(), (int)size.getY());
		}			
		return i;
	}
	public void hit(){			
		coolDown = COOLDOWN;	//If the shield is hit then set it on cooldown
		enabled = false;		//And disable it
		update();
	}
	public String coolDownFormat(){
		if(coolDown > 0){		//Formats the cooldown nicely into seconds
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			return "Shield Cooldown: " + df.format(coolDown/100) + " s";
		}
		else{
			return "Shield Ready";
		}
	}
	public void activate(boolean state){
		if(state&&coolDown==0){	//Activates the shield if it's off cooldown
			enabled = true;
		}
		else{
			enabled = false;
		}
	}
}
