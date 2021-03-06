package ab.ai.info;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ab.vision.Vision;

/***
 * Contains the rectangles of the slingshot and detected birds.
 * As well as contains a list of the birds in firing order
 * 
 */
public class SlingInfo {
	public Rectangle slingshot;
	public List<Bird> firingOrder; //List of Birds in firing order, bird class contains color information with the rectangle.
	public List<Rectangle> redBirds;
	public List<Rectangle> blueBirds;
	public List<Rectangle> yellowBirds;
	public List<Rectangle> whiteBirds;
	public List<Rectangle> blackBirds;
	
	public SlingInfo(Vision vision){
		//Find objects with the current vision capture
		slingshot = vision.findSlingshot();
		redBirds = vision.findRedBirds();
		blueBirds = vision.findBlueBirds();
		yellowBirds = vision.findYellowBirds();
		whiteBirds = vision.findWhiteBirds();
		blackBirds = vision.findBlackBirds();
		
		firingOrder = new ArrayList<Bird>();
		//Combine the detected birds into a single list and sort them into their firing order
		for(Rectangle b : redBirds)
			firingOrder.add(new Bird(b, Bird.Color.RED));
		for(Rectangle b : blueBirds)
			firingOrder.add(new Bird(b, Bird.Color.BLUE));
		for(Rectangle b : yellowBirds)
			firingOrder.add(new Bird(b, Bird.Color.YELLOW));
		for(Rectangle b : whiteBirds)
			firingOrder.add(new Bird(b, Bird.Color.WHITE));
		for(Rectangle b : blackBirds)
			firingOrder.add(new Bird(b, Bird.Color.BLACK));
		
		Collections.sort(firingOrder, new BirdFiringOrder(slingshot));
	}
	
	private static class BirdFiringOrder implements Comparator<Bird>{
		private Rectangle slingshot;
		public BirdFiringOrder(Rectangle slingshot){
			this.slingshot = slingshot;
		}
		@Override
		public int compare(Bird bird0, Bird bird1) {
			double dist0 = Math.pow(bird0.position.getCenterX() - slingshot.getCenterX(), 2) + Math.pow(bird0.position.getCenterY() - slingshot.getCenterY(), 2);
			double dist1 = Math.pow(bird1.position.getCenterX() - slingshot.getCenterX(), 2) + Math.pow(bird1.position.getCenterY() - slingshot.getCenterY(), 2);
			if (dist0 > dist1){
				return 1;
			}else if (dist0 < dist1){
				return -1;
			}else{
				return 0;
			}
		}
	}
}
