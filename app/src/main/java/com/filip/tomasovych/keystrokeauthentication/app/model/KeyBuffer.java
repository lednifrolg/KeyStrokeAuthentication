package com.filip.tomasovych.keystrokeauthentication.app.model;

import java.util.ArrayList;

/**
 * Created by nolofinwe on 3.10.2016.
 */

public class KeyBuffer {

    private static final String TAG = KeyBuffer.class.getSimpleName();

    private ArrayList<KeyObject> mBuffer;
    private int mSize;

    public KeyBuffer() {
        mBuffer = new ArrayList<>();
        mSize = 0;
    }

    /**
     * Get buffer
     * @return Buffer
     */
    public ArrayList<KeyObject> getBuffer() {
        return mBuffer;
    }

    /**
     * Get number of elements in the buffer
     * @return Number of elements in the buffer
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Add KeyObject to the buffer
     * @param key KeyObject to be added
     * @return true if successfully added, false otherwise
     */
    public boolean add(KeyObject key) {
        if (key != null) {
            mBuffer.add(key);
            mSize = mBuffer.size();

            return true;
        }

        return false;
    }

    /**
     * Remove last KeyObject element from the buffer
     */
    public void removeLastElement() {
        if(mSize > 0) {
            mBuffer.remove(mSize - 1);
            mSize = mBuffer.size();
        }
    }

    /**
     * Remove all KeyObject elements from buffer
     */
    public void clear() {
        mBuffer.clear();
        mSize = 0;
    }
}
