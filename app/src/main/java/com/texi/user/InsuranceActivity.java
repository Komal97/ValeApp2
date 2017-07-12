package com.texi.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.techintegrity.appu.R;
import com.texi.user.utils.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsuranceActivity extends AppCompatActivity {

    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;
    TextView[] rcupload = new TextView[5];
    TextView[] insuranceUpload = new TextView[5];
    Button submit;
    private Uri mCapturedImageURI;
    File userImage;
    Button add_card;
    RelativeLayout layout_menu;
    LinearLayout cardLinearLayout;


    SlidingMenu slidingMenu;
    ImageView[] insuranceImg = new ImageView[5];
    ImageView[] rc_img = new ImageView[5];
    boolean isCamera = false;
    private static String itemSelected;
    Common common = new Common();
    private Dialog OpenCameraDialog;
    private int num;
    private static int pos = 0;
    private View.OnClickListener clickListener;
    private SharedPreferences pref; //to store
    private SharedPreferences.Editor edit;  //update
    private String myFinalImagePath;
    private String[] imagePathsinsuarnce = new String[3];
    private String[] imagePathsRc = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);
        insuranceImg[0] = (ImageView) findViewById(R.id.insurance_img);
        rcupload[0] = (TextView) findViewById(R.id.rc_upload);
        insuranceUpload[0] = (TextView) findViewById(R.id.insurance_txt);
        rc_img[0] = (ImageView) findViewById(R.id.rc_img);
        submit = (Button) findViewById(R.id.submit);
        add_card = (Button) findViewById(R.id.add_card_btn);
        layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);
        cardLinearLayout = (LinearLayout) findViewById(R.id.card_linear_layout);
        pref = getSharedPreferences("mypref", MODE_PRIVATE);
        edit = pref.edit();
        num = 0;


        if (getIntent().getBooleanExtra("main", false)) {
            slidingMenu = new SlidingMenu(this);
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
            slidingMenu.setFadeDegree(0.20f);
            slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
            slidingMenu.setMenu(R.layout.left_menu);


            common.SlideMenuDesign(slidingMenu, InsuranceActivity.this, "insurance");
            layout_menu.setVisibility(View.VISIBLE);

            layout_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slidingMenu.toggle();
                }
            });
            submit.setVisibility(View.GONE);


//            if (pref.getInt("num", 0) != 0) {
//                num=pref.getInt("num",0);
//                for (int i = 0; i <= num; i++)
//                    addCardLayout();
//
//
//                for(int i=0;i<=(pref.getInt("num", 0));i++){
////                    imagePathsinsuarnce[i]=pref.getString("insurance"+i,"null");
////                    imagePathsRc[i]=pref.getString("rc"+i,null);
//
////                    uploadImage(imagePathsinsuarnce[i],i);
////                    uploadImage(imagePathsRc[i],i);
//                }
//                num=0;
//
            //          }
        } else {
            layout_menu.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);

        }


        insuranceUpload[num].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = 0;
                    itemSelected = "insurance";
                openCameraDialog();
            }
        });
        rcupload[num].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = 0;
                openCameraDialog();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InsuranceActivity.this, "Upload Successfully", Toast.LENGTH_LONG);
                // edit.commit();

                Intent intent = new Intent(InsuranceActivity.this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        });

        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCardLayout();
            }
        });


    }

    private void addCardLayout() {
        num++;
        LayoutInflater inflater = getLayoutInflater();
        View card = inflater.inflate(R.layout.insurance_card, null, false);

        insuranceImg[num] = (ImageView) card.findViewById(R.id.insurance_img);
        rcupload[num] = (TextView) card.findViewById(R.id.rc_upload);
        rcupload[num].setId(num);
        insuranceUpload[num] = (TextView) card.findViewById(R.id.insurance_txt);
        insuranceUpload[num].setId(num);
        rc_img[num] = (ImageView) card.findViewById(R.id.rc_img);
        insuranceUpload[num].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pos = view.getId();
                itemSelected = "insurance";
                openCameraDialog();

            }
        });
        rcupload[num].setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                pos = view.getId();
                itemSelected = "rc_upload";
                openCameraDialog();
            }
        });


        cardLinearLayout.addView(card);
        edit.putInt("num", num);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("requestCode = ", "requestCode = " + requestCode + "==" + resultCode + "==" + data);
        if (requestCode == REQUEST_CAMERA) {
            isCamera = true;
            CreateUserImage(mCapturedImageURI);
        } else if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                isCamera = false;
                String selImagePath = data.getData().getPath();
                mCapturedImageURI = Uri.parse(selImagePath);
                CreateUserImage(mCapturedImageURI);

            }
        }

    }

    public void openCameraDialog() {
        OpenCameraDialog = new Dialog(InsuranceActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        OpenCameraDialog.setContentView(R.layout.camera_dialog_layout);

        RelativeLayout layout_open_camera = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_camera);
        layout_open_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraDialog.cancel();
                Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mCapturedImageURI = getImageUri();
                ci.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(ci, REQUEST_CAMERA);
            }
        });

        RelativeLayout layout_open_gallery = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_gallery);
        layout_open_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraDialog.cancel();
                Intent gi = new Intent(Intent.ACTION_PICK);
                gi.setType("image/*");
                startActivityForResult(gi, REQUEST_GALLERY);
            }
        });

        RelativeLayout layout_open_cancel = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_cancel);
        layout_open_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraDialog.cancel();
            }
        });

        OpenCameraDialog.show();
    }

    public Uri getImageUri() {
        File file1 = new File(Environment.getExternalStorageDirectory() + "/Taxiel");
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(Environment.getExternalStorageDirectory() + "/Taxiel/UserImage");
        if (!file2.exists()) {
            file2.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/Taxiel/UserImage/" + System.currentTimeMillis() + ".jpg");

        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void CreateUserImage(Uri imagePath) {
        try {

            if (isCamera)                   //when image is clicked from camera
                userImage = new File(compressImage(imagePath.getPath()));

            else {                         //when image is picked from gallery

                Log.e("Profile", "Gallery 1:" + compressImage(imagePath.getPath()));

                String picturePath = compressImage(imagePath.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap thumbnail = BitmapFactory.decodeFile((picturePath), options);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                myFinalImagePath = saveToInternalSorage(thumbnail, currentDateandTime);


                Log.e("Gallery", "Photo = " + myFinalImagePath);
                System.gc();
                userImage = new File(myFinalImagePath);

            }

            if (itemSelected == "insurance") {
                // edit.putString("insurance" + position, myFinalImagePath);
                Picasso.with(InsuranceActivity.this).load(userImage).fit().into(insuranceImg[pos]);
                insuranceUpload[pos].setText("Insurance");
            } else {
                //edit.putString("rc" + position, myFinalImagePath);
                Picasso.with(InsuranceActivity.this).load(userImage).fit().into(rc_img[pos]);
                rcupload[pos].setText("RC");
            }


        } catch (Exception es) {
            es.printStackTrace();
            System.out.println("==== exceptin in setimage : " + es);
        }
    }

    public void uploadImage(String path, int position) {
        userImage = new File(path);
        if (itemSelected == "insurance") {
            Picasso.with(InsuranceActivity.this).load(userImage).fit().into(insuranceImg[position]);
            insuranceUpload[pos].setText("Insurance");
        } else {

            Picasso.with(InsuranceActivity.this).load(userImage).fit().into(rc_img[position]);
            rcupload[pos].setText("RC");
        }
        edit.commit();
    }


    private String saveToInternalSorage(Bitmap bitmapImage, String imageName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("imagePath2", "imagePath2 = " + directory);
        // Create imageDir
        File mypath = new File(directory, imageName + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath() + "/" + imageName + ".jpg";
    }


    //New Gallery
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 512.0f;
        float maxWidth = 512.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    @Override
    public void onBackPressed() {

        if (slidingMenu != null && slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.really_exit))
                    .setMessage(getResources().getString(R.string.are_you_sure))
                    .setNegativeButton(getResources().getString(R.string.cancel), null)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {

//                            HomeActivity.super.onBackPressed();
                            finish();
                        }
                    }).create().show();
        }

    }


}
