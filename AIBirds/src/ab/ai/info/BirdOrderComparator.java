package ab.ai.info;

import java.awt.Rectangle;
import java.util.Comparator;

/***
 * Compares two bird positions to see which bird center is closest to the slingshot.
 *
 */
public class BirdOrderComparator implements Comparator<Bird>{
	private Rectangle slingshot;
	public BirdOrderComparator(Rectangle slingshot){
		this.slingshot = slingshot;
	}
	@Override
	public int compare(Bird bird0, Bird bird1) {
		double dist0 = Math.pow(bird0.position.getCenterX() - slingshot.getCenterX(), 2) + Math.pow(bird0.position.getCenterY() - slingshot.getCenterY(), 2);
		double dist1 = Math.pow(bird1.position.getCenterX() - slingshot.getCenterX(), 2) + Math.pow(bird1.position.getCenterY() - slingshot.getCenterY(), 2);
		if (dist0 > dist1){
			return -1;
		}else if (dist0 < dist1){
			return 1;
		}else{
			return 0;
		}
	}

}
