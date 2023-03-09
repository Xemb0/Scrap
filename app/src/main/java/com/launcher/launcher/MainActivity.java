package com.launcher.launcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Method;


import com.launcher.launcher.R;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;
    private TextView instructions;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructions = findViewById(R.id.instructions);
        decorView = getWindow().getDecorView();

        gestureDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
    private void GetPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.EXPAND_STATUS_BAR}, 1);
        }
    }

    @Override

    //permisson for notification
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void ExpandNotificationBar(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED)
            return;

        try{
            Object service = getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel"); //<-
            expand.invoke(service);
        }
        catch (Exception e){
            Log.e("StatusBar", e.toString());
            Toast.makeText(getApplicationContext(), "Expansion Not Working", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getY() < e2.getY()) {
            // The user has swiped down
            ExpandNotificationBar();
        } else if (e1.getY() > e2.getY()) {
            // The user has swiped up
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("app_package", getPackageName());
            notificationIntent.putExtra("app_uid", getApplicationInfo().uid);
            startActivity(notificationIntent);
        } else if (e1.getX() < e2.getX()) {
            // The user has swiped left
            Toast.makeText(MainActivity.this, "Hello, World! (left)", Toast.LENGTH_SHORT).show();
        } else if (e1.getX() > e2.getX()) {
            // The user has swiped right
            Toast.makeText(MainActivity.this, "Hello, World! (right)", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
