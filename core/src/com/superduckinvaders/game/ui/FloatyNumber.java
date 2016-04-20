package com.superduckinvaders.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.superduckinvaders.game.assets.Assets;

/**
 * A floaty number is a text string that will -once spawned- perform a simple movement before disappearing
 * This is used for damage and score numbers popping off of enemies when they are hit
 */
public class FloatyNumber {

    /**
     * The type of floaty number this is
     */
    private floatyNumberType type;
    /**
     * Starting coordinates
     */
    private final float STARTX, STARTY;
    /**
     * Current coordinates, velocities and accelerations
     */
    private float x,y, velx, vely, accx, accy;
    /**
     * The string to render
     */
    private String string;

    /**
     * How old the FloatyNumber is
     */
    private float age;
    /**
     * The maximum age of the FloatyNumber before it is deleted
     */
    private final float MAXAGE=1f;

    /**
     * The opacity to render the FloatyNumber with
     */
    private float opacity = 1f;

    /**
     * If the floaty number isDead then it will be deleted on the next update by the manager
     */
    private boolean isDead = false;


    /**
     * Create a new FloatyNumber which will be a string starting at a given x and y. Type defines the movement it will undertake
     * @param type The type is used to decide on the movement logic for the floaty number
     * @param string The character that will be rendered as the floaty number
     * @param x The starting x coordinate
     * @param y The starting y coordinate
     */
    public FloatyNumber(floatyNumberType type, String string, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.STARTX=x;
        this.STARTY=y;
        this.string = string;

        age = MAXAGE;

        //Setup the initial numbers based on the type
        //Damage numbers float upwards, losing opacity as they do so
        if(type==floatyNumberType.DAMAGE) {
            vely = 2f;
            velx= .5f;
            accx = -0.05f;
            accy = -0.05f;

        }
        //Score numbers jump upwards and down again
        else if(type==floatyNumberType.SCORE){
            vely = 9;
            accy = -0.5f;
        }

    }

    /**
     * Update function for floaty number positions
     * Updates position using the velocity and acceleration variables
     * @param delta Time passed between frames
     */
    public void update(float delta){
        age-=delta;
        if(age<=0)
            isDead=true;

        velx+=accx;
        vely+=accy;

        x+=velx;
        y+=vely;

        if(type == floatyNumberType.SCORE) {
            if (y < STARTY)
                y = STARTY;
        }
        else if(type== floatyNumberType.DAMAGE){
            opacity= age/MAXAGE;
            if(vely<0)
                accy=0;
            if(velx<0)
                accx=0;
        }
    }

    /**
     * Renders the character at the current x and y positions
     * Colour is decided by the type of the floaty number
     * @param batch
     */
    public void render(SpriteBatch batch){
        if(type==floatyNumberType.DAMAGE) {
            Assets.font.setColor(0f, 0f, 0f, opacity);
        }
        else{
            Assets.font.setColor(0f, 0f, 0f, 1.0f);
        }
        Assets.font.draw(batch, string, x*2, y*2-2);
        if(type==floatyNumberType.DAMAGE) {
            Assets.font.setColor(1.0f, 00f, 00f, opacity);
        }
        else{
            Assets.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        Assets.font.draw(batch, string, x*2, y*2);
    }

    /**
     * The enum of floaty number types, types decide the floaty number's logic
     */
    public enum floatyNumberType{
        SCORE,DAMAGE
    }

    /**
     * @return true if the FloatyNumber should be deleted
     */
    public boolean isDead() {
        return isDead;
    }
}
