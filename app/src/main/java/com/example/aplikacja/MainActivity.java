package com.example.aplikacja;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.zip.CheckedOutputStream;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, RamkaDialog.RamkaDialogListener
        , ErozjaDialog.ErozjaDialogListener, RogiDialog.RogiDialogListener {


    ImageView imageView;
    private int REQUEST_CODE = 1;
    MenuItem idk;
    MenuItem addImage;
    MenuItem saturation;
    MenuItem border;
    MenuItem erozja;
    MenuItem applyRoundCornerEffect;
    ImageFilters imageFilters;
    private EditText level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageFilters = new ImageFilters();
        addImage = (MenuItem) findViewById(R.id.add_image);







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

        saturation = menu.findItem(R.id.saturation);
        saturation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openDialog();
                return true;
            }
        });
        border = menu.findItem(R.id.border);
        border.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openBorderDialog();
                return true;
            }
        });
        erozja = menu.findItem(R.id.rozja);
        erozja.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openErozjaDialog();
                return false;
            }
        });
        applyRoundCornerEffect = menu.findItem(R.id.rogi);
        applyRoundCornerEffect.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openRogiDialog();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.sepia) {
            imageView.setImageBitmap(imageFilters.toSephia(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
            return true;
        }

        if (id == R.id.delete) {
            imageView.setImageBitmap(null);
            return true;
        }

        if (id == R.id.blackandwhite) {


            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(filter);
            return true;
        }

//        if (id == R.id.border) {
//            //imageView.setImageBitmap(imageFilters.addBorder(((BitmapDrawable)imageView.getDrawable()).getBitmap(),20));
//            imageView.setImageBitmap(imageFilters.applyInvertEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
//            return true;
//        }

        if(id == R.id.prawo) {
            imageView.setRotation(imageView.getRotation() + 90);
        }

        if(id == R.id.lewo) {
            imageView.setRotation(imageView.getRotation() - 90);
        }

        if (id == R.id.add_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select"), REQUEST_CODE);
            return true;
        }

//        if(id == R.id.save) {
//            SaveImage(((BitmapDrawable)imageView.getDrawable()).getBitmap());
//            return true;
//        }

        if(id == R.id.gauss) {
            imageView.setImageBitmap(imageFilters.applyGaussianBlurEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
            return true;
        }

//        if(id == R.id.rozja) {
//            imageView.setImageBitmap(imageFilters.applyBrightnessEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap(),5));
//            return true;
//        }

        if(id == R.id.engrave) {
            imageView.setImageBitmap(imageFilters.applyEngraveEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
            return true;
        }

        if(id == R.id.snow) {
            imageView.setImageBitmap(imageFilters.applySnowEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
            return true;
        }

//        if(id == R.id.saturation) {
//            imageView.setImageBitmap(imageFilters.applySaturationFilter(((BitmapDrawable)imageView.getDrawable()).getBitmap(), 10));
//            return true;
//        }

//        if(id == R.id.idk){
//
//
//
//
////            openDialog();
////            imageView.setImageBitmap(imageFilters.applyRoundCornerEffect(((BitmapDrawable)imageView.getDrawable()).getBitmap(), 10));
////            System.out.println(level);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
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
}
