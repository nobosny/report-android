package com.vectoranalytica.trashwatchfree;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shamanland.fab.FloatingActionButton;
import com.vectoranalytica.trashwatchfree.httpclient.TrashWatchClient;
import com.vectoranalytica.trashwatchfree.model.Serverbin;
import com.vectoranalytica.trashwatchfree.model.Trashbin;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainMapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    Trashbin trashbin;

    Context context;

    static final int NEW_ITEM_REQUEST = 1;

    private static Date lastRequestTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        context = this;
        buildGoogleApiClient();

        setUpMapIfNeeded();

        FloatingActionButton btn_newitem = (FloatingActionButton) findViewById(R.id.btn_new);
        btn_newitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    trashbin = new Trashbin();
                    trashbin.setLat(mLastLocation.getLatitude());
                    trashbin.setLon(mLastLocation.getLongitude());
                    trashbin.setRecorddate(new Date());
                    DataHolder.getInstance().setData(trashbin);
                    Intent newItem = new Intent(context, NewItemActivity.class);
                    startActivityForResult(newItem, NEW_ITEM_REQUEST);
                }
            }
        });

        FloatingActionButton btn_refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean clean = false;
                if (lastRequestTime == null) {
                    lastRequestTime = getWeekAgo();
                    clean = true;
                }
                getPointsFromServer(clean, lastRequestTime);
                lastRequestTime = new Date();

                postUnsynced();
            }
        });

        mGoogleApiClient.connect();

        //Trashbin.deleteAll(Trashbin.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.clear();

        //request server points from now to 1 week ago
        lastRequestTime = new Date();
        getPointsFromServer(true, getWeekAgo());

        /*List<Serverbin> serverPoints = Serverbin.listAll(Serverbin.class);
        for (Serverbin item: serverPoints) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLat(), item.getLon())).title(item.getRecorddate().toString()));
        }*/

        //get the list of points and plot as markers
        List<Trashbin> points = Trashbin.listAll(Trashbin.class);
        for (Trashbin point: points) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(point.getLat(), point.getLon()))
                            .title(point.getRecorddate().toString())
                            .snippet(point.getImagefile())
            );
        }
    }

    private Date getWeekAgo() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        return cal.getTime();
    }

    private void getPointsFromServer(final Boolean cleanFirst, Date lastTime) {
        TrashWatchClient.get("/getpoints/" + lastTime.getTime(), null,
            new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (cleanFirst) {
                        Serverbin.deleteAll(Serverbin.class); //clear the temp table
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Serverbin trashserver = new Serverbin();
                            trashserver.setRecorddate(new Date(response.getJSONObject(i).getLong("recorddate")));
                            trashserver.setMosquitoes(response.getJSONObject(i).getInt("mosquitoes") == 1);
                            trashserver.setCockroaches(response.getJSONObject(i).getInt("cockroaches") == 1);
                            trashserver.setMice(response.getJSONObject(i).getInt("mice") == 1);
                            trashserver.setBiohazard(response.getJSONObject(i).getInt("biohazard") == 1);
                            trashserver.setOthervectors(response.getJSONObject(i).getString("othervectors"));

                            trashserver.setHouses(response.getJSONObject(i).getInt("houses") == 1);
                            trashserver.setBusinesses(response.getJSONObject(i).getInt("businesses") == 1);
                            trashserver.setConstructionsites(response.getJSONObject(i).getInt("constructionsites") == 1);
                            trashserver.setOthersources(response.getJSONObject(i).getString("othersources"));

                            trashserver.setImagefile("noimage");
                            trashserver.setDescription(response.getJSONObject(i).getString("description"));

                            trashserver.setLat(response.getJSONObject(i).getDouble("lat"));
                            trashserver.setLon(response.getJSONObject(i).getDouble("lon"));
                            trashserver.save();

                            mMap.addMarker(new MarkerOptions().position(new LatLng(trashserver.getLat(), trashserver.getLon())).title(trashserver.getRecorddate().toString()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /*List<Serverbin> serverPoints = Serverbin.listAll(Serverbin.class);
                    for (Serverbin item: serverPoints) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLat(), item.getLon())).title(item.getRecorddate().toString()));
                    }*/
                }
            }
        );
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //update the map
            LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                //reset the map with all markers
                setUpMap();
                //Sync data from database, all record marked as not synced
                postUnsynced();
            }
        }
    }

    private void postUnsynced() {
        for (Trashbin item: Trashbin.getUnsynced()) {
            postTrashbin(item);
        }
    }

    private void postTrashbin(final Trashbin bin) {
        RequestParams params = new RequestParams();
        params.put("recorddate", bin.getRecorddate().getTime());
        params.put("mosquitoes", bin.getMosquitoes());
        params.put("cockroaches", bin.getCockroaches());
        params.put("mice", bin.getMice());
        params.put("biohazard", bin.getBiohazard());
        params.put("othervectors", bin.getOthervectors());
        params.put("houses", bin.getHouses());
        params.put("businesses", bin.getBusinesses());
        params.put("constructions", bin.getConstructionsites());
        params.put("othersources", bin.getOthersources());
        params.put("siteimage", bin.getImageFileBase64());
        //params.put("siteimage", new ByteArrayInputStream(bin.getImageFileBase64Bytes()), bin.getImagefile());
        params.put("description", bin.getDescription());
        params.put("coords_lat", bin.getLat());
        params.put("coords_lon", bin.getLon());

        TrashWatchClient.post("/api/createreport", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //tell the success
                Toast.makeText(context, "Item posted: " + response.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString.length() > 10) {
                    Log.e("CONNECTION ERROR", responseString);
                    Toast.makeText(context, "Error " + responseString, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Item posted", Toast.LENGTH_LONG).show();
                    bin.setSynced(true);
                    bin.save();
                }
            }
        });
    }
}
