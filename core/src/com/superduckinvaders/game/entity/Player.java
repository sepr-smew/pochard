package com.superduckinvaders.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.entity.item.PowerupManager;

import java.util.ArrayList;

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
    public static final float PLAYER_FLIGHT_SPEED_MULTIPLIER = 5f;

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
     * 2D array storing the offset from the bottom left of the sprite at which projectiles will be spawned.
     * Basically make the projectiles come out the gun barrel for each direction sprite
     * Rows are for each direction
     * 0th column for x, 1st column for right
     */
    private Vector2[] projectileDrawPoint;
    /**
     * 2D array storing the offset from the bottom left of the sprite at which projectiles will be spawned.
     * Basically make the projectiles come out the gun barrel for each direction sprite
     * Rows are for each direction
     * 0th column for x, 1st column for right
     */
    private Vector2[] projectileDrawPointSwimming;

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

    private ArrayList<Contact> flyingContacts = new ArrayList<>();

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
        RANGED_DAMAGE = 50;
        MELEE_ATTACK_COOLDOWN = 0.05f;
        RANGED_ATTACK_COOLDOWN = 0.5f;
        createDynamicBody(PLAYER_BITS, ALL_BITS, NO_GROUP, false);
        createMeleeSensor(40f);

        projectileDrawPoint = new Vector2[]{
                new Vector2(7 - boundsX, 26 - boundsY),  // Front
                new Vector2(-4 - boundsX, 30 - boundsY), // Front Left
                new Vector2(0 - boundsX, 33 - boundsY),  // Left
                new Vector2(17 - boundsX, 29 - boundsY), // Back Left
                new Vector2(27 - boundsX, 27 - boundsY), // Back
                new Vector2(31 - boundsX, 29 - boundsY), // Back Right
                new Vector2(32 - boundsX, 27 - boundsY), // Right
                new Vector2(21 - boundsX, 27 - boundsY)  // Front Right
        };

        //Swimming projectile draw points
        projectileDrawPointSwimming = new Vector2[]{
                new Vector2(5 - boundsX, 5 - boundsY),   // Front
                new Vector2(-1 - boundsX, 11 - boundsY), // Front Left
                new Vector2(2 - boundsX, 13 - boundsY),  // Left
                new Vector2(17 - boundsX, 9 - boundsY),  // Back Left
                new Vector2(24 - boundsX, 10 - boundsY), // Back
                new Vector2(30 - boundsX, 7 - boundsY),  // Back Right
                new Vector2(29 - boundsX, 8 - boundsY),  // Right
                new Vector2(18 - boundsX, 7 - boundsY)   // Front Right
        };
        
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

        setVelocity(getVelocity().setLength(PLAYER_SPEED*PLAYER_FLIGHT_SPEED_MULTIPLIER*1.3f));

        disableCollision();
        isFlying = true;

        Assets.flying.loop();
    }

    /**
     * Attempts to disable flying. Will not disable if the player is currently over a collision tile
     */
    public void disableFlying(){
        if (!flyingContacts.isEmpty()) return;

        enableCollision();
        isFlying=false;
        if(flyingTimer<0)
            flyingTimer=0;
        Assets.flying.stop();
    }

    @Override
    protected boolean meleeAttack(int damage) {
        if (super.meleeAttack(damage)){
            Assets.saberHit.play(0.05f);
            return true;
        }
        return false;
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
     *  -- not any more it doesn't!
     */
    private void updateMeleeAttack(){
        //End condition for melee state
        if(stateTime>=Assets.playerMelee.getAnimationDuration(facing)-Assets.playerMelee.getFrameDuration(facing)/2){
            isMeleeing=false;
        }
        meleeAttack(50);
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

    @Override
    public boolean canBeDamaged(){
        return !(isFlying ||
               parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.INVULNERABLE) ||
               isDamageFrames);
    }

    /**
     * Damages the Player, taking into account the possibility of invulnerability.
     * @param health the number of points to damage by
     */
    @Override
    public void damage(int health) {
        // Only apply damage if we don't have the invulnerability powerup/ invincibility frames
        if(canBeDamaged()) {
            isDamageFrames = true;
            damageFramesTimer = DAMAGE_FRAMES_LENGTH;
            super.damage(health);
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

        //Update miscellaneous player inputs
        updatePlayerInputs();

        //Update movement
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
                facing=TextureSet.Facing.RIGHT;
            }
            else{
                facing=TextureSet.Facing.LEFT;
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
            if (rangedAttackTimer >= RANGED_ATTACK_COOLDOWN * (parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.RATE_OF_FIRE) ? PLAYER_ATTACK_DELAY_MULTIPLIER : 1)) {
                rangedAttackTimer = 0;

                //Update aim direction
                Vector3 target3 = parent.unproject(Gdx.input.getX(), Gdx.input.getY());
                Vector2 target = new Vector2(target3.x, target3.y);

                //Alter starting point based on if on water or not
                Vector2 origin = getPosition()
                                .add((isOnWater() ? projectileDrawPointSwimming : projectileDrawPoint)[facing.index()]);
                Vector2 velocity = target.sub(origin).setLength(500);
                fireAt(origin, velocity);
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

        float speed = PLAYER_SPEED;

        if (parent.powerUpManager.getIsActive(PowerupManager.powerupTypes.SUPER_SPEED))
            speed *= PLAYER_SUPER_SPEED_MULTIPLIER;
        if (isFlying)         speed *= PLAYER_FLIGHT_SPEED_MULTIPLIER;
        else if (isOnWater()) speed *= WATER_SPEED_MODIFIER;

        Vector2 velocity = new Vector2();
        // Left/right movement.
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x += -1f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x += 1f;
        }

        // Left/right movement.
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y += 1f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y += -1f;
        }

        if (isFlying && velocity.isZero())
            velocity = getVelocity();

        velocity.setLength(speed);

        setVelocity(velocity, isFlying ? 0.7f : 4f);
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

        spriteBatch.draw(Assets.playerShadow, getX()-2, getY()-6);//Draw the mobShadow under the player
        if(isFlying)
            spriteBatch.draw(Assets.playerFlying.getTexture(facing, 0), getX() - getBoundsX()-18, getY() - getBoundsY()+10);
        else {
            if(!isDamageFrames || isDamageFrames && damageFramesFrame)
                if(isMeleeing)
                    spriteBatch.draw(Assets.playerMelee.getTexture(facing, stateTime), getX() - getBoundsX()-16, getY() - getBoundsY());
                else
                    spriteBatch.draw(textureSet.getTexture(facing, stateTime), getX() - getBoundsX(), getY() - getBoundsY());
        }
        damageFramesFrame=!damageFramesFrame;
    }


    @Override
    public void beginCollision(PhysicsEntity other, Contact contact) {
        super.beginCollision(other, contact);
        if (isFlying && 0 == (other.getCategoryBits() & (BOUNDS_BITS | WATER_BITS))){
            flyingContacts.add(contact);
        }
    }

    @Override
    public void endCollision(PhysicsEntity other, Contact contact) {
        super.endCollision(other, contact);
        if (isFlying && flyingContacts.contains(contact)){
            flyingContacts.remove(contact);
        }
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
            return texture;
        }
    }
}
