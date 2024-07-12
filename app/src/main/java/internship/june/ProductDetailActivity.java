package internship.june;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ProductDetailActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    ImageView imageView;
    TextView name,price,desc;

    Button payNow;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        imageView = findViewById(R.id.product_detail_image);
        name = findViewById(R.id.product_detail_name);
        price = findViewById(R.id.product_detail_price);
        desc = findViewById(R.id.product_detail_desc);

        name.setText(sp.getString(ConstantSp.PRODUCTNAME,""));
        price.setText(ConstantSp.PRICE_SYMBOL+sp.getString(ConstantSp.PRODUCTPRICE,""));
        desc.setText(sp.getString(ConstantSp.PRODUCTDESC,""));
        imageView.setImageResource(Integer.parseInt(sp.getString(ConstantSp.PRODUCTIMAGE,"")));

        payNow = findViewById(R.id.product_detail_pay);

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
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
            options.put("amount", String.valueOf(Integer.parseInt(sp.getString(ConstantSp.PRODUCTPRICE,"")) * 100));//pass amount in currency subunits
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
        alertMethod("Payment Success","Your Payment Is Success. Transaction Id Is "+s);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        alertMethod("Payment Failed","OOPS!!! Your Payment Failed");
    }

    private void alertMethod(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
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

}