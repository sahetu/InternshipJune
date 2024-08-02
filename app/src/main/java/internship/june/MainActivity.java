package internship.june;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button signin;
    EditText email,password;
    TextView forgotPassword,createAccount;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;

    ImageView hideIv,showIv;

    SharedPreferences sp;
    ApiInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        signin = findViewById(R.id.main_signin);
        email = findViewById(R.id.main_email);
        password = findViewById(R.id.main_password);

        hideIv = findViewById(R.id.main_password_hide);
        showIv = findViewById(R.id.main_password_show);

        hideIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIv.setVisibility(View.GONE);
                showIv.setVisibility(View.VISIBLE);

                password.setTransformationMethod(null);

            }
        });

        showIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIv.setVisibility(View.VISIBLE);
                showIv.setVisibility(View.GONE);

                password.setTransformationMethod(new PasswordTransformationMethod());

            }
        });

        forgotPassword = findViewById(R.id.main_forgot_password);
        createAccount = findViewById(R.id.main_create_account);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
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
                    password.setError("Min. 6 Char Password Required");
                }
                else {
                    //doLoginSqlite(view);
                    if(new ConnectionDetector(MainActivity.this).networkConnected()){
                        //new doLogin().execute();
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        doLoginRetrofit();
                    }
                    else{
                        new ConnectionDetector(MainActivity.this).networkDisconnected();
                    }
                }
            }
        });

    }

    private void doLoginRetrofit() {
        Call<GetLoginData> call = apiInterface.doLoginData(email.getText().toString(),password.getText().toString());
        call.enqueue(new Callback<GetLoginData>() {
            @Override
            public void onResponse(Call<GetLoginData> call, Response<GetLoginData> response) {
                pd.dismiss();
                if(response.code()==200){
                    if(response.body().status){
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        for(int i=0;i<response.body().userDetails.size();i++){
                            sp.edit().putString(ConstantSp.USERID,response.body().userDetails.get(i).userid).commit();
                            sp.edit().putString(ConstantSp.NAME,response.body().userDetails.get(i).name).commit();
                            sp.edit().putString(ConstantSp.EMAIL,response.body().userDetails.get(i).email).commit();
                            sp.edit().putString(ConstantSp.CONTACT,response.body().userDetails.get(i).contact).commit();
                            sp.edit().putString(ConstantSp.PASSWORD,"").commit();
                        }
                        Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Serever Error Code : "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetLoginData> call, Throwable t) {
                pd.dismiss();
                Log.d("RESPONSE_ERROR",t.getMessage());
            }
        });
    }

    private void doLoginSqlite(View view) {
        String selectQuery = "SELECT * FROM USERS WHERE (EMAIL='"+email.getText().toString()+"' OR CONTACT='"+email.getText().toString()+"') AND PASSWORD='"+password.getText().toString()+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                sp.edit().putString(ConstantSp.USERID,cursor.getString(0)).commit();
                sp.edit().putString(ConstantSp.NAME,cursor.getString(1)).commit();
                sp.edit().putString(ConstantSp.EMAIL,cursor.getString(2)).commit();
                sp.edit().putString(ConstantSp.CONTACT,cursor.getString(3)).commit();
                sp.edit().putString(ConstantSp.PASSWORD,cursor.getString(4)).commit();
            }

            System.out.println("Login Successfully");
            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Login Successfully", Snackbar.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(MainActivity.this, "Invalid Email Id/Password", Toast.LENGTH_SHORT).show();
        }
    }

    private class doLogin extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("email",email.getText().toString());
            hashMap.put("password",password.getText().toString());
            return new MakeServiceCall().MakeServiceCall(ConstantSp.LOGIN_URL,MakeServiceCall.POST,hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getBoolean("status")){
                    Toast.makeText(MainActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONArray array = object.getJSONArray("UserDetails");
                    for(int i=0; i<array.length();i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        sp.edit().putString(ConstantSp.USERID,jsonObject.getString("userid")).commit();
                        sp.edit().putString(ConstantSp.NAME,jsonObject.getString("name")).commit();
                        sp.edit().putString(ConstantSp.EMAIL,jsonObject.getString("email")).commit();
                        sp.edit().putString(ConstantSp.CONTACT,jsonObject.getString("contact")).commit();
                        sp.edit().putString(ConstantSp.PASSWORD,"").commit();
                    }
                    Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}