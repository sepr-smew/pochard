package com.superduckinvaders.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.entity.item.PowerupManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player of the game.
 */
public class Player extends Character {

    /**
     * Player's maximum health.
     */
    public static final int PLAYER_HEALTH = 6;

    /**
     * Player's standard movement speed in pixels per second.
     */
    public static final int PLAYER_SPEED = 125;

    /**
     * How much the player's speed should be multiplied by if they are swimming.
     */
    public static final float WATER_SPEED_MODIFIER=1.6f;

    /**
     * Player's standard attack delay (how many seconds between attacks).
     */
    public static final int PLAYER_ATTACK_DELAY = 1;

    /**
     * How much the Player's score increases should be multiplied by if they have the score multiplier powerup.
     */
    public static final float PLAYER_SCORE_MULTIPLIER = 5;

    /**
     * How much the Player's speed should be multiplied by if they have the super speed powerup.
     */
    public static final float PLAYER_SUPER_SPEED_MULTIPLIER = 1.7f;

    /**
     * How much the Player's speed should me multiplied by if they are flying.
     */
    public static final float PLAYER_FLIGHT_SPEED_MULTIPLIER = 2.5f;

    /**
     * How much the Player's attack rate should be multiplied by if they have the rate of fire powerup.
     */
    public static final float PLAYER_ATTACK_DELAY_MULTIPLIER = 0.2f;

    /**
     * How long the Player can fly for, in seconds.
     */
    public static final float PLAYER_MAX_FLIGHT_TIME = 1;


    /**
     * Player's current score.
     */
    private int points = 0;

    /**
     * Player's upgrade.
     */
    private Upgrade upgrade = Upgrade.GUN;

    /**
     * Timer limiting flight usage. Flying can only be used if timer is MAX
     */
    private float flyingTimer = PLAYER_MAX_FLIGHT_TIME;

    /**
     * Whether the player is flying or not
     */
    private boolean isFlying = false;

    /**
     * Whether the player is meleeing or not
     */
    private boolean isMeleeing = false;

    /**
     * How long it has been since the Player last attacked.
     */
    private float attackTimer = 0;

    /**
     * 2D array storing the offset from the bottom left of the sprite at which projectiles will be spawned.
     * Basically make the projectiles come out the gun barrel for each direction sprite
     * Rows are for each direction
     * 0th column for x, 1st column for right
     */
    private int[][] projectileDrawPoint = new int[8][2];
    /**
     * 2D array storing the offset from the bottom left of the sprite at which projectiles will be spawned.
     * Basically make the projectiles come out the gun barrel for each direction sprite
     * Rows are for each direction
     * 0th column for x, 1st column for right
     */
    private int[][] projectileDrawPointSwimming = new int[8][2];

    /**
     * Whether the player is currently invulnerable due to just being hit
     */
    private boolean isDamageFrames = false;
    /**
     * Whether the player should be drawn or not. Variable is notted each frame when isDamageFrames is true
     */
    private boolean damageFramesFrame = false;

    /**
     * Timer manages how long the damageFrames invulnerability has lasted
     */
    private float damageFramesTimer = 0;

    /**
     * Indicates the duration of damageFrames
     */
    private final float DAMAGE_FRAMES_LENGTH=1.5f;

    /**
     * The width that the collision of the player should have.
     * Useful for collision detection.
     */
    private int boundsW = 28;

    /**
     * The height that the collision of the player should have.
     * Useful for collision detection.
     */
    private int boundsH = 26;

    /**
     * The x offset for the correct position of the sprite.
     * Useful for collision detection.
     */
    private int boundsX = 2;

    /**
     * The y offset for the correct position of the sprite.
     * Useful for collision detection.
     */
    private int boundsY = 7;

    /**
     * Used in flying movement
     * The maximum acceleration amount
     */
    private final float MAX_ACC=2;

    /**
     * Used in flying movement
     * The minimum acceleration amount
     */
    private final float MIN_ACC=0.5f;

    /**
     * Used in flying movement
     * The amount to change acceleration by each frame
     */
    private final float ACC_SPEED=0.2f;

    /**
     * Used in flying movement
     * The current x acceleration of the player
     */
    private float accX=0;

    /**
     * Used in flying movement
     * The current y acceleration of the player
     */
    private float accY=0;


    /**
     * Initialises this Player at the specified coordinates and with the specified initial health.
     *
     * @param parent the round this Player belongs to
     * @param x      the initial x coordinate
     * @param y      the initial y coordinate
     */
    public Player(Round parent, int x, int y) {
        super(parent, x, y, PLAYER_HEALTH);
        enemyBits = MOB_BITS | PROJECTILE_BITS;
        MELEE_RANGE = 40f;
        createDynamicBody(PLAYER_BITS, ALL_BITS, NO_GROUP, false);

        //Fill the correct values for the projectile draw points
        projectileDrawPoint[TextureSet.FACING_FRONT][0]=7-boundsX;
        projectileDrawPoint[TextureSet.FACING_FRONT][1]=26-boundsY;

        projectileDrawPoint[TextureSet.FACING_FRONT_LEFT][0]=-4-boundsX;
        projectileDrawPoint[TextureSet.FACING_FRONT_LEFT][1]=30-boundsY;

        projectileDrawPoint[TextureSet.FACING_LEFT][0]=0-boundsX;
        projectileDrawPoint[TextureSet.FACING_LEFT][1]=33-boundsY;

        projectileDrawPoint[TextureSet.FACING_BACK_LEFT][0]=17-boundsX;
        projectileDrawPoint[TextureSet.FACING_BACK_LEFT][1]=29-boundsY;

        projectileDrawPoint[TextureSet.FACING_BACK][0]=27-boundsX;
        projectileDrawPoint[TextureSet.FACING_BACK][1]=27-boundsY;

        projectileDrawPoint[TextureSet.FACING_BACK_RIGHT][0]=31-boundsX;
        projectileDrawPoint[TextureSet.FACING_BACK_RIGHT][1]=29-boundsY;

        projectileDrawPoint[TextureSet.FACING_RIGHT][0]=32-boundsX;
        projectileDrawPoint[TextureSet.FACING_RIGHT][1]=27-boundsY;

        projectileDrawPoint[TextureSet.FACING_FRONT_RIGHT][0]=21-boundsX;
        projectileDrawPoint[TextureSet.FACING_FRONT_RIGHT][1]=27-boundsY;

        //Swimming projectile draw points
        projectileDrawPointSwimming[TextureSet.FACING_FRONT][0]=5-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_FRONT][1]=5-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_FRONT_LEFT][0]=-1-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_FRONT_LEFT][1]=11-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_LEFT][0]=2-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_LEFT][1]=13-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_BACK_LEFT][0]=17-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_BACK_LEFT][1]=9-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_BACK][0]=24-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_BACK][1]=10-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_BACK_RIGHT][0]=30-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_BACK_RIGHT][1]=7-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_RIGHT][0]=29-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_RIGHT][1]=8-boundsY;

        projectileDrawPointSwimming[TextureSet.FACING_FRONT_RIGHT][0]=18-boundsX;
        projectileDrawPointSwimming[TextureSet.FACING_FRONT_RIGHT][1]=7-boundsY;

    }

    /**
     * Increases the Player's score by the specified amount.
     *
     * @param amount the amount to increase the score by
     */
    public void addScore(int amount) {
        points += amount;
    }

    /**
     * Gets the Player's current score.
     *
     * @return the current score
     */
    public int getScore() {
        return points;
    }

    /**
     * Gets the Player's current flying timer.
     *
     * @return the current flying timer
     */
    public float getFlyingTimer() {
        return flyingTimer;
    }

    /**
     * Get the players current upgrade (in the Upgrade enum).
     * @return the current upgrade
     */
    public Upgrade getUpgrade(){
        return upgrade;
    }

    /**
     * Sets the Player's current upgrade.
     *
     * @param upgrade the upgrade to set
     */
    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    /**
     * Sets the player to flying, sets up starting conditions and disables collisions
     * There are 2 flying textures, one for left and one for right
     */
    public void enableFlying(){

        Vector2 velocity = new Vector2();
        //Get left/right movement
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
            facing=TextureSet.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = (PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
            facing=TextureSet.FACING_RIGHT;
        }
        //Get up/down movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y = (PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
        }
        accX=0;
        accY=0;
        disableCollision();
        isFlying = true;

        //Normalizes vectors to prevent diagonal movement being faster
        setVelocity(velocity.nor());

        Assets.flying.loop();
    }

    /**
     * Attempts to disable flying. Will not disable if the player is currently over a collision tile
     */
    public void disableFlying(){
        if(collidesX(0))
            return;
        if(collidesY(0))
            return;

        enableCollision();
        isFlying=false;
        if(flyingTimer<0)
            flyingTimer=0;
        Assets.flying.stop();
    }

    /**
     * Sets the player into the meleeing state if not flying and not swimming
     */
    private void doMeleeAttack(){
        if(!isOnWater() && !isFlying) {
            stateTime = 0;
            isMeleeing = true;
            Assets.saber.play(0.1f);
        }
    }

    /**
     * Additional update function for the melee attack state
     * Logic for ending melee state and melee hit detection logic
     * Utilises the MeleeHitbox class to create a rectangular hitbox dependant on the direction the player is facing
     */
    private void updateMeleeAttack(){
        //End condition for melee state
        if(stateTime>=Assets.playerMelee.getAnimationDuration(facing)-Assets.playerMelee.getFrameDuration(facing)/2){
            isMeleeing=false;
        }

        //Melee hit detection, define a hitbox for each facing direction
        MeleeHitbox mHitbox;
        switch (facing){
            case TextureSet.FACING_BACK:{
                mHitbox= new MeleeHitbox(parent, x, y+getHeight(), 32,32);
                break;
            }
            case TextureSet.FACING_BACK_RIGHT:{
                mHitbox= new MeleeHitbox(parent, x+getWidth(), y, 32,64);
                break;
            }
            case TextureSet.FACING_RIGHT:{
                mHitbox= new MeleeHitbox(parent, x+getWidth(), y, 32,64);
                break;
            }
            case TextureSet.FACING_FRONT_RIGHT:{
                mHitbox= new MeleeHitbox(parent, x+getWidth(), y, 32,64);
                break;
            }
            case TextureSet.FACING_FRONT:{
                mHitbox= new MeleeHitbox(parent, x, y-32, 32,32);
                break;
            }
            case TextureSet.FACING_FRONT_LEFT:{
                mHitbox= new MeleeHitbox(parent, x-32, y, 32,64);
                break;
            }
            case TextureSet.FACING_LEFT:{
                mHitbox= new MeleeHitbox(parent, x-32, y, 32,64);
                break;
            }
            case TextureSet.FACING_BACK_LEFT:{
                mHitbox= new MeleeHitbox(parent, x-32, y, 32,64);
                break;
            }
            default:{
                mHitbox = new MeleeHitbox(parent, 0, 0, 0, 0);
                break;
            }

        }
        List<Mob> collideMobs = mHitbox.getCollides();
        for(Mob thisMob : collideMobs){
            thisMob.damage(100);
        }
        if(collideMobs.size()!=0)
            Assets.saberHit.play(0.05f);

    }

    /**
     * @return the width of this Player
     */
    @Override
    public float getWidth() {
        return boundsW;
    }

    /**
     * @return the height of this Player
     */
    @Override
    public float getHeight() {
        return boundsH;
    }

    /**
     * @return the x bounds of this player
     */
    @Override
    public int getBoundsX() {
        return boundsX;
    }

    /**
     * @return the y bounds of this player
     */
    @Override
    public int getBoundsY() {
        return boundsY;
    }

    /**
     * Damages the Player, taking into account the possibility of invulnerability.
     * @param health the number of points to damage by
     */
    @Override
    public void damage(int health) {
        // Only apply damage if we don't have the invulnerability powerup/ invincibility frames
        if(!isFlying) {
            if (!(parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.INVULNERABLE) && !isDamageFrames)) {
                isDamageFrames = true;
                damageFramesTimer = DAMAGE_FRAMES_LENGTH;
                this.currentHealth -= health;
                parent.floatyNumbersManager.createDamageNumber(health, x, y);
            }
        }
    }


    /**
     * Updates the state of this Player.
     * Calls movement and input functions
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {

        if(isDamageFrames){
            damageFramesTimer-=delta;
            if(damageFramesTimer<=0)
                isDamageFrames=false;
        }

        if(isMeleeing)
            updateMeleeAttack();

        //update attack timer.
        attackTimer += delta;

        //Update miscellaneous player inputs
        updatePlayerInputs();

        //Update movement
        if(isFlying)
            updateFlyingMovement();
        else
            updateWalkingMovement();

        // Update animation state time.
        if (!getVelocity().isZero(0.1f) || isMeleeing) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        //Update movement with super function
        super.update(delta);

        if(isFlying){
            //Decrement flying timer.
            flyingTimer -= delta;
            if(flyingTimer<=0)
                disableFlying();

            //Update the flying direction texture using the x velocity
            if(getVelocity().x>=0){
                facing=TextureSet.FACING_RIGHT;
            }
            else{
                facing=TextureSet.FACING_LEFT;
            }
        }
        else{
            //Fill the flying bar over time
            if(flyingTimer<PLAYER_MAX_FLIGHT_TIME)
                flyingTimer+=delta;

            //Update the facing direction using current mouse position
            Vector3 target = parent.unproject(Gdx.input.getX(), Gdx.input.getY());
            facing = directionTo(target.x, target.y);
        }
    }

    /**
     * Checks the necessary inputs for shooting, meleeing and flying and performs the necessary actions
     * Shooting can only be done if enough time has passed between now and the previous shot
     * Flying can only occur if the flyingTimer is full and the player is not stationary
     */
    private void updatePlayerInputs(){
        // Left mouse to attack.
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isFlying && !isMeleeing) {
            //Limits attack rate to attackTimer
            if (attackTimer >= PLAYER_ATTACK_DELAY * (parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.RATE_OF_FIRE) ? PLAYER_ATTACK_DELAY_MULTIPLIER : 1)) {
                attackTimer = 0;

                    //Update aim direction
                    Vector3 target = parent.unproject(Gdx.input.getX(), Gdx.input.getY());

                    //Alter starting point based on if on water or not
                    if(isOnWater()){
                        fireAt(projectileDrawPointSwimming[facing][0],projectileDrawPointSwimming[facing][1],target.x, target.y, 500, 50);
                    }
                    else
                        fireAt(projectileDrawPoint[facing][0],projectileDrawPoint[facing][1],target.x, target.y + 4, 500, 50);

            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
            if(!isOnWater() && !isFlying)
                doMeleeAttack();
        }

        // Press space to start flying, but only if flying isn't cooling down and we're moving.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(isFlying){
                disableFlying();
            }
            if(flyingTimer >= PLAYER_MAX_FLIGHT_TIME && (!getVelocity().isZero(0.1f)))
                enableFlying();

        }
    }

    /**
     * Walking Movement logic, uses player keyboard input to move player character at fixed speeds
     * Direction keys directly change the player velocity
     */
    private void updateWalkingMovement(){
        // Only allow movement via keys if not flying.
        // Calculate speed at which to move the player.
        float speed = PLAYER_SPEED * (parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.SUPER_SPEED) ? PLAYER_SUPER_SPEED_MULTIPLIER : 1);
        speed *= isOnWater() ? WATER_SPEED_MODIFIER : 1;

        Vector2 velocity = new Vector2();
        // Left/right movement.
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -speed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = speed;
        }

        // Left/right movement.
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y = speed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -speed;
        }

        setVelocityClamped(velocity);
    }

    /**
     * Flying movement logic
     * Flying sets the player with a reasonably fast starting velocity using the direction they were walking in
     * When flying, the movement keys affect acceleration instead of velocity giving a "floatier" feel and giving less control
     */
    private void updateFlyingMovement(){

        //Check key inputs  to influence acceleration.
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(accX==0) //Set a starting speed
                accX=-MIN_ACC;
            else
                accX -= ACC_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(accX==0)
                accX=MIN_ACC;
            else
                accX += ACC_SPEED;
        }

        // Left/right movement.
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if(accY==0)
                accY=MIN_ACC;
            else
                accY += ACC_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if(accY==0)
                accY=-MIN_ACC;
            else
                accY -= ACC_SPEED;
        }

        //Limit acceleration to Max
        if(accX<-MAX_ACC)
            accX=-MAX_ACC;
        else if(accX>MAX_ACC)
            accX=MAX_ACC;

        if(accY<-MAX_ACC)
            accY=-MAX_ACC;
        else if(accY>MAX_ACC)
            accY=MAX_ACC;

        //Update velocities with acceleration
//        velocityX+=accX;
//        velocityY+=accY;
//
//        //Limit The Maximum velocity
//        if(velocityX<-(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER))
//            velocityX=-(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
//        else if(velocityX>(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER))
//            velocityX=(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
//
//        if(velocityY<-(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER))
//            velocityY=-(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
//        else if(velocityY>(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER))
//            velocityY=(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER);
    }

    /**
     * Renders this Player with the correct set of animations based on current states
     *
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        // Use the right texture set.
        TextureSet textureSet = isOnWater() ? Assets.playerSwimming : Assets.playerNormal;

        spriteBatch.draw(Assets.shadow2, getX()-2, getY()-6);//Draw the shadow under the player
        if(isFlying)
            spriteBatch.draw(Assets.playerFlying.getTexture(facing, 0), x - getBoundsX()-18, y - getBoundsY()+10);
        else {
            if(!isDamageFrames || isDamageFrames && damageFramesFrame)
                if(isMeleeing)
                    spriteBatch.draw(Assets.playerMelee.getTexture(facing, stateTime), getX() - getBoundsX()-16, getY() - getBoundsY());
                else
                    spriteBatch.draw(textureSet.getTexture(facing, stateTime), getX() - getBoundsX(), getY() - getBoundsY());
        }
        damageFramesFrame=!damageFramesFrame;
    }


    /**
     * Available upgrades (upgrades are persistent).
     */
    public enum Upgrade {
        NONE(null),
        GUN(Assets.floorItemGun);

        public final TextureRegion texture;
        Upgrade(TextureRegion texture){
            this.texture = texture;
        }

        /**
         * Gets a texture for this upgrade's floor item.
         *
         * @return the texture for the floor item
         */
        public TextureRegion getTextureForUpgrade() {
            return this.texture;
        }
    }

    /**
     * Class used to represent a rectangle for the melee hitbox/collision detection
     * Used to provide a function that returns a list of entities that collide with it
     */
    private class MeleeHitbox extends Entity{

        private float width, height;

        /**
         * Creates a rectangular hit box with bottom-left corner at coordinates x,y
         * @param parent The current round that this is a child of
         * @param x The x position (from bottom-left)
         * @param y The y position (from bottom-left)
         * @param width The width of the rectangle
         * @param height The height of the rectangle
         */
        private MeleeHitbox(Round parent, float x, float y, float width, float height){
            super(parent, x, y);
            this.width=width;
            this.height=height;
        }

        /**
         * Render function, does nothing currently, could be used for debug rendering
         * @param spriteBatch the sprite batch on which to render
         */
        public void render(SpriteBatch spriteBatch){

        }

        /**
         * Returns the height of the rectangle
         * @return rectangle height
         */
        public float getHeight(){
            return height;
        }

        /**
         * Returns the width of the rectangle
         * @return rectangle width
         */
        public float getWidth(){
            return width;
        }

        /**
         * Gets a list of Mobs that the hitbox collides with/ are inside the hitbox
         * @return List of mobs that the hitbox collides with
         */
        public List<Mob> getCollides() {
            // Check for entity collisions.
            List<Mob> collisionMobs = new ArrayList<Mob>();
            for (Entity entity : parent.getEntities()) {

                if (entity instanceof Mob && entity.intersects(x, y, getWidth(), getHeight())) {
                    collisionMobs.add((Mob)entity);
                }
            }
            return collisionMobs;
        }


    }

}
