//package com.example.paindiary.workmanager;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.example.paindiary.databinding.DailyRecordFragmentBinding;
//import com.example.paindiary.entity.PainRecord;
//import com.example.paindiary.viewmodel.PainRecordViewModel;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//
//public class MyWorker extends Worker {
//    private PainRecordViewModel painRecordViewModel;
//    private AppBarConfiguration mAppBarConfiguration;
//    FirebaseDatabase rootNode;
//    DatabaseReference reference;
//    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        return null;
//    }
//
//    public void saveDataToFirebase(){
//
//        painRecordViewModel =
//                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplicationContext()).create(PainRecordViewModel.class);
//        painRecordViewModel.getAllPainRecords().observe(this, painRecords -> {
//            rootNode = FirebaseDatabase.getInstance();
//            reference = rootNode.getReference("painRecord");
//            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//            for (PainRecord temp : painRecords) {
//
//                //reference.child(String.valueOf(temp.uid)).setValue(temp);
//                String strDate = dateFormat.format(temp.date);
//                reference.child(strDate).setValue(temp);
//
//            }
//        });
//
//    }
//
//}
//
//
//
//    FragmentManager fragmentManager = getSupportFragmentManager();
//    NavHostFragment navHostFragment = (NavHostFragment)
//            fragmentManager.findFragmentById(R.id.nav_host_fragment);
//
//    NavController navController = navHostFragment.getNavController();
//NavigationUI.setupWithNavController(binding.navView, navController);
//        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController,
//        mAppBarConfiguration);
//
//// logout button listener
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(MenuItem -> {
//        logout(view);
//        return true;
//        });
//
//
//// FirebBase Upload
//        recordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RecordViewModel.class);
//
//        try {
//        List<Record> allRecordList = recordViewModel.getAllRecordList();
//        BackgroundRecordUploader.allRecords = allRecordList;
//        Log.i("aaaa", "data update to Back");
//        } catch (ExecutionException e) {
//        e.printStackTrace();
//        } catch (InterruptedException e) {
//        e.printStackTrace();
//        }
//
//        LiveData<List<Record>> allRecords = recordViewModel.getAllRecord();
//        allRecords.observe(this, new Observer<List<Record>>() {
//@Override
//public void onChanged(List<Record> records) {
//        BackgroundRecordUploader.allRecords = records;
//        }
//        });
//        Calendar c = Calendar.getInstance();
//
//
//@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
//        try {
//        String currentTimeString =sdf.format(c.getTime());//convert format first
//        Date currTime=sdf.parse(currentTimeString);//get the Date object of current time
//        Date uploadTime = sdf.parse("13:22:00");
//        long diffInMs=uploadTime.getTime()-currTime.getTime();//million seconds
//        long diffInSec=TimeUnit.MILLISECONDS.toSeconds(diffInMs);//seconds
//        Log.d("aaaaa", "uploadTime:"+String.valueOf(uploadTime.getTime()));
//        Log.d("aaaaa", "currentTime:"+String.valueOf(currTime.getTime()));
//        Log.d("aaaaa", "durati
