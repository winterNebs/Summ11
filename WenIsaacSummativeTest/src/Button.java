//Isaac Wen
//2017-05-08
//A little game in the "Bullet Hell" genre 
import java.awt.*;
import java.awt.geom.Point2D;
public class Button extends Text{//Clickable text									
	public int number;			//Number of the button
	public boolean isClicked;	//Whether the button is clicked or not
	private Color original;		//The original color of the button
	public Button(){
		number = -1;			//Default constructor
	}
	public Button(int n,Image i, Graphics b,String t, Point2D l, Font f, Color c){
		number = n;				//Constructor stuff
		image = i;				//
		buffer = b;				//
		text = t;				//
		location = l;			//
		font = f;				//
		original = c;			//
		color = c;				//
		draw();					//Draws the button
	}
	public void hovered(boolean hovered, boolean clicked){
		if(hovered){			//Changes the color of the button when hovered
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
