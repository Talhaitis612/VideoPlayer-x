package avideoplayer.project.example.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import avideoplayer.project.example.R;
import avideoplayer.project.example.VideoFiles;
import avideoplayer.project.example.adapter.VideoFolderAdapter;

public class VideoFolderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, View.OnLongClickListener {
    private final String MY_SORT_PREF = "SortOrder";
    int counter = 0;
    TextView counterText;
    public boolean is_not_in_action_mode = true;
    String myFolderName;
    int position;
    RecyclerView recyclerView;
    ArrayList<VideoFiles> selection_ArrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();
    VideoFolderAdapter videoFolderAdapter;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_folder);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        this.counterText = (TextView) findViewById(R.id.counter_text_view);
        this.recyclerView = (RecyclerView) findViewById(R.id.video_folder_RecyclerView);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_file);
        this.myFolderName = getIntent().getStringExtra("folderName");
        loadVideos();
        this.swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loadVideos() {
        ArrayList<VideoFiles> allVideos = getallVideoFromFolder(this,myFolderName);
        this.videoFilesArrayList = allVideos;
        if (this.myFolderName != null && allVideos.size() > 0) {
            this.videoFolderAdapter = new VideoFolderAdapter(this, this.videoFilesArrayList);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setDrawingCacheEnabled(true);
            this.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            this.recyclerView.setAdapter(this.videoFolderAdapter);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            this.recyclerView.setHasFixedSize(true);
        }
    }

    private ArrayList<VideoFiles> getallVideoFromFolder(Context context, String name) {
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        //which one you want to set default in sorting
        // i am setting by date
        String sort = preferences.getString("sorting", "sortByDate");
        String order = null;
        switch (sort) {

            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "sortBySize":
                order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;

        }
        ArrayList<VideoFiles>list = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String resolution = cursor.getString(4);
                int duration = cursor.getInt(5);
                String disName = cursor.getString(6);
                String bucket_display_name = cursor.getString(7);
                String width_height = cursor.getString(8);

                //this method convert 1204 in 1MB
                String human_can_read = null;
                if (size < 1024) {
                    human_can_read = String.format(context.getString(R.string.size_in_b), (double) size);
                } else if (size < Math.pow(1024, 2)) {
                    human_can_read = String.format(context.getString(R.string.size_in_kb), (double) (size / 1024));
                } else if (size < Math.pow(1024, 3)) {
                    human_can_read = String.format(context.getString(R.string.size_in_mb), size / Math.pow(1024, 2));
                } else {
                    human_can_read = String.format(context.getString(R.string.size_in_gb), size / Math.pow(1024, 3));
                }

                //this method convert any random video duration like 1331533132 into 1:21:12
                String duration_formatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = duration / (1000 * 60 * 60);

                if (hrs == 0) {
                    duration_formatted = String.valueOf(min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                } else {
                    duration_formatted = String.valueOf(hrs)
                            .concat(":".concat(String.format(Locale.UK, "%02d", min)
                                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                }


                VideoFiles files = new VideoFiles(id, path, title,
                        human_can_read, resolution, duration_formatted,
                        disName, width_height);
                if (name.endsWith(bucket_display_name))
                    list.add(files);

            }
            cursor.close();
        }

        return list;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_more, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_file).getActionView();
        ImageView ivClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
        searchView.setQueryHint("Search file name");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<VideoFiles> searchFiles = new ArrayList<>();
        Iterator<VideoFiles> it = this.videoFilesArrayList.iterator();
        while (it.hasNext()) {
            VideoFiles files = it.next();
            if (files.getTitle().toLowerCase().contains(userInput)) {
                searchFiles.add(files);
            }
        }
        this.videoFolderAdapter.updateFileList(searchFiles);
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences("SortOrder", 0).edit();
        int itemId = item.getItemId();
        if (itemId != 16908332) {
            if (itemId != R.id.delete_selected) {
                switch (itemId) {
                    case R.id.sort_by_date /*2131231088*/:
                        editor.putString("sorting", "sortByDate");
                        editor.apply();
                        recreate();
                        break;
                    case R.id.sort_by_name /*2131231089*/:
                        editor.putString("sorting", "sortByName");
                        editor.apply();
                        recreate();
                        break;
                    case R.id.sort_by_size /*2131231090*/:
                        editor.putString("sorting", "sortBySize");
                        editor.apply();
                        recreate();
                        break;
                }
            } else {
                this.videoFolderAdapter.updateDeleteAdapter(this.selection_ArrayList, this.position);
                clearActionMode();
            }
        } else if (this.is_not_in_action_mode) {
            onBackPressed();
        } else {
            clearActionMode();
            this.videoFolderAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearActionMode() {
        this.is_not_in_action_mode = true;
        this.toolbar.getMenu().clear();
        this.toolbar.inflateMenu(R.menu.search_more);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        this.counterText.setVisibility(View.GONE);
        this.counterText.setText("0 item Selected");
        this.counter = 0;
        this.selection_ArrayList.clear();
    }

    public void onRefresh() {
        clearActionMode();
        if (this.myFolderName == null || this.videoFilesArrayList.size() <= 0) {
            this.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Folder is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        this.videoFilesArrayList.clear();
        loadVideos();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    public boolean onLongClick(View v) {
        this.toolbar.getMenu().clear();
        this.toolbar.inflateMenu(R.menu.contextual_menu);
        this.counterText.setVisibility(View.VISIBLE);
        this.is_not_in_action_mode = false;
        this.videoFolderAdapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        return true;
    }

    public void prepareSelection(View view, int position2) {
        if (((CheckBox) view).isChecked()) {
            this.selection_ArrayList.add(this.videoFilesArrayList.get(position2));
            int i = this.counter + 1;
            this.counter = i;
            updateCounter(i);
            return;
        }
        this.selection_ArrayList.remove(this.videoFilesArrayList.get(position2));
        int i2 = this.counter - 1;
        this.counter = i2;
        updateCounter(i2);
    }

    public void updateCounter(int counter2) {
        if (counter2 == 0) {
            this.counterText.setText("0 item Selected");
            return;
        }
        TextView textView = this.counterText;
        textView.setText(counter2 + " item Selected");
    }

    public void onBackPressed() {
        if (!this.is_not_in_action_mode) {
            clearActionMode();
            this.videoFolderAdapter.notifyDataSetChanged();
            return;
        }
        super.onBackPressed();
    }
}
