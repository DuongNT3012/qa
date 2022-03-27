package com.qrcode.barcode.barcodescanner.qrcodereader;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;


public class RatingDialog extends Dialog {
    private OnPress onPress;
    private TextView tvTitle,tvContent;
    private RatingBar rtb;
    private ImageView imgIcon,imageView;
    private EditText editFeedback;
    private Context context;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private String KEY_CHECK_OPEN_APP = "KEY CHECK OPEN APP";
    private Button btnRate,Send,Cancel,btnLater;
    public RatingDialog(Context context2) {
        super(context2, R.style.CustomAlertDialog);
        this.context = context2;
        setContentView(R.layout.dialog_rating_app);
        // ButterKnife.bind(this);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(attributes);
        getWindow().setSoftInputMode(16);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvContent = (TextView) findViewById(R.id.tvContent);
        rtb = (RatingBar)findViewById(R.id.rtb);
        imgIcon=(ImageView)findViewById(R.id.imgIcon);
        imageView=(ImageView)findViewById(R.id.imageView);
        editFeedback=(EditText)findViewById(R.id.editFeedback);
        btnRate=(Button)findViewById(R.id.btnRate);
        //Send=(Button)findViewById(R.id.Send);
        //Cancel=(Button)findViewById(R.id.Cancel);
        btnLater=(Button)findViewById(R.id.btnLater);
        onclick();
        changeRating();

    }
    public interface OnPress {
        void send();
        void rating();
        void cancel();
        void later();
    }
    public void init(Context context, RatingDialog.OnPress onPress) {
        this.onPress = onPress;
    }
    public void changeRating() {
        rtb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String getRating = String.valueOf(rtb.getRating());
                switch (getRating){
                    case "1.0":
                        editFeedback.setVisibility(View.VISIBLE);
                        btnRate.setText("Send");
                        imgIcon.setImageResource(R.drawable.rating_1);
                        tvTitle.setText("Oh, no!");
                        /* tvTitle.setTypeface(null, Typeface.BOLD);*/
                        tvContent.setText("Please leave us some feedback");
                        break;
                    case "2.0":
                        editFeedback.setVisibility(View.VISIBLE);
                        btnRate.setText("Send");
                        imgIcon.setImageResource(R.drawable.rating_2);
                        tvTitle.setText("Oh, no!");
                        /* tvTitle.setTypeface(null, Typeface.BOLD);*/
                        tvContent.setText("Please leave us some feedback");
                        break;
                    case "3.0":
                        editFeedback.setVisibility(View.VISIBLE);
                        btnRate.setText("Send");
                        imgIcon.setImageResource(R.drawable.rating_3);
                        tvTitle.setText("Oh, no!");
                        /*tvTitle.setTypeface(null, Typeface.BOLD);*/
                        tvContent.setText("Please leave us some feedback");
                        break;
                    case "4.0":
                        editFeedback.setVisibility(View.GONE);
                        btnRate.setText("Rate");
                        imgIcon.setImageResource(R.drawable.rating_4);
                        tvTitle.setText("We like you too!");
                        /*  tvTitle.setTypeface(null, Typeface.BOLD);*/
                        tvContent.setText("Thank for your feedback.");
                        break;
                    case "5.0":
                        editFeedback.setVisibility(View.GONE);
                        btnRate.setText("Rate");
                        imgIcon.setImageResource(R.drawable.rating_5);
                        tvTitle.setText("We like you too!");
                        /* tvTitle.setTypeface(null, Typeface.BOLD);*/
                        tvContent.setText("Thank for your feedback.");
                        break;
                    default:
                        btnRate.setText("Rate");
                        editFeedback.setVisibility(View.GONE);
                        imgIcon.setImageResource(R.drawable.rating_0);
                        tvTitle.setText("We are working hard for a better user eperience.");
                        tvContent.setText("We’d greatly appreciate if you can rate us .");
                        break;
                }
            }
        });


    }
    public String getNewName() {
        return this.editFeedback.getText().toString();
    }
    public String getRating(){
        return  String.valueOf(this.rtb.getRating());
    }

    public void onclick() {
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rtb.getRating()==0){
                    Toast.makeText(context,"Please feedback",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(rtb.getRating()<=3.0){
                    imageView.setVisibility(View.GONE);
                    imgIcon.setVisibility(View.GONE);
                    onPress.send();
                }else{
                    imageView.setVisibility(View.VISIBLE);
                    imgIcon.setVisibility(View.VISIBLE);
                    onPress.rating();
                }
            }
        });
     /*   Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPress.send();
            }
        });*/
       /* Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPress.cancel();
            }
        });*/
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPress.later();
            }
        });

    }

}
