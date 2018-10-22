import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Wall here.
 * 
 * @author Michael Jonas
 * @version October 15, 2012
 */
public class Wall extends Actor
{
    // instance variables - replace the example below with your own
    private int row;
    private int col;
    private int orient;
    
    
    /**
     * Constructor for Wall
     */
    public Wall(int r, int c, int o)
    {
        row = r;
        col = c;
        orient = o;
    }
    
    
    /**
     * setRow - setter for row
     */
    public void setRow(int index)
    {
        row = index;
    }
    
    
    /**
     * getRow - getter for row
     */
    public int getRow()
    {
        return row;
    }
    
    
    /**
     * setCol - setter for col
     */
    public void setCol(int index)
    {
        col = index;
    }
    
    
    /**
     * getCol - getter for col
     */
    public int getCol()
    {
        return col;
    }
        
    
    /**
     * setOrient - setter for orient
     */
    public void setOrient(int orientation)
    {
        orient = orientation;
    }
    
    
    /**
     * getOrient - getter for orient
     */
    public int getOrient()
    {
        return orient;
    }
}
