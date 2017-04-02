package finalyearproject.nearu.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.pojo.ShopsStruct;
import finalyearproject.nearu.vendoractivities.ActivityVendorMain;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class FragmentDashboard extends BaseFragment {

    Spinner spShop, spOffer;

    ArrayList<ShopsStruct> shopsStructs = new ArrayList<ShopsStruct>();
    //offer clicks,shops,usersnear;
    RequestQueue requestQueue;
    ArrayList<OffersStruct> offersStructs = new ArrayList<OffersStruct>();
    TextView tvShops, tvOfferClicks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        spShop = (Spinner) view.findViewById(R.id.spShop);
        spOffer = (Spinner) view.findViewById(R.id.spOffer);
        if (ActivityVendorMain.actionBar != null) {
            ActivityVendorMain.actionBar.setTitle(getActivity().getResources().getString(R.string.dashboard));
        }

        tvShops = (TextView) view.findViewById(R.id.tvShops);
        tvOfferClicks = (TextView) view.findViewById(R.id.tvOfferClicks);

        ArrayList<String> offername = new ArrayList<String>();
        offername.add("Select Offer");
        ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_dropdown, offername);
        type.setDropDownViewResource(R.layout.spinner_item_dropdown);
        spOffer.setAdapter(type);

        requestQueue = Volley.newRequestQueue(getActivity());

        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getShops();
        } else {
            showDialogs();
        }

        spShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    if (!shopsStructs.get(position).getVid().equals("")) {
                        getOffers(shopsStructs.get(position).getSid());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                try {
                    if (!shopsStructs.get(1).getVid().equals("")) {
                        getOffers(shopsStructs.get(1).getSid());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        spOffer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    if(offersStructs.size() > 0){
                        if (!offersStructs.get(position).getClicks().equals("")) {
                            tvOfferClicks.setText(offersStructs.get(position).getClicks()+" \n Offer Clicks");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                try {
                    if(offersStructs.size() > 0){
                    if (!offersStructs.get(1).getVid().equals("")) {
                        tvOfferClicks.setText(offersStructs.get(1).getClicks()+" \n Offer Clicks");
                    }}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        return view;
    }

    public void showDialogs() {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(getActivity());
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
                    getShops();
                } else {
                    dialog.dismiss();
                    showDialogs();
                }
            }
        });
        builderSingle.show();
    }


    private void getShops() {
        Utils.showProgressDialog(getActivity(), "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("vid", new TinyDB(getActivity()).getString("vid"));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.shopsAPI), hashMap, this.Success2(), this.Fail2());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener Fail2() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> Success2() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    JSONArray shops = response.getJSONArray("shops");
                    ShopsStruct shopsStruct1 = new ShopsStruct();

                    shopsStruct1.setSid("");
                    shopsStruct1.setVid("");
                    shopsStruct1.setVname("");
                    shopsStruct1.setSname("Select Shop");
                    shopsStruct1.setSaddress("");
                    shopsStruct1.setSmobileno("");
                    shopsStruct1.setStatus("");
                    shopsStruct1.setScreatedon("");
                    shopsStruct1.setSlat("");
                    shopsStruct1.setSlng("");

                    shopsStructs.add(shopsStruct1);

                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject post = shops.optJSONObject(i);
                        ShopsStruct shopsStruct = new ShopsStruct();

                        shopsStruct.setSid(post.optString("sid"));
                        shopsStruct.setVid(post.optString("vid"));
                        shopsStruct.setVname(post.optString("vname"));
                        shopsStruct.setSname(post.optString("sname"));
                        shopsStruct.setSlat(post.optString("slat"));
                        shopsStruct.setSlng(post.optString("slng"));
                        shopsStruct.setSaddress(post.optString("saddress"));
                        shopsStruct.setSmobileno(post.optString("smobileno"));
                        shopsStruct.setStatus(post.optString("status"));
                        shopsStruct.setScreatedon(post.optString("screatedon"));

                        shopsStructs.add(shopsStruct);
                    }
                    tvShops.setText(shopsStructs.size()+" \n Shops");
                    Utils.hideProgressDialog();
                    ArrayAdapter<ShopsStruct> type = new ArrayAdapter<ShopsStruct>(getActivity(), R.layout.spinner_item_dropdown, shopsStructs);
                    type.setDropDownViewResource(R.layout.spinner_item_dropdown);
                    spShop.setAdapter(type);
                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }

    String getVid(String shopName) {
        String id = "";
        for (int i = 0; i < shopsStructs.size(); i++) {
            if (shopName.equals(shopsStructs.get(i).getSname())) {
                id = shopsStructs.get(i).getVid();
            }
        }
        return id;
    }
    private void getOffers(String id) {
        Utils.showProgressDialog(getActivity(), "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("sid", id);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getShopOffersAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener Fail() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hideProgressDialog();
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
                    JSONArray data = response.getJSONArray("offers");
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
                        offersStruct.setClicks(post.optString("clicks"));

                        offersStructs.add(offersStruct);
                    }
                    Utils.hideProgressDialog();
                    ArrayList<String> offername = new ArrayList<String>();
                    offername.add("Select Offer");
                    for (int i = 0; i < offersStructs.size(); i++) {
                        if (offersStructs.get(i).getDiscounttype().equals("Per.")) {
                            offername.add(offersStructs.get(i).getDiscount() + " % OFF");
                        } else if (offersStructs.get(i).getDiscounttype().equals("Rs.")) {
                            offername.add(offersStructs.get(i).getDiscount() + " RS. OFF");
                        }
                    }
                    ArrayAdapter<String> type = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_dropdown, offername);
                    type.setDropDownViewResource(R.layout.spinner_item_dropdown);
                    spOffer.setAdapter(type);

                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getActivity(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }


}
