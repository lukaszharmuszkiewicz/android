package com.example.aplikacja;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import com.squareup.picasso.Picasso;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;
import java.util.zip.CheckedOutputStream;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, RamkaDialog.RamkaDialogListener
        , ErozjaDialog.ErozjaDialogListener, RogiDialog.RogiDialogListener, MemDialog.ExampleDialogListener {


    ImageView imageView;
    private int GALLERY_REQUEST = 100;
    private int CAMERA_REQUEST = 200;
    MenuItem idk;
    MenuItem addImage;
    MenuItem saturation;
    MenuItem border;
    MenuItem erozja;
    MenuItem applyRoundCornerEffect;
    ImageFilters imageFilters;
    Uri imageUri;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    private EditText level;
    SeekBar seekBar;
    String s1, s2, s3, s4;
    Bitmap bitmap;
    Bitmap prev;
    Bitmap curr;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageFilters = new ImageFilters();
        addImage = (MenuItem) findViewById(R.id.add_image);
        shareDialog = new ShareDialog(this);
        seekBar = findViewById(R.id.seekBar1);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setVisibility(View.GONE);
            }
        });

    printHeyHash();
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            editText(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    });
        //int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);




    }

    private void printHeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.aplikacja", PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures){
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        idk = menu.findItem(R.id.idk);
//        idk.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openDialog();
//                return true;
//            }
//        });
//
//        saturation = menu.findItem(R.id.saturation);
//        saturation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openDialog();
//                return true;
//            }
//        });
//        border = menu.findItem(R.id.border);
//        border.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openBorderDialog();
//                return true;
//            }
//        });
//        erozja = menu.findItem(R.id.rozja);
//        erozja.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openErozjaDialog();
//                return false;
//            }
//        });
//        applyRoundCornerEffect = menu.findItem(R.id.rogi);
//        applyRoundCornerEffect.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openRogiDialog();
//                return false;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.filtry) {
            if(!(imageView.getDrawable()==null)) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz filtr");

// add a list
                String[] animals = {"sepia", "czarno-białe", "rozmycie Gaussa", "śnieg", "nasycenie", "rozjaśnienie", "grawer"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.toSephia(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 1:

                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.toGrayscale(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
//                                ColorMatrix matrix = new ColorMatrix();
//                                matrix.setSaturation(0);
//
//                                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//                                imageView.setColorFilter(filter);
                                break;
                            case 2:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.applyGaussianBlurEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 3:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.applySnowEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 4:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                openDialog();
                                break;
                            case 5:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                openErozjaDialog();
                                break;
                            case 6:
                                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.applyEngraveEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;

                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            return true;
        }

        if (id == R.id.delete) {
            if (!(imageView.getDrawable()==null)) {
                imageView.setImageBitmap(null);
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            return true;
        }

        if (id == R.id.obrót) {
            if(!(imageView.getDrawable()==null)) {

                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz obrót");
                String[] animals = {"90 stopni w prawo", "90 stopni w lewo"};
                final Bitmap img = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Matrix matrix = new Matrix();
                                matrix.setRotate(90);
                               Bitmap bitmap = Bitmap.createBitmap(img, 0,0,img.getWidth(),img.getHeight(),matrix,true);
                                imageView.setImageBitmap(bitmap);
                                break;
                            case 1:
                                Matrix matrix1 = new Matrix();
                                matrix1.setRotate(-90);
                                Bitmap bitmap1 = Bitmap.createBitmap(img, 0,0,img.getWidth(),img.getHeight(),matrix1,true);
                                imageView.setImageBitmap(bitmap1);
                                break;


                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }



        if(id == R.id.dodajElementy) {
            if(!(imageView.getDrawable() == null)) {

                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz element");
                String[] animals = {"Śnieg", "Ramka", "Zaokrąglone rogi"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                imageView.setImageBitmap(imageFilters.applySnowEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 1:
                                openBorderDialog();
                                break;
                            case 2:
                                openRogiDialog();
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }


//        if( id == R.id.facebook){
//            byte[] data = null;
//
//            Bitmap bi = BitmapFactory.decodeFile("/sdcard/viewitems.png");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            data = baos.toByteArray();
//
//
//            Bundle params = new Bundle();
//            params.putString(Facebook.TOKEN, facebook.getAccessToken());
//            params.putString("method", "photos.upload");
//            params.putByteArray("picture", data);
//
//            AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
//            mAsyncRunner.request(null, params, "POST", new SampleUploadListener(), null);
//
//        }

        if( id == R.id.instagram){



if(!(imageView.getDrawable()==null)) {


    Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    File path = Environment.getExternalStorageDirectory();
    File dir = new File(path + "/DCIM");
    dir.mkdirs();
    String name = (new Date()).toString() + ".png";
    File file = new File(dir, name);


    OutputStream out;

    try {
        out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    if (intent != null) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.instagram.android");
        try {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), path + "/DCIM/" + name, name, "Share happy !")));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        shareIntent.setType("image/jpeg");

        startActivity(shareIntent);
    } else {
        // bring user to the market to download the app.
        // or let them choose an app?
        intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
        startActivity(intent);
    }
}  else {
    View parentLayout = findViewById(android.R.id.content);
    Snackbar snackbar = Snackbar
            .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
    snackbar.show();
}
return true;
        }



        if(id == R.id.facebook){

            if(!(imageView.getDrawable()==null)){



           SharePhoto sharePhoto = new SharePhoto.Builder()
                   .setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap())
                   .build();

           SharePhotoContent photoContent = new SharePhotoContent.Builder()
                   .addPhoto(sharePhoto)
                   .build();
           if(shareDialog.canShow(SharePhotoContent.class)){
               shareDialog.show(photoContent);
           }

        }  else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }


        if(id == R.id.undo){
            undo();
            return true;
        }

        if (id == R.id.add_image) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Dodaj zdjęcie");
            String[] animals = {"Galeria", "Aparat"};
            builder.setItems(animals, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Select"), GALLERY_REQUEST);
                            break;
                        case 1:



                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                             imageUri = getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent1, CAMERA_REQUEST);


                            //Intent intent11 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //startActivityForResult(intent1,CAMERA_REQUEST);
                            break;

                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent,"Select"), GALLERY_REQUEST);
            return true;
        }

        if (id == R.id.save) {
            if (!(imageView.getDrawable() == null)) {


                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //File path = Environment.getExternalStorageDirectory();
                File dir = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/");
                if(!dir.exists())
                dir.mkdirs();
                String name = (new Date()).toString() + ".jpg";
                File file = new File(dir, name);


                OutputStream out;

                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }


        if(id == R.id.text){
            if(!(imageView.getDrawable()==null)){
                prev = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                openMemDialog();
            }  else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
//        if(id == R.id.zrobZdjecie) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent,CAMERA_REQUEST);
//        }



        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try{
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
                byte[] bytes = outputStream.toByteArray();
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap1);
                Log.d("width" + bitmap.getWidth() , "heigth" + bitmap.getHeight() + " ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


    public void openBorderDialog() {
        RamkaDialog exampleDialog = new RamkaDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void openRogiDialog() {
        RogiDialog exampleDialog = new RogiDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void openErozjaDialog() {
        ErozjaDialog exampleDialog = new ErozjaDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


    public void openMemDialog(){
        MemDialog memDialog = new MemDialog();
        memDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyTexts(String level) {
        System.out.println(level);

    }


    public void saturation(String s){
        imageView.setImageBitmap(imageFilters.applySaturationFilter(((BitmapDrawable)imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void ramka(String s) {
        imageView.setImageBitmap(imageFilters.addBorder(((BitmapDrawable)imageView.getDrawable()).getBitmap(),Integer.valueOf(s)));

    }

    public void erozja(String s){
        imageView.setImageBitmap(imageFilters.applyBrightnessEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap(),Integer.valueOf(s)));

    }

    public void rogi(String s) {
        imageView.setImageBitmap(imageFilters.applyRoundCornerEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void addText(String s1, String s2, String s3, String s4){
        writeTextOnDrawableUpUp(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s1);
        writeTextOnDrawableUpDown(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s2);
        writeTextOnDrawableDownUp(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s3);
        writeTextOnDrawableDownDown(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s4);
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        seekBar.setVisibility(View.VISIBLE);
        Bitmap workingBitmap = Bitmap.createBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        seekBar.setProgress(convertToPixels(mutableBitmap));
        seekBar.setMax(convertToPixels(mutableBitmap) + 100);

    }


    public void editText(int progress){
        imageView.setImageBitmap(bitmap);
        editTextOnDrawableUpUp(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s1, progress);
        editTextOnDrawableUpDown(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s2, progress);
        editTextOnDrawableDownUp(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s3, progress);
        editTextOnDrawableDownDown(((BitmapDrawable)imageView.getDrawable()).getBitmap(), s4, progress);
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeTextOnDrawableDownUp(Bitmap bm, String text) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight()/6) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

    }


    private void writeTextOnDrawableUpUp(Bitmap bm, String text) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        //paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));
        paint.setTextSize(convertToPixels(mutableBitmap));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight()/7) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

    }
    private void writeTextOnDrawableUpDown(Bitmap bm, String text) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        //paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));
        paint.setTextSize(convertToPixels(mutableBitmap));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()/7) + getResources().getDimensionPixelSize(R.dimen.myFontSize) + 10 - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }


    private void writeTextOnDrawableDownDown(Bitmap bm, String text) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight()/6) - convertToPixels(mutableBitmap) - 10 - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }

    public static int convertToPixels(Bitmap bitmap)
    {
            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();

            int size = width * heigth / 5000;
            return size;

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        Matrix m = new Matrix();

        float sx = width / (float) image.getWidth();
        float sy = height / (float) image.getHeight();
        m.setScale(sx, sy);
        if(width > image.getWidth() || height > image.getHeight())
            return image;

        return Bitmap.createBitmap(image, 0, 0, width, height, m,true);
    }


/////////////////////////////////////////////////////////


    private void editTextOnDrawableUpUp(Bitmap bm, String text, int size) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        //paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));
        paint.setTextSize(size);

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()/7) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }
    private void editTextOnDrawableUpDown(Bitmap bm, String text, int size) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        //paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()/7) + size + 10 - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }





    private void editTextOnDrawableDownUp(Bitmap bm, String text, int size) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight()/6) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }



    private void editTextOnDrawableDownDown(Bitmap bm, String text, int size) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(convertToPixels(imageView.getContext(), 35));
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        //If the text is bigger than the canvas , reduce the font size
        // if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
        //paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positionsf
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight()/6) - size - 10 - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

        //return new BitmapDrawable(getResources(), mutableBitmap);
    }




    public void undo(){
        imageView.setImageBitmap(prev);
    }
}
