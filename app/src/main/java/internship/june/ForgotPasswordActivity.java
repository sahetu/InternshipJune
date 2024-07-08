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

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email,password,confirmpassword;
    Button submit;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        email = findViewById(R.id.forgot_password_email);
        password = findViewById(R.id.forgot_password_password);
        confirmpassword = findViewById(R.id.forgot_password_confirm_password);
        submit = findViewById(R.id.forgot_password_submit);
        

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().trim().equals("")){
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
                    /*Toast.makeText(ForgotPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();*/
                    String selectQuery = "SELECT * FROM USERS WHERE EMAIL='"+email.getText().toString()+"'";
                    Cursor cursor = db.rawQuery(selectQuery,null);
                    if(cursor.getCount()>0){
                        //password change
                        String updateQuery = "UPDATE USERS SET PASSWORD='"+password.getText().toString()+"' WHERE EMAIL='"+email.getText().toString()+"'";
                        db.execSQL(updateQuery);
                        Toast.makeText(ForgotPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else{
                        //Invalid Valid
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid Email Id", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
    }
}