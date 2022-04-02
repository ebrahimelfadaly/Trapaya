package com.example.tripplanner.Home.Activity;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.WorkerParameters;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripplanner.Adapter.ViewerPageAdapter;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.TripDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;


public class Home_Activity extends AppCompatActivity {

    public ViewPager2 viewPager;
    public static TripDatabase database;
    public static String fireBaseUserId;
    public static String fireBaseUserName;
    public static String fireBaseEmail;
    public static Uri fireBaseUserPhotoUri;
    TextView textTitle, textMessage;
    Button btnCancel,btnOk;

    WorkerParameters workerParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //frebase
        fireBaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fireBaseUserName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        fireBaseEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        fireBaseUserPhotoUri=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        //initalize DB
        database = Room.databaseBuilder(this, TripDatabase.class, "Trip").build();
        setContentView(R.layout.activity_home);
        initComponent();
        if (!Settings.canDrawOverlays(this)) {
            checkDrawOverAppsPermissionsDialog();
        }
        viewPager.getAdapter().notifyDataSetChanged();
    }
    private void initComponent() {
        TabLayout tabLayout = findViewById(R.id.main_home_tabLayout);
        viewPager = findViewById(R.id.main_home_pager);
        //initializing viewPager by my view adabter
        ViewerPageAdapter adapter = new ViewerPageAdapter(this);
        viewPager.setAdapter(adapter);
        //link tab with viewpager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            //give name in case of position
            switch (position) {
                case 0:
                    tab.setText("UpComing");
                    break;
                case 1:
                    tab.setText("History");
                    break;
                default:
                    tab.setText("profile");
                    break;
            }
        }).attach();
    }

    private void checkDrawOverAppsPermissionsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_permission_dialog,(ConstraintLayout) findViewById(R.id.dialogLayoutContainerp));
        builder.setView(view);
        textTitle =view.findViewById(R.id.textTitle);
        textTitle.setText(Final.APP_NAME);
        textMessage =view.findViewById(R.id.textMessage);
        textMessage.setText("Allow Draw Over Apps Permission to be able to use application probably");
        btnCancel=view.findViewById(R.id.btnCancel);
        btnCancel.setText(Final.DIALOG_CANCEL);
        btnOk=view.findViewById(R.id.btnOk);
        btnOk.setText(Final.DIALOG_OK);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        btnCancel.setOnClickListener(v -> {
            errorWarningForNotGivingDrawOverAppsPermissions();
            alertDialog.dismiss();
        });

        btnOk.setOnClickListener(v -> {
            drawOverAppPermission();
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
       alertDialog.show();
    }
    public void drawOverAppPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));

            startActivityForResult(intent, 0);
        }
    }



    private void errorWarningForNotGivingDrawOverAppsPermissions(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialog,(ConstraintLayout) findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Final.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Over other apps permission is not granted so the application might not behave properly" +
                " \n To enable this permission kindly restart the application");
        ((Button)view.findViewById(R.id.btnOk)).setText(Final.DIALOG_OK);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btnOk).setOnClickListener(v -> alertDialog.dismiss());

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


}

