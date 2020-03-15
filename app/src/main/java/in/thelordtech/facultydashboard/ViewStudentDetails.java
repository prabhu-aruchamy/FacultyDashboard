package in.thelordtech.facultydashboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ViewStudentDetails extends AppCompatActivity {


    TextView tvname, tvid, tvmarks, tvattendance
            ,tvattendancepercent ;
    String studentname, studentid, courseid, marks, attendance, totalclasses;
    double attendancePercent;
    DatabaseReference mRef, fUID, studentIDref;
       FirebaseAuth fAuth;
    DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_details);

        Bundle b = getIntent().getExtras();
        studentname = b.getString("studentname");
        studentid = b.getString("studentid");
        courseid = b.getString("courseid");

        tvname = findViewById(R.id.tvname);
        tvid = findViewById(R.id.tvid);
        tvmarks = findViewById(R.id.tvmarks);
        tvattendance = findViewById(R.id.tvattendance);
        tvattendancepercent = findViewById(R.id.tvattendancepercent);

        tvname.setText(studentname);
        tvid.setText(studentid);

        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        fAuth = FirebaseAuth.getInstance();
        fUID = mRef.child(fAuth.getCurrentUser().getUid());
        //fUID = mRef.child("id1");
        studentIDref = fUID.child(courseid).child(studentid);
        //sets a student's details to their corresponding views
        displayStudentDetails();


    }

    private void displayStudentDetails()
    {
        studentIDref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                marks = dataSnapshot.child("marks").getValue(String.class);
                attendance = dataSnapshot.child("attendance").getValue(String.class);
                totalclasses = dataSnapshot.child("totalclasses").getValue(String.class);
                tvmarks.setText(marks);
                tvattendance.setText(attendance);
                attendancePercent = ( Integer.parseInt(attendance)*1.0/
                        Integer.parseInt(totalclasses) )*100;
                tvattendancepercent.setText(""+df2.format(attendancePercent));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
