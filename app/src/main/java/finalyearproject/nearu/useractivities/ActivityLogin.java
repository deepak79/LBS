package finalyearproject.nearu.useractivities;

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
import finalyearproject.nearu.vendoractivities.BaseActivity;
import finalyearproject.nearu.databinding.ActivityLoginBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;

/**
 * Created by deepakgavkar on 20/02/17.
 */
public class ActivityLogin extends BaseActivity implements View.OnClickListener {

    ActivityLoginBinding activityLoginBinding;
    RequestQueue requestQueue;
    TinyDB tinyDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = DataBindingUtil.setContentView(ActivityLogin.this, R.layout.activity_login);
        setSupportActionBar((Toolbar) activityLoginBinding.toolbarActionbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.ulogin));
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tinyDB = new TinyDB(this);
        setClick();
    }

    public void setClick() {
        activityLoginBinding.btnLogin.setOnClickListener(this);
        activityLoginBinding.tvNewUser.setOnClickListener(this);
    }

    public void doLogin() {
        if (validate() == true) {
            Utils.showProgressDialog(ActivityLogin.this, "Making request", "Please wait..");

            final HashMap<String, String> hashMap = new HashMap<String, String>();

            hashMap.put("email", activityLoginBinding.etEmail.getText().toString());
            hashMap.put("password", activityLoginBinding.etPassword.getText().toString());

            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.loginAPI), hashMap, this.Success(), this.Fail());
            Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
            requestQueue.add(jsObjRequest);
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


    public void resetFields() {
        activityLoginBinding.etEmail.setText("");
        activityLoginBinding.etPassword.setText("");
    }

    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("pass")) {

                        resetFields();
                        JSONArray pass = response.getJSONArray("pass");
                        JSONObject data = pass.getJSONObject(0);

                        tinyDB.putString("uid", data.getString("uid"));
                        tinyDB.putString("uname", data.getString("uname"));
                        tinyDB.putString("uprofile", data.getString("uprofile"));
                        tinyDB.putString("upassword", data.getString("upassword"));
                        tinyDB.putString("ugender", data.getString("ugender"));
                        tinyDB.putString("umobileno", data.getString("umobileno"));
                        tinyDB.putString("uemail", data.getString("uemail"));
                        tinyDB.putString("ucity", data.getString("ucity"));
                        tinyDB.putString("udob", data.getString("udob"));
                        tinyDB.putString("status", data.getString("status"));
                        tinyDB.putString("creationdate", data.getString("creationdate"));
                        tinyDB.putString("fav", data.getString("fav"));

                        if (data.getString("ugender").equals("M")) {
                            Utils.ShowShortToast(getApplicationContext(), "Welcome Mr." + data.getString("uname"));
                        } else if (data.getString("ugender").equals("F")) {
                            Utils.ShowShortToast(getApplicationContext(), "Welcome Ms." + data.getString("uname"));
                        }

                        Intent i = new Intent(ActivityLogin.this, ActivityMain.class);
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
        if (activityLoginBinding.etEmail.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter email");
            return false;
        }
        if (!Utils.isValidEmail(activityLoginBinding.etEmail.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid email");
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
        } else if (v == activityLoginBinding.tvNewUser) {
            Intent i = new Intent(ActivityLogin.this, ActivityRegister.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }
}
