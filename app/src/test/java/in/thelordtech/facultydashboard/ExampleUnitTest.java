package in.thelordtech.facultydashboard;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void checkForNumericValue(){
        EditStudentDetails ed = new EditStudentDetails();
        boolean fun = ed.isNumeric("13241654");
        assertTrue(fun);
    }
    @Test
    public void checkForEmptyValue(){
        EditStudentDetails ed = new EditStudentDetails();
        boolean fun = ed.emptyFields("1243", "");
        assertTrue(fun);
    }

    @Test
    public void checkForValidMarks(){
        EditStudentDetails ed = new EditStudentDetails();
        boolean fun = ed.invalidMarks("98");
        assertTrue(fun);
    }

//
//    @Test
//    public void requestAppointment(){
//        RequestAppointmentActivity rqa = new RequestAppointmentActivity();
//        boolean out = rqa.isPM("PM");
//        assertTrue(out);
//
//    }
//    @Test
//    public void Start_End_Discrepancy(){
//        RequestAppointmentActivity rq = new RequestAppointmentActivity();
//        boolean out = rq.timedisc("10:30 AM","12:30 PM");
//        assertTrue(out);
//    }



}