package com.ahmedghassen.socialwifi;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMap.OnInfoWindowClickListener;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Map_Fragment extends Fragment implements OnInfoWindowClickListener
        {

    private static final String TAG = "LocationPickerActivity";

    ArrayList<LocationWifi> listlocations = new ArrayList<LocationWifi>();
    private Gson gson;
    static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final String STATE_IN_PERMISSION = "inPermission";
    private static final int REQUEST_PERMS = 1337;
    private boolean needsInit = false;
    private boolean isInPermission = false;
    //GoogleMap m;

    private Mapbox mapView;
    private MapboxMap mapboxMap ;
    private Marker droppedMarker;
    private ImageView hoveringMarker;
    Dialog dialog ;
    MarkerView marky;
    ConnectionManager con;
    RequestQueue queue ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map_, null, false);

        Bundle bundle = this.getArguments();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        String jsonMyObject = "";
        if (bundle != null) {
            jsonMyObject = bundle.getString("locations");
            Log.d("bundle", jsonMyObject);
            List<LocationWifi> alist = Arrays.asList(gson.fromJson(jsonMyObject, LocationWifi[].class));
            listlocations = new ArrayList<LocationWifi>(alist);
        }

        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));

        // Create supportMapFragment
        SupportMapFragment mapFragment;
        if (savedInstanceState == null) {

            // Create fragment
            final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            LatLng patagonia = new LatLng(36.8984, 10.1897);

            // Build mapboxMap
            MapboxMapOptions options = new MapboxMapOptions();
            options.styleUrl(Style.MAPBOX_STREETS);
            options.camera(new CameraPosition.Builder()
                    .target(patagonia)
                    .zoom(9)
                    .build());

            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(options);

            // Add map fragment to parent container
            transaction.add(R.id.map, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("com.mapbox.map");
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                for (LocationWifi loc : listlocations) {
                    LatLng sydney = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLng()));
                    mapboxMap.addMarker(new MarkerViewOptions().position(sydney)
                            .title(loc.getDesc())
                            .snippet(loc.getWifi_pass()));
                }

                /*mapboxMap.getMarkerViewManager().setOnMarkerViewClickListener((marker, view, adapter) -> {                        if(marker.equals(marky)) {

                }
                    return false;
                });*/

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        MarkerViewOptions mark = new MarkerViewOptions().position(point);
                        mapboxMap.addMarker(mark);
                        marky = new MarkerView(new MarkerViewOptions().position(point));

                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.popupadd);
                        dialog.setTitle("Add Your WIFI Access Point");

                        Log.d("marker", "click");
                        // set the custom dialog components - text, image and button
                        EditText desc = (EditText) dialog.findViewById(R.id.desc_loc_add);
                        EditText pw = (EditText) dialog.findViewById(R.id.pw_loc_add);


                        Button dialogButton = (Button) dialog.findViewById(R.id.add_loc);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(v -> {
                            con = new ConnectionManager("addloc");

                            queue = Volley.newRequestQueue(getActivity().getApplicationContext());




                            String s = con.getPath();
                            String uri = s+String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s",
                                    desc.getText().toString(),
                                    pw.getText().toString(),
                                    Double.toString(point.getLatitude()),
                                    Double.toString(point.getLongitude()));

                            StringRequest myReq = new StringRequest(Request.Method.GET,
                                    uri,
                                    response -> {
                                        Toast.makeText(getActivity(),"Successful",Toast.LENGTH_LONG).show();
                                    },
                                    error -> {
                                        Toast.makeText(getActivity(),"Failed",Toast.LENGTH_LONG).show();

                                    });
                            Log.d("requet",myReq.toString());

                            queue.add(myReq);
                            Log.d("requet",myReq.toString());

                            dialog.dismiss();
                        });

                        dialog.show();
                    }
                });


            }
        });



        return root;
    }



    @Override
    public boolean onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity().getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
        return false;
    }

    private void addMarker(MapboxMap map, double lat, double lon,
                           String title, String snippet) {
        map.addMarker(new MarkerViewOptions()
                .position(new LatLng(lat,lon))
                .title(title)
                .snippet(snippet)
        );
    }



/*



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), "user_location_permission_explanation",
                Toast.LENGTH_LONG).show();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(getActivity(), "user_location_permission_not_granted", Toast.LENGTH_LONG).show();

        }
    }

    private void reverseGeocode(final LatLng point) {
        // This method is used to reverse geocode where the user has dropped the marker.
        try {
            MapboxGeocoding client = new MapboxGeocoding.Builder()
                    .setAccessToken(getString(R.string.access_token))
                    .setCoordinates(Position.fromCoordinates(point.getLongitude(), point.getLatitude()))
                    .setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    List<CarmenFeature> results = response.body().getFeatures();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        // If the geocoder returns a result, we take the first in the list and update
                        // the dropped marker snippet with the information. Lastly we open the info
                        // window.
                        if (droppedMarker != null) {
                            droppedMarker.setSnippet(feature.getPlaceName());
                            mapboxMap.selectMarker(droppedMarker);
                        }

                    } else {
                        if (droppedMarker != null) {
                            droppedMarker.setSnippet("location_picker_dropped_marker_snippet_no_results");
                            mapboxMap.selectMarker(droppedMarker);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Log.e(TAG, "Geocoding Failure: " + throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Log.e(TAG, "Error geocoding: " + servicesException.toString());
            servicesException.printStackTrace();
        }
    } // reverseGeocode

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(getActivity());
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }*/
}
