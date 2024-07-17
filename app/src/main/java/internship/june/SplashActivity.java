package internship.june;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    SharedPreferences sp;

    SQLiteDatabase db;

    int[] productIdArray = {1,2,3};
    int[] productSubCatIdArray = {1,4,4};
    String[] productNameArray = {"Neck Tshirt","Regular Feet","Casual"};
    String[] productPriceArray = {"1499","2999","2599"};
    int[] productImageArray = {R.drawable.allen_tshirt,R.drawable.uspolo_regular_fit,R.drawable.us_polo_shirt};
    String[] productDescArray = {
            "Perfect for every situation, our plain t-shirts are a must-have for every wardrobe. This plain t-shirt is perfect to go with everything. Shop for this white plain t-shirt online in India, available exclusively at Be Awara.",
            "Smart is redefined with this shirt, woven from a lightly textured pure cotton fabric. Cut to a regular fit for a timeless silhouette, its perfect for working or weddings. An easy-to-iron finish makes laundering a breeze. We only ever use responsibly sourced cotton for our clothes. M&S Collection: easy-to-wear wardrobe staples that combine classic and contemporary styles.",
            "Elevate your formal look effortlessly with the Peter England Elite Regular Fit Stripe Full Sleeves Shirt. Made from high-quality cotton fabric, this white shirt adds refined comfort to your wardrobe. The stripe pattern adds a subtle touch of texture, making it perfect for any formal occasion. The shirt has a regular fit that sits well on any body type. Enjoy the comfort that this shirt provides as you carry on through daily work activities."
    };

    int[] subCategoryIdArray = {1,2,3,4,5,6,7,8};
    int[] subCategoryCategoryIdArray = {1,1,2,2,2,2,2,2};
    String[] subCategoryNameArray = {"Allen Solly","US Polo","Allen Solly","US Polo","Mufti","Levis","Louis Philippe","H & M"};
    int[] subCategoryImageArray = {R.drawable.allen,R.drawable.uspolo,R.drawable.allen,R.drawable.uspolo,R.drawable.mufti,R.drawable.levis_logo,R.drawable.louis_logo,R.drawable.hm_logo};

    int[] categoryIdArray = {1,2,3,4};
    String[] categoryNameArray = {"Tshirt","Shirt","Jeans","Shorts"};
    int[] categoryimageArray = {R.drawable.tshirts,R.drawable.shirts,R.drawable.jeans,R.drawable.shorts};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

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

        categoryStore();
        subCategoryStore();
        productDataStore();

        imageView = findViewById(R.id.splash_image);

        Glide
                .with(SplashActivity.this).
                asGif().
                load("https://i.pinimg.com/originals/d8/d1/64/d8d1645996dcf0eb683cacc81210c606.gif")
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sp.getString(ConstantSp.USERID,"").equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);

    }

    private void categoryStore() {
        for(int i=0;i<categoryIdArray.length;i++){
            String selectQuery = "SELECT * FROM CATEGORY WHERE NAME='"+categoryNameArray[i]+"'";
            Cursor cursor = db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){

            }
            else{
                String insertQuery = "INSERT INTO CATEGORY VALUES (NULL,'"+categoryNameArray[i]+"','"+categoryimageArray[i]+"')";
                db.execSQL(insertQuery);
            }
        }
    }

    private void subCategoryStore() {
        for(int i=0;i<subCategoryIdArray.length;i++){
            String selectSubCategoryQuery = "SELECT * FROM SUBCATEGORY WHERE NAME='"+subCategoryNameArray[i]+"' AND CATEGORYID='"+subCategoryCategoryIdArray[i]+"'";
            Cursor cursor = db.rawQuery(selectSubCategoryQuery,null);
            if(cursor.getCount()>0){

            }
            else{
                String insertQuery = "INSERT INTO SUBCATEGORY VALUES (NULL,'"+subCategoryCategoryIdArray[i]+"','"+subCategoryNameArray[i]+"','"+subCategoryImageArray[i]+"')";
                db.execSQL(insertQuery);
            }
        }
    }

    private void productDataStore() {
        for(int i=0;i<productIdArray.length;i++){
            String selectProdQuery = "SELECT * FROM PRODUCT WHERE NAME='"+productNameArray[i]+"' AND SUBCATEGORYID='"+productSubCatIdArray[i]+"'";
            Cursor cursor = db.rawQuery(selectProdQuery,null);
            if(cursor.getCount()>0){

            }
            else{
                String insertQuery = "INSERT INTO PRODUCT VALUES(NULL,'"+productSubCatIdArray[i]+"','"+productNameArray[i]+"','"+productPriceArray[i]+"','"+productImageArray[i]+"','"+productDescArray[i]+"')";
                db.execSQL(insertQuery);
            }
        }
    }
}