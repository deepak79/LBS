package finalyearproject.nearu.useractivities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityChangePassBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 14/03/17.
 */
public class ActivityChangePass extends BaseActivity implements View.OnClickListener {

    ActivityChangePassBinding activityChangePassBinding;
    TinyDB tinyDB;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChangePassBinding = DataBindingUtil.setContentView(ActivityChangePass.this, R.layout.activity_change_pass);
        setSupportActionBar((Toolbar) activityChangePassBinding.toolbarActionbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tinyDB = new TinyDB(ActivityChangePass.this);
        activityChangePassBinding.btnUpdate.setOnClickListener(this);
    }

    public void reset(){
        activityChangePassBinding.etOldPassword.setText("");
        activityChangePassBinding.etPassword.setText("");
        activityChangePassBinding.etPassword1.setText("");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean validate() {
        if (activityChangePassBinding.etOldPassword.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter your old password");
            return false;
        }
        if (!activityChangePassBinding.etOldPassword.getText().toString().equals(tinyDB.getString("upassword"))) {
            Utils.ShowShortToast(getApplicationContext(), "Your old password not matched");
            return false;
        }
        if (activityChangePassBinding.etPassword.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter your new password");
            return false;
        }
        if (activityChangePassBinding.etPassword1.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter your new password again");
            return false;
        }
        if (!activityChangePassBinding.etPassword.getText().toString().equals(activityChangePassBinding.etPassword1.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Both new password not matched");
            return false;
        }
        return true;
    }


    public void changePass() {
        Utils.showProgressDialog(ActivityChangePass.this, "Making request", "Please wait..");

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("password", activityChangePassBinding.etPassword.getText().toString());
        hashMap.put("uid", tinyDB.getString("uid"));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.changePassAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
        requestQueue.add(jsObjRequest);
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
                        reset();
                        finish();
                        Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
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

    @Override
    public void onClick(View v) {
        if (v == activityChangePassBinding.btnUpdate) {
            if (validate() == true) {
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    changePass();
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "Network error occurred, Please make sure you have proper connectivity!");

                }
            }
        }
    }
}
