package com.superduckinvaders.game.util;

import com.badlogic.gdx.InputAdapter;

/**
 * Listen to a sequence of keys and trigger when the sequence is pressed.
 * It would be cool if it called round.whatever, but I'm trying to avoid awkward dependencies.
 */
public abstract class KeySequenceListener extends InputAdapter {
    private int[] sequence;
    private int sequencePtr;

    /**
     * Create a KeySequenceListener
     * @param sequence the array of keys to supply.
     */
    public KeySequenceListener(int[] sequence) {
        this.sequence = sequence;
        sequencePtr = 0;
    }

    @Override
    public boolean keyDown(int key) {
        if (key == sequence[sequencePtr]) {
            sequencePtr++;
            if (sequencePtr == sequence.length) {
                done();
                sequencePtr = 0;
            }
        } else {
            sequencePtr = 0;
        }
        // To make sure the others fire, we'll pretend we didn't process it.
        return false;
    }

    /**
     * Method called when the sequence is done.
     */
    public abstract void done();
}
