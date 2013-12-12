package ab.ai.info;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Line{
    private Point start; 
    private Point end;

    private List<Point> lineDiagonal = new ArrayList<Point>();

    public Line(Point s, Point e) {
    	start = s;
    	end = e;
    	lineDiagonal.add(end);

        for(int i = end.x; i < start.x; i++)
        {
        	for(int j = end.y; j > start.y; j++){
        		Point temp = new Point(i, j);
        		lineDiagonal.add(temp);
        	}
        }
        
        lineDiagonal.add(start);
    } 
    
    public Point getStartPoint(){ return start;}
    public Point getEndPoint(){return end;}
    public List<Point> getDiagonalPoints(){return lineDiagonal;}
   
    
    
}
