package ab.ai.info;

import ab.vision.Vision;

/***
 * GameInfo will hold data that we can gain from the vision object.
 * I imagine we can probably put quite a few helper functions for our AI in here to reason about the game state - Brian
 *
 */
public class GameInfo {
	//SlingInfo contains information on the slingshot position, birds, and the bird firing order
	public SlingInfo slingInfo;
	//ObstacleInfo contains information on the pigs, structures, and TNTs
	public ObstacleInfo obstacleInfo;
	
	public GameInfo(Vision vision){
		slingInfo = new SlingInfo(vision);
		obstacleInfo = new ObstacleInfo(vision);
	}
	
	
}
