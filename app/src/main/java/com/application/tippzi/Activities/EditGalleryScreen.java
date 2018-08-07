package com.application.tippzi.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.RegisterDealModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.until.OnPermssionCallBackListener;
import com.application.tippzi.until.ProviderUtil;
import com.application.tippzi.until.RuntimeUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditGalleryScreen extends AbstractActivity implements View.OnClickListener {

    private ImageView gallery1, gallery2, gallery3, gallery4, gallery5, gallery6;

    //select image from gallery
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_GALLERY = 2;
    private String imgPath ;
    public String mCurrentPhotoPath;
    private Uri selectedImage;
    private Uri outputFileUri;
    private String filename = "" ;
    private Bitmap bitmap;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    private final int CAMERA_REQUEST_CODE = 100;
    private String select_kind ;
    private  int CVersion;
    private static final String TAG_TAKE_PICTURE = "is_taking_picture";
    private static int RESULT_LOAD_IMAGE = 1;
    private int gallery_index = 0;
    private ACProgressFlower dialog;

    private boolean delPhoto1 = false;
    private boolean delPhoto2 = false;
    private boolean delPhoto3 = false;
    private boolean delPhoto4 = false;
    private boolean delPhoto5 = false;
    private boolean delPhoto6 = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile_gallery);

        CVersion = android.os.Build.VERSION.SDK_INT;

        TextView bar_name = findViewById(R.id.tv_business_name_gallery);
        bar_name.setText(GD.temp_bar.business_name);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Registering...")
                .build();

        ImageView back = findViewById(R.id.iv_back_edit_gallery);
        ImageView back_up = findViewById(R.id.iv_back_up_edit_gallery);
        TextView upload = findViewById(R.id.btn_you_are_done);

        back.setOnClickListener(this);
        back_up.setOnClickListener(this);
        upload.setOnClickListener(this);

        gallery1 = findViewById(R.id.iv_gallery1);
        gallery2 = findViewById(R.id.iv_gallery2);
        gallery3 = findViewById(R.id.iv_gallery3);
        gallery4 = findViewById(R.id.iv_gallery4);
        gallery5 = findViewById(R.id.iv_gallery5);
        gallery6 = findViewById(R.id.iv_gallery6);

        gallery1.setOnClickListener(this);
        gallery2.setOnClickListener(this);
        gallery3.setOnClickListener(this);
        gallery4.setOnClickListener(this);
        gallery5.setOnClickListener(this);
        gallery6.setOnClickListener(this);

        gallery1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background1.equals("")) {

                } else {
                    gallery1.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto1 = true;
                }
                return true;
            }
        });

        gallery2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background2.equals("")) {

                } else {
                    gallery2.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto2 = true;
                }
                return false;
            }
        });

        gallery3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background3.equals("")) {

                } else {
                    gallery3.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto3 = true;
                }
                return false;
            }
        });

        gallery4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background4.equals("")) {

                } else {
                    gallery4.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto4 = true;
                }
                return false;
            }
        });

        gallery5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background5.equals("")) {

                } else {
                    gallery5.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto5 = true;
                }
                return false;
            }
        });

        gallery6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (GD.temp_bar.galleryModel.background6.equals("")) {

                } else {
                    gallery6.setImageDrawable(getResources().getDrawable(R.mipmap.ico_close));
                    delPhoto6 = true;
                }
                return false;
            }
        });

        SetImagePrevious();
    }

    private class PicassoTarget implements Target{
        ImageView mView;
        int mIndex;
        public PicassoTarget(ImageView view, int idx){
            mView = view;
            mIndex = idx;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mView.setImageBitmap(bitmap);
            if(mIndex == 1)
                GD.temp_bar.galleryModel.background1 = getStringImage(bitmap);
            if(mIndex == 2)
                GD.temp_bar.galleryModel.background2 = getStringImage(bitmap);
            if(mIndex == 3)
                GD.temp_bar.galleryModel.background3 = getStringImage(bitmap);
            if(mIndex == 4)
                GD.temp_bar.galleryModel.background4 = getStringImage(bitmap);
            if(mIndex == 5)
                GD.temp_bar.galleryModel.background5 = getStringImage(bitmap);
            if(mIndex == 6)
                GD.temp_bar.galleryModel.background6 = getStringImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
    private void SetImagePrevious() {
        if (GD.temp_bar.galleryModel.background1.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background1).into(new PicassoTarget(gallery1, 1));
        }

        if (GD.temp_bar.galleryModel.background2.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background2).into(new PicassoTarget(gallery2, 2));
        }

        if (GD.temp_bar.galleryModel.background3.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background3).into(new PicassoTarget(gallery3, 3));
        }

        if (GD.temp_bar.galleryModel.background4.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background4).into(new PicassoTarget(gallery4, 4));
        }

        if (GD.temp_bar.galleryModel.background5.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background5).into(new PicassoTarget(gallery5, 5));
        }

        if (GD.temp_bar.galleryModel.background6.equals("")) {

        } else {
            Picasso.with(this).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background6).into(new PicassoTarget(gallery6, 6));
        }
    }


    public void onClick(View view){
        int id = view.getId();

        Intent intent;
        switch (id){
            case R.id.iv_back_edit_gallery:
                intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;
            case R.id.iv_back_up_edit_gallery:
                intent = new Intent(getApplicationContext(), EditOpenHourScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;
            case R.id.btn_you_are_done:

                CallEditProfileAPI();

                break;
            case R.id.iv_gallery1:
                if(delPhoto1){
                    GD.temp_bar.galleryModel.background1 = "close";
                    delPhoto1 = false;
                    gallery1.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 1 ;
                SelectTakePictureLoadMethod();
//                CaptureImageFromGallery();
                break;
            case R.id.iv_gallery2:
                if(delPhoto2){
                    GD.temp_bar.galleryModel.background2 = "close";
                    delPhoto2 = false;
                    gallery2.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 2;
                SelectTakePictureLoadMethod();
               // CaptureImageFromGallery();
                break;
            case R.id.iv_gallery3:
                if(delPhoto3){
                    GD.temp_bar.galleryModel.background3 = "close";
                    delPhoto3 = false;
                    gallery3.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 3 ;
                SelectTakePictureLoadMethod();
                //CaptureImageFromGallery();
                break;
            case R.id.iv_gallery4:
                if(delPhoto4){
                    GD.temp_bar.galleryModel.background4 = "close";
                    delPhoto4 = false;
                    gallery4.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 4 ;
                SelectTakePictureLoadMethod();
                //CaptureImageFromGallery();
                break;
            case R.id.iv_gallery5:
                if(delPhoto5){
                    GD.temp_bar.galleryModel.background5 = "close";
                    delPhoto5 = false;
                    gallery5.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 5 ;
                SelectTakePictureLoadMethod();
                //CaptureImageFromGallery();
                break;
            case R.id.iv_gallery6:
                if(delPhoto6){
                    GD.temp_bar.galleryModel.background6 = "close";
                    delPhoto6 = false;
                    gallery6.setImageDrawable(getResources().getDrawable(R.mipmap.ico_camera));
                    return;
                }
                gallery_index = 6 ;
                SelectTakePictureLoadMethod();
               // CaptureImageFromGallery();
                break;
        }
    }

    private PopupWindow selectpicturemethodpup ;
    private void SelectTakePictureLoadMethod(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_select_picture,
                (ViewGroup) findViewById(R.id.layout_root));
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        selectpicturemethodpup = new PopupWindow(layout, width , height, true);
        selectpicturemethodpup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        selectpicturemethodpup.setFocusable(true);

        TextView btn_take_camera = layout.findViewById(R.id.btn_take_camera);
        TextView btn_take_gallery = layout.findViewById(R.id.btn_take_gallery);

        btn_take_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("LLL", Integer.toString(CVersion));
                if (CVersion >= 11 && CVersion <= 23) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        if (!hasPermissions()){
                            select_kind = "camera" ;
                            // your app doesn't have permissions, ask for them.
                            requestNecessaryPermissions();
                        }
                        else {
                            // your app already have permissions allowed.
                            // do what you want.
                            select_kind = "camera" ;
                            dispatchTakePictureIntent();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera not supported", Toast.LENGTH_LONG).show();
                    }
                } else {
                    select_kind = "camera" ;
                    OpenCamera();
                }
                selectpicturemethodpup.dismiss();
            }
        });

        btn_take_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CVersion >= 11 && CVersion <= 23) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        if (!hasPermissions()){
                            select_kind = "gallery" ;
                            // your app doesn't have permissions, ask for them.
                            requestNecessaryPermissions();
                        }
                        else {
                            // your app already have permissions allowed.
                            // do what you want.
                            select_kind = "gallery" ;
                            dispatchTakePictureIntent();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Gallery is not exists.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    OpenGallery();
                }

                selectpicturemethodpup.dismiss();
            }
        });
    }

    private void OpenCamera(){
//        RuntimeUtil.checkPermission(EditGalleryScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, RuntimeUtil.PERMISSION_ALBUM, new OnPermssionCallBackListener() {
//            @Override
//            public void OnGrantPermission() {
//                RuntimeUtil.checkPermission(EditGalleryScreen.this, getWindow().getDecorView(), Manifest.permission.CAMERA, RuntimeUtil.PERMISSION_CAMERA, null, new OnPermssionCallBackListener() {
//                    @Override
//                    public void OnGrantPermission() {
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (intent.resolveActivity(getPackageManager()) != null) {
//                            outputFileUri = ProviderUtil.getOutputMediaFileUri(getApplicationContext());
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//                            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
//                        }
//                    }
//                });
//            }
//        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},MY_REQUEST_CODE);
        }else {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_CODE_STORAGE);
            }else{
                currentImageUri = getImageFileUri();
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                // start the image capture Intent
                startActivityForResult(intentPicture, CAMERA_REQUEST_CODE
                );  // 1 for REQUEST_CAMERA and 2 for REQUEST_CAMERA_ATT
            }
        }
    }
    private final int MY_REQUEST_CODE = 101;
    private final int MY_REQUEST_CODE_STORAGE = 102;
    //private final int REQUEST_CAMERA = 103;
    private Uri currentImageUri;
    private static File imagePath;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_CODE_STORAGE);
                    }else{
                        currentImageUri = getImageFileUri();
                        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                        // start the image capture Intent
                        startActivityForResult(intentPicture, CAMERA_REQUEST_CODE);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Doesn't have permission... ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_REQUEST_CODE_STORAGE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentImageUri = getImageFileUri();
                    Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intentPicture, CAMERA_REQUEST_CODE);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Doesn't have permission...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private Uri getImageFileUri(){
        // Create a storage directory for the images
        // To be safe(er), you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this

        imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyProject");
        if (! imagePath.exists()){
            if (! imagePath.mkdirs()){
                return null;
            }else{
                //create new folder
            }
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = new File(imagePath,"MyProject_"+ timeStamp + ".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();

        if(!image.exists()){
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create an File Uri
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".camera", image);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private void OpenGallery(){
        RuntimeUtil.checkPermission(EditGalleryScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, RuntimeUtil.PERMISSION_ALBUM, new OnPermssionCallBackListener() {
            @Override
            public void OnGrantPermission() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });
    }

    private boolean hasPermissions() {
        int res = 0;
        // list all permissions which you want to check are granted or not.
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                // it return false because your app dosen't have permissions.
                return false;
            }
        }
        // it return true, your app has permissions.
        return true;
    }

    private void requestNecessaryPermissions() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("lpl", ex.getMessage());
            }
            if (select_kind.equals("camera")) {
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            } else if (select_kind.equals("gallery")) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(TAG_TAKE_PICTURE, false);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
            // Continue only if the File was successfully created
        }

    }

    public File createImageFile() throws IOException {
        String folderName = "test";
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        String imagecurrentDateandTime = sdf.format(new Date());
        filename = "IMG_" +  imagecurrentDateandTime + "_receipt";

        File root = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                filename,
                ".jpg",
                root
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( CVersion >= 11 && CVersion <= 23 ) {
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
                try {
                    setPic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                selectedImage = data.getData();

                ContentResolver cr = this.getContentResolver() ;
                String mine = cr.getType(selectedImage);
                String[] filePathColumn = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                final File path1 = Environment.getExternalStorageDirectory();
                path1.getAbsolutePath();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath =  cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Uri urlpath = Uri.fromFile(new File(imgPath));

                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(urlpath));
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (gallery_index == 1 ) {
                    gallery1.setImageBitmap(resizeBitmap(bitmap));
                    GD.temp_bar.galleryModel.background1 = getStringImage(resizeBitmap(bitmap));
                } else if (gallery_index == 2) {
                    gallery2.setImageBitmap(bitmap);
                    GD.temp_bar.galleryModel.background2 = getStringImage(resizeBitmap(bitmap));
                } else if (gallery_index == 3 ) {
                    gallery3.setImageBitmap(bitmap);
                    GD.temp_bar.galleryModel.background3 = getStringImage(resizeBitmap(bitmap));
                } else if (gallery_index == 4 ) {
                    gallery4.setImageBitmap(bitmap);
                    GD.temp_bar.galleryModel.background4 = getStringImage(resizeBitmap(bitmap));
                } else if (gallery_index == 5 ){
                    gallery5.setImageBitmap(bitmap);
                    GD.temp_bar.galleryModel.background5 = getStringImage(resizeBitmap(bitmap));
                } else if (gallery_index == 6 ) {
                    gallery6.setImageBitmap(bitmap);
                    GD.temp_bar.galleryModel.background6 = getStringImage(resizeBitmap(bitmap));
                }


            }
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GALLERY) {
                    outputFileUri = data.getData();
                }

                if (outputFileUri != null) {
                    drawFile();
                }
            }
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                try {
                    setPic();
                } catch (Exception e) {
                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                }
            }
        }
    }


    private void setPic() throws Exception {
        int targetW = 250;
        int targetH = 250;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap selectbitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if (gallery_index == 1 ) {
            gallery1.setImageBitmap(resizeBitmap(selectbitmap));
            GD.temp_bar.galleryModel.background1 = getStringImage(resizeBitmap(selectbitmap));
        } else if (gallery_index == 2) {
            gallery2.setImageBitmap(selectbitmap);
            GD.temp_bar.galleryModel.background2 = getStringImage(resizeBitmap(selectbitmap));
        } else if (gallery_index == 3 ) {
            gallery3.setImageBitmap(selectbitmap);
            GD.temp_bar.galleryModel.background3 = getStringImage(resizeBitmap(selectbitmap));
        } else if (gallery_index == 4 ) {
            gallery4.setImageBitmap(selectbitmap);
            GD.temp_bar.galleryModel.background4 = getStringImage(resizeBitmap(selectbitmap));
        } else if (gallery_index == 5 ){
            gallery5.setImageBitmap(selectbitmap);
            GD.temp_bar.galleryModel.background5 = getStringImage(resizeBitmap(selectbitmap));
        } else if (gallery_index == 6 ) {
            gallery6.setImageBitmap(selectbitmap);
            GD.temp_bar.galleryModel.background6 = getStringImage(resizeBitmap(selectbitmap));
        }
    }

    private void drawFile() {
        Bitmap bitmapImage;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);

            if (gallery_index == 1 ) {
                gallery1.setImageBitmap(resizeBitmap(bitmapImage));
                GD.temp_bar.galleryModel.background1 = getStringImage(resizeBitmap(bitmapImage));
            } else if (gallery_index == 2) {
                gallery2.setImageBitmap(bitmapImage);
                GD.temp_bar.galleryModel.background2 = getStringImage(resizeBitmap(bitmapImage));
            } else if (gallery_index == 3 ) {
                gallery3.setImageBitmap(bitmapImage);
                GD.temp_bar.galleryModel.background3 = getStringImage(resizeBitmap(bitmapImage));
            } else if (gallery_index == 4 ) {
                gallery4.setImageBitmap(bitmapImage);
                GD.temp_bar.galleryModel.background4 = getStringImage(resizeBitmap(bitmapImage));
            } else if (gallery_index == 5 ){
                gallery5.setImageBitmap(bitmapImage);
                GD.temp_bar.galleryModel.background5 = getStringImage(resizeBitmap(bitmapImage));
            } else if (gallery_index == 6 ) {
                gallery6.setImageBitmap(bitmapImage);
                GD.temp_bar.galleryModel.background6 = getStringImage(resizeBitmap(bitmapImage));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "IOException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private Bitmap resizeBitmap(Bitmap origin_bitmap){
        Matrix matrix = new Matrix();
        float scale = 1024/origin_bitmap.getWidth();
        float xTranslation = 0.0f, yTranslation = (720 - origin_bitmap.getHeight() * scale)/2.0f;
        RectF drawableRect = new RectF(0, 0, origin_bitmap.getWidth()-100,
                origin_bitmap.getHeight()-100);

        RectF viewRect = new RectF(0, 0, origin_bitmap.getWidth(),
                origin_bitmap.getHeight());

        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        matrix.setRotate(90, origin_bitmap.getWidth(), origin_bitmap.getHeight());
        matrix.postTranslate(xTranslation, yTranslation);
        matrix.preScale(scale, scale);
        return origin_bitmap ;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void CallEditProfileAPI() {
        String url = GD.BaseUrl + "edit_business_profile.php";

        Gson gson = new Gson();
        String json = gson.toJson(GD.temp_bar);

        new EditProfielWorker().execute(url, json);
    }

    public class EditProfielWorker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data);
            publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            if (!response.isEmpty()) {
                try {
                    JSONObject loginJson = new JSONObject(response);
                    String success = loginJson.getString("success");
                    if (success.equals("true")) {

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = loginJson.getInt("user_id");
                        GD.businessModel.username = loginJson.getString("username");
                        GD.businessModel.email = loginJson.getString("email");
                        GD.businessModel.telephone = loginJson.getString("telephone");
                        JSONArray jsonArray = loginJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.category = jsonObject.getString("category");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
                            barModel.telephone = jsonObject.getString("telephone");
                            barModel.website = jsonObject.getString("website");
                            barModel.email = jsonObject.getString("email");
                            barModel.description = jsonObject.getString("description");
                            barModel.music_type = jsonObject.getString("music_type");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            barModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            barModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            barModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            barModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            barModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            barModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            barModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            barModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            barModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            barModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            barModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            barModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            barModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            barModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            JSONObject jsonObject2 = jsonObject.getJSONObject("gallery");
                            barModel.galleryModel.background1 = jsonObject2.getString("background1");
                            barModel.galleryModel.background2 = jsonObject2.getString("background2");
                            barModel.galleryModel.background3 = jsonObject2.getString("background3");
                            barModel.galleryModel.background4 = jsonObject2.getString("background4");
                            barModel.galleryModel.background5 = jsonObject2.getString("background5");
                            barModel.galleryModel.background6 = jsonObject2.getString("background6");

                            JSONArray jsonArray1 = jsonObject.getJSONArray("deals");
                            for (int j = 0; j < jsonArray1.length(); j ++) {
                                JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                DealModel dealModel = new DealModel();
                                dealModel.deal_id = jsonObject3.getInt("deal_id");
                                dealModel.deal_title = jsonObject3.getString("title");
                                dealModel.deal_description = jsonObject3.getString("description");
                                dealModel.deal_duration =jsonObject3.getString("duration");
                                dealModel.deal_qty = jsonObject3.getInt("qty");
                                dealModel.impressions = jsonObject3.getInt("impressions");
                                dealModel.in_wallet = jsonObject3.getInt("in_wallet");
                                dealModel.claimed = jsonObject3.getInt("claimed");
                                dealModel.engagement = jsonObject3.getInt("engagement");
                                dealModel.clicks = jsonObject3.getInt("clicks");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.businessModel.bars.add(barModel);
                        }
                        dialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), loginJson.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            } else {

            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditOpenHourScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}
