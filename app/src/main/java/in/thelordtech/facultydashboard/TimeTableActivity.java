package in.thelordtech.facultydashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import in.thelordtech.facultydashboard.helpers.Utils;

public class TimeTableActivity extends AppCompatActivity {

    ImageView facultyTimeTable;
    DatabaseReference facultyTimeTableref;
    FirebaseAuth firebaseAuth;
    String TimetableLink = "";
    String uid = "";
    ProgressBar progress;
   // PhotoViewAttacher pAttacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        facultyTimeTable = findViewById(R.id.facultyTimeTable);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //pAttacher = new PhotoViewAttacher(facultyTimeTable);
        //pAttacher.update();
        LoadTimeTable();
    }

    private void LoadTimeTable() {
        facultyTimeTableref = FirebaseDatabase.getInstance().getReference("userProfile");

        facultyTimeTableref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    String UID = String.valueOf(ds.child("ProfileID").getValue());

                    if(UID.equals(uid)) {
                        TimetableLink = String.valueOf(ds.child("TimeTable").getValue());
                        setImagetoImageView(TimetableLink);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showToast(TimeTableActivity.this,"Error: "+databaseError.getMessage());
                progress.setVisibility(View.GONE);

            }
        });


    }

    private void setImagetoImageView(String timetableLink) {
        try {
            Picasso.get().load(timetableLink).into(facultyTimeTable);
            progress.setVisibility(View.GONE);

        }catch (Exception e){
            progress.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
