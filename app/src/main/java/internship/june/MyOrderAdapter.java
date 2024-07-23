package internship.june;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyHolder> {

    Context context;
    ArrayList<OrderList> arrayList;
    SharedPreferences sp;

    public MyOrderAdapter(Context context, ArrayList<OrderList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sp = context.getSharedPreferences(ConstantSp.PREF,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_order,parent,false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView orderNo,price,address,paymentType;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            orderNo = itemView.findViewById(R.id.custom_my_order_no);
            price = itemView.findViewById(R.id.custom_my_order_amount);
            address = itemView.findViewById(R.id.custom_my_order_address);
            paymentType = itemView.findViewById(R.id.custom_my_order_payment_type);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.orderNo.setText("Order No : "+arrayList.get(position).getOrderId());
        holder.price.setText(ConstantSp.PRICE_SYMBOL+arrayList.get(position).getAmount());
        holder.address.setText(arrayList.get(position).getAddress()+"\n"+arrayList.get(position).getCity());
        if(arrayList.get(position).getTransactionId().equalsIgnoreCase("")){
            holder.paymentType.setText(arrayList.get(position).getPaymentType());
        }
        else{
            holder.paymentType.setText(arrayList.get(position).getPaymentType()+" ("+arrayList.get(position).getTransactionId()+")");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.ORDERID,arrayList.get(position).getOrderId()).commit();
                sp.edit().putString(ConstantSp.ORDER_ADDRESS,arrayList.get(position).getAddress()).commit();
                sp.edit().putString(ConstantSp.ORDER_CITY,arrayList.get(position).getCity()).commit();
                sp.edit().putString(ConstantSp.ORDER_PRICE,arrayList.get(position).getAmount()).commit();
                sp.edit().putString(ConstantSp.ORDER_PAYMENT_TYPE,arrayList.get(position).getPaymentType()).commit();
                sp.edit().putString(ConstantSp.ORDER_TRANSACTION_ID,arrayList.get(position).getTransactionId()).commit();

                Intent intent = new Intent(context,OrderDetailActivity.class);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
