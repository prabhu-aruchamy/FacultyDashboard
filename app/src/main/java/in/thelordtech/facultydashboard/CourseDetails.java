package in.thelordtech.facultydashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDetails extends AppCompatActivity {

    DatabaseReference mRef, fUID, courseID;
    /*    FirebaseAuth fAuth;*/
    String courseid, coursename;
    ListView lv;
    List<String> studentid = new ArrayList<>();
    List<String> studentname = new ArrayList<>();
    List<String> attendance = new ArrayList<>();
    List<String> totalclasses = new ArrayList<>();
    List<String> marks = new ArrayList<>();
    HashMap<String,String> content ;
    List<Map<String, String>> messages;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        lv =findViewById(R.id.list);
        Bundle b = getIntent().getExtras();
        courseid = b.getString("courseid");
        coursename = b.getString("coursename");
        messages = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference().child("courses");
        fAuth = FirebaseAuth.getInstance();
        fUID = mRef.child(fAuth.getCurrentUser().getUid());
        //fUID = mRef.child("id1");
        courseID = fUID.child(courseid);

        createListOfStudentsInSelectedCourse();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        studentid.clear();
        studentname.clear();
        attendance.clear();
        totalclasses.clear();
        marks.clear();
        courseID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot studentDetails: dataSnapshot.getChildren())
                {
                    studentid.add( studentDetails.child("studentid")
                            .getValue(String.class) );
                    studentname.add( studentDetails.child("studentname")
                            .getValue(String.class) );
                    attendance.add( studentDetails.child("attendance")
                            .getValue(String.class));
                    totalclasses.add( studentDetails.child("totalclasses")
                            .getValue(String.class));
                    marks.add(studentDetails.child("marks")
                            .getValue(String.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void createListOfStudentsInSelectedCourse()
    {
        courseID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot studentDetails: dataSnapshot.getChildren())
                {
                    studentid.add( studentDetails.child("studentid")
                            .getValue(String.class) );
                    studentname.add( studentDetails.child("studentname")
                            .getValue(String.class) );
                    attendance.add( studentDetails.child("attendance")
                            .getValue(String.class));
                    totalclasses.add( studentDetails.child("totalclasses")
                            .getValue(String.class));
                    marks.add(studentDetails.child("marks")
                            .getValue(String.class));

                }


                for(int i = 0 ; i < studentid.size(); i++) {
                    content = new HashMap<String, String>();
                    content.put("studentid", studentid.get(i));
                    content.put("studentname", studentname.get(i));
                    messages.add(content);
                }
                String[] entry = new String[] {"studentid", "studentname"};
                SimpleAdapter adapter = new SimpleAdapter(CourseDetails.this, messages,
                        android.R.layout.simple_list_item_2,
                        entry,
                        new int[] {android.R.id.text1,
                                android.R.id.text2,
                        });
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Toast.makeText(CourseDetails.this
                                , "Long Press for options", Toast.LENGTH_SHORT).show();
                    }
                });

                registerForContextMenu(lv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu
            , View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId())
        {
            case R.id.view:
                Intent intent1 = new Intent(CourseDetails.this, ViewStudentDetails.class);
                intent1.putExtra("studentid", studentid.get(info.position));
                intent1.putExtra("studentname", studentname.get(info.position));
                intent1.putExtra("courseid", courseid);
                startActivity(intent1);
                return super.onContextItemSelected(item);

            case R.id.edit:
                Intent intent2 = new Intent(CourseDetails.this, EditStudentDetails.class);
                intent2.putExtra("studentid", studentid.get(info.position));
                intent2.putExtra("studentname", studentname.get(info.position));
                intent2.putExtra("courseid", courseid);
                intent2.putExtra("totalclasses", totalclasses.get(info.position));
                startActivity(intent2);
                return super.onContextItemSelected(item);

            case R.id.markAttendance:
                Intent i3 = new Intent(CourseDetails.this, Mark_Attendance.class);
                i3.putExtra("studentid", studentid.get(info.position));
                i3.putExtra("studentname", studentname.get(info.position));
                i3.putExtra("courseid", courseid);
                i3.putExtra("attendance", attendance.get(info.position));
                i3.putExtra("totalclasses", totalclasses.get(info.position));
                i3.putExtra("marks", marks.get(info.position));
                startActivity(i3);
                return super.onContextItemSelected(item);

            default:
                return super.onContextItemSelected(item);
        }
    }

}
