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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder> {

    Context context;
    ArrayList<ProductList> arrayList;

    SharedPreferences sp;
    SQLiteDatabase db;
    int iQty = 0;

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

        ImageView imageView,wishlist,addCart,plus,minus;
        TextView name,price,qty;
        LinearLayout cartLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custom_product_name);
            price = itemView.findViewById(R.id.custom_product_price);
            wishlist = itemView.findViewById(R.id.custom_product_wishlist);
            imageView = itemView.findViewById(R.id.custom_product_image);
            qty = itemView.findViewById(R.id.custom_product_qty);
            addCart = itemView.findViewById(R.id.custom_product_cart);
            plus = itemView.findViewById(R.id.custom_product_plus);
            minus = itemView.findViewById(R.id.custom_product_minus);
            cartLayout = itemView.findViewById(R.id.custom_product_cart_layout);
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

        if(arrayList.get(position).getCartId().equalsIgnoreCase("0")){
            holder.addCart.setVisibility(View.VISIBLE);
            holder.cartLayout.setVisibility(View.GONE);
        }
        else{
            holder.addCart.setVisibility(View.GONE);
            holder.cartLayout.setVisibility(View.VISIBLE);
        }

        holder.qty.setText(String.valueOf(arrayList.get(position).getQty()));

        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCartMethod(position);
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty = arrayList.get(position).getQty();
                iQty += 1; //iQty = iQty+1
                updateMethod(iQty,"Update",position);
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty = arrayList.get(position).getQty();
                iQty -= 1; //iQty = iQty-1
                if(iQty<=0){
                    updateMethod(iQty, "Delete",position);
                }
                else {
                    updateMethod(iQty, "Update",position);
                }
            }
        });

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

    private void addCartMethod(int position) {
        String addCartQuery = "INSERT INTO CART VALUES(NULL,'0','"+sp.getString(ConstantSp.USERID,"")+"','"+arrayList.get(position).getId()+"','1','"+arrayList.get(position).getPrice()+"','"+arrayList.get(position).getPrice()+"')";
        db.execSQL(addCartQuery);

        String selectDataQuery = "SELECT CARTID FROM CART ORDER BY CARTID DESC limit 1";
        Cursor cartCusror = db.rawQuery(selectDataQuery,null);
        String sCartId = "";
        if(cartCusror.getCount()>0){
            while (cartCusror.moveToNext()){
                sCartId = cartCusror.getString(0);
            }
        }

        ProductList list = new ProductList();
        list.setId(arrayList.get(position).getId());
        list.setSubCatId(arrayList.get(position).getSubCatId());
        list.setName(arrayList.get(position).getName());
        list.setPrice(arrayList.get(position).getPrice());
        list.setDesc(arrayList.get(position).getDesc());
        list.setImage(arrayList.get(position).getImage());
        list.setWishlist(arrayList.get(position).isWishlist());
        list.setCartId(sCartId);
        list.setQty(1);
        arrayList.set(position,list);
        notifyDataSetChanged();
    }

    private void updateMethod(int iQty, String type,int position) {
        int iProductAmount = Integer.parseInt(arrayList.get(position).getPrice());
        int iTotalPrice = iQty * iProductAmount;
        if(type.equalsIgnoreCase("Update")) {
            String updateQuery = "UPDATE CART SET QTY='" + iQty + "',TOTALPRICE='" + iTotalPrice + "' WHERE CARTID='" + arrayList.get(position).getCartId() + "'";
            db.execSQL(updateQuery);

            ProductList list = new ProductList();
            list.setId(arrayList.get(position).getId());
            list.setSubCatId(arrayList.get(position).getSubCatId());
            list.setName(arrayList.get(position).getName());
            list.setPrice(arrayList.get(position).getPrice());
            list.setDesc(arrayList.get(position).getDesc());
            list.setImage(arrayList.get(position).getImage());
            list.setWishlist(arrayList.get(position).isWishlist());
            list.setCartId(arrayList.get(position).getCartId());
            list.setQty(iQty);
            arrayList.set(position,list);
            notifyDataSetChanged();

        }
        else{
            String deleteQuery = "DELETE FROM CART WHERE CARTID='" + arrayList.get(position).getCartId()+"'";
            db.execSQL(deleteQuery);
            Toast.makeText(context, "Product Removed From Cart Successfully", Toast.LENGTH_SHORT).show();

            ProductList list = new ProductList();
            list.setId(arrayList.get(position).getId());
            list.setSubCatId(arrayList.get(position).getSubCatId());
            list.setName(arrayList.get(position).getName());
            list.setPrice(arrayList.get(position).getPrice());
            list.setDesc(arrayList.get(position).getDesc());
            list.setImage(arrayList.get(position).getImage());
            list.setWishlist(arrayList.get(position).isWishlist());
            list.setCartId("0");
            list.setQty(0);
            arrayList.set(position,list);
            notifyDataSetChanged();

        }

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
        list.setCartId(arrayList.get(position).getCartId());
        list.setQty(arrayList.get(position).getQty());
        arrayList.set(position,list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
