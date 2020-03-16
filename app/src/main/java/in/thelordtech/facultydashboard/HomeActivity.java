package in.thelordtech.facultydashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth fAuth;
    DatabaseReference updateRef;
    String whatsNew = "";
    int VolumeUpCounter = 0;
    int VolumeDownCounter = 0;
    int PasswordSum = 8;
    SharedPreferences preferences;
    String uservolumeUPCount, uservolumeDOWNCount;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Faculty Dashboard");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, FacultyListActivity.class);
                startActivity(i);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        fAuth = FirebaseAuth.getInstance();
        updateRef = FirebaseDatabase.getInstance().getReference("Update");
//        AppUpdater appUpdater = new AppUpdater(HomeActivity.this)
//                .setDisplay(Display.DIALOG)
//                .setUpdateFrom(UpdateFrom.GITHUB)
//                .setGitHubUserAndRepo("thelordtech", "FacultyDashboard");

//        appUpdater.start();

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

        uservolumeUPCount = preferences.getString("VolumeUpCount", "3");
        uservolumeDOWNCount = preferences.getString("VolumeDownCount", "5");

        checkUpdates();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void openImpNotesActivity(MenuItem item) {
        startActivity(new Intent(this, StarNotesActivity.class));
    }

    public void openAboutUsActivity(MenuItem item){
        startActivity(new Intent(this, AboutUsActivity.class));

    }

    public void openSettingsActivity(MenuItem item){
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void CheckForUpdates(MenuItem item){

        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String isUpdateAvaliable = String.valueOf(dataSnapshot.child("isUpdateAvailable").getValue());
                String availableVersion = String.valueOf(dataSnapshot.child("Version").getValue());
                whatsNew = String.valueOf(dataSnapshot.child("WhatsNew").getValue());
                String versionName = BuildConfig.VERSION_NAME;

                if(isUpdateAvaliable.equals("1") && !(versionName.equals(availableVersion))){
                    showAlertDialog("App Update Available!", "What's New\n\n"+whatsNew, "Update", "Later");
                }else {
                    showAlertDialog("No Update Avaliable!","Chill!! Your App is Up to Date😊😊","Close","");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUpdates() {

        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String isUpdateAvaliable = String.valueOf(dataSnapshot.child("isUpdateAvailable").getValue());
                String availableVersion = String.valueOf(dataSnapshot.child("Version").getValue());
                whatsNew = String.valueOf(dataSnapshot.child("WhatsNew").getValue());
                String versionName = BuildConfig.VERSION_NAME;

                if(isUpdateAvaliable.equals("1") && !(versionName.equals(availableVersion))){
                    showAlertDialog("App Update Available!", "What's New\n\n"+whatsNew, "Update", "Later");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showAlertDialog(String aTitle, String msg, final String pBtn, String nBtn) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(aTitle)
                .setMessage(msg)
                .setPositiveButton(pBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(pBtn.equals("Update")){
                            String url = "https://github.com/thelordtech/FacultyDashboard";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(nBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("chosen","later");
                    }
                }).show();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                VolumeDownCounter = VolumeDownCounter + 1;
                PasswordSum = PasswordSum - 1;
                //Toast.makeText(this, "Down Count: "+VolumeDownCounter, Toast.LENGTH_SHORT).show();
                checkForCorrectPassword();
                return true;
            }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                //Toast.makeText(this, "Up Pressed: "+VolumeUpCounter, Toast.LENGTH_SHORT).show();
                checkForCorrectPassword();
                VolumeUpCounter = VolumeUpCounter + 1;
                return true;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }

    private void checkForCorrectPassword() {

        if(VolumeUpCounter == Integer.parseInt(uservolumeUPCount) && VolumeDownCounter == Integer.parseInt(uservolumeDOWNCount)){
            VolumeUpCounter = 0;
            VolumeDownCounter = 0;
            Intent i = new Intent(HomeActivity.this, hiddenNotesActivity.class);
            startActivity(i);
        }
    }
}

