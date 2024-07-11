package internship.june;

import android.content.SharedPreferences;
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

    int[] idArray = {1,2,3,4,5,6,7,8};
    int[] categoryIdArray = {1,1,2,2,2,2,2,2};
    String[] nameArray = {"Allen Solly","US Polo","Allen Solly","US Polo","Mufti","Levis","Louis Philippe","H & M"};
    int[] imageArray = {R.drawable.allen,R.drawable.uspolo,R.drawable.allen,R.drawable.uspolo,R.drawable.mufti,R.drawable.levis_logo,R.drawable.louis_logo,R.drawable.hm_logo};

    ArrayList<SubCategoryList> arrayList;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        recyclerView = findViewById(R.id.sub_category_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();
        for(int i=0;i<idArray.length;i++){
            if(Integer.parseInt(sp.getString(ConstantSp.CATEGORYID,"")) == categoryIdArray[i]) {
                SubCategoryList list = new SubCategoryList();
                list.setId(idArray[i]);
                list.setCategoryId(categoryIdArray[i]);
                list.setName(nameArray[i]);
                list.setImage(imageArray[i]);
                arrayList.add(list);
            }
        }
        SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,arrayList);
        recyclerView.setAdapter(adapter);
    }
}