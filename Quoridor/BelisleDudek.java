
import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
 
/**
* Write a description of class BelisleDudek here.
* 
* @author Amanda Belisle and Zack Dudek
* @version 4/16/13
*/
public class BelisleDudek extends Player
{
       
    private static final int MAX = 8;
    private boolean[][] hasVerticalWall = new boolean[MAX][MAX];
    private boolean[][] hasHorizontalWall = new boolean[MAX][MAX];
    
    /**
     * Random constructor - initializes random player
     */
    public BelisleDudek (int row, int col, int goal, String color)
    {
        super(row, col, goal, color);
    }
    
    
    /**
     * teamName - returns the team name. 
     */
    public String teamName()
    {
        return "BelisleDudek";
    }
   
    /**
     * getTreeLength - get number of steps in route path
     */
    public int getPathLength(String route)
    {
        return route.length() / 5;
    }
       
 
     /**
     * move - Make the player do an action
     */
    public Coord move(Player opponent)
    {
        String route = getShortestPath(opponent);
        String opponentRoute = opponent.getShortestPath(this);
        int row = getPathRow(route, 1);
        int col = getPathCol(route, 1);
        
           if (row == opponent.getCurRow() && col == opponent.getCurCol())     // if we are blocked by opponent from next square
        {
            row = getPathRow(route, 2);
            col = getPathCol(route, 2);
        }
        
        
        return new Coord(row, col);
                
        /**   if (getPathLength(route) < getPathLength(opponentRoute))             //compare shortest path of both
           {
            return movePlayer(opponent);
            }
            
            else
            {
            if (hasWall())                                                          // out of walls so move player instead
            {
                 return placeWall(opponent);   
            }   
             else
             {
                 return movePlayer(opponent);
             }                
        }
        */
    }
        
 
             
    
    /**
     * placeWall - chooses row, col to add wall to the board
     */
    public Coord placeWall(Player opponent)
    {
        int row, col, orient;                                         // initialize my representation of walls
        String opponentRoute = opponent.getShortestPath(this);
        
        for (int i = 0; i < MAX; i++)
        {
            for (int j = 0; j < MAX; j++)
            {
                hasVerticalWall[i][j] = isWall(i+1, j+1, VER) || opponent.isWall(i+1, j+1, VER);
                hasHorizontalWall[i][j] = isWall(i+1, j+1, HOR) || opponent.isWall(i+1, j+1, HOR);
            }
        }
 
           
          
        if (!hasWall())                                                          // out of walls so move player instead
            {
                return move(opponent);  
            }   
             
        if (getPathLength(opponentRoute) < 3)                              //block 2 ahead
            return null;
                
        if (getPathRow(opponentRoute, 1) < getPathRow(opponentRoute, 2))   // moving left block the row before
            row = getPathRow(opponentRoute, 1);                            
        else
            row = getPathRow(opponentRoute, 2);
        
        
        if (getPathCol(opponentRoute, 1) < getPathCol(opponentRoute, 2))   // moving right block the columns before
            col = getPathCol(opponentRoute, 1);                           
        else
            col = getPathCol(opponentRoute, 2);
        
        if (getPathRow(opponentRoute, 2) == getPathRow(opponentRoute, 3))  // depending on which we we travel
            orient = VER;  // if row is same then we're traveling left or right, so vertical wall
        else
            orient = HOR;    
            
        if (isValidWall(opponent, row, col, orient))
            return new Coord(row, col, orient);
        else
            return null;
    }
    
     /**
     * movePlayer - chooses  row, col for player to move to.
     
    public Coord movePlayer(Player opponent)
    {
        String route = getShortestPath(opponent);
        int row = getPathRow(route, 1);
        int col = getPathCol(route, 1);
               
        if (row == opponent.getCurRow() && col == opponent.getCurCol())     // if we are blocked by opponent from next square
        {
            row = getPathRow(route, 2);
            col = getPathCol(route, 2);
        }
        
        printPath(route, row, col);
        return new Coord(row, col);
    }
     */
  /**
     * isValidWall - check for valid wall placement
     */
    public boolean isValidWall(Player opponent, int row, int col, int orient)
    {
        if (row < 1 || row > 8 || col < 1 || col > 8)
            return false;
        
        tryMove(new Coord(row, col, orient));
        if ((getPathLength(getShortestPath(opponent)) == 0) || (getPathLength(opponent.getShortestPath(this)) == 0))
        {
            return false;    
        }
        
        int i = row - 1;  // map from (1,1) coordinate to [0,0] index arrays
        int j = col - 1;
 
        if (orient == VER && !hasVerticalWall[i][j] && !hasHorizontalWall[i][j] && (i<=0 || !hasVerticalWall[i-1][j]) && (i>=7 || !hasVerticalWall[i+1][j]))            
            return true;
 
        if (orient == HOR && !hasHorizontalWall[i][j] && !hasVerticalWall[i][j] && (j<=0 || !hasHorizontalWall[i][j-1]) && (j>=7 || !hasHorizontalWall[i][j+1]))
            return true;
        
        return false;
    }
}

