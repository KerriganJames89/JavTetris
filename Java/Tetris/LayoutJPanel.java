import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

public class LayoutJPanel extends JPanel implements ActionListener, KeyListener{

  //General shape coordinates
  private int x = 0, y = 0;
  
  private Random random = new Random(); 
  
  //Default game speed; as the player progresses, the speed at which the shapes fall increases
  private Timer t = new Timer(250, this);
  
  //Throttles the speed at which players can move the shapes
  private long timePrevious = 0;
  private long timeCurrent = 0;
  private long timeCheck = 0;
  
  //Picks a shape at random and draws it every cycle till placed
  private int shapeType = random.nextInt(7);
  //private int shapeType = 6;
  
  //Indicator that helps reset the coordinates for newly made shapes
  private boolean shapeSpawn = true;
  
  //Indicator for current shape's turning position
  private String shapeTransform = "DEFAULT";
  
  //GracePeriod gives the player time to move their shape when ontop of another shape
  private int gracePeriod = 1;
  
  //Prevents using key events between shape creation - 
  //shouldn't really need, but I was getting OOB exceptions by smashing my fist onto the arrow keys
  private int shapeDelay = 0;
  
  //Tetris board: 10 x 22; only 10 x 20 is drawn on the panel since shapes are created above player vision
  private int arr[][] = new int[10][22];
  
  private Color[] shapeColors = new Color[] {Color.WHITE, new Color(255, 136, 17), Color.WHITE, 
  new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17), Color.WHITE, new Color(255, 136, 17)}; 

  
  public void paintComponent( Graphics g)
  {
    setBackground(Color.GRAY);
    double velX = 2, velY = 2;
    
    double width = getWidth();
    double height = getHeight();

    this.addKeyListener(this);
    this.setFocusable(true);
    
    super.paintComponent( g );
    Graphics2D g2d = (Graphics2D) g;
    GeneralPath coordinates = new GeneralPath();
    
    //Draws board gridlines to Jpanel
    for(int i = 0; i <= width; i+=30)
    {
      coordinates.moveTo(i,height);
      coordinates.lineTo(i,0);
    }
      
    for(int i = 0; i <= height; i+=30)
    {
      coordinates.moveTo(width,i);
      coordinates.lineTo(0,i);
    }
    
    g2d.setColor(Color.BLACK);
    g2d.draw(coordinates);
    
    //Draws current shapes to the JPanel and updates them every cycle
    drawShape(g2d);
    
    //Restores key events for player when shape is in play
    if(shapeDelay == 0)
    {
      shapeDelay = 1;
    }
    
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
      
    
  //coordinate testing
  System.out.println("x value is:" + x);
  System.out.println("y value is:" + y);
  //System.out.println();

  t.start();
  
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
    int check = placementCheck();

    if(check == 1)
    {
    
      //Variable reset for next shape
      shapeType = random.nextInt(7);
      shapeSpawn = true;
      shapeTransform = "DEFAULT";
      gracePeriod = 2;
      shapeDelay = 0;
      
      //Coordinate reset for next shape
      x = 0;
      y = 0;
       
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

      
      
      //print test
      printBoard();
      
      repaint();
      return;
    }
    
    else if(check == 2)
    {
      gracePeriod = 1;
      y += 30;
      repaint();
    }
  }
  
  //Key events; players primarily use the arrow keys to adjust the shapes
  public void keyPressed(KeyEvent e)
  {
    //Pauses event until a shape is created
    if(shapeDelay == 0)
    {
      return;
    }
      
      timeCurrent = System.currentTimeMillis();
      timeCheck = timeCurrent - timePrevious;
  
    if (timeCheck < 0 || timeCheck > 5) 
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
              
              //gracePeriod = 0; 

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
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 540
                    && arr[((x + 0)/30)][(y + 120)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
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
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
   
                    break;
                    
                    case "b" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
   
                    break;
                    
                    case "c" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
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
                    && arr[((x + 30)/30)][(y + 0)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x + 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
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
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[((x + 0)/30)][(y + 90)/30] == 0
                    && arr[((x - 30)/30)][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
   
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
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[(x + 0)/30][(y + 90)/30] == 0
                    && arr[(x + 30)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "b" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 90)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 60)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "c" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 30)/30] == 0
                    && arr[(x + 0)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
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
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "a" :
                  
                    if(y != 570
                    && arr[(x + 0)/30][(y + 90)/30] == 0
                    && arr[(x + 30)/30][(y + 30)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "b" :
                  
                    if(y != 570
                    && arr[(x - 30)/30][(y + 60)/30] == 0
                    && arr[(x + 0)/30][(y + 60)/30] == 0
                    && arr[(x + 30)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
                    
                    break;
                    
                  case "c" :
                  
                    if(y != 570
                    && arr[(x + 30)/30][(y + 90)/30] == 0
                    && arr[(x + 0)/30][(y + 90)/30] == 0)
                    {
                      y += 30;
                    }
                    
                    //gracePeriod = 0; 
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
    switch(shapeType) 
    {
      //Square Block
      case 0 :
          
          g2d.setColor(shapeColors[1]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          break;

      //Long Block
      case 1 :
          
          g2d.setColor(shapeColors[2]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
      
          g2d.setColor(shapeColors[3]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
          g2d.setColor(shapeColors[4]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
          g2d.setColor(shapeColors[5]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
      
          g2d.setColor(shapeColors[6]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
      
          g2d.setColor(shapeColors[7]);
          
          if(shapeSpawn == true)
          {
            x = 120;
            y = 0;
            
            shapeSpawn = false;
          }
          
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
  
  //Private helper function that checks if a shape is located ontop of another shape or on the board floor
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
    
            if(gracePeriod > 0)
            {
              gracePeriod = gracePeriod - 1;
              
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
            
            if(gracePeriod > 0)
            {
              gracePeriod = gracePeriod - 1;
              
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
            
            if(gracePeriod > 0)
            {
              gracePeriod = gracePeriod - 1;
              
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
            
            if(gracePeriod > 0)
            {
              gracePeriod = gracePeriod - 1;
              
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
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
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
  
  public void keyReleased(KeyEvent e) 
  {
    //unused
  }
  public void keyTyped(KeyEvent e) 
  {
    //unused
  }
}