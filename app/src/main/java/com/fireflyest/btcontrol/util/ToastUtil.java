package com.fireflyest.btcontrol.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showShort(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }

    public static void showLong(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setText(message);
        toast.show();
    }

    public static void showShort(Context context, int message) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }

    public static void showLong(Context context, int message) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setText(message);
        toast.show();
    }


}
