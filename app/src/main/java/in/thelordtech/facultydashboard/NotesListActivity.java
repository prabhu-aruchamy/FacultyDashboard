package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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

public class NotesListActivity extends AppCompatActivity {

    ArrayList<String> NotesTitle = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView notesList;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference fnotesDataBaseReference;
    private ProgressDialog progressDialog;
    private TextView content;
    private String arr[]=new String[500];
    String noteID;
    private  TextView indexImage;
    String shContent,shTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...\nLoading Notes From DataBase...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        notesList = (ListView) findViewById(R.id.notes_list_view);

        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        Toast.makeText(this, "User: "+ Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();


        fnotesDataBaseReference = database.getReference("Notes").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());//goes upto
        adapter = new ArrayAdapter<String>(this,R.layout.task_list_row,R.id.noteTitleFB,NotesTitle);
        fnotesDataBaseReference.addValueEventListener(new ValueEventListener() { //error line
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String count = String.valueOf(dataSnapshot.getChildrenCount());
                int count1 = Integer.parseInt(count);

                if (count1 == 0){

                    Intent intent = new Intent(NotesListActivity.this,AddNoteActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Load(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error","ERROR!");
                Toast.makeText(NotesListActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void Load(final DataSnapshot dataSnapshot){

        NotesTitle.clear();

        try {
            int i = 0;
            for (DataSnapshot ds: dataSnapshot.getChildren())
            {
                String isImportant = String.valueOf(ds.child("isHidden").getValue());

                if(isImportant.equals("0")){
                    NotesTitle.add(String.valueOf(ds.child("Title").getValue()));
                    arr[i]=String.valueOf(ds.child("Noteid").getValue());
                    i++;
                }

            }
            progressDialog.dismiss();

        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(NotesListActivity.this, "DataBase Error\nPlease Try Again After Sometime!", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
        notesList.setAdapter(adapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                noteID =arr[i];
                Log.d("qwerty i: ",i+"");


                String Title = String.valueOf(dataSnapshot.child(noteID).child("Title").getValue());
                String Content = String.valueOf(dataSnapshot.child(noteID).child("Content").getValue());

                Intent intent = new Intent(NotesListActivity.this,ViewNoteActivity.class);
                intent.putExtra("title", Title);
                intent.putExtra("content", Content);
                intent.putExtra("noteID", noteID);
                startActivity(intent);
            }
        });

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                noteID = arr[i];
                registerForContextMenu(adapterView);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_notes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.addnotes:
                Intent noteintent = new Intent(NotesListActivity.this,AddNoteActivity.class);
                startActivity(noteintent);
                break;

            case R.id.refreshh:
                if(isOnline()){
                    Intent refreshintent = new Intent(NotesListActivity.this,NotesListActivity.class);
                    startActivity(refreshintent);
                    finish();
                }
                else {
                    Toast.makeText(NotesListActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.delete_note_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_menu:
                if (isOnline()){
                    deletenoteonMenuClick(noteID);
                }
                else {
                    Toast.makeText(NotesListActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_note:
                ShareNotesInUsingOtherApps(noteID);
                break;

            case R.id.fav_note:
                AddNotetoImportantList(noteID);
                break;

            case R.id.hide_note:
                HideNote(noteID);
            default: return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private void HideNote(String noteID) {
        fnotesDataBaseReference.child(noteID).child("isHidden").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(NotesListActivity.this, "Note Hidden!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NotesListActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void AddNotetoImportantList(String noteID) {

        fnotesDataBaseReference.child(noteID).child("isImportant").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(NotesListActivity.this, "Marked as Important!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NotesListActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }




    private void ShareNotesInUsingOtherApps(final String noteID) {
        fnotesDataBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shTitle = String.valueOf(dataSnapshot.child(noteID).child("Title").getValue());
                shContent = String.valueOf(dataSnapshot.child(noteID).child("Content").getValue());
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shTitle+"\n"+shContent);
                startActivity(Intent.createChooser(sharingIntent, " Share Using..."));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void deletenoteonMenuClick(String noteID) {
        fnotesDataBaseReference.child(noteID).removeValue();
        Toast.makeText(this, "Note Deleted Sucessfully", Toast.LENGTH_SHORT).show();
    }

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connManager != null;
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
