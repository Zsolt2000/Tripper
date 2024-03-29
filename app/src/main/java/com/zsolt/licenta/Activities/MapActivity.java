package com.zsolt.licenta.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.zsolt.licenta.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private Button buttonSelectLocation;
    private MarkerOptions markerOptions;
    private String userPrivilege, pinnedLocation;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private boolean isGuest, isEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        buttonSelectLocation = findViewById(R.id.button_select_location);
        userPrivilege = getIntent().getStringExtra("trip");
        pinnedLocation = getIntent().getStringExtra("location");
        isEditable = getIntent().getBooleanExtra("editable", true);
        setupMap();
        setupSelectLocation();
    }

    private void setupSelectLocation() {
        buttonSelectLocation.setOnClickListener(v -> {
            List<Address> addressList;
            LatLng markerPosition = markerOptions.getPosition();
            Geocoder geocoder = new Geocoder(MapActivity.this);
            try {
                addressList = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Address address = addressList.get(0);
            Intent intent = new Intent();
            intent.putExtra("Location", address);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void setupMap() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (!isGuest) {
                    map.clear();
                    List<Address> addressList;
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(place.getName(), 10);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        markerOptions = new MarkerOptions().position(latLng);
                        map.addMarker(markerOptions);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        buttonSelectLocation.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MapActivity.this, "Can't find this location", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (userPrivilege != null && pinnedLocation != null) {
            if (userPrivilege.equals("guest")) {
                buttonSelectLocation.setVisibility(View.GONE);
                autocompleteSupportFragment.getView().setVisibility(View.GONE);
                isGuest = true;
            } else {
                if (isEditable) {
                    buttonSelectLocation.setVisibility(View.VISIBLE);
                    autocompleteSupportFragment.getView().setVisibility(View.VISIBLE);
                    map.setOnMapClickListener(latLng -> {
                        markerOptions = new MarkerOptions().position(latLng);
                        map.clear();
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        map.addMarker(markerOptions);
                        buttonSelectLocation.setVisibility(View.VISIBLE);
                    });}
                else{
                    buttonSelectLocation.setVisibility(View.GONE);
                    autocompleteSupportFragment.getView().setVisibility(View.GONE);
                }
            }

                List<Address> addressList;
                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    addressList = geocoder.getFromLocationName(pinnedLocation, 10);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    markerOptions = new MarkerOptions().position(latLng);
                    map.addMarker(markerOptions);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                } else {
                    Toast.makeText(MapActivity.this, "Can't find this location", Toast.LENGTH_SHORT).show();
                }
            }else{
            map.setOnMapClickListener(latLng -> {
                markerOptions = new MarkerOptions().position(latLng);
                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                map.addMarker(markerOptions);
                buttonSelectLocation.setVisibility(View.VISIBLE);
            });
        }
        }
    }