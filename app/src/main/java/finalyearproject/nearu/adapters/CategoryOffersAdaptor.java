package finalyearproject.nearu.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.useractivities.ActivitySingleOffer;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class CategoryOffersAdaptor extends RecyclerView.Adapter<CategoryOffersAdaptor.OffersHolder> {

    RequestQueue requestQueue;
    Gson gson;
    private List<OffersStruct> offersStructs;
    private Context context;
    View.OnClickListener Open = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OffersHolder holder = (OffersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            OffersStruct offersStruct = offersStructs.get(position);

            ArrayList<OffersStruct> offtemp = new ArrayList<OffersStruct>();

            offtemp.add(offersStruct);

            addVClicks(offtemp.get(0).getOid());

            Intent i = new Intent(context, ActivitySingleOffer.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String jsonOffers = gson.toJson(offtemp);
            i.putExtra("offer", jsonOffers);
            context.startActivity(i);
        }
    };

    public CategoryOffersAdaptor(Context context, List<OffersStruct> offersStructs) {
        this.offersStructs = offersStructs;
        this.context = context;
    }

    @Override
    public OffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_categories_offer, null);
        OffersHolder fp = new OffersHolder(v);
        requestQueue = Volley.newRequestQueue(parent.getContext());
        gson = new Gson();
        return fp;
    }

    @Override
    public void onBindViewHolder(OffersHolder holder, int position) {
        try {
            try {
                OffersStruct offersStruct = offersStructs.get(position);

                holder.Touch.setOnClickListener(Open);
                holder.Touch.setTag(holder);

                holder.imgOpen.setOnClickListener(Open);
                holder.imgOpen.setTag(holder);

                holder.Touch.setBackgroundColor(Color.parseColor(offersStruct.getColor()));

                if (!offersStruct.getDistance().equals("")) {
                    holder.tvMeters.setText(offersStruct.getDistance() + " KM away");
                }

                Picasso.with(context).load(offersStruct.getVlogo()).into(holder.imgShopLogo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != offersStructs ? offersStructs.size() : 0);
    }

    public void addVClicks(String offerid) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("offerid", offerid);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, context.getResources().getString(R.string.baseURL) + context.getResources().getString(R.string.addClicksAPI), hashMap, this.Success1(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private Response.Listener<JSONObject> Success1() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());
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

    class OffersHolder extends RecyclerView.ViewHolder {

        public Context context;
        public RelativeLayout Touch;
        public ImageView imgOpen, imgShopLogo;
        public TextView tvMeters;

        public OffersHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.Touch = (RelativeLayout) itemView.findViewById(R.id.Touch);
            this.imgOpen = (ImageView) itemView.findViewById(R.id.imgOpen);
            this.imgShopLogo = (ImageView) itemView.findViewById(R.id.imgShopLogo);
            this.tvMeters = (TextView) itemView.findViewById(R.id.tvMeters);
        }
    }
}
