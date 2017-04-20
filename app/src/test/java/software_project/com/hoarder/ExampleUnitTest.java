package software_project.com.hoarder;

import android.app.Activity;
import android.widget.EditText;

import org.junit.Test;

import java.util.Objects;

import software_project.com.hoarder.Activity.LoginActivity;
import software_project.com.hoarder.Object.Item;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for the EmailValidator logic.
 */
public class ExampleUnitTest extends LoginActivity{

    EditText emailText = (EditText) findViewById(R.id.input_email);
    EditText passwordText = (EditText) findViewById(R.id.input_password);

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        emailText.setText("test@test.com");
        passwordText.setText("notAPassword");
        login();
        assertFalse(statusCode.equals("200"));
    }
}