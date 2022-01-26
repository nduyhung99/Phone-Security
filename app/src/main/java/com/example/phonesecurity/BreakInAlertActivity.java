package com.example.phonesecurity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

public class BreakInAlertActivity extends AppCompatActivity {
    private RecyclerView rcvBreakInAlert;
    private ArrayList<ImageSecurity> mListImageSecurity = new ArrayList<ImageSecurity>();
    private ImageSecurityAdapter imageSecurityAdapter;
    private File[] listFile;
    private RelativeLayout layoutSecurityImage;
    private ImageView btnDeleteAlert;
    private PhotoView securityImage;
    private TextView dateAndTimeOfImage1,passwordWrong1, txtNoImage;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_in_alert);
        addControls();

        imageSecurityAdapter.setOnItemClickListener(new ImageSecurityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                rcvBreakInAlert.setVisibility(View.GONE);
                layoutSecurityImage.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                dateAndTimeOfImage1.setText(mListImageSecurity.get(position).getDateAndTime());
                passwordWrong1.setText(mListImageSecurity.get(position).getPasswordWrong());
                Bitmap bitmap = BitmapFactory.decodeFile(mListImageSecurity.get(position).path);
                securityImage.setImageBitmap(FileUltils.modifyOrientation(BreakInAlertActivity.this,bitmap,mListImageSecurity.get(position).path));

                btnDeleteAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAlert(mListImageSecurity.get(position).path);
                    }
                });
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (mListImageSecurity.isEmpty()){
                    Toast.makeText(BreakInAlertActivity.this,getString(R.string.have_no_photo),Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(BreakInAlertActivity.this);
                builder.setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.delete_all_photos))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(getStore(BreakInAlertActivity.this));
                                if (file.isDirectory()){
                                    listFile = file.listFiles();
                                    ArrayList<ImageSecurity> list = new ArrayList<ImageSecurity>();
                                    for (int i=0; i<listFile.length; i++){
                                        String path = listFile[i].getAbsolutePath();
                                        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
                                            File file1 = new File(path);
                                            file1.delete();
                                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file1)));
                                        }
                                    }
                                    txtNoImage.setVisibility(View.VISIBLE);
                                    rcvBreakInAlert.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
            }
        });
    }

    private void deleteAlert(String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BreakInAlertActivity.this);
        builder.setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_photo))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(path);
                        file.delete();
                        imageSecurityAdapter.setData(getData());
                        checkListImage();
                        layoutSecurityImage.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
    }

    private void addControls() {
        rcvBreakInAlert = findViewById(R.id.rcvBreakInAlert);
        layoutSecurityImage = findViewById(R.id.layoutSecurityImage);
        securityImage  = findViewById(R.id.securityImage);
        dateAndTimeOfImage1 = findViewById(R.id.dateAndTimeOfImage1);
        passwordWrong1 = findViewById(R.id.passwordWrong1);
        btnDeleteAlert = findViewById(R.id.btnDeleteAlert);
        view = findViewById(R.id.deleteAll);
        txtNoImage = findViewById(R.id.txtNoImage);
        imageSecurityAdapter = new ImageSecurityAdapter(BreakInAlertActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BreakInAlertActivity.this, RecyclerView.VERTICAL,false);
        rcvBreakInAlert.setLayoutManager(linearLayoutManager);
        imageSecurityAdapter.setData(getData());
        rcvBreakInAlert.setAdapter(imageSecurityAdapter);
        rcvBreakInAlert.setHasFixedSize(true);
        checkListImage();
    }

    private void checkListImage() {
        if (mListImageSecurity.size()!=0){
            txtNoImage.setVisibility(View.GONE);
            rcvBreakInAlert.setVisibility(View.VISIBLE);
        }else {
            txtNoImage.setVisibility(View.VISIBLE);
            rcvBreakInAlert.setVisibility(View.GONE);
        }
    }

    private ArrayList<ImageSecurity> getData() {
        File file = new File(getStore(BreakInAlertActivity.this));
        if (file.isDirectory()){
            listFile = file.listFiles();
            ArrayList<ImageSecurity> list = new ArrayList<ImageSecurity>();
            for (int i=0; i<listFile.length; i++){
                String path = listFile[i].getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
                    String name = listFile[i].getName();
                    String dateAndTime = name.substring(4,23).replaceAll("&","/");
                    String passwordWrong = name.substring(24,name.lastIndexOf("."));
                    list.add(new ImageSecurity(path,dateAndTime,passwordWrong));
                }
            }
            mListImageSecurity = list;
        }
        return mListImageSecurity;
    }

    public static String getStore(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null)
                return f.getAbsolutePath();
            else
                return "/storage/emulated/0/Android/data/" + c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory()
                    + "/Android/data/" + c.getPackageName();
        }
    }

    public void clickBack(View view){
        if (rcvBreakInAlert.getVisibility()==View.GONE){
            rcvBreakInAlert.setVisibility(View.VISIBLE);
            layoutSecurityImage.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (rcvBreakInAlert.getVisibility()==View.GONE && txtNoImage.getVisibility()==View.GONE){
            rcvBreakInAlert.setVisibility(View.VISIBLE);
            layoutSecurityImage.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageSecurityAdapter.setData(getData());
    }
}