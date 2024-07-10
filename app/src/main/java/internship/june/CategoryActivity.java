package internship.june;

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

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    int[] idArray = {1,2,3,4};
    String[] nameArray = {"Tshirt","Shirt","Jeans","Shorts"};
    int[] imageArray = {R.drawable.tshirts,R.drawable.shirts,R.drawable.jeans,R.drawable.shorts};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.category_recyclerview);
        //recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,idArray,nameArray,imageArray);
        recyclerView.setAdapter(adapter);

    }
}