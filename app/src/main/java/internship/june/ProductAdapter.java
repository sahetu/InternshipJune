package internship.june;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder> {

    Context context;
    ArrayList<ProductList> arrayList;

    SharedPreferences sp;
    SQLiteDatabase db;

    public ProductAdapter(Context context, ArrayList<ProductList> arrayList, SQLiteDatabase db) {
        this.context = context;
        this.arrayList = arrayList;
        this.db = db;
        sp = context.getSharedPreferences(ConstantSp.PREF,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ProductAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product,parent,false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView,wishlist;
        TextView name,price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custom_product_name);
            price = itemView.findViewById(R.id.custom_product_price);
            wishlist = itemView.findViewById(R.id.custom_product_wishlist);
            imageView = itemView.findViewById(R.id.custom_product_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL+arrayList.get(position).getPrice());
        holder.imageView.setImageResource(arrayList.get(position).getImage());

        if(arrayList.get(position).isWishlist()){
            holder.wishlist.setImageResource(R.drawable.wishlist_fill);
        }
        else{
            holder.wishlist.setImageResource(R.drawable.wishlist_blank);
        }

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrayList.get(position).isWishlist){
                    String removewQuery = "DELETE FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+arrayList.get(position).getId()+"'";
                    db.execSQL(removewQuery);
                    setData(position,false);
                }
                else{
                    String insertQuery = "INSERT INTO WISHLIST VALUES(NULL,'"+sp.getString(ConstantSp.USERID,"")+"','"+arrayList.get(position).getId()+"')";
                    db.execSQL(insertQuery);
                    setData(position,true);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCTID, String.valueOf(arrayList.get(position).getId())).commit();
                sp.edit().putString(ConstantSp.PRODUCTNAME,arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCTPRICE,arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCTDESC,arrayList.get(position).getDesc()).commit();
                sp.edit().putString(ConstantSp.PRODUCTIMAGE, String.valueOf(arrayList.get(position).getImage())).commit();

                Intent intent = new Intent(context, ProductDetailActivity.class);
                context.startActivity(intent);

            }
        });

    }

    private void setData(int position, boolean b) {
        ProductList list = new ProductList();
        list.setId(arrayList.get(position).getId());
        list.setSubCatId(arrayList.get(position).getSubCatId());
        list.setName(arrayList.get(position).getName());
        list.setPrice(arrayList.get(position).getPrice());
        list.setDesc(arrayList.get(position).getDesc());
        list.setImage(arrayList.get(position).getImage());
        list.setWishlist(b);
        arrayList.set(position,list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
