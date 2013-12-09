package ab.ai.info;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Structure {
	public enum Type { ROCK, WOOD, ICE, TNT }
	public Rectangle pos;
	public Type type;
	public double value;
	public ArrayList<Structure> supporting, supportedBy;
	public ArrayList<Structure> neighbors;
	public Structure(Rectangle position, Type type){
		this.pos = position;
		this.type = type;
		value = 0.0;
		neighbors = new ArrayList<Structure>();
		supporting = new ArrayList<Structure>();
		supportedBy = new ArrayList<Structure>();
	}

	public String toString(){
		return "[" + type + " (X: " + pos.getCenterX() + ", Y: " + pos.getCenterY() + ")]";
	}
}
