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
  private JButton start, scores, play, menu;
  private JTextArea wm, userName;
  private JTextField name;
  private JPanel frontPage = new JPanel();
  private JPanel fScores = new JPanel();
  private LayoutJPanel layoutjpanel = new LayoutJPanel();
  private JLabel playerText;
  private JTextField playerName = new JTextField(20);

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
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
  
    
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

    setPreferredSize(new Dimension(500,625));
    pack();
    setVisible(true);

  mainMenu();
  }
  
  public void actionPerformed(ActionEvent e){

    getContentPane().removeAll();
    layoutjpanel.timerStop();
  
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
//    
//     JFrame StartGame = new JFrame ("Start Game");
//      StartGame.add(userName);
//      StartGame.add(name);
//      //StartGame.add(play)
//      StartGame.setSize(400, 600);
//      StartGame.setVisible(true);
//      
//      JFrame frame = new JFrame( "Tetris");
//
//      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      

      add( layoutjpanel );

      setContentPane(layoutjpanel);
      revalidate();

      layoutjpanel.setPreferredSize(new Dimension(500,600));

      setContentPane(layoutjpanel);

      pack();

      layoutjpanel.setFocusable(true);
      
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
    
      JTextArea printScores = new JTextArea(allScores);
      printScores.setBounds(0,0,500,625);
      add(fScores);
      setContentPane( fScores );
      fScores.setSize(500,625);
      fScores.add(printScores);
      fScores.setVisible(true);
      fScores.setFocusable(true);
      revalidate();
      repaint();
      pack();
    }
    
    else if(e.getActionCommand() == "Menu")
    {
      mainMenu();
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
  
  public void mainMenu()
  {
      URL imageurl = getClass().getResource("/Images/tetrisimg.png"); 
      Image image = Toolkit.getDefaultToolkit().getImage(imageurl);
      playerText = new JLabel("Enter Player Name: ");
      JLabel pic = new JLabel(new ImageIcon( image ));
      frontPage.add(playerText);
      frontPage.add(playerName);
      add(frontPage);
      frontPage.setPreferredSize(new Dimension(500,625));
      frontPage.add(pic);
      setContentPane( frontPage );
      frontPage.setFocusable(true);
      frontPage.setVisible( true );
      revalidate();
      repaint();
      pack();
  }
  
  public static void main(String args[])
  {
    new TetrisHomePage();
  }
}