import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.*;
import java.util.Scanner;
import java.net.URL; 

public class TetrisHomePage extends JFrame implements ActionListener 
{

  //JMenu Play;
  JButton start, scores, play, menu;
  JTextArea wm, userName;
  JTextField name;
  URL imageurl = getClass().getResource("/Images/tetrisimg.png"); 
  Image image = Toolkit.getDefaultToolkit().getImage(imageurl);
  JLabel pic = new JLabel(new ImageIcon( image ));

  TetrisHomePage (){
  
    //JFrame f = new JFrame("Tetris Home Page");
    JMenuBar menuBar = new JMenuBar();
    //Play = new JMenu("Play");
    //start = new JButton("Start");
    menu = new JButton("Menu");
    scores = new JButton("Scores");
    play = new JButton("Play");
    name = new JTextField();
    name.setBounds(0, 20, 400, 20);
    userName = new JTextArea("User Name", 1, 1);
    userName.setEditable(false);
    userName.setBounds(0, 0, 400, 20);
    wm = new JTextArea();
    wm.setBounds(0, 0, 400, 400);
    wm.setText("\n\n\n\n\t   Welcome to our Tetris Game\n\n\t   by  James Kerrigan\n\n\t               and\n\n\t        Samuel Machado");
  
    
    //Play.addActionListener(this);
    scores.addActionListener(this);
    menu.addActionListener(this);
    //start.addActionListener(this);
    play.addActionListener(this);
    name.addActionListener(this);
    //menuBar.add(Play);
    //menuBar.add(start);
    menuBar.add(menu);
    menuBar.add(play);
    menuBar.add(scores);
    setJMenuBar(menuBar);
    //add(wm);
    
    //setPreferredSize(new Dimension(500,625));
    
    //JPanel frontPage = new JPanel();
    //add(frontPage);
    setPreferredSize(new Dimension(500,625));
    pack();
    
//    URL imageurl = getClass().getResource("/Images/tetrisimg.png"); 
//    Image image = Toolkit.getDefaultToolkit().getImage(imageurl);
//    JLabel pic = new JLabel(new ImageIcon( image ));
//    pic.setBounds(0,0,image.getWidth(null),image.getHeight(null));

    add(pic); 
    setPreferredSize(new Dimension(500,625));
    pack();
    setLayout(null);
    setFocusable(true);
    setVisible(true);
    
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
  }
  
  public void actionPerformed(ActionEvent e){

    getContentPane().removeAll();
  
    if(e.getActionCommand() == "Start"){
      
      //JFrame StartGame = new JFrame ("Start Game");
      //StartGame.add(userName);
      //StartGame.add(name);
      //StartGame.add(play)
      //StartGame.setSize(400, 600);
      //StartGame.setVisible(true);
      //f.remove(wm);
      //f.add(name);
    
    }
  
    else if(e.getActionCommand() == "Play"){
    
     JFrame StartGame = new JFrame ("Start Game");
      StartGame.add(userName);
      StartGame.add(name);
      //StartGame.add(play)
      StartGame.setSize(400, 600);
      StartGame.setVisible(true);
      
    }
    
    else if(e.getActionCommand() == "Scores"){
      
      File scoreFile;
      Scanner readScores;
      String oneLine = "";
      String allScores = "";
      try{
      
         scoreFile = new File("scores.txt");  
         readScores = new Scanner(scoreFile); 
         while(readScores.hasNext()){
         
           oneLine = readScores.nextLine();
           allScores += oneLine;
           allScores +='\n';
         
         }
          
      }
      catch(IOException ioex){
      
         ioex.printStackTrace();
      
      }
    
      JPanel fScores = new JPanel();
      JTextArea printScores = new JTextArea(allScores);
      printScores.setBounds(0,0,500,600);
      setContentPane(fScores);
      fScores.setSize(500,625);
      fScores.add(printScores);
      fScores.setLayout(null);
      fScores.setVisible(true);
      fScores.setFocusable(true);
      revalidate();
      repaint();
    }
    
    else if(e.getActionCommand() == "Menu")
    {
      add(pic);
      repaint();
    }
    
    else{
    
      String PlayerName = name.getText();
      //System.out.printf("%s", PlayerName);
      //File file = new File("scores.txt");
      FileWriter fr = null;
      //Writer out = null;
      try{
      
        System.out.printf("%s", PlayerName);
        fr = new FileWriter("scores.txt", true);
        fr.write("\n");
        fr.write(PlayerName);
        //out = new BufferedWriter(new FileWriter("scores.txt"));
        //out.write((char)PlayerName);
      
      }
      catch(IOException ioex){
      
        ioex.printStackTrace();
      
      }
      finally{
      
        try{
          fr.close();
          //out.close();
        }
        catch(IOException ioex){
        
          ioex.printStackTrace();
        
        }
      
      }
      
      JFrame frame = new JFrame( "Tetris");

      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    

        

      LayoutJPanel layoutjpanel = new LayoutJPanel();
      

      add( layoutjpanel );

      setContentPane(layoutjpanel);
      revalidate();

      layoutjpanel.setPreferredSize(new Dimension(500,600));

      getContentPane().add( layoutjpanel );

      pack();

      setVisible( true );

      setResizable(true);

      setBackground(Color.GRAY);

      layoutjpanel.setFocusable(true);
    
      /*JFrame frame = new JFrame( "Game");
      //frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      JPanel container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
    
      LayoutJPanel LayoutJPanel = new LayoutJPanel();
      AssistantJPanel assistantJPanel = new AssistantJPanel();

      container.add( LayoutJPanel);
      container.add( assistantJPanel);
    
      JTextArea nshape = new JTextArea();
      nshape.setBounds(300, 0, 300, 20);
      nshape.setText("Next Shape");
    
      JTextArea score = new JTextArea();
      score.setBounds(300, 500, 300, 20);
      score.setText("Score");
      
      frame.add(nshape);
      frame.add(score);
      frame.add(container);
      frame.setSize(600, 600);
      frame.setVisible( true );*/
    
    }
  }
  
  public static void main(String args[])
  {
    new TetrisHomePage();
  }
}