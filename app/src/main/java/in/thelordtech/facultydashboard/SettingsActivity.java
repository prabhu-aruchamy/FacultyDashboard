package in.thelordtech.facultydashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Switch aSwitch;
    DatabaseReference dbRef;
    FirebaseAuth fAuth;
    SharedPreferences preferences;
    TextView switchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        aSwitch = findViewById(R.id.switchPrivate);
        switchText = findViewById(R.id.switchText);
        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        String isPrivate = preferences.getString("isProfilePrivate", "0");

        if(isPrivate.equals("1")){
            aSwitch.setChecked(true);
            switchText.setText("Make Your Profile Public   ");
        }else{
            aSwitch.setChecked(false);
            switchText.setText("Make Your Profile Private   ");
        }

        fAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("userProfile").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    makeProfilePrivate();
                }else {
                    makeProfilePublic();
                }

            }
        });
    }

    private void makeProfilePrivate() {

        dbRef.child("isProfilePrivate").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("isProfilePrivate","1");
                    editor.apply();
                    switchText.setText("Make Your Profile Public   ");
                    Toast.makeText(SettingsActivity.this, "Profile Made Private!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SettingsActivity.this, "Can't do this operation Now!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void makeProfilePublic() {

        dbRef.child("isProfilePrivate").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("isProfilePrivate","0");
                    editor.apply();
                    switchText.setText("Make Your Profile Private   ");
                    Toast.makeText(SettingsActivity.this, "Profile Made Public!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SettingsActivity.this, "Can't do this operation Now!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
