package in.thelordtech.facultydashboard;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RequestAppointmentActivity extends AppCompatActivity {

    EditText dateEditText,timeEditText, finEditText, content;
    private FirebaseAuth fAuth;
    private DatabaseReference fnotesDataBase;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar fromTime = Calendar.getInstance();
    private  int checkforclick;
    DataSnapshot datas;
    AlarmManager alarmManager;
    ProgressDialog progressDialog;
    String con;
    String t,h,mi,d,m,y;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    String dat, tim, etim;
    final Calendar myCalendar = Calendar. getInstance () ;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);
        dateEditText = findViewById(R.id.from_date);
        timeEditText = findViewById(R.id.from_time);
        Objects.requireNonNull(getSupportActionBar()).hide();
        finEditText = findViewById(R.id.to_time);
        content = findViewById(R.id.groundsEdittext);
        fAuth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        fnotesDataBase = FirebaseDatabase.getInstance().getReference().child("Schedule").child(fAuth.getCurrentUser().getUid());
    }
    String tel = "";

    public void setFromDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar. YEAR , year) ;
                myCalendar.set(Calendar. MONTH , month) ;
                myCalendar.set(Calendar. DAY_OF_MONTH , dayOfMonth) ;
                if (dayOfMonth<10)
                    d = 0+""+dayOfMonth+"";
                else
                    d = dayOfMonth+"";
                if(month<10)
                    m = 0+""+(month+1)+"";
                else
                    m = (month+1)+"";
                y = year+"";
                dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                fromDate.set(year, month, dayOfMonth);

            }
        }, fromDate.get(Calendar.YEAR),
                fromDate.get(Calendar.MONTH),
                fromDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void setFromTime(View view) {
        int hour = fromTime.get(Calendar.HOUR_OF_DAY);
        int minute = fromTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(RequestAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hour, int min) {
                fromTime.set(Calendar.HOUR_OF_DAY, hour);
                fromTime.set(Calendar.MINUTE, min);
                String timeSet = "";
                myCalendar.set(Calendar.HOUR_OF_DAY,hour);
                myCalendar.set(Calendar.MINUTE,min);
                if(hour<10)
                    h = 0+""+hour;
                else
                    h = hour+"";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String minutes;
                if (min < 10)
                    minutes = "0" + min;
                else
                    minutes = String.valueOf(min);
                mi = minutes;
                timeEditText.setText(hour + ":" + minutes + " " + timeSet);
            }
        }, hour,
                minute,
                false
        );
        timePickerDialog.show();
    }
    public void setToTime(View view) {
        int hour = fromTime.get(Calendar.HOUR_OF_DAY);
        int minute = fromTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(RequestAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hour, int min) {
                fromTime.set(Calendar.HOUR_OF_DAY, hour);
                fromTime.set(Calendar.MINUTE, min);
                String timeSet = "";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String minutes;
                if (min < 10)
                    minutes = "0" + min;
                else
                    minutes = String.valueOf(min);
                finEditText.setText(hour + ":" + minutes + " " + timeSet);
            }
        }, hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    public void submit(View view) {
        con = content.getText().toString().trim();
        dat = dateEditText.getText().toString().trim();
        tim = timeEditText.getText().toString().trim();
        etim = finEditText.getText().toString().trim();
        t = y+m+d+h+mi;
        if(!TextUtils.isEmpty(dat) && !TextUtils.isEmpty(con)&& !TextUtils.isEmpty(tim)) {

            if (isOnline())
                createNotes();

            else {
                Snackbar.make(view, "Please Connect to Internet", Snackbar.LENGTH_SHORT).show();
            }

        }
        else{
            Snackbar.make(view,"Enter All Fields",Snackbar.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBackPressed(){
        closeAddNote();
    }

    private void createNotes() {
        checkforclick++;

        if (fAuth.getCurrentUser() != null) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait.. Validating and Saving...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            try {
                final DatabaseReference newNoteref = fnotesDataBase.push();
                String eh,em, sh,sm,fs,ts;
                ts = etim.split(" ")[1];
                eh = etim.split(" ")[0].split(":")[0];
                em = etim.split(" ")[0].split(":")[1];
                fs = tim.split(" ")[1];
                sh = tim.split(" ")[0].split(":")[0];
                sm = tim.split(" ")[0].split(":")[1];
                if(ts.equals("PM")){
                    if(Integer.parseInt(eh)<12)
                        eh = String.valueOf(Integer.parseInt(eh)+12);
                }
                eh = eh+em;
                if(fs.equals("PM")){
                    if(Integer.parseInt(sh)<12)
                        sh = String.valueOf(Integer.parseInt(sh)+12);
                }
                sh = sh+sm;
                if(Integer.parseInt(sh)>=Integer.parseInt(eh)){
                    Toast.makeText(getApplicationContext(),"Start time should be before Endtime!!",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                fnotesDataBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        datas = dataSnapshot;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Boolean flag = true;
                String tem = tim.split(" ")[0];
                String nowsess = tim.split(" ")[1];
                String h, m;
                h = tem.split(":")[0];
                m = tem.split(":")[1];
                if(nowsess.equals("PM") &&Integer.parseInt(h)<12){
                        h = String.valueOf(Integer.parseInt(h)+12);
                }
                int nowtime = Integer.parseInt(h+m);
                for (DataSnapshot ds: datas.getChildren()){
                    if(ds!=null) {
                        String star = String.valueOf(ds.child("Time").getValue());
                        String end = String.valueOf(ds.child("EndTime").getValue());
                        String sess = star.split(" ")[1];
                        String sessend = end.split(" ")[1];
                        String hh,mm;
                        star = star.split(" ")[0];
                        hh = star.split(":")[0];
                        mm = star.split(":")[1];
                        int mmm = Integer.parseInt(mm.trim());
                        int start = Integer.parseInt(hh+mm);
                        if(sess.equals("PM")){
                            if(Integer.parseInt(hh)<12)
                                hh = String.valueOf(Integer.parseInt(hh)+12);

                        }
                        else if(Integer.parseInt(hh)==12){
                            hh = String.valueOf(00);
                        }
                        end = end.split(" ")[0];
                        hh = end.split(":")[0];
                        mm = end.split(":")[1];
                        if(sessend.equals("PM")){
                            if(Integer.parseInt(hh)<12)
                                hh = String.valueOf(Integer.parseInt(hh)+12);
                        }
                        int endt = Integer.parseInt(hh+""+mm);
                        String date = String.valueOf(ds.child("Date").getValue());
                        if(dat.equals(date)&&(nowtime>=start&&nowtime<=endt)) {
                            Log.d("maatikichi","clash");
                            flag = false;
                            break;
                        }
                    }
                }
                if(flag){

                    final Map notemap = new HashMap();
                    notemap.put("Sort", t);
                    notemap.put("Content", con);
                    notemap.put("Date", dat);
                    notemap.put("Time", tim);
                    notemap.put("EndTime",finEditText.getText().toString());
                    notemap.put("Noteid",newNoteref.getKey());
                    notemap.put("timestamp", ServerValue.TIMESTAMP);

                    Thread makeThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            newNoteref.setValue(notemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        checkforclick++;
                                        boolean alarm = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("ALARM"), PendingIntent.FLAG_NO_CREATE) == null);

                                        int hr = 13;
                                        int mn = 30;
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Schedule Added Sucessfully", Toast.LENGTH_SHORT).show();

                                        finish();

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    makeThread.start();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Schedule Already exists in this slot!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Click again for confirmation", Toast.LENGTH_SHORT).show();
                Log.d("erroar",e.getMessage());
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Not Signed in", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeAddNote(){

        con = content.getText().toString().trim();
        dat = dateEditText.getText().toString().trim();
        tim = timeEditText.getText().toString().trim();
        t = y+m+d+h+mi;
        if (!TextUtils.isEmpty(dat) || !TextUtils.isEmpty(con)|| !TextUtils.isEmpty(tim)){

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            builder1.setTitle("Exit without saving?");
            builder1.setMessage("Schedule will not be Saved.\nClick Save to save!!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if(isOnline()){
                                createNotes();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        }
        else {

            finish();
        }


    }
    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    public void goback(View view) {
        finish();
    }
}
