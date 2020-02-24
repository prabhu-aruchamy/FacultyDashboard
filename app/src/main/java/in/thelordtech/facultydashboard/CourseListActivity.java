package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

import in.thelordtech.facultydashboard.helpers.Utils;

public class CourseListActivity extends AppCompatActivity {

    DatabaseReference mref, fUID;
    ListView lv;
    String selectedCourseId, selectedCourseName;
    String fUIDD;
    ProgressDialog progressDialog;
//    String courseid[] = {"15CSE311", "15CSE312", "15CSE313", "15MAT315", "15CSE432"};
//    String coursename[] = {"Compiler Design", "Computer Networks",
//            "Software Engineering", "Calculus", "Machine Learning"};

    HashMap<String,String> content ;
    List<Map<String, String>> messages;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        lv=findViewById(R.id.lv);

        progressDialog = new ProgressDialog(CourseListActivity.this);
        progressDialog.setMessage("Fetching Courses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference().child("courses").child(fAuth.getCurrentUser().getUid());

        //fUID = mref.child(fAuth.getCurrentUser().getUid());
        fUIDD = fAuth.getCurrentUser().getUid();
        //fUID = mref.child("id1");
        messages = new ArrayList<>();

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Utils.showToast(CourseListActivity.this, "Val: "+ds.getKey());

                    content = new HashMap<String, String>();
                    content.put("courseid", String.valueOf(ds.getKey()));
                    content.put("coursename", "demo");
                    messages.add(content);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showToast(CourseListActivity.this, databaseError.getMessage());
                progressDialog.dismiss();

            }
        });




//        for(int i = 0 ; i < courseid.length; i++) {
//            content = new HashMap<String, String>();
//            content.put("courseid", courseid[i]);
//            content.put("coursename", coursename[i]);
//            messages.add(content);
//        }
        String[] entry = new String[] {"courseid", "coursename"};
        SimpleAdapter adapter = new SimpleAdapter(this, messages,
                android.R.layout.simple_list_item_2,
                entry,
                new int[] {android.R.id.text1,
                        android.R.id.text2,
                });
        lv.setAdapter(adapter);
        progressDialog.dismiss();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(CourseListActivity.this, CourseDetails.class);
                    intent.putExtra("courseid", selectedCourseId);
                    intent.putExtra("coursename",selectedCourseName );
                    startActivity(intent);
                checkForCourse();

            }
        });

    }

    private void checkForCourse()
    {
//        fUID.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                if(dataSnapshot.hasChild(selectedCourseId))
//                {
//                    Log.d("MainActivity", ""+selectedCourseId);
//                    Intent intent = new Intent(CourseListActivity.this, CourseDetails.class);
//                    intent.putExtra("courseid", selectedCourseId);
//                    intent.putExtra("coursename",selectedCourseName );
//                    startActivity(intent);
//                }
//                else
//                {
//                    Log.d("MainActivity", ""+selectedCourseId);
//                    Toast.makeText(CourseListActivity.this
//                            , "Select the course which you teach"
//                            , Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });


    }




}

