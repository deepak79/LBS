package finalyearproject.nearu.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import finalyearproject.nearu.adapters.CategoriesAdaptor;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.CategoryStruct;
import finalyearproject.nearu.useractivities.ActivityShowOffers;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class FragmentHome extends BaseFragment implements View.OnClickListener {
    GridView gridCategories;
    ArrayList<CategoryStruct> listCategories = new ArrayList<CategoryStruct>();
    RequestQueue requestQueue;
    TextView tvResults;
    Button btnTryAgain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gridCategories = (GridView) view.findViewById(R.id.gridCategories);
        tvResults = (TextView) view.findViewById(R.id.tvResults);
        btnTryAgain = (Button) view.findViewById(R.id.btnTryAgain);
        requestQueue = Volley.newRequestQueue(getActivity());
        btnTryAgain.setOnClickListener(this);
        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getData();
        } else {
            Utils.ShowShortToast(getActivity(), "Please make sure your connected to internert!");
            tvResults.setVisibility(View.VISIBLE);
            btnTryAgain.setVisibility(View.VISIBLE);
            gridCategories.setVisibility(View.GONE);
        }
        return view;
    }

    private void getData() {
        Utils.showProgressDialog(getActivity(), "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("key", getResources().getString(R.string.key));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getDataAPI), hashMap, this.Success1(), this.Fail1());
        Utils.PrintErrorLog("@@@### Params", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private Response.ErrorListener Fail1() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Utils.ShowShortToast(getActivity(), "Network error occurred, Please make sure you have proper connectivity!");
                Utils.hideProgressDialog();
                request();
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> Success1() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    JSONArray shops = response.getJSONArray("categories");

                    listCategories.clear();

                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject post = shops.optJSONObject(i);
                        CategoryStruct categoryStruct = new CategoryStruct();

                        categoryStruct.setCatid(post.optString("catid"));
                        categoryStruct.setCatname(post.optString("catname"));
                        categoryStruct.setCatlogo(post.optString("catlogo"));

                        listCategories.add(categoryStruct);
                    }
                    Utils.hideProgressDialog();
                    if (listCategories.size() > 0) {
                        tvResults.setVisibility(View.GONE);
                        btnTryAgain.setVisibility(View.GONE);
                        gridCategories.setVisibility(View.VISIBLE);
                        CategoriesAdaptor adapter1 = new CategoriesAdaptor(getActivity(), listCategories);
                        gridCategories.setAdapter(adapter1);
                        gridCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (BaseActivity.mCurrentLocation == null) {
                                    if (!BaseActivity.isLocationEnabled(getActivity())) {
                                        BaseActivity.displayPromptForEnablingGPS(getActivity());
                                    } else if (BaseActivity.mCurrentLocation == null) {
                                        Utils.ShowShortToast(getActivity(), "Please wait until your location is traced!");
                                    }
                                } else {
                                    CategoryStruct categoryStruct = listCategories.get(position);
                                    Intent i = new Intent(getActivity(), ActivityShowOffers.class);
                                    i.putExtra("catid", categoryStruct.getCatid());
                                    i.putExtra("catname", categoryStruct.getCatname());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().startActivity(i);
                                }
//                                CategoryStruct categoryStruct = listCategories.get(position);
//                                Intent i = new Intent(getActivity(), ActivityShowOffers.class);
//                                i.putExtra("catid", categoryStruct.getCatid());
//                                i.putExtra("catname", categoryStruct.getCatname());
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                getActivity().startActivity(i);
                            }
                        });
                    } else {
                        tvResults.setVisibility(View.VISIBLE);
                        btnTryAgain.setVisibility(View.VISIBLE);
                        gridCategories.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    tvResults.setVisibility(View.VISIBLE);
                    btnTryAgain.setVisibility(View.VISIBLE);
                    gridCategories.setVisibility(View.GONE);
                    Utils.ShowShortToast(getActivity(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }

    public void request() {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Try again?");


        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getData();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnTryAgain) {
            getData();
        }
    }
}
