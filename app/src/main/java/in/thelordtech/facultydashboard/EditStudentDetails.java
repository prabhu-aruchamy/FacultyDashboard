package in.thelordtech.facultydashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditStudentDetails extends AppCompatActivity {

    Button button;
    TextView tvname, tvid, tvtotalclasses;
    ImageView ivstudentpic;
    EditText etmarks, etattendance;
    String studentname, studentid, courseid, totalclasses;
    DatabaseReference mRef, fUID, studentIDref;
    int studentPic;
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
        studentPic = Integer.parseInt(b.getString("studentPic") );

        tvname = findViewById(R.id.tvname);
        ivstudentpic = findViewById(R.id.studentImage);
        tvtotalclasses = findViewById(R.id.tvtotalclasses);
        tvid = findViewById(R.id.tvid);
        etmarks= findViewById(R.id.etmarks);
        etattendance = findViewById(R.id.etattendance);
        button = findViewById(R.id.button);

        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        fAuth = FirebaseAuth.getInstance();
        fUID = mRef.child(fAuth.getCurrentUser().getUid());
        studentIDref = fUID.child(courseid).child(studentid);

        ivstudentpic.setImageResource(studentPic);
        tvname.setText(studentname);
        tvid.setText(studentid);
        tvtotalclasses.setText(totalclasses);

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

        if (emptyFields(newMarks, newAttendance))
        {
            Toast.makeText(EditStudentDetails.this
                    , "Fill in both the details", Toast.LENGTH_SHORT).show();
        }

        else if ( !(isNumeric(newMarks)) || !(isNumeric(newAttendance)) )
        {
            Toast.makeText(EditStudentDetails.this
                    , "Fill Numeric Values", Toast.LENGTH_SHORT).show();
        }

        else if( ( invalidMarks(newMarks)) )
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
            studentIDref.child("marks").setValue(newMarks);
            studentIDref.child("attendance").setValue(newAttendance);
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

    public boolean invalidMarks(String newMarks)
    {
        return ( Integer.parseInt(newMarks ) > 100) || (Integer.parseInt(newMarks ) < 0);
    }

    public boolean emptyFields(String newMarks, String newAttendance)
    {
        return ( newMarks.isEmpty() || newAttendance.isEmpty() );
    }


}
