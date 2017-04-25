/** make button inherit text :)
 * 
 *  */
import java.awt.*;
import java.awt.geom.Point2D;
public class Text {
	protected Image image;				
	protected Graphics buffer;												
	protected Point2D location;
	protected String text;
	protected Font font;
	protected Color color;
	public Rectangle bounds;
	public Text(){
	}
	public Text(Image i, Graphics b,String t, Point2D l, Font f, Color c){
		image = i;
		buffer = b;
		text = t;
		location = l;
		font = f;
		color = c;
		draw();
	}
	public Image draw(){
		Rectangle rect = new Rectangle();
		FontMetrics metrics = buffer.getFontMetrics(font);
		//Sets the size of the rectangle to the size of the text
		rect.setBounds(rectangleBounds(new Point2D.Double(metrics.stringWidth(text), metrics.getHeight()), location));
		buffer.setColor(Color.black);
		buffer.fillRect(rect.x, rect.y, rect.width, rect.height);
		buffer.setColor(color);
		drawCenteredString(buffer, text, rect, font);
		//buffer.drawRect(rect.x, rect.y, rect.width, rect.height);
		bounds = new Rectangle(rect);
		return image;
	}
	protected Rectangle rectangleBounds(Point2D s, Point2D l){
		Rectangle r = new Rectangle();
		r.setBounds((int)(l.getX()-(s.getX()/2)), (int)(l.getY()-(s.getY()/2)),(int)(s.getX()), (int)(s.getY()));
		return r;
	}
	protected void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
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
