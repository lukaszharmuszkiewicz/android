package com.example.aplikacja;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;
import java.util.zip.CheckedOutputStream;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, RamkaDialog.RamkaDialogListener
        , ErozjaDialog.ErozjaDialogListener, RogiDialog.RogiDialogListener {


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

        //int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);




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
                                imageView.setImageBitmap(imageFilters.toSephia(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 1:
                                ColorMatrix matrix = new ColorMatrix();
                                matrix.setSaturation(0);

                                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                                imageView.setColorFilter(filter);
                                break;
                            case 2:
                                imageView.setImageBitmap(imageFilters.applyGaussianBlurEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 3:
                                imageView.setImageBitmap(imageFilters.applySnowEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                                break;
                            case 4:
                                openDialog();
                                break;
                            case 5:
                                openErozjaDialog();
                                break;
                            case 6:
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


        if( id == R.id.facebook){
            byte[] data = null;

            Bitmap bi = BitmapFactory.decodeFile("/sdcard/viewitems.png");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();


            Bundle params = new Bundle();
            params.putString(Facebook.TOKEN, facebook.getAccessToken());
            params.putString("method", "photos.upload");
            params.putByteArray("picture", data);

            AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
            mAsyncRunner.request(null, params, "POST", new SampleUploadListener(), null);

        }

        if( id == R.id.instagram){







            Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/DCIM");
            dir.mkdirs();
            String name = (new Date()).toString() + ".png";
            File file = new File(dir,name);


            OutputStream out;

            try{
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            if (intent != null)
            {
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
            }
            else
            {
                // bring user to the market to download the app.
                // or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
                startActivity(intent);
            }
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
                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent1,CAMERA_REQUEST);
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

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/DCIM");
            dir.mkdirs();
            String name = (new Date()).toString() + ".png";
            File file = new File(dir,name);


            OutputStream out;

            try{
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
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
