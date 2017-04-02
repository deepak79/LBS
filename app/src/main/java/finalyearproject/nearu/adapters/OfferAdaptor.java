package finalyearproject.nearu.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import finalyearproject.nearu.vendoractivities.ActivityAddOffer;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class OfferAdaptor extends RecyclerView.Adapter<OfferAdaptor.OffersHolder> {

    RequestQueue requestQueue;
    Gson gson;
    private List<OffersStruct> offersStructs;
    private Context context;
    View.OnClickListener Active = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OffersHolder holder = (OffersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(true);
            holder.rbDeactive.setChecked(false);

            OffersStruct offersStruct = offersStructs.get(position);

            set(offersStruct.getOid(), "1");
        }
    };
    View.OnClickListener Deactive = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OffersHolder holder = (OffersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            holder.rbActive.setChecked(false);
            holder.rbDeactive.setChecked(true);

            OffersStruct offersStruct = offersStructs.get(position);

            set(offersStruct.getOid(), "0");

        }
    };

    View.OnClickListener Edit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OffersHolder holder = (OffersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            OffersStruct offersStruct = offersStructs.get(position);

            ArrayList<OffersStruct> offtemp = new ArrayList<OffersStruct>();

            offtemp.add(offersStruct);

            Intent i = new Intent(context, ActivityAddOffer.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String jsonOffers = gson.toJson(offtemp);
            i.putExtra("jsonOffers", jsonOffers);
            context.startActivity(i);
        }
    };

    View.OnClickListener ShowQR = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OffersHolder holder = (OffersHolder) view.getTag();
            int position = holder.getAdapterPosition();

            OffersStruct offersStruct = offersStructs.get(position);

            Dialog builder = new Dialog(context);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });

            ImageView imageView = new ImageView(context);
            Picasso.with(context).load(offersStruct.getCouponqr()).into(imageView);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            builder.show();
        }
    };


    public OfferAdaptor(Context context, List<OffersStruct> offersStructs) {
        this.offersStructs = offersStructs;
        this.context = context;
    }

    public void set(String id, String status) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", id);
        hashMap.put("status", status);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, context.getResources().getString(R.string.baseURL) + context.getResources().getString(R.string.offeractiveAPI), hashMap, this.Success1(status), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    @Override
    public OffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_offer, null);
        OffersHolder fp = new OffersHolder(v);
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
                            Utils.ShowShortToast(context, "Offer status has been changed to active");
                        } else if (status.equals("0")) {
                            Utils.ShowShortToast(context, "Offer status has been changed to deactive");
                        }
                    } else {
                        Utils.ShowShortToast(context, "Failed to change the status of shop!");
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
    public void onBindViewHolder(OffersHolder holder, int position) {
        try {
            try {
                OffersStruct offersStruct = offersStructs.get(position);

                holder.Touch.setOnClickListener(Edit);
                holder.Touch.setTag(holder);

                holder.btnQRCode.setOnClickListener(ShowQR);
                holder.btnQRCode.setTag(holder);

                holder.rbActive.setOnClickListener(Active);
                holder.rbActive.setTag(holder);

                holder.rbDeactive.setOnClickListener(Deactive);
                holder.rbDeactive.setTag(holder);

                holder.imgEdit.setOnClickListener(Edit);
                holder.imgEdit.setTag(holder);

                holder.tvCounter.setText("" + (position + 1));
                holder.tvTitle.setText("Title : " + offersStruct.getVtitle());
                holder.tvShopName.setText("Shop Name : " + offersStruct.getSname());
                holder.tvCouponCode.setText("Coupon Code : " + offersStruct.getCoupon());
                if (offersStruct.getDiscounttype().equals("Per.")) {
                    holder.tvDiscount.setText("Discount : " + offersStruct.getDiscount() + " % OFF");
                } else if (offersStruct.getDiscounttype().equals("Rs.")) {
                    holder.tvDiscount.setText("Discount : " + offersStruct.getDiscount() + " Rs. OFF");
                }

                if (offersStruct.getStatus().equals("1")) {
                    holder.rbActive.setChecked(true);
                    holder.rbDeactive.setChecked(false);
                } else if (offersStruct.getStatus().equals("0")) {
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
        return (null != offersStructs ? offersStructs.size() : 0);
    }

    class OffersHolder extends RecyclerView.ViewHolder {

        public Context context;
        public TextView tvTitle, tvCouponCode, tvDiscount, tvCounter, tvShopName;
        public RelativeLayout Touch;
        public AppCompatRadioButton rbActive, rbDeactive;
        public ImageView imgEdit;
        public Button btnQRCode;

        public OffersHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            this.tvShopName = (TextView) itemView.findViewById(R.id.tvShopName);
            this.imgEdit = (ImageView) itemView.findViewById(R.id.imgEdit);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvCounter = (TextView) itemView.findViewById(R.id.counter);
            this.tvCouponCode = (TextView) itemView.findViewById(R.id.tvCouponCode);
            this.tvDiscount = (TextView) itemView.findViewById(R.id.tvDiscount);
            this.Touch = (RelativeLayout) itemView.findViewById(R.id.Touch);
            this.rbActive = (AppCompatRadioButton) itemView.findViewById(R.id.rbActive);
            this.rbDeactive = (AppCompatRadioButton) itemView.findViewById(R.id.rbDeactive);
            this.btnQRCode = (Button) itemView.findViewById(R.id.btnQRCode);
        }
    }
}
