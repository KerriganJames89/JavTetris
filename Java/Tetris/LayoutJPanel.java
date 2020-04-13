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

  private Timer t = new Timer(250, this);
  private double velX = 2, velY = 2;
  private int x = 0, y = 0;
  private int delay = 0;
  private long timePrevious = 0;
  private long timeCurrent = 0;
  private long timeCheck = 0;
  private Random random = new Random(); 
  private int newShape = 1;
  
  //gracePeriod gives the player time to move their shape when onto of another shape
  private int gracePeriod = 1;
  
  //Tetris board: 10 x 22; only 10 x 20 is drawn on the panel since shapes are created outside of player vision
  private int arr[][] = new int[10][22];

  
  public void paintComponent( Graphics g)
  {
  
      double width = getWidth();
      double height = getHeight();
      
      
      this.addKeyListener(this);
      this.setFocusable(true);
      
      super.paintComponent( g );
      Graphics2D g2d = (Graphics2D) g;
      GeneralPath coordinates = new GeneralPath();
      
        for(int i = 0; i <= width; i+=30){
          coordinates.moveTo(i,height);
          coordinates.lineTo(i,0);
          }
          
        for(int i = 0; i <= height; i+=30){
          coordinates.moveTo(width,i);
          coordinates.lineTo(0,i);
          }
        
        g2d.setColor(Color.BLACK);
        g2d.draw(coordinates);
        
        //need to make a random function in here that selects a shape randomly
        if(newShape == 1)
        {
          //square starting values
          x = 120;
          y = 0;
        
          newShape = 0;
        }
        
        
        //coordinates for a newly made square;
        //need to make a function for each shape coordinates and their rotation positions
        g2d.setColor(Color.RED);
        g2d.fillRect(x + 1, y - 29, 29, 29);
        g2d.fillRect(x + 1, y - 59, 29, 29);
        g2d.fillRect(x + 31, y - 29, 29, 29);
        g2d.fillRect(x + 31, y - 59, 29, 29);
        

      
     //Checks tetris board for all block locations and fills them
    for(int i = 0; i < 10; i++)
    {
      for(int j = 0; j < 22; j++)
      {
        if(arr[i][j] == 1)
        {
          g2d.setColor(Color.RED);
          g2d.fillRect((i * 30) + 1, (j * 30) - 59, 29, 29);
        }
      }
    }
  
        
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
    
    //coordinate testing
    //System.out.println("x value is:" + x);
    //System.out.println("y value is:" + y);
    //System.out.println();
    
    
    System.out.println();
    System.out.println("--------------------------");
  }
 
  public void actionPerformed(ActionEvent e)
  {
    
    //check if square is ontop of another square;
    //we need a function for ALL shapes/rotations here  
    if(y  == 600 
    || arr[(x) / 30][(y + 60) / 30] == 1
    || arr[(x + 30) / 30][(y + 60) / 30] == 1)
    {
    
      if(gracePeriod > 0)
      {
        gracePeriod = gracePeriod - 1;
        return;
      }
      
      else
      {
        //sets square coordinates to ascii 2d array; 
        //we need a function for ALL shapes/rotations here
        arr[((x)/30)][(y/30)] = 1;
        arr[((x + 30)/30)][(y/30)] = 1;
        arr[((x)/30)][((y + 30)/30)] = 1;
        arr[((x + 30)/30)][((y + 30)/30)] = 1;
         
        
        x = 0;
        y = 0;
        
        newShape = 1;
        gracePeriod = 1;
        
        //print test
        printBoard();
        repaint();
        return;
      }
    }
    
    gracePeriod = 1;
    y += 30;
    repaint();
  }
  
  public void keyPressed(KeyEvent e)
  {
      timeCurrent = System.currentTimeMillis();
      timeCheck = timeCurrent - timePrevious;
  
    if (timeCheck < 0 || timeCheck > 50) 
    {
      timePrevious = timeCurrent;
      
      int key = e.getKeyCode();
      
      //collision only works with this specific square shape
      //we need a function for each key to check for collision for ALL shapes/rotations
      if (key == KeyEvent.VK_LEFT) 
      {
        if(x != 0 
        && arr[(x - 30)/30][y/30] != 1
        && arr[(x - 30)/30][(y + 30)/30] != 1)
        {
          x -= 30;
        }
      }
    
      else if (key == KeyEvent.VK_RIGHT) 
      {
        if(x != 240
        && arr[(x + 60)/30][y/30] != 1
        && arr[(x + 60)/30][(y + 30)/30] != 1)
        {
          x += 30;
        }
      }
    
      else if (key == KeyEvent.VK_UP) 
      {
          
      }
    
      else if (key == KeyEvent.VK_DOWN) 
      { 
      if(y  != 600 
        && arr[(x) / 30][(y + 60) / 30] != 1
        && arr[(x + 30) / 30][(y + 60) / 30] != 1)
        {
          y += 30;
        }   
        
        gracePeriod = 0; 
      }
    }
    repaint();
  }
  
  public void keyReleased(KeyEvent e) 
  {
    
  }
  public void keyTyped(KeyEvent e) 
  {
  
  }
}