import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Player extends Entity implements Observer{
	private final int FAST = 5;
	private final int SLOW = 1;
	private boolean controls[] = {false,false,false,false,false,false};
	//FW,BW,LEFT,RIGHT,SHIFT,SPACE;
	private int keyBinding[];
	private int speed;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	//{87,83,65,68,16,32} = W S A D SHIFT SPACE
	//{38,40,37,38,16,32} = UP DOWN LEFT RIGHT SHIFT SPACE
	public Player(){
		name = "player";
		speed = FAST;
		location = new Point2D.Double(MainClass.PLAY_FIELD_SIZE.getX() / 2, MainClass.PLAY_FIELD_SIZE.getY()+10);
		size = new Point2D.Double(15,15);
		color = Color.red;
		MainClass.addObserver(this);
		keyBinding = new int[6];
		keyBinding[0]=38;
		keyBinding[1]=40;
		keyBinding[2]=37;
		keyBinding[3]=39;
		keyBinding[4]=16;
		keyBinding[5]=32;
		//keyBinding[5]=67;
	}
	@Override
	public void update(KeyEvent keyevent, boolean pressed) {
		// TODO Auto-generated method stub
		for(int i = 0; i < keyBinding.length; i++){
			if(keyevent.getKeyCode() == keyBinding[i]){
				controls[i] = pressed;
			}
		}
		//move();
	}
	public void move(){
		for(int i = 0; i < controls.length; i++){
			if(controls[i]){
				switch(i){
				case 0: location = new Point2D.Double(location.getX(),location.getY()-speed); /*System.out.println("w");*/ break;
				case 1: location = new Point2D.Double(location.getX(),location.getY()+speed); /*System.out.println("s");*/ break;
				case 2: location = new Point2D.Double(location.getX()-speed,location.getY()); /*System.out.println("a");*/ break;
				case 3: location = new Point2D.Double(location.getX()+speed,location.getY()); /*System.out.println("d");*/ break;
				case 4: speed = SLOW; break;
				case 5: shoot(); /*System.out.println("space");*/ break;
				}
			}
		}	
		if(!controls[4]){
			speed = FAST;
		}
	}
	private void shoot(){
	///	int deltaX = (speed * cos )
		Bullet b = new Bullet(getCenter(), new Point2D.Double(10,10), Color.red, 10, 90);
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
}