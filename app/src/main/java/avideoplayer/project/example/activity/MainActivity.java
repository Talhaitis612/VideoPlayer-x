package avideoplayer.project.example.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Locale;

import avideoplayer.project.example.R;
import avideoplayer.project.example.VideoFiles;
import avideoplayer.project.example.adapter.FolderAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {
    static ArrayList<String> folderList = new ArrayList<>();
    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;
    DrawerLayout drawerLayout;
    FolderAdapter folderAdapter;
    RecyclerView folder_RecyclerView;
    ImageView menuIcon;
    NavigationView navigationView;
    SwipeRefreshLayout swipeRefreshLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.toolbar = (Toolbar) findViewById(R.id.folder_toolbar);
        this.navigationView = (NavigationView) findViewById(R.id.folder_nav);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.menuIcon = (ImageView) findViewById(R.id.drawer_menu_icon);
        navigationDrawer();
        this.folder_RecyclerView = (RecyclerView) findViewById(R.id.folder_RecyclerView);
        SwipeRefreshLayout swipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.refresh_folder);
        this.swipeRefreshLayout = swipeRefreshLayout2;
        swipeRefreshLayout2.setOnRefreshListener(this);
        loadFolder();
    }

    private void navigationDrawer() {
        this.navigationView.bringToFront();
        this.navigationView.setNavigationItemSelectedListener(this);
        this.menuIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.drawerLayout.isDrawerVisible((int) GravityCompat.START)) {
                    MainActivity.this.drawerLayout.closeDrawer((int) GravityCompat.START);
                } else {
                    MainActivity.this.drawerLayout.openDrawer((int) GravityCompat.START);
                }
            }
        });
    }

    private void loadFolder() {
        ArrayList<VideoFiles> arrayList;
        videoFiles = getAllVideos(this);
        ArrayList<String> arrayList2 = folderList;
        if (arrayList2 == null || arrayList2.size() <= 0 || (arrayList = videoFiles) == null) {
            Toast.makeText(getApplicationContext(), "Can't find any videos folder", Toast.LENGTH_SHORT).show();
            return;
        }
        FolderAdapter folderAdapter2 = new FolderAdapter(folderList, arrayList, this);
        this.folderAdapter = folderAdapter2;
        this.folder_RecyclerView.setAdapter(folderAdapter2);
        this.folder_RecyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
    }

    public ArrayList<VideoFiles> getAllVideos(Context context) {
        Uri uri;
        String[] projection;
        String duration_formatted;
        ArrayList<VideoFiles> temVideoFiles = new ArrayList<>();
        Uri uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        int i = 0;
        int i2 = 2;
        int i3 = 3;
        int i4 = 4;
        int i5 = 5;
        int i6 = 6;
        String[] projection2 = {"_id", "_data", "title", "_size", "height", "duration", "_display_name", "resolution"};
        Cursor cursor = context.getContentResolver().query(uri2, projection2, (String) null, (String[]) null, "date_added DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(i);
                String path = cursor.getString(1);
                String title = cursor.getString(i2);
                String size = cursor.getString(i3);
                String resolution = cursor.getString(i4);
                int duration = cursor.getInt(i5);
                String disName = cursor.getString(i6);
                String width_height = cursor.getString(7);
                int sec = (duration / 1000) % 60;
                int min = (duration / 60000) % 60;
                int hrs = duration / 3600000;
                if (hrs == 0) {
                    projection = projection2;
                    duration_formatted = String.valueOf(min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)));                    uri = uri2;
                } else {
                    projection = projection2;
                    uri = uri2;
                    duration_formatted = String.valueOf(hrs)
                            .concat(":".concat(String.format(Locale.UK, "%02d", min)
                                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));                }
                VideoFiles videoFiles2 = new VideoFiles(id, path, title, size, resolution, duration_formatted, disName, width_height);
                String subString = path.substring(0, path.lastIndexOf("/"));
                if (!folderList.contains(subString)) {
                    folderList.add(subString);
                }
                temVideoFiles.add(videoFiles2);
                projection2 = projection;
                uri2 = uri;
                i = 0;
                i2 = 2;
                i3 = 3;
                i4 = 4;
                i5 = 5;
                i6 = 6;
            }
            Uri uri3 = uri2;
            cursor.close();
        } else {
            Uri uri4 = uri2;
        }
        return temVideoFiles;
    }

    public void onRefresh() {
        ArrayList<VideoFiles> arrayList;
        ArrayList<String> arrayList2 = folderList;
        if (arrayList2 == null || arrayList2.size() <= 0 || (arrayList = videoFiles) == null) {
            this.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "Can't find any videos folder", 0).show();
            return;
        }
        arrayList.clear();
        loadFolder();
        this.folderAdapter.notifyDataSetChanged();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        videoFiles.clear();
        loadFolder();
    }

    public void onBackPressed() {
        if (this.drawerLayout.isDrawerVisible((int) GravityCompat.START)) {
            this.drawerLayout.closeDrawer((int) GravityCompat.START);
        } else if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", 0).show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                MainActivity.this.doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.drawer_share) {
            return true;
        }
        callingShareMethod();
        this.drawerLayout.closeDrawer((int) GravityCompat.START);
        return true;
    }

    private void callingShareMethod() {
        Toast.makeText(this, "Loading...", 0).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.TEXT", "Download VideoPlayer App :\n https://drive.google.com/u/0/uc?id=1wdT24kXSehh4tDIN_A-bMgUHkIwGoHhs&export=download");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
