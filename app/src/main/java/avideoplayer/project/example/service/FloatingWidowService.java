package avideoplayer.project.example.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import avideoplayer.project.example.R;
import avideoplayer.project.example.activity.VideoPlayer;

public class FloatingWidowService extends Service {
    //The reference variables for the
    //ViewGroup, WindowManager.LayoutParams, WindowManager, Button, EditText classes are created
    private ViewGroup floatView;
    private int LAYOUT_TYPE;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private WindowManager windowManager;
    private ImageButton maximizeBtn;
    private VideoView videoView;
    private ImageButton pausePlayBtn,skipNextBtn,skippreviousBtn,closeBtn;
    private RelativeLayout floating_windowLayout;
    String videoPath;
    VideoPlayer videoPlayer=new VideoPlayer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        //The screen height and width are calculated, cause
        //the height and width of the floating window is set depending on this
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        //To obtain a WindowManager of a different Display,
        //we need a Context for that display, so WINDOW_SERVICE is used
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //A LayoutInflater instance is created to retrieve the LayoutInflater for the floating_layout xml
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflate a new view hierarchy from the floating_layout xml
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_layout, null);
        maximizeBtn= (ImageButton) floatView.findViewById(R.id.fullScreenBtn);
        pausePlayBtn=floatView.findViewById(R.id.pausePlayButton);
        skipNextBtn=floatView.findViewById(R.id.skip_next);
        skippreviousBtn=floatView.findViewById(R.id.skip_previous);
        closeBtn=floatView.findViewById(R.id.closeBtn);
        videoView=floatView.findViewById(R.id.videoViewFw);
        floating_windowLayout=floatView.findViewById(R.id.floating_windowLayout);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                windowManager.removeView(floatView);
            }
        });
        skippreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.previousVideo();
            }
        });
        skipNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.nextVideo();
            }
        });

        //WindowManager.LayoutParams takes a lot of parameters to set the
        //the parameters of the layout. One of them is Layout_type.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            //If API Level is lesser than 26, then we can use TYPE_SYSTEM_ERROR,
            //TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE. But these are all
            //deprecated in API 26 and later. Here TYPE_TOAST works best.
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }
        //Now the Parameter of the floating-window layout is set.
        //1) The Width of the window will be 90% of the phone width.
        //2) The Height of the window will be 58% of the phone height.
        //3) Layout_Type is already set.
        //4) Next Parameter is Window_Flag. Here FLAG_NOT_FOCUSABLE is used. But
        //problem with this flag is key inputs can't be given to the EditText.
        //This problem is solved later.
        //5) Next parameter is Layout_Format. System chooses a format that supports translucency by PixelFormat.TRANSLUCENT
        floatWindowLayoutParam = new WindowManager.LayoutParams(
                (int) (width * (0.9f)),
                (int) (height * (0.5f)),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //The Gravity of the Floating Window is set. The Window will appear in the center of the screen
        floatWindowLayoutParam.gravity = Gravity.CENTER;
        //X and Y value of the window is set
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        //The ViewGroup that inflates the floating_layout.xml is
        //added to the WindowManager with all the parameters
        windowManager.addView(floatView, floatWindowLayoutParam);
        maximizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopSelf() method is used to stop the service if
                //it was previously started
                stopSelf();
                //The window is removed from the screen
                windowManager.removeView(floatView);
                //The app will maximize again. So the MainActivity class will be called again.
                Intent backToHome = new Intent(FloatingWidowService.this, VideoPlayer.class);
                //1) FLAG_ACTIVITY_NEW_TASK flag helps activity to start a new task on the history stack.
                //If a task is already running like the floating window service, a new activity will not be started.
                //Instead the task will be brought back to the front just like the MainActivity here
                //2) FLAG_ACTIVITY_CLEAR_TASK can be used in the conjunction with FLAG_ACTIVITY_NEW_TASK. This flag will
                //kill the existing task first and then new activity is started.
                backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backToHome);

            }
        });

        //Another feature of the floating window is, the window is movable.
        //The window can be moved at any position on the screen.
        floatView.setOnTouchListener(new View.OnTouchListener() {

            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //When the window will be touched, the x and y position of that position will be retrieved
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        //returns the original raw X coordinate of this event
                        px = event.getRawX();
                        //returns the original raw Y coordinate of this event
                        py = event.getRawY();
                        break;
                    //When the window will be dragged around, it will update the x, y of the Window Layout Parameter
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);

                        //updated parameter is applied to the WindowManager
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                        break;
                }

                return false;
            }
        });
        floatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = floatWindowLayoutParam;
                //Layout Flag is changed to FLAG_NOT_TOUCH_MODAL which helps to take inputs inside floating window, but
                //while in EditText the back button won't work and FLAG_LAYOUT_IN_SCREEN flag helps to keep the window
                //always over the keyboard
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                //WindowManager is updated with the Updated Parameters
                windowManager.updateViewLayout(floatView, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

    }
    public void setVideoView()
    {
        if (videoPath != null) {
            videoView.setVideoPath(videoPath);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                  stopSelf();
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        videoPath=intent.getStringExtra("videoPath");
        setVideoView();
        return Service.START_STICKY;
    }

    //It is called when stopService() method is called in MainActivity
    @Override
    public void onDestroy() {

        super.onDestroy();
        stopSelf();
        //Window is removed from the screen
        windowManager.removeView(floatView);
    }
}
