package finalyearproject.nearu.useractivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.adapters.CategoryOffersAdaptor;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.pojo.ShopsStruct;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class ActivityShowOffers extends BaseActivity {
    ArrayList<OffersStruct> offersStructs = new ArrayList<OffersStruct>();
    RecyclerView RecyclerOfferList;
    RequestQueue requestQueue;
    TextView tvNoOffers;
    TinyDB tinyDB;
    Bundle extras;
    String catid = "", catname = "";
    String shopid = "";
    Toolbar toolbar;
    Gson gson;
    private CategoryOffersAdaptor offerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_offers);

        tinyDB = new TinyDB(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        RecyclerOfferList = (RecyclerView) findViewById(R.id.RecyclerOfferList);
        final LinearLayoutManager llm = new LinearLayoutManager(ActivityShowOffers.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerOfferList.setLayoutManager(llm);
        tvNoOffers = (TextView) findViewById(R.id.tvNoOffers);
        gson = new Gson();
        extras = getIntent().getExtras();

        if (extras != null) {
            catid = extras.getString("catid");
            catname = extras.getString("catname");

            shopid = extras.getString("jsonShops");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(catname);
            }
            if (catid != null && !catid.equals("")) {
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    if (BaseActivity.mCurrentLocation == null) {
                        if (!BaseActivity.isLocationEnabled(getApplicationContext())) {
                            BaseActivity.displayPromptForEnablingGPS(ActivityShowOffers.this);
                        } else if (BaseActivity.mCurrentLocation == null) {
                            Utils.ShowShortToast(getApplicationContext(), "Please wait until your location is traced!");
                        }
                    } else {
                        getOffers(catid);
                    }
                } else {
                    showDialogs(catid);
                }
            }
            if (shopid != null && !shopid.equals("")) {

                ArrayList<ShopsStruct> shoptemp = new ArrayList<ShopsStruct>();
                Type s = new TypeToken<List<ShopsStruct>>() {
                }.getType();
                shoptemp = gson.fromJson(shopid, s);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(shoptemp.get(0).getSname() + " Offers");
                }
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    getShopOffers(shoptemp.get(0).getSid());
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "No internet, Make sure your connected to internet and press Ok");
                }
            }

        } else {
            Utils.ShowShortToast(getApplicationContext(), "Please try again!");
            finish();
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void showDialogs(final String catid) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityShowOffers.this);
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
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    dialog.dismiss();
                    if (BaseActivity.mCurrentLocation == null) {
                        if (!BaseActivity.isLocationEnabled(getApplicationContext())) {
                            BaseActivity.displayPromptForEnablingGPS(ActivityShowOffers.this);
                        } else if (BaseActivity.mCurrentLocation == null) {
                            Utils.ShowShortToast(getApplicationContext(), "Please wait until your location is traced!");
                        }
                    } else {
                        getOffers(catid);
                    }
                } else {
                    dialog.dismiss();
                    showDialogs(catid);
                }
            }
        });
        builderSingle.show();
    }


    private void getShopOffers(String shopID) {
        Utils.showProgressDialog(ActivityShowOffers.this, "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("sid", shopID);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getShopOffersAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Params", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private void getOffers(String catid) {
        Utils.showProgressDialog(ActivityShowOffers.this, "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("catid", catid);
        hashMap.put("ulat", String.valueOf(BaseActivity.mCurrentLocation.getLatitude()));
        hashMap.put("ulng", String.valueOf(BaseActivity.mCurrentLocation.getLongitude()));


        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getCatOffersAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Params", hashMap.toString());
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
                    Utils.hideProgressDialog();
                    if (response.has("offers")) {
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
                            offersStruct.setVlogo(post.optString("vlogo"));
                            offersStruct.setSname(post.optString("sname"));
                            offersStruct.setDiscounttype(post.optString("discounttype"));
                            offersStruct.setDiscount(post.optString("discount"));
                            offersStruct.setCoupon(post.optString("coupon"));
                            offersStruct.setCouponqr(post.optString("couponqr"));
                            offersStruct.setColor(post.optString("color"));
                            offersStruct.setLink(post.optString("link"));
                            offersStruct.setDistance(post.optString("distance"));
                            offersStruct.setSaddress(post.optString("saddress"));
                            offersStruct.setSlat(post.optString("slat"));
                            offersStruct.setSlng(post.optString("slng"));
                            offersStruct.setScontactno(post.optString("scontactno"));
                            offersStruct.setOfferlogo(post.optString("offerlogo"));
                            offersStruct.setOfferstartfrom(post.optString("offerstartfrom"));
                            offersStruct.setOfferexpierson(post.optString("offerexpierson"));
                            offersStruct.setPriority(post.optString("priority"));
                            offersStruct.setStatus(post.optString("status"));
                            offersStruct.setClicks(post.optString("clicks"));
                            offersStruct.setOncreatedon(post.optString("ocreatedon"));

                            offersStructs.add(offersStruct);
                        }
                        if (offersStructs.size() != 0) {
                            offerAdaptor = new CategoryOffersAdaptor(ActivityShowOffers.this, offersStructs);
                            RecyclerOfferList.setAdapter(offerAdaptor);
                            offerAdaptor.notifyDataSetChanged();
                            tvNoOffers.setVisibility(View.GONE);
                        } else {
                            tvNoOffers.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvNoOffers.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getApplicationContext(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }

}
