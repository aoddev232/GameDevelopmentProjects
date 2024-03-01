import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import util.UnitTests;


public class MainWindow {
	 private static JFrame frame = new JFrame("Whimsical Wanderer"); 
	 private static Model gameworld= new Model();
	 private static Viewer canvas = new  Viewer( gameworld);
	 private KeyListener Controller =new Controller()  ; 
	 private static int TargetFPS = 100;
	 private static boolean startGame= false; 
	 private JLabel BackgroundImageForStartMenu ;
	 public AudioPlayer audioplayer = new AudioPlayer();
	  
	public MainWindow() {
	        frame.setSize(1000, 1000);
	      	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLayout(null);
	        frame.add(canvas);  
	        canvas.setBounds(0, 0, 1000, 1000); 
			canvas.setBackground(new Color(255,255,255));
		    canvas.setVisible(false); 

			// Buttons:
	        JButton startMenuButton1P = new JButton("Play Game - Single Player");  // start button
	        startMenuButton1P.addActionListener(new ActionListener()
			{ 
			@Override
			public void actionPerformed(ActionEvent e) { 
				startMenuButton1P.setVisible(false);
				BackgroundImageForStartMenu.setVisible(false); 
				canvas.setVisible(true); 
				canvas.addKeyListener(Controller);
				canvas.requestFocusInWindow();
				startGame=true;

			}});  
	        startMenuButton1P.setBounds(400, 550, 200, 40);
			startMenuButton1P.setBackground(Color.PINK);
			startMenuButton1P.setForeground(Color.BLACK); 
			frame.add(startMenuButton1P);
			

			JButton startMenuButton2P = new JButton("Play Game - Two Player");
	        startMenuButton2P.addActionListener(new ActionListener()
			{ 
			@Override
			public void actionPerformed(ActionEvent e) { 
				startMenuButton2P.setVisible(false);
				BackgroundImageForStartMenu.setVisible(false); 
				canvas.setVisible(true); 
				canvas.addKeyListener(Controller);
				canvas.requestFocusInWindow();
				startGame=true;
				gameworld.two_player = true;
				gameworld.addSecondPLayer();
			}});  
	        startMenuButton2P.setBounds(400, 600, 200, 40); 
			startMenuButton2P.setBackground(Color.PINK);
			startMenuButton2P.setForeground(Color.BLACK);
			frame.add(startMenuButton2P);  
	        

	        // Loading background image 
	        File BackroundToLoad = new File("res/forestbackground0.png");
			try {
				 
				BufferedImage myPicture = ImageIO.read(BackroundToLoad);
				BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
				BackgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
				frame.add(BackgroundImageForStartMenu); 
			}  catch (IOException e) { 
				e.printStackTrace();
			}   
			frame.setTitle("Whimsical Wanderer - Main Menu"); 
	       	frame.setVisible(true);   
			audioplayer.playSound("res/GameMusic.wav", true);
	}

	public static void main(String[] args) {
		MainWindow mainwindow = new MainWindow();  //sets up environment 
		while(true)
		{
			int TimeBetweenFrames =  1000 / TargetFPS;
			long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames; 
			
			//wait till next time step 
		 	while (FrameCheck > System.currentTimeMillis()){} 
			
			if(startGame)
			{
				gameloop();
			}
	
			//UNIT test to see if framerate matches 
			UnitTests.CheckFrameRate(System.currentTimeMillis(),FrameCheck, TargetFPS); 
		}
	} 

	//Basic Model-View-Controller pattern 
	private static void gameloop() { 
		// GAMELOOP  

		// model update   
		gameworld.gamelogic();
		// view update 
		canvas.updateview(); 
		
		if(gameworld.getCurrentLevel() == 4){
			frame.setTitle("Whimsical Wanderer - Game Completed!"); 
		}
		else{
			frame.setTitle("Whimsical Wanderer - Level " + gameworld.getCurrentLevel() + "/3             Deaths: " + gameworld.deaths); 
		}
	}

}