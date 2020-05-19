package com.example.aplikacja;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.RelativeLayout;
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
    View v;
    boolean imageExists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        v = findViewById(android.R.id.content).getRootView();
        v.setBackgroundColor(Color.WHITE);
        imageView = findViewById(R.id.imageView);
        setBackground();
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


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.filtry) {
            if (imageExists) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz filtr");
                String[] options = {"sepia", "czarno-białe", "rozmycie Gaussa", "śnieg", "nasycenie", "rozjaśnienie", "grawer"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.toSephia(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 1:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.toGrayScale(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 2:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.applyGaussianBlurEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 3:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                imageView.setImageBitmap(imageFilters.applySnowEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 4:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                openDialog();
                                break;
                            case 5:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                openErozjaDialog();
                                break;
                            case 6:
                                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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
            if (imageExists) {
                imageView.setImageBitmap(null);
                setBackground();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }

        if (id == R.id.obrót) {
            if (imageExists) {
                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz obrót");
                String[] options = {"90 stopni w prawo", "90 stopni w lewo"};
                final Bitmap img = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Matrix matrix = new Matrix();
                                matrix.setRotate(90);
                                Bitmap bitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
                                imageView.setImageBitmap(bitmap);
                                break;
                            case 1:
                                Matrix matrix1 = new Matrix();
                                matrix1.setRotate(-90);
                                Bitmap bitmap1 = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix1, true);
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


        if (id == R.id.dodajElementy) {
            if (imageExists) {

                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybierz element");
                String[] options = {"Śnieg", "Ramka", "Zaokrąglone rogi"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
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


        if (id == R.id.instagram) {
            if (imageExists) {
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
                        e.printStackTrace();
                    }
                    shareIntent.setType("image/jpeg");
                    startActivity(shareIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
                    startActivity(intent);
                }
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }


        if (id == R.id.facebook) {
            if (imageExists) {


                SharePhoto sharePhoto = new SharePhoto.Builder()
                        .setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap())
                        .build();

                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                if (shareDialog.canShow(SharePhotoContent.class)) {
                    shareDialog.show(photoContent);
                }

            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            return true;
        }


        if (id == R.id.undo) {
            undo();
            return true;
        }

        if (id == R.id.add_image) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Dodaj zdjęcie");
            String[] options = {"Galeria", "Aparat"};
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select"), GALLERY_REQUEST);
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

                            break;

                    }
                }
            });
            v.setBackgroundColor(Color.WHITE);
            AlertDialog dialog = builder.create();
            imageExists = true;
            imageView.setAlpha(255);
            dialog.show();

            return true;
        }

        if (id == R.id.save) {
            if (imageExists) {


                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                File dir = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/");
                if (!dir.exists())
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
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }


        if (id == R.id.text) {
            if (imageExists) {
                prev = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                openMemDialog();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "W pierwszej kolejności dodaj zdjęcie", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
                byte[] bytes = outputStream.toByteArray();
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e) {
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


    public void openMemDialog() {
        MemDialog memDialog = new MemDialog();
        memDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String level) {
        System.out.println(level);

    }


    public void saturation(String s) {
        imageView.setImageBitmap(imageFilters.applySaturationFilter(((BitmapDrawable) imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void ramka(String s) {
        imageView.setImageBitmap(imageFilters.addBorder(((BitmapDrawable) imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void erozja(String s) {
        imageView.setImageBitmap(imageFilters.applyBrightnessEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void rogi(String s) {
        imageView.setImageBitmap(imageFilters.applyRoundCornerEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap(), Integer.valueOf(s)));

    }

    public void addText(String s1, String s2, String s3, String s4) {
        writeTextOnDrawableUpUp(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s1);
        writeTextOnDrawableUpDown(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s2);
        writeTextOnDrawableDownUp(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s3);
        writeTextOnDrawableDownDown(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s4);
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        seekBar.setVisibility(View.VISIBLE);
        Bitmap workingBitmap = Bitmap.createBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        seekBar.setProgress(convertToPixels(mutableBitmap));
        seekBar.setMax(convertToPixels(mutableBitmap) + 100);

    }


    public void editText(int progress) {
        imageView.setImageBitmap(bitmap);
        editTextOnDrawableUpUp(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s1, progress);
        editTextOnDrawableUpDown(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s2, progress);
        editTextOnDrawableDownUp(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s3, progress);
        editTextOnDrawableDownDown(((BitmapDrawable) imageView.getDrawable()).getBitmap(), s4, progress);
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
        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight() / 6) - ((paint.descent() + paint.ascent()) / 2));

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
        paint.setTextSize(convertToPixels(mutableBitmap));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight() / 7) - ((paint.descent() + paint.ascent()) / 2));

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
        paint.setTextSize(convertToPixels(mutableBitmap));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight() / 7) + getResources().getDimensionPixelSize(R.dimen.myFontSize) + 10 - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());
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
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.myFontSize));


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight() / 6) - convertToPixels(mutableBitmap) - 10 - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());
    }

    public static int convertToPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int heigth = bitmap.getHeight();

        int size = width * heigth / 5000;
        return size;

    }



    private void editTextOnDrawableUpUp(Bitmap bm, String text, int size) {

        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(size);

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
        int yPos = (int) ((canvas.getHeight() / 7) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

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
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight() / 7) + size + 10 - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());
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
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        int xPos = (canvas.getWidth() / 2) - 2;

        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight() / 6) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

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
        paint.setTextSize(size);


        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(mutableBitmap);

        int xPos = (canvas.getWidth() / 2) - 2;

        int yPos = (int) ((canvas.getHeight()) - (canvas.getHeight() / 6) - size - 10 - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, xPos, yPos, paint);

        BitmapDrawable bitmap = new BitmapDrawable(getResources(), mutableBitmap);
        imageView.setImageBitmap(bitmap.getBitmap());

    }


    public void undo() {
        imageView.setImageBitmap(prev);
    }


    public void setBackground(){
        imageView.setImageResource(R.drawable.tlo);
        imageView.setAlpha(180);
        imageExists = false;
    }
}
