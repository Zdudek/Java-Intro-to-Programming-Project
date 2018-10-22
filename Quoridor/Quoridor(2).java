import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Quoridor here.
 * 
 * @author Michael Jonas
 * @version October 15, 2012
 */
public class Quoridor extends World
{

    /**
     * Constructor for objects of class Quoridor.
     * 
     */
    public Quoridor()
    {    
        // Create a new world with 400x400 cells with a cell size of 1x1 pixels.
        super(400, 450, 1); 
        
        Board game = new Board();
        addObject(game, 200, 200);
        game.setupBoard();
    }
}
