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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Integer;


public class LayoutJPanel extends JPanel implements ActionListener, KeyListener
{

  //Initial shape coordinates
  private int x = 120, y = 0;
  
  //Preview shape coordinates
  private int x2 = 385, y2 = 205;
  
  private Random random = new Random(); 
  
  //Default game speed; as the player progresses, the speed at which the shapes fall increases
  public Timer t = new Timer(500, this);
  private int timeUpdater = 500;
  private boolean timeSwitch = true;
  
  //Player scoring; also affects game speed
  private int totalScore = 0;
  private int totalLines = 0;
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
  private int gracePeriod[] = new int[] {2, 2, 2, 2, 3, 3, 3, 4, 4, 6, 8};
  private int graceCounter = 2;
  
  //Prevents using key events between shape creation - 
  //shouldn't really need, but I was getting OOB exceptions by smashing my fist onto the arrow keys
  private boolean shapeDelay = false;
  
  //Lose condition check
  private boolean loseCheck = false;
  
  //Tetris board: 10 x 22; only 10 x 20 is drawn on the panel since shapes are created above player vision
  private int arr[][] = new int[10][22];
  
  //Color Palette for each level
  private Color LevelColor1[] = new Color[] {new Color(55, 56, 59), new Color(255, 213, 0), new Color(24, 227, 222),
  new Color(116, 24, 227),new Color(227, 24, 24), new Color(58, 227, 24), new Color(227, 130, 24), new Color(24, 77, 227)};
  
  private Color LevelColor2[] = new Color[] {new Color(43, 45, 66), new Color(47, 73, 174), new Color(239, 35, 60), 
  new Color(237, 242, 244), new Color(47, 73, 174), new Color(239, 35, 60), new Color(47, 73, 174), new Color(239, 35, 60)};
  
  private Color LevelColor3[] = new Color[] {new Color(20, 52, 43), new Color(255, 87, 159), new Color(187, 223, 197), 
  new Color(255, 87, 159), new Color(96, 147, 93), new Color(187, 223, 197), new Color(255, 87, 159), new Color(96, 147, 93)}; 
  
  private Color LevelColor4[] = new Color[] {Color.GRAY, new Color(255, 136, 17), Color.WHITE, 
  new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17)}; 
  
  private Color LevelColor5[] = new Color[] {new Color(12, 42, 95), new Color(252, 255, 75), new Color(255, 173, 5), 
  new Color(124, 175, 196), new Color(89, 149, 237), new Color(252, 255, 75), new Color(255, 173, 5), new Color(89, 149, 237)};
  
  private Color LevelColor6[] = new Color[] {new Color(110, 37, 5), new Color(162, 167, 158), new Color(167, 116, 100), 
  new Color(199, 91, 99), new Color(162, 167, 158), new Color(167, 116, 100), new Color(199, 91, 99), new Color(167, 116, 100)}; 
  
  private Color LevelColor7[] = new Color[] {new Color(85, 61, 99), new Color(234, 55, 136), new Color(176, 34, 140),
  new Color(229, 107, 112),new Color(234, 55, 136), new Color(176, 34, 140), new Color(229, 107, 112), new Color(234, 55, 136)};
  
  private Color LevelColor8[] = new Color[] {new Color(41, 47, 54), new Color(255, 107, 107), new Color(78, 205, 196), 
  new Color(255, 107, 107), new Color(255, 255, 255), new Color(78, 205, 196), new Color(255, 107, 107), new Color(255, 255, 255)};
  
  private Color LevelColor9[] = new Color[] {new Color(82, 34, 28), new Color(149, 10, 17), new Color(240, 102, 37), 
  new Color(245, 185, 29), new Color(247, 240, 2), new Color(149, 10, 17), new Color(240, 102, 37), new Color(245, 185, 29)};
  
  private Color shapeColors[][] = new Color[][] {LevelColor1, LevelColor2, LevelColor3, LevelColor4, LevelColor5,
  LevelColor6, LevelColor7, LevelColor8, LevelColor9, LevelColor1, LevelColor1};
  
  
  public void paintComponent( Graphics g) 
  {    
  
    double velX = 2, velY = 2;
    
    double width = getWidth();
    double height = getHeight();

    addKeyListener(this);

    
    super.paintComponent( g );
    Graphics2D g2d = (Graphics2D) g;
    GeneralPath coordinates = new GeneralPath();
    
    
    
    
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
          
          g2d.setColor(shapeColors[currentDifficulty][(arr[i][j])]);
          g2d.fillRect((i * 30) + 1, (j * 30) - 59, 29, 29);
        }
      }
    }
    
    //Draws current shapes to the JPanel and updates them every cycle
    drawShape(g2d);
    
    //Changes background depending if player has lost or not
    if(loseCheck)
    {     
       setBackground(Color.BLACK);
    }
    
    else {setBackground(shapeColors[currentDifficulty][0]);}
    
    //Draws out the preview info field and the next shape
    g2d.setColor(new Color(236, 234, 236));
    g2d.fillRect(305, 0, 195, 600);
    
    g2d.setColor(Color.BLACK);
    g2d.fillRect(325, 100, 155, 155);
    
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(10));
    g2d.drawLine(306, 0, 306, 600);
    
    g2d.setStroke(new BasicStroke(5));
    g2d.drawLine(306, 350, 400, 350);
    g2d.drawLine(306, 450, 425, 450);
    g2d.drawLine(306, 550, 450, 550);
    
    g.setFont(new Font("default", Font.BOLD, 16));
    g2d.drawString("Next Shape", 350, 95);
    g2d.drawString("LvL: " + currentDifficulty, 320, 345);
    g2d.drawString("Lines: " + totalLines, 320, 445);
    g2d.drawString("Score: " + totalScore, 320, 545);
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
 
   //Action event that triggers through a timer cycle
  public void actionPerformed(ActionEvent e)
  {
    
    //If losing condition was met, exit the game
    if(loseCheck)
    {     
      //String score = totalScore.toString();
      Integer TotalScore = new Integer(totalScore);
      FileWriter fr = null;
      try{
        fr = new FileWriter("scores.txt", true);
        fr.write("------------Score:  ");
        fr.write(TotalScore.toString());
      
      }
      catch(IOException ioex){
      
        ioex.printStackTrace();
      
      }
      finally{
      
        try{
          fr.close();
        }
        catch(IOException ioex){
        
          ioex.printStackTrace();
        
        }
      
      }
      
      t.stop();
      setFocusable(false);
      return;
    }

    //Checks if shape is connected above another block or on the floor
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
      x = 120;
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
      
      //Calculates score and evalutes difficulty
      if(scoreCombo > 0)
      {
        totalScore += lineValue[scoreCombo - 1] * (currentDifficulty + 1);
        lineCounter += scoreCombo;
        totalLines += scoreCombo;
        
        if(lineCounter >= 10 && currentDifficulty != 10)
        {
          lineCounter = lineCounter % 10;
          currentDifficulty++;
          
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
    
    g2d.setColor(shapeColors[currentDifficulty][shapeType + 1]);
    
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
  
  //Draws preview field shapes
  public void drawPreview(Graphics2D g2d)
  {
     
     g2d.setColor(shapeColors[currentDifficulty][nextShape + 1]);
     
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

  //Prints board to console    
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
    System.out.println();
  }
  
  //Stops timer; removing the components doesnt seem to stop the timer for some reason
  public void timerStop()
  {
    t.stop();
  }
  
  public void keyReleased(KeyEvent e) 
  {
    //unused
  }
  public void keyTyped(KeyEvent e) 
  {
    //unused
  }
}