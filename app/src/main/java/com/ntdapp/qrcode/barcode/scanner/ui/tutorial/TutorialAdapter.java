package com.ntdapp.qrcode.barcode.scanner.ui.tutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ntdapp.qrcode.barcode.scanner.R;

import java.util.ArrayList;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialAdapterHolder> {
    private ArrayList<HelpGuidModel> mHelpGuid;
    private Context context;

    public TutorialAdapter(ArrayList<HelpGuidModel> mHelpGuid, Context context){
        this.mHelpGuid = mHelpGuid;
        this.context = context;
    }

    @Override
    public TutorialAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_guide, parent, false);
        return new TutorialAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(TutorialAdapterHolder holder, int position) {
        HelpGuidModel helpGuidModel = mHelpGuid.get(position);
        if (helpGuidModel == null) {
            return;
        }
        Glide.with(context).load(helpGuidModel.getImg()).into(holder.img_guide);
        //holder.img_guide.setImageResource(helpGuidModel.getImg());
        holder.tv_content.setText(helpGuidModel.getContent());
    }

    @Override
    public int getItemCount() {
        if (mHelpGuid != null) {
            return mHelpGuid.size();
        } else {
            return 0;
        }
    }

    public class TutorialAdapterHolder extends RecyclerView.ViewHolder {
        private ImageView img_guide;
        private TextView tv_content;

        public TutorialAdapterHolder(View itemView) {
            super(itemView);
            img_guide = itemView.findViewById(R.id.img_guide);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}

