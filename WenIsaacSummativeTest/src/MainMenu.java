import java.awt.Graphics;
import java.awt.Image;
/**
 TODO: 
 main menu passing shinanigans aaaaaa
 */
public class MainMenu {
	Image offscreen;													//Double buffer image **
	Graphics buffer;													//Double buffer drawer **
	public MainMenu(Image o, Graphics b){
		offscreen = o;
		buffer = b;
	}
}
