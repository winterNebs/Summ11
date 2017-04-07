import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
/**
 TODO: 
 main menu passing shinanigans aaaaaa
 */
public class MainMenu implements Observer{
	private Image image;													//Double buffer image **
	private Graphics buffer;												//Double buffer drawer **			
	private Font dFont; 													//Default font
	private ArrayList<Button> buttons = new ArrayList<Button>();
	public boolean enabled = false;
	public MainMenu(Image i, Graphics b, boolean e){
		MainClass.addObserver(this);
		image = i;
		buffer = b;
		enabled = e;
		addButtons();
	}
	private void addButtons(){
		dFont = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/15));
		buttons.add(new Button(0,image, buffer,"Paused", 
				new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/10),
				dFont, Color.red));
		buttons.add(new Button(1,image, buffer,"Start", 
				new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()/4),
				dFont, Color.red));
	}
	public Image draw(){
		for(Button b: buttons){
			buffer.drawImage(b.draw(), 0, 0, null);
		}
		return image;
	}
	public void keyUpdate(KeyEvent keyevent, boolean pressed) {

	}
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked) {
		for(Button b: buttons){
			b.hovered(b.bounds.contains(mouseevent.getPoint()), clicked);
		}
		update();
	}
	public void update(){
		if(enabled){
			for(Button b: buttons){
				if(b.isClicked){
					switch(b.number){
					case 0: ;break;
					case 1: MainClass.isPlaying = true; break;
					}
				}
			}
		}
	}
}
