import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Viewer extends JPanel {

  private long CurrentAnimationTime = 0;

  Model gameworld = new Model();

  public Viewer(Model World) {
    this.gameworld = World;
  }

  public Viewer(LayoutManager layout) {
    super(layout);
  }

  public Viewer(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  public void updateview() {
    this.repaint();

  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    CurrentAnimationTime++; // runs animation time step

    //Draw background
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

    //Draw player Game Object
    int x = (int) gameworld.getPlayer().getCentre().getX();
    int y = (int) gameworld.getPlayer().getCentre().getY();
    int width = (int) gameworld.getPlayer().getWidth();
    int height = (int) gameworld.getPlayer().getHeight();
    String texture = gameworld.getPlayer().getTexture();
    drawPlayer(x, y, width, height, texture, g, gameworld.isFacingLeft(), gameworld.isDead());

    // Check if Player 2 actiavted
    if(gameworld.two_player){
      x = (int) gameworld.getPlayer2().getCentre().getX();
      y = (int) gameworld.getPlayer2().getCentre().getY();
      width = (int) gameworld.getPlayer2().getWidth();
      height = (int) gameworld.getPlayer2().getHeight();
      texture = gameworld.getPlayer2().getTexture();
      drawPlayer(x, y, width, height, texture, g, gameworld.P2isFacingLeft(), gameworld.P2isDead());
    }

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
      displayStatus("Jump Potion", " Activated!", 650, 50, g, Color.PINK);
    }

    //Draw Fire Potion / Status
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
        displayStatus("Fire Resistance Potion", " Activated!", 650, 25, g, Color.RED);
      }

    // Draw Golden Starv / Status
    if(gameworld.goldenStarCollected){
      drawLevelDoor(
        (int) gameworld.getLevelDoor().getCentre().getX(),
        (int) gameworld.getLevelDoor().getCentre().getY(),
        (int) gameworld.getLevelDoor().getWidth(),
        (int) gameworld.getLevelDoor().getHeight(),
        gameworld.getLevelDoor().getTexture(),
        g);
        displayStatus("Star Key", " Acquired!", 650, 75, g, Color.YELLOW);
    }

    // Draw Level Door
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
    File TextureToLoad = new File(texture);
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4) * 12); //slows down animation
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
      e.printStackTrace();
    }
  }

  private void drawBackground(Graphics g, int level) {
    File TextureToLoad ;
    Image myImage;
    if(level == 1){
      TextureToLoad = new File("res/forestbackground1.png"); 
    }
    else if(level == 2){
      TextureToLoad = new File("res/forestbackground2.png");
    }
    else if(level == 3){
      TextureToLoad = new File("res/forestbackground3.png");
    }
    else{
      TextureToLoad = new File("res/forestbackground4.png");
    }
    try {
      myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, 0, 0, 1000, 1000, 0, 0, 1000, 1000, null);
      if(level == 4){
        displayStatus("Game Completed!", "", 430, 350, g, Color.YELLOW);
        displayStatus("       Deaths: " + gameworld.deaths, "", 430, 375, g, Color.RED);
      }
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
    File TextureToLoad = new File(texture);
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 946, 210, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void drawJumpPotion(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture); 
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 200, 200, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void displayStatus(String status, String statusType, int x, int y, Graphics g, Color color){
    // Set status color
    g.setColor(color);

    // Change the font size and style
    Font font = new Font("Serif", Font.BOLD, 20);
    g.setFont(font); 

    // Draw the text at the top-left corner
    g.drawString(status + statusType, x, y);
  }

  private void drawGoldenStar(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture);
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 150, 200, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void drawLevelDoor(int x, int y, int width, int height, String texture, Graphics g) {
    File TextureToLoad = new File(texture);
    try {
      Image myImage = ImageIO.read(TextureToLoad);
      g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 225, 225, null);
    } catch (IOException e) {
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
  // Fireball, forestbackgrounds and platforms from https://chat.openai.com/
  // Idle from https://craftpix.net/
  // Golden Star & Door from https://www.freepik.com/premium-vector/golden-star-rotate-animation-animated-game-sprite_44235675.htm
  // Potions from https://opengameart.org/
  // Sound Effects from https://pixabay.com/sound-effects/
  // Music Produced by me
}
