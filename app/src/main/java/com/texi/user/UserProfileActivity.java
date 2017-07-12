package com.texi.user;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.squareup.picasso.Picasso;
import com.texi.user.utils.CircleTransform;
import com.texi.user.utils.Common;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.texi.user.utils.Url;
import com.techintegrity.appu.R;

public class UserProfileActivity extends AppCompatActivity {

    TextView txt_profile;
    TextView txt_name;
    EditText edt_name;
    //TextView txt_user_name;
   // EditText edt_user_name;
    TextView txt_user_mobile;
    EditText edt_mobile;
    TextView txt_user_email;
    EditText edt_email;
    RelativeLayout layout_back_arrow;
    ImageView img_add_image;
    //TextView txt_date_of_birth;
    //EditText edt_date_of_birth_val;
   // Spinner spinner_gender;
    RelativeLayout layout_save;
    TextView txt_save;



    Typeface OpenSans_Regular,OpenSans_Bold,Roboto_Regular,Roboto_Medium,Roboto_Bold;
    SharedPreferences userPref;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    Dialog OpenCameraDialog;
    private Uri mCapturedImageURI;
    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;
    File userImage;

   // Calendar myCalendar;
   // DatePickerDialog.OnDateSetListener date;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;
    String genderString;

    boolean isCamera=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        genderString = getResources().getString(R.string.gender);

        getWindow().setBackgroundDrawableResource(R.drawable.background);

        //Error Alert
        rlMainView=(RelativeLayout)findViewById(R.id.rlMainView);
        tvTitle=(TextView)findViewById(R.id.tvTitle);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) getResources().getDimension(R.dimen.height_50), 0, 0);
        rlMainView.setLayoutParams(params);

        txt_profile = (TextView)findViewById(R.id.txt_profile);
        txt_name = (TextView)findViewById(R.id.txt_name);
        edt_name = (EditText)findViewById(R.id.edt_name);
        //txt_user_name = (TextView)findViewById(R.id.txt_user_name);
        //edt_user_name = (EditText)findViewById(R.id.edt_user_name);
        txt_user_mobile = (TextView)findViewById(R.id.txt_user_mobile);
        edt_mobile = (EditText)findViewById(R.id.edt_mobile);
        txt_user_email = (TextView)findViewById(R.id.txt_user_email);
        edt_email = (EditText)findViewById(R.id.edt_email);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);
        img_add_image = (ImageView)findViewById(R.id.img_add_image);
       // txt_date_of_birth = (TextView)findViewById(R.id.txt_date_of_birth);
       // edt_date_of_birth_val = (EditText)findViewById(R.id.edt_date_of_birth_val);
       // spinner_gender = (Spinner)findViewById(R.id.spinner_gender);
        layout_save = (RelativeLayout)findViewById(R.id.layout_save);
        txt_save = (TextView)findViewById(R.id.txt_save);

        ProgressDialog = new Dialog(UserProfileActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        userPref = PreferenceManager.getDefaultSharedPreferences(UserProfileActivity.this);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_profile.setTypeface(OpenSans_Bold);
        txt_save.setTypeface(Roboto_Regular);

        txt_name.setTypeface(Roboto_Regular);
        edt_name.setTypeface(Roboto_Regular);
        //txt_user_name.setTypeface(Roboto_Regular);
        //edt_user_name.setTypeface(Roboto_Regular);
        txt_user_mobile.setTypeface(Roboto_Regular);
        edt_mobile.setTypeface(Roboto_Regular);
        txt_user_email.setTypeface(Roboto_Regular);

        //edt_date_of_birth_val.setTypeface(Roboto_Regular);
        //txt_date_of_birth.setTypeface(Roboto_Regular);

        edt_email.setTypeface(Roboto_Regular);

       // edt_user_name.setText(userPref.getString("username", ""));
        edt_name.setText(userPref.getString("name", ""));
        edt_mobile.setText(userPref.getString("mobile", ""));
        edt_email.setText(userPref.getString("email", ""));
        //edt_date_of_birth_val.setText(userPref.getString("date_of_birth", ""));

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String facebook_id = userPref.getString("facebook_id","");
        Log.d("facebook_id","facebook_id = "+facebook_id);
        if(facebook_id != null && !facebook_id.equals("") && userPref.getString("userImage", "").equals("")) {
            String facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large";
            Log.d("facebookImage","facebookImage = "+facebookImage);
            Picasso.with(UserProfileActivity.this)
                    .load(facebookImage)
                    .placeholder(R.drawable.avatar_placeholder)
                    .resize(200, 200)
                    .transform(new CircleTransform())
                    .into(img_add_image);
        }else {
            Picasso.with(UserProfileActivity.this)
                    .load(Uri.parse(Url.userImageUrl + userPref.getString("userImage", "")))
                    .placeholder(R.drawable.mail_defoult)
                    .transform(new CircleTransform())
                    .into(img_add_image);
        }

        layout_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_name.getText().toString().trim().length() == 0) {
                    Common.showMKPanelError(UserProfileActivity.this, getResources().getString(R.string.please_enter_name),rlMainView,tvTitle,Roboto_Regular);
                    edt_name.requestFocus();
                    return;
                }
//                else if (edt_user_name.getText().toString().trim().length() == 0) {
//                    showMKPanelError(UserProfileActivity.this, getResources().getString(R.string.please_enter_username),rlMainView,tvTitle,Roboto_Regular);
//                    edt_user_name.requestFocus();
//                    return;
//                }
                else if (edt_mobile.getText().toString().trim().length() == 0) {
                    Common.showMKPanelError(UserProfileActivity.this, getResources().getString(R.string.please_enter_mobile),rlMainView,tvTitle,Roboto_Regular);
                    edt_mobile.requestFocus();
                    return;
                } else if (edt_email.getText().toString().trim().length() == 0) {
                    Common.showMKPanelError(UserProfileActivity.this, getResources().getString(R.string.please_enter_email),rlMainView,tvTitle,Roboto_Regular);
                    edt_email.requestFocus();
                    return;
                } else if (edt_email.getText().toString().trim().length() != 0 && !isValidEmail(edt_email.getText().toString().trim())) {
                    Common.showMKPanelError(UserProfileActivity.this, getResources().getString(R.string.please_enter_valid_email),rlMainView,tvTitle,Roboto_Regular);
                    edt_email.requestFocus();
                    return;
                }

                ProgressDialog.show();
                cusRotateLoading.start();

                Log.d("user id","user id = "+userPref.getString("id", ""));
                Builders.Any.B IonObj = Ion.with(UserProfileActivity.this).load(Url.profileUrl).setTimeout(60*60*1000);
                //.setJsonObjectBody(json)
                IonObj.setMultipartParameter("name", edt_name.getText().toString().trim())
                .setMultipartParameter("username", edt_email.getText().toString().trim())
                .setMultipartParameter("email", edt_email.getText().toString().trim())
                .setMultipartParameter("mobile", edt_mobile.getText().toString().trim())
                .setMultipartParameter("uid", userPref.getString("id", ""))
                .setMultipartParameter("dob", "")
                .setMultipartParameter("gender", genderString);
                Log.d("userImage","UserProfile userImage = "+userImage);
                if (userImage != null) {
                    IonObj.setMultipartFile("image", userImage);
                }
                else
                    IonObj.setMultipartParameter("image", "");

                IonObj.setMultipartParameter("isdevice", "1")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("UserProfile result", "UserProfile result = " + result + "==" + error);
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        if (error == null) {
                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("status").equals("success")) {

                                    JSONArray userAry = new JSONArray(resObj.getString("user_detail"));
                                    JSONObject userDetilObj = userAry.getJSONObject(0);

                                    SharedPreferences.Editor id = userPref.edit();
                                    id.putString("id", userDetilObj.getString("id").toString());
                                    id.commit();

                                    SharedPreferences.Editor name = userPref.edit();
                                    name.putString("name", userDetilObj.getString("name").toString());
                                    name.commit();

                                    SharedPreferences.Editor username = userPref.edit();
                                    username.putString("username", userDetilObj.getString("username").toString());
                                    username.commit();

                                    SharedPreferences.Editor mobile = userPref.edit();
                                    mobile.putString("mobile", userDetilObj.getString("mobile").toString());
                                    mobile.commit();

                                    SharedPreferences.Editor email = userPref.edit();
                                    email.putString("email", userDetilObj.getString("email").toString());
                                    email.commit();

                                    SharedPreferences.Editor isLogin = userPref.edit();
                                    isLogin.putString("isLogin", "1");
                                    isLogin.commit();

                                    SharedPreferences.Editor userImage = userPref.edit();
                                    userImage.putString("userImage", userDetilObj.getString("image").toString());
                                    userImage.commit();

                                    SharedPreferences.Editor dob = userPref.edit();
                                    dob.putString("date_of_birth", userDetilObj.getString("dob").toString());
                                    dob.commit();

                                    SharedPreferences.Editor gender = userPref.edit();
                                    gender.putString("gender", genderString);
                                    gender.commit();

                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();

                                    Common.showMkSucess(UserProfileActivity.this, getResources().getString(R.string.profile_update_sucess),"yes");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 2000);

                                } else if (resObj.getString("status").equals("false")) {
                                    Common.user_InActive = 1;
                                    Common.InActive_msg = resObj.getString("message");

                                    SharedPreferences.Editor editor = userPref.edit();
                                    editor.clear();
                                    editor.commit();

                                    Intent logInt = new Intent(UserProfileActivity.this, LoginOptionActivity.class);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(logInt);
                                }else if(resObj.getString("status").equals("failed")){
                                    Common.showMkError(UserProfileActivity.this, resObj.getString("error code"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Common.ShowHttpErrorMessage(UserProfileActivity.this, error.getMessage());
                        }
                    }
                });
            }
        });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenCameraDialog = new Dialog(UserProfileActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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
        });

        List<String> list = new ArrayList<String>();
        list.add(getResources().getString(R.string.gender));
        list.add(getResources().getString(R.string.male));
        list.add(getResources().getString(R.string.female));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.gender_spinner_layout);
        //spinner_gender.setAdapter(dataAdapter);
//        for (int si=0;si<list.size();si++) {
//            if(userPref.getString("gender","").equals(list.get(si))) {
//                spinner_gender.setSelection(si);
//            }
//        }
//        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
//                ((TextView) parent.getChildAt(0)).setTextSize(16);
//                ((TextView) parent.getChildAt(0)).setTypeface(OpenSans_Regular);
//                ((TextView) parent.getChildAt(0)).setGravity(Gravity.RIGHT);
//                genderString = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        //myCalendar = Calendar.getInstance();


//        date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                String myFormat = "yyyy-MM-dd";
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
//
//                int catalog_outdated = 0;
//                try {
//                    Date strDate = sdf.parse(sdf.format(myCalendar.getTime()));
//                    if (System.currentTimeMillis() < strDate.getTime()) {
//                        catalog_outdated = 1;
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Log.d("catalog_outdated","catalog_outdated = "+catalog_outdated);
//                if(catalog_outdated == 0)
//                    edt_date_of_birth_val.setText(sdf.format(myCalendar.getTime()));
//                else {
//                    edt_date_of_birth_val.setText(userPref.getString("date_of_birth", ""));
//                    Common.showMkError(UserProfileActivity.this, getResources().getString(R.string.invalid_birth_date));
//                }
//            }
//        };

//        edt_date_of_birth_val.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//
//                return false;
//            }
//        });

//        edt_date_of_birth_val.setInputType(InputType.TYPE_NULL);
//        edt_date_of_birth_val.requestFocus();

//        edt_date_of_birth_val.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DatePickerDialog dpd = new DatePickerDialog(UserProfileActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
//                Calendar minCal = Calendar.getInstance();
//                minCal.add(Calendar.YEAR, -100);
//                long hundredYearsAgo = minCal.getTimeInMillis();
//                dpd.getDatePicker().setMinDate(hundredYearsAgo);
//                dpd.show();
//            }
//        });

        Common.ValidationGone(UserProfileActivity.this,rlMainView,edt_name);
        //Common.ValidationGone(UserProfileActivity.this,rlMainView,edt_user_name);
        Common.ValidationGone(UserProfileActivity.this,rlMainView,edt_mobile);
        Common.ValidationGone(UserProfileActivity.this,rlMainView,edt_email);
        //Common.ValidationGone(UserProfileActivity.this,rlMainView,edt_date_of_birth_val);

    }

    public Uri getImageUri(){
        File file1 = new File(Environment.getExternalStorageDirectory() + "/Taxiel");
        if (!file1.exists())
        {
            file1.mkdirs();
        }
        File file2 = new File(Environment.getExternalStorageDirectory() + "/Taxiel/UserImage");
        if (!file2.exists())
        {
            file2.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/Taxiel/UserImage/"+System.currentTimeMillis()+".jpg");

        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_profile = null;
       // txt_user_name = null;
       // edt_user_name = null;
        txt_user_mobile = null;
        edt_mobile = null;
        txt_user_email = null;
        edt_email = null;
        layout_save = null;
        txt_save = null;
        layout_back_arrow = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("requestCode = ","requestCode = "+requestCode+"=="+resultCode+"=="+data);
        if(requestCode == REQUEST_CAMERA){
            isCamera=true;
            CreateUserImage(mCapturedImageURI);
        }else if(requestCode == REQUEST_GALLERY){
            if(data != null){
                isCamera=false;
                String selImagePath = getPath(data.getData());
                mCapturedImageURI=Uri.parse(selImagePath);
                CreateUserImage(mCapturedImageURI);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void CreateUserImage(Uri imagePath)
    {
        try{


            if(isCamera)
                userImage=new File(compressImage(imagePath.getPath()));
            else{

                Log.e("Profile","Gallery 1:"+compressImage(imagePath.getPath()));

                String picturePath=compressImage(imagePath.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap thumbnail = BitmapFactory.decodeFile((picturePath), options);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                String myFinalImagePath = saveToInternalSorage(thumbnail, currentDateandTime);



                Log.e("Gallery","Photo = "+myFinalImagePath);
                System.gc();
                userImage=new File(myFinalImagePath);
            }

            Picasso.with(UserProfileActivity.this).load(userImage).transform(new CircleTransform()).into(img_add_image);
        }
        catch(Exception es)
        {	es.printStackTrace();
            System.out.println("==== exceptin in setimage : "+es);
        }
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
    private String saveToInternalSorage(Bitmap bitmapImage,String imageName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("imagePath2","imagePath2 = "+directory);
        // Create imageDir
        File mypath=new File(directory,imageName+".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath()+"/"+imageName+".jpg";
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
