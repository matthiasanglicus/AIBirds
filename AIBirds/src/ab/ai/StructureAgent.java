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
import ab.ai.info.Line;
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

		while (vision.findSlingshot() == null
				&& ar.checkState() == GameState.PLAYING) {
			System.out
					.println("no slingshot detected. Please remove pop up or zoom out");
			ar.fullyZoom();
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
		}

		info = new GameInfo(vision);
		GameState state = ar.checkState();
		int complexity = 0;
		Structure targetPiece = null;
		for (List<Structure> building : info.obstacleInfo.buildings) {
			if (building.size() > complexity) {
				complexity = building.size();
				targetPiece = building.get(5);
			}
		}
		Point target = new Point((int) targetPiece.pos.x,
				(int) targetPiece.pos.y);
		ArrayList<Shot> shots = aimAtTarget(target);
		parabolaTest(screenshot, target);

		state = ar.shootWithStateInfoReturned(shots);

		return state;
	}

	private ArrayList<Shot> aimAtTarget(Point target) {
		ArrayList<Point> pts = tp.estimateLaunchPoint(info.slingInfo.slingshot,
				target);
		Point releasePoint = pts.size() > 1 ? pts.get(1) : pts.get(0);
		Point refPoint = tp.getReferencePoint(info.slingInfo.slingshot);
		ArrayList<Shot> shots = new ArrayList<Shot>();
		shots.add(new Shot(refPoint.x, refPoint.y, (int) releasePoint.getX()
				- refPoint.x, (int) releasePoint.getY() - refPoint.y, 0, 1500));
		return shots;
	}

	private void parabolaTest(BufferedImage bi, Point target) {
		
		//Array to hold rectangles they have already collided with
		List<Rectangle> collision = new ArrayList<Rectangle>();
		//Array to hold type of things it has collided with
		List<String> holder = new ArrayList<String>();

		// Find the end point of our diagonal test
		Point end = new Point(target.x - 40, target.y - 40);
		//Point end = new Point(target.x + 40, target.y);
		Line testLine = new Line(target, end);

		List<Point> testingLine = testLine.getDiagonalPoints();

		for (Point po : testingLine) {
			boolean t = false;

			for (Rectangle r : info.obstacleInfo.stones) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" rock ");
						collision.add(r);
						//t = true;
					}
					break;
				}
			}
			//if (t)
				//continue;

			for (Rectangle r : info.obstacleInfo.wood) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" wood ");
						collision.add(r);
						//t = true;
					}
					break;
				}
			}
			//if (t)
				//continue;

			for (Rectangle r : info.obstacleInfo.ice) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" ice ");
						collision.add(r);
						//t = true;
					}
					break;
				}
			}
			//if (t)
				//continue;

		}

		System.out.println("Line thinks it collided with:");
		for (String r : holder) {
			System.out.print(r);
		}

	}
}
