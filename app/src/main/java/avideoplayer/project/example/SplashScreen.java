package avideoplayer.project.example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import avideoplayer.project.example.activity.MainActivity;


public class SplashScreen extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 123;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permission();
    }

    private void nextActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
            return;
        }
        nextActivity();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 123) {
            return;
        }
        if (grantResults[0] == 0) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
            nextActivity();
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
    }
}
