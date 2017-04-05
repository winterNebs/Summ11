import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
/**
 TODO: 
 main menu passing shinanigans aaaaaa
 */
public class MainMenu implements Observer{
	Image image;													//Double buffer image **
	Graphics buffer;												//Double buffer drawer **			
	Font dFont; 													//Default font
	public MainMenu(Image i, Graphics b){
		MainClass.addObserver(this);
		image = i;
		buffer = b;
	}
	public Image draw(){
		dFont = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/15));
		Button b = new Button(image, buffer,"This is a test", 
				new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/10),
				dFont, Color.red);
		//buffer.drawLine((int)MainClass.PLAY_FIELD_SIZE.getX() / 2, 0, (int)MainClass.PLAY_FIELD_SIZE.getX() / 2, 10000);
		buffer.drawImage(b.draw(), 0, 0, null);
		return image;
	}
	public void keyUpdate(KeyEvent keyevent, boolean pressed) {

	}
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked) {

	}
}
