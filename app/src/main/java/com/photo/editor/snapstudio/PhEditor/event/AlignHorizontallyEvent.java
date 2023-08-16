package com.photo.editor.snapstudio.PhEditor.event;

import android.view.MotionEvent;

import com.photo.editor.snapstudio.PhEditor.sticker.StickerView;

public class AlignHorizontallyEvent implements StickerIconEvent {
    public void onActionDown(StickerView paramStickerView, MotionEvent paramMotionEvent) {
    }

    public void onActionMove(StickerView paramStickerView, MotionEvent paramMotionEvent) {
    }

    public void onActionUp(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        paramStickerView.alignHorizontally();
    }
}
