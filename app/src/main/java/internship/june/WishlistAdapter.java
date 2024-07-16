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

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyHolder> {

    Context context;
    ArrayList<WishlistList> arrayList;
    SQLiteDatabase db;

    SharedPreferences sp;

    public WishlistAdapter(Context context, ArrayList<WishlistList> arrayList, SQLiteDatabase db) {
        this.context = context;
        this.arrayList = arrayList;
        this.db = db;
        sp = context.getSharedPreferences(ConstantSp.PREF,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public WishlistAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_wishlist,parent,false);
        return new WishlistAdapter.MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView,remove;
        TextView name,price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custom_wishlist_name);
            price = itemView.findViewById(R.id.custom_wishlist_price);
            imageView = itemView.findViewById(R.id.custom_wishlist_image);
            remove = itemView.findViewById(R.id.custom_wishlist_remove);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL+arrayList.get(position).getPrice());
        holder.imageView.setImageResource(arrayList.get(position).getImage());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String removeQuery = "DELETE FROM WISHLIST WHERE WISHLISTID='"+arrayList.get(position).getWishlistId()+"'";
                db.execSQL(removeQuery);
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCTID, String.valueOf(arrayList.get(position).getProductId())).commit();
                sp.edit().putString(ConstantSp.PRODUCTNAME,arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCTPRICE,arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCTDESC,arrayList.get(position).getDesc()).commit();
                sp.edit().putString(ConstantSp.PRODUCTIMAGE, String.valueOf(arrayList.get(position).getImage())).commit();

                Intent intent = new Intent(context, ProductDetailActivity.class);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
