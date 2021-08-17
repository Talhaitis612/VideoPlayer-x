package avideoplayer.project.example.adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import avideoplayer.project.example.R;
import avideoplayer.project.example.VideoFiles;
import avideoplayer.project.example.activity.VideoFolderActivity;
import avideoplayer.project.example.activity.VideoPlayer;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyViewHolder> {
    public static ArrayList<VideoFiles> foderVideoFiles = new ArrayList<>();
    Context mcontext;
    VideoFolderActivity videoFolderActivity;
    View view;
    private ArrayList<VideoFiles> videoFolder;

    public VideoFolderAdapter(Context mcontext2, ArrayList<VideoFiles> foderVideoFiles2) {
        this.mcontext = mcontext2;
        foderVideoFiles = foderVideoFiles2;
        this.videoFolderActivity = (VideoFolderActivity) mcontext2;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(this.mcontext).inflate(R.layout.files_view, parent, false);
        this.view = inflate;
        return new MyViewHolder(inflate, this.videoFolderActivity);
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(foderVideoFiles.get(position).getTitle());
        Glide.with(this.mcontext).load(foderVideoFiles.get(position).getPath()).into(holder.thumbnail);
        TextView textView = holder.resolution;
        textView.setText(foderVideoFiles.get(position).getResolution() + "p");
        holder.duration.setText(foderVideoFiles.get(position).getDuration());
        holder.videoSize.setText(foderVideoFiles.get(position).getSize());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(VideoFolderAdapter.this.mcontext, VideoPlayer.class);
                intent.putExtra("position", position);
                VideoFolderAdapter.this.mcontext.startActivity(intent);
            }
        });
        holder.fileMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(VideoFolderAdapter.this.mcontext, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(VideoFolderAdapter.this.mcontext).inflate(R.layout.fragment_menu, (ViewGroup) null);
                bottomSheetView.findViewById(R.id.file_menu_cancel).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.file_menu_share).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        VideoFolderAdapter.this.shareFiles(position);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.file_menu_rename).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        VideoFolderAdapter.this.renameFiles(position);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.file_menu_delete).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        VideoFolderAdapter.this.deleteFiles(position, v);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.file_menu_properties).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        VideoFolderAdapter.this.callProperties(position);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
        if (this.videoFolderActivity.is_not_in_action_mode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.fileMenu.setVisibility(View.VISIBLE);
            return;
        }
        holder.fileMenu.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(false);
    }

    /* access modifiers changed from: private */
    public void shareFiles(int position) {
        Uri uri = Uri.parse(foderVideoFiles.get(position).getPath());
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("video/*");
        shareIntent.putExtra("android.intent.extra.STREAM", uri);
        this.mcontext.startActivity(Intent.createChooser(shareIntent, "share video via"));
        Toast.makeText(this.mcontext, "Loading...", Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: private */
    public void renameFiles(int position) {
        final Dialog dialog = new Dialog(this.mcontext);
        dialog.setContentView(R.layout.rename_file);
        final EditText editText = (EditText) dialog.findViewById(R.id.rename_edit_text);
        final File renameFile = new File(foderVideoFiles.get(position).getPath());
        String nameText = renameFile.getName();
        editText.setText(nameText.substring(0, nameText.lastIndexOf(".")));
        editText.requestFocus();
        dialog.getWindow().setSoftInputMode(5);
        ((Button) dialog.findViewById(R.id.cancel_rename_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.rename_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String onlyPath = renameFile.getParentFile().getAbsolutePath();
                String ext = renameFile.getAbsolutePath();
                String ext2 = ext.substring(ext.lastIndexOf("."));
                File newFile = new File(onlyPath + "/" + editText.getText() + ext2);
                if (renameFile.renameTo(newFile)) {
                    VideoFolderAdapter.this.mcontext.getApplicationContext().getContentResolver().delete(MediaStore.Files.getContentUri("external"), "_data=?", new String[]{renameFile.getAbsolutePath()});
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(newFile));
                    VideoFolderAdapter.this.mcontext.getApplicationContext().getApplicationContext().sendBroadcast(intent);
                    Toast.makeText(VideoFolderAdapter.this.mcontext, "Rename Success", Toast.LENGTH_SHORT).show();
                    VideoFolderAdapter.this.videoFolderActivity.onRefresh();
                } else {
                    Toast.makeText(VideoFolderAdapter.this.mcontext, "Rename Fail", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void updateSearchList(ArrayList<VideoFiles> searchList) {
        videoFolder = new ArrayList<>();
        videoFolder.addAll(searchList);
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void deleteFiles(final int position, final View v) {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(this.mcontext);
        alertbox.setCancelable(true);
        alertbox.setTitle("Delete");
        alertbox.setMessage(foderVideoFiles.get(position).getTitle()).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertbox.setCancelable(true);
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(VideoFolderAdapter.foderVideoFiles.get(position).getId()));
                if (new File(VideoFolderAdapter.foderVideoFiles.get(position).getPath()).delete()) {
                    VideoFolderAdapter.this.mcontext.getApplicationContext().getContentResolver().delete(contentUri, (String) null, (String[]) null);
                    VideoFolderAdapter.foderVideoFiles.remove(position);
                    VideoFolderAdapter.this.notifyItemRemoved(position);
                    VideoFolderAdapter.this.notifyItemRangeChanged(position, VideoFolderAdapter.foderVideoFiles.size());
                    Snackbar.make(v, (CharSequence) "Delete Success", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(VideoFolderAdapter.this.mcontext, "Delete Fail", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    public void callProperties(int position) {
        Dialog proDialog = new Dialog(this.mcontext);
        proDialog.setContentView(R.layout.file_properties);
        String name = foderVideoFiles.get(position).getTitle();
        String storage = foderVideoFiles.get(position).getPath();
        String size = foderVideoFiles.get(position).getSize();
        String duration = foderVideoFiles.get(position).getDuration();
        String resolution = foderVideoFiles.get(position).getWh();
        ((TextView) proDialog.findViewById(R.id.pro_title)).setText(name);
        ((TextView) proDialog.findViewById(R.id.pro_storage)).setText(storage);
        ((TextView) proDialog.findViewById(R.id.pro_size)).setText(size);
        ((TextView) proDialog.findViewById(R.id.pro_duration)).setText(duration);
        ((TextView) proDialog.findViewById(R.id.pro_resolution)).setText(resolution);
        proDialog.show();
    }

    public int getItemCount() {
        return foderVideoFiles.size();
    }

    public void updateFileList(ArrayList<VideoFiles> fileList) {
        ArrayList<VideoFiles> arrayList = new ArrayList<>();
        foderVideoFiles = arrayList;
        arrayList.addAll(fileList);
        notifyDataSetChanged();
    }

    public void updateDeleteAdapter(ArrayList<VideoFiles> list, int position) {
        Iterator<VideoFiles> it = list.iterator();
        while (it.hasNext()) {
            VideoFiles files = it.next();
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(list.get(position).getId()));
            if (new File(list.get(position).getPath()).delete()) {
                this.mcontext.getContentResolver().delete(contentUri, (String) null, (String[]) null);
                foderVideoFiles.remove(files);
                notifyItemRemoved(position);
                Toast.makeText(this.mcontext, "File deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.mcontext, "Fail to delete", Toast.LENGTH_SHORT).show();
            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBox;
        TextView duration;
        ImageView fileMenu;
        TextView resolution;
        ImageView thumbnail;
        TextView title;
        VideoFolderActivity videoFolderActivity;
        TextView videoSize;

        public MyViewHolder(View itemView, VideoFolderActivity videoFolderActivity2) {
            super(itemView);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            this.fileMenu = (ImageView) itemView.findViewById(R.id._file_menu_more);
            this.title = (TextView) itemView.findViewById(R.id.video_title);
            this.duration = (TextView) itemView.findViewById(R.id.video_duration);
            this.resolution = (TextView) itemView.findViewById(R.id.video_resolution);
            this.videoSize = (TextView) itemView.findViewById(R.id.video_size);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.video_folder_checkbox);
            this.videoFolderActivity = videoFolderActivity2;
            itemView.setOnLongClickListener(videoFolderActivity2);
            this.checkBox.setOnClickListener(this);
        }

        public void onClick(View v) {
            this.videoFolderActivity.prepareSelection(v, getAdapterPosition());
        }
    }
}
