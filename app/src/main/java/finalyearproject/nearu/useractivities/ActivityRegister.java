package finalyearproject.nearu.useractivities;

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
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityRegisterUserBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.ImageUtil;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 20/02/17.
 */
public class ActivityRegister extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final int INTENT_CAMERA_IMAGE = 1001;
    public static final int INTENT_GALLARY_IMAGE = 1002;
    ActivityRegisterUserBinding activityRegisterUserBinding;
    File imageFile;
    ArrayList<String> listCities = new ArrayList<String>();
    String gender = "";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterUserBinding = DataBindingUtil.setContentView(ActivityRegister.this, R.layout.activity_register_user);
        setSupportActionBar((Toolbar) activityRegisterUserBinding.toolbarActionbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        setOnClick();
        setAdapters();
    }

    public boolean validate() {
        if (activityRegisterUserBinding.etName.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter name");
            return false;
        }
        if (activityRegisterUserBinding.etPassword.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter password");
            return false;
        }
        if (activityRegisterUserBinding.etPassword1.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter password again");
            return false;
        }
        if (!activityRegisterUserBinding.etPassword.getText().toString().equals(activityRegisterUserBinding.etPassword1.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Passwords not matched");
            return false;
        }
        if (gender.equals("")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select gender");
            return false;
        }
        if (activityRegisterUserBinding.etMobileNumber.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter mobile number");
            return false;
        }
        if (activityRegisterUserBinding.etMobileNumber.getText().toString().length() < 10) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid mobile number");
            return false;
        }
        if (activityRegisterUserBinding.etEmail.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter email");
            return false;
        }
        if (!Utils.isValidEmail(activityRegisterUserBinding.etEmail.getText().toString())) {
            Utils.ShowShortToast(getApplicationContext(), "Please enter valid email");
            return false;
        }
        if (activityRegisterUserBinding.spCity.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            Utils.ShowShortToast(getApplicationContext(), "Please select city");
            return false;
        }
        if (activityRegisterUserBinding.etDob.getText().toString().length() < 1) {
            Utils.ShowShortToast(getApplicationContext(), "Please select your date of birth");
            return false;
        }
        return true;
    }

    public void setOnClick() {
        activityRegisterUserBinding.profileImage.setOnClickListener(this);
        activityRegisterUserBinding.btnRegister.setOnClickListener(this);
        activityRegisterUserBinding.etDob.setOnClickListener(this);
        activityRegisterUserBinding.tvAlreadyUser.setOnClickListener(this);
        activityRegisterUserBinding.rbMale.setOnClickListener(this);
        activityRegisterUserBinding.rbFemale.setOnClickListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog views, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        activityRegisterUserBinding.etDob.setText(date);
    }

    public void setAdapters() {
        listCities.clear();
        listCities.add("Select");
        listCities.add("Mumbai");
        listCities.add("Banglore");
        listCities.add("Chennai");
        listCities.add("Mysore");
        listCities.add("Delhi");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_dropdown, listCities);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        activityRegisterUserBinding.spCity.setAdapter(spinnerArrayAdapter);
    }

    public void resetFields() {
        activityRegisterUserBinding.profileImage.setImageDrawable(null);
        activityRegisterUserBinding.profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        activityRegisterUserBinding.etName.setText("");
        activityRegisterUserBinding.etMobileNumber.setText("");
        activityRegisterUserBinding.etPassword.setText("");
        activityRegisterUserBinding.etPassword1.setText("");
        activityRegisterUserBinding.etEmail.setText("");
        activityRegisterUserBinding.etDob.setText("");
        gender = "";
        activityRegisterUserBinding.spCity.setSelection(0);
        activityRegisterUserBinding.rbMale.setChecked(false);
        activityRegisterUserBinding.rbFemale.setChecked(false);
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

    void showOptions() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityRegister.this);
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

    private void saveImageOnSDCard(Bitmap bitmap) {
        Utils.verifyStoragePermissions(ActivityRegister.this);
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
                activityRegisterUserBinding.profileImage.setImageBitmap(myBitmap);
            }
        } else {
            imageFile = null;
            Utils.ShowShortToast(getApplicationContext(), "Error during image saving");
        }
    }

    public void showDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ActivityRegister.this,
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

    public void onMale() {
        gender = "M";
        activityRegisterUserBinding.rbFemale.setChecked(false);
    }

    public void onFemale() {
        gender = "F";
        activityRegisterUserBinding.rbMale.setChecked(false);
    }

    public void registerUser() {
        if (validate() == true) {
            Utils.showProgressDialog(ActivityRegister.this, "Making request", "Please wait..");

            final HashMap<String, String> hashMap = new HashMap<String, String>();

            activityRegisterUserBinding.profileImage.buildDrawingCache();
            hashMap.put("profile", ImageUtil.convert(activityRegisterUserBinding.profileImage.getDrawingCache()));
            hashMap.put("name", activityRegisterUserBinding.etName.getText().toString());
            hashMap.put("password", activityRegisterUserBinding.etPassword.getText().toString());
            hashMap.put("gender", gender);
            hashMap.put("mobileno", activityRegisterUserBinding.etMobileNumber.getText().toString());
            hashMap.put("email", activityRegisterUserBinding.etEmail.getText().toString());
            hashMap.put("city", activityRegisterUserBinding.spCity.getSelectedItem().toString());
            hashMap.put("dob", activityRegisterUserBinding.etDob.getText().toString());
            hashMap.put("ip", getResources().getString(R.string.baseURL));
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.registerAPI), hashMap, this.Success(), this.Fail());
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
                        Intent i = new Intent(ActivityRegister.this, ActivityLogin.class);
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
    public void onClick(View v) {
        if (v == activityRegisterUserBinding.profileImage) {
            showOptions();
        } else if (v == activityRegisterUserBinding.etDob) {
            showDate();
        } else if (v == activityRegisterUserBinding.rbMale) {
            onMale();
        } else if (v == activityRegisterUserBinding.rbFemale) {
            onFemale();
        } else if (v == activityRegisterUserBinding.btnRegister) {
            registerUser();
        } else if (v == activityRegisterUserBinding.tvAlreadyUser) {
            Intent i = new Intent(ActivityRegister.this, ActivityLogin.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }
}
