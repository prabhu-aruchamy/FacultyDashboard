package in.thelordtech.facultydashboard;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RequestAppointmentActivity extends AppCompatActivity {

    EditText dateEditText,timeEditText;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar fromTime = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);
        dateEditText = findViewById(R.id.from_date);
        timeEditText = findViewById(R.id.from_time);
    }

    public void setFromDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
                timeEditText.setText(hour + ":" + minutes + " " + timeSet);
            }
        }, hour,
                minute,
                false
        );
        timePickerDialog.show();
 }
}
