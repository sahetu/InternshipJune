package internship.june;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<ProductList> arrayList;
    SharedPreferences sp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

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

        recyclerView = findViewById(R.id.product_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM PRODUCT WHERE SUBCATEGORYID='"+sp.getString(ConstantSp.SUBCATEGORYID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                ProductList list = new ProductList();
                list.setId(cursor.getString(0));
                list.setSubCatId(cursor.getString(1));
                list.setName(cursor.getString(2));
                list.setPrice(cursor.getString(3));
                list.setImage(Integer.parseInt(cursor.getString(4)));
                list.setDesc(cursor.getString(5));
                String wishlistSelectQuery = "SELECT * FROM WISHLIST WHERE PRODUCTID='"+cursor.getString(0)+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
                Cursor wishCursor = db.rawQuery(wishlistSelectQuery,null);
                if(wishCursor.getCount()>0){
                    list.setWishlist(true);
                }
                else {
                    list.setWishlist(false);
                }

                String cartSelectQuery = "SELECT * FROM CART WHERE PRODUCTID='"+cursor.getString(0)+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
                Cursor cartCursor = db.rawQuery(cartSelectQuery,null);
                if(cartCursor.getCount()>0){
                    while (cartCursor.moveToNext()){
                        list.setCartId(cartCursor.getString(0));
                        list.setQty(Integer.parseInt(cartCursor.getString(4)));
                    }
                }
                else{
                    list.setCartId("0");
                    list.setQty(0);
                }

                arrayList.add(list);
            }
            ProductAdapter adapter = new ProductAdapter(ProductActivity.this,arrayList,db);
            recyclerView.setAdapter(adapter);
        }

        /*arrayList = new ArrayList<>();
        for(int i=0;i<idArray.length;i++){
            if(Integer.parseInt(sp.getString(ConstantSp.SUBCATEGORYID,"")) == subCatIdArray[i]) {
                ProductList list = new ProductList();
                list.setId(idArray[i]);
                list.setSubCatId(subCatIdArray[i]);
                list.setName(nameArray[i]);
                list.setPrice(priceArray[i]);
                list.setImage(imageArray[i]);
                list.setDesc(descArray[i]);
                arrayList.add(list);
            }
        }
        ProductAdapter adapter = new ProductAdapter(ProductActivity.this,arrayList);
        recyclerView.setAdapter(adapter);*/
    }
}