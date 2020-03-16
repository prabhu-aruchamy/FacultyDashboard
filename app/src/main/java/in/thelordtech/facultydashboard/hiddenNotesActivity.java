package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class hiddenNotesActivity extends AppCompatActivity {

    ListView fav_list_view;
    TextView infoText;
    private ArrayList<String> ImpNotes = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    DatabaseReference impDataBaseReference;
    private FirebaseAuth fAuth;
    private String impID[] = new String[500];
    ProgressDialog progressDialog;
    String impNoteID;
    DataSnapshot DScopy;
    String iTitle, iContent, inoteID;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_notes);


        fav_list_view = findViewById(R.id.fav_list_view);
        infoText = findViewById(R.id.infoTexts);
        infoText.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(hiddenNotesActivity.this);
        progressDialog.setMessage("Fetching Hidden Notes...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fAuth = FirebaseAuth.getInstance();
        impDataBaseReference = FirebaseDatabase.getInstance().getReference("Notes").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());

        impDataBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ImpNotes.clear();
                DScopy = dataSnapshot;

                if (dataSnapshot.getChildrenCount() == 0) {
                    infoText.setVisibility(View.VISIBLE);
                } else {
                    int i = 0;
                    int flag = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String isHidden = String.valueOf(ds.child("isHidden").getValue());

                        if (!(isHidden.equals("0"))) {
                            ImpNotes.add(String.valueOf(ds.child("Title").getValue()));
                            impID[i] = String.valueOf(ds.child("Noteid").getValue());
                            flag = 1;
                            i++;
                        }
                    }

                    if (flag == 1) {
                        infoText.setVisibility(View.GONE);
                        adapter = new ArrayAdapter<String>(hiddenNotesActivity.this, R.layout.task_list_row, R.id.noteTitleFB, ImpNotes);
                        fav_list_view.setAdapter(adapter);
                    } else {
                        infoText.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(hiddenNotesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        fav_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                impNoteID = impID[position];

                iTitle = String.valueOf(DScopy.child(impNoteID).child("Title").getValue());
                iContent = String.valueOf(DScopy.child(impNoteID).child("Content").getValue());

                Intent intent = new Intent(hiddenNotesActivity.this, ViewNoteActivity.class);
                intent.putExtra("title", iTitle);
                intent.putExtra("content", iContent);
                intent.putExtra("noteID", impNoteID);
                startActivity(intent);

            }
        });

        fav_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                impNoteID = impID[position];
                registerForContextMenu(parent);
                return false;
            }
        });
    }
}
