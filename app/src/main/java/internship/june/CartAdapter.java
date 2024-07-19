package internship.june;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyHolder> {

    Context context;
    ArrayList<CartList> arrayList;
    SQLiteDatabase db;
    SharedPreferences sp;
    int iQty = 0;

    public CartAdapter(Context context, ArrayList<CartList> arrayList, SQLiteDatabase db) {
        this.context = context;
        this.arrayList = arrayList;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
        this.db = db;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart, parent, false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView, plus, minus;
        TextView name, price, qty;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custom_cart_name);
            price = itemView.findViewById(R.id.custom_cart_price);
            imageView = itemView.findViewById(R.id.custom_cart_image);
            plus = itemView.findViewById(R.id.custom_cart_plus);
            minus = itemView.findViewById(R.id.custom_cart_minus);
            qty = itemView.findViewById(R.id.custom_cart_qty);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getPrice() + " * " + arrayList.get(position).getQty() + " = " + ConstantSp.PRICE_SYMBOL + arrayList.get(position).getTotalprice());
        holder.imageView.setImageResource(arrayList.get(position).getImage());
        holder.qty.setText(String.valueOf(arrayList.get(position).getQty()));

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty = arrayList.get(position).getQty();
                iQty += 1; //iQty = iQty+1
                updateMethod(iQty, "Plus", position);
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty = arrayList.get(position).getQty();
                iQty -= 1; //iQty = iQty-1
                if (iQty <= 0) {
                    updateMethod(iQty, "Delete", position);
                } else {
                    updateMethod(iQty, "Minus", position);
                }
            }
        });

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

    private void updateMethod(int iQty, String type, int position) {
        int iProductAmount = Integer.parseInt(arrayList.get(position).getPrice());
        int iTotalPrice = iQty * iProductAmount;
        if (type.equalsIgnoreCase("Plus") || type.equalsIgnoreCase("Minus")) {
            String updateQuery = "UPDATE CART SET QTY='" + iQty + "',TOTALPRICE='" + iTotalPrice + "' WHERE CARTID='" + arrayList.get(position).getCartId() + "'";
            db.execSQL(updateQuery);

            if(type.equalsIgnoreCase("Plus")){
                CartActivity.iCartTotal += iProductAmount;
            }
            else{
                CartActivity.iCartTotal -= iProductAmount;
            }
            CartActivity.total.setText("Total : "+ConstantSp.PRICE_SYMBOL+CartActivity.iCartTotal);

            CartList list = new CartList();
            list.setCartId(arrayList.get(position).getCartId());
            list.setQty(iQty);
            list.setTotalprice(iTotalPrice);
            list.setProductId(arrayList.get(position).getProductId());
            list.setSubCatId(arrayList.get(position).getSubCatId());
            list.setName(arrayList.get(position).getName());
            list.setPrice(arrayList.get(position).getPrice());
            list.setImage(arrayList.get(position).getImage());
            list.setDesc(arrayList.get(position).getDesc());
            arrayList.set(position,list);
            notifyDataSetChanged();

        } else {
            String deleteQuery = "DELETE FROM CART WHERE CARTID='" + arrayList.get(position).getCartId() + "'";
            db.execSQL(deleteQuery);
            arrayList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Product Removed From Cart Successfully", Toast.LENGTH_SHORT).show();
            CartActivity.iCartTotal -= iProductAmount;
            CartActivity.total.setText("Total : "+ConstantSp.PRICE_SYMBOL+CartActivity.iCartTotal);
        }

    }

}
