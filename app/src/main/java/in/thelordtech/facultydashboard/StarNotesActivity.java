package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import in.thelordtech.facultydashboard.helpers.Utils;

public class StarNotesActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_notes);

        fav_list_view = findViewById(R.id.fav_list_view);
        infoText = findViewById(R.id.infoTexts);
        infoText.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(StarNotesActivity.this);
        progressDialog.setMessage("Fetching Important Notes!");
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

                        String isImportant = String.valueOf(ds.child("isImportant").getValue());

                        if (isImportant.equals("1")) {
                            ImpNotes.add(String.valueOf(ds.child("Title").getValue()));
                            impID[i] = String.valueOf(ds.child("Noteid").getValue());
                            flag = 1;
                            i++;
                        }
                    }

                    if (flag == 1) {
                        infoText.setVisibility(View.GONE);
                        adapter = new ArrayAdapter<String>(StarNotesActivity.this, R.layout.task_list_row, R.id.noteTitleFB, ImpNotes);
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
                Toast.makeText(StarNotesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        fav_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                impNoteID = impID[position];

                iTitle = String.valueOf(DScopy.child(impNoteID).child("Title").getValue());
                iContent = String.valueOf(DScopy.child(impNoteID).child("Content").getValue());

                Intent intent = new Intent(StarNotesActivity.this, ViewNoteActivity.class);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.imp_fav_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu:
                if (Utils.isOnline(StarNotesActivity.this)) {
                    deletenoteonMenuClick(impNoteID);
                } else {
                    Toast.makeText(StarNotesActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_note:
                ShareNotesInUsingOtherApps(impNoteID);
                break;

            case R.id.rem_fav_note:
                RemoveNotesFromImportantList(impNoteID);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private void deletenoteonMenuClick(String noteID) {
        impDataBaseReference.child(noteID).removeValue();
        Toast.makeText(this, "Note Deleted Sucessfully", Toast.LENGTH_SHORT).show();
    }

    private void ShareNotesInUsingOtherApps(final String noteID) {
        impDataBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String shTitle = String.valueOf(dataSnapshot.child(noteID).child("Title").getValue());
                String shContent = String.valueOf(dataSnapshot.child(noteID).child("Content").getValue());
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shTitle + "\n" + shContent);
                startActivity(Intent.createChooser(sharingIntent, " Share Using..."));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void RemoveNotesFromImportantList(String noteID) {

        impDataBaseReference.child(noteID).child("isImportant").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(StarNotesActivity.this, "Removed From Favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StarNotesActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
