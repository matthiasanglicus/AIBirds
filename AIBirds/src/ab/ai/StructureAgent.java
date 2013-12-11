package ab.ai;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ab.ai.info.GameInfo;
import ab.ai.info.Structure;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import ab.vision.VisionUtils;

public class StructureAgent implements Runnable {
	public ActionRobot ar;
	public TrajectoryPlanner tp;
	public GameInfo info;
	public int currentlevel;

	public StructureAgent() {
		ar = new ActionRobot();
		tp = new TrajectoryPlanner();
		currentlevel = 1;
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}

	@Override
	public void run() {
		ar.loadLevel(currentlevel);
		GameState state = solve();
	}

	public GameState solve() {
		// capture Image
		BufferedImage screenshot = ActionRobot.doScreenShot();
		// process image
		Vision vision = new Vision(screenshot);
		
		while (vision.findSlingshot() == null && ar.checkState() == GameState.PLAYING) {
			System.out.println("no slingshot detected. Please remove pop up or zoom out");
			ar.fullyZoom();
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
		}

		info = new GameInfo(vision);
		GameState state = ar.checkState();
		int complexity = 0;
		Structure targetPiece = null;
		for (List<Structure> building : info.obstacleInfo.buildings){
			if (building.size() > complexity){
				complexity = building.size();
				targetPiece = building.get(6);
			}
		}
		Point target = new Point((int)targetPiece.pos.x, (int)targetPiece.pos.y);
		ArrayList<Shot> shots = aimAtTarget(target);
		parabolaTest(screenshot, shots, vision);
		
		
		state = ar.shootWithStateInfoReturned(shots);
		
		return state;
	}

	private ArrayList<Shot> aimAtTarget(Point target) {
		ArrayList<Point> pts = tp.estimateLaunchPoint(info.slingInfo.slingshot, target);
		Point releasePoint = pts.size() > 1 ? pts.get(1) : pts.get(0);
		Point refPoint = tp.getReferencePoint(info.slingInfo.slingshot);
		ArrayList<Shot> shots = new ArrayList<Shot>();
		shots.add(new Shot(refPoint.x, refPoint.y, (int) releasePoint.getX() - refPoint.x, (int) releasePoint.getY() - refPoint.y, 0, 1500));
		return shots;
	} 
	
	
	private void parabolaTest(BufferedImage bi, ArrayList<Shot> shots, Vision vision){
		List<Rectangle> collision = new ArrayList<Rectangle>();
		List<String> holder = new ArrayList<String>();
		
		
		for(Shot s : shots){
			Point releasePoint = new Point(s.getDx(), s.getDy());
			List<Point> trajectory = tp.predictTrajectory(info.slingInfo.slingshot, releasePoint, bi.getWidth(null));
			
			Matrix W = vision.fitParabola(trajectory); // <--- returns matrix containing likely path of parabola
			int p[][] = new int[2][1000];
			int startx = (int) info.slingInfo.slingshot.getCenterX();
			for (int i = 0; i < 1000; i++) {
				p[0][i] = startx;
				double tem = W.get(0, 0) * Math.pow(p[0][i], 2) + W.get(1, 0)
						* p[0][i] + W.get(2, 0);
				p[1][i] = (int) tem;
				startx += 2;
			}

			List<Point> testing = new ArrayList<Point>();
			for(int i = 0; i < p[0].length; i++){
				int j = p[0][i];
				int k = p[1][i];
				Point temp = new Point(j, k);
				testing.add(temp);
			}
			
			
			for (Point po : testing) {
				 boolean t = false;
				 
				 for(Rectangle r :info.obstacleInfo.stones)
				 {
					 if(r.contains(po)){
						 if(!collision.contains(r)) {
							 holder.add(" rock ");
							 collision.add(r);
						 }
						 t = true;
						 break;
					 }
				 }
				 if(t) continue;
				 
				 for(Rectangle r :info.obstacleInfo.wood)
				 {
					 if(r.contains(po)){
						 if(!collision.contains(r)) {
							 holder.add(" wood ");
							 collision.add(r);
						 }
						 t = true;
						 break;
					 }
				 }
				 if(t) continue;
				 
				 for(Rectangle r :info.obstacleInfo.ice)
				 {
					 if(r.contains(po)){
						 if(!collision.contains(r)) {
							 holder.add(" ice ");
							 collision.add(r);
						 }
						 t = true;
						 break;
					 }
				 }
				 if(t) continue;
		           
		    
		        }
		
		}
		
		System.out.println("Derp");
		for(String r: holder){
			System.out.println(r);
		}
		
	}
	
}
