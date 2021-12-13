package fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.adapter.RecyclerViewAdapter;
import com.example.paindiary.databinding.DailyRecordFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import viewmodel.SharedViewModel;


public class DailyRecordFragment extends Fragment {
    private DailyRecordFragmentBinding addBinding;
    private PainRecordViewModel painRecordViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private List<PainRecord> painRecordList;
    private RecyclerViewAdapter adapter;
    private FirebaseAuth auth;

    public DailyRecordFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment using the binding
        addBinding = DailyRecordFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();

        adapter = new RecyclerViewAdapter();
        addBinding.recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL));
        addBinding.recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        addBinding.recyclerView.setLayoutManager(layoutManager);
        auth = FirebaseAuth.getInstance();

        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //addBinding.textMessage.setText(s);
            }
        });


        addBinding.deleteAllR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                painRecordViewModel.deleteAll();
            }
        });
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(PainRecordViewModel.class);
        painRecordViewModel.getUserData(auth.getCurrentUser().getEmail()).observe(requireActivity(), painRecords -> {
            adapter.setData(painRecords);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
}