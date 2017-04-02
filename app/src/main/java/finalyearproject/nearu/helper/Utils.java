package finalyearproject.nearu.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import finalyearproject.nearu.R;
import finalyearproject.nearu.application.AppController;


/**
 * Created by deepakgavkar on 06/04/16.
 */
public class Utils {
    public static final Boolean DEBUG_STATUS = true;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private static ProgressDialog loadingDialog;
    private static MaterialDialog materialDialog;

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return true;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static void ShowShortToast(Context context, String message) {
        try {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundResource(R.color.colorPrimary);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            toast.setGravity(Gravity.CENTER, 0, 0);
            text.setTextColor(context.getResources().getColor(R.color.white));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            text.setLayoutParams(llp);
            text.setGravity(Gravity.CENTER);
            text.setPadding(10, 10, 10, 10);
            text.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShowLongToast(Context view, String message) {
        Toast.makeText(view, message, Toast.LENGTH_LONG).show();
    }

    public static void PrintErrorLog(String TAG, String message) {
        if (DEBUG_STATUS) {
            Log.w(TAG, message);
        }
    }

    /**
     * creates retry policy with 60 seconds of timeout (resolves issue of repeated calls to the server).
     *
     * @return retryPolicy - RetryPolicy
     */
    public static RetryPolicy getVolleyRetryPolicy() {
        return new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    /**
     * shows permission denied dialog and allow user to go to settings.
     *
     * @param activity
     * @param message
     */
    public static void showPermissionDialog(final Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startInstalledAppDetailsActivity(activity);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    /**
     * appends all url params to the actual url and returns encoded final url.
     *
     * @param url
     * @param urlParams
     * @return
     */
    public static String appendParams(String url, Map<String, String> urlParams) {
        String encodedString = null;
        try {
            url += "?";
            Iterator iterator = urlParams.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                url += (key + "=" + URLEncoder.encode(urlParams.get(key), "utf-8") + "&");
            }

            url = url.substring(0, url.length() - 1);
            encodedString = URLEncoder.encode(url, "utf-8");
            encodedString = url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * converts dp value to pixel value
     *
     * @param dp int
     * @return pixel value - int
     */
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = AppController.getInstance().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int dpToPx(float dp) {
        DisplayMetrics displayMetrics = AppController.getInstance().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static MaterialDialog showProgressDialog(Activity activity, String title, String mMessage) {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog.Builder(activity).title(title).content(mMessage).progress(true, 0).show();
        materialDialog.setCancelable(false);
        return materialDialog;
    }

    ///dismiss loading dialog
    public static void hideProgressDialog() {
//        if (loadingDialog != null && loadingDialog.isShowing())
//            loadingDialog.dismiss();

        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
    }

    /* Displays a dialog with custom message and a title and finish current activity when click ok. */
    public static void displayAlertDialog(final Activity context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(msg);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * isNetworkAvailable(Context) provides a network state status.
     */
    public static boolean isNetworkAvailable(final Activity activity, View view) {
        boolean var = false;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (cm.getActiveNetworkInfo() != null) {
                var = true;
            }
            if (!var) {
                showSnackBar(activity, view, "Please check network and try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return var;
    }

    /**
     * shows snackbar in provided view with the message.
     *
     * @param view
     * @param message
     */
    public static void showSnackBar(Activity activity, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }
}
