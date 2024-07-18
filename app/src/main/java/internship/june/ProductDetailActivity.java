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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class ProductDetailActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    ImageView imageView;
    TextView name,price,desc,qty;

    Button payNow;
    ImageView wishlist,addCart,plus,minus;

    LinearLayout cartLayout;

    SharedPreferences sp;
    SQLiteDatabase db;
    boolean isWishlist = false;
    int iQty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

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

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        imageView = findViewById(R.id.product_detail_image);
        name = findViewById(R.id.product_detail_name);
        price = findViewById(R.id.product_detail_price);
        desc = findViewById(R.id.product_detail_desc);

        cartLayout = findViewById(R.id.product_detail_cart_layout);
        plus = findViewById(R.id.product_detail_plus);
        minus = findViewById(R.id.product_detail_minus);
        qty = findViewById(R.id.product_detail_qty);

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

        wishlist = findViewById(R.id.product_detail_wishlist);
        addCart = findViewById(R.id.product_detail_cart);

        String selectCartQuery = "SELECT * FROM CART WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+sp.getString(ConstantSp.PRODUCTID,"")+"' AND ORDERID='0'";
        Cursor cartCursor = db.rawQuery(selectCartQuery,null);
        if(cartCursor.getCount()>0){
            while (cartCursor.moveToNext()){
                iQty = Integer.parseInt(cartCursor.getString(4));
                qty.setText(String.valueOf(iQty));
            }
            addCart.setVisibility(View.GONE);
            cartLayout.setVisibility(View.VISIBLE);
        }
        else{
            addCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
        }


        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty = 1;
                int iPrice = Integer.parseInt(sp.getString(ConstantSp.PRODUCTPRICE,""));
                int iTotalPrice = iQty * iPrice;
                String insertQuery = "INSERT INTO CART VALUES(NULL,'0','"+sp.getString(ConstantSp.USERID,"")+"','"+sp.getString(ConstantSp.PRODUCTID,"")+"','"+iQty+"','"+iPrice+"','"+iTotalPrice+"')";
                db.execSQL(insertQuery);
                Toast.makeText(ProductDetailActivity.this, "Product Added In Cart Successfully", Toast.LENGTH_SHORT).show();

                qty.setText(String.valueOf(iQty));
                addCart.setVisibility(View.GONE);
                cartLayout.setVisibility(View.VISIBLE);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty += 1; //iQty = iQty+1
                updateMethod(iQty,"Update");
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty -= 1; //iQty = iQty-1
                if(iQty<=0){
                    updateMethod(iQty, "Delete");
                }
                else {
                    updateMethod(iQty, "Update");
                }
            }
        });

        String selectQuery = "SELECT * FROM WISHLIST WHERE PRODUCTID='"+sp.getString(ConstantSp.PRODUCTID,"")+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            isWishlist = true;
            wishlist.setImageResource(R.drawable.wishlist_fill);
        }
        else{
            isWishlist = false;
            wishlist.setImageResource(R.drawable.wishlist_blank);
        }

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWishlist){
                    String deleteQuery = "DELETE FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+sp.getString(ConstantSp.PRODUCTID,"")+"'";
                    db.execSQL(deleteQuery);
                    Toast.makeText(ProductDetailActivity.this, "Product Removed From Wishlist", Toast.LENGTH_SHORT).show();
                    wishlist.setImageResource(R.drawable.wishlist_blank);
                    isWishlist = false;
                }
                else {
                    String insertQuery = "INSERT INTO WISHLIST VALUES(NULL,'" + sp.getString(ConstantSp.USERID, "") + "','" + sp.getString(ConstantSp.PRODUCTID, "") + "')";
                    db.execSQL(insertQuery);
                    Toast.makeText(ProductDetailActivity.this, "Product Added In Wishlist", Toast.LENGTH_SHORT).show();
                    wishlist.setImageResource(R.drawable.wishlist_fill);
                    isWishlist = true;
                }
            }
        });

    }

    private void updateMethod(int iQty, String type) {
        int iProductAmount = Integer.parseInt(sp.getString(ConstantSp.PRODUCTPRICE,""));
        int iTotalPrice = iQty * iProductAmount;
        if(type.equalsIgnoreCase("Update")) {
            String updateQuery = "UPDATE CART SET QTY='" + iQty + "',TOTALPRICE='" + iTotalPrice + "' WHERE PRODUCTID='" + sp.getString(ConstantSp.PRODUCTID, "") + "' AND USERID='" + sp.getString(ConstantSp.USERID, "") + "' AND ORDERID='0'";
            db.execSQL(updateQuery);
            //Toast.makeText(ProductDetailActivity.this, "Cart Updated Successfully", Toast.LENGTH_SHORT).show();
            qty.setText(String.valueOf(iQty));
        }
        else{
            String deleteQuery = "DELETE FROM CART WHERE PRODUCTID='" + sp.getString(ConstantSp.PRODUCTID, "") + "' AND USERID='" + sp.getString(ConstantSp.USERID, "") + "' AND ORDERID='0'";
            db.execSQL(deleteQuery);
            Toast.makeText(ProductDetailActivity.this, "Product Removed From Cart Successfully", Toast.LENGTH_SHORT).show();
            cartLayout.setVisibility(View.GONE);
            addCart.setVisibility(View.VISIBLE);
        }
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