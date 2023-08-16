package com.photo.editor.snapstudio.PhEditor.Editor;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public final class Landmark {

    private final int f226id;
    private PointF point;

    public Landmark(PointF pointF, int i) {
        this.point = pointF;
        this.f226id = i;
    }

    public static List<Landmark> generateExtendedLandmarks(List<Landmark> list, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList();
        int i3 = 0;
        while (i3 < list.size() - 1) {
            i3++;
            arrayList.addAll(extend((Landmark) list.get(i3), (Landmark) list.get(i3), i, i2, z));
        }
        arrayList.addAll(extend((Landmark) list.get(list.size() - 1), (Landmark) list.get(0), i, i2, z));
        return arrayList;
    }

    public static List<Landmark> generateExtendedLandmarksForDarkPath(List<Landmark> list, int i, int i2) {
        if (list == null || list.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        float f = ((Landmark) list.get(0)).getPosition().x - ((Landmark) list.get(list.size() / 2)).getPosition().x;
        double d = (double) (-(((Landmark) list.get(0)).getPosition().y - ((Landmark) list.get(list.size() / 2)).getPosition().y));
        double d2 = (double) (-f);
        Double.isNaN(d2);
        float f2 = ((float) (d2 * 0.2d)) + ((Landmark) list.get(list.size() / 2)).getPosition().x;
        float f3 = ((Landmark) list.get(list.size() / 2)).getPosition().y;
        Double.isNaN(d);
        Landmark landmark = new Landmark(new PointF(f2, f3 + ((float) (d * 0.2d))), 37);
        arrayList.add(list.get(0));
        double d3 = (double) f;
        Double.isNaN(d3);
        float f4 = ((float) (d3 * 0.2d)) + ((Landmark) list.get(0)).getPosition().x;
        float f5 = ((Landmark) list.get(0)).getPosition().y;
        Double.isNaN(d);
        arrayList.add(new Landmark(new PointF(f4, f5 + ((float) (d * 1.2d))), 37));
        for (int size = list.size() - 1; size > list.size() / 2; size--) {
            double d4 = (double) (((Landmark) list.get(size)).getPosition().x - ((Landmark) list.get(list.size() - size)).getPosition().x);
            Double.isNaN(d4);
            arrayList.add(new Landmark(new PointF(((float) (d4 * 1.5d)) + ((Landmark) list.get(size)).getPosition().x, ((Landmark) list.get(size)).getPosition().y + ((((Landmark) list.get(size)).getPosition().y - ((Landmark) list.get(list.size() - size)).getPosition().y) * 2.0f)), 43));
        }
        arrayList.add(landmark);
        arrayList.addAll(list.subList(3, 5));
        return generateExtendedLandmarks(arrayList, i, i2, true);
    }

    private static List<Landmark> extend(Landmark landmark, Landmark landmark2, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList();
        float f = landmark.getPosition().x - landmark2.getPosition().x;
        float f2 = landmark.getPosition().y - landmark2.getPosition().y;
        arrayList.add(landmark);
        int i3 = 0;
        while (i3 < i) {
            PointF pointF = new PointF();
            float f3 = (float) (i + 1);
            i3++;
            float f4 = (float) i3;
            pointF.x = landmark.getPosition().x - ((f / f3) * f4);
            pointF.y = landmark.getPosition().y - ((f2 / f3) * f4);
            if (f < 0.0f && !z) {
                pointF.y -= (Math.abs(f2) / 100.0f) * ((float) i2);
            } else if (f > 0.0f || z) {
                pointF.y += (Math.abs(f2) / 100.0f) * ((float) i2);
            }
            arrayList.add(new Landmark(pointF, 43));
        }
        return arrayList;
    }

    public PointF getPosition() {
        return this.point;
    }

    public void setPosition(PointF pointF) {
        this.point = pointF;
    }

    public void setPosition(float f, float f2) {
        this.point.x = f;
        this.point.y = f2;
    }

    public int getType() {
        return this.f226id;
    }
}
