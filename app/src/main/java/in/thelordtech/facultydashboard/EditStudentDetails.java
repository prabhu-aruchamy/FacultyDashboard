package in.thelordtech.facultydashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditStudentDetails extends AppCompatActivity {


    Button button;
    TextView tvname, tvid;
    EditText etmarks, etattendance;
    String studentname, studentid, courseid, totalclasses;
    DatabaseReference mRef, fUID, studentID;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_details);

        Bundle b = getIntent().getExtras();
        studentname = b.getString("studentname");
        studentid = b.getString("studentid");
        courseid = b.getString("courseid");
        totalclasses = b.getString("totalclasses");

        tvname = findViewById(R.id.tvname);
        tvid = findViewById(R.id.tvid);
        etmarks= findViewById(R.id.etmarks);
        etattendance = findViewById(R.id.etattendance);
        button = findViewById(R.id.button);

        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
       fAuth = FirebaseAuth.getInstance();
        fUID = mRef.child(fAuth.getCurrentUser().getUid());
        //fUID = mRef.child("id1");
        studentID = fUID.child(courseid).child(studentid);

        tvname.setText(studentname);
        tvid.setText(studentid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                updateDetails( (etmarks.getText()).toString(), (etattendance.getText()).toString());
            }
        });
    }

    private void updateDetails(String newMarks, String newAttendance)
    {

        if (newMarks.isEmpty() || newAttendance.isEmpty())
        {
            Toast.makeText(EditStudentDetails.this
                    , "Fill in both the details", Toast.LENGTH_SHORT).show();
        }

        else if ( !(isNumeric(newMarks)) || !(isNumeric(newAttendance)) )
        {
            Toast.makeText(EditStudentDetails.this
                    , "Fill Numeric Values", Toast.LENGTH_SHORT).show();
        }

        else if( ( Integer.parseInt(newMarks ) > 100) || (Integer.parseInt(newMarks ) < 0) )
        {
            Toast.makeText(EditStudentDetails.this
                    , "Invalid marks", Toast.LENGTH_SHORT).show();
        }

        else if( ( ( Integer.parseInt(newAttendance ) > Integer.parseInt(totalclasses))
                || (Integer.parseInt(newAttendance ) < 0) ) )
        {
            Toast.makeText(EditStudentDetails.this
                    , "Invalid attendance", Toast.LENGTH_SHORT).show();
        }

        else
        {
            studentID.child("marks").setValue(newMarks);
            studentID.child("attendance").setValue(newAttendance);
            Toast.makeText(EditStudentDetails.this, "Changes saved", Toast.LENGTH_SHORT).show();
        }

    }


    public static boolean isNumeric(final String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        try {

            Integer.parseInt(str);
            return true;

        } catch (Exception e) {
            return false;
        }

    }

}
