import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Gui extends Applet{
	Image offscreen;													//Double buffer image **
	Graphics buffer;													//Double buffer drawer **
	static Point2D PLAY_FIELD_SIZE;										//Supposed to be a constant, but can't actually set this until i set the size)
	
	public void init(){
		this.setSize(2500,1500);											//Set window size
		PLAY_FIELD_SIZE  = new Point2D.Double(this.getWidth()/4*3, this.getHeight());	//Sets a "constant" (not really because things)
		offscreen = createImage(this.getWidth(),this.getHeight());		//Initialized the buffer image
		buffer = offscreen.getGraphics();								//Sets the buffer to draw on offscreen												//Starts the game
	}

}
