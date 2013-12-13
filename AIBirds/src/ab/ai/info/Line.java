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
    	
    	
    	///vertical line
    	if(end.x == start.x){
    		for(int i = end.y; i < start.y; i++){
    			Point temp = new Point(start.x, i);
    			lineDiagonal.add(temp);
    		}
    	}
    	//horizontal line
    	else if(end.y == start.y)
    	{
    		for(int i = end.x; i < start.x; i++)
    		{
    			Point temp = new Point(i, start.y);
    			lineDiagonal.add(temp);
    		}
    	}
    	else
    	{
    		double increment = (end.y - start.y) / (end.x - start.x);
    		for(int i = end.x; i < start.x; i++)
    		{
    			for(double j = end.y; j < start.y; j+=increment){
    				Point temp = new Point(i, (int)j);
    				lineDiagonal.add(temp);
    			}
    		}
    	}
    	
        
        lineDiagonal.add(start);
    } 
    
    public Point getStartPoint(){ return start;}
    public Point getEndPoint(){return end;}
    public List<Point> getDiagonalPoints(){return lineDiagonal;}
   
    
    
}
