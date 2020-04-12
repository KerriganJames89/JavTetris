import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Component;


public class Layout {
  public static void main( String args[] ){
    JFrame frame = new JFrame( "Tetris");
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
    LayoutJPanel layoutjpanel = new LayoutJPanel();
    frame.add( layoutjpanel );
    frame.setSize(300, 600);
    frame.setVisible( true );
    frame.setResizable(false);
  }
}

