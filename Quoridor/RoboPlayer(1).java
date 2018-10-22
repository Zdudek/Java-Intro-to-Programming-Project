import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RoboPlayer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoboPlayer extends Player
{
    private static final int MAX = 8;
    
    private boolean[][] hasVerticalWall = new boolean[MAX][MAX];
    private boolean[][] hasHorizontalWall = new boolean[MAX][MAX];

    
    /**
     * Random constructor - initializes random player
     */
    public RoboPlayer (int row, int col, int goal, String color)
    {
        super(row, col, goal, color);
    }
    
    
    /**
     * teamName - returns the team name. This should be SAME AS CLASS NAME!
     */
    public String teamName()
    {
        return "RoboPlayer";
    }
    
    
    /**
     * move - make a random move randomly.
     */
    public Coord move(Player opponent)
    {   
        boolean isBlockedFromGoal = false;
        Coord wallCoord;
        
        if (Greenfoot.getRandomNumber(3)<1)  // switch between step & wall 1/3
            return movePlayer(opponent);
        else
        {
            if (!hasWall())   // no walls we must move player
                return movePlayer(opponent);
            else
            {
                wallCoord = placeWall(opponent);
                if (wallCoord != null)  // found where to place wall
                {
                    // add some code using tryMove() 
                    // and getShortest path to figure it out
                    // NOTE: call tryMove(null) to clear your temp move
                    
                    if (isBlockedFromGoal)
                        return movePlayer(opponent);
                    else
                        return wallCoord;
                }
                else
                    return movePlayer(opponent);  // otherwise move player
            }
        }
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
    public Coord placeWall(Player opponent)
    {
        boolean validWall = false;
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
        if (getPathLength(opponentRoute) < 4)
            return null;
        
        // because of board coordinates up means you block the row before
        if (getPathRow(opponentRoute, 2) < getPathRow(opponentRoute, 3))
            row = getPathRow(opponentRoute, 2);  // this is one less because of how our coordinates are
        else
            row = getPathRow(opponentRoute, 3);
        
        // ...and moving right, again you block the columns before
        if (getPathCol(opponentRoute, 2) < getPathCol(opponentRoute, 3))
            col = getPathCol(opponentRoute, 2); // this is one less because of how our coordinates are
        else
            col = getPathCol(opponentRoute, 3);
        
        // we cannot place a wall outside of the 8x8 grid
        if (row > 8 || col > 8)
            return null;
        
        if (getPathRow(opponentRoute, 2) == getPathRow(opponentRoute, 3))  // depending on which we we travel
            orient = VER;  // if row is same then we're traveling left or right, so vertical wall
        else
            orient = HOR; // if row is different then we're traveling up or down, so horizontal
        
        // this section now only requires an if and an else
        int i = row - 1;  // map from (1,1) coordinate to [0,0] index arrays
        int j = col - 1;  // ditto
        if (orient == VER && !hasVerticalWall[i][j] && !hasHorizontalWall[i][j] && (i<=0 || !hasVerticalWall[i-1][j]) && (i>=7 || !hasVerticalWall[i+1][j]))            
            validWall = true;
        else if (orient == HOR && !hasHorizontalWall[i][j] && !hasVerticalWall[i][j] && (j<=0 || !hasHorizontalWall[i][j-1]) && (j>=7 || !hasHorizontalWall[i][j+1]))
            validWall = true;
            
        if (validWall)
            return new Coord(row, col, orient);
        else
            return null;
    }
}

/* CODE SNIPPET FOR LOOKAHEAD USING TRYMOVE FUNCTION
String route = getShorestPath(opponent);
int row = getPathRow(route, 1);
int col = getPathCol(route, 1);
boolean randomMove = false;

Coord move = new Coord(row, col);
tryMove(move);

Coord prevMove = opponent.move():
for (int i=0; i<20; i++)
{
    Coord curMove = opponent.move();
    if ( curMove.isPiece() != prevMove.isPiece() )
    {
        randomMove = true;
        break;
    }
    prevMove = curMove;
}

if (!randomMove)
{
    if  (oppMove.getRow() == opponet.getGoalRow() )
    {
        return nextPossibleWall;
    }
}
*/
