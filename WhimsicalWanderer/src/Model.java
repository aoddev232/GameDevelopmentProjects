import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import util.GameObject;
import util.Point3f;
import util.Vector3f;

public class Model {
  private GameObject Player;
  private GameObject Player2;
  public boolean two_player;

  public AudioPlayer audioPlayer = new AudioPlayer();

  private GameObject JumpPotion;
  private GameObject FireResistancePotion;
  private GameObject GoldenStar;
  private GameObject LevelDoor;
  private GameObject MovingPlatform;

  private CopyOnWriteArrayList<GameObject> FireballList = new CopyOnWriteArrayList<GameObject>();
  private CopyOnWriteArrayList<GameObject> PlatformList = new CopyOnWriteArrayList<GameObject>();
  private CopyOnWriteArrayList<GameObject> PlayerList = new CopyOnWriteArrayList<GameObject>();
  private CopyOnWriteArrayList<GameObject> JumpPotionList = new CopyOnWriteArrayList<GameObject>();
  private CopyOnWriteArrayList<GameObject> FireResistancePotionList = new CopyOnWriteArrayList<GameObject>();
  private CopyOnWriteArrayList<GameObject> GoldenStarList = new CopyOnWriteArrayList<GameObject>();

  private Vector3f velocity = new Vector3f(0, 0, 0);
  private float gravity = -0.15f;
  private boolean isOnPlatform = false;
  private boolean isFacingLeft = false;
  private boolean playerIsDead = false;

  private float defaultJumpVelocity = 7;
  private float jumpPotionJumpVelocity = 10; 
  private boolean jumpPotionActivated = false;
  private boolean fireResistancePotionActivated = false;

  private int currentLevel = 1; //should be 1 initially
  public float goldenStarStartPosition;
  public boolean goldenStarCollected = false;
  public boolean levelCompleted = false; //should be false to start
  public int deaths = 0;
  public int maxFireBallCount = 4;

  private float playerLeft;
  private float playerRight;
  private float playerTop;
  private float playerBottom;

  //PLAYER 2 VARIABLES
  private Vector3f P2velocity = new Vector3f(0, 0, 0);
  private boolean P2isOnPlatform = false;
  private boolean P2isFacingLeft = false;
  private boolean P2playerIsDead = false;
  private float P2playerLeft;
  private float P2playerRight;
  private float P2playerTop;
  private float P2playerBottom;
  
  public Model() {
    startGame();
  }

  public void startGame(){
    CreateLevel(currentLevel);
    Player = new GameObject("res/Idle1.png", 128, 128, new Point3f(650, 500, 0)); //Player 650, 500
    PlayerList.add(Player);
    playerIsDead = false;
    jumpPotionActivated = false;
    fireResistancePotionActivated = false;
    goldenStarCollected = false;
    moving_platform_velocity = 1;
    // levelCompleted = false;
  }

  public void addSecondPLayer(){
    Player2 = new GameObject("res/Idle2.png", 128, 128, new Point3f(750, 500, 0)); //Player 650, 500
    PlayerList.add(Player2);
    P2playerIsDead = false;
    jumpPotionActivated = false;
    fireResistancePotionActivated = false;
    goldenStarCollected = false;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Game Logic
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void gamelogic() {
    playerLogic();
    if(two_player){
      player2logic();
    }
    
    if(currentLevel < 4){
      fireballLogic();
    }

    jumpPotionLogic();
    goldenStarLogic();
    gameLogic();

    if(goldenStarCollected){
      levelDoorLogic();
    }

    if(currentLevel == 2){
      movingPlatformLogic();
    }
    else if(currentLevel == 3){
      movingPlatformLogic();
      fireResistancePotionLogic();
    }
  }

  private void gameLogic() {
    if(goldenStarCollected){
      LevelDoor = new GameObject("res/leveldoor.png", 100, 120, new Point3f(850, 655, 0)); //850
    }
    if(levelCompleted){
      currentLevel++;
      levelCompleted = false;
      goldenStarCollected = false;
      jumpPotionActivated = false;
      fireResistancePotionActivated = false;
      CreateLevel(currentLevel);
      if(two_player){
        addSecondPLayer();
        Player = new GameObject("res/Idle1.png", 128, 128, new Point3f(650, 500, 0)); //Player 650, 500
      }
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Player Logic
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  private void playerLogic() {

    // Check firstly if player has fallen off the map
    if(Player.getCentre().getY() == 1000.0f){
      audioPlayer.playSound("res/falling.wav", false);
      playerIsDead = true;
    }

    // Check if player has been killed
    if(playerIsDead){
      deaths++;
      removeAllGameObjects();
      startGame();
    }

    // Apply gravity if the player is not on a platform
    if (!isOnPlatform) {
        velocity.setY(velocity.getY() + gravity);
        Player.getCentre().ApplyVector(velocity);
    } else {
        // Reset velocity when on a platform to prevent accumulating gravity
        velocity.setY(0);
    }

    if (Controller.getInstance().isKeyAPressed() && !playerIsDead) {
        Player.getCentre().ApplyVector(new Vector3f(-2, 0, 0));
        isFacingLeft = true; // Player is moving left
    } else if (Controller.getInstance().isKeyDPressed() && !playerIsDead) {
        Player.getCentre().ApplyVector(new Vector3f(2, 0, 0));
        isFacingLeft = false; // Player is moving right
    }

    // Jumping
    if (Controller.getInstance().isKeyWPressed() && isOnPlatform) {
        Jump();
    }

    // Update isOnPlatform based on collision detection with platforms
    getPlayerDimensionLocations(Player);
    updateIsOnPlatform();
}

private void player2logic(){
    // Check firstly if player has fallen off the map
    if(Player2.getCentre().getY() == 1000.0f){
    audioPlayer.playSound("res/falling.wav", false);
    P2playerIsDead = true;
    } 

    // Check if player has been killed
    if(P2playerIsDead){
      deaths++;
      addSecondPLayer();
    }

    // Apply gravity if the player is not on a platform
    if (!P2isOnPlatform) {
      P2velocity.setY(P2velocity.getY() + gravity);
      Player2.getCentre().ApplyVector(P2velocity);
    }
    else {
        // Reset velocity when on a platform to prevent accumulating gravity
        P2velocity.setY(0);
    }

    if (Controller.getInstance().isKeyJPressed() && !P2playerIsDead) {
      Player2.getCentre().ApplyVector(new Vector3f(-2, 0, 0));
      P2isFacingLeft = true; // Player is moving left
  } else if (Controller.getInstance().isKeyLPressed() && !P2playerIsDead) {
      Player2.getCentre().ApplyVector(new Vector3f(2, 0, 0));
      P2isFacingLeft = false; // Player is moving right
  }

    // Jumping
    if (Controller.getInstance().isKeyIPressed() && P2isOnPlatform) {
      P2Jump();
  }

  // Update isOnPlatform based on collision detection with platforms
  P2getPlayerDimensionLocations(Player2);
  P2updateIsOnPlatform();
    
}

private void Jump() {
    // This is a simple jumping mechanism, adjust the vector for your game's needs
    float jumpVelocity;
    if(jumpPotionActivated){
      jumpVelocity = jumpPotionJumpVelocity;
    }
    else{
      jumpVelocity = defaultJumpVelocity;
    }

    
    velocity.setY(jumpVelocity); // Adjust the jump strength
    Player.getCentre().ApplyVector(velocity);
}

private void P2Jump() {
  // This is a simple jumping mechanism, adjust the vector for your game's needs
  float jumpVelocity;
  if(jumpPotionActivated){
    jumpVelocity = jumpPotionJumpVelocity;
  }
  else{
    jumpVelocity = defaultJumpVelocity;
  }

  
  P2velocity.setY(jumpVelocity); // Adjust the jump strength
  Player2.getCentre().ApplyVector(P2velocity);
}

private void updateIsOnPlatform() {
  isOnPlatform = false;
  // Basic collision detection
  for (GameObject platform : PlatformList) {
      if (playerIsCollidingWithPlatform(Player, platform)) {
          isOnPlatform = true;
          break; // Exit the loop once a collision is found
      }
  }
}

private void P2updateIsOnPlatform() {
  P2isOnPlatform = false;
  // Basic collision detection
  for (GameObject platform : PlatformList) {
      if (P2playerIsCollidingWithPlatform(Player2, platform)) {
          P2isOnPlatform = true;
          break; // Exit the loop once a collision is found
      }
  }
}

private void getPlayerDimensionLocations(GameObject player) {
  playerLeft = player.getCentre().getX();
  playerRight = player.getCentre().getX() + player.getWidth() - 60;
  playerTop = player.getCentre().getY();
  playerBottom = player.getCentre().getY() + player.getHeight() - 10;
}

private void P2getPlayerDimensionLocations(GameObject player) {
  P2playerLeft = player.getCentre().getX();
  P2playerRight = player.getCentre().getX() + player.getWidth() - 60;
  P2playerTop = player.getCentre().getY();
  P2playerBottom = player.getCentre().getY() + player.getHeight() - 10;
}



private boolean playerIsCollidingWithPlatform(GameObject player, GameObject platform) {
  //note getPlayerDimensionLocations() is getting called right before this

  float platformLeft = platform.getCentre().getX();
  float platformRight = platform.getCentre().getX() + platform.getWidth() - 60;
  float platformTop = platform.getCentre().getY();
  float platformBottom = platform.getCentre().getY() + platform.getHeight() - 10;

  // Check if any of the edges of the rectangles exceed the other's edges.
  boolean collisionX = playerRight > platformLeft && platformRight > playerLeft;
  boolean collisionY = playerBottom > platformTop && platformBottom > playerTop;

  // Ensuring player's bottom edge is not too far above the platform to be considered on it
  boolean isCorrectlyAbovePlatform = playerBottom - platformTop < 10; // Example threshold

  // If X axes are colliding, Y axes are in correct relation, and player is correctly above the platform
  return collisionX && collisionY && isCorrectlyAbovePlatform;
}

private boolean P2playerIsCollidingWithPlatform(GameObject player, GameObject platform) {
  //note getPlayerDimensionLocations() is getting called right before this

  float platformLeft = platform.getCentre().getX();
  float platformRight = platform.getCentre().getX() + platform.getWidth() - 60;
  float platformTop = platform.getCentre().getY();
  float platformBottom = platform.getCentre().getY() + platform.getHeight() - 10;

  // Check if any of the edges of the rectangles exceed the other's edges.
  boolean collisionX = P2playerRight > platformLeft && platformRight > P2playerLeft;
  boolean collisionY = P2playerBottom > platformTop && platformBottom > P2playerTop;

  // Ensuring player's bottom edge is not too far above the platform to be considered on it
  boolean isCorrectlyAbovePlatform = P2playerBottom - platformTop < 10; // Example threshold

  // If X axes are colliding, Y axes are in correct relation, and player is correctly above the platform
  return collisionX && collisionY && isCorrectlyAbovePlatform;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Level Creation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


private void CreateLevel(int level) {
  //Remove any exisiting game objects
  removeAllGameObjects();
  
  //Level platform designs
  int LV1_platformCoordinates[] = {200,180,600,400,250,650,-100,550,-50,750,260,750,570,750,880,750};
  int LV2_platformCoordinates[] = {-10,750,640,750,-150,600,-180,450,-230,300,700,300};
  int LV3_platformCoordinates[] = {630,750,-240,680,-200,200,160,750,-280,550,150,500,600,550,800,440};
  int LV4_platformCoordinates[] = {-50,750,260,750,570,750,880,750};

  //Platform details
  int originalHeight = 210;
  int originalWidth = 946;
  // The actual visual size of the sprite after scaling.
  int scaledVisualWidth = originalWidth / 3; // Should be close to 315
  int scaledVisualHeight = originalHeight / 3; // Should be close to 70

  ArrayList<Integer> level_to_draw = new ArrayList<>();

  switch (level) {
    case 1:
      level_to_draw = populateLevelArray(LV1_platformCoordinates);
      break;
    case 2:
      level_to_draw = populateLevelArray(LV2_platformCoordinates);
      break;
    case 3:
      level_to_draw = populateLevelArray(LV3_platformCoordinates);
      break;
    case 4:
      level_to_draw = populateLevelArray(LV4_platformCoordinates);
      break;
  }

  // CREATE PLATFORMS
  for(int i = 0; i < level_to_draw.size(); i +=2){
      // Create a new GameObject with the visual size for rendering.
      GameObject platform = new GameObject(
          "res/platform1.png",
          scaledVisualWidth,
          scaledVisualHeight,
          new Point3f(level_to_draw.get(i), level_to_draw.get(i+1), 0)
      );
      PlatformList.add(platform);
  }

  // CREATE GAME OBJECTS

  if(level == 1){
    JumpPotion = new GameObject("res/jumppotion.png", 50, 50, new Point3f(30, 520, 0)); // Jump Potion
    goldenStarStartPosition = 160;
    GoldenStar = new GameObject("res/goldenstar.png", 50, 50, new Point3f(200, goldenStarStartPosition, 0 ));

    JumpPotionList.add(JumpPotion);
    GoldenStarList.add(GoldenStar);
  }


  else if(level == 2){
    GameObject moving_platform = new GameObject("res/platform2.png", scaledVisualWidth, scaledVisualHeight, new Point3f(300, 600, 0));
    PlatformList.add(moving_platform);
    // MovingPlatformList.add(moving_platform);
    MovingPlatform = moving_platform;
    moving_platform_velocity = 1;
    JumpPotion = new GameObject("res/jumppotion.png", 50, 50, new Point3f(30, 270, 0));
    JumpPotionList.add(JumpPotion);
    goldenStarStartPosition = 280;
    GoldenStar = new GameObject("res/goldenstar.png", 50, 50, new Point3f(900, goldenStarStartPosition, 0 ));
    GoldenStarList.add(GoldenStar);
  }


  else if(level == 3){
    GameObject moving_platform = new GameObject("res/platform2.png", scaledVisualWidth, scaledVisualHeight, new Point3f(300, 300, 0));
    PlatformList.add(moving_platform);
    // MovingPlatformList.add(moving_platform);
    MovingPlatform = moving_platform;
    moving_platform_velocity = 1;
    left_bounds = 150;
    right_bounds = 420;


    FireResistancePotion = new GameObject("res/fireresistancepotion.png", 50, 50, new Point3f(940, 410, 0));
    FireResistancePotionList.add(FireResistancePotion);

    goldenStarStartPosition = 180;
    GoldenStar = new GameObject("res/goldenstar.png", 50, 50, new Point3f(20, goldenStarStartPosition, 0 ));
    GoldenStarList.add(GoldenStar);
  }
}

private ArrayList<Integer> populateLevelArray (int[] array){
  ArrayList<Integer> arraylist = new ArrayList<>();
  for(int i = 0; i < array.length; i++){
    arraylist.add(array[i]);
  }
  return arraylist;
}

private void removeAllGameObjects(){
  // REMOVE ANY EXISTING PLATFORMS/GAME OBJECTS (IF ANY)
  for(GameObject platform: PlatformList){
    PlatformList.remove(platform);
  }
  for(GameObject jumppotion :JumpPotionList){
    JumpPotionList.remove(jumppotion);
  }
  for(GameObject goldenstar: GoldenStarList){
    GoldenStarList.remove(goldenstar);
  }
  for(GameObject fireball: FireballList){
    FireballList.remove(fireball);
  }
  for(GameObject firepotion : FireResistancePotionList){
    FireResistancePotionList.remove(firepotion);
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Moving Platform Logic
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


boolean moving_platform_turn = false;
int moving_platform_velocity = 1;
int left_bounds = 150;
int right_bounds = 700;
private void movingPlatformLogic(){
    if (!moving_platform_turn && MovingPlatform.getCentre().getX() < right_bounds) {
        MovingPlatform.getCentre().ApplyVector(new Vector3f(moving_platform_velocity, 0, 0));
    } 
    else if (!moving_platform_turn && MovingPlatform.getCentre().getX() >= right_bounds) {
        moving_platform_turn = true; 
    }
    else if (moving_platform_turn && MovingPlatform.getCentre().getX() > left_bounds) {
        MovingPlatform.getCentre().ApplyVector(new Vector3f(-moving_platform_velocity, 0, 0));
    }
    else if (moving_platform_turn && MovingPlatform.getCentre().getX() <= left_bounds) {
        moving_platform_turn = false; 
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Fireball Logic
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


private void fireballLogic() {
  for (GameObject fireball : FireballList) {
    // Check if colliding with player
    if (fireball.getCentre().getX() < Player.getCentre().getX() + Player.getWidth() - 40 &&
    fireball.getCentre().getX() + fireball.getWidth() > Player.getCentre().getX() + 40 &&
    fireball.getCentre().getY() < Player.getCentre().getY() + Player.getHeight() - 40 && 
    fireball.getCentre().getY() + fireball.getHeight() > Player.getCentre().getY() + 80) {
      if(!fireResistancePotionActivated){
        audioPlayer.playSound("res/FireballSound.wav", false);
        playerIsDead = true;
      }
    }
    if(two_player){
      if (fireball.getCentre().getX() < Player2.getCentre().getX() + Player2.getWidth() - 40 &&
      fireball.getCentre().getX() + fireball.getWidth() > Player2.getCentre().getX() + 40 &&
      fireball.getCentre().getY() < Player2.getCentre().getY() + Player2.getHeight() - 40 && 
      fireball.getCentre().getY() + fireball.getHeight() > Player2.getCentre().getY() + 80) {
        if(!fireResistancePotionActivated){
          audioPlayer.playSound("res/FireballSound.wav", false);
          P2playerIsDead = true;
        }
      }
    }

    // Move fireballs
    fireball.getCentre().ApplyVector(new Vector3f(0, -1, 0));

    //see if they get to the top of the screen ( remember 0 is the top
    if (fireball.getCentre().getY() == 900.0f) { // current boundary need to pass value to model
      FireballList.remove(fireball);
    }
  }

  if (FireballList.size() < 2) {
    while (FireballList.size() < maxFireBallCount) {
      FireballList.add(
        new GameObject(
          "res/Fireball.png",
          24,
          72,
          new Point3f(((float) Math.random() * 1000), 0, 0)
        )
      );
    }
  }
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Jump  & Fire Potion Logic
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


private void jumpPotionLogic(){
  for (GameObject jumpPotion : JumpPotionList) {
    // Check if colliding with players
    if (jumpPotion.getCentre().getX() < Player.getCentre().getX() + Player.getWidth() - 60 &&
    jumpPotion.getCentre().getX() + jumpPotion.getWidth() > Player.getCentre().getX() + 60 &&
    jumpPotion.getCentre().getY() < Player.getCentre().getY() + Player.getHeight() - 40 && 
    jumpPotion.getCentre().getY() + jumpPotion.getHeight() > Player.getCentre().getY() + 80) {
        jumpPotionActivated = true;
        JumpPotionList.remove(jumpPotion); //test
        audioPlayer.playSound("res/PotionSound2.wav", false);
    }
    if(two_player){
      if (jumpPotion.getCentre().getX() < Player2.getCentre().getX() + Player2.getWidth() - 60 &&
      jumpPotion.getCentre().getX() + jumpPotion.getWidth() > Player2.getCentre().getX() + 60 &&
      jumpPotion.getCentre().getY() < Player2.getCentre().getY() + Player2.getHeight() - 40 && 
      jumpPotion.getCentre().getY() + jumpPotion.getHeight() > Player2.getCentre().getY() + 80) {
          jumpPotionActivated = true;
          JumpPotionList.remove(jumpPotion); //test
          audioPlayer.playSound("res/PotionSound2.wav", false);
      }
    }
  }
}

private void fireResistancePotionLogic(){
  for (GameObject firePotion : FireResistancePotionList) {
    // Check if colliding with player
    if (firePotion.getCentre().getX() < Player.getCentre().getX() + Player.getWidth() - 60 &&
    firePotion.getCentre().getX() + firePotion.getWidth() > Player.getCentre().getX() + 60 &&
    firePotion.getCentre().getY() < Player.getCentre().getY() + Player.getHeight() - 40 && 
    firePotion.getCentre().getY() + firePotion.getHeight() > Player.getCentre().getY() + 80) {
        fireResistancePotionActivated = true;
        FireResistancePotionList.remove(firePotion);
        audioPlayer.playSound("res/PotionSound1.wav", false);
    }
    if(two_player){
      if (firePotion.getCentre().getX() < Player2.getCentre().getX() + Player2.getWidth() - 60 &&
      firePotion.getCentre().getX() + firePotion.getWidth() > Player2.getCentre().getX() + 60 &&
      firePotion.getCentre().getY() < Player2.getCentre().getY() + Player2.getHeight() - 40 && 
      firePotion.getCentre().getY() + firePotion.getHeight() > Player2.getCentre().getY() + 80) {
          fireResistancePotionActivated = true;
          FireResistancePotionList.remove(firePotion);
          audioPlayer.playSound("res/PotionSound1.wav", false);
      }
    }
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Golden Star Logic & Door Logic
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

float golden_star_bounce_speed = 0.8f;
boolean golden_star_bouncing_up = true;
private void goldenStarLogic() {
  if(golden_star_bouncing_up){
    getGoldenStar().getCentre().ApplyVector(new Vector3f(0, golden_star_bounce_speed, 0));
    if(getGoldenStar().getCentre().getY() < goldenStarStartPosition - 50){
      golden_star_bouncing_up = false;
    }
  }
  else{
    getGoldenStar().getCentre().ApplyVector(new Vector3f(0, -golden_star_bounce_speed, 0));
    if(getGoldenStar().getCentre().getY() > goldenStarStartPosition){
      golden_star_bouncing_up = true;
    }
  }


  if (GoldenStar.getCentre().getX() < Player.getCentre().getX() + Player.getWidth() - 60 &&
  GoldenStar.getCentre().getX() + GoldenStar.getWidth() > Player.getCentre().getX() + 60 &&
  GoldenStar.getCentre().getY() < Player.getCentre().getY() + Player.getHeight() - 40 && 
  GoldenStar.getCentre().getY() + GoldenStar.getHeight() > Player.getCentre().getY() + 80 &&
  !goldenStarCollected) {
      goldenStarCollected = true;
      moving_platform_velocity = 0;
      audioPlayer.playSound("res/GoldenStarSound.wav", false);
  }

  if(two_player){
    if (GoldenStar.getCentre().getX() < Player2.getCentre().getX() + Player2.getWidth() - 60 &&
    GoldenStar.getCentre().getX() + GoldenStar.getWidth() > Player2.getCentre().getX() + 60 &&
    GoldenStar.getCentre().getY() < Player2.getCentre().getY() + Player2.getHeight() - 40 && 
    GoldenStar.getCentre().getY() + GoldenStar.getHeight() > Player2.getCentre().getY() + 80 &&
    !goldenStarCollected) {
        goldenStarCollected = true;
        moving_platform_velocity = 0;
        audioPlayer.playSound("res/GoldenStarSound.wav", false);
    }
  }

}

private void levelDoorLogic(){
  if (LevelDoor.getCentre().getX() < Player.getCentre().getX() + Player.getWidth() - 60 &&
  LevelDoor.getCentre().getX() + LevelDoor.getWidth() > Player.getCentre().getX() + 60 &&
  LevelDoor.getCentre().getY() < Player.getCentre().getY() + Player.getHeight() - 40 && 
  LevelDoor.getCentre().getY() + LevelDoor.getHeight() > Player.getCentre().getY() + 80) {
      levelCompleted = true;
      audioPlayer.playSound("res/DoorSound.wav", false);
  }

  if(two_player){
    if (LevelDoor.getCentre().getX() < Player2.getCentre().getX() + Player2.getWidth() - 60 &&
    LevelDoor.getCentre().getX() + LevelDoor.getWidth() > Player2.getCentre().getX() + 60 &&
    LevelDoor.getCentre().getY() < Player2.getCentre().getY() + Player2.getHeight() - 40 && 
    LevelDoor.getCentre().getY() + LevelDoor.getHeight() > Player2.getCentre().getY() + 80) {
        levelCompleted = true;
        audioPlayer.playSound("res/DoorSound.wav", false);
    }
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Getting Methods
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  public GameObject getPlayer() {
    return Player;
  }

  public GameObject getPlayer2(){
    return Player2;
  }

  public CopyOnWriteArrayList<GameObject> getEnemies() {
    return FireballList;
  }

  public CopyOnWriteArrayList<GameObject> getPlatforms() {
    return PlatformList;
  }

  public CopyOnWriteArrayList<GameObject> getJumpPotionList() {
    return JumpPotionList;
  }

  public CopyOnWriteArrayList<GameObject> getFireResistancePotionList() {
    return FireResistancePotionList;
  }

  public CopyOnWriteArrayList<GameObject> getGoldenStarList() {
    return GoldenStarList;
  }

  public boolean isFacingLeft() {
    return isFacingLeft;
  }

  public boolean isDead(){
    return playerIsDead;
  }

  public GameObject getJumpPotion() {
    return JumpPotion;
  }

  public boolean isJumpPotionActivated(){
    return jumpPotionActivated;
  }

  public GameObject getGoldenStar(){
    return GoldenStar;
  }

  public GameObject getLevelDoor(){
    return LevelDoor;
  }

  public int getCurrentLevel(){
    return currentLevel;
  }

  public boolean isFirePotionActivated(){
    return fireResistancePotionActivated;
  }

  //PLAYER 2 EXTRAS 
  public boolean P2isFacingLeft() {
    return P2isFacingLeft;
  }

  public boolean P2isDead(){
    return P2playerIsDead;
  }
}