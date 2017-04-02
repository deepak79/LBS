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
import finalyearproject.nearu.vendoractivities.ActivityAddShop;
import finalyearproject.nearu.vendoractivities.ActivityVendorMain;
import finalyearproject.nearu.adapters.ShopAdaptor;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.ShopsStruct;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class FragmentManageShops extends BaseFragment {

    RequestQueue requestQueue;
    FloatingActionButton fabAddShops;
    ArrayList<ShopsStruct> shopsStructs = new ArrayList<ShopsStruct>();
    RecyclerView RecyclerShopsList;
    TinyDB tinyDB;
    TextView tvNoShops;
    private ShopAdaptor shopAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_shops, container, false);
        if(ActivityVendorMain.actionBar!=null){
            ActivityVendorMain.actionBar.setTitle(getActivity().getResources().getString(R.string.manageshops));
        }
        ActivityVendorMain.activityMainBinding.navigationView.getMenu().getItem(1).setChecked(true);
        requestQueue = Volley.newRequestQueue(getActivity());
        tinyDB = new TinyDB(getActivity());

        RecyclerShopsList = (RecyclerView) view.findViewById(R.id.RecyclerShopsList);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerShopsList.setLayoutManager(llm);
        tvNoShops = (TextView) view.findViewById(R.id.tvNoShops);
        fabAddShops = (FloatingActionButton) view.findViewById(R.id.fabAddShops);
        fabAddShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityAddShop.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getShops();
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

        hashMap.put("vid", tinyDB.getString("vid"));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.shopsAPI), hashMap, this.Success(), this.Fail());
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
                    JSONArray shops = response.getJSONArray("shops");
                    shopsStructs.clear();
                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject post = shops.optJSONObject(i);
                        ShopsStruct shopsStruct = new ShopsStruct();

                        shopsStruct.setSid(post.optString("sid"));
                        shopsStruct.setVid(post.optString("vid"));
                        shopsStruct.setVname(post.optString("vname"));
                        shopsStruct.setSname(post.optString("sname"));
                        shopsStruct.setSaddress(post.optString("saddress"));
                        shopsStruct.setSmobileno(post.optString("smobileno"));
                        shopsStruct.setStatus(post.optString("status"));
                        shopsStruct.setScreatedon(post.optString("screatedon"));

                        shopsStructs.add(shopsStruct);
                    }
                    Utils.hideProgressDialog();
                    if (shopsStructs.size() != 0) {
                        shopAdaptor = new ShopAdaptor(getActivity(), shopsStructs);
                        RecyclerShopsList.setAdapter(shopAdaptor);
                        shopAdaptor.notifyDataSetChanged();
                        tvNoShops.setVisibility(View.GONE);
                    } else {
                        tvNoShops.setVisibility(View.VISIBLE);
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
            getShops();
        } else {
            showDialogs();
        }
    }
}
