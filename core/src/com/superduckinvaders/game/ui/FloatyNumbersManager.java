package com.superduckinvaders.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages adding, deleting and updating floaty numbers acting as a simple interface
 */
public class FloatyNumbersManager {

    /**
     * List of all current FloatyNumbers
     */
    private List<FloatyNumber> numbersList = new ArrayList<FloatyNumber>();

    /**
     * Create a FloatyNumbersManager
     */
    public FloatyNumbersManager() {
    }

    /**
     * Updates all the floaty numbers and manages the deletion of the numbers if they are 'dead'
     * @param delta
     */
    public void update(float delta){
        List<FloatyNumber> deadList = new ArrayList<FloatyNumber>();

        //Update floaty numbers and check if they are dead
        for(FloatyNumber thisNumber : numbersList){
            thisNumber.update(delta);
            if(thisNumber.isDead()){
                deadList.add(thisNumber);
            }
        }

        //Remove dead floaty numbers
        for(FloatyNumber thisNumber : deadList){
            numbersList.remove(thisNumber);
        }
    }

    /**
     * Renders all the floaty numbers by calling their render functions
     * @param batch
     */
    public void render(SpriteBatch batch){
        for(FloatyNumber thisNumber : numbersList){
            thisNumber.render(batch);
        }
    }

    /**
     * Create a floaty number of the damage type
     * @param number The number the floaty number should render
     * @param x The start x position
     * @param y The start y position
     */
    public void createDamageNumber(int number, float x, float y){
        createNumber(number, FloatyNumber.floatyNumberType.DAMAGE, x, y);
    }

    /**
     * Create a floaty number of the score type
     * @param number The number the floaty number should render
     * @param x The start x position
     * @param y The start y position
     */
    public void createScoreNumber(int number, float x, float y){
        createNumber(number, FloatyNumber.floatyNumberType.SCORE, x, y);
    }

    /**
     * Creates a floaty number for the given number.
     * floaty numbers are created from left to right with the first one set at the given x and y coordinates
     * @param number The number to create floaty numbers for
     * @param type The type of floaty numbers to create
     * @param x The start x position
     * @param y The start y position
     */
    private void createNumber(int number, FloatyNumber.floatyNumberType type, float x, float y){
        String numberString = Integer.toString(number);
        if (type == FloatyNumber.floatyNumberType.SCORE) {
            numberString += "pts";
        } else {
            numberString += "hp";
        }

        numbersList.add(new FloatyNumber(type, numberString, x, y));

    }
}


