package internship.june;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    EditText name,contact,email,password,confirmpassword;
    Button submit;
    TextView alreadyAccount;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        name = findViewById(R.id.signup_name);
        contact = findViewById(R.id.signup_contact);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmpassword = findViewById(R.id.signup_confirm_password);
        submit = findViewById(R.id.signup_signup);
        alreadyAccount = findViewById(R.id.signup_already_account);

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().trim().equals("")){
                    name.setError("Name Required");
                }else if(contact.getText().toString().trim().equals("")){
                    contact.setError("Contact No. Required");
                }
                else if(contact.getText().toString().trim().length()<10){
                    contact.setError("Valid Contact No. Required");
                }
                else if(email.getText().toString().trim().equals("")){
                    email.setError("Email Id Required");
                }
                else if(!email.getText().toString().trim().matches(emailPattern)){
                    email.setError("Valid Email Id Required");
                }
                else if(password.getText().toString().trim().equals("")){
                    password.setError("Password Required");
                }
                else if(password.getText().toString().trim().length()<6){
                    password.setError("Min.6 Char Password Required");
                }
                else if(confirmpassword.getText().toString().trim().equals("")){
                    confirmpassword.setError("Confirm Password Required");
                }
                else if(confirmpassword.getText().toString().trim().length()<6){
                    confirmpassword.setError("Min.6 Char Confirm Password Required");
                }
                else if(!password.getText().toString().trim().matches(confirmpassword.getText().toString().trim())){
                    confirmpassword.setError("Password Does Not Match");
                }
                else{
                    String selectQuery = "SELECT * FROM USERS WHERE EMAIL='"+email.getText().toString()+"' OR CONTACT='"+contact.getText().toString()+"'";
                    Cursor cursor = db.rawQuery(selectQuery,null);
                    if(cursor.getCount()>0){
                        Toast.makeText(SignupActivity.this, "Email Id/Contact No. Already Registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String insertQuery = "INSERT INTO USERS VALUES (NULL,'" + name.getText().toString() + "','" + email.getText().toString() + "','" + contact.getText().toString() + "','" + password.getText().toString() + "')";
                        db.execSQL(insertQuery);
                        Toast.makeText(SignupActivity.this, "Signup Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }
        });

    }
}