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

public class WishlistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SQLiteDatabase db;
    SharedPreferences sp;

    ArrayList<WishlistList> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

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

        recyclerView = findViewById(R.id.wishlist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(WishlistActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                WishlistList list = new WishlistList();
                list.setWishlistId(cursor.getString(0));
                String productSelectQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+cursor.getString(2)+"'";
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
            WishlistAdapter adapter = new WishlistAdapter(WishlistActivity.this,arrayList,db);
            recyclerView.setAdapter(adapter);
        }
    }
}