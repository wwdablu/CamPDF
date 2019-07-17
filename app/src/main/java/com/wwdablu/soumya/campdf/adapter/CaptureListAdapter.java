package com.wwdablu.soumya.campdf.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.manager.StorageManager;

import java.util.LinkedList;

public class CaptureListAdapter extends RecyclerView.Adapter<CaptureListAdapter.CaptureSessionViewHolder> {

    public interface ActionCallback {
        void onDelete(StorageManager.EntryInfo entryInfo, int position);
    }

    private LinkedList<StorageManager.EntryInfo> mEntryInfoList;
    private ActionCallback mCallback;

    public CaptureListAdapter(@NonNull LinkedList<StorageManager.EntryInfo> list,
                              @NonNull ActionCallback callback) {
        mEntryInfoList = list;
        mCallback = callback;
    }

    public void setData(@NonNull LinkedList<StorageManager.EntryInfo> list) {
        mEntryInfoList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CaptureSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_entry_info, parent, false);
        return new CaptureSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaptureSessionViewHolder holder, int position) {
        holder.bind(mEntryInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mEntryInfoList == null ? 0 : mEntryInfoList.size();
    }

    class CaptureSessionViewHolder extends RecyclerView.ViewHolder {

        private TextView mFileNameTextView;
        private CardView mCardView;
        private ImageView mDelete;
        private ImageView mShowFolder;

        CaptureSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            mFileNameTextView = itemView.findViewById(R.id.tv_file_name);
            mCardView = itemView.findViewById(R.id.cv_container);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mShowFolder = itemView.findViewById(R.id.iv_share);

            mDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                mCallback.onDelete(mEntryInfoList.get(position), position);
            });

            mShowFolder.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(mEntryInfoList.get(getAdapterPosition()).getCaptureDirectory().getPath());
                intent.setDataAndType(uri, "text/csv");
                mShowFolder.getContext().startActivity(Intent.createChooser(intent, "Open folder"));
            });

            mCardView.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                StorageManager.EntryInfo entryInfo = mEntryInfoList.get(getAdapterPosition());
                String auth = view.getContext().getPackageName() + ".fileprovider";
                intent.setDataAndType(FileProvider.getUriForFile(view.getContext(), auth, entryInfo.getPdfFile()), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                view.getContext().startActivity(intent);
            });
        }

        void bind(StorageManager.EntryInfo entryInfo) {
            mFileNameTextView.setText(entryInfo.getFileName().substring(0, entryInfo.getFileName()
                    .lastIndexOf(".")));
        }
    }
}
