package com.example.mobile4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobile4.databinding.ActivityMainBinding;
import com.example.mobile4.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
   // nó là một trường tĩnh riêng bên trong lớp MainActivity với kiểu int và giá trị 10.
    public static final int CAMERA = 10;
    Uri imageUri;
    OutputStream outputStream;
     ImageView imageView;
    private String[] imageList;
    private  int index = 0;
    // camera
    private ActivityMainBinding binding;
    Handler mainHandler = new Handler();
    Button button, capture_img, saveBtn;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageView = findViewById(R.id.imageView);
        saveBtn = findViewById(R.id.saveBtn);
        imageList = getResources().getStringArray(R.array.images);
        button = findViewById(R.id.dimg);
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imagePath = createImage();
                // the captured image will be written to that path and no data will be provided to onActivityResult.
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(intent, CAMERA);
            }
        });

        loadImage();




        String img = "https://prep.vn/blog/wp-content/uploads/2022/05/bai-mau-ielts-writing-task-1-line-graph-2.jpg";
        Picasso.get().load(img).into(imageView);

        binding.clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etURL.setText("");
                binding.imageView.setImageBitmap(null);
            }
        });
    }

    // save in storage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA) {
            if(resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show();
                imageView.setImageURI(imageUri);
            }
        }
    }
    private Uri createImage() {
        Uri uri = null;
        ContentResolver resolver = getContentResolver();
        // Open app setting
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String imgName = String.valueOf(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgName+".jpg" );
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/"+"My Image");
        Uri finalUri = resolver.insert(uri, contentValues);
        imageUri = finalUri;
        return finalUri;
    }


    // Glide img
    private void loadImage() {
        Glide.with(MainActivity.this)
                .load(imageList[index])
                .centerCrop()
                .into(imageView);
    }

    public void nextImage(View view) {
        index++;
        if(index >= imageList.length)
            index = 0;
        loadImage();
    }

    public void previousImage(View view) {
        index--;
        if(index <= -1)
            index = imageList.length - 1;
        loadImage();
    }


    class FetchImage extends Thread{
        String URL;
        Bitmap bitmap;
        FetchImage(String URL) {
            this.URL = URL;

        }
        public  void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Getting your pic...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            InputStream inputStream = null;
            try {
                inputStream = new java.net.URL(URL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    binding.imageView.setImageBitmap(bitmap);
                }
            });



        }
    }


}