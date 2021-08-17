package avideoplayer.project.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;

import avideoplayer.project.example.R;
import avideoplayer.project.example.VideoFiles;
import avideoplayer.project.example.activity.VideoFolderActivity;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyHolder> {
    /* access modifiers changed from: private */
    public ArrayList<String> folderName;
    /* access modifiers changed from: private */
    public Context mContext;
    public ArrayList<VideoFiles> videoFiles;

    public FolderAdapter(ArrayList<String> folderName2, ArrayList<VideoFiles> videoFiles2, Context mContext2) {
        this.folderName = folderName2;
        this.videoFiles = videoFiles2;
        this.mContext = mContext2;
    }

    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(this.mContext).inflate(R.layout.folder_view, parent, false));
    }

    public void onBindViewHolder(MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.folder.setText(this.folderName.get(position).substring(this.folderName.get(position).lastIndexOf("/") + 1));
        holder.countFolder.setText(String.valueOf(numberOfFiles(this.folderName.get(position))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(FolderAdapter.this.mContext, VideoFolderActivity.class);
                intent.putExtra("folderName", (String) FolderAdapter.this.folderName.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.folderName.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView countFolder;
        TextView folder;

        public MyHolder(View itemView) {
            super(itemView);
            this.folder = (TextView) itemView.findViewById(R.id.folder_Name);
            this.countFolder = (TextView) itemView.findViewById(R.id.count_folder);
        }
    }

    /* access modifiers changed from: package-private */
    public int numberOfFiles(String folderName2) {
        int countFiles = 0;
        Iterator<VideoFiles> it = this.videoFiles.iterator();
        while (it.hasNext()) {
            VideoFiles videoFiles2 = it.next();
            if (videoFiles2.getPath().substring(0, videoFiles2.getPath().lastIndexOf("/")).endsWith(folderName2)) {
                countFiles++;
            }
        }
        return countFiles;
    }
}
