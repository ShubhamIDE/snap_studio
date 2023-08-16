package com.photo.editor.snapstudio.PhEditor.Editor;


import android.graphics.Point;

import java.util.ArrayList;

public final class VisionDetRet {
    private String mLabel;
    private float mConfidence;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private ArrayList<Point> mLandmarkPoints = new ArrayList<>();

    public VisionDetRet(String label, float confidence, int l, int t, int r, int b) {
        mLabel = label;
        mLeft = l;
        mTop = t;
        mRight = r;
        mBottom = b;
        mConfidence = confidence;
    }

    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getRight() {
        return mRight;
    }

    public int getBottom() {
        return mBottom;
    }

    public float getConfidence() {
        return mConfidence;
    }

    public String getLabel() {
        return mLabel;
    }


    public boolean addLandmark(int x, int y) {
        return mLandmarkPoints.add(new Point(x, y));
    }

    public ArrayList<Point> getFaceLandmarks() {
        return mLandmarkPoints;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Left:");
        sb.append(mLabel);
        sb.append(", Top:");
        sb.append(mTop);
        sb.append(", Right:");
        sb.append(mRight);
        sb.append(", Bottom:");
        sb.append(mBottom);
        sb.append(", Label:");
        sb.append(mLabel);
        return sb.toString();
    }
}