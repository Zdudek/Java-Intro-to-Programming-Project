import greenfoot.*;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Player. This is an abstract base class for all players. In addition to the standard Actor
 * methods, it provides the ability to move and determine winner. The move class must be
 * defined by the inheriting class.
 * 
 * @author Michael Jonas
 * @version October 15, 2012
 */
public abstract class Player extends Actor
{
    // class constants
    public static final boolean AUTOPLAY = true;   // allow multiple games to be played
    public static final boolean CONTINUE = false;  // pause between games
    public static final boolean NONSTOP = false;   // pause between illegal player moves
    
    public static final int MAX_WALLS = 10;
    public static final int TOP = 1;
    public static final int BOT = 9;
    public static final int MID = 5;
    public static final int HOR = -1;
    public static final int VER = -2;
    public static final String VERSION = "v13";
    public static final String PIECE = "Player.png";
    public static final String DEAD = "Dead.png";
    public static final String WINNER = "Winner.png";
    public static final String V_WALL = "VerticalWall.png";
    public static final String H_WALL = "HorizontallWall.png";

    // object constatns
    private final int EDGE = 28;
    private final double STEP = 43.65;
    
    // object variables
    private Text message;
    
    private String searchQueue;  // queue breadth first search
    private int searchIndex;     // indexes best path

    private Coord look;          // temporary move we look at
    
    private Wall[] list;
    private int numWalls;
    
    private int curRow;
    private int curCol;
    private int goalRow;
    private String color;
    private boolean dead;
    
    
    /**
     * Constructor for Player - initialize row, col and goal.
     */
    public Player(int row, int col, int goal, String paint)
    {
        resetPlayer(row, col, goal, paint);
    }
    
    
    /**
     * resetPlayer - intializes the player
     */
    public Player resetPlayer(int row, int col, int goal, String paint)
    {
        message = null;
        look = null;
        list = new Wall[MAX_WALLS];
        numWalls = 0;
        curRow = row;
        curCol = col;
        goalRow = goal;
        color = paint;
        dead = false;
        
        return this;
    }
    
    /**
     * getCurRow - get current row.
     */
    public int getCurRow()
    {
        // if it's a temporary move, return where we will be
        if (look != null && look.isPiece())
            return look.getRow();
        else
            return curRow;
    }
    
    
    /**
     * getCurCol - get current column
     */
    public int getCurCol()
    {
        // if it's a temporary move, return where we will be
        if (look != null && look.isPiece())
            return look.getCol();
        else
            return curCol;
    }
    
    
    /**
     * getCurRow - get goal row
     */
    public int getGoalRow()
    {
        return goalRow;
    }
    
    
    /**
     * isDead - has the player died
     */
    public boolean isDead()
    {
        return dead;
    }
    
    
    /**
     * setDead - the player has just died
     */
    public void setDead()
    {
        dead = true;
    }
    
    
    /**
     * getColor - get color
     */
    public String getColor()
    {
        return color;
    }
    
    
    /**
     * teamName - returns the team name. Implement as return with string (no need for instance variable)
     */
    public abstract String teamName();
    
    
    /**
     * move - abstract method which needs to be implemented by inherited class.
     */
    public abstract Coord move(Player player);
    
    
    /**
     * tryMove - try a move without committing
     */
    public void tryMove(Coord action)
    {
        look = action;
    }
    
    
    /**
     * play - this function gets called to play a move
     */
    public Coord play(Player player)
    {
        // calls childs move function
        Coord action = move(player);
        
        // clears out any temporary move tried
        look = null;
        
        return action;
    }
    
    
    /**
     * placeOnBoard - places piece on board
     */
    public void placePiece(int row, int col)
    {
       setLocation((int)(EDGE+STEP*(col-1)), (int)(EDGE+STEP*(row-1)));
       curRow = row;
       curCol = col;
    }
    
   
    /**
     * isPathClear - checks to see if path is clear
     */
    public boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol, Player opponent, boolean blocking)
    {
        if (opponent == null) // cannot check for clear path without opponent's info
            return false;
        
        if (newRow < 1 || newRow > 9 || newCol < 1 || newCol > 9) // out of bounds
            return false;
                
        if (newRow != oldRow && newCol != oldCol) // no diaganol movement unless jump
            if (!(opponent.getCurRow() == newRow && opponent.getCurCol() == oldCol) &&
                !(opponent.getCurRow() == oldRow && opponent.getCurCol() == newCol))
            {
                return false;
            }
            else
            {
                // we've done a diagonal jump, so pretend we're our opponent
                oldRow = opponent.getCurRow();
                oldCol = opponent.getCurCol();
            }
        
        if (Math.abs(newRow-oldRow) > 1 || Math.abs(newCol-oldCol) > 1)  // only one step unless jump
            if ((newRow > oldRow && opponent.getCurRow() != oldRow + 1) &&
                (newRow < oldRow && opponent.getCurRow() != oldRow - 1) &&
                (newCol > oldCol && opponent.getCurCol() != oldCol + 1) &&
                (newCol < oldCol && opponent.getCurCol() != oldCol - 1))
            {
                return false;
            }
            else
            {
                // we've done a normal jump, so pretend we're our opponent
                oldRow = opponent.getCurRow();
                oldCol = opponent.getCurCol();
            }
        
        // if we aren't ignoring our opponent
        if (blocking && newRow == opponent.getCurRow() && newCol == opponent.getCurCol())
            return false;
        
        // check for blocking walls
        if (newRow < oldRow)
        {
            return checkForWalls(newRow, newCol, HOR, opponent);
        }
        else if (newRow > oldRow)
        {
            return checkForWalls(oldRow, newCol, HOR, opponent);
        }
        else if (newCol < oldCol)
        {
            return checkForWalls(newRow, newCol, VER, opponent);
        }
        else if (newCol > oldCol)
        {
            return checkForWalls(newRow, oldCol, VER, opponent);
        }
        else
            return false;  // didn't move
    }
    
    
    /**
     * checkForWalls - checks to see if walls are in our path
     */
    public boolean checkForWalls(int row, int col, int orient, Player opponent)
    {
        boolean isClear;
        
        isClear = !isWall(row, col, orient) && !opponent.isWall(row, col, orient);

        if (orient == HOR && col > 1)
        {
            isClear = isClear && !isWall(row, col-1, orient) && !opponent.isWall(row, col-1, orient);
        }
        else if (orient == VER && row > 1)
        {
            isClear = isClear && !isWall(row-1, col, orient) && !opponent.isWall(row-1, col, orient);
        }
        
        return isClear;
    }
    
    
    /**
     * getNumWalls - how many walls has player used
     */
    public int getNumWallsUsed()
    {
        if (look != null && look.isWall())  // for temporary moves
            return numWalls+1;
        else
            return numWalls;
    }
    
    
    /**
     * getWalls - returns the ith wall
     */
    public Coord getWall(int index)
    {
        if (index < numWalls)
            return new Coord(list[index].getRow(), list[index].getCol(), list[index].getOrient());
        else if (look != null && look.isWall() && index == numWalls)
            return look;    // for temporary moves
        else
            return null;
    }
    
    
    /**
     * hasWall - does player have walls left
     */
    public boolean hasWall()
    {
        if (look != null && look.isWall())  // for temporary moves
            return numWalls+1 < MAX_WALLS;
        else
            return numWalls < MAX_WALLS;
    }
    
    
    /**
     * isWall - check to see if there's a wall in place alread
     */
    public boolean isWall(int row, int col, int orient)
    {
        for (int i=0; i<numWalls; i++)
        {
            if (list[i].getRow() == row && list[i].getCol() == col && list[i].getOrient() == orient)
                return true;
        }
        
        // for temporary moves we check this too
        if (look != null && look.isWall())
            return look.getRow() == row && look.getCol() == col && look.getOrient() == orient;
        else
            return false;
    }
    
    
    /**
     * placeWall - places a wall
     */
    public boolean placeWall(int row, int col, int orient)
    {
        if (numWalls >= MAX_WALLS)
            return false;
        
        Wall wall = new Wall(row, col, orient);
        getWorld().addObject(wall, 0, 0);
        list[numWalls++] = wall;
        
        if (orient == VER)
            wall.setImage(new GreenfootImage(color + V_WALL));
        else
            wall.setImage(new GreenfootImage(color + H_WALL));

        wall.setLocation((int)(EDGE+STEP*(col-1)), (int)(EDGE+STEP*(row-1)));
        
        return true;
    }
    
    
    /**
     * hasWon - check to see if player has won.
     */
    public boolean hasWon()
    {
        return goalRow == curRow;
    }
    
    
    /**
     * getShortestPath - returns shortest path to user
     */
    public String getShortestPath(Player opponent)
    {
        searchQueue = "";
        searchIndex = 0;
        addNode(-1, curRow, curCol);

        if (shortestPath(searchIndex, curRow, curCol, opponent))
            return findPath(searchIndex);      
        else
            return "";
    }
    
    
    private boolean shortestPath(int index, int row, int col, Player opponent)
    {
        boolean reachedGoal = false;
        int startLen, stopLen, newRow, newCol;
        
        startLen = getQueueLength();
        while (!reachedGoal)
        {
            addChildren(index, row, col, opponent);
            
            stopLen = getQueueLength();

            if (startLen == stopLen)  // we've exhaustively searched
                break;

            for (int i = startLen; i < stopLen; i++)
            {
                newRow = getQueueRow(i);
                newCol = getQueueCol(i);
                addChildren(i, newRow, newCol, opponent);
                if (newRow == goalRow && (newRow != opponent.getCurRow() || newCol != opponent.getCurCol()))
                {
                   searchIndex = i;
                   reachedGoal = true;
                   break;
                }
            }
            startLen = stopLen;
        }
        
        return reachedGoal;
    }
    
    
    /**
     * addChildren - add children to our search list
     */
    private void addChildren(int index, int row, int col, Player opponent)
    {
        if (isPathClear(row, col, row+1, col, opponent, false))
           addNode(index, row+1, col);
        if (isPathClear(row, col, row-1, col, opponent, false))
           addNode(index, row-1, col);
        if (isPathClear(row, col, row, col+1, opponent, false))
           addNode(index, row, col+1);
        if (isPathClear(row, col, row, col-1, opponent, false))
           addNode(index, row, col-1);
    }
    
    
    /**
     * addNode - add a child node
     */
    private void addNode(int index, int row, int col)
    {
        // using strings for now...each node looks like "r,c:##" where ## is a trace back index to start
        String key = row + "," + col + ":";
        
        if (!searchQueue.contains(key))
            if (index >=0 && index <=9)
                searchQueue += key + "0" + index + ";";
            else if (index <=99)
                searchQueue += key + index + ";";
    }
    
    
    /**
     * findPath - returns path from start to goal
     */
    private String findPath(int index)
    {
       String route = "";
       
       while (index >= 0)
       {
           route = "(" + getQueueRow(index) + "," + getQueueCol(index) + ")" + route;
           index = getQueueIndex(index);
       }
       
       return route;
    }
    
    
    /**
     * getQueueRow - get row for i'th queue entry
     */
    private int getQueueRow(int i)
    {
        int row = 0;
        int offset = i * 7;
        
        if (getQueueLength() > i)
            row = Integer.parseInt(searchQueue.substring(0+offset, 1+offset));
        
        return row;
    }
    

    /**
     * getQueueCol - get col for i'th queue entry
     */
    private int getQueueCol(int i)
    {
        int col = 0;
        int offset = i * 7;
        
        if (getQueueLength() > i)
            col = Integer.parseInt(searchQueue.substring(2+offset, 3+offset));
        
        return col;
    }
    

    /**
     * getQueueIndex - get index pointing to previous item for i'th queue entry
     */
    private int getQueueIndex(int i)
    {
        int index = 0;
        int offset = i * 7;
        
        if (getQueueLength() > i)
            index = Integer.parseInt(searchQueue.substring(4+offset, 6+offset));
        
        return index;
    }
    
    
    /**
     * getSearchLength - get number of nodes in search queue
     */
    private int getQueueLength()
    {
        return searchQueue.length() / 7;
    }
    
    
    /**
     * getPathRow - get row for i'th step
     */
    public int getPathRow(String route, int i)
    {
        int row = 0;
        int offset = i * 5;
        
        if (getPathLength(route) > i)
            row = Integer.parseInt(route.substring(1+offset, 2+offset));
        
        return row;
    }
    

    /**
     * getPathCol - get col for i'th step
     */
    public int getPathCol(String route, int i)
    {
        int col = 0;
        int offset = i * 5;
        
        if (getPathLength(route) > i)
            col = Integer.parseInt(route.substring(3+offset, 4+offset));
        
        return col;
    }
    
    
    /**
     * getTreeLength - get number of steps in route path
     */
    public int getPathLength(String route)
    {
        return route.length() / 5;
    }
    
    
    /**
     * printPath - prints path list on the board
     */ 
    public void printPath(String route, int row, int col) 
    {
        String txt;
        
        if (message == null)
            getWorld().addObject(message = new Text(), 0, 0);
        
        if (route.length() > 55)
            txt = route.substring(5, 55) + "...";
        else if (route.length() > 5)
            txt = route.substring(5) + "!";
        else
            txt = route;
        
        message.setImage(new GreenfootImage(txt, 25, Color.WHITE, new Color(0,0,0,0))); 
        message.setLocation((int)(EDGE+STEP*(col-1)), (int)(EDGE+STEP*(row-1)));
    }
}