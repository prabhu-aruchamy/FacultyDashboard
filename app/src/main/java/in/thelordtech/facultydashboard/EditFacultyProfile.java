package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.thelordtech.facultydashboard.helpers.Utils;

public class EditFacultyProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_DOCUMENT_REQUEST = 2;

    CircleImageView fImage;
    EditText editNumber, editBio, editEducation,fName, fEmail,fprojects;
    EditText uploadResume;
    Button updateProifle;
    FirebaseAuth firebaseAuth;
    private Uri mImageUri;
    int ProfileIconFlag = 0;
    private StorageReference mStorageRef;
    String DownloadableIconLink;
    DatabaseReference updateFacultyProfile;
    Uri mDocumentURI;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_faculty_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();

        fImage = (CircleImageView)findViewById(R.id.chooseProfileImage);
        fName =  findViewById(R.id.fName);
        fEmail = findViewById(R.id.editFacultyEmail);
        editNumber =  findViewById(R.id.editFacultyNumber);
        editBio =  findViewById(R.id.editFacultyBIO);
        editEducation = findViewById(R.id.editFacultyEducation);
        uploadResume = findViewById(R.id.uploadResume);
        updateProifle = findViewById(R.id.UpdateProfile);
        fprojects = findViewById(R.id.facultyProjects);

        if((getIntent().getStringExtra("Contact") != null) && (getIntent().getStringExtra("BIO") != null && (getIntent().getStringExtra("Education")!=null))){

            Picasso.get().load(getIntent().getStringExtra("IconURL")).into(fImage);
            editBio.setText(getIntent().getStringExtra("BIO"));
            editEducation.setText(getIntent().getStringExtra("Education"));
            editNumber.setText(getIntent().getStringExtra("Contact"));
            fprojects.setText(getIntent().getStringExtra("Projects"));
            uploadResume.setText(getIntent().getStringExtra("ResumeLink"));
            mImageUri = Uri.parse(getIntent().getStringExtra("IconURL"));

            ProfileIconFlag = 2;
        }

        progressDialog = new ProgressDialog(EditFacultyProfile.this);
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        fName.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName());
        fEmail.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()));


        fImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGalleryToPickImage();
            }
        });

        updateProifle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editNumber.getText().toString().isEmpty() || editBio.getText().toString().isEmpty() || editEducation.getText().toString().isEmpty() || uploadResume.getText().toString().isEmpty() || fprojects.getText().toString().isEmpty()){
                    Toast.makeText(EditFacultyProfile.this, "Enter All Fields!", Toast.LENGTH_SHORT).show();
                }else if(editNumber.getText().toString().length()<10){
                    Toast.makeText(EditFacultyProfile.this, "Phone Number Must be 10 digits!", Toast.LENGTH_SHORT).show();
                }else if(ProfileIconFlag == 0){
                    Toast.makeText(EditFacultyProfile.this, "Select Profile Image", Toast.LENGTH_SHORT).show();
                }else if(uploadResume.getText().toString().length() < 10 || !(uploadResume.getText().toString().contains(".")) || !(uploadResume.getText().toString().contains("http"))){
                    Toast.makeText(EditFacultyProfile.this, "Not a Valid Resume Link!", Toast.LENGTH_SHORT).show();
                } else{
                    UpdateProfile(editNumber.getText().toString(), editBio.getText().toString(), editEducation.getText().toString());
                }
            }
        });

    }



    private void UpdateProfile(String number, String bio, String education){
        if(ProfileIconFlag == 1) {
            uploadIconandUserDetails(number, bio, education);
        }else if(ProfileIconFlag == 2){
            updateDetails();
        }
    }

    private void updateDetails() {

        updateFacultyProfile = FirebaseDatabase.getInstance().getReference("userProfile");

        Map updatedProfile = new HashMap<>();

        updatedProfile.put("ContactNumber", editNumber.getText().toString());
        updatedProfile.put("Bio", editBio.getText().toString());
        updatedProfile.put("EducationalDetails", editEducation.getText().toString());
        updatedProfile.put("Projects",fprojects.getText().toString());
        updatedProfile.put("ResumeDownloadLink",uploadResume.getText().toString());
        updatedProfile.put("isProfileUpdatedFully","1");

        updateFacultyProfile.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).updateChildren(updatedProfile);

        Toast.makeText(this, "Intent: Profile Updated!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(EditFacultyProfile.this, FacultyProfileActivity.class);
        startActivity(i);
        finish();


    }

    private void uploadIconandUserDetails(final String Number, final String Bio, final String Education) {
        if (firebaseAuth.getCurrentUser() != null) {
            progressDialog.show();

            progressDialog.setMessage("Uploading Icon...");
            mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference riversRef = mStorageRef.child("Faculty Icon/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "." + Utils.getIconImageExtension(EditFacultyProfile.this, mImageUri));

            riversRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DownloadableIconLink = uri.toString();
                            UpdateFacultyProfile(Number, Bio, Education, DownloadableIconLink);
                            Toast.makeText(EditFacultyProfile.this, "Upload Successful ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DownloadableIconLink = "https://pishvazasia.com/wp-content/uploads/2011/07/op/profile-default-male-e1461603546697.png";
                            UpdateFacultyProfile(Number, Bio, Education, DownloadableIconLink);
                            e.printStackTrace();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    DownloadableIconLink = "https://pishvazasia.com/wp-content/uploads/2011/07/op/profile-default-male-e1461603546697.png";
                    UpdateFacultyProfile(Number, Bio, Education, DownloadableIconLink);
                    Toast.makeText(EditFacultyProfile.this, "Error1: " + exception.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(this, "Sign in Error!", Toast.LENGTH_SHORT).show();
        }

    }

    private void UpdateFacultyProfile(String number, String bio, String education, String downloadableIconLink) {

        updateFacultyProfile = FirebaseDatabase.getInstance().getReference("userProfile");

        Map updatedProfile = new HashMap<>();

        updatedProfile.put("ContactNumber",number);
        updatedProfile.put("Bio",bio);
        updatedProfile.put("EducationalDetails",education);
        updatedProfile.put("IconURL",downloadableIconLink);
        updatedProfile.put("ResumeDownloadLink",uploadResume.getText().toString());
        updatedProfile.put("Projects",fprojects.getText().toString());
        updatedProfile.put("isProfileUpdatedFully","1");

        updateFacultyProfile.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).updateChildren(updatedProfile);

        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(EditFacultyProfile.this, FacultyProfileActivity.class);
        startActivity(i);
        finish();


    }

    private void OpenGalleryToPickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(fImage);
            ProfileIconFlag = 1;
        }
    }
}
