import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.awt.Color;

/**
 * Write a description of class Name here.
 * 
 * @author Michael Jonas
 * @version October 15, 2012
 */
public class Text extends Actor
{
    /**
     * default contructor
     */
    public Text()
    {
    }
    
    
    /**
     * constructor - we just create a text image
     */
    public Text(String message)
    {
        GreenfootImage image = new GreenfootImage(message, 18, Color.BLACK, new Color(0,0,0,0));
        setImage(image); 
    }
}
