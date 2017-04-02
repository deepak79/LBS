package finalyearproject.nearu.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.vendoractivities.ActivityAddShop;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.ShopsStruct;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class ShopAdaptor extends RecyclerView.Adapter<ShopAdaptor.ShopsHolder> {

    RequestQueue requestQueue;
    Gson gson;
    private List<ShopsStruct> shopsStructs;
    View.OnClickListener Upper = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShopsHolder holder = (ShopsHolder) view.getTag();
            int position = holder.getAdapterPosition();

            ShopsStruct shopsStruct = shopsStructs.get(position);

            ArrayList<ShopsStruct> shoptemp = new ArrayList<ShopsStruct>();

            shoptemp.add(shopsStruct);

            Intent i = new Intent(context, ActivityAddShop.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String jsonShops = gson.toJson(shoptemp);
            i.putExtra("jsonShops", jsonShops);
            context.startActivity(i);
        }
    };
    private Context context;
    View.OnClickListener Active = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShopsHolder holder = (ShopsHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(true);
            holder.rbDeactive.setChecked(false);

            ShopsStruct shopsStruct = shopsStructs.get(position);

            set(shopsStruct.getSid(), "1");
        }
    };
    View.OnClickListener Deactive = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShopsHolder holder = (ShopsHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(false);
            holder.rbDeactive.setChecked(true);

            ShopsStruct shopsStruct = shopsStructs.get(position);
            set(shopsStruct.getSid(), "0");

        }
    };


    public ShopAdaptor(Context context, List<ShopsStruct> shopsStructs) {
        this.shopsStructs = shopsStructs;
        this.context = context;
    }

    public void set(String id, String status) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", id);
        hashMap.put("status", status);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, context.getResources().getString(R.string.baseURL) + context.getResources().getString(R.string.shopactiveAPI), hashMap, this.Success1(status), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    @Override
    public ShopsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_shop, null);
        ShopsHolder fp = new ShopsHolder(v);
        requestQueue = Volley.newRequestQueue(parent.getContext());
        gson = new Gson();
        return fp;
    }


    private Response.Listener<JSONObject> Success1(final String status) {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("pass")) {
                        if (status.equals("1")) {
                            Utils.ShowShortToast(context, "Shop status has been changed to active");
                        } else if (status.equals("0")) {
                            Utils.ShowShortToast(context, "Shop status has been changed to deactive");
                        }
                    }else{
                        Utils.ShowShortToast(context,"Failed to change the status of shop!");
                    }
                } catch (Exception e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(context, "Failed to fetch data..");
                }
            }
        };
        return listener;
    }


    private Response.ErrorListener Fail() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        };

        return err;
    }

    @Override
    public void onBindViewHolder(ShopsHolder holder, int position) {
        try {
            try {
                ShopsStruct shopsStruct = shopsStructs.get(position);

                holder.Touch.setOnClickListener(Upper);
                holder.Touch.setTag(holder);

                holder.rbActive.setOnClickListener(Active);
                holder.rbActive.setTag(holder);

                holder.rbDeactive.setOnClickListener(Deactive);
                holder.rbDeactive.setTag(holder);

                holder.tvVendorName.setText("Shop Name : " + shopsStruct.getSname());
                holder.tvShopAddress.setText("Shop Address : " + shopsStruct.getSaddress());
                holder.tvShopContactNo.setText("Contact No. : " + shopsStruct.getSmobileno());

                if (shopsStruct.getStatus().equals("1")) {
                    holder.rbActive.setChecked(true);
                    holder.rbDeactive.setChecked(false);
                } else if (shopsStruct.getStatus().equals("0")) {
                    holder.rbDeactive.setChecked(true);
                    holder.rbActive.setChecked(false);
                }
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
        public TextView tvVendorName, tvShopAddress, tvShopContactNo;
        public RelativeLayout Touch;
        public AppCompatRadioButton rbActive, rbDeactive;

        public ShopsHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            this.tvShopAddress = (TextView) itemView.findViewById(R.id.tvShopAddress);
            this.tvShopContactNo = (TextView) itemView.findViewById(R.id.tvShopContactNo);
            this.Touch = (RelativeLayout) itemView.findViewById(R.id.Touch);
            this.rbActive = (AppCompatRadioButton) itemView.findViewById(R.id.rbActive);
            this.rbDeactive = (AppCompatRadioButton) itemView.findViewById(R.id.rbDeactive);
        }
    }
}
