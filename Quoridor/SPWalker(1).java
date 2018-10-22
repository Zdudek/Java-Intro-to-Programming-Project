import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SPWalker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SPWalker extends Player
{
     public SPWalker (int row, int col, int goal, String color)
    {
        super(row, col, goal, color);
    }

    public String teamName()
    {
        return "SPWalker";
    }
    
    public Coord move(Player opponent)
    {
        String route = getShortestPath(opponent);
        int row = getPathRow(route, 1);
        int col = getPathCol(route, 1);
        
        // if we are blocked by opponent from next square
        if (row == opponent.getCurRow() && col == opponent.getCurCol())
        {
            row = getPathRow(route, 2);
            col = getPathCol(route, 2);
        }
        
        //printPath(route, row, col);
        return new Coord(row, col);
    }
}
