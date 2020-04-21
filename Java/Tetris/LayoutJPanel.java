import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.awt.geom.Line2D;

public class LayoutJPanel extends JPanel implements ActionListener, KeyListener
{

  //General shape coordinates
  private int x = 0, y = 0;
  
  //Preview shape coordinates
  private int x2 = 385, y2 = 205;
  
  private Random random = new Random(); 
  
  //Default game speed; as the player progresses, the speed at which the shapes fall increases
  private Timer t = new Timer(500, this);
  private int timeUpdater = 500;
  private boolean timeSwitch = true;
  
  //Player scoring; also affects game speed
  private int totalScore = 0;
  private double lineValue[] = new double[] {40, 200, 300, 1200};
  private int scoreCombo = 0;
  private int currentDifficulty = 0;
  private int lineCounter = 0;
  
  //Throttles the speed at which players can move the shapes
  private long timePrevious = 0;
  private long timeCurrent = 0;
  private long timeCheck = 0;
  
  //Picks a shape at random and draws it every cycle till placed
  private int nextShape = random.nextInt(7);
  private int shapeType = random.nextInt(7);
  //private int shapeType = 6;
  
  //Indicator that helps reset the coordinates for newly made shapes
  private boolean shapeSpawn = true;
  
  //Indicator for current shape's turning position
  private String shapeTransform = "DEFAULT";
  
  //GracePeriod gives the player time to move their shape when ontop of another shape; based on difficulty level
  private int gracePeriod[] = new int[] {2, 2, 2, 2, 3, 3, 3, 4, 4, 6};
  private int graceCounter = 2;
  
  //Prevents using key events between shape creation - 
  //shouldn't really need, but I was getting OOB exceptions by smashing my fist onto the arrow keys
  private boolean shapeDelay = false;
  
  //Lose condition check
  private boolean loseCheck = false;
  
  //Tetris board: 10 x 22; only 10 x 20 is drawn on the panel since shapes are created above player vision
  private int arr[][] = new int[10][22];
  
  //Color Palette for blocks
  private Color[] shapeColors = new Color[] {Color.GRAY, new Color(255, 136, 17), Color.WHITE, 
  new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17)}; 
  
  //private Color[] shapeColors = new Color[] {Color.GRAY, new Color(255, 136, 17), Color.WHITE, 
  //new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17)};
  

  
  
  
  public void paintComponent( Graphics g) 
  {    
  
    double velX = 2, velY = 2;
    
    double width = getWidth();
    double height = getHeight();

    addKeyListener(this);
    requestFocus();
    setFocusable(true);
    
    super.paintComponent( g );
    Graphics2D g2d = (Graphics2D) g;
    GeneralPath coordinates = new GeneralPath();
    GeneralPath divider = new GeneralPath();
    
    
    ////Origin
  //g2d.translate(0, 0);

  //Scales to window size
  //g2d.scale(width/300, height/600);
    
    //Draws board gridlines to Jpanel
    g2d.setStroke(new BasicStroke(1));
    for(int i = 0; i <= 300; i+=30)
    {
      coordinates.moveTo(i,600);
      coordinates.lineTo(i,0);
    }
      
    for(int i = 0; i <= 600; i+=30)
    {
      coordinates.moveTo(300,i);
      coordinates.lineTo(0,i);
    }
    
    g2d.setColor(Color.BLACK);
    g2d.draw(coordinates);
    
    

    //Checks tetris board for all block locations and fills them
    for(int i = 0; i < 10; i++)
    {
      for(int j = 0; j < 22; j++)
      {
        if(arr[i][j] != 0)
        {
          
          g2d.setColor(shapeColors[(arr[i][j])]);
          g2d.fillRect((i * 30) + 1, (j * 30) - 59, 29, 29);
        }
      }
    }
    
    //Draws current shapes to the JPanel and updates them every cycle
    drawShape(g2d);
    
    if(loseCheck)
    {     
       setBackground(Color.BLACK);
    }
    
    else {setBackground(shapeColors[0]);}
    
    //Draws out the preview info field and the next shape
    g2d.setStroke(new BasicStroke(10));
    divider.moveTo(306,0);
    divider.lineTo(306,600);
    g2d.setColor(Color.BLACK);
    g2d.draw(divider);
    
    g2d.setColor(Color.GRAY);
    g2d.fillRect(305, 0, 195, 600);
    
    g2d.setColor(Color.BLACK);
    g2d.fillRect(325, 100, 155, 155);
    
    drawPreview(g2d);
    

    
    //Restores key events for player when shape is in play
    if(!shapeDelay)
    {
      shapeDelay = true;
    }
      
    
  //coordinate testing
  System.out.println("x value is:" + x);
  System.out.println("y value is:" + y);
  //System.out.println();
    
    if(timeSwitch)
    {
      t.setDelay(timeUpdater);
      t.restart();
      timeSwitch = false;
    }
  }
  
  public void printBoard()
  {
    for(int i = 0; i < 22; i++)
    {
    System.out.println();
      for(int j = 0; j < 10; j++)
      {
        System.out.print(arr[j][i] + " ");
      }
    }
    System.out.println();
  }
 
   //Action event that triggers through a timer cycle
  public void actionPerformed(ActionEvent e)
  {
    
    //If losing condition was met, exit the game
    if(loseCheck)
    {     

     try 
      {  
        TimeUnit.MILLISECONDS.sleep(10000);
      } 
      
      catch (InterruptedException r)
      {
        
      }
      
      System.exit(0);
    }

    
    int check = placementCheck();

    if(check == 1)
    {
    
      //Variable reset for next shape
      shapeType = nextShape;
      nextShape = random.nextInt(7);
      shapeSpawn = true;
      shapeTransform = "DEFAULT";
      shapeDelay = false;
      
      //Coordinate reset for next shape
      x = 0;
      y = 0;
       
      //Checks rows to see if they are completed; increases player score and squashes board if so
      for(int i = 21; i > 0; i--)
      {
        int counter = 0;
      
        for(int j = 0; j < 10; j++)
        {
          if(arr[j][i] != 0)
          {
            counter++;
          }
        }
        
        if(counter == 10)
        {
          scoreCombo++;
          for(int j = i; j > 0; j--)
          {
            for(int k = 0; k < 10; k++)
            {
              arr[k][j] = arr[k][j - 1];
            }
          }
          i++;
        }
      }
      
      if(scoreCombo > 0)
      {
        totalScore += lineValue[scoreCombo - 1] * (currentDifficulty + 1);
        lineCounter += scoreCombo;
        
        if(lineCounter >= 10 && currentDifficulty != 11)
        {
          lineCounter = lineCounter % 10;
          
          timeUpdater = 300 - (currentDifficulty * 25);
          timeSwitch = true;
        }
        
        scoreCombo = 0;
      }
      
      graceCounter = gracePeriod[currentDifficulty];
    
      //Checks the board if the losing condition is met
      for(int i = 0; i < 10; i++)
      {
        if(arr[i][0] != 0 || arr[i][1] != 0)
          {
            loseCheck = true;
            break;
          }
      }
        
      //print test
      printBoard();
        
      repaint();
      return;
    }
    
    else if(check == 2)
    {
      graceCounter = gracePeriod[currentDifficulty];
      y += 30;
      repaint();
    }
  }
  
  //Key events; players primarily use the arrow keys to adjust the shapes
  public void keyPressed(KeyEvent e)
  {
    //Pauses event until a shape is created
    if(!shapeDelay)
    {
      return;
    }
      
      timeCurrent = System.currentTimeMillis();
      timeCheck = timeCurrent - timePrevious;
  
    if (timeCheck < 0 || timeCheck > 50) 
    {
      timePrevious = timeCurrent;
      
      int key = e.getKeyCode();
      
      //Key events; deals with moving the shapes, checking collision, and shape rotation
      if (key == KeyEvent.VK_LEFT) 
      {
        switch(shapeType) 
        {
        //first statement checks if shape is near wall; second: checks for collision with other shapes
        
          //Square Block
          case 0 :
          
            if(x != 0 
              && arr[(x - 30)/30][(y + 0)/30] == 0
              && arr[(x - 30)/30][(y + 30)/30] == 0)
              {
                x -= 30;
              }
              
              break;
          
          //Long Block
          case 1 :
              
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 0 
                  && arr[(x - 30)/30][(y)/30] == 0
                  && arr[(x - 30)/30][(y + 30) / 30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0
                  && arr[(x - 30)/30][(y + 90)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
          
          //T Block
          case 2 :
          
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 30)/30][(y + 0)/30] == 0
                  && arr[(x - 60)/30][(y + 30)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 0 
                  && arr[(x - 30)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 30 
                  && arr[(x - 30)/30][(y + 0)/30] == 0
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
              
          //Z Block
          case 3 :
          
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 0 
                  && arr[(x + 0)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
           
          //S Block   
          case 4 :
          
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 0)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 0)/30] == 0
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
              
          //L Block
          case 5 :
    
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x + 0)/30][(y + 0)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 0 
                  && arr[(x - 30)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 60)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
              
          //J Block
          case 6 :
    
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x - 60)/30][(y + 0)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 0 
                  && arr[(x - 30)/30][(y + 0)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 30)/30] == 0
                  && arr[(x + 0)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 30 
                  && arr[(x - 60)/30][(y + 60)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 0)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
        }
      }
    
      else if (key == KeyEvent.VK_RIGHT) 
      {
        switch(shapeType) 
        {
          //Square block
          case 0 :
          
            if(x != 240
            && arr[(x + 60)/30][(y + 0)/30] == 0
            && arr[(x + 60)/30][(y + 30)/30] == 0)
            {
              x += 30;
            }
              break;
              
          //Long block
          case 1 :
          
               switch(shapeTransform)
                {
                case "DEFAULT" :
                
                  if(x != 210 
                  && arr[((x + 90)/30)][(y + 30)/30] == 0)
                  {
                    x += 30;
                  }
                  break;
                  
                case "a" :
                
                  if(x != 270
                  && arr[(x + 30)/30][(y + 0)/30] == 0
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0
                  && arr[(x + 30)/30][(y + 90)/30] == 0)
                  {
                    x += 30;
                  }
                }
                
              break;
              
           //T Block   
          case 2 :
    
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 0)/30] == 0)
                  {
                    x += 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 240 
                  && arr[(x + 30)/30][(y + 0)/30] == 0
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 270 
                  && arr[(x + 30)/30][(y + 0)/30] == 0
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
              }
              break;

          //Z Block
          case 3 :

              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 240 
                  && arr[(x + 30)/30][(y + 0)/30] == 0
                  && arr[(x + 60)/30][(y + 30)/30] == 0)
                  {
                    x += 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 240 
                  && arr[(x + 60)/ 30][(y + 0)/30] == 0
                  && arr[(x + 60) / 30][(y + 30)/30] == 0
                  && arr[(x + 30) / 30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
              }
              break;
          
          //S Block
          case 4 :
          
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 240 
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 60)/30][(y + 0)/30] == 0)
                  {
                    x += 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 270 
                  && arr[(x + 0)/30][(y + 0)/30] == 0
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
              }
              break;
          //L Block 
          case 5 :
    
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 0)/30] == 0
                  && arr[(x + 60)/30][(y + 30)/30] == 0)
                  {
                    x += 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 60)/30] == 0
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 0)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 0)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 270 
                  && arr[(x + 30) / 30][(y + 0)/30] == 0
                  && arr[(x + 30) / 30][(y + 30)/30] == 0
                  && arr[(x + 30) / 30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
              }
              break;
              
          //J Block
          case 6 :
      
              switch(shapeTransform)
              {
                case "DEFAULT" :
                
                  if(x != 240 
                  && arr[(x + 0)/30][(y + 0)/30] == 0
                  && arr[(x + 60)/30][(y + 30)/30] == 0)
                  {
                    x += 30;
                  }

                  break;
                  
                case "a" :
                
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 0)/30] == 0
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "b" :
                  
                  if(x != 240 
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 60)/30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
                  
                  break;
                  
                  case "c" :
                  
                  if(x != 270 
                  && arr[(x + 30) / 30][(y + 0)/30] == 0
                  && arr[(x + 30) / 30][(y + 30)/30] == 0
                  && arr[(x + 30) / 30][(y + 60)/30] == 0)
                  {
                    x += 30;
                  }
              }
              break;
        }
      }
    
      else if (key == KeyEvent.VK_UP) 
      {
        switch(shapeType) 
        {
          //Square block
          case 0 :
              
                break;
          
          //Long block
          case 1 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600 && y != 570
                    && arr[(x + 0)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 90)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(x != 0 && x != 300 && x != 270 && x != 240
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0
                    && arr[(x + 60)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                  }
                
                break;
                
          //T Block
          case 2 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][(y + 60)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 600 && x != 0
                    && arr[(x - 30)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "b";
                    }
                    
                    break;
                    
                  case "b" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][((y + 0))/30] == 0)
                    {
                      shapeTransform = "c";
                    }
                    
                    break;
                    
                  case "c" :
                  
                    if(y != 600 && x != 270
                    && arr[(x + 30)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                }
                break;
                
          //Z Block
          case 3 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 30)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(x != 0
                    && arr[(x - 30)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 0)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                }
              break;
        
          //S Block
          case 4 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x - 30)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(x != 270
                    && arr[(x + 0)/30][(y + 0)/30] == 0
                    && arr[(x + 30)/30][(y + 0)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                }
              break;
          
          //L Block    
          case 5 :
          
              switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 0)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(x != 0
                    && arr[(x - 30)/30][(y + 60)/30] == 0
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "b";
                    }
                    
                    break;
                    
                  case "b" :
                  
                    if(
                       arr[(x - 30)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 0)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0)
                    {
                      shapeTransform = "c";
                    }
                    
                    break;
                    
                  case "c" :
                  
                    if(x != 270
                    && arr[(x + 30)/30][(y + 0)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0
                    && arr[(x - 30)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                }
                break;
              
          //J Block
          case 6 :
          
              switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 0)/30] == 0
                    && arr[(x + 30)/30][(y + 0)/30] == 0)
                    {
                      shapeTransform = "a";
                    }
                    
                    break;
                    
                  case "a" :
                  
                    if(x != 0
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0)
                    {
                      shapeTransform = "b";
                    }
                    
                    break;
                    
                  case "b" :
                  
                    if(
                       arr[(x - 30)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 0)/30] == 0)
                    {
                      shapeTransform = "c";
                    }
                    
                    break;
                    
                  case "c" :
                  
                    if(x != 270
                    && arr[(x - 30)/30][(y + 0)/30] == 0
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0)
                    {
                      shapeTransform = "DEFAULT";
                    }
                }
                break;
        }
      }
    
      else if (key == KeyEvent.VK_DOWN) 
      { 
        switch(shapeType) 
        {
        
          //Square Block
          case 0 :
            if(y  != 600 
              && arr[(x + 0) / 30][(y + 60) / 30] == 0
              && arr[(x + 30) / 30][(y + 60) / 30] == 0)
              {
                y += 30;
              }   
              
              graceCounter = 0; 

              break;
    
          //Long Block
          case 1 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600 
                    && arr[((x - 30)/30)][(y + 60)/30] == 0
                    && arr[((x + 0)/30)][(y + 60)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0
                    && arr[((x + 60)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 540
                    && arr[((x + 0)/30)][(y + 120)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                }
                
              break;
              
          case 2 :

                //T Block
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600 
                    && arr[((x + 0)/30)][(y + 60)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
   
                    break;
                    
                    case "b" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
   
                    break;
                    
                    case "c" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                }
                
              break;
              
           //Z Block
          case 3 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600 
                    && arr[((x + 0)/30)][(y + 60)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0
                    && arr[((x - 30)/30)][(y + 30)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                }
              break;
          
          //S Block
          case 4 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600 
                    && arr[((x + 0)/30)][(y + 60)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0
                    && arr[((x + 30)/30)][(y + 30)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
   
               }
              break;
           
          //L Block
          case 5 :
          
                switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0
                    && arr[(x - 30)/30][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[(x + 0)/30][(y + 90)/30] == 0
                    && arr[(x + 30)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "b" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 90)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "c" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 0)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                }
                break;
          
          //J Block   
          case 6 :

              switch(shapeTransform)
                {
                  case "DEFAULT" :
                  
                    if(y != 600
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0
                    && arr[(x - 30)/30][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[(x + 0)/30][(y + 90)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "b" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                    
                    break;
                    
                  case "c" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 90)/30] == 0
                    && arr[(x + 0)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    graceCounter = 0; 
                }
                break;
        }
      }
    }
    
    repaint();

  }
  
  //Draws current shapes to the JPanel and updates them every cycle
  public void drawShape(Graphics2D g2d)
  {
  
    if(shapeSpawn == true)
    {
      x = 120;
      y = 0;
      
      shapeSpawn = false;
    }
    
    g2d.setColor(shapeColors[shapeType + 1]);
    
    switch(shapeType)
      {
      
      //Square Block
      case 0 :
  
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          break;

      //Long Block
      case 1 :
          
          
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              g2d.fillRect(x + 61, y - 29, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 1, y + 31, 29, 29);
              break;
          }
          break;
      
      //T Block
      case 2 :
 
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "b":
            
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "c":
            
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              break;
          }
          break;
          
      //Z Block
      case 3 :
      
   
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x - 29, y - 59, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 59, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              break;
          }
          break;
          
      //S Block
      case 4 :
      
        
          
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 31, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x - 29, y - 59, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              break;
          }
          break;
          
      //L Block
      case 5 :
    
          
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 59, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 31, y + 1, 29, 29);
              break;
              
            case "b":
            
              g2d.fillRect(x - 29, y + 1, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "c":
            
              g2d.fillRect(x - 29, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              break;
          }
          break;
      
      //J Block    
      case 6 :
    
          
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              g2d.fillRect(x - 29, y - 59, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 31, y - 59, 29, 29);
              break;
              
            case "b":
            
              g2d.fillRect(x + 31, y + 1, 29, 29);
              g2d.fillRect(x - 29, y - 29, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              break;
              
            case "c":
            
              g2d.fillRect(x - 29, y + 1, 29, 29);
              g2d.fillRect(x + 1, y - 59, 29, 29);
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              break;
          }
          break;
    }
  }
  
  //Checks if a shape is located ontop of another shape or on the board floor
  private int placementCheck()
  {
    switch(shapeType) 
    {
      //Square Block
      case 0 :
      
          if(y  == 600 
          || arr[(x + 0)/30][(y + 60)/30] != 0
          || arr[(x + 30)/30][(y + 60)/30] != 0)
          {
    
            if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
        
            else
            {
              arr[((x + 0)/30)][(y + 0)/30] = 1;
              arr[((x + 0)/30)][(y + 30)/30] = 1;
              arr[((x + 30)/30)][(y + 0)/30] = 1;
              arr[((x + 30)/30)][(y + 30)/30] = 1;
              
              return 1;
            }
          }
          
          break;

      //Long Block
      case 1 :
      
        switch(shapeTransform)
        {
          case "DEFAULT" :
            if(y  == 600 
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 60)/30] != 0
            || arr[(x + 60)/30][(y + 60)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 30)/30] = 2;
                arr[(x - 30)/30][(y + 30)/30] = 2;
                arr[(x + 30)/30][(y + 30)/30] = 2;
                arr[(x + 60)/30][(y + 30)/30] = 2;
                    
                return 1;
              }
            }
            break;
            
          case "a" :
            if(y  == 540 
            || arr[(x + 0)/30][(y +120)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 0)/30] = 2;
                arr[(x + 0)/30][(y + 30)/30] = 2;
                arr[(x + 0)/30][(y + 60)/30] = 2;
                arr[(x + 0)/30][(y + 90)/30] = 2;
                    
                return 1;
              }
            }
            break;
        }

          break;
          
      //T Block
      case 2 :
      
       switch(shapeTransform)
        {
          case "DEFAULT" :
          
            if(y  == 600 
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 60)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 30)/30] = 3;
                arr[(x - 30)/30][(y + 30)/30] = 3;
                arr[(x + 30)/30][(y + 30)/30] = 3;
                arr[(x + 0)/30][(y) / 30] = 3;
                    
                return 1;
              }
            }
            break;
            
          case "a" :
          
            if(y  == 570 
            || arr[(x + 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 30)/30][(y + 30)/30] = 3;
                arr[(x + 0)/30][(y + 0)/30] = 3;
                arr[(x + 0)/30][(y + 30)/30] = 3;
                arr[(x + 0)/30][(y + 60)/30] = 3;
                    
                return 1;
              }
            }
            break;
            
          case "b" :
          
          if(y  == 570 
          || arr[(x - 30)/30][(y + 60)/30] != 0
          || arr[(x + 30)/30][(y + 60)/30] != 0
          || arr[(x + 0)/30][(y + 90)/30] != 0)
          {
            
            if(graceCounter > 0)
            {
              graceCounter = graceCounter - 1;
              
              return 0;
            }
        
            else
            {
              arr[(x - 30)/30][(y + 30)/30] = 3;
              arr[(x + 30)/30][(y + 30)/30] = 3;
              arr[(x + 0 )/30][(y + 60)/30] = 3;
              arr[(x + 0 )/30][(y + 30)/30] = 3;
                  
              return 1;
              }
            }
            break;
          
          case "c" :
          
            if(y  == 570 
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 30)/30] = 3;
                arr[(x + 0)/30][(y + 0)/30] = 3;
                arr[(x + 0)/30][(y + 30)/30] = 3;
                arr[(x + 0)/30][(y + 60)/30] = 3;
                    
                return 1;
              }
            }
            break;
          }

          break;
          
      //Z Block   
      case 3 :
      
        switch(shapeTransform)
        {
          case "DEFAULT" :
          
            if(y  == 600 
            || arr[(x - 30)/30][(y + 30)/30] != 0
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 60)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 0)/30] = 4;
                arr[(x + 0)/30][(y + 0)/30] = 4;
                arr[(x + 0)/30][(y + 30)/30] = 4;
                arr[(x + 30)/30][(y + 30)/30] = 4;
                
                    
                return 1;
              }
            }
            break;
            
          case "a" :
          
            if(y  == 570 
            || arr[(x + 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)

            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 30)/30] = 4;
                arr[(x + 0)/30][(y + 60)/30] = 4;
                arr[(x + 30)/30][(y + 30)/30] = 4;
                arr[(x + 30)/30][(y + 0)/30] = 4;
                    
                return 1;
              }
            }
            break;
          }

          break;

      //S Block 
      case 4 :
      
        switch(shapeTransform)
        {
          case "DEFAULT" :
          
            if(y  == 600 
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 30)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 30)/30] = 5;
                arr[(x + 0)/30][(y + 30)/30] = 5;
                arr[(x + 0)/30][(y + 0)/30] = 5;
                arr[(x + 30)/30][(y + 0)/30] = 5;
                
                    
                return 1;
              }
            }
            break;
            
          case "a" :
          
            if(y  == 570 
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)

            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 0)/30] = 5;
                arr[(x - 30)/30][(y + 30)/30] = 5;
                arr[(x + 0)/30][(y + 30)/30] = 5;
                arr[(x + 0)/30][(y + 60)/30] = 5;
                    
                return 1;
              }
            }
            break;
          }

          break;
      
      //L Block    
      case 5 :
      
      switch(shapeTransform)
        {
          case "DEFAULT" :
          
            if(y  == 600 
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 60)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 30)/30] = 6;
                arr[(x + 0)/30][(y + 30)/30] = 6;
                arr[(x + 30)/30][(y + 30)/30] = 6;
                arr[(x + 30)/30][(y + 0)/30] = 6;
                    
                return 1;
              }
            }
            break;
            
          case "a" :
          
            if(y  == 570 
            || arr[(x + 0)/30][(y + 90)/30] != 0
            || arr[(x + 30)/30][(y + 90)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 0)/30] = 6;
                arr[(x + 0)/30][(y + 30)/30] = 6;
                arr[(x + 0)/30][(y + 60)/30] = 6;
                arr[(x + 30)/30][(y + 60)/30] = 6;
                    
                return 1;
              }
            }
            break;
            
          case "b" :
          
          if(y  == 570 
          || arr[(x - 30)/30][(y + 90)/30] != 0
          || arr[(x + 0)/30][(y + 60)/30] != 0
          || arr[(x + 30)/30][(y + 60)/30] != 0)
          {
            
            if(graceCounter > 0)
            {
              graceCounter = graceCounter - 1;
              
              return 0;
            }
        
            else
            {
              arr[(x - 30)/30][(y + 60)/30] = 6;
              arr[(x - 30)/30][(y + 30)/30] = 6;
              arr[(x + 0)/30][(y + 30)/30] = 6;
              arr[(x + 30)/30][(y + 30)/30] = 6;
                  
              return 1;
              }
            }
            break;
          
          case "c" :
          
            if(y  == 570 
            || arr[(x - 30)/30][(y + 30)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 0)/30] = 6;
                arr[(x + 0)/30][(y + 0)/30] = 6;
                arr[(x + 0)/30][(y + 30)/30] = 6;
                arr[(x + 0)/30][(y + 60)/30] = 6;
                    
                return 1;
              }
            }
            break;
          }

          break;
      
      //J Block   
      case 6 :
      
      switch(shapeTransform)
        {
          case "DEFAULT" :
          
            if(y  == 600 
            || arr[(x - 30)/30][(y + 60)/30] != 0
            || arr[(x + 0)/30][(y + 60)/30] != 0
            || arr[(x + 30)/30][(y + 60)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 30)/30] = 7;
                arr[(x + 0)/30][(y + 30)/30] = 7;
                arr[(x + 30)/30][(y + 30)/30] = 7;
                arr[(x - 30)/30][(y + 0)/30] = 7;
                    
                return 1;
              }
            }
            break;
            
          case "a" :
          
            if(y  == 570 
            || arr[(x + 0)/30][(y + 90)/30] != 0
            || arr[(x + 30)/30][(y + 30)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0)/30][(y + 0)/30] = 7;
                arr[(x + 0)/30][(y + 30)/30] = 7;
                arr[(x + 0)/30][(y + 60)/30] = 7;
                arr[(x + 30)/30][(y + 0)/30] = 7;
                    
                return 1;
              }
            }
            break;
            
          case "b" :
          
          if(y  == 570 
          || arr[(x + 30)/30][(y + 90)/30] != 0
          || arr[(x + 0)/30][(y + 60)/30] != 0
          || arr[(x - 30)/30][(y + 60)/30] != 0)
          {
            
            if(graceCounter > 0)
            {
              graceCounter = graceCounter - 1;
              
              return 0;
            }
        
            else
            {
              arr[(x - 30)/30][(y + 30)/30] = 7;
              arr[(x + 0)/30][(y + 30)/30] = 7;
              arr[(x + 30)/30][(y + 30)/30] = 7;
              arr[(x + 30)/30][(y + 60)/30] = 7;
                  
              return 1;
              }
            }
            break;
          
          case "c" :
          
            if(y  == 570 
            || arr[(x - 30)/30][(y + 90)/30] != 0
            || arr[(x + 0)/30][(y + 90)/30] != 0)
            {
              
              if(graceCounter > 0)
              {
                graceCounter = graceCounter - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x - 30)/30][(y + 60)/30] = 7;
                arr[(x + 0)/30][(y + 0)/30] = 7;
                arr[(x + 0)/30][(y + 30)/30] = 7;
                arr[(x + 0)/30][(y + 60)/30] = 7;
                    
                return 1;
              }
            }
            break;
          }
  
          break;
    }
    
    return 2;
  }
  
  public void drawPreview(Graphics2D g2d)
  {
     
     g2d.setColor(shapeColors[nextShape + 1]);
     
     switch(nextShape) 
        { 
        
        //Square Block
          case 0 :
    
              g2d.fillRect(x2 - 12, y2 - 59, 29, 29);
              g2d.fillRect(x2 - 12, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 18, y2 - 59, 29, 29);
              g2d.fillRect(x2 + 18, y2 - 29, 29, 29);
              break;
    
          //Long Block
          case 1 :
            
              g2d.fillRect(x2 - 44, y2 - 40, 29, 29);
              g2d.fillRect(x2 - 14, y2 - 40, 29, 29);
              g2d.fillRect(x2 + 16, y2 - 40, 29, 29);
              g2d.fillRect(x2 + 46, y2 - 40, 29, 29);
              break;
          
          //T Block
          case 2 :
          
                
              g2d.fillRect(x2 - 29, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 1, y2 - 59, 29, 29);
              g2d.fillRect(x2 + 1, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 29, 29, 29);
              break;
    
          //Z Block
          case 3 :
    
              g2d.fillRect(x2 + 1, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 1, y2 - 59, 29, 29);
              g2d.fillRect(x2 - 29, y2 - 59, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 29, 29, 29);
              break;
              
          //S Block
          case 4 :
            
              g2d.fillRect(x2 + 1, y2 - 59, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 59, 29, 29);
              g2d.fillRect(x2 + 1, y2 - 29, 29, 29);
              g2d.fillRect(x2 - 29, y2 - 29, 29, 29);
              break;
    
              
          //L Block
          case 5 :
          
              g2d.fillRect(x2 + 1, y2 - 29, 29, 29);
              g2d.fillRect(x2 - 29, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 59, 29, 29);
              break;
    
          
          //J Block    
          case 6 :
          
              g2d.fillRect(x2 + 1, y2 - 29, 29, 29);
              g2d.fillRect(x2 - 29, y2 - 29, 29, 29);
              g2d.fillRect(x2 + 31, y2 - 29, 29, 29);
              g2d.fillRect(x2 - 29, y2 - 59, 29, 29);
              break;
      }
  }

      
//  private void gameOver(Graphics2D g2d, int c)
//  {
//   
//  }

  
  public void keyReleased(KeyEvent e) 
  {
    //unused
  }
  public void keyTyped(KeyEvent e) 
  {
    //unused
  }
}