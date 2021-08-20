package avideoplayer.project.example.activity;

import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;

import avideoplayer.project.example.R;
import avideoplayer.project.example.adapter.VideoFolderAdapter;

public class VideoPlayer extends AppCompatActivity implements View.OnClickListener, ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener{
    private static final String KEY_POSITION = "KEY_POSITION";
    private static final float MAX_ZOOM = 5.0f;
    private static final float MIN_ZOOM = 1.0f;
    /* access modifiers changed from: private */
    public ImageView brightnessImage;
    int device_width;
    private Display display;

    /* renamed from: dx */
    private float f96dx = 0.0f;

    /* renamed from: dy */
    private float f97dy = 0.0f;
    /* access modifiers changed from: private */
    public TextView endTime;
    private LinearLayout five;
    private LinearLayout fiveOne;
    private ImageButton forward;
    private LinearLayout four;
    private LinearLayout fourOne;
    /* access modifiers changed from: private */
    public LinearLayout fourTwo;
    GestureDetectorCompat gestureDetector;
    private ImageButton goBack;
    boolean intLeft;
    boolean intRight;
    /* access modifiers changed from: private */
    public boolean isEnable = true;
    private boolean isOpen = true;
    private int lastPosition;
    private float lastScaleFactor = 0.0f;
    private LinearLayout lockScreen;
    private TextView lockTextOne;
    private TextView lockTextTwo;
    private Mode mode = Mode.NONE;
    /* access modifiers changed from: private */
    public ImageButton more;
    /* access modifiers changed from: private */
    public LinearLayout one;
    Animation playPauseAnim;
    /* access modifiers changed from: private */
    public ImageButton playPauseBtn;
    private int position = -1;
    private float prevDx = 0.0f;
    private float prevDy = 0.0f;
    private SeekBar progressBar;
    private ImageButton rewind;
    Animation rotateIn;
    Animation rotateOut;
    private LinearLayout rotation;
    /* access modifiers changed from: private */
    public int sWidth;
    private float scale = 1.0f;
    ScaleGestureDetector scaleDetector;
    /* access modifiers changed from: private */
    public SeekBar seekBar;
    private Point size;
    private float startX = 0.0f;
    private float startY = 0.0f;
    /* access modifiers changed from: private */
    public LinearLayout three;
    private TextView title;
    /* access modifiers changed from: private */
    public LinearLayout track;
    /* access modifiers changed from: private */
    public LinearLayout two;
    private LinearLayout unLockScreen;
    VideoView videoView;
    /* access modifiers changed from: private */
    public int wait = 0;
    RelativeLayout zoomLayout;



    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    public boolean onScale(ScaleGestureDetector scaleDetector2) {
        float scaleFactor = scaleDetector2.getScaleFactor();
        if (this.lastScaleFactor == 0.0f || Math.signum(scaleFactor) == Math.signum(this.lastScaleFactor)) {
            float f = this.scale * scaleFactor;
            this.scale = f;
            this.scale = Math.max(1.0f, Math.min(f, MAX_ZOOM));
            this.lastScaleFactor = scaleFactor;
            return true;
        }
        this.lastScaleFactor = 0.0f;
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playe);
        this.position = getIntent().getIntExtra("position", -1);
        this.display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        this.size = point;
        this.display.getSize(point);
        this.sWidth = this.size.x;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        this.device_width = displaymetrics.widthPixels;
        this.videoView = (VideoView) findViewById(R.id.video_view);
        TextView textView = (TextView) findViewById(R.id.videoView_title);
        this.title = textView;
        textView.setText(VideoFolderAdapter.foderVideoFiles.get(this.position).getTitle());
        this.goBack = (ImageButton) findViewById(R.id.videoView_go_back);
        this.more = (ImageButton) findViewById(R.id.videoView_more);
        this.zoomLayout = (RelativeLayout) findViewById(R.id.zoom_layout);
        this.one = (LinearLayout) findViewById(R.id.videoView_one_layout);
        this.two = (LinearLayout) findViewById(R.id.videoView_two_layout);
        this.three = (LinearLayout) findViewById(R.id.videoView_three_layout);
        this.four = (LinearLayout) findViewById(R.id.videoView_four_layout);
        this.five = (LinearLayout) findViewById(R.id.video_five_layout);
        this.fourOne = (LinearLayout) findViewById(R.id.videoView_four_one_child_layout);
        this.fourTwo = (LinearLayout) findViewById(R.id.videoView_four_two_child_layout);
        this.playPauseBtn = (ImageButton) findViewById(R.id.videoView_play_pause_btn);
        this.seekBar = (SeekBar) findViewById(R.id.videoView_seekbar);
        this.endTime = (TextView) findViewById(R.id.videoView_endtime);
        this.rewind = (ImageButton) findViewById(R.id.videoView_rewind);
        this.forward = (ImageButton) findViewById(R.id.videoView_forward);
        this.progressBar = (SeekBar) findViewById(R.id.videoView_brightness);
        this.brightnessImage = (ImageView) findViewById(R.id.videoView_brightness_image);
        this.lockScreen = (LinearLayout) findViewById(R.id.videoView_lock_screen);
        this.fiveOne = (LinearLayout) findViewById(R.id.video_five_child_layout);
        this.lockTextOne = (TextView) findViewById(R.id.videoView_lock_text);
        this.lockTextTwo = (TextView) findViewById(R.id.videoView_lock_text_two);
        this.rotation = (LinearLayout) findViewById(R.id.videoView_rotation);
        this.track = (LinearLayout) findViewById(R.id.videoView_track);
        this.goBack.setOnClickListener(this);
        this.more.setOnClickListener(this);
        this.playPauseBtn.setOnClickListener(this);
        this.rewind.setOnClickListener(this);
        this.forward.setOnClickListener(this);
        this.lockScreen.setOnClickListener(this);
        this.fiveOne.setOnClickListener(this);
        this.five.setOnClickListener(this);
        this.rotation.setOnClickListener(this);
        this.zoomLayout.setOnTouchListener(this);
        this.gestureDetector = new GestureDetectorCompat(getApplicationContext(), new GestureDetector());
        this.scaleDetector = new ScaleGestureDetector(getApplicationContext(), this);
        this.rotateIn = AnimationUtils.loadAnimation(this, R.anim.rotate_in);
        this.rotateOut = AnimationUtils.loadAnimation(this, R.anim.rotate_out);
        this.playPauseAnim = AnimationUtils.loadAnimation(this, R.anim.fade);
        String path = VideoFolderAdapter.foderVideoFiles.get(this.position).getPath();
        if (path != null) {
            this.videoView.setVideoPath(path);
            this.videoView.requestFocus();
            this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {
                    VideoPlayer.this.seekBar.setMax(VideoPlayer.this.videoView.getDuration());
                    VideoPlayer.this.videoView.start();
                    VideoPlayer.this.track.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            VideoPlayer.this.checkMultiAudioTrack(mp);
                        }
                    });
                    VideoPlayer.this.more.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            int unused = VideoPlayer.this.wait = 5000;
                            VideoPlayer.this.showOptionMenu(v, mp);
                        }
                    });
                    VideoPlayer.this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            VideoPlayer.this.videoView.start();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "Fail to load Video", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        setHandler();
        initializeSeekBar();
        changeBrightnessMode();
        checkDeviceState();
        autoHideControls();
    }

    private void checkDeviceState() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 2) {
            this.two.setVisibility(View.VISIBLE);
        } else if (orientation == 1) {
            this.two.setVisibility(View.GONE);
        }
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(this.scale);
        child().setScaleY(this.scale);
        child().setTranslationX(this.f96dx);
        child().setTranslationY(this.f97dy);
    }

    private View child() {
        return zoomLayout(0);
    }

    private View zoomLayout(int i) {
        return this.videoView;
    }

    /* access modifiers changed from: private */
    public void setPlayBackSpeed(final MediaPlayer mediaPlayer) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.video_speed);
        ((RadioButton) dialog.findViewById(R.id.slow_speed_one)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.5f));
                dialog.dismiss();
            }
        });
        ((RadioButton) dialog.findViewById(R.id.slow_speed_two)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.75f));
                dialog.dismiss();
            }
        });
        ((RadioButton) dialog.findViewById(R.id.speed_normal)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f));
                dialog.dismiss();
            }
        });
        ((RadioButton) dialog.findViewById(R.id.fast_speed_one)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.25f));
                dialog.dismiss();
            }
        });
        ((RadioButton) dialog.findViewById(R.id.fast_speed_two)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.75f));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /* access modifiers changed from: private */
    public void checkMultiAudioTrack(final MediaPlayer mediaPlayer) {
        MediaPlayer.TrackInfo[] trackInfos = mediaPlayer.getTrackInfo();
        ArrayList<Integer> audioTracksIndex = new ArrayList<>();
        for (int i = 0; i < trackInfos.length; i++) {
            if (trackInfos[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                audioTracksIndex.add(Integer.valueOf(i));
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Select Audio Track");
        String[] values = new String[audioTracksIndex.size()];
        for (int i2 = 0; i2 < audioTracksIndex.size(); i2++) {
            values[i2] = "Track " + i2;
        }
        builder.setSingleChoiceItems((CharSequence[]) values, 0, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.selectTrack(which);
                VideoPlayer videoPlayer = VideoPlayer.this;
                Toast.makeText(videoPlayer, "Track " + which + " Selected", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= 21) {
                    mediaPlayer.getSelectedTrack(which);
                }
                mediaPlayer.start();
                Toast.makeText(VideoPlayer.this, "we are working on that :)", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initializeSeekBar() {
        this.wait = 5000;
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getId() == R.id.videoView_seekbar && fromUser) {
                    VideoPlayer.this.videoView.seekTo(progress);
                    VideoPlayer.this.videoView.start();
                    int currentVideoDuration = VideoPlayer.this.videoView.getCurrentPosition();
                    TextView access$800 = VideoPlayer.this.endTime;
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    VideoPlayer videoPlayer = VideoPlayer.this;
                    sb.append(videoPlayer.convertIntoTime(videoPlayer.videoView.getDuration() - currentVideoDuration));
                    access$800.setText(sb.toString());
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getId() == R.id.videoView_seekbar) {
                    VideoPlayer.this.one.setVisibility(View.INVISIBLE);
                    VideoPlayer.this.two.setVisibility(View.INVISIBLE);
                    VideoPlayer.this.three.setVisibility(View.INVISIBLE);
                    VideoPlayer.this.fourTwo.setVisibility(View.INVISIBLE);
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getId() == R.id.videoView_seekbar) {
                    VideoPlayer.this.one.setVisibility(View.VISIBLE);
                    VideoPlayer.this.two.setVisibility(View.VISIBLE);
                    VideoPlayer.this.three.setVisibility(View.VISIBLE);
                    VideoPlayer.this.fourTwo.setVisibility(View.VISIBLE);
                    VideoPlayer.this.playPauseBtn.setImageDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.netflix_pause_button));
                }
            }
        });
        this.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 50) {
                    VideoPlayer.this.screenBrightness(50.0d);
                    VideoPlayer.this.brightnessImage.setImageDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.netflix_brightness_two));
                } else if (progress <= 100) {
                    VideoPlayer.this.screenBrightness(100.0d);
                    VideoPlayer.this.brightnessImage.setImageDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.netflix_brightness_three));
                } else if (progress <= 150) {
                    VideoPlayer.this.screenBrightness(150.0d);
                    VideoPlayer.this.brightnessImage.setImageDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.netflix_brightness_four));
                } else if (progress <= 200) {
                    VideoPlayer.this.screenBrightness(200.0d);
                    VideoPlayer.this.brightnessImage.setImageDrawable(VideoPlayer.this.getResources().getDrawable(R.drawable.netflix_brightness_one));
                } else {
                    VideoPlayer.this.screenBrightness((double) progress);
                }
                int i = progress * 10;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void screenBrightness(double newBrightnessValue) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = ((float) newBrightnessValue) / 255.0f;
        getWindow().setAttributes(lp);
    }

    /* access modifiers changed from: package-private */
    public void changeBrightnessMode() {
        try {
            if (Settings.System.getInt(getContentResolver(), "screen_brightness_mode") == 1) {
                Settings.System.putInt(getContentResolver(), "screen_brightness_mode", 0);
            }
        } catch (Exception e) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void setHandler() {
        final Handler videoHandler = new Handler();
        videoHandler.postDelayed(new Runnable() {
            public void run() {
                if (VideoPlayer.this.videoView.getDuration() > 0) {
                    int currentVideoDuration = VideoPlayer.this.videoView.getCurrentPosition();
                    VideoPlayer.this.seekBar.setProgress(currentVideoDuration);
                    TextView access$800 = VideoPlayer.this.endTime;
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    VideoPlayer videoPlayer = VideoPlayer.this;
                    sb.append(videoPlayer.convertIntoTime(videoPlayer.videoView.getDuration() - currentVideoDuration));
                    access$800.setText(sb.toString());
                }
                videoHandler.postDelayed(this, 0);
            }
        }, 500);
    }

    /* access modifiers changed from: private */
    public String convertIntoTime(int ms) {
        int x = ms / 1000;
        int seconds = x % 60;
        int x2 = x / 60;
        int minutes = x2 % 60;
        int hours = (x2 / 60) % 24;
        if (hours != 0) {
            return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }
         return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoView_forward /*2131231166*/:
                this.wait = 10000;
                VideoView videoView2 = this.videoView;
                videoView2.seekTo(videoView2.getCurrentPosition() + 10000);
                this.forward.startAnimation(this.rotateOut);
                return;
            case R.id.videoView_go_back /*2131231170*/:
                onBackPressed();
                return;
            case R.id.videoView_lock_screen /*2131231171*/:
                hideDefaultControls();
                this.five.setVisibility(View.VISIBLE);
                this.zoomLayout.setClickable(false);
                return;
            case R.id.videoView_play_pause_btn /*2131231176*/:
                this.wait = 5000;
                this.playPauseBtn.startAnimation(this.playPauseAnim);
                if (this.videoView.isPlaying()) {
                    this.videoView.pause();
                    this.playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    return;
                }
                this.videoView.start();
                this.playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.netflix_pause_button));
                return;
            case R.id.videoView_rewind /*2131231177*/:
                this.wait = 10000;
                VideoView videoView3 = this.videoView;
                videoView3.seekTo(videoView3.getCurrentPosition() - 10000);
                this.rewind.startAnimation(this.rotateIn);
                return;
            case R.id.videoView_rotation /*2131231178*/:
                getResources().getConfiguration();
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    return;
                } else if (orientation == 2) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return;
                } else {
                    return;
                }
            case R.id.video_five_child_layout /*2131231186*/:
                this.five.setVisibility(View.GONE);
                showDefaultControls();
                this.zoomLayout.setClickable(true);
                return;
            case R.id.video_five_layout /*2131231187*/:
                if (this.isOpen) {
                    this.fiveOne.setVisibility(View.GONE);
                    this.lockTextOne.setVisibility(View.GONE);
                    this.lockTextTwo.setVisibility(View.GONE);
                    this.isOpen = false;
                    return;
                }
                this.fiveOne.setVisibility(View.VISIBLE);
                this.lockTextOne.setVisibility(View.VISIBLE);
                this.lockTextTwo.setVisibility(View.VISIBLE);
                this.isOpen = true;
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void showOptionMenu(View v, final MediaPlayer mp) {
        final PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.video_player_more_options, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.video_player_speed_options /*2131231190*/:
                        VideoPlayer.this.setPlayBackSpeed(mp);
                        popupMenu.dismiss();
                        return true;
                    case R.id.video_player_volume_options /*2131231191*/:
                        VideoPlayer.this.handleVolume();
                        popupMenu.dismiss();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleVolume() {
        this.wait = 5000;
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(3, audioManager.getStreamMaxVolume(3), 0);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.videoplayer_more_options);
        SeekBar volume = (SeekBar) dialog.findViewById(R.id.videoView_volume_seek_bar);
        volume.setMax(audioManager.getStreamMaxVolume(3));
        volume.setProgress(audioManager.getStreamVolume(3));
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(3, progress, 0);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            this.two.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == 1) {
            this.two.setVisibility(View.VISIBLE);
        }
    }

    public void hideDefaultControls() {
        this.one.setVisibility(View.GONE);
        this.two.setVisibility(View.GONE);
        this.three.setVisibility(View.GONE);
        this.four.setVisibility(View.GONE);
        Window window = getWindow();
        if (window != null) {
            window.clearFlags(2048);
            window.addFlags(1024);
            View decorView = window.getDecorView();
            if (decorView != null) {
                int uiOptions = decorView.getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= 14) {
                    uiOptions |= 1;
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    uiOptions |= 2;
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 4096;
                }
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    public void showDefaultControls() {
        autoHideControls();
        int orientation = getResources().getConfiguration().orientation;
        this.one.setVisibility(View.VISIBLE);
        this.three.setVisibility(View.VISIBLE);
        this.four.setVisibility(View.VISIBLE);
        if (orientation == 2) {
            this.two.setVisibility(View.VISIBLE);
        } else if (orientation == 1) {
            this.two.setVisibility(View.GONE);
        }
        Window window = getWindow();
        if (window != null) {
            window.clearFlags(1024);
            window.addFlags(2048);
            View decorView = window.getDecorView();
            if (decorView != null) {
                int uiOptions = decorView.getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= 14) {
                    uiOptions &= -2;
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    uiOptions &= -3;
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions &= -4097;
                }
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= 26) {
            PictureInPictureParams params = new PictureInPictureParams.Builder().build();
            this.lastPosition = this.videoView.getCurrentPosition();
            this.videoView.pause();
            enterPictureInPictureMode(params);
        }
        super.onUserLeaveHint();
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            hideDefaultControls();
            this.five.setVisibility(View.GONE);
            this.videoView.resume();
            this.videoView.seekTo(this.lastPosition);
            return;
        }
        showDefaultControls();
        checkDeviceState();
        this.playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.netflix_pause_button));
    }

    public void onPause() {
        super.onPause();
        this.lastPosition = this.videoView.getCurrentPosition();
        this.videoView.pause();
        this.playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        persistStopPosition(this.lastPosition);
    }

    private void persistStopPosition(int lastPosition2) {
        SharedPreferences.Editor preferences = getSharedPreferences("Preferences", 0).edit();
        preferences.putInt(KEY_POSITION, lastPosition2);
        preferences.commit();
    }

    public void onStart() {
        int i = getSharedPreferences("Preferences", 0).getInt(KEY_POSITION, -1);
        this.lastPosition = i;
        if (i != -1) {
            this.videoView.seekTo(i);
            this.playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.netflix_pause_button));
        }
        this.videoView.start();
        super.onStart();
    }

    public boolean onTouch(View v, MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            hideDefaultControls();
            if (this.scale > 1.0f) {
                this.mode = Mode.DRAG;
                this.startX = motionEvent.getX() - this.prevDx;
                this.startY = motionEvent.getY() - this.prevDy;
            }
        } else if (action == 1) {
            this.mode = Mode.NONE;
            this.prevDx = this.f96dx;
            this.prevDy = this.f97dy;
        } else if (action == 2) {
            hideDefaultControls();
            this.isEnable = false;
            if (this.mode == Mode.DRAG) {
                this.f96dx = motionEvent.getX() - this.startX;
                this.f97dy = motionEvent.getY() - this.startY;
            }
        } else if (action == 5) {
            this.mode = Mode.ZOOM;
        } else if (action == 6) {
            this.mode = Mode.DRAG;
        }
        this.gestureDetector.onTouchEvent(motionEvent);
        this.scaleDetector.onTouchEvent(motionEvent);
        if ((this.mode == Mode.DRAG && this.scale >= 1.0f) || this.mode == Mode.ZOOM) {
            this.zoomLayout.requestDisallowInterceptTouchEvent(true);
            float f = this.scale;
            float maxDx = ((((float) child().getWidth()) - (((float) child().getWidth()) / f)) / 2.0f) * f;
            float f2 = this.scale;
            float maxDy = ((((float) child().getHeight()) - (((float) child().getHeight()) / f2)) / 2.0f) * f2;
            this.f96dx = Math.min(Math.max(this.f96dx, -maxDx), maxDx);
            this.f97dy = Math.min(Math.max(this.f97dy, -maxDy), maxDy);
            applyScaleAndTranslation();
        }
        return true;
    }

    private void autoHideControls() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                VideoPlayer.this.hideDefaultControls();
                boolean unused = VideoPlayer.this.isEnable = false;
            }
        }, (long) (this.wait + 5000));
    }

    private class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
        private GestureDetector() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (VideoPlayer.this.isEnable) {
                VideoPlayer.this.hideDefaultControls();
                boolean unused = VideoPlayer.this.isEnable = false;
            } else {
                VideoPlayer.this.showDefaultControls();
                boolean unused2 = VideoPlayer.this.isEnable = true;
            }
            return super.onSingleTapConfirmed(e);
        }

        public boolean onDoubleTap(MotionEvent event) {
            if (event.getX() < ((float) (VideoPlayer.this.sWidth / 2))) {
                VideoPlayer.this.intLeft = true;
                VideoPlayer.this.intRight = false;
                VideoPlayer.this.videoView.seekTo(VideoPlayer.this.videoView.getCurrentPosition() - 20000);
                Toast.makeText(VideoPlayer.this, "-20sec", Toast.LENGTH_SHORT).show();
            } else if (event.getX() > ((float) (VideoPlayer.this.sWidth / 2))) {
                VideoPlayer.this.intLeft = false;
                VideoPlayer.this.intRight = true;
                VideoPlayer.this.videoView.seekTo(VideoPlayer.this.videoView.getCurrentPosition() + 20000);
                Toast.makeText(VideoPlayer.this, "+20sec", Toast.LENGTH_SHORT).show();
            }
            return super.onDoubleTap(event);
        }
    }
}
