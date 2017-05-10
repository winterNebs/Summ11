//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.*;
import java.awt.geom.Point2D;
public class Text {				//"Textbox" 
	protected Image image;		//Image to draw on		
	protected Graphics buffer;	//Thing to draw image						
	protected Point2D location;	//Location of it
	protected String text;		//Its text
	protected Font font;		//Its font
	protected Color color;		//Color
	public Rectangle bounds;	//Bounds
	public Text(){				//Default constructor
	}
	public Text(Image i, Graphics b,String t, Point2D l, Font f, Color c){
		image = i;				//Constructor stuff
		buffer = b;				//
		text = t;				//
		location = l;			//
		font = f;				//
		color = c;				//
		draw();					//draws the text
	}
	public Image draw(){
		Rectangle rect = new Rectangle();					//Creates a rectangle
		FontMetrics metrics = buffer.getFontMetrics(font);	//Creates a thing that gets data for font
		rect.setBounds(rectangleBounds(new Point2D.Double(metrics.stringWidth(text), metrics.getHeight()), location));
		buffer.setColor(Color.black);						//Sets the rectangle to the bounds of the text
		buffer.fillRect(rect.x, rect.y, rect.width, rect.height);
		buffer.setColor(color);
		drawCenteredString(buffer, text, rect, font);		//Draws the string centered in the box
		buffer.drawRect(rect.x, rect.y, rect.width, rect.height);
		bounds = new Rectangle(rect);
		return image;	
	}
	protected Rectangle rectangleBounds(Point2D s, Point2D l){
		Rectangle r = new Rectangle();	//Creates a rectangle with the location and size
		r.setBounds((int)(l.getX()-(s.getX()/2)), (int)(l.getY()-(s.getY()/2)),(int)(s.getX()), (int)(s.getY()));
		return r;
	}
	protected void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);					//Gets the data of the font
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;	// Determine the X coordinate for the text
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();	// Determine the Y coordinate for the text 
		g.setFont(font);			//Set the font
		g.drawString(text, x, y);	//Draw the String
	}

}
