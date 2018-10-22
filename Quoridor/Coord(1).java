import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Coord here.
 * 
 * @author Michael Jonas 
 * @version October 20, 2012
 */
public class Coord extends Actor
{
    private int row;
    private int col;
    private boolean piece;    // true for player piece, false for wall
    private int orient;  // walls are VERtical or HORizontal
    
    
    /**
     * Constructor for piece - creates coordinate for piece
     */
    public Coord(int r, int c)
    {
        row = r;
        col = c;
        piece = true;
        orient = -1;
    }
    
    
    /**
     * Constructor for wall - creates coordinate for wall
     */
    public Coord(int r, int c, int o)
    {
        row = r;
        col = c;
        piece = false;
        orient = o;
    }
    
    
    /**
     * getRow - returns row
     */
    public int getRow()
    {
        return row;
    }
    
    
    /**
     * getCol - returns col
     */
    public int getCol()
    {
        return col;
    }
    
    
    /**
     * getOrient - returns wall orientation
     */
    public int getOrient()
    {
        return orient;
    }
    
    
    /**
     * isWall - returns true if wall
     */
    public boolean isWall()
    {
        return !piece;
    }
    
    
    /**
     * isPiece - return true if piece
     */
    public boolean isPiece()
    {
        return piece;
    }
}
