package ab.ai.info;

import java.awt.Rectangle;
import java.util.List;

import ab.vision.Vision;

/**
 * 
 * 
 */
public class ObstacleInfo {
	public List<Rectangle> pigs;
	public List<Rectangle> stones;
	public List<Rectangle> wood;
	public List<Rectangle> ice;
	public List<Rectangle> tnt;
	public ObstacleInfo(Vision vision){
		pigs = vision.findPigs();
		stones = vision.findStones();
		wood = vision.findWood();
		ice = vision.findIce();
		tnt = vision.findTNTs();
	}
}
