import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.soap.Text;

public class Viewer extends JPanel {

  private long CurrentAnimationTime = 0;

  Model gameworld = new Model();

  public Viewer(Model World) {
    this.gameworld = World;
    // TODO Auto-generated constructor stub
  }

  public Viewer(LayoutManager layout) {
    super(layout);
    // TODO Auto-generated constructor stub
  }

  public Viewer(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    // TODO Auto-generated constructor stub
  }

  public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    // TODO Auto-generated constructor stub
  }

  public void updateview() {
    this.repaint();
    // TODO Auto-generated method stub

  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    CurrentAnimationTime++; // runs animation time step

    //Draw player Game Object
    int x = (int) gameworld.getPlayer().getCentre().getX();
    int y = (int) gameworld.getPlayer().getCentre().getY();
    int width = (int) gameworld.getPlayer().getWidth();
    int height = (int) gameworld.getPlayer().getHeight();
    String texture = gameworld.getPlayer().getTexture();

    //Draw background (optimise?)
    drawBackground(g, gameworld.getCurrentLevel());

    //Draw Platforms
	  gameworld
      .getPlatforms()
      .forEach(temp -> {
      drawPlatforms(
        (int) temp.getCentre().getX(),
        (int) temp.getCentre().getY(),
        (int) temp.getWidth(),
        (int) temp.getHeight(),
        temp.getTexture(),
        g
      );
    });

    if(!gameworld.goldenStarCollected && gameworld.getGoldenStarList().size() != 0){
      drawGoldenStar(
        (int) gameworld.getGoldenStarList().get(0).getCentre().getX(), 
        (int) gameworld.getGoldenStarList().get(0).getCentre().getY(),
        (int) gameworld.getGoldenStarList().get(0).getWidth(),
        (int) gameworld.getGoldenStarList().get(0).getHeight(),
        gameworld.getGoldenStarList().get(0).getTexture(),
        g);
    }

    //Draw Jump Potion / Status
    if (!gameworld.isJumpPotionActivated() && gameworld.getJumpPotionList().size() != 0){
    drawJumpPotion(
      (int) gameworld.getJumpPotionList().get(0).getCentre().getX(),
      (int) gameworld.getJumpPotionList().get(0).getCentre().getY(),
      (int) gameworld.getJumpPotionList().get(0).getWidth(),
      (int) gameworld.getJumpPotionList().get(0).getHeight(),
      gameworld.getJumpPotionList().get(0).getTexture(),
      g
    );
    }
    else if (gameworld.isJumpPotionActivated()){
      displayStatus("Jump Potion", " Activated!", 750, 50, g, Color.PINK);
    }

    //Draw Fire Potion
    if (!gameworld.isFirePotionActivated() && gameworld.getFireResistancePotionList().size() != 0){
      drawJumpPotion(
        (int) gameworld.getFireResistancePotionList().get(0).getCentre().getX(),
        (int) gameworld.getFireResistancePotionList().get(0).getCentre().getY(),
        (int) gameworld.getFireResistancePotionList().get(0).getWidth(),
        (int) gameworld.getFireResistancePotionList().get(0).getHeight(),
        gameworld.getFireResistancePotionList().get(0).getTexture(),
        g
      );
      }
      else if (gameworld.isFirePotionActivated()){
        displayStatus("Fire Resistance Potion", " Activated!", 750, 50, g, Color.RED);
      }

    //Draw Golden Starv / Statuss
    if(gameworld.goldenStarCollected){
      drawLevelDoor(
        (int) gameworld.getLevelDoor().getCentre().getX(),
        (int) gameworld.getLevelDoor().getCentre().getY(),
        (int) gameworld.getLevelDoor().getWidth(),
        (int) gameworld.getLevelDoor().getHeight(),
        gameworld.getLevelDoor().getTexture(),
        g);
        displayStatus("Star Key", " Acquired!", 750, 75, g, Color.YELLOW);
    }
    if(gameworld.levelCompleted){
      drawLevelDoor(
        (int) gameworld.getLevelDoor().getCentre().getX(),
        (int) gameworld.getLevelDoor().getCentre().getY(),
        (int) gameworld.getLevelDoor().getWidth(),
        (int) gameworld.getLevelDoor().getHeight(),
        gameworld.getLevelDoor().getTexture(),
        g);
        displayStatus("Level Completed!", "  Next Level starting...", 350, 350, g, Color.YELLOW);
    }

    //Draw player
    drawPlayer(x, y, width, height, texture, g, gameworld.isFacingLeft(), gameworld.isDead());

    //Draw Enemies
    gameworld
      .getEnemies()
      .forEach(temp -> {
        drawFireballs(
          (int) temp.getCentre().getX(),
          (int) temp.getCentre().getY(),
          (int) temp.getWidth(),
          (int) temp.getHeight(),
          temp.getTexture(),
          g
        );
      });
  }

  private void drawFireballs(
    int x,
    int y,
    int width,
    int height,
    String texture,
    Graphics g
  ) {
    File TextureToLoad = new File(texture); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      //The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
      //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
      int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4) * 12); //slows down animation so every 10 frames we get another frame so every 100ms
      g.drawImage(
        myImage,
        x,
        y,
        x + width,
        y + height,
        currentPositionInAnimation,
        0,
        currentPositionInAnimation + 11,
        32,
        null
      );
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void drawBackground(Graphics g, int level) {
    File TextureToLoad ;
    Image myImage;
    if(level == 1){
      TextureToLoad = new File("res/forestbackground.png"); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    }
    else if(level == 2){
      TextureToLoad = new File("res/forestbackground2.png"); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    }
    else{
      TextureToLoad = new File("res/forestbackground3.png");
    }
    try {
      myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, 0, 0, 1000, 1000, 0, 0, 1000, 1000, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void drawPlatforms(
    int x,
    int y,
    int width,
    int height,
    String texture,
    Graphics g
  ) {
    File TextureToLoad = new File(texture); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      //64 by 128
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 946, 210, null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void drawJumpPotion(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      //64 by 128
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 200, 200, null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void displayStatus(String status, String statusType, int x, int y, Graphics g, Color color){
    g.setColor(color); // Set the text color to red

    // Change the font size and style
    Font font = new Font("Serif", Font.BOLD, 20); // Create a new font instance (Font Name, Style, Size)
    g.setFont(font); // Set the font

    // Draw the text at the top-left corner
    g.drawString(status + statusType, x, y); // Adjusted y position for visibility
  }

  private void drawGoldenStar(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      //64 by 128
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 150, 200, null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void drawLevelDoor(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture); //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      //64 by 128
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 225, 225, null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  

  private void drawPlayer(int x, int y, int width, int height, String texture, Graphics g, boolean isFacingLeft, boolean isDead) {
    File TextureToLoad = new File(texture);
    try {
        Image myImage = ImageIO.read(TextureToLoad);
        int currentPositionInAnimation = ((int) (CurrentAnimationTime % 3) * 128);
        if(!isDead){
          if (isFacingLeft) {
              // Flip image horizontally when moving left
              g.drawImage(myImage,
                          x + width, // Start drawing from right to left
                          y,
                          x, // End drawing to the original start
                          y + height,
                          currentPositionInAnimation,
                          0,
                          currentPositionInAnimation + 127,
                          128,
                          null);
          } 
          else {
              // Normal drawing when moving right
              g.drawImage(myImage,
                          x,
                          y,
                          x + width,
                          y + height,
                          currentPositionInAnimation,
                          0,
                          currentPositionInAnimation + 127,
                          128,
                          null);
          }
      }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
  // Fireball, forestbackground and startscreen from https://chat.openai.com/
  // Idle from https://craftpix.net/
  // Golden Star from https://www.freepik.com/premium-vector/golden-star-rotate-animation-animated-game-sprite_44235675.htm
}
