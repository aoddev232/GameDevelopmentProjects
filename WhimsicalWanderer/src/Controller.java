import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {
        
	   private static boolean KeyAPressed= false;
	   private static boolean KeyDPressed= false;
	   private static boolean KeySpacePressed= false;

	   //2 player mode
	   private static boolean KeyJPressed= false;
	   private static boolean KeyLPressed= false;
	   private static boolean KeyIPressed= false;
	   
	   private static final Controller instance = new Controller();
	   
	 public Controller() { 
	}
	 
	 public static Controller getInstance(){
	        return instance;
	    }
	   
	@Override
	// Key pressed , will keep triggering 
	public void keyTyped(KeyEvent e) { 
		 
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{ 
		switch (e.getKeyChar()) 
		{
			case 'a':setKeyAPressed(true);break;  
			case 'd':setKeyDPressed(true);break;
			case 'w':setKeyWPressed(true);break;   
			case 'j':setKeyJPressed(true);break;
			case 'l':setKeyLPressed(true);break;
			case 'i':setKeyIPressed(true);break;
		    default:
		    	System.out.println("Controller test:  Unknown key pressed");
		        break;
		}  
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{ 
		switch (e.getKeyChar()) 
		{
			case 'a':setKeyAPressed(false);break;  
			case 'd':setKeyDPressed(false);break;
			case 'w':setKeyWPressed(false);break;   
			case 'j':setKeyJPressed(false);break;
			case 'l':setKeyLPressed(false);break;
			case 'i':setKeyIPressed(false);break;
		    default:
		    	System.out.println("Controller test:  Unknown key pressed");
		        break;
		}  
	}



	// GETTER & SETTER METHODS 
	public boolean isKeyAPressed() {
		return KeyAPressed;
	}


	public void setKeyAPressed(boolean keyAPressed) {
		KeyAPressed = keyAPressed;
	}

	public boolean isKeyDPressed() {
		return KeyDPressed;
	}


	public void setKeyDPressed(boolean keyDPressed) {
		KeyDPressed = keyDPressed;
	}


	public boolean isKeyWPressed() {
		return KeySpacePressed;
	}


	public void setKeyWPressed(boolean keyWPressed) {
		KeySpacePressed = keyWPressed;
	} 
	

	// *** 2 Player Mode ***
	public boolean isKeyJPressed() {
		return KeyJPressed;
	}

	public void setKeyJPressed(boolean keyJPressed) {
		KeyJPressed = keyJPressed;
	}

	public boolean isKeyLPressed() {
		return KeyLPressed;
	}

	public void setKeyLPressed(boolean keyLPressed) {
		KeyLPressed = keyLPressed;
	}

	public boolean isKeyIPressed() {
		return KeyIPressed;
	}

	public void setKeyIPressed(boolean keyIPressed) {
		KeyIPressed = keyIPressed;
	} 
	 
}