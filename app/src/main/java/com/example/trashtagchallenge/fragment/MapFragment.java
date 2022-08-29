package com.example.trashtagchallenge.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.activity.LoginActivity;
import com.example.trashtagchallenge.classes.Trash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {


    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    private DatabaseReference databaseReferenceTrash;
    private Query queryTrash;
    private Location currentLocation;
    private static final int ERROR_DIALOG__REQUEST = 9001;
    private double lat_view;
    private double lon_view;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    private boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),available,ERROR_DIALOG__REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getContext(),"You can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment

        if(isServicesOk()){
            try {
                if(mapFragment==null){
                    Toast.makeText(getContext(),"Map Fragment is null",Toast.LENGTH_SHORT).show();
                    return;
                }
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.clear();
                        getLocationPermission();
                        if (mLocationPermissionsGranted) {
                            getDeviceLocation();
                            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                        addMarkers();
                        addTrash();
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }




        }

    }







    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        mLocationPermissionsGranted = false;
        if (requestCode== LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length >0){
                for(int i=0; i < grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        mLocationPermissionsGranted = false;
                        return;
                    }
                }
                mLocationPermissionsGranted = true;
            }
        }
    }






    private void getDeviceLocation(){
        Log.d("Map Fragment", "getDeviceLocation: Getting devices current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try{
            if(mLocationPermissionsGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d("Map Fragment", "onComplete: Found location");
                            currentLocation = (Location) task.getResult();
                            //Maribor
                            double lat = 46.554650;
                            double lon = 15.64667;
                            if(currentLocation != null) {
                                lat = currentLocation.getLatitude();
                                lon = currentLocation.getLongitude();
                            }
                            else{
                                //buildAlertMessageNoGps();
                            }
                            moveCamera(new LatLng(lat,lon),DEFAULT_ZOOM);
                            lat_view = lat;
                            lon_view = lon;
                        }
                        else {
                            Log.d("Map Fragment", "onComplete: Current location is null");
                            Toast.makeText(getContext(),"unable to get current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
            else {
                double lat = 46.55472;
                double lon = 15.64667;
                lat_view = lat;
                lon_view = lon;
                moveCamera(new LatLng(lat,lon),DEFAULT_ZOOM);
            }
        }catch (SecurityException e){
            Log.d("Map Fragment", "getDeviceLocation: " + e.getMessage());
        }
    }





    private void moveCamera(LatLng latLng, float zoom){
        Log.d("Map Fragment", "moveCamera: moving the camera to:"+latLng.latitude + ", " +latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }


    private void addTrash(){
        mMap.setOnMapLongClickListener(new
           GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick (LatLng latLng){
                   Log.d("Map Fragment", "onMapLongClick: Long click ocurred");

                   Bundle bundle = new Bundle();
                   bundle.putDouble("lat",latLng.latitude);
                   bundle.putDouble("lng",latLng.longitude);

                   Navigation.findNavController(getView()).navigate(R.id.action_mapFragment_to_addTrashFragment, bundle);

               }
           });
    }



    private void addMarkers(){
        databaseReferenceTrash = FirebaseDatabase.getInstance().getReference();
        queryTrash = databaseReferenceTrash.child("trashes");
        queryTrash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot trashSnapshot : dataSnapshot.getChildren()) {
                    Trash trash = trashSnapshot.getValue(Trash.class);
                    if (trash.getCleanedUsername() != null) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(trash.getLat(), trash.getLng())).title(trash.getTrashID()).
                                snippet("Cleaned").icon(BitmapDescriptorFactory.fromResource(R.mipmap.greenwhitetrashmarker)));
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(trash.getLat(), trash.getLng())).title(trash.getTrashID())
                                .snippet("Recorded").icon(BitmapDescriptorFactory.fromResource(R.mipmap.redwhitetrashmarker)));
                    }
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Bundle bundle = new Bundle();
                        bundle.putString("markerIdKey",marker.getTitle());
                        if(marker.getSnippet().trim().equals("Recorded"))
                            Navigation.findNavController(getView()).navigate(R.id.action_mapFragment_to_addCleanedTrashFragment, bundle);
                        else
                            Navigation.findNavController(getView()).navigate(R.id.action_mapFragment_to_trashFragment,bundle);
                        return false;
                    }
                });

    }
    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(getContext(),"Error occured while accessing database: " +databaseError,Toast.LENGTH_SHORT).show();
    }
        });
    }




}
