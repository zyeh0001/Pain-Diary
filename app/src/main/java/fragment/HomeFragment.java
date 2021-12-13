package fragment;

import androidx.annotation.NonNull;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.Retrofit.ApiInterface;
import com.example.paindiary.Retrofit.WeatherResponse;
import com.example.paindiary.databinding.HomeFragmentBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import viewmodel.SharedViewModel;


public class HomeFragment extends Fragment {
    private SharedViewModel model;
    private HomeFragmentBinding addBinding;
    final String WEATHER_API_KEY = "b25f808cd5609d8efdcc3019409dcca6";
    final String BaseUrl = "https://api.openweathermap.org/";
    private String tmpResult;
    private String cTemp;
    private String cHumidity;
    private String cPressure;
    String Location_Provider = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    TextView Pressure, Humidity, Temperature, City;

    //public static String lat = "52.8219";
    //public static String lon = "-1.4252";


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment

        addBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        Pressure = addBinding.pressure;
        Humidity = addBinding.humidity;
        Temperature = addBinding.temperature;
        City = addBinding.cityName;

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        getCurrentData();

        return view;

    }

    void getCurrentData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData("Melbourne,au", "metric",WEATHER_API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    City.setText("City: "+weatherResponse.name);
                    Temperature.setText(" Temp: "+ weatherResponse.getMain().getTemp());
                    cTemp = "" + weatherResponse.getMain().getTemp();
                    Humidity.setText("Humidity: "+ weatherResponse.getMain().getHumidity());
                    cHumidity = weatherResponse.getMain().getHumidity();
                    Pressure.setText("Pressure: "+weatherResponse.getMain().getPressure());
                    cPressure = weatherResponse.getMain().getPressure();
                    String sendData  = "";
                    sendData = cTemp + " " + cHumidity + " " + cPressure;
                    model.setMessage(sendData);

                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                City.setText(t.getMessage());
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        getCurrentData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
}
