/** what am i doing */
import java.awt.*;
import java.awt.geom.Point2D;
public class Button extends Text{											
	public int number;
	public boolean isClicked;
	private Color original;
	public Button(){
		number = -1;
	}
	public Button(int n,Image i, Graphics b,String t, Point2D l, Font f, Color c){
		number = n;
		image = i;
		buffer = b;
		text = t;
		location = l;
		font = f;
		original = c;
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
		buffer.drawRect(rect.x, rect.y, rect.width, rect.height);
		bounds = new Rectangle(rect);
		return image;
	}
	public void hovered(boolean hovered, boolean clicked){
		if(hovered){
			color = new Color(255 - original.getRed(), 255 - original.getGreen(), 255 - original.getBlue());
			isClicked = clicked;
			if(isClicked){
				color = Color.white;
			}
		}
		else{
			color = original;
		}
	}
}
