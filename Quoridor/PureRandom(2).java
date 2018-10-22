import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PureRandom here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PureRandom extends Player
{
    /**
     * Random constructor - initializes random player
     */
    public PureRandom (int row, int col, int goal, String color)
    {
        super(row, col, goal, color);
    }
    
    
    /**
     * teamName - returns the team name. This should be SAME AS CLASS NAME!
     */
    public String teamName()
    {
        return "PureRandom";
    }
    
    
    /**
     * move - make a random move randomly.
     */
    public Coord move(Player opponent)
    {
        Coord wallCoord;
        
        if (Greenfoot.getRandomNumber(4)<1)  // switch between step & wall 25/75
            return movePlayer(opponent);
        else
        {
            if (!hasWall())   // no walls we must move player
                return movePlayer(opponent);
            else
            {
                wallCoord = placeWall(opponent);
                if (wallCoord != null)  // found where to place wall
                    return wallCoord;
                else
                    return movePlayer(opponent);  // otherwise move player
            }
        }
    }

    
    /**
     * movePlayer - move player
     */
    public Coord movePlayer(Player opponent)
    {
        boolean validMove = false;
        int newRow;
        int newCol;
        
        do
        {
            newRow = getCurRow();
            newCol = getCurCol();
            
            if (Greenfoot.getRandomNumber(2)<1)  // 50% of time I move towards goal
            {
                if (getGoalRow() == Player.TOP)
                    newRow--;
                else
                    newRow++;
            }
            else  // the other 50%
            {
                if (Greenfoot.getRandomNumber(2)<1)  // 50% I move left or right
                    newCol--;
                else
                    newCol++;
            }
            validMove = isPathClear(getCurRow(), getCurCol(), newRow, newCol, opponent, true);
            
        } while (!validMove);
        
        return new Coord(newRow, newCol);
    }
    
    
    /**
     * placeWall - chooses row, col to add wall to the board
     */
    public Coord placeWall(Player opponent)
    {
        boolean validWall = false;
        int row, col, orient;
        
        if (!hasWall())
            return null;
        
        do 
        {
            row = Greenfoot.getRandomNumber(8) + 1;  // random valid row
            col = Greenfoot.getRandomNumber(8) + 1;  // random valid col
            
            if (Greenfoot.getRandomNumber(2) < 1)  // 50% of time place vertical versus horizontal wall
                orient = VER;
            else
                orient = HOR;
            
            validWall = !isWall(row, col, HOR) && !opponent.isWall(row, col, HOR);
            validWall = validWall && !isWall(row, col, VER) && !opponent.isWall(row, col, VER);
            
            if (orient == HOR)
            {
                if (col > 1)
                    validWall = validWall && !isWall(row, col-1, orient) && !opponent.isWall(row, col-1, orient);
                validWall = validWall && !isWall(row, col+1, orient) && !opponent.isWall(row, col+1, orient);
            }
            else
            {
                if (row > 1)
                    validWall = validWall && !isWall(row-1, col, orient) && !opponent.isWall(row-1, col, orient);
                validWall = validWall && !isWall(row+1, col, orient) && !opponent.isWall(row+1, col, orient);
            }
            
        } while (!validWall);
        
        return new Coord(row, col, orient);
    }
}
