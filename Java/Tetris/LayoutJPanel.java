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

public class LayoutJPanel extends JPanel implements ActionListener, KeyListener{

  //General shape coordinates
  private int x = 0, y = 0;
  
  private Random random = new Random(); 
  
  //Default game speed; as the player progresses, the speed at which the shapes fall increases
  private Timer t = new Timer(500, this);
  
  //Throttles the speed at which players can move the shapes
  private long timePrevious = 0;
  private long timeCurrent = 0;
  private long timeCheck = 0;
  
  //Picks a shape at random and draws it every cycle till placed
  private int shapeType = random.nextInt(2);
  //private int shapeType = 0;
  
  //Indicator that helps reset the coordinates for newly made shapes
  private boolean shapeSpawn = true;
  
  //Indicator for current shape's turning position
  private String shapeTransform = "DEFAULT";
  
  //GracePeriod gives the player time to move their shape when ontop of another shape
  private int gracePeriod = 1;
  
  //Tetris board: 10 x 22; only 10 x 20 is drawn on the panel since shapes are created outside of player vision
  private int arr[][] = new int[10][22];
  
  private Color[] shapeColors = new Color[] {Color.BLACK, Color.RED, Color.BLUE}; 

  
  public void paintComponent( Graphics g)
  {
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
    
    //Draws previous shapes to the JPanel by accessing the stored board array data
    drawPrevious(g2d);
      
    
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
  
  //Draws current shapes to the JPanel and updates them every cycle
  public void drawShape(Graphics2D g2d)
  {
    switch(shapeType) 
    {
      case 0 :
          
          g2d.setColor(Color.RED);
          
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

          
      case 1 :
          
          g2d.setColor(Color.BLUE);
          
          if(shapeSpawn == true)
          {
            x = 90;
            y = 0;
            
            shapeSpawn = false;
          }
          
          switch(shapeTransform)
          {
            case "DEFAULT":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 31, y - 29, 29, 29);
              g2d.fillRect(x + 61, y - 29, 29, 29);
              g2d.fillRect(x + 91, y - 29, 29, 29);
              break;
              
            case "a":
            
              g2d.fillRect(x + 1, y - 29, 29, 29);
              g2d.fillRect(x + 1, y + 1, 29, 29);
              g2d.fillRect(x + 1, y + 31, 29, 29);
              g2d.fillRect(x + 1, y + 61, 29, 29);
              break;
          }
          break;
          
      case 2 :
          g2d.setColor(Color.GREEN);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          break;
          
      case 3 :
          g2d.setColor(Color.YELLOW);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          break;
          
      case 4 :
          g2d.setColor(Color.ORANGE);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          break;
          
      case 5 :
          g2d.setColor(Color.BLUE);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          break;
          
      case 6 :
          g2d.setColor(Color.RED);
          g2d.fillRect(x + 1, y - 29, 29, 29);
          g2d.fillRect(x + 1, y - 59, 29, 29);
          g2d.fillRect(x + 31, y - 29, 29, 29);
          g2d.fillRect(x + 31, y - 59, 29, 29);
          break;
    }
  }
  
  public void drawPrevious(Graphics2D g2d)
  {
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
  }
 
  public void actionPerformed(ActionEvent e)
  {
    int check = placementCheck();

    if(check == 1)
    {
      x = 0;
      y = 0;
      
      shapeType = random.nextInt(2);
      //shapeType = 0;
      shapeSpawn = true;
      shapeTransform = "DEFAULT";
      gracePeriod = 1;
      System.out.println("Shape type: " + shapeType);
      
      
      
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
  
  public void keyPressed(KeyEvent e)
  {
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
          case 0 :
            if(x != 0 
              && arr[(x - 30)/30][y/30] == 0
              && arr[(x - 30)/30][(y + 30)/30] == 0)
              {
                x -= 30;
              }
              
              break;
    
          case 1 :
              
              switch(shapeTransform)
              {
                case "DEFAULT" :
                  if(x != 0 
                  && arr[(x - 30)/30][(y + 30)/30] == 0)
                  {
                    x -= 30;
                  }

                  break;
                  
                case "a" :
                  if(x != 0 
                  && arr[(x - 30)/30][(y - 30)/30] == 0
                  && arr[(x - 30)/30][(y)/30] == 0
                  && arr[(x - 30)/30][(y + 30)/30] == 0
                  && arr[(x - 30)/30][(y + 60)/30] == 0)
                  {
                    x -= 30;
                  }
              }
              break;
              
          case 2 :
    
              break;
              
          case 3 :
    
              break;
              
          case 4 :
    
              break;
              
          case 5 :
    
              break;
              
          case 6 :
      
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
            && arr[(x + 60)/30][y/30] == 0
            && arr[(x + 60)/30][(y + 30)/30] == 0)
            {
              x += 30;
            }
              break;
          //Long block
          case 1 :
               switch(shapeTransform)
                {
                //shape size = 120px, so 120px + 180px = 300 (the border)
                //
                case "DEFAULT" :
                  if(x != 180 
                  && arr[((x + 120)/30)][(y + 30)/30] == 0)
                  {
                    x += 30;
                  }
                  break;
                  
                case "a" :
                  if(x != 270
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 30)/30][(y + 60)/30] == 0
                  && arr[(x + 30)/30][(y + 90)/30] == 0
                  && arr[(x + 30)/30][(y + 120)/30] == 0)
                  {
                    x += 30;
                  }
                  break;
                }
                
              break;
              
          case 2 :
    
              break;
              
          case 3 :
    
              break;
              
          case 4 :
    
              break;
              
          case 5 :
    
              break;
              
          case 6 :
      
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
                  if(y != 600 && y != 570 & y != 540
                  && arr[(x + 0)/30][(y + 30)/30] == 0
                  && arr[(x + 0)/30][(y + 60)/30] == 0
                  && arr[(x + 0)/30][(y + 90)/30] == 0
                  && arr[(x + 0)/30][(y + 120)/30] == 0)
                  {
                    shapeTransform = "a";
                  }
                  
                  break;
                  
                case "a" :
                  if(x != 210 && x != 240 && x != 270 && x != 300 
                  && arr[(x + 30)/30][(y + 30)/30] == 0
                  && arr[(x + 60)/30][(y + 30)/30] == 0
                  && arr[(x + 90)/30][(y + 30)/30] == 0)
                  {
                    shapeTransform = "DEFAULT";
                  }

                  break;
                }
                break;
              
          case 2 :
    
              break;
              
          case 3 :
    
              break;
              
          case 4 :
    
              break;
              
          case 5 :
    
              break;
              
          case 6 :
      
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
              && arr[(x) / 30][(y + 60) / 30] == 0
              && arr[(x + 30) / 30][(y + 60) / 30] == 0)
              {
                y += 30;
              }   
              
              gracePeriod = 0; 

              break;
    
          //Long Block
          case 1 :
          
            switch(shapeTransform)
                {
                //shape size = 120px, so 120px + 180px = 300 (the border)
                //careful copy pasting stuff ahhh
                case "DEFAULT" :
                  if(y != 600 
                  && arr[((x)/30)][(y + 60)/30] == 0
                  && arr[((x + 30)/30)][(y + 60)/30] == 0
                  && arr[((x + 60)/30)][(y + 60)/30] == 0
                  && arr[((x + 90)/30)][(y + 60)/30] == 0)
                  {
                    y += 30;
                  }
                  
                  gracePeriod = 0; 
                  
                  break;
                  
                case "a" :
                
                  if(y != 600 && y != 570 && y != 540 && y != 510
                  && arr[((x)/30)][(y + 150)/30] == 0)
                  {
                    y += 30;
                  }
                  
                  gracePeriod = 0; 
 
                  break;
                }
                
              break;
              
          case 2 :
    
              break;
              
          case 3 :
    
              break;
              
          case 4 :
    
              break;
              
          case 5 :
    
              break;
              
          case 6 :

              break;
        }
      }
    }
    repaint();
  }
  
  //Private helper function that checks if a shape is located ontop of another shape or on the board floor; checks/updates internally; need to make more for each transform
  private int placementCheck()
  {
    switch(shapeType) 
    {
      //Square Block
      case 0 :
          if(y  == 600 
          || arr[(x) / 30][(y + 60) / 30] != 0
          || arr[(x + 30) / 30][(y + 60) / 30] != 0)
          {
    
            if(gracePeriod > 0)
            {
              gracePeriod = gracePeriod - 1;
              
              return 0;
            }
        
            else
            {
              arr[((x + 0 )/30)][( y / 30)] = 1;
              arr[((x + 0 )/30)][((y + 30)/ 30)] = 1;
              arr[((x + 30)/30)][( y / 30)] = 1;
              arr[((x + 30)/30)][((y + 30)/ 30)] = 1;
              
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
            || arr[(x + 0 ) / 30][(y + 60) / 30] != 0
            || arr[(x + 30) / 30][(y + 60) / 30] != 0
            || arr[(x + 60) / 30][(y + 60) / 30] != 0
            || arr[(x + 90) / 30][(y + 60) / 30] != 0)
            {
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0 ) / 30][(y + 30) / 30] = 2;
                arr[(x + 30) / 30][(y + 30) / 30] = 2;
                arr[(x + 60) / 30][(y + 30) / 30] = 2;
                arr[(x + 90) / 30][(y + 30) / 30] = 2;
                    
                return 1;
              }
            }
            break;
            
          case "a" :
            if(y  == 510 
            || arr[(x + 0) / 30][(y + 60) / 30] != 0
            || arr[(x + 0) / 30][(y + 90) / 30] != 0
            || arr[(x + 0) / 30][(y + 120) / 30] != 0
            || arr[(x + 0) / 30][(y + 150) / 30] != 0)
            {
              
              if(gracePeriod > 0)
              {
                gracePeriod = gracePeriod - 1;
                
                return 0;
              }
          
              else
              {
                arr[(x + 0 ) / 30][(y + 30) / 30] = 2;
                arr[(x + 0 ) / 30][(y + 60) / 30] = 2;
                arr[(x + 0 ) / 30][(y + 90) / 30] = 2;
                arr[(x + 0 ) / 30][(y + 120) / 30] = 2;
                    
                return 1;
              }
            }
            break;
        }

          break;
          
      case 2 :

          break;
          
      case 3 :

          break;
          
      case 4 :

          break;
          
      case 5 :

          break;
          
      case 6 :
  
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