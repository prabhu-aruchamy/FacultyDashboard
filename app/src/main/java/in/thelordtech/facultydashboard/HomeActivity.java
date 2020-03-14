package in.thelordtech.facultydashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
                Intent i = new Intent(HomeActivity.this, FacultyChatActivity.class);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                    showAlertDialog("No Update Avaliable!","Your App is Up to Date😊😊","Close","");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        //set what should happen when negative button is clicked
                        //Toast.makeText(getApplicationContext(),"Later",Toast.LENGTH_LONG).show();
                    }
                }).show();
    }
}
