package internship.june;

import android.content.SharedPreferences;
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

    int[] idArray = {1,2,3};
    int[] subCatIdArray = {1,4,4};
    String[] nameArray = {"Neck Tshirt","Regular Feet","Casual"};
    String[] priceArray = {"1499","2999","2599"};
    int[] imageArray = {R.drawable.allen_tshirt,R.drawable.uspolo_regular_fit,R.drawable.us_polo_shirt};
    String[] descArray = {
            "Perfect for every situation, our plain t-shirts are a must-have for every wardrobe. This plain t-shirt is perfect to go with everything. Shop for this white plain t-shirt online in India, available exclusively at Be Awara.",
            "Smart is redefined with this shirt, woven from a lightly textured pure cotton fabric. Cut to a regular fit for a timeless silhouette, it's perfect for working or weddings. An easy-to-iron finish makes laundering a breeze. We only ever use responsibly sourced cotton for our clothes. M&S Collection: easy-to-wear wardrobe staples that combine classic and contemporary styles.",
            "Elevate your formal look effortlessly with the Peter England Elite Regular Fit Stripe Full Sleeves Shirt. Made from high-quality cotton fabric, this white shirt adds refined comfort to your wardrobe. The stripe pattern adds a subtle touch of texture, making it perfect for any formal occasion. The shirt has a regular fit that sits well on any body type. Enjoy the comfort that this shirt provides as you carry on through daily work activities."
    };

    ArrayList<ProductList> arrayList;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        recyclerView = findViewById(R.id.product_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();
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
        recyclerView.setAdapter(adapter);
    }
}