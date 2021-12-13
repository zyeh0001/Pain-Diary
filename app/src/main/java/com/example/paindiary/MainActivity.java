package com.example.paindiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.paindiary.databinding.ActivityMainBinding;
import com.example.paindiary.databinding.NavHeaderMainBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private PainRecordViewModel painRecordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);
        saveDataToFirebase();
        setSupportActionBar(binding.appBar.toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_pain_data,
                R.id.nav_daily_record,
                R.id.nav_reports,
                R.id.nav_map,
                R.id.nav_logout
        )
//to display the Navigation button as a drawer symbol,not being shown as an Up button
                .setOpenableLayout(binding.drawerLayout)
                .build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        //Sets up a NavigationView for use with a NavController.
        NavigationUI.setupWithNavController(binding.navView, navController);
        //Sets up a Toolbar for use with a NavController.
        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController,
                mAppBarConfiguration);

    }

    public void saveDataToFirebase() {

        painRecordViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()).create(PainRecordViewModel.class);
        painRecordViewModel.getAllPainRecords().observe(this, painRecords -> {
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("painRecord");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            for (PainRecord temp : painRecords) {

                //reference.child(String.valueOf(temp.uid)).setValue(temp);
                String strDate = dateFormat.format(temp.date);
                reference.child(strDate).setValue(temp);

            }
        });

    }

    public void logout(MenuItem item) {
        Log.i("logout",""+ FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));

        finish();
    }
}