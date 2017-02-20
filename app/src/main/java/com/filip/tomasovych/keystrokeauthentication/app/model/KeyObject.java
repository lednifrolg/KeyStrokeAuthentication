package com.filip.tomasovych.keystrokeauthentication.app.model;

/**
 * Created by nolofinwe on 3.10.2016.
 * Class to hold information about single key presses
 */
public class KeyObject {

    private static final String TAG = KeyObject.class.getSimpleName();

    private Character mKeyChar;
    private long mPressedTime;
    private long mReleasedTime;
    private double mCoordXPressed;
    private double mCoordYPressed;
    private double mCoordXReleased;
    private double mCoordYReleased;
    private double mCenterXCoord;
    private double mCenterYCoord;
    private double mPressedPressure;
    private double mReleasedPressure;


    public Character getKeyChar() {
        return mKeyChar;
    }

    public void setKeyChar(Character keyChar) {
        mKeyChar = keyChar;
    }

    public long getPressedTime() {
        return mPressedTime;
    }

    public void setPressedTime(long pressedTime) {
        mPressedTime = pressedTime;
    }

    public long getReleasedTime() {
        return mReleasedTime;
    }

    public void setReleasedTime(long releasedTime) {
        mReleasedTime = releasedTime;
    }

    public double getCoordXPressed() {
        return mCoordXPressed;
    }

    public void setCoordXPressed(double coordXPressed) {
        mCoordXPressed = coordXPressed;
    }

    public double getCoordYPressed() {
        return mCoordYPressed;
    }

    public void setCoordYPressed(double coordYPressed) {
        mCoordYPressed = coordYPressed;
    }

    public double getCoordXReleased() {
        return mCoordXReleased;
    }

    public void setCoordXReleased(double coordXReleased) {
        mCoordXReleased = coordXReleased;
    }

    public double getCoordYReleased() {
        return mCoordYReleased;
    }

    public void setCoordYReleased(double coordYReleased) {
        mCoordYReleased = coordYReleased;
    }

    public double getCenterXCoord() {
        return mCenterXCoord;
    }

    public void setCenterXCoord(double centerXCoord) {
        mCenterXCoord = centerXCoord;
    }

    public double getCenterYCoord() {
        return mCenterYCoord;
    }

    public void setCenterYCoord(double centerYCoord) {
        mCenterYCoord = centerYCoord;
    }

    public double getPressedPressure() {
        return mPressedPressure;
    }

    public void setPressedPressure(double pressedPressure) {
        mPressedPressure = pressedPressure;
    }

    public double getReleasedPressure() {
        return mReleasedPressure;
    }

    public void setReleasedPressure(double releasedPressure) {
        mReleasedPressure = releasedPressure;
    }

    @Override
    public String toString() {
        return "KeyObject{" +
                "mKeyChar=" + mKeyChar +
                ", mPressedTime=" + mPressedTime +
                ", mReleasedTime=" + mReleasedTime +
                ", mCoordXPressed=" + mCoordXPressed +
                ", mCoordYPressed=" + mCoordYPressed +
                ", mCoordXReleased=" + mCoordXReleased +
                ", mCoordYReleased=" + mCoordYReleased +
                ", mCenterXCoord=" + mCenterXCoord +
                ", mCenterYCoord=" + mCenterYCoord +
                ", mPressedPressure=" + mPressedPressure +
                ", mReleasedPressure=" + mReleasedPressure +
                '}';
    }
}
