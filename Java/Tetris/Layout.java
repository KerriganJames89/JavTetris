import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;


public class Layout {
  public static void main( String args[] )throws InterruptedException{
    JFrame frame = new JFrame( "Tetris");
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
        
    LayoutJPanel layoutjpanel = new LayoutJPanel();
    frame.add( layoutjpanel );
    layoutjpanel.setPreferredSize(new Dimension(300,600));
    frame.getContentPane().add( layoutjpanel );
    frame.pack();
    frame.setVisible( true );
    frame.setResizable(true);
    frame.setBackground(Color.GRAY);
  }
}

