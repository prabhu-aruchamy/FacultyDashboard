package in.thelordtech.facultydashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.thelordtech.facultydashboard.helpers.Utils;

public class FacultyProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    CircleImageView icon;
    Uri mImageUri;
    TextView facultyEmail, facultyNumber, facultyName;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);

        icon = findViewById(R.id.chooseImage);
        facultyName = findViewById(R.id.facultyName);
        facultyEmail = findViewById(R.id.facultyEmail);
        facultyNumber = findViewById(R.id.facultyNumber);

        fAuth = FirebaseAuth.getInstance();
        facultyName.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName());
        facultyEmail.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());

        facultyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailApp(facultyEmail.getText().toString());
            }
        });

        facultyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPhoneApp(facultyNumber.getText().toString());
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGalleryToPickImage();
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

            Picasso.get().load(mImageUri).into(icon);
        }
    }
}
