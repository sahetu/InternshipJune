package internship.june;

import android.content.SharedPreferences;
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

public class ProfileActivity extends AppCompatActivity {

    EditText name,contact,email,password,confirmpassword;
    Button edit,submit;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;
    SharedPreferences sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        name = findViewById(R.id.profile_name);
        contact = findViewById(R.id.profile_contact);
        email = findViewById(R.id.profile_email);
        password = findViewById(R.id.profile_password);
        confirmpassword = findViewById(R.id.profile_confirm_password);
        edit = findViewById(R.id.profile_edit);
        submit = findViewById(R.id.profile_update);

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
                    String selectQuery = "SELECT * FROM USERS WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
                    Cursor cursor = db.rawQuery(selectQuery,null);
                    if(cursor.getCount()>0){
                        String updateQuery = "UPDATE USERS SET NAME='"+name.getText().toString()+"',EMAIL='"+email.getText().toString()+"',CONTACT='"+contact.getText().toString()+"',PASSWORD='"+password.getText().toString()+"' WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
                        db.execSQL(updateQuery);

                        sp.edit().putString(ConstantSp.NAME,name.getText().toString()).commit();
                        sp.edit().putString(ConstantSp.EMAIL,email.getText().toString()).commit();
                        sp.edit().putString(ConstantSp.CONTACT,contact.getText().toString()).commit();
                        sp.edit().putString(ConstantSp.PASSWORD,password.getText().toString()).commit();

                        setData(false);
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "Invalid UserId", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        setData(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData(true);
            }
        });

    }

    private void setData(boolean b) {
        name.setText(sp.getString(ConstantSp.NAME,""));
        email.setText(sp.getString(ConstantSp.EMAIL,""));
        contact.setText(sp.getString(ConstantSp.CONTACT,""));
        password.setText(sp.getString(ConstantSp.PASSWORD,""));
        confirmpassword.setText(sp.getString(ConstantSp.PASSWORD,""));

        name.setEnabled(b);
        email.setEnabled(b);
        contact.setEnabled(b);
        password.setEnabled(b);
        confirmpassword.setEnabled(b);

        if(b){
            confirmpassword.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }
        else{
            confirmpassword.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }

    }
}