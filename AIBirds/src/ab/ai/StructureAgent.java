package ab.ai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ab.ai.info.GameInfo;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.Vision;
import ab.vision.GameStateExtractor.GameState;

public class StructureAgent implements Runnable {
	public ActionRobot ar;
	public TrajectoryPlanner tp;
	public GameInfo info;
	public int currentlevel;

	public StructureAgent() {
		ar = new ActionRobot();
		tp = new TrajectoryPlanner();

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
		
		Point target = new Point(600, 300);
		ArrayList<Point> pts = tp.estimateLaunchPoint(info.slingInfo.slingshot, target);
		Point releasePoint = pts.get(0);
		Point refPoint = tp.getReferencePoint(info.slingInfo.slingshot);
		ArrayList<Shot> shots = new ArrayList<Shot>();
		shots.add(new Shot(refPoint.x, refPoint.y, (int) releasePoint.getX() - refPoint.x, (int) releasePoint.getY() - refPoint.y, 0, 1500));
		state = ar.shootWithStateInfoReturned(shots);
		
		return state;
	}

}
