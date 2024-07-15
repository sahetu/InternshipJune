package internship.june;

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CategoryList> arrayList;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        db = openOrCreateDatabase("AndroidInternshipJune.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),EMAIL VARCHAR(50),CONTACT BIGINT(10),PASSWORD VARCHAR(20))";
        db.execSQL(tableQuery);

        String categoryQuery = "CREATE TABLE IF NOT EXISTS CATEGORY(CATEGORYID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),IMAGE VARCHAR(100))";
        db.execSQL(categoryQuery);

        recyclerView = findViewById(R.id.category_recyclerview);
        //recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CATEGORY";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                CategoryList list = new CategoryList();
                list.setId(cursor.getString(0));
                list.setName(cursor.getString(1));
                list.setImage(Integer.parseInt(cursor.getString(2)));
                arrayList.add(list);
            }
            CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this, arrayList);
            recyclerView.setAdapter(adapter);
        }
        /*CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,idArray,nameArray,imageArray);
        recyclerView.setAdapter(adapter);*/
    }
}