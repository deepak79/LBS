package finalyearproject.nearu.vendoractivities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityVloginBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class ActivityVendorLogin extends BaseActivity implements View.OnClickListener {

    ActivityVloginBinding activityLoginBinding;

    TinyDB tinyDB;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = DataBindingUtil.setContentView(ActivityVendorLogin.this, R.layout.activity_vlogin);
        setSupportActionBar((Toolbar) activityLoginBinding.toolbarActionbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.vendorlogin));
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tinyDB = new TinyDB(this);
        setClick();
    }

    public void setClick() {
        activityLoginBinding.btnLogin.setOnClickListener(this);
        activityLoginBinding.tvNormalLogin.setOnClickListener(this);
    }

    public void doLogin() {
        if (validate() == true) {
            Utils.showProgressDialog(ActivityVendorLogin.this, "Making request", "Please wait..");

            final HashMap<String, String> hashMap = new HashMap<String, String>();

            hashMap.put("username", activityLoginBinding.etUsername.getText().toString());
            hashMap.put("password", activityLoginBinding.etPassword.getText().toString());

            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.vloginAPI), hashMap, this.Success(), this.Fail());
            Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
            requestQueue.add(jsObjRequest);
        }
    }


    public void resetFields() {
        activityLoginBinding.etUsername.setText("");
        activityLoginBinding.etPassword.setText("");
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

    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("pass")) {
                        JSONArray data = response.getJSONArray("pass");
                        JSONObject data0 = data.getJSONObject(0);

                        tinyDB.putString("vusername", data0.getString("vusername"));
                        tinyDB.putString("vname", data0.getString("vname"));
                        tinyDB.putString("vpassword", data0.getString("vpassword"));
                        tinyDB.putString("vmobileno", data0.getString("vmobileno"));
                        tinyDB.putString("vlogo", data0.getString("vlogo"));
                        tinyDB.putString("vclicks", data0.getString("vclicks"));
                        tinyDB.putString("vid", data0.getString("vid"));

                        Utils.ShowShortToast(getApplicationContext(), "Welcome " + data0.getString("vusername"));
                        resetFields();

                        Intent i = new Intent(ActivityVendorLogin.this, ActivityVendorMain.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return listener;
    }


    public boolean validate() {
        if (activityLoginBinding.etUsername.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter username");
            return false;
        }
        if (activityLoginBinding.etPassword.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter password");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == activityLoginBinding.btnLogin) {
            doLogin();
        } else if (v == activityLoginBinding.tvNormalLogin) {
            Intent i = new Intent(ActivityVendorLogin.this, ActivityVendorRegister.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }
}
