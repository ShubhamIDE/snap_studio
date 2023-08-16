package com.photo.editor.snapstudio.PhEditor.support;

import android.app.Activity;
import android.content.Intent;

import com.photo.editor.snapstudio.Activity.DashboardActivity;


public class MyExceptionHandlerPix implements Thread.UncaughtExceptionHandler {
    private Activity activity;

    public MyExceptionHandlerPix(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Intent intent = null;
        if (activity != null) {
            intent = new Intent(activity, DashboardActivity.class);
//        } else if (AppEditor.getContext() != null) {
//            intent = new Intent(AppEditor.getContext(), DashboardActivity.class);
//        }
            intent.putExtra("crash", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(AppEditor.getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager mgr = (AlarmManager) AppEditor.getContext().getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, pendingIntent);
            System.exit(2);
        }
    }
}