import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
/**
 TODO: 
 - Nothing for now :)
 */
public class MainMenu implements Observer{
	private Image image;										//Image to draw on
	private Graphics buffer;									//Drawer to draw on the image (not a buffer really, but i copy pasted)
	public Font dFont; 											//Default font
	private ArrayList<Button> buttons = new ArrayList<Button>();//Array of all our buttons on the menu
	private ArrayList<Text> texts = new ArrayList<Text>();		//Array of all our Text on the menu
	public boolean enabled = false;								//Boolean for (should the menu appear and work)
	public int number;
	public MainMenu(int n, Image i, Graphics b, boolean e){
		MainClass.addObserver(this);							//Adds itself as an observer
		//Constructor jargon
		number = n;
		image = i;
		buffer = b;
		enabled = e;
		dFont = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/15));
	}
	public MainMenu(){/**Gross default constructor*/}
	public Image draw(){
		//Draws all the buttons on the image, returns that image so the mainclass can draw it
		buffer.setColor(Color.black);
		buffer.fillRect(0, 0, (int)MainClass.PLAY_FIELD_SIZE.getX(), (int)MainClass.PLAY_FIELD_SIZE.getY());
		for(Button b: buttons){
			buffer.drawImage(b.draw(), 0, 0, null);
		}
		for(Text t: texts){
			buffer.drawImage(t.draw(), 0, 0, null);
		}
		return image;
	}
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked) {
		//Does stuff for the buttons
			for(Button b: buttons){
				if(enabled){
					b.hovered(b.bounds.contains(mouseevent.getPoint()), clicked);
				}
				else{
					b.hovered(b.bounds.contains(mouseevent.getPoint()), false);
				}
				
			}
	}
	public ArrayList<Button> updater(){
		//Returns list of buttons
		return buttons;
	}
	public void addButton(int n, String t, Point2D l, Font f, Color c){
		//Creates new buttons
		buttons.add(new Button(n,image, buffer, t, l, f, c));
	}
	public void addText(String t, Point2D l, Font f, Color c){
		//Creates new text
		texts.add(new Text(image, buffer, t, l, f, c));
	}
	public void keyUpdate(KeyEvent keyevent, boolean pressed) {	/**Don't really need this*/}
}
