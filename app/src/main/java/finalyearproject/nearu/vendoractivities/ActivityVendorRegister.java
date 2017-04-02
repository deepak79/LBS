package finalyearproject.nearu.vendoractivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityVendorRegisterBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.ImageUtil;
import finalyearproject.nearu.helper.Utils;

/**
 * Created by deepakgavkar on 11/03/17.
 */
public class ActivityVendorRegister extends BaseActivity implements View.OnClickListener {

    public static final int INTENT_CAMERA_IMAGE = 1001;
    public static final int INTENT_GALLARY_IMAGE = 1002;
    File imageFile;
    public final static int QRcodeWidth = 512;
    ActivityVendorRegisterBinding activityVendorRegisterBinding;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVendorRegisterBinding = DataBindingUtil.setContentView(ActivityVendorRegister.this, R.layout.activity_vendor_register);
        setSupportActionBar((Toolbar) activityVendorRegisterBinding.toolbarActionbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        setOnClick();

    }

    public void setOnClick() {
        activityVendorRegisterBinding.profileImage.setOnClickListener(this);
        activityVendorRegisterBinding.btnRegister.setOnClickListener(this);
        activityVendorRegisterBinding.tvAlreadyUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == activityVendorRegisterBinding.profileImage) {
            showOptions();
        } else if (v == activityVendorRegisterBinding.btnRegister) {
            if (validate() == true) {
                if (Utils.getConnectivityStatus(getApplicationContext()) != 0) {
                    registerVendor();
                } else {
                    Utils.ShowShortToast(getApplicationContext(), "Network error occurred, Please make sure you have proper connectivity!");
                }
            }
        } else if (v == activityVendorRegisterBinding.tvAlreadyUser) {
            Intent i = new Intent(ActivityVendorRegister.this, ActivityVendorLogin.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        }
    }

    void showOptions() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityVendorRegister.this);
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
        Utils.verifyStoragePermissions(ActivityVendorRegister.this);
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
                activityVendorRegisterBinding.profileImage.setImageBitmap(myBitmap);
            }
        } else {
            imageFile = null;
            Utils.ShowShortToast(getApplicationContext(), "Error during image saving");
        }
    }

    public boolean validate() {
        if (imageFile == null) {
            Utils.ShowShortToast(getApplicationContext(), "Please select vendor image");
            return false;
        }
        if (activityVendorRegisterBinding.etName.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter vendor name");
            return false;
        }
        if (activityVendorRegisterBinding.etUsername.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter username");
            return false;
        }
        if (activityVendorRegisterBinding.etPassword.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter password");
            return false;
        }
        if (activityVendorRegisterBinding.etPassword1.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter password again");
            return false;
        }
        if (!activityVendorRegisterBinding.etPassword.getText().toString().equals(activityVendorRegisterBinding.etPassword1.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Passwords not matched");
            return false;
        }
        if (activityVendorRegisterBinding.etEmail.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter email");
            return false;
        }
        if (!Utils.isValidEmail(activityVendorRegisterBinding.etEmail.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid email");
            return false;
        }
        if (activityVendorRegisterBinding.etMobileNumber.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter mobile number");
            return false;
        }
        if (activityVendorRegisterBinding.etMobileNumber.getText().toString().length() < 10) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid mobile number");
            return false;
        }
        return true;
    }


    public void registerVendor() {
        if (validate() == true) {
            Utils.showProgressDialog(ActivityVendorRegister.this, "Making request", "Please wait..");

            final HashMap<String, String> hashMap = new HashMap<String, String>();

            activityVendorRegisterBinding.profileImage.buildDrawingCache();
            hashMap.put("profile", ImageUtil.convert(activityVendorRegisterBinding.profileImage.getDrawingCache()));
            hashMap.put("name", activityVendorRegisterBinding.etName.getText().toString());
            hashMap.put("username", activityVendorRegisterBinding.etUsername.getText().toString());
            hashMap.put("email", activityVendorRegisterBinding.etEmail.getText().toString());
            hashMap.put("password", activityVendorRegisterBinding.etPassword.getText().toString());
            hashMap.put("mobileno", activityVendorRegisterBinding.etMobileNumber.getText().toString());
            hashMap.put("ip", getResources().getString(R.string.baseURL));

            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.vregisterAPI), hashMap, this.Success(), this.Fail());
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

    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("pass")) {
                        Utils.ShowShortToast(getApplicationContext(), response.getString("pass"));
                        resetFields();
                        Intent i = new Intent(ActivityVendorRegister.this, ActivityVendorLogin.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ActivityVendorRegister.this, ActivityVendorLogin.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    public void resetFields() {
        activityVendorRegisterBinding.profileImage.setImageDrawable(null);
        activityVendorRegisterBinding.profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        activityVendorRegisterBinding.etName.setText("");
        activityVendorRegisterBinding.etMobileNumber.setText("");
        activityVendorRegisterBinding.etUsername.setText("");
        activityVendorRegisterBinding.etPassword1.setText("");
        activityVendorRegisterBinding.etPassword.setText("");
    }
}
