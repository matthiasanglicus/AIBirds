package ab.ai;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ab.ai.info.Building;
import ab.ai.info.GameInfo;
import ab.ai.info.Line;
import ab.ai.info.Structure;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.demo.util.StateUtil;
import ab.planner.TrajectoryPlanner;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import ab.vision.VisionUtils;

public class StructureAgent implements Runnable {
	public ActionRobot ar;
	public TrajectoryPlanner tp;
	public GameInfo info;
	public int currentLevel;

	public StructureAgent() {
		ar = new ActionRobot();
		tp = new TrajectoryPlanner();
		currentLevel = 1;
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}

	@Override
	public void run() {
		ar.loadLevel(currentLevel);
		while (true) {
			GameState state = solve();
			if (state == GameState.WON) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int score = -2;
				while (score != StateUtil.checkCurrentScore(ar.proxy)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					score = StateUtil.checkCurrentScore(ar.proxy);
				}
				System.out.println("###### The game score is " + score + "########");
				ar.loadLevel(++currentLevel);
				tp = new TrajectoryPlanner();
			} else if (state == GameState.LOST) {
				System.out.println("restart");
				ar.restartLevel();
			} else if (state == GameState.LEVEL_SELECTION) {
				System.out.println("unexpected level selection page, go to the lasts current level : " + currentLevel);
				ar.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out.println("unexpected main menu page, go to the lasts current level : " + currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out.println("unexpected episode menu page, go to the lasts current level : " + currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			}

		}
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		info = new GameInfo(vision);
		GameState state = ar.checkState();
		if (info.obstacleInfo.pigs.size() > 0) {
			Point target = null;
			List<Building> targetBuildings = info.obstacleInfo.importantBuildings();
			if (targetBuildings.size() > 0) {
				System.out.println("Building Found!");
				info.obstacleInfo.weighBuildings(targetBuildings);
				Building selected = null;
				int pigCount = 0;
				for (Building b : targetBuildings) {
					if (b.pigCount > pigCount) {
						pigCount = b.pigCount;
						selected = b;
					} else if (b.pigCount == pigCount && b.leftMostX() < selected.leftMostX()) {
						selected = b;
					}
				}
				Structure.sortLowestXValue(selected.parts);
				for (Structure s : selected.parts) {
					if (s.value == selected.highestWeight) {
						target = new Point((int) s.pos.getCenterX(), (int) s.pos.getCenterY());
						break;
					}
				}
			} else {
				System.out.println("No Important Building Found, aiming at a pig");
				Rectangle pig = info.obstacleInfo.pigs.get(0);
				target = new Point((int) pig.getCenterX(), (int) pig.getCenterY());
			}

			ArrayList<Shot> shots = aimAtTarget(target);
			parabolaTest(screenshot, target);

			state = ar.shootWithStateInfoReturned(shots);
		}
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

	private void parabolaTest(BufferedImage bi, Point target) {

		// Array to hold rectangles they have already collided with
		List<Rectangle> collision = new ArrayList<Rectangle>();
		// Array to hold type of things it has collided with
		List<String> holder = new ArrayList<String>();

		// Find the end point of our diagonal test
		Point end = new Point(target.x - 40, target.y - 40);
		// Point end = new Point(target.x + 40, target.y);
		Line testLine = new Line(target, end);

		List<Point> testingLine = testLine.getDiagonalPoints();

		for (Point po : testingLine) {
			boolean t = false;

			for (Rectangle r : info.obstacleInfo.stones) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" rock ");
						collision.add(r);
						// t = true;
					}
					break;
				}
			}
			// if (t)
			// continue;

			for (Rectangle r : info.obstacleInfo.wood) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" wood ");
						collision.add(r);
						// t = true;
					}
					break;
				}
			}
			// if (t)
			// continue;

			for (Rectangle r : info.obstacleInfo.ice) {
				if (r.contains(po)) {
					if (!collision.contains(r)) {
						holder.add(" ice ");
						collision.add(r);
						// t = true;
					}
					break;
				}
			}
			// if (t)
			// continue;

		}

		System.out.println("Line thinks it collided with:");
		for (String r : holder) {
			System.out.print(r);
		}

	}
}
