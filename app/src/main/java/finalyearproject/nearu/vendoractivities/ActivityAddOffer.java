package finalyearproject.nearu.vendoractivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityAddOfferBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.ImageUtil;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.pojo.CategoryStruct;
import finalyearproject.nearu.pojo.ColorsStruct;
import finalyearproject.nearu.pojo.OffersStruct;
import finalyearproject.nearu.pojo.ShopsStruct;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

/**
 * Created by deepakgavkar on 25/02/17.
 */
public class ActivityAddOffer extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final int INTENT_CAMERA_IMAGE = 1001;
    public static final int INTENT_GALLARY_IMAGE = 1002;
    ActivityAddOfferBinding activityAddOfferBinding;
    RequestQueue requestQueue;
    TinyDB tinyDB;
    ArrayList<String> listDiscountType = new ArrayList<String>();
    ArrayList<String> listPriorities = new ArrayList<String>();
    ArrayList<CategoryStruct> listCategories = new ArrayList<CategoryStruct>();
    File imageFile;
    String offerImage = "", status = "";
    Gson gson;
    ArrayList<ShopsStruct> shopsStructs = new ArrayList<ShopsStruct>();
    ArrayList<ColorsStruct> colorsStructs = new ArrayList<ColorsStruct>();
    View editTextView = null;
    Bundle bundle;
    Bitmap qrCode = null;

    public void showDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ActivityAddOffer.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog views, int year, int monthOfYear, int dayOfMonth) {
        String month = String.valueOf(++monthOfYear);
        String day = String.valueOf(dayOfMonth);
        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }

        if (Integer.parseInt(day) < 10) {
            day = "0" + day;
        }
        String date = day + "/" + month + "/" + year;
        if (editTextView == activityAddOfferBinding.etOfferStart) {
            activityAddOfferBinding.etOfferStart.setText(date);
        } else if (editTextView == activityAddOfferBinding.etOfferExpire) {
            activityAddOfferBinding.etOfferExpire.setText(date);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddOfferBinding = DataBindingUtil.setContentView(ActivityAddOffer.this, R.layout.activity_add_offer);
        setSupportActionBar((Toolbar) activityAddOfferBinding.toolbarActionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tinyDB = new TinyDB(this);
        init();
        setClicks();
        gson = new Gson();

        if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
            getData();
            getShops();
        } else {
            showDialogs();
        }
    }

    public int shopPosition(String shopname) {
        int pos = 0;

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopname)) {
                pos = i;
            }
        }
        return pos;
    }

    public int getPriority(String text) {
        int pos = 0;

        for (int i = 0; i < listPriorities.size(); i++) {
            if (listPriorities.get(i).equals(text)) {
                pos = i;
            }
        }
        return pos;
    }

    public int getDiscountType(String text) {
        int pos = 0;

        for (int i = 0; i < listDiscountType.size(); i++) {
            if (listDiscountType.get(i).equals(text)) {
                pos = i;
            }
        }
        return pos;
    }

    public int getCategory(String text) {
        int pos = 0;

        for (int i = 0; i < listCategories.size(); i++) {
            CategoryStruct categoryStruct = listCategories.get(i);
            if (categoryStruct.getCatname().equals(text)) {
                pos = i;
            }
        }
        return pos;
    }


    void setClicks() {
        activityAddOfferBinding.btnAdd.setOnClickListener(this);
        activityAddOfferBinding.btnOfferImage.setOnClickListener(this);
        activityAddOfferBinding.btnReset.setOnClickListener(this);
        activityAddOfferBinding.etOfferExpire.setOnClickListener(this);
        activityAddOfferBinding.etOfferStart.setOnClickListener(this);
        activityAddOfferBinding.rbActive.setOnClickListener(this);
        activityAddOfferBinding.rbDeactive.setOnClickListener(this);
        activityAddOfferBinding.btnUpdate.setOnClickListener(this);
    }

    void init() {
        listDiscountType.clear();
        listDiscountType.add("Select Discount Type");
        listDiscountType.add("Rs.");
        listDiscountType.add("Per.");

        ArrayAdapter<String> type = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_dropdown, listDiscountType);
        type.setDropDownViewResource(R.layout.spinner_item_dropdown);
        activityAddOfferBinding.spDiscountType.setAdapter(type);

        listPriorities.clear();
        listPriorities.add("Select Priority");
        listPriorities.add("Low");
        listPriorities.add("Medium");
        listPriorities.add("High");

        ArrayAdapter<String> pr = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_dropdown, listPriorities);
        pr.setDropDownViewResource(R.layout.spinner_item_dropdown);
        activityAddOfferBinding.spPriority.setAdapter(pr);

    }

    boolean validate() {
        if (activityAddOfferBinding.etTitle.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter title");
            return false;
        }
        if (activityAddOfferBinding.etDesc.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter description");
            return false;
        }
        if (activityAddOfferBinding.spShop.getSelectedItem().toString().equals("Select Shop")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select shop");
            return false;
        }
        if (activityAddOfferBinding.spCategory.getSelectedItem().toString().equals("Select Category")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select shop category");
            return false;
        }
        if (activityAddOfferBinding.spDiscountType.getSelectedItem().toString().equals("Select Discount Type")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select discount type");
            return false;
        }
        if (activityAddOfferBinding.etDiscount.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter discount");
            return false;
        }
        if (activityAddOfferBinding.etCouponCode.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter coupon code");
            return false;
        }
        if (activityAddOfferBinding.spColor.getSelectedItem().toString().equals("Select Color")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select color setting");
            return false;
        }
        if (activityAddOfferBinding.etLink.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter link");
            return false;
        }
        if (activityAddOfferBinding.etOfferStart.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please select offer start date");
            return false;
        }
        if (activityAddOfferBinding.etOfferExpire.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please select offer expiry date");
            return false;
        }
        if (activityAddOfferBinding.spPriority.getSelectedItem().toString().equals("Select Priority")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select priority");
            return false;
        }
        if (offerImage.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select offer image");
            return false;
        }
        if (status.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select offer status");
            return false;
        }
        if (activityAddOfferBinding.spDiscountType.getSelectedItem().equals("Per.")) {
            if (Integer.parseInt(activityAddOfferBinding.etDiscount.getText().toString()) > 99) {
                Utils.ShowShortToast(getApplicationContext(), "Discount percentage must be less than 100");
                return false;
            }
        }
        return true;
    }

    void onReset() {
        activityAddOfferBinding.etTitle.setText("");
        activityAddOfferBinding.etDesc.setText("");
        activityAddOfferBinding.etDiscount.setText("");
        activityAddOfferBinding.etOfferExpire.setText("");
        activityAddOfferBinding.etOfferStart.setText("");
        activityAddOfferBinding.etCouponCode.setText("");
        activityAddOfferBinding.etLink.setText("");
        activityAddOfferBinding.spCategory.setSelection(0);
        activityAddOfferBinding.spColor.setSelection(0);
        activityAddOfferBinding.spDiscountType.setSelection(0);
        activityAddOfferBinding.spPriority.setSelection(0);
        activityAddOfferBinding.spShop.setSelection(0);
        activityAddOfferBinding.rbActive.setChecked(false);
        activityAddOfferBinding.rbDeactive.setChecked(false);
        offerImage = "";
        status = "";
    }

    String getShopID(String shopname) {
        String id = "";

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopname)) {
                id = shopsStruct.getSid();
            }
        }

        return id;
    }

    String getShopLat(String shopname) {
        String lat = "";

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopname)) {
                lat = shopsStruct.getSlat();
            }
        }

        return lat;
    }


    String getShopLng(String shopname) {
        String lng = "";

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopname)) {
                lng = shopsStruct.getSlng();
            }
        }

        return lng;
    }


    String getCatID(String catname) {
        String id = "";

        for (int i = 0; i < listCategories.size(); i++) {
            CategoryStruct categoryStruct = listCategories.get(i);
            if (categoryStruct.getCatname().equals(catname)) {
                id = categoryStruct.getCatid();
            }
        }

        return id;
    }

    @Override
    public void onClick(View v) {
        if (v == activityAddOfferBinding.btnReset) {
            onReset();
        } else if (v == activityAddOfferBinding.btnAdd) {
            if (validate() == true) {
                Utils.showProgressDialog(ActivityAddOffer.this, "Making request", "Please wait..");

                try {
                    qrCode = null;
                    qrCode = new QrGenerator.Builder()
                            .content(activityAddOfferBinding.etCouponCode.getText().toString())
                            .qrSize(300)
                            .margin(2)
                            .color(Color.BLACK)
                            .bgColor(Color.WHITE)
                            .ecc(ErrorCorrectionLevel.H)
                            .overlaySize(100)
                            .overlayAlpha(255)
                            .overlayXfermode(PorterDuff.Mode.SRC_ATOP)
                            .encode();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("title", activityAddOfferBinding.etTitle.getText().toString());
                hashMap.put("desc", activityAddOfferBinding.etDesc.getText().toString());
                hashMap.put("catname", activityAddOfferBinding.spCategory.getSelectedItem().toString());
                hashMap.put("cat", getCatID(activityAddOfferBinding.spCategory.getSelectedItem().toString()));
                hashMap.put("discounttype", activityAddOfferBinding.spDiscountType.getSelectedItem().toString());
                hashMap.put("discount", activityAddOfferBinding.etDiscount.getText().toString());
                hashMap.put("couponcode", activityAddOfferBinding.etCouponCode.getText().toString());
                hashMap.put("color", getColor(activityAddOfferBinding.spColor.getSelectedItem().toString()));
                hashMap.put("link", activityAddOfferBinding.etLink.getText().toString());
                hashMap.put("offerstart", activityAddOfferBinding.etOfferStart.getText().toString());
                hashMap.put("offerexpire", activityAddOfferBinding.etOfferExpire.getText().toString());
                hashMap.put("sname", activityAddOfferBinding.spShop.getSelectedItem().toString());
                hashMap.put("sid", getShopID(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("sname", activityAddOfferBinding.spShop.getSelectedItem().toString());
                hashMap.put("slat", getShopLat(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("slng", getShopLng(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("vid", tinyDB.getString("vid"));
                hashMap.put("vlogo", tinyDB.getString("vlogo"));
                hashMap.put("saddress",getAddress(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("scontactno",getContactNo(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("priority", activityAddOfferBinding.spPriority.getSelectedItem().toString());
                hashMap.put("offerimage", offerImage);
                hashMap.put("status", status);
                hashMap.put("ip", getResources().getString(R.string.baseURL));
                hashMap.put("couponqr", ImageUtil.convert(qrCode));
                if (qrCode != null) {
                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.addOfferAPI), hashMap, this.Success("0"), this.Fail());
                    Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
                    requestQueue.add(jsObjRequest);
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "Failed to generate QR code!");
                }

            }
        } else if (v == activityAddOfferBinding.btnOfferImage) {
            showOptions();
        } else if (v == activityAddOfferBinding.etOfferStart) {
            showDate();
            editTextView = activityAddOfferBinding.etOfferStart;
        } else if (v == activityAddOfferBinding.etOfferExpire) {
            showDate();
            editTextView = activityAddOfferBinding.etOfferExpire;
        } else if (v == activityAddOfferBinding.rbActive) {
            onActive();
        } else if (v == activityAddOfferBinding.rbDeactive) {
            onDeactive();
        } else if (v == activityAddOfferBinding.btnUpdate) {
            if (validate() == true) {
                Utils.showProgressDialog(ActivityAddOffer.this, "Making request", "Please wait..");


                try {
                    qrCode = null;
                    qrCode = new QrGenerator.Builder()
                            .content(activityAddOfferBinding.etCouponCode.getText().toString())
                            .qrSize(300)
                            .margin(2)
                            .color(Color.BLACK)
                            .bgColor(Color.WHITE)
                            .ecc(ErrorCorrectionLevel.H)
                            .overlaySize(100)
                            .overlayAlpha(255)
                            .overlayXfermode(PorterDuff.Mode.SRC_ATOP)
                            .encode();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("title", activityAddOfferBinding.etTitle.getText().toString());
                hashMap.put("desc", activityAddOfferBinding.etDesc.getText().toString());
                hashMap.put("catname", activityAddOfferBinding.spCategory.getSelectedItem().toString());
                hashMap.put("cat", getCatID(activityAddOfferBinding.spCategory.getSelectedItem().toString()));
                hashMap.put("discounttype", activityAddOfferBinding.spDiscountType.getSelectedItem().toString());
                hashMap.put("discount", activityAddOfferBinding.etDiscount.getText().toString());
                hashMap.put("couponcode", activityAddOfferBinding.etCouponCode.getText().toString());
                hashMap.put("color", getColor(activityAddOfferBinding.spColor.getSelectedItem().toString()));
                hashMap.put("link", activityAddOfferBinding.etLink.getText().toString());
                hashMap.put("offerstart", activityAddOfferBinding.etOfferStart.getText().toString());
                hashMap.put("offerexpire", activityAddOfferBinding.etOfferExpire.getText().toString());
                hashMap.put("sname", activityAddOfferBinding.spShop.getSelectedItem().toString());
                hashMap.put("sid", getShopID(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("sname", activityAddOfferBinding.spShop.getSelectedItem().toString());
                hashMap.put("vid", tinyDB.getString("vid"));
                hashMap.put("vlogo", tinyDB.getString("vlogo"));
                hashMap.put("scontactno",getContactNo(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("saddress",getAddress(activityAddOfferBinding.spShop.getSelectedItem().toString()));
                hashMap.put("priority", activityAddOfferBinding.spPriority.getSelectedItem().toString());
                hashMap.put("offerimage", offerImage);
                hashMap.put("status", status);
                hashMap.put("oid", offerImage);
                hashMap.put("ip", getResources().getString(R.string.baseURL));
                hashMap.put("couponqr", ImageUtil.convert(qrCode));
                if (qrCode != null) {
                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.updateOfferAPI), hashMap, this.Success("1"), this.Fail());
                    Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
                    requestQueue.add(jsObjRequest);
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "Failed to generate QR code!");
                }
            }
        }
    }

    void onActive() {
        status = "1";
        activityAddOfferBinding.rbActive.setChecked(true);
        activityAddOfferBinding.rbDeactive.setChecked(false);
    }

    void onDeactive() {
        status = "0";
        activityAddOfferBinding.rbActive.setChecked(false);
        activityAddOfferBinding.rbDeactive.setChecked(true);
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

    void showOptions() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityAddOffer.this);
        String options[] = {"Open from Gallery", "Take from Camera"};
        builderSingle.setTitle("Select");
        builderSingle.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, INTENT_GALLARY_IMAGE);
                } else if (position == 1) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, INTENT_CAMERA_IMAGE);
                }
            }
        });
        AlertDialog alertDialog = builderSingle.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_CAMERA_IMAGE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                saveImageOnSDCard(photo);
            }

            if (requestCode == INTENT_GALLARY_IMAGE) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgString = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap photo = BitmapFactory.decodeFile(imgString);
                    saveImageOnSDCard(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImageOnSDCard(Bitmap bitmap) {
        Utils.verifyStoragePermissions(ActivityAddOffer.this);
        if (bitmap == null) {
            Utils.ShowShortToast(getApplicationContext(), "Image Not Available Please Try Again.!!");
            return;
        }
        boolean success = false;

        File sdCardDirectory = Environment.getExternalStorageDirectory();
        String imageName = UUID.randomUUID().toString();
        imageFile = new File(sdCardDirectory, imageName + ".png");

        FileOutputStream outStream;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            if (bitmap != null) {
                bitmap.recycle();
            }
            outStream = new FileOutputStream(imageFile);
            outStream.write(bytes.toByteArray());
            outStream.flush();
            outStream.close();
            outStream = null;
            success = true;
        } catch (FileNotFoundException e) {
            imageFile = null;
            e.printStackTrace();
        } catch (IOException e) {
            imageFile = null;
            e.printStackTrace();
        }
        if (success) {
            if (imageFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                offerImage = ImageUtil.convert(myBitmap);
            }
        } else {
            imageFile = null;
            Utils.ShowShortToast(getApplicationContext(), "Error during image saving");
        }
    }

    private void getData() {
        Utils.showProgressDialog(ActivityAddOffer.this, "Please wait", "Getting data...");
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
                Utils.ShowShortToast(ActivityAddOffer.this, "Network error occurred, Please make sure you have proper connectivity!");
                Utils.hideProgressDialog();
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

                    CategoryStruct categoryStruct1 = new CategoryStruct();

                    categoryStruct1.setCatid("");
                    categoryStruct1.setCatname("Select Category");
                    categoryStruct1.setCatlogo("");

                    listCategories.add(categoryStruct1);

                    for (int i = 0; i < shops.length(); i++) {
                        JSONObject post = shops.optJSONObject(i);
                        CategoryStruct categoryStruct = new CategoryStruct();

                        categoryStruct.setCatid(post.optString("catid"));
                        categoryStruct.setCatname(post.optString("catname"));
                        categoryStruct.setCatlogo(post.optString("catlogo"));

                        listCategories.add(categoryStruct);
                    }
                    Utils.hideProgressDialog();

                    ArrayAdapter<CategoryStruct> type = new ArrayAdapter<CategoryStruct>(getApplicationContext(), R.layout.spinner_item_dropdown, listCategories);
                    type.setDropDownViewResource(R.layout.spinner_item_dropdown);
                    activityAddOfferBinding.spCategory.setAdapter(type);
                } catch (JSONException e) {
                    Utils.hideProgressDialog();
                    e.printStackTrace();
                    Utils.ShowShortToast(getApplicationContext(), "Failed to fetch data..");
                }
            }
        };
        return listener;
    }


    public void showDialogs() {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
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
                    getData();
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
        Utils.showProgressDialog(ActivityAddOffer.this, "Please wait", "Getting data...");
        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("vid", tinyDB.getString("vid"));

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
                    JSONArray colors = response.getJSONArray("colors");
                    shopsStructs.clear();
                    colorsStructs.clear();


                    ColorsStruct colorsStruct = new ColorsStruct();

                    colorsStruct.setColorid("");
                    colorsStruct.setColorcode("");
                    colorsStruct.setColorname("Select Color");

                    colorsStructs.add(colorsStruct);

                    for (int i = 0; i < colors.length(); i++) {
                        JSONObject post = colors.optJSONObject(i);
                        ColorsStruct colorsStruct1 = new ColorsStruct();

                        colorsStruct1.setColorid(post.optString("colorid"));
                        colorsStruct1.setColorcode(post.optString("colorcode"));
                        colorsStruct1.setColorname(post.optString("colorname"));

                        colorsStructs.add(colorsStruct1);
                    }

                    ArrayAdapter<ColorsStruct> cr = new ArrayAdapter<ColorsStruct>(getApplicationContext(), R.layout.spinner_item_dropdown, colorsStructs);
                    cr.setDropDownViewResource(R.layout.spinner_item_dropdown);
                    activityAddOfferBinding.spColor.setAdapter(cr);

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
                    Utils.hideProgressDialog();
                    ArrayAdapter<ShopsStruct> type = new ArrayAdapter<ShopsStruct>(getApplicationContext(), R.layout.spinner_item_dropdown, shopsStructs);
                    type.setDropDownViewResource(R.layout.spinner_item_dropdown);
                    activityAddOfferBinding.spShop.setAdapter(type);

                    try {
                        bundle = getIntent().getExtras();
                        if (bundle != null) {
                            ArrayList<OffersStruct> offtemp = new ArrayList<OffersStruct>();
                            Type s = new TypeToken<List<OffersStruct>>() {
                            }.getType();
                            offtemp = gson.fromJson(bundle.getString("jsonOffers"), s);

                            OffersStruct offersStruct = offtemp.get(0);
                            activityAddOfferBinding.etTitle.setText(offersStruct.getVtitle());
                            activityAddOfferBinding.etDesc.setText(offersStruct.getVdesc());
                            activityAddOfferBinding.spShop.setSelection(shopPosition(offersStruct.getSname()));
                            activityAddOfferBinding.spDiscountType.setSelection(getDiscountType(offersStruct.getDiscounttype()));
                            activityAddOfferBinding.spCategory.setSelection(getCategory(offersStruct.getCatname()));
                            activityAddOfferBinding.spColor.setSelection(getColorPos(offersStruct.getColor()));
                            activityAddOfferBinding.spPriority.setSelection(getPriority(offersStruct.getPriority()));
                            activityAddOfferBinding.etDiscount.setText(offersStruct.getDiscount());
                            activityAddOfferBinding.etCouponCode.setText(offersStruct.getCoupon());
                            activityAddOfferBinding.etDiscount.setText(offersStruct.getDiscount());
                            activityAddOfferBinding.etLink.setText(offersStruct.getLink());
                            activityAddOfferBinding.etOfferExpire.setText(offersStruct.getOfferexpierson());
                            activityAddOfferBinding.etOfferStart.setText(offersStruct.getOfferstartfrom());
                            if (offersStruct.getStatus().equals("1")) {
                                activityAddOfferBinding.rbDeactive.setChecked(false);
                                activityAddOfferBinding.rbActive.setChecked(true);
                                status = "1";
                            } else {
                                activityAddOfferBinding.rbDeactive.setChecked(true);
                                activityAddOfferBinding.rbActive.setChecked(false);
                                status = "0";
                            }
                            activityAddOfferBinding.btnReset.setVisibility(View.GONE);
                            activityAddOfferBinding.btnAdd.setVisibility(View.GONE);
                            activityAddOfferBinding.btnOfferImage.setVisibility(View.GONE);
                            activityAddOfferBinding.btnUpdate.setVisibility(View.VISIBLE);
                            offerImage = offersStruct.getOid();

                            try {
                                ActionBar actionBar = getSupportActionBar();
                                actionBar.setTitle("Update Offer Details");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    public String getColor(String colorName) {
        String color = "";

        for (int i = 0; i < colorsStructs.size(); i++) {
            ColorsStruct colorsStruct = colorsStructs.get(i);
            if (colorsStruct.getColorname().equals(colorName)) {
                color = colorsStruct.getColorcode();
            }
        }

        return color;
    }

    public String getAddress(String shopName) {
        String address = "";

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopName)) {
                address = shopsStruct.getSaddress();
            }
        }

        return address;
    }

    public String getContactNo(String shopName) {
        String address = "";

        for (int i = 0; i < shopsStructs.size(); i++) {
            ShopsStruct shopsStruct = shopsStructs.get(i);
            if (shopsStruct.getSname().equals(shopName)) {
                address = shopsStruct.getSmobileno();
            }
        }

        return address;
    }


    public int getColorPos(String colorName) {
        int pos = 0;

        for (int i = 0; i < colorsStructs.size(); i++) {
            ColorsStruct colorsStruct = colorsStructs.get(i);
            if (colorsStruct.getColorcode().equals(colorName)) {
                pos = i;
            }
        }

        return pos;
    }
}
