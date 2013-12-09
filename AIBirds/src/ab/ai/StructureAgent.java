package ab.ai;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ab.ai.info.GameInfo;
import ab.ai.info.Structure;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

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
				targetPiece = building.get(0);
			}
		}
		Point target = new Point((int)targetPiece.pos.x, (int)targetPiece.pos.y);
		ArrayList<Shot> shots = aimAtTarget(target);
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
	
}
