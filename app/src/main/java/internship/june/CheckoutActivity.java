package internship.june;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    EditText name,email,contact,address;
    Spinner city;
    RadioGroup radioGroup;
    Button payNow;

    SharedPreferences sp;

    String[] cityArray = {"Ahmedabad","Gandhinagar","Vadodara","Surat","Rajkot"};
    String sCity = "";
    String sPaymentType = "";

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        String categoryQuery = "CREATE TABLE IF NOT EXISTS CATEGORY(CATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),IMAGE VARCHAR(100))";
        db.execSQL(categoryQuery);

        String subCategoryQuery = "CREATE TABLE IF NOT EXISTS SUBCATEGORY(SUBCATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT,CATEGORYID VARCHAR(10),NAME VARCHAR(50),IMAGE VARCHAR(100))";
        db.execSQL(subCategoryQuery);

        String productQuery = "CREATE TABLE IF NOT EXISTS PRODUCT(PRODUCTID INTEGER PRIMARY KEY AUTOINCREMENT,SUBCATEGORYID VARCHAR(10),NAME VARCHAR(50),PRICE VARCHAR(20),IMAGE VARCHAR(100),DESCRIPTION TEXT)";
        db.execSQL(productQuery);

        String wishlistQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),PRODUCTID INTEGER(10))";
        db.execSQL(wishlistQuery);

        String cartQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY AUTOINCREMENT,ORDERID INTEGER(10),USERID INTEGER(10),PRODUCTID INTEGER(10),QTY INTEGER(3),PRICE VARCHAR(10),TOTALPRICE VARCHAR(10))";
        db.execSQL(cartQuery);

        String orderQuery = "CREATE TABLE IF NOT EXISTS TBL_ORDER(ORDERID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),NAME VARCHAR(100),CONTACT BIGINT(10),EMAIL VARCHAR(100),ADDRESS TEXT,CITY VARCHAR(50),PAYMENTTYPE VARCHAR(50),TRANSACTIONID VARCHAR(50),AMOUNT VARCHAR(20))";
        db.execSQL(orderQuery);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        name = findViewById(R.id.checkout_name);
        email = findViewById(R.id.checkout_email);
        contact = findViewById(R.id.checkout_contact);
        address = findViewById(R.id.checkout_address);
        city = findViewById(R.id.checkout_city);
        radioGroup = findViewById(R.id.checkout_group);
        payNow = findViewById(R.id.checkout_pay_now);

        name.setText(sp.getString(ConstantSp.NAME,""));
        email.setText(sp.getString(ConstantSp.EMAIL,""));
        contact.setText(sp.getString(ConstantSp.CONTACT,""));

        ArrayAdapter adapter = new ArrayAdapter(CheckoutActivity.this, android.R.layout.simple_list_item_1,cityArray);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sCity = cityArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sPaymentType = radioButton.getText().toString();
            }
        });

        payNow.setText("Pay Now "+ConstantSp.PRICE_SYMBOL+sp.getString(ConstantSp.CARTTOTAL,""));

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().trim().equalsIgnoreCase("")){
                    name.setError("Name Required");
                }
                else if(contact.getText().toString().trim().equalsIgnoreCase("")){
                    contact.setError("Contact No. Required");
                }
                else if(contact.getText().toString().trim().length()<10){
                    contact.setError("Valid Contact No. Required");
                }
                else if(email.getText().toString().trim().equalsIgnoreCase("")){
                    email.setError("Email Id Required");
                }
                else if(!email.getText().toString().trim().matches(emailPattern)){
                    email.setError("Valid Email Id Required");
                }
                else if(address.getText().toString().trim().equalsIgnoreCase("")){
                    address.setError("Address Required");
                }
                else if(sCity.equalsIgnoreCase("")){
                    Toast.makeText(CheckoutActivity.this, "Please Select City", Toast.LENGTH_SHORT).show();
                }
                else if(sPaymentType.equalsIgnoreCase("")){
                    Toast.makeText(CheckoutActivity.this, "Please Select Payment Type", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(sPaymentType.equalsIgnoreCase("Cash On Delivery")){
                        doOrderProcess(sPaymentType,"");
                    }
                    else if(sPaymentType.equalsIgnoreCase("Online")){
                        startPayment();
                    }
                }
            }
        });

    }

    private void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_xsiOz9lYtWKHgF");
        /**
         * Set your logo here
         */
        checkout.setImage(R.mipmap.ic_launcher);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", sp.getString(ConstantSp.PRODUCTNAME,""));
            options.put("send_sms_hash",true);
            options.put("allow_rotation",true);
            options.put("image", R.mipmap.ic_launcher);
            //options.put("order_id", "order_"+sp.getString(ConstantSp.PRODUCTID,""));//from response of step 3.
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Integer.parseInt(sp.getString(ConstantSp.CARTTOTAL,"")) * 100));//pass amount in currency subunits
            options.put("prefill.email", sp.getString(ConstantSp.EMAIL,""));
            options.put("prefill.contact",sp.getString(ConstantSp.CONTACT,""));
            /*JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);*/
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("RESPONSE_CATCH", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        //alertMethod("Payment Success","Your Payment Is Success. Transaction Id Is "+s);
        doOrderProcess(sPaymentType,s);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        alertMethod("Payment Failed","OOPS!!! Your Payment Failed");
    }

    private void alertMethod(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void doOrderProcess(String sPaymentType, String transactionId) {
        String insertQuery = "INSERT INTO TBL_ORDER VALUES(NULL,'"+sp.getString(ConstantSp.USERID,"")+"','"+name.getText().toString()+"','"+contact.getText().toString()+"','"+email.getText().toString()+"','"+address.getText().toString()+"','"+sCity+"','"+sPaymentType+"','"+transactionId+"','"+sp.getString(ConstantSp.CARTTOTAL,"")+"')";
        db.execSQL(insertQuery);

        String selectDataQuery = "SELECT ORDERID FROM TBL_ORDER ORDER BY ORDERID DESC limit 1";
        Cursor orderCusror = db.rawQuery(selectDataQuery,null);
        String sOrderId = "";
        if(orderCusror.getCount()>0){
            while (orderCusror.moveToNext()){
                sOrderId = orderCusror.getString(0);
            }
        }

        String updateQuery = "UPDATE CART SET ORDERID='"+sOrderId+"' WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
        db.execSQL(updateQuery);

        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Order Placed");
        builder.setIcon(R.mipmap.ic_launcher);
        if(transactionId.trim().equalsIgnoreCase("")){
            builder.setMessage("Your Order Placed Successfully. Order Id = "+sOrderId);
        }
        else{
            builder.setMessage("Your Order Placed Successfully. Order Id = "+sOrderId+" And Transaction Id = "+transactionId);
        }

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(CheckoutActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();

    }
}