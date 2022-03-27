package com.qrcode.barcode.barcodescanner.qrcodereader.ui.feedback;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.ActivityFeedbackBinding;

public class FeedbackActivity extends AppCompatActivity {
    private ActivityFeedbackBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        setBack();
    }

    private void setBack() {
        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void clickButtonFeedback(View view) {
        showDialog();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edt_title = (EditText) dialog.findViewById(R.id.edt_title);
        EditText edt_content = (EditText) dialog.findViewById(R.id.edt_content);
        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText = "mailto:" + mail() +
                        "?subject=" + "feedback Qr code" +
                        "&body=" + "Title : " + edt_title.getText() + "\nContent: " + edt_content.getText();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(FeedbackActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private String mail(){
        String mailTest = "hoanganh710pt@gmail.com";
        String mailThat = "anhnt.android@gmail.com";
        return mailThat;
    }
}