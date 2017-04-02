package finalyearproject.nearu.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.FragmentProfileBinding;
import finalyearproject.nearu.helper.CustomRequest;
import finalyearproject.nearu.helper.ImageUtil;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.helper.Utils;
import finalyearproject.nearu.useractivities.ActivityChangePass;
import finalyearproject.nearu.useractivities.ActivityMain;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class FragmentProfile extends BaseFragment implements View.OnClickListener {


    public static final int INTENT_CAMERA_IMAGE = 1001;
    public static final int INTENT_GALLARY_IMAGE = 1002;
    File imageFile;
    RequestQueue requestQueue;
    TinyDB tinyDB;
    FragmentProfileBinding fragmentProfileBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        tinyDB = new TinyDB(getActivity());
        setClicks();
        if (Utils.getConnectivityStatus(getActivity()) != 0) {
            getDetails();
        }
        return fragmentProfileBinding.getRoot();
    }

    public void setClicks() {
        fragmentProfileBinding.profileImage.setOnClickListener(this);
        fragmentProfileBinding.btnSave.setOnClickListener(this);
        fragmentProfileBinding.btnChangePass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fragmentProfileBinding.profileImage) {
            showOptions();
        } else if (v == fragmentProfileBinding.btnSave) {
            if (validate() == true) {
                Utils.showProgressDialog(getActivity(), "Making request", "Please wait..");

                final HashMap<String, String> hashMap = new HashMap<String, String>();

                fragmentProfileBinding.profileImage.buildDrawingCache();
                hashMap.put("profile", ImageUtil.convert(fragmentProfileBinding.profileImage.getDrawingCache()));
                hashMap.put("mobileno", fragmentProfileBinding.etMobileNumber.getText().toString());
                hashMap.put("ip", getResources().getString(R.string.baseURL));
                hashMap.put("uid", new TinyDB(getActivity()).getString("uid"));

                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.updateUserDetailsAPI), hashMap, this.Success(), this.Fail());
                Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
                requestQueue.add(jsObjRequest);
            }
        } else if (v == fragmentProfileBinding.btnChangePass) {
            Intent i = new Intent(getActivity(), ActivityChangePass.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private Response.ErrorListener Fail() {
        Response.ErrorListener err = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.PrintErrorLog("Volley Error", "" + error);
                Utils.ShowShortToast(getActivity(), "Network error occurred, Please make sure you have proper connectivity!");
                Utils.hideProgressDialog();
            }
        };

        return err;
    }

    private Response.Listener<JSONObject> Success1() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("data")) {
                        JSONArray pass = response.getJSONArray("data");
                        JSONObject data = pass.getJSONObject(0);

                        tinyDB.remove("uname");
                        tinyDB.remove("uprofile");
                        tinyDB.remove("upassword");
                        tinyDB.remove("ugender");
                        tinyDB.remove("umobileno");
                        tinyDB.remove("uemail");
                        tinyDB.remove("ucity");
                        tinyDB.remove("udob");
                        tinyDB.remove("status");
                        tinyDB.remove("creationdate");

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

                        Picasso.with(getActivity()).load(data.getString("uprofile")).into(fragmentProfileBinding.profileImage);
                        Picasso.with(getActivity()).load(data.getString("uprofile")).into(ActivityMain.img);
                        fragmentProfileBinding.etEmail.setText(data.getString("uemail"));
                        ActivityMain.email.setText(data.getString("uemail"));
                        ActivityMain.name.setText(data.getString("uname"));
                        fragmentProfileBinding.etUname.setText(data.getString("uname"));
                        fragmentProfileBinding.etMobileNumber.setText(data.getString("umobileno"));

                    } else {
                        Utils.ShowShortToast(getActivity(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return listener;
    }

    private Response.Listener<JSONObject> Success() {
        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog();
                try {
                    Utils.PrintErrorLog("response", "" + response);
                    if (response.has("pass")) {
                        getDetails();
                        Utils.ShowShortToast(getActivity(), response.getString("pass"));
                    } else {
                        Utils.ShowShortToast(getActivity(), response.getString("fail"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return listener;
    }


    void showOptions() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
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
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == INTENT_CAMERA_IMAGE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                saveImageOnSDCard(photo);
            }

            if (requestCode == INTENT_GALLARY_IMAGE) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

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
        Utils.verifyStoragePermissions(getActivity());
        if (bitmap == null) {
            Utils.ShowShortToast(getActivity(), "Image Not Available Please Try Again.!!");
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
                fragmentProfileBinding.profileImage.setImageBitmap(myBitmap);
            }
        } else {
            imageFile = null;
            Utils.ShowShortToast(getActivity(), "Error during image saving");
        }
    }

    public void getDetails() {
        Utils.showProgressDialog(getActivity(), "Making request", "Please wait..");

        final HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("uid", new TinyDB(getActivity()).getString("uid"));

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getResources().getString(R.string.baseURL) + getResources().getString(R.string.getUserDetailsAPI), hashMap, this.Success1(), this.Fail());
        Utils.PrintErrorLog("Post Parameters", "" + hashMap.toString());
        requestQueue.add(jsObjRequest);
    }

    public boolean validate() {
        if (fragmentProfileBinding.etMobileNumber.getText().toString().length() < 1) {
            Utils.ShowShortToast(getActivity(), "Please enter mobile number");
            return false;
        }
        if (fragmentProfileBinding.etMobileNumber.getText().toString().length() < 10) {
            Utils.ShowShortToast(getActivity(), "Please enter valid mobile number");
            return false;
        }
        return true;
    }

}
