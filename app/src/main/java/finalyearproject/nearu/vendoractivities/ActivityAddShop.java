package finalyearproject.nearu.vendoractivities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityAddShopBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.ShopsStruct;

/**
 * Created by deepakgavkar on 25/02/17.
 */
public class ActivityAddShop extends BaseActivity implements View.OnClickListener {

    ActivityAddShopBinding activityAddShopBinding;
    String status = "";
    TinyDB tinyDB;
    RequestQueue requestQueue;
    Bundle bundle;
    Gson gson;
    String sid = "";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddShopBinding = DataBindingUtil.setContentView(ActivityAddShop.this, R.layout.activity_add_shop);
        setSupportActionBar((Toolbar) activityAddShopBinding.toolbarActionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tinyDB = new TinyDB(this);
        gson = new Gson();
        setClicks();

        bundle = getIntent().getExtras();

        if (bundle != null) {
            ArrayList<ShopsStruct> shoptemp = new ArrayList<ShopsStruct>();
            Type s = new TypeToken<List<ShopsStruct>>() {
            }.getType();
            shoptemp = gson.fromJson(bundle.getString("jsonShops"), s);

            ShopsStruct shopsStruct = shoptemp.get(0);
            activityAddShopBinding.btnReset.setVisibility(View.GONE);
            activityAddShopBinding.btnAdd.setVisibility(View.GONE);
            activityAddShopBinding.btnUpdate.setVisibility(View.VISIBLE);
            sid = "";
            sid = shopsStruct.getSid();
            activityAddShopBinding.etShopName.setText(shopsStruct.getSname());
            activityAddShopBinding.etShopAddress.setText(shopsStruct.getSaddress());
            activityAddShopBinding.etShopMobileno.setText(shopsStruct.getSmobileno());
            if (shopsStruct.getStatus().equals("1")) {
                activityAddShopBinding.rbDeactive.setChecked(false);
                activityAddShopBinding.rbActive.setChecked(true);
                status = "1";
            } else {
                activityAddShopBinding.rbDeactive.setChecked(true);
                activityAddShopBinding.rbActive.setChecked(false);
                status = "0";
            }

            try {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle("Update Shop Details");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean validate() {
        if (activityAddShopBinding.etShopName.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter shop name");
            return false;
        }
        if (activityAddShopBinding.etShopAddress.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter shop address");
            return false;
        }
        if (activityAddShopBinding.etShopMobileno.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter mobile number");
            return false;
        }
        if (activityAddShopBinding.etShopMobileno.getText().toString().length() < 10) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid mobile number");
            return false;
        }
        if (status.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select status of shop");
            return false;
        }
        if (BaseActivity.mCurrentLocation == null) {
            if (!BaseActivity.isLocationEnabled(getApplicationContext())) {
                BaseActivity.displayPromptForEnablingGPS(ActivityAddShop.this);
                return false;
            } else if (BaseActivity.mCurrentLocation == null) {
                Utils.ShowShortToast(getApplicationContext(), "Please wait until your location is traced!");
                return false;
            }
        }
        return true;
    }

    public void setClicks() {
        activityAddShopBinding.btnAdd.setOnClickListener(this);
        activityAddShopBinding.btnReset.setOnClickListener(this);
        activityAddShopBinding.rbActive.setOnClickListener(this);
        activityAddShopBinding.rbDeactive.setOnClickListener(this);
        activityAddShopBinding.btnUpdate.setOnClickListener(this);
    }

    void onReset() {
        status = "";
        activityAddShopBinding.etShopAddress.setText("");
        activityAddShopBinding.etShopMobileno.setText("");
        activityAddShopBinding.etShopName.setText("");
        activityAddShopBinding.rbActive.setChecked(false);
        activityAddShopBinding.rbDeactive.setChecked(false);
    }

    void onActive() {
        status = "1";
        activityAddShopBinding.rbActive.setChecked(true);
        activityAddShopBinding.rbDeactive.setChecked(false);
    }

    void onDeactive() {
        status = "0";
        activityAddShopBinding.rbActive.setChecked(false);
        activityAddShopBinding.rbDeactive.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        if (v == activityAddShopBinding.btnAdd) {
            if (validate() == true) {
                Utils.showProgressDialog(ActivityAddShop.this, "Making request", "Please wait..");

                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("sname", activityAddShopBinding.etShopName.getText().toString());
                hashMap.put("saddress", activityAddShopBinding.etShopAddress.getText().toString());
                hashMap.put("smobile", activityAddShopBinding.etShopMobileno.getText().toString());
                hashMap.put("status", status);
                hashMap.put("vid", tinyDB.getString("vid"));
                hashMap.put("vname", tinyDB.getString("vname"));
                hashMap.put("slat", String.valueOf(BaseActivity.mCurrentLocation.getLatitude()));
                hashMap.put("slng", String.valueOf(BaseActivity.mCurrentLocation.getLongitude()));

                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.addShopAPI), hashMap, this.Success("0"), this.Fail());
                Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
                requestQueue.add(jsObjRequest);
            }
        } else if (v == activityAddShopBinding.btnReset) {
            onReset();
        } else if (v == activityAddShopBinding.rbActive) {
            onActive();
        } else if (v == activityAddShopBinding.rbDeactive) {
            onDeactive();
        } else if (v == activityAddShopBinding.btnUpdate) {
            if (validate() == true) {
                Utils.showProgressDialog(ActivityAddShop.this, "Making request", "Please wait..");

                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("sname", activityAddShopBinding.etShopName.getText().toString());
                hashMap.put("saddress", activityAddShopBinding.etShopAddress.getText().toString());
                hashMap.put("smobile", activityAddShopBinding.etShopMobileno.getText().toString());
                hashMap.put("status", status);
                hashMap.put("slat", String.valueOf(BaseActivity.mCurrentLocation.getLatitude()));
                hashMap.put("slng", String.valueOf(BaseActivity.mCurrentLocation.getLongitude()));
                hashMap.put("sid", sid);

                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.updateShopAPI), hashMap, this.Success("1"), this.Fail());
                Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
                requestQueue.add(jsObjRequest);
            }
        }
    }

    private Response.ErrorListener Fail() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.PrintErrorLog("Volley Error", "" + error);
                Utils.ShowShortToast(getApplicationContext(), "Network error occurred, Please make sure you have proper connectivity!");
                Utils.hideProgressDialog();
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> Success(final String flag) {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("pass")) {
                        onReset();
                        if (flag.equals("1")) {
                            Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
                            finish();
                        }
                        Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
                    } else if (response.has("fail")) {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return listener;
    }

}
