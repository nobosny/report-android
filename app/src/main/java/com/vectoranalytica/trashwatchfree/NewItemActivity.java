package com.vectoranalytica.trashwatchfree;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vectoranalytica.trashwatchfree.model.Trashbin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewItemActivity extends ActionBarActivity {

    Activity context;
    ActionBar actionBar;
    Trashbin trashbin;

    ImageButton btn_mosq;
    Boolean mosquitoes;

    ImageButton btn_cock;
    Boolean cockroaches;

    ImageButton btn_mice;
    Boolean mice;

    ImageButton btn_bio;
    Boolean biohazard;

    ImageButton btn_tick;
    Boolean tick;

    ImageButton btn_house;
    Boolean house;

    ImageButton btn_business;
    Boolean business;

    ImageButton btn_construction;
    Boolean construction;

    ImageButton btn_image;
    public int TAKE_PICTURE = 1;
    private Bitmap bitmap;
    String mCurrentPhotoPath;

    EditText et_othervectors;
    EditText et_othersources;
    EditText et_description;

    TextView tv_datetime;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        context = this;

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        trashbin = (Trashbin) DataHolder.getInstance().getData();
        if (trashbin == null) {
            trashbin = new Trashbin();
        }

        setUpMapIfNeeded();
        setOptionsButtons_UI();



        btn_image = (ImageButton) findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, TAKE_PICTURE);
                        galleryAddPic();
                        trashbin.setImagefile(mCurrentPhotoPath);
                    }
                }
            }
        });

        et_othervectors = (EditText) findViewById(R.id.et_othervectors);
        et_othersources = (EditText) findViewById(R.id.et_othersources);
        et_description = (EditText) findViewById(R.id.et_description);

        setUiByModel(trashbin);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                setPic(mCurrentPhotoPath);
            }
        }
    }

    private void setPic(String photoPath) {
        // Get the dimensions of the View
        int targetW = btn_image.getWidth();
        int targetH = btn_image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = 4; //Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        btn_image.setImageBitmap(bitmap);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_point))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if (trashbin != null) {
            LatLng position = new LatLng(trashbin.getLat(), trashbin.getLon());
            mMap.addMarker(new MarkerOptions().position(position).title(trashbin.getRecorddate().toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            //Action to save NewItem to database
            if (trashbin == null) {
                trashbin = new Trashbin();
            }

            //update trashbin with the values on screen
            trashbin.setRecorddate(new Date()); //right now date
            trashbin.setMosquitoes(mosquitoes);
            trashbin.setCockroaches(cockroaches);
            trashbin.setMice(mice);
            trashbin.setBiohazard(biohazard);
            trashbin.setTick(tick);
            trashbin.setOthervectors(et_othervectors.getText().toString());
            trashbin.setHouses(house);
            trashbin.setBusinesses(business);
            trashbin.setConstructionsites(construction);
            trashbin.setOthersources(et_othersources.getText().toString());
            trashbin.setDescription(et_description.getText().toString());

            trashbin.save();
            setResult(Activity.RESULT_OK);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOptionsButtons_UI() {

        tv_datetime = (TextView) findViewById(R.id.tv_datetime);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        tv_datetime.setText(date);

        mosquitoes = false;
        btn_mosq = (ImageButton) findViewById(R.id.btn_mosq);
        btn_mosq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mosquitoes = !mosquitoes;
                trashbin.setMosquitoes(mosquitoes);
                if (mosquitoes) {
                    btn_mosq.setImageDrawable(getResources().getDrawable(R.drawable.mosquitoes_on));
                } else {
                    btn_mosq.setImageDrawable(getResources().getDrawable(R.drawable.mosquitoes_off));
                }
            }
        });

        cockroaches = false;
        btn_cock = (ImageButton) findViewById(R.id.btn_cock);
        btn_cock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cockroaches = !cockroaches;
                trashbin.setCockroaches(cockroaches);
                if (cockroaches) {
                    btn_cock.setImageDrawable(getResources().getDrawable(R.drawable.cock_on));
                } else {
                    btn_cock.setImageDrawable(getResources().getDrawable(R.drawable.cock_off));
                }
            }
        });

        mice = false;
        btn_mice = (ImageButton) findViewById(R.id.btn_mice);
        btn_mice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mice = !mice;
                trashbin.setMice(mice);
                if (mice) {
                    btn_mice.setImageDrawable(getResources().getDrawable(R.drawable.mice_on));
                } else {
                    btn_mice.setImageDrawable(getResources().getDrawable(R.drawable.mice_off));
                }
            }
        });

        biohazard = false;
        btn_bio = (ImageButton) findViewById(R.id.btn_bio);
        btn_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biohazard = !biohazard;
                trashbin.setBiohazard(biohazard);
                if (biohazard) {
                    btn_bio.setImageDrawable(getResources().getDrawable(R.drawable.fly_on));
                } else {
                    btn_bio.setImageDrawable(getResources().getDrawable(R.drawable.fly_off));
                }
            }
        });

        tick = false;
        btn_tick = (ImageButton) findViewById(R.id.btn_tick);
        btn_tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tick = !tick;
                trashbin.setTick(tick);
                if (tick) {
                    btn_tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_on));
                } else {
                    btn_tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_off));
                }
            }
        });

        house = false;
        btn_house = (ImageButton) findViewById(R.id.btn_house);
        btn_house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                house = !house;
                trashbin.setHouses(house);
                if (house) {
                    btn_house.setImageDrawable(getResources().getDrawable(R.drawable.houses_on));
                } else {
                    btn_house.setImageDrawable(getResources().getDrawable(R.drawable.houses_off));
                }
            }
        });

        business = false;
        btn_business = (ImageButton) findViewById(R.id.btn_business);
        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                business = !business;
                trashbin.setBusinesses(business);
                if (business) {
                    btn_business.setImageDrawable(getResources().getDrawable(R.drawable.business_on));
                } else {
                    btn_business.setImageDrawable(getResources().getDrawable(R.drawable.business_off));
                }
            }
        });

        construction = false;
        btn_construction = (ImageButton) findViewById(R.id.btn_construction);
        btn_construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                construction = !construction;
                trashbin.setConstructionsites(construction);
                if (construction) {
                    btn_construction.setImageDrawable(getResources().getDrawable(R.drawable.construction_on));
                } else {
                    btn_construction.setImageDrawable(getResources().getDrawable(R.drawable.construction_off));
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setUiByModel(Trashbin trashbin) {
        if (trashbin.getMosquitoes()) {
            btn_mosq.setImageDrawable(getResources().getDrawable(R.drawable.mosquitoes_on));
        } else {
            btn_mosq.setImageDrawable(getResources().getDrawable(R.drawable.mosquitoes_off));
        }

        if (trashbin.getCockroaches()) {
            btn_cock.setImageDrawable(getResources().getDrawable(R.drawable.cock_on));
        } else {
            btn_cock.setImageDrawable(getResources().getDrawable(R.drawable.cock_off));
        }

        if (trashbin.getMice()) {
            btn_mice.setImageDrawable(getResources().getDrawable(R.drawable.mice_on));
        } else {
            btn_mice.setImageDrawable(getResources().getDrawable(R.drawable.mice_off));
        }

        if (trashbin.getBiohazard()) {
            btn_bio.setImageDrawable(getResources().getDrawable(R.drawable.fly_on));
        } else {
            btn_bio.setImageDrawable(getResources().getDrawable(R.drawable.fly_off));
        }

        if (trashbin.getTick()) {
            btn_tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_on));
        } else {
            btn_tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_off));
        }

        if (trashbin.getHouses()) {
            btn_house.setImageDrawable(getResources().getDrawable(R.drawable.houses_on));
        } else {
            btn_house.setImageDrawable(getResources().getDrawable(R.drawable.houses_off));
        }

        if (trashbin.getBusinesses()) {
            btn_business.setImageDrawable(getResources().getDrawable(R.drawable.business_on));
        } else {
            btn_business.setImageDrawable(getResources().getDrawable(R.drawable.business_off));
        }

        if (trashbin.getConstructionsites()) {
            btn_construction.setImageDrawable(getResources().getDrawable(R.drawable.construction_on));
        } else {
            btn_construction.setImageDrawable(getResources().getDrawable(R.drawable.construction_off));
        }

        if (trashbin.getImagefile() != null) {
            setPic(trashbin.getImagefile());
        }
    }
}
