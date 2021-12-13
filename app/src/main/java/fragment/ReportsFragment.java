package fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.paindiary.R;
import com.example.paindiary.databinding.ReportsFragmentBinding;
import com.example.paindiary.viewmodel.PainRecordViewModel;

import viewmodel.SharedViewModel;


public class ReportsFragment extends Fragment {
    private SharedViewModel model;
    private ReportsFragmentBinding addBinding;


    public ReportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        addBinding = ReportsFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();


        addBinding.toPainWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.report_to_pain_weather);
            }
        });

        addBinding.toStepsPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.reports_to_reportStepsPie);
            }
        });

        addBinding.toPainLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.reports_to_reportPainLocation);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
}
