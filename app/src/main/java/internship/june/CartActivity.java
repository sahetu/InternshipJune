package internship.june;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    public static TextView total;
    Button checkout;

    RecyclerView recyclerView;

    SQLiteDatabase db;
    SharedPreferences sp;

    ArrayList<CartList> arrayList;

    public static int iCartTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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

        total = findViewById(R.id.cart_bottom_total);
        checkout = findViewById(R.id.cart_bottom_checkout);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.CARTTOTAL,String.valueOf(iCartTotal)).commit();
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.cart_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CART WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                CartList list = new CartList();
                list.setCartId(cursor.getString(0));
                list.setQty(Integer.parseInt(cursor.getString(4)));
                list.setTotalprice(Integer.parseInt(cursor.getString(6)));
                String productSelectQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+cursor.getString(3)+"'";
                Cursor proCursor = db.rawQuery(productSelectQuery,null);
                if(proCursor.getCount()>0){
                    while (proCursor.moveToNext()) {
                        list.setProductId(proCursor.getString(0));
                        list.setSubCatId(proCursor.getString(1));
                        list.setName(proCursor.getString(2));
                        list.setPrice(proCursor.getString(3));
                        list.setImage(Integer.parseInt(proCursor.getString(4)));
                        list.setDesc(proCursor.getString(5));
                    }
                }
                iCartTotal += Integer.parseInt(cursor.getString(6));
                arrayList.add(list);
            }
            CartAdapter adapter = new CartAdapter(CartActivity.this,arrayList,db);
            recyclerView.setAdapter(adapter);

            total.setText("Total : "+ConstantSp.PRICE_SYMBOL+iCartTotal);

        }
        else{
            iCartTotal = 0;
            total.setText("Total : "+ConstantSp.PRICE_SYMBOL+iCartTotal);
        }

    }
}