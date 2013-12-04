package ab.ai.info;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
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

	public List<List<Structure>> buildings;
	public List<Structure> parts;

	public ObstacleInfo(Vision vision) {
		pigs = vision.findPigs();
		stones = vision.findStones();
		wood = vision.findWood();
		ice = vision.findIce();
		tnt = vision.findTNTs();

		parts = new ArrayList<Structure>();
		for (Rectangle s : stones)
			parts.add(new Structure(s, Structure.Type.ROCK));
		for (Rectangle s : wood)
			parts.add(new Structure(s, Structure.Type.WOOD));
		for (Rectangle s : ice)
			parts.add(new Structure(s, Structure.Type.ICE));
		connectPieces(parts);
		buildings = findBuildings(parts);
		
	}

	private List<List<Structure>> findBuildings(List<Structure> pieces) {
		//The List of found structures
		List<List<Structure>> buildings = new ArrayList<List<Structure>>();
		//This list holds parts we discover to be part of the building (the neighbors of parts in the building)
		List<Structure> neighborhood = new ArrayList<Structure>();
		//Structure parts we've visited before to avoid reapplying the same pieces
		List<Structure> visited = new ArrayList<Structure>();

		for (Structure s : pieces) {
			if(visited.contains(s)){
				continue;
			}
			List<Structure> building = new ArrayList<Structure>();
			neighborhood.add(s);
			while (!neighborhood.isEmpty()) { //Iterate until we've run out of neighbors to expand
				Structure component = neighborhood.remove(0);
				if (!visited.contains(component)){
					if (!building.contains(component)) {
						building.add(component);
						neighborhood.addAll(component.neighbors);
					}
					visited.add(component);
				}
			}
			buildings.add(building);
		}
		return buildings;
	}

	private void connectPieces(List<Structure> pieces) {
		for (Structure piece : pieces) {
			Rectangle enlarged = (Rectangle) piece.pos.clone();
			enlarged.grow(5, 5);
			for (Structure piece2 : pieces) {
				if (piece != piece2) {
					if (enlarged.intersects(piece2.pos) && !piece.neighbors.contains(piece2)) {
						piece.neighbors.add(piece2);
						piece2.neighbors.add(piece);
					}
				}
			}
		}
	}
}
