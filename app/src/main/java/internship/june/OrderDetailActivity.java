package internship.june;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {

    TextView orderNo,price,address,paymentType;
    RecyclerView recyclerView;
    SQLiteDatabase db;
    SharedPreferences sp;

    ArrayList<CartList> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

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
        
        orderNo = findViewById(R.id.order_detail_no);
        price = findViewById(R.id.order_detail_amount);
        address = findViewById(R.id.order_detail_address);
        paymentType = findViewById(R.id.order_detail_payment_type);

        orderNo.setText("Order No : "+sp.getString(ConstantSp.ORDERID,""));
        price.setText(ConstantSp.PRICE_SYMBOL+sp.getString(ConstantSp.ORDER_PRICE,""));
        address.setText(sp.getString(ConstantSp.ORDER_ADDRESS,"")+"\n"+sp.getString(ConstantSp.ORDER_CITY,""));
        if(sp.getString(ConstantSp.ORDER_TRANSACTION_ID,"").equalsIgnoreCase("")){
            paymentType.setText(sp.getString(ConstantSp.ORDER_PAYMENT_TYPE,""));
        }
        else{
            paymentType.setText(sp.getString(ConstantSp.ORDER_PAYMENT_TYPE,"")+" ("+sp.getString(ConstantSp.ORDER_TRANSACTION_ID,"")+")");
        }

        recyclerView = findViewById(R.id.order_detail_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CART WHERE ORDERID='"+sp.getString(ConstantSp.ORDERID,"")+"'";
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
                arrayList.add(list);
            }
            OrderDetailAdapter adapter = new OrderDetailAdapter(OrderDetailActivity.this,arrayList);
            recyclerView.setAdapter(adapter);
        }

    }
}