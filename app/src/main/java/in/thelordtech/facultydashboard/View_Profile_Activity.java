package in.thelordtech.facultydashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_Profile_Activity extends AppCompatActivity {

    CircleImageView fIcon;
    TextView fname, femail, fnum, fbio, fedu, fproj;
    Button viewResume;
    String icon, name,email,num,bio,edu,proj,resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fprofile);

        Objects.requireNonNull(getSupportActionBar()).hide();

        fIcon = findViewById(R.id.fimage);
        fname = findViewById(R.id.fName);
        femail = findViewById(R.id.fEmail);
        fnum = findViewById(R.id.fNumber);
        fbio = findViewById(R.id.fBIO);
        fedu = findViewById(R.id.fEducation);
        fproj = findViewById(R.id.fproj);
        viewResume = findViewById(R.id.viewResume);

        icon = getIntent().getStringExtra("ficon");
        Picasso.get().load(icon).into(fIcon);
        name = getIntent().getStringExtra("fname");
        email = getIntent().getStringExtra("femail");
        num = getIntent().getStringExtra("fnum");
        bio = getIntent().getStringExtra("fbio");
        edu = getIntent().getStringExtra("fedu");
        proj = getIntent().getStringExtra("fproj");
        resume = getIntent().getStringExtra("fresume");

        fname.setText(name);
        femail.setText(email);
        fnum.setText(num);
        fbio.setText(bio);
        fedu.setText(edu);
        fproj.setText(proj);

        viewResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(resume));
                startActivity(i);
            }
        });
    }
}
