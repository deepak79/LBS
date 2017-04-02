package finalyearproject.nearu.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.pojo.ShopsStruct;
import finalyearproject.nearu.useractivities.ActivityShowOffers;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class FavShopAdaptor extends RecyclerView.Adapter<FavShopAdaptor.ShopsHolder> {

    RequestQueue requestQueue;
    Gson gson;
    private List<ShopsStruct> shopsStructs;
    private Context context;
    View.OnClickListener Upper = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShopsHolder holder = (ShopsHolder) view.getTag();
            int position = holder.getAdapterPosition();

            ShopsStruct shopsStruct = shopsStructs.get(position);

            ArrayList<ShopsStruct> shoptemp = new ArrayList<ShopsStruct>();

            shoptemp.add(shopsStruct);

            Intent i = new Intent(context, ActivityShowOffers.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String jsonShops = gson.toJson(shoptemp);
            i.putExtra("jsonShops", jsonShops);
            context.startActivity(i);
        }
    };

    public FavShopAdaptor(Context context, List<ShopsStruct> shopsStructs) {
        this.shopsStructs = shopsStructs;
        this.context = context;
    }

    @Override
    public ShopsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_fav_shop, null);
        ShopsHolder fp = new ShopsHolder(v);
        requestQueue = Volley.newRequestQueue(parent.getContext());
        gson = new Gson();
        return fp;
    }

    @Override
    public void onBindViewHolder(ShopsHolder holder, int position) {
        try {
            try {
                ShopsStruct shopsStruct = shopsStructs.get(position);

                holder.Touch.setOnClickListener(Upper);
                holder.Touch.setTag(holder);


                holder.tvCounter.setText("" + (position + 1));
                holder.tvVendorName.setText("Shop Name : " + shopsStruct.getSname());
                holder.tvShopAddress.setText("Shop Address : " + shopsStruct.getSaddress());
                holder.tvShopContactNo.setText("Contact No. : " + shopsStruct.getSmobileno());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != shopsStructs ? shopsStructs.size() : 0);
    }

    class ShopsHolder extends RecyclerView.ViewHolder {

        public Context context;
        public TextView tvVendorName, tvShopAddress, tvShopContactNo, tvCounter;
        public RelativeLayout Touch;

        public ShopsHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            this.tvShopAddress = (TextView) itemView.findViewById(R.id.tvShopAddress);
            this.tvShopContactNo = (TextView) itemView.findViewById(R.id.tvShopContactNo);
            this.tvCounter = (TextView) itemView.findViewById(R.id.counter);
            this.Touch = (RelativeLayout) itemView.findViewById(R.id.Touch);
        }
    }
}
