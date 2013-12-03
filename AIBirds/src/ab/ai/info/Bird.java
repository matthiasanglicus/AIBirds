package ab.ai.info;

import java.awt.Rectangle;

/**
 * Bird contains the position rectangle and color of a bird.
 *
 */
public class Bird {
	public enum Color {
		RED, BLUE, YELLOW, WHITE, BLACK
	}
	public Color color;
	public Rectangle position;
	public Bird(Rectangle position, Color color){
		this.position = position;
		this.color = color;
	}
	
	public double getCenterX(){
		return position.getCenterX();
	}
	public double getCenterY(){
		return position.getCenterY();
	}
}
