package in.thelordtech.facultydashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    EditText VolumeUpCount, VolumeDownCount;
    int VolumeUpCounter = 0;
    int VolumeDownCounter = 0;
    int PasswordSum = 8;
    LinearLayout passwordchangeContainer;
    Button updatePasswordButton;

    String uservolumeUPCount, uservolumeDOWNCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        aSwitch = findViewById(R.id.switchPrivate);
        switchText = findViewById(R.id.switchText);
        passwordchangeContainer = findViewById(R.id.passwordchangeContainer);
        passwordchangeContainer.setVisibility(View.GONE);

        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        VolumeUpCount = findViewById(R.id.VolumeUpCount);
        VolumeDownCount = findViewById(R.id.VolumeDownCount);

        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        String isPrivate = preferences.getString("isProfilePrivate", "0");
        uservolumeUPCount = preferences.getString("VolumeUpCount", "3");
        uservolumeDOWNCount = preferences.getString("VolumeDownCount", "5");

        VolumeUpCount.setText(uservolumeUPCount);
        VolumeDownCount.setText(uservolumeDOWNCount);


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

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VolumeUpCount.getText().toString().isEmpty() || VolumeDownCount.getText().toString().isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Enter All Fields! ", Toast.LENGTH_SHORT).show();
                }else{
                    int vuc, vdc;
                    vuc = Integer.parseInt(VolumeUpCount.getText().toString());
                    vdc = Integer.parseInt(VolumeDownCount.getText().toString());

                    if(vdc == 0 || vuc == 0){
                        Toast.makeText(SettingsActivity.this, "Value Cannot be zero!", Toast.LENGTH_SHORT).show();
                    }else {
                        savePasswordToSharedPref(vuc, vdc);
                    }
                }

            }
        });
    }

    private void savePasswordToSharedPref(int vuc, int vdc) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isPasswordChanged", "1");
        editor.putString("VolumeUpCount", String.valueOf(vuc));
        editor.putString("VolumeDownCount", String.valueOf(vdc));
        editor.apply();
        Toast.makeText(this, "Password Updated!", Toast.LENGTH_SHORT).show();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            VolumeDownCounter = VolumeDownCounter + 1;
            PasswordSum = PasswordSum - 1;
            //Toast.makeText(this, "Down Count: "+VolumeDownCounter, Toast.LENGTH_SHORT).show();
            openChangePassword();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //Toast.makeText(this, "Up Pressed: "+VolumeUpCounter, Toast.LENGTH_SHORT).show();
            openChangePassword();
            VolumeUpCounter = VolumeUpCounter + 1;
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void openChangePassword() {
        if(VolumeUpCounter == Integer.parseInt(uservolumeUPCount) && VolumeDownCounter == Integer.parseInt(uservolumeDOWNCount)){
            passwordchangeContainer.setVisibility(View.VISIBLE);

        }

    }
}
