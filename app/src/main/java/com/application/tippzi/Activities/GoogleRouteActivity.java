package com.application.tippzi.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.tippzi.Adapters.BarTitleViewAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.services.Constants.PRECISION_6;

public class GoogleRouteActivity extends FragmentActivity {

    private static final String TAG = "DirectionsActivity";

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    //
    private ImageView google_route_back ;

    private int newWidth = 0 ;
    private int newHeight = 0 ;
    private GPSTracker mGPS ;
    private Icon icon1, icon2 ;
    private MarkerViewOptions mo;
    private MarkerViewOptions mo1 ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoiaml1cmVuaGUiLCJhIjoiY2phenYzeHVjMWxiZDMzcWt2ajRkdDh4OSJ9.Ic_Y3m40PXSiBYmS8kVZuw");

        setContentView(R.layout.activity_google_route);
        mGPS = new GPSTracker(this);
        if (mGPS.canGetLocation()){
            mGPS.getLocation();
        }
        final Position origin = Position.fromCoordinates(mGPS.getLatitude(), mGPS.getLongitude());
        // Plaza del Triunfo in Granada, Spain.
        final Position destination = Position.fromCoordinates(Double.valueOf(GD.customerModel.bars.get(GD.select_pos).lat), Double.valueOf(GD.customerModel.bars.get(GD.select_pos).lon));

        icon1 = resizeBitmap(R.mipmap.ico_my_location);
        icon2 = resizeBitmap(R.mipmap.ico_selected_location);
        // Setup the MapView
        mapView = (MapView) findViewById(R.id.mapView);
        google_route_back = findViewById(R.id.iv_google_route_back);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                // Add origin and destination to the map
                CameraPosition results1
                        = new CameraPosition.Builder().target(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()))
                        .zoom(14.0f)
                        .bearing(0)
                        .tilt(0)
                        .build();
                String bar_title = GD.customerModel.bars.get(GD.select_pos).business_name ;
                mo1 = createMarkerOptions(new LatLng(destination.getLatitude(), destination.getLongitude()), bar_title, "unselect");
                mo = createMarkerOptions(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), "My Location", "select");

                LayoutInflater inflater = LayoutInflater.from(GoogleRouteActivity.this);
                mapboxMap.setInfoWindowAdapter(new BarTitleViewAdapter(inflater));
                mapboxMap.setCameraPosition(results1);
                mapboxMap.setZoom(15);
                mapboxMap.addMarker(mo1).setInfoWindowAnchor(0.5f, 0.0f);
                mapboxMap.addMarker(mo).setAnchor(0.5f,0.0f);
                mapboxMap.selectMarker(map.addMarker(mo1));
                mapboxMap.selectMarker( map.addMarker(mo));

                getRoute(origin,destination);
            }
        });

        google_route_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backintent = new Intent(getApplicationContext(), CustomerBarDetailActivity.class);
                startActivity(backintent);
                finish();
            }
        });
    }

    private MarkerViewOptions createMarkerOptions(LatLng location_pos, String title, String select_flag) {
        if (location_pos == null) return null;
        if (select_flag.equals("select")) {
            icon1 = resizeBitmap(R.mipmap.ico_my_location);
        } else if (select_flag.equals("unselect")){
            icon1 = resizeBitmap(R.mipmap.ico_selected_location);
        }
        return new MarkerViewOptions()
                .position(new LatLng(location_pos.getLatitude(), location_pos.getLongitude()))
                .icon(icon1)
                .anchor(0.5f,0.0f)
                .title(title);
    }

    private Icon resizeBitmap(int resId){
        newWidth = 200;
        newHeight = 200;
        BitmapDrawable bitmapDrawable =(BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), resId);
        Bitmap originalBitmap = bitmapDrawable.getBitmap();

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        IconFactory iconFactory = IconFactory.getInstance(getApplicationContext());
        Icon icon = iconFactory.fromBitmap(resizedBitmap);

        return icon;
    }

    private void getRoute(Position origin, Position destination) {

        client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setOverview(DirectionsCriteria.OVERVIEW_FULL)
                .setProfile(DirectionsCriteria.PROFILE_CYCLING)
                .setAccessToken("pk.eyJ1Ijoiaml1cmVuaGUiLCJhIjoiY2phenYzeHVjMWxiZDMzcWt2ajRkdDh4OSJ9.Ic_Y3m40PXSiBYmS8kVZuw")
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }
                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());
                Toast.makeText(GoogleRouteActivity.this, String.format("Select Location",
                        currentRoute.getDistance()), Toast.LENGTH_SHORT).show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(GoogleRouteActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), PRECISION_6);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the directions API request
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
