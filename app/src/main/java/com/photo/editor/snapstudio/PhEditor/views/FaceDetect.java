package com.photo.editor.snapstudio.PhEditor.views;

import android.graphics.Rect;

import com.photo.editor.snapstudio.PhEditor.Editor.Landmark;

import java.util.ArrayList;
import java.util.List;

public class FaceDetect {
    public Rect faceRect;
    public List<Landmark> landmarks = new ArrayList();

    public FaceDetect(List<Landmark> list, int[] iArr) {
        Rect rect = new Rect();
        this.faceRect = rect;
        rect.left = iArr[0];
        this.faceRect.top = iArr[1];
        this.faceRect.right = iArr[2];
        this.faceRect.bottom = iArr[3];
        this.landmarks = list;
    }

    public List<Landmark> getLandmarks() {
        return this.landmarks;
    }

    public List<Landmark> getLeftEyeLandmarks() {
        return this.landmarks.subList(11, 17);
    }

    public List<Landmark> getRightEyeLandmarks() {
        return this.landmarks.subList(17, 23);
    }


    public List<Landmark> getLeftSlimLandmarks() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.landmarks.get(2));
        arrayList.add(this.landmarks.get(1));
        arrayList.add(this.landmarks.get(0));
        arrayList.add(this.landmarks.get(16));
        arrayList.add(this.landmarks.get(8));
        arrayList.add(this.landmarks.get(23));
        return arrayList;
    }

    public List<Landmark> getRightSlimLandmarks() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.landmarks.get(3));
        arrayList.add(this.landmarks.get(4));
        arrayList.add(this.landmarks.get(5));
        arrayList.add(this.landmarks.get(21));
        arrayList.add(this.landmarks.get(10));
        arrayList.add(this.landmarks.get(29));
        return arrayList;
    }

    public List<Landmark> getOuterMouthLandmarks() {
        return this.landmarks.subList(23, 35);
    }

    public List<Landmark> getInnerMouthLandmarks() {
        return this.landmarks.subList(35, 43);
    }

    public Rect getFaceRect() {
        return this.faceRect;
    }

    public void setFaceRect(Rect rect) {
        this.faceRect = rect;
    }

    public void setLandMarks(List<Landmark> list) {
        this.landmarks = list;
    }

}
