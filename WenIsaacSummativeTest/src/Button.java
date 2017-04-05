/** what am i doing */
import java.awt.*;
import java.awt.geom.Point2D;
public class Button {
	Image image;													//Double buffer image **
	Graphics buffer;												//Double buffer drawer **
	String name;
	Point2D location;
	//Point2D size;
	String text;
	Font font;
	Color color;
	public Button(){
		name = "test";
		//font = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/15));
	}
	public Button(Image i, Graphics b,String t, Point2D l, Font f, Color c){
		image = i;
		buffer = b;
		text = t;
		location = l;
		//size = s;
		font = f;
		color = c;
	}
	public Image draw(){
		Rectangle rect = new Rectangle();
		FontMetrics metrics = buffer.getFontMetrics(font);
		//Sets the size of the rectangle to the size of the text
		rect.setBounds(rectangleBounds(new Point2D.Double(metrics.stringWidth(text), metrics.getHeight()), location));
		//font = new Font("TimesRoman", Font.PLAIN, (int)(MainClass.PLAY_FIELD_SIZE.getX()/15));
		buffer.setColor(color);
		drawCenteredString(buffer, text, rect, font);
		buffer.drawRect(rect.x, rect.y, rect.width, rect.height);
		return image;
	}
	private Rectangle rectangleBounds(Point2D s, Point2D l){
		Rectangle r = new Rectangle();
		r.setBounds((int)(l.getX()-(s.getX()/2)), (int)(l.getY()-(s.getY()/2)),(int)(s.getX()), (int)(s.getY()));
		return r;
	}
	private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		/**Draws text centered*/
		//AAAAAAAAAA finally fixed 
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(text, x, y);
	}
}
