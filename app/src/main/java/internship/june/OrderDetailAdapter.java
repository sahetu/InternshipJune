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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyHolder> {

    Context context;
    ArrayList<CartList> arrayList;
    SharedPreferences sp;

    public OrderDetailAdapter(Context context, ArrayList<CartList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public OrderDetailAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_detail, parent, false);
        return new OrderDetailAdapter.MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custom_order_detail_name);
            price = itemView.findViewById(R.id.custom_order_detail_price);
            imageView = itemView.findViewById(R.id.custom_order_detail_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getPrice() + " * " + arrayList.get(position).getQty() + " = " + ConstantSp.PRICE_SYMBOL + arrayList.get(position).getTotalprice());
        holder.imageView.setImageResource(arrayList.get(position).getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCTID, String.valueOf(arrayList.get(position).getProductId())).commit();
                sp.edit().putString(ConstantSp.PRODUCTNAME, arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCTPRICE, arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCTDESC, arrayList.get(position).getDesc()).commit();
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
