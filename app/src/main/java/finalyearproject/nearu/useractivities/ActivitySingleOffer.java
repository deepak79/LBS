package finalyearproject.nearu.useractivities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivitySingleOfferBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class ActivitySingleOffer extends BaseActivity implements View.OnClickListener {

    ActivitySingleOfferBinding activitySingleOfferBinding;
    Bundle extras;
    String offerstruct = "";
    ArrayList<OffersStruct> offersStructs = new ArrayList<OffersStruct>();
    Gson gson;
    OffersStruct offer = null;
    RequestQueue requestQueue;
    String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySingleOfferBinding = DataBindingUtil.setContentView(ActivitySingleOffer.this, R.layout.activity_single_offer);
        setSupportActionBar((Toolbar) activitySingleOfferBinding.toolbarActionbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setOnClicks();
        extras = getIntent().getExtras();
        gson = new Gson();
        if (extras != null) {
            offerstruct = extras.getString("offer");

            Type type = new TypeToken<List<OffersStruct>>() {
            }.getType();
            offersStructs = gson.fromJson(offerstruct, type);

            offer = offersStructs.get(0);
            getSupportActionBar().setTitle(offer.getVtitle());
            if (offer != null) {
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    checkFav(offer.getSid(), new TinyDB(ActivitySingleOffer.this).getString("uid"));
                    Picasso.with(getApplicationContext()).load(offer.getOfferlogo()).into(activitySingleOfferBinding.imgOfferImage);
                    Picasso.with(getApplicationContext()).load(offer.getCouponqr()).into(activitySingleOfferBinding.imgQR);
                    activitySingleOfferBinding.tvTitle.setText(offer.getVtitle());
                    activitySingleOfferBinding.tvDesc.setText(offer.getVdesc());
                    activitySingleOfferBinding.tvValidity.setText(offer.getOfferstartfrom() + " To " + offer.getOfferexpierson());
                    activitySingleOfferBinding.tvShopName.setText(offer.getSname());
                    activitySingleOfferBinding.tvAddress.setText(offer.getSaddress());
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "Please make sure your connected to internet!");
                    finish();
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public void setOnClicks() {
        activitySingleOfferBinding.imgQR.setOnClickListener(this);
        activitySingleOfferBinding.imgCall.setOnClickListener(this);
        activitySingleOfferBinding.imgShare.setOnClickListener(this);
        activitySingleOfferBinding.imgFavourite.setOnClickListener(this);
        activitySingleOfferBinding.imgLocation.setOnClickListener(this);
    }


    public void showDialogs(String QR) {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(ActivitySingleOffer.this);
        builderSingle.setTitle("QR Code");
        builderSingle.setMessage(QR);
        builderSingle.setCancelable(false);
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builderSingle.create();
        alert.show();
        alert.getWindow().getAttributes();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(40);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void showCallDialog(final String contactNumber) {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(ActivitySingleOffer.this);
        builderSingle.setTitle("Shop Contact Number");
        builderSingle.setMessage(contactNumber);
        builderSingle.setCancelable(false);
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + contactNumber));
                if (ActivityCompat.checkSelfPermission(ActivitySingleOffer.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                ActivitySingleOffer.this.startActivity(intent);
            }
        });
        AlertDialog alert = builderSingle.create();
        alert.show();
        alert.getWindow().getAttributes();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(30);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == activitySingleOfferBinding.imgQR) {
            if (offer != null) {
                showDialogs(offer.getCoupon());
            }
        } else if (v == activitySingleOfferBinding.imgCall) {
            if (offer != null) {
                showCallDialog(offer.getScontactno());
            }
        } else if (v == activitySingleOfferBinding.imgShare) {
            if (offer != null) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, offer.getLink());
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        } else if (v == activitySingleOfferBinding.imgLocation) {
            if (offer != null) {
                try {
                    Dialog dialog = new Dialog(ActivitySingleOffer.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_map);
                    GoogleMap googleMap;

                    MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                    MapsInitializer.initialize(getApplicationContext());

                    mMapView = (MapView) dialog.findViewById(R.id.mapView);
                    mMapView.onCreate(dialog.onSaveInstanceState());
                    mMapView.onResume();// needed to get the map to display immediately
                    googleMap = mMapView.getMap();
                    LatLng latLng = new LatLng(Double.parseDouble(offer.getSlat()), Double.parseDouble(offer.getSlng()));
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(offer.getVtitle())
                            .snippet(offer.getVdesc()));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (v == activitySingleOfferBinding.imgFavourite) {
            if (flag.equals("1")) {
                flag = "0";
                activitySingleOfferBinding.imgFavourite.setBackground(getResources().getDrawable(R.drawable.fav));
                removeFav(offer.getSid(), new TinyDB(ActivitySingleOffer.this).getString("uid"));
            } else if (flag.equals("0")) {
                flag = "1";
                activitySingleOfferBinding.imgFavourite.setBackground(getResources().getDrawable(R.drawable.favred));
                addFav(offer.getSid(), new TinyDB(ActivitySingleOffer.this).getString("uid"));
            }
        }
    }

    public void checkFav(String sid, String uid) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("oid", sid);
        hashMap.put("uid", uid);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getFavAPI), hashMap, this.Success1(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    public void addFav(String sid, String uid) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("oid", sid);
        hashMap.put("uid", uid);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.addFavAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    public void removeFav(String sid, String uid) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("oid", sid);
        hashMap.put("uid", uid);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.removeFavAPI), hashMap, this.Success(), this.Fail());
        Utils.PrintErrorLog("@@@### Parms", hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    private Response.Listener<JSONObject> Success1() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("pass")) {
                        flag = response.getString("pass");
                        if (response.getString("pass").equals("1")) {
                            activitySingleOfferBinding.imgFavourite.setBackground(getResources().getDrawable(R.drawable.favred));
                        } else if (response.getString("pass").equals("0")) {
                            activitySingleOfferBinding.imgFavourite.setBackground(getResources().getDrawable(R.drawable.fav));
                        }
                    }
                } catch (Exception e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getApplicationContext(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }


    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON Response", response.toString());

                try {
                    if (response.has("pass")) {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
                    } else {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getApplicationContext(), "Failed to fetch data..");
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
                Utils.ShowShortToast(getApplicationContext(), "Please make sure you have proper connectivity!");
            }
        };

        return err;
    }


}
