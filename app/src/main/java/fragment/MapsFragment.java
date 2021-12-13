package fragment;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.paindiary.Login;
import com.example.paindiary.R;
import com.example.paindiary.databinding.MapsFragmentBinding;
import com.example.paindiary.databinding.ReportsFragmentBinding;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.io.IOException;
import java.util.List;

import viewmodel.SharedViewModel;


public class MapsFragment extends Fragment {
    private SharedViewModel model;
    private MapsFragmentBinding addBinding;
    private MapView mapView;
    private Geocoder geocoder;
    private EditText uAddress;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        geocoder = new Geocoder(getActivity());
        String token = getString(R.string.mapbox_access_token);
        Mapbox.getInstance(getActivity(), token);
        addBinding = MapsFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        final LatLng[] latLng = {new LatLng(-37.876823, 145.045837)};
        uAddress = addBinding.address;
        mapView = addBinding.mapView;

        addBinding.mapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(uAddress.getText().toString().trim(), 1);

                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        latLng[0] = new LatLng(address.getLatitude(), address.getLongitude());

                        mapView.onCreate(savedInstanceState);
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull final MapboxMap mapboxMap) {


                                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

                                    @Override

                                    public void onStyleLoaded(@NonNull Style style) {
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(new LatLng(address.getLatitude(), address.getLongitude()))
                                                .title(uAddress.getText().toString().trim());
                                        CameraPosition position = new CameraPosition.Builder().target(latLng[0]).zoom(13).build();
                                        mapboxMap.addMarker(markerOptions);
                                        mapboxMap.setCameraPosition(position);

                                    }
                                });


                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Address doesn't exist", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {


                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

                    @Override

                    public void onStyleLoaded(@NonNull Style style) {
                        CameraPosition position = new CameraPosition.Builder().target(latLng[0]).zoom(13).build();

                        mapboxMap.setCameraPosition(position);

                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();

        mapView.onStart();

    }

    @Override
    public void onResume() {

        super.onResume();

        mapView.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

        mapView.onPause();

    }

    @Override
    public void onStop() {

        super.onStop();
        mapView.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();

        mapView.onLowMemory();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
        mapView.onDestroy();
    }
}
