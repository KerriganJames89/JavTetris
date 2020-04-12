import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.*;
import java.util.Random;

public class LayoutJPanel extends JPanel implements ActionListener, KeyListener{

  private Timer t = new Timer(500, this);
  private double velX = 2, velY = 2;
  private int x = 0, y = 0;
  private int delay = 0;
  private long timePrevious = 0;
  private long timeCurrent = 0;
  private long timeCheck = 0;
  private Random random = new Random(); 
  private int newShape = 1;
  
  //Tetris board: 10 x 22
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
        
        if(newShape == 1)
        {
          //square starting values
          x = 120;
          y = 0;
          
          newShape = 0;
        }
        
        g2d.fillRect(x, y - 60, 60, 60);
        
        //coordinate testing
        //System.out.println("x value is:" + x);
        //System.out.println("y value is:" + y);
        //System.out.println();
        
        
    //specific to the 4x4 square block piece that detects collision    
    if(y  == 600 
    || arr[(x) / 30][(y + 60) / 30] == 1
    || arr[(x + 30) / 30][(y + 60) / 30] == 1)
    {
      arr[((x)/30)][(y/30)] = 1;
      arr[((x + 30)/30)][(y/30)] = 1;
      arr[((x)/30)][((y + 30)/30)] = 1;
      arr[((x + 30)/30)][((y + 30)/30)] = 1;
       
      
      x = 0;
      y = 0;
      
      newShape = 1;
      
      //print test
      printBoard();
      
    }
      
     //Checks tetris board for all block locations and fills them
    for(int i = 0; i < 10; i++)
    {
      for(int j = 0; j < 22; j++)
      {
        if(arr[i][j] == 1)
        {
          g2d.fillRect((i * 30), (j * 30) - 60, 30, 30);
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
    System.out.println();
    System.out.println("--------------------------");
  }
 
  public void actionPerformed(ActionEvent e){
    
    
    y += 30;
    
    repaint();
  }
  
  public void keyPressed(KeyEvent e)
  {
      timeCurrent = System.currentTimeMillis();
      timeCheck = timeCurrent - timePrevious;
  
    if (timeCheck < 0 || timeCheck > 100) 
    {
      timePrevious = timeCurrent;
      
      int key = e.getKeyCode();
      
      //collision only works with this specific shape
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
        y += 30;
      }
      
      repaint();
    }
  }
  
  public void keyReleased(KeyEvent e) 
  {
    
  }
  public void keyTyped(KeyEvent e) 
  {
  
  }
}