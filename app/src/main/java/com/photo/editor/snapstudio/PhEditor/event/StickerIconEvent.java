package com.photo.editor.snapstudio.PhEditor.event;

import android.view.MotionEvent;

import com.photo.editor.snapstudio.PhEditor.sticker.StickerView;

public interface StickerIconEvent {
    void onActionDown(StickerView paramStickerView, MotionEvent paramMotionEvent);

    void onActionMove(StickerView paramStickerView, MotionEvent paramMotionEvent);

    void onActionUp(StickerView paramStickerView, MotionEvent paramMotionEvent);
}
