import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Player extends Entity implements Observer{
	private final int FAST = 4;
	private final int SLOW = 1;
	private final Point2D BULLET_SIZE = new Point2D.Double(10,10);
	private boolean controls[] = {false,false,false,false,false,false,false};
	//FW,BW,LEFT,RIGHT,SHIFT,SPACE;
	private int keyBinding[];
	private int speed;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	Shield shield;
	//{87,83,65,68,16,32} = W S A D SHIFT SPACE
	//{38,40,37,38,16,32,17} = UP DOWN LEFT RIGHT SHIFT SPACE CONTROL
	public Player(){
		name = "player";
		speed = FAST;
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()+10);
		size = new Point2D.Double(15,15);
		color = Color.red;
		MainClass.addObserver(this);
		keyBinding = new int[controls.length];
		//rebind everything
		keyBinding[0]=KeyEvent.VK_W;
		keyBinding[1]=KeyEvent.VK_S;
		keyBinding[2]=KeyEvent.VK_A;
		keyBinding[3]=KeyEvent.VK_D;
		keyBinding[4]=KeyEvent.VK_SLASH;
		//keyBinding[4]=16;
		keyBinding[5]=KeyEvent.VK_PERIOD;
		//keyBinding[6]=17;
		keyBinding[6]=KeyEvent.VK_SHIFT;

		shield = new Shield(this,20,200);
	}
	public void keyUpdate(KeyEvent keyevent, boolean pressed) {
		for(int i = 0; i < keyBinding.length; i++){
			if(keyevent.getKeyCode() == keyBinding[i]){
				controls[i] = pressed;
			}
		}
	}
	public void move(){
		for(int i = 0; i < controls.length; i++){
			if(controls[i]){
				switch(i){
				case 0: location = new Point2D.Double(location.getX(),location.getY()-speed); /*System.out.println("w"); */break;
				case 1: location = new Point2D.Double(location.getX(),location.getY()+speed); /*System.out.println("s"); */break;
				case 2: location = new Point2D.Double(location.getX()-speed,location.getY()); /*System.out.println("a"); */break;
				case 3: location = new Point2D.Double(location.getX()+speed,location.getY()); /*System.out.println("d"); */break;
				case 4: shield.enabled = true; break;
				case 5: shoot(); break;
				case 6: speed = SLOW; break;
				}
			}
		}	
		if(!controls[4]){
			shield.enabled = false;
		}
		if(!controls[6]){
			speed = FAST;
		}
	}
	private void shoot(){
		Bullet b = new Bullet(new Point2D.Double(getCenter().getX()-(BULLET_SIZE.getX()/2),getCenter().getY()-(BULLET_SIZE.getY()/2)), BULLET_SIZE, Color.red, 10, 90);
		bullets.add(b);
	}
	public void update(){
		for(int i = bullets.size()-1; i >= 0; i--){
			bullets.get(i).move();
			if(MainClass.outOfBounds(bullets.get(i))){
				bullets.remove(bullets.get(i));
			}
		}
	}
	public Image shieldDraw(){ 
		if(shield.enabled){
			buffer.drawImage(shield.draw(image,buffer),0,0,null);
		}
		return image;
	}
/**//***//**//***//**//***//**//***//**//***//**//***//**//***//**//***/
//						Not efficient
//	public Image bulletDraw(){
//		for(Bullet b: bullets){
//			buffer.drawImage(b.draw(image, buffer), 0, 0, null);
//		}
//		return image;
//
//	}
	public void mouseUpdate(MouseEvent mouseevent, boolean clicked) {	}
}
