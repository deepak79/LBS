package finalyearproject.nearu.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import finalyearproject.nearu.R;
import finalyearproject.nearu.adapters.OfferAdaptor;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.vendoractivities.ActivityAddOffer;
import finalyearproject.nearu.vendoractivities.ActivityVendorMain;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class FragmentManageOffers extends BaseFragment {

    FloatingActionButton floatingActionButton;
    TinyDB tinyDB;
    ArrayList<OffersStruct> offersStructs = new ArrayList<OffersStruct>();
    RecyclerView RecyclerOfferList;
    RequestQueue requestQueue;
    TextView tvNoOffers;
    private OfferAdaptor offerAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_offers, container, false);
        if (ActivityVendorMain.actionBar != null) {
            ActivityVendorMain.actionBar.setTitle(getActivity().getResources().getString(R.string.manageoffers));
        }
        ActivityVendorMain.activityMainBinding.navigationView.getMenu().getItem(2).setChecked(true);
        tinyDB = new TinyDB(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());

        RecyclerOfferList = (RecyclerView) view.findViewById(R.id.RecyclerOfferList);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerOfferList.setLayoutManager(llm);
        tvNoOffers = (TextView) view.findViewById(R.id.tvNoOffers);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAddOffer);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityAddOffer.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getOffers();
        } else {
            showDialogs();
        }

        return view;
    }


    public void showDialogs() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("No internet, Make sure your connected to internet and press Ok");


        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.getConnectivityStatus(getActivity()) != 0) {
                    dialog.dismiss();
                    getOffers();
                } else {
                    dialog.dismiss();
                    showDialogs();
                }
            }
        });
        builderSingle.show();
    }

    private void getOffers() {
        Utils.showProgressDialog(getActivity(), "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("vid", tinyDB.getString("vid"));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getOffersAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
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

    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    JSONArray data = response.getJSONArray("data");
                    offersStructs.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject post = data.optJSONObject(i);
                        OffersStruct offersStruct = new OffersStruct();

                        offersStruct.setOid(post.optString("oid"));
                        offersStruct.setVid(post.optString("vid"));
                        offersStruct.setSid(post.optString("sid"));
                        offersStruct.setVtitle(post.optString("vtitle"));
                        offersStruct.setVdesc(post.optString("vdesc"));
                        offersStruct.setCatid(post.optString("catid"));
                        offersStruct.setCatname(post.optString("catname"));
                        offersStruct.setSname(post.optString("sname"));
                        offersStruct.setDiscounttype(post.optString("discounttype"));
                        offersStruct.setDiscount(post.optString("discount"));
                        offersStruct.setCoupon(post.optString("coupon"));
                        offersStruct.setCouponqr(post.optString("couponqr"));
                        offersStruct.setColor(post.optString("color"));
                        offersStruct.setLink(post.optString("link"));
                        offersStruct.setOfferlogo(post.optString("offerlogo"));
                        offersStruct.setOfferstartfrom(post.optString("offerstartfrom"));
                        offersStruct.setOfferexpierson(post.optString("offerexpierson"));
                        offersStruct.setPriority(post.optString("priority"));
                        offersStruct.setStatus(post.optString("status"));
                        offersStruct.setOncreatedon(post.optString("ocreatedon"));

                        offersStructs.add(offersStruct);
                    }
                    Utils.hideProgressDialog();
                    if (offersStructs.size() != 0) {
                        offerAdaptor = new OfferAdaptor(getActivity(), offersStructs);
                        RecyclerOfferList.setAdapter(offerAdaptor);
                        offerAdaptor.notifyDataSetChanged();
                        tvNoOffers.setVisibility(View.GONE);
                    } else {
                        tvNoOffers.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getOffers();
        } else {
            showDialogs();
        }

    }
}
