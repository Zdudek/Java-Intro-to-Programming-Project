import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RoboPlayerMeToo here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoboPlayerMeToo extends Player
{
    private static final int MAX = 8;
    
    private boolean[][] hasVerticalWall = new boolean[MAX][MAX];
    private boolean[][] hasHorizontalWall = new boolean[MAX][MAX];

    
    /**
     * Random constructor - initializes random player
     */
    public RoboPlayerMeToo (int row, int col, int goal, String color)
    {
        super(row, col, goal, color);
    }
    
    
    /**
     * teamName - returns the team name. This should be SAME AS CLASS NAME!
     */
    public String teamName()
    {
        return "RoboPlayerMeToo";
    }
    
    
    /**
     * move - make a random move randomly.
     */
    public Coord move(Player opponent)
    {
        // note, this code simpler plus it won't step on itself while using tryMove()
        Coord nextPossibleStep = movePlayer(opponent);
        Coord nextPossibleWall = placeWall(opponent, 1);

        if (nextPossibleWall == null || Greenfoot.getRandomNumber(3)<1)  // switch between step & wall 1/3
            return nextPossibleStep;
        else
            return nextPossibleWall;
    }
    
    
    /**
     * movePlayer - chooses  row, col for player to move to.
     */
    public Coord movePlayer(Player opponent)
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
    
    
    /**
     * placeWall - chooses row, col to add wall to the board
     */
    public Coord placeWall(Player opponent, int dist)
    {
        boolean withGrain = false;
        int row, col, orient;
        String opponentRoute = opponent.getShortestPath(this);
        
        // initialize my representation of walls
        for (int i = 0; i < MAX; i++)
        {
            for (int j = 0; j < MAX; j++)
            {
                hasVerticalWall[i][j] = isWall(i+1, j+1, VER) || opponent.isWall(i+1, j+1, VER);
                hasHorizontalWall[i][j] = isWall(i+1, j+1, HOR) || opponent.isWall(i+1, j+1, HOR);
            }
        }

        // if we our out of walls, return with nothing
        if (!hasWall())
            return null;
        
        //  strategy to block 3 ahead, if 0 is our position then it's the 4th entry, so check we have that many
        if (getPathLength(opponentRoute) < dist + 1)
            return null;
        
        // determine if we go with or against grain in row or column
        if (getPathRow(opponentRoute, dist-1) < getPathRow(opponentRoute, dist) || getPathCol(opponentRoute, dist-1) < getPathCol(opponentRoute, dist))
            withGrain = true;
        
        // block opponenet at dist from player piece
        row = getPathRow(opponentRoute, dist);
        col = getPathCol(opponentRoute, dist);
            
        if (getPathRow(opponentRoute, dist-1) == getPathRow(opponentRoute, dist))  // depending on which we we travel
        {
            orient = VER;  // if row is same then we're traveling left or right, so vertical wall
            if (withGrain)
                col = getPathCol(opponentRoute, dist-1);
            }
        else
        {
            orient = HOR; // if row is different then we're traveling up or down, so horizontal
            if (withGrain)
                row = getPathRow(opponentRoute, dist-1);
        }
        
        // let's check wall placement...
        if (isValidWall(opponent, row, col, orient))
            return new Coord(row, col, orient);
        else
            return null;
    }
    
    
    /**
     * isValidWall - check for valid wall placement
     */
    public boolean isValidWall(Player opponent, int row, int col, int orient)
    {
        if (row < 1 || row > 8 || col < 1 || col > 8)
            return false;
        
        // add code here to try a move via tryMove() and check if shortest path
        // path is till reachable by you or opponent and if not, return false
        // ...also to be safe,  call tryMove(null) before returning false.
        
        int i = row - 1;  // map from (1,1) coordinate to [0,0] index arrays
        int j = col - 1;  // ditto

        if (orient == VER && !hasVerticalWall[i][j] && !hasHorizontalWall[i][j] && (i<=0 || !hasVerticalWall[i-1][j]) && (i>=7 || !hasVerticalWall[i+1][j]))            
            return true;

        if (orient == HOR && !hasHorizontalWall[i][j] && !hasVerticalWall[i][j] && (j<=0 || !hasHorizontalWall[i][j-1]) && (j>=7 || !hasHorizontalWall[i][j+1]))
            return true;
        
        return false;
    }
}
