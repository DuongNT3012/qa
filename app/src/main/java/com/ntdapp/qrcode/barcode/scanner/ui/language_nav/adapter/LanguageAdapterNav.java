package com.ntdapp.qrcode.barcode.scanner.ui.language_nav.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ntdapp.qrcode.barcode.scanner.ui.language_nav.LanguageModelNav;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.ui.language_nav.IClickLanguageNav;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LanguageAdapterNav extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<LanguageModelNav> lists;
    private IClickLanguageNav iClickLanguage;

    public LanguageAdapterNav(Context context, List<LanguageModelNav> lists, IClickLanguageNav iClickLanguage) {
        this.context = context;
        this.lists = lists;
        this.iClickLanguage = iClickLanguage;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_language, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        LanguageModelNav data = lists.get(position);
        if (holder instanceof LanguageViewHolder) {
            ((LanguageViewHolder) holder).bind(data);
            ((LanguageViewHolder) holder).relayEnglish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickLanguage.onClick(data);
                }
            });

            if (data.isCheck()) {
                //((LanguageViewHolder) holder).relayEnglish.setBackgroundResource(R.drawable.border_item_language_select);
                ((LanguageViewHolder) holder).tvTitle.setTextColor(context.getColor(R.color.black));
                //((LanguageViewHolder) holder).rbBtn.setChecked(true);
                ((LanguageViewHolder) holder).img_selected.setVisibility(View.VISIBLE);
            } else {
                //((LanguageViewHolder) holder).relayEnglish.setBackgroundResource(R.drawable.border_item_language);
                ((LanguageViewHolder) holder).tvTitle.setTextColor(context.getColor(R.color.black));
                //((LanguageViewHolder) holder).rbBtn.setChecked(false);
                ((LanguageViewHolder) holder).img_selected.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        ImageView img_selected;
        TextView tvTitle;
        //RadioButton rbBtn;
        CardView relayEnglish;

        public LanguageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img_selected = itemView.findViewById(R.id.img_selected);
            tvTitle = itemView.findViewById(R.id.tv_title);
            //rbBtn = itemView.findViewById(R.id.rb_language);
            relayEnglish = itemView.findViewById(R.id.relay_english);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bind(LanguageModelNav data) {
            //ivAvatar.setImageDrawable(context.getDrawable(data.getImage()));
            tvTitle.setText(data.getLanguageName());
            //rbBtn.setChecked(data.isCheck());
        }
    }

    public void setSelectLanguage(LanguageModelNav model) {
        for (LanguageModelNav data : lists) {
            if (data.getLanguageName().equals(model.getLanguageName())) {
                data.setCheck(true);
            } else {
                data.setCheck(false);
            }
        }
        notifyDataSetChanged();
    }
}
