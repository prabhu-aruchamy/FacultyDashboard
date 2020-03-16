package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import in.thelordtech.facultydashboard.helpers.Utils;

public class FacultyProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    CircleImageView icon;
    Uri mImageUri;
    TextView facultyEmail, facultyNumber, facultyName, bioFaculty, educationFaculty, facultproj ;
    FirebaseAuth fAuth;
    DatabaseReference facultyDBref;
    private ProgressDialog progressDialog;
    String ProfileIconURL = "";
    String fproject = "";
    Button DownloadResume;
    LinearLayout UI_Container;

    private String phoneNumber, facultyBIO, facultyIconURL, isProfileUpdated, resumeDownloadLink, facultyEducationalDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);

        UI_Container = (LinearLayout)findViewById(R.id.UI_Container);
        UI_Container.setVisibility(View.GONE);
        icon = findViewById(R.id.chooseImage);
        facultyName = findViewById(R.id.facultyName);
        facultyEmail = findViewById(R.id.facultyEmail);
        facultyNumber = findViewById(R.id.facultyNumber);
        bioFaculty = findViewById(R.id.facultyBIO);
        educationFaculty = findViewById(R.id.facultyEducation);
        facultproj = findViewById(R.id.facultproj);
        DownloadResume = findViewById(R.id.DownloadResume);

        progressDialog = new ProgressDialog(FacultyProfileActivity.this);
        progressDialog.setMessage("Fetching Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fAuth = FirebaseAuth.getInstance();
        final String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        facultyDBref = FirebaseDatabase.getInstance().getReference("userProfile");
        facultyName.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName());
        facultyEmail.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());

        facultyDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String UID = String.valueOf(ds.child("ProfileID").getValue());
                    if(UID.equals(userID)){
                        isProfileUpdated = String.valueOf(ds.child("isProfileUpdatedFully").getValue());
                        if(isProfileUpdated.equals("0")){
                            Intent i = new Intent(FacultyProfileActivity.this, EditFacultyProfile.class);
                            i.putExtra("facultyName",fAuth.getCurrentUser().getDisplayName());
                            i.putExtra("facultyEmail",fAuth.getCurrentUser().getEmail());
                            progressDialog.dismiss();
                            startActivity(i);
                            finish();
                        }else {
                            UI_Container.setVisibility(View.VISIBLE);
                            phoneNumber = String.valueOf(ds.child("ContactNumber").getValue());
                            facultyBIO = String.valueOf(ds.child("Bio").getValue());
                            facultyEducationalDetails = String.valueOf(ds.child("EducationalDetails").getValue());
                            facultyIconURL = String.valueOf(ds.child("IconURL").getValue());
                            fproject = String.valueOf(ds.child("Projects").getValue());
                            Picasso.get().load(facultyIconURL).into(icon); //Load Faculty Icon into Image View
                            resumeDownloadLink = String.valueOf(ds.child("ResumeDownloadLink").getValue());

                            if(resumeDownloadLink.equals("NU") || resumeDownloadLink.equals("")){ //If resume link is not given
                                DownloadResume.setEnabled(false);
                                DownloadResume.setBackgroundColor(Color.GRAY);
                                DownloadResume.setText("Resume Not Updated");
                            }

                            facultyNumber.setText(phoneNumber);
                            bioFaculty.setText(facultyBIO);
                            facultproj.setText(fproject);
                            educationFaculty.setText(facultyEducationalDetails);

                            progressDialog.dismiss();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(FacultyProfileActivity.this, "ERROR: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        DownloadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = resumeDownloadLink;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        facultyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailApp(facultyEmail.getText().toString());
            }
        });

        facultyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OpenPhoneApp(facultyNumber.getText().toString());
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FacultyProfileActivity.this, EditFacultyProfile.class);
                i.putExtra("Contact",facultyNumber.getText().toString());
                i.putExtra("BIO",bioFaculty.getText().toString());
                i.putExtra("Education",educationFaculty.getText().toString());
                i.putExtra("Projects",facultproj.getText().toString());
                i.putExtra("ResumeLink",resumeDownloadLink);
                i.putExtra("IconURL",facultyIconURL);
                startActivity(i);
            }
        });
    }

    private void openEmailApp(String emailID) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailID}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Summa Subject!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(emailIntent);
        }catch (Exception e){
            Toast.makeText(this, "Please Update/Configure Gmail App", Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenPhoneApp(String number) {
        try {
            int phno = Integer.parseInt(number);
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:"+String.valueOf(number)));
            startActivity(i);
        }catch (Exception e){
            Utils.showToast(FacultyProfileActivity.this, e.getMessage());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.faculty_update_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.editProfile) {
            Intent editProfile = new Intent(FacultyProfileActivity.this, EditFacultyProfile.class);
            editProfile.putExtra("Contact",facultyNumber.getText().toString());
            editProfile.putExtra("BIO",bioFaculty.getText().toString());
            editProfile.putExtra("Education",educationFaculty.getText().toString());
            editProfile.putExtra("Projects",facultproj.getText().toString());
            editProfile.putExtra("ResumeLink",resumeDownloadLink);
            editProfile.putExtra("IconURL",facultyIconURL);
            startActivity(editProfile);
        }
        return super.onOptionsItemSelected(item);
    }
}
