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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<SubCategoryList> arrayList;

    SharedPreferences sp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        String categoryQuery = "CREATE TABLE IF NOT EXISTS CATEGORY(CATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),IMAGE VARCHAR(100))";
        db.execSQL(categoryQuery);

        String subCategoryQuery = "CREATE TABLE IF NOT EXISTS SUBCATEGORY(SUBCATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT,CATEGORYID VARCHAR(10),NAME VARCHAR(50),IMAGE VARCHAR(100))";
        db.execSQL(subCategoryQuery);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        recyclerView = findViewById(R.id.sub_category_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM SUBCATEGORY WHERE CATEGORYID='"+sp.getString(ConstantSp.CATEGORYID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                SubCategoryList list = new SubCategoryList();
                list.setId(cursor.getString(0));
                list.setCategoryId(cursor.getString(1));
                list.setName(cursor.getString(2));
                list.setImage(Integer.parseInt(cursor.getString(3)));
                arrayList.add(list);
            }
            SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,arrayList);
            recyclerView.setAdapter(adapter);
        }

        /*arrayList = new ArrayList<>();
        if(Integer.parseInt(sp.getString(ConstantSp.CATEGORYID,"")) == categoryIdArray[i]) {
                SubCategoryList list = new SubCategoryList();
                list.setId(idArray[i]);
                list.setCategoryId(categoryIdArray[i]);
                list.setName(nameArray[i]);
                list.setImage(imageArray[i]);
                arrayList.add(list);
            }*/
    }
}