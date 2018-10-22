import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Board here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Board extends Actor
{
    private static final int MAX_PLAYERS = 4;
    private boolean[][] battleMatrix = new boolean[MAX_PLAYERS][MAX_PLAYERS];
    private Player[] battleList = new Player[MAX_PLAYERS];
    private int playerOne;
    private int playerTwo;
    
    private Player bluePlayer;
    private Player redPlayer;
    
    private int playerMove;
    private int playerSkip;
    private boolean restartGame;
    private boolean resetGame;
    private boolean stopGame;
    
    
    /**
     * Constructor for Board class
     */
    public Board()
    {
        initPlayers();
    }
    
    
    /**
     * initPlayers - initializes players
     */
    public void initPlayers()
    {
        restartGame = false;
        stopGame = false;
        playerOne = 0;
        playerTwo = 1;

        initGame();
        initBattle();
    }
    
    
    /**
     * initGame - initializes a game in the battle
     */
    public void initGame()
    {
        playerMove = 1;
        playerSkip = 1;
        resetGame = false;
    }
    
    
    /**
     * initBattle - initiazlie the battle
     */
    public void initBattle()
    {
        // initialize our battle...diagonal not for batteling
        for (int i = 0; i < MAX_PLAYERS; i++)
            for (int j = 0; j < MAX_PLAYERS; j++)
                if (i == j)
                    battleMatrix[i][j] = false;
                else
                    battleMatrix[i][j] = true;

        // list of competitors
        battleList[0] = new BelisleDudek(Player.TOP, Player.MID, Player.BOT, "Red");
        battleList[1] = new ProfJonas(Player.TOP, Player.MID, Player.BOT, "Red");
        battleList[3] = new RoboPlayerMeToo (Player.TOP, Player.MID, Player.BOT, "Red");
        battleList[2] = new BelisleDudek(Player.TOP, Player.MID, Player.BOT, "Red");
       
       
    }
    
    
    /**
     * setupBoard - sets up game board
     */
    public void setupBoard()
    {
        String name;
        
        redPlayer = battleList[playerOne].resetPlayer(Player.TOP, Player.MID, Player.BOT, "Red");
        bluePlayer = battleList[playerTwo].resetPlayer(Player.BOT, Player.MID, Player.TOP, "Blue");
        
        // first remove any old walls and players
        getWorld().removeObjects(getWorld().getObjects(Text.class));
        getWorld().removeObjects(getWorld().getObjects(Wall.class));
        getWorld().removeObjects(getWorld().getObjects(Player.class));
        
        // then set up the board with players and game info
        getWorld().addObject(new Text(Player.VERSION), 386, 442);

        getWorld().addObject(redPlayer, 0, 0);
        getWorld().addObject(new Text(redPlayer.teamName()), 175, 414);
        
        getWorld().addObject(bluePlayer, 0, 0);
        getWorld().addObject(new Text(bluePlayer.teamName()), 175, 438);
        
        redPlayer.setImage(new GreenfootImage(redPlayer.getColor()+Player.PIECE));
        redPlayer.placePiece(redPlayer.getCurRow(), redPlayer.getCurCol());
        
        bluePlayer.setImage(new GreenfootImage(bluePlayer.getColor()+Player.PIECE));
        bluePlayer.placePiece(bluePlayer.getCurRow(), bluePlayer.getCurCol());
    }
    
    
    /**
     * clearBoard - initializes a game in the battle
     */
    public void clearBoard()
    {
        // first remove any old walls and players
        getWorld().removeObjects(getWorld().getObjects(Text.class));
        getWorld().removeObjects(getWorld().getObjects(Wall.class));
        getWorld().removeObjects(getWorld().getObjects(Player.class));
        
        // then set up the board with players and game info
        getWorld().addObject(new Text(Player.VERSION), 386, 442);
        
        getWorld().addObject(new Text("***GAME***"), 175, 414);
        getWorld().addObject(new Text("***OVER***"), 175, 438);
    }
    
    
    /**
     * Act - do whatever the Game wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if (stopGame)
        {
            clearBoard();
            Greenfoot.stop();
        }
        else if (restartGame)
        {
            initPlayers();
            
            Greenfoot.delay(2);               
            setupBoard();
            Greenfoot.delay(1);
        }
        else if (resetGame)  // if we're in AUTOPLAY mode then this gets activated at end of games
        {
            while (true)
            {
                // play next game
                playerTwo++;
                if (playerTwo >= MAX_PLAYERS)
                {
                    playerTwo = 0;
                    playerOne++;
                    if (playerOne >= MAX_PLAYERS)
                    {
                        playerOne = 0;
                        if (Player.AUTOPLAY && Player.CONTINUE)
                            restartGame = true;
                        else
                            stopGame = true;
                        break;
                    }
                    break;
                }
                
                // let's see if we've already done this
                if (battleMatrix[playerOne][playerTwo])
                {
                    battleMatrix[playerOne][playerTwo] = false;
                    break;
                }
            }
            
            // if we aren't restarting then play next game
            if (!restartGame && !stopGame)
            {
                Greenfoot.delay(2);
                initGame();
                setupBoard();
                Greenfoot.delay(1);
            }
        }
        else if (checkForWinner(bluePlayer) || checkForWinner(redPlayer) || (redPlayer.isDead() || bluePlayer.isDead()))
        {
            if (Player.AUTOPLAY)
            {
                resetGame = true;
                if (!Player.CONTINUE || (!Player.NONSTOP && (redPlayer.isDead() || bluePlayer.isDead())))
                   Greenfoot.stop();
            }
            else
                Greenfoot.stop();
        }
        else 
        {
            if (playerMove == 1)
            {
                if (!redPlayer.isDead() && !movePlayer(redPlayer, bluePlayer)) // check for invalid move
                {
                    redPlayer.setDead();
                    redPlayer.setImage(new GreenfootImage(redPlayer.getColor() + Player.DEAD));
                    //playerMove = 2; playerSkip = 2;  // skip player 1 from now on
                }
            }
            else if (playerMove == 2)
            {
                if (!bluePlayer.isDead() && !movePlayer(bluePlayer, redPlayer)) // check for invalid move
                {
                    bluePlayer.setDead();
                    bluePlayer.setImage(new GreenfootImage(bluePlayer.getColor() + Player.DEAD));
                    //playerMove = 1; playerSkip = 0;  // skip player 2 from now on
                }
            }
            playerMove = playerMove % 2 + playerSkip;  // toggle between players
        }
    }
    
    
    /**
     * movePlayer - make a player move, checking for validity
     */
    public boolean movePlayer(Player me, Player opponent)
    {
        int meRow, meCol, meWalls, opRow, opCol, opWalls;
        
        // get current board configuration
        meRow = me.getCurRow();
        meCol = me.getCurCol();
        meWalls = me.getNumWallsUsed();
        opRow = opponent.getCurRow();
        opCol = opponent.getCurCol();
        opWalls = opponent.getNumWallsUsed();
        
        Coord move = me.play(opponent);
        
        // if something has changed then we return bad move
        if (meRow != me.getCurRow() || meCol != me.getCurCol() || meWalls != me.getNumWallsUsed() ||
            opRow != opponent.getCurRow() || opCol != opponent.getCurCol() || opWalls != opponent.getNumWallsUsed())
            return false;
        
        if (move.isPiece())
        {
            return step(move.getRow(), move.getCol(), me, opponent);
        }
        else
        {
            return addWall(move.getRow(), move.getCol(), move.getOrient(), me, opponent);
        }
    }
    
    
    /**
     * step - steps piece in the current direction.
     */
    public boolean step(int newRow, int newCol, Player me, Player opponent)
    {
        // capture where we are now
        int oldRow = me.getCurRow();
        int oldCol = me.getCurCol();
        
        // we definitely can't move off board
        if (newRow < 1 || newRow > 9 || newCol < 1 || newCol > 9)
            return false;
        
        // we have to move while stepping
        if (oldRow == newRow && oldCol == newCol)
            return false;
        
        // pace the piece
        me.placePiece(newRow, newCol);
        
        // check to see if we went through walls or stepped on opponent.
        return me.isPathClear(oldRow, oldCol, newRow, newCol, opponent, true);
    }
    
    
    /**
     * addWall - add a wall to our list
     */
    public boolean addWall(int row, int col, int orient, Player me, Player opponent)
    {
        boolean validWall = true;
        
        if (
            !me.hasWall() ||                                                              // no more walls
            row < 1 || row > 8 || col < 1 || col > 8 ||                                   // out of bounds
            me.isWall(row, col, Player.HOR) || opponent.isWall(row, col, Player.HOR) ||   // already a wall there
            me.isWall(row, col, Player.VER) || opponent.isWall(row, col, Player.VER)      // ...or crossing wall
           )
        {
            validWall = false;
        }
        else if (orient == Player.VER)   // check for neighboring walls in the vertical
        {
            if (row > 1 && (me.isWall(row-1, col, orient) || opponent.isWall(row-1, col, orient)))
                validWall = false;
            if (row < 8 && (me.isWall(row+1, col, orient) || opponent.isWall(row+1, col, orient)))
                validWall = false;
        }
        else   // otherwise check for neighboring walls in the vertical
        {
            if (col > 1 && (me.isWall(row, col-1, orient) || opponent.isWall(row, col-1, orient)))
                validWall = false;
            if (col < 8 && (me.isWall(row, col+1, orient) || opponent.isWall(row, col+1, orient)))
                validWall = false;
        }
        
        // place the  wall, even if we already know it's not valid
        validWall = me.placeWall(row, col, orient) && validWall;
        
        // let's be sure we still can both get to our goal state
        validWall = validWall && me.getPathLength(me.getShortestPath(opponent)) > 0;
        validWall = validWall && opponent.getPathLength(opponent.getShortestPath(me)) > 0;
        
        return validWall;
    }
    
    
    /**
     * checkForWinner - checks to see if we have a winner.
     */
    public boolean checkForWinner(Player player)
    {
        if (player.hasWon())
        {
            player.setImage(new GreenfootImage(player.getColor() + Player.WINNER));
            return true;
        }
        return false;
    }
}
