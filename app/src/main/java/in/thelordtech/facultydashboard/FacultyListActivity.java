package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FacultyListActivity extends AppCompatActivity {

    private ListView fac_list;
    private List<Faculty> facultyarrayList = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference facultyDBRef;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);

        fac_list = findViewById(R.id.fac_list);

        progressDialog = new ProgressDialog(FacultyListActivity.this);
        progressDialog.setMessage("Fetching Faculty List...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        facultyDBRef = FirebaseDatabase.getInstance().getReference("userProfile");

        facultyDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                facultyarrayList.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String isProfileFullyUpdated = String.valueOf(ds.child("isProfileUpdatedFully").getValue());
                    String isProfilePrivate = String.valueOf(ds.child("isProfilePrivate").getValue());

                    if(isProfileFullyUpdated.equals("1") && isProfilePrivate.equals("0")){   //only adds the faculty who has updated their profile fully!
                        String ficon, fname, fuid, femail, fnum, fbio, fedu, fproj, fresume;

                        ficon = String.valueOf(ds.child("IconURL").getValue());
                        fname = String.valueOf(ds.child("Name").getValue());
                        fuid = String.valueOf(ds.child("ProfileID").getValue());
                        femail = String.valueOf(ds.child("Email").getValue());
                        fnum = String.valueOf(ds.child("ContactNumber").getValue());
                        fbio = String.valueOf(ds.child("Bio").getValue());
                        fedu = String.valueOf(ds.child("EducationalDetails").getValue());
                        fproj = String.valueOf(ds.child("Projects").getValue());
                        fresume = String.valueOf(ds.child("ResumeDownloadLink").getValue());

                        facultyarrayList.add(new Faculty(
                                ficon, fname, fuid, femail, fnum, fbio, fedu, fproj, fresume
                        ));
                    }
                }

                FacultyListAdapter adapter = new FacultyListAdapter(FacultyListActivity.this, R.layout.faculty_list_item, facultyarrayList);
                fac_list.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();

            }
        });


        fac_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Faculty dataModel = facultyarrayList.get(position);
                Intent intent = new Intent(FacultyListActivity.this, View_Profile_Activity.class);
                intent.putExtra("ficon", dataModel.getFacultyIconLink());
                intent.putExtra("fname", dataModel.getFacutyName());
                intent.putExtra("femail", dataModel.getFacultyEmail());
                intent.putExtra("fnum", dataModel.getFacultyNumber());
                intent.putExtra("fbio", dataModel.getFacultyBio());
                intent.putExtra("fedu", dataModel.getFacultyEducation());
                intent.putExtra("fproj", dataModel.getFacultyProjects());
                intent.putExtra("fresume", dataModel.getFacultyResume());
                startActivity(intent);

            }
        });






    }
}
