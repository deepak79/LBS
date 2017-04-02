package finalyearproject.nearu.vendoractivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityVmainBinding;
import finalyearproject.nearu.fragments.FragmentDashboard;
import finalyearproject.nearu.fragments.FragmentManageOffers;
import finalyearproject.nearu.fragments.FragmentManageShops;
import finalyearproject.nearu.helper.TinyDB;

/**
 * Created by deepakgavkar on 18/02/17.
 */
public class ActivityVendorMain extends BaseActivity {

    public static ActivityVmainBinding activityMainBinding;
    TinyDB tinyDB;
    public static ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(ActivityVendorMain.this, R.layout.activity_vmain);
        setSupportActionBar((Toolbar) activityMainBinding.toolbarActionbar);
        actionBar = getSupportActionBar();
        tinyDB = new TinyDB(this);

        replaceFragment(new FragmentManageShops());

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        activityMainBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                activityMainBinding.drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.dashboard:
                        replaceFragment(new FragmentDashboard());
                        return true;

                    case R.id.manageshops:
                        replaceFragment(new FragmentManageShops());
                        return true;

                    case R.id.manageoffers:
                        replaceFragment(new FragmentManageOffers());
                        return true;

                    case R.id.logout:
                        tinyDB.remove("vid");
                        tinyDB.remove("vusername");
                        tinyDB.remove("vname");
                        tinyDB.remove("vpassword");
                        tinyDB.remove("vmobileno");
                        tinyDB.remove("vlogo");
                        tinyDB.remove("vclicks");

                        Intent i = new Intent(ActivityVendorMain.this, ActivityVendorLogin.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();

                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        View headerLayout = activityMainBinding.navigationView.getHeaderView(0);
        TextView name = (TextView) headerLayout.findViewById(R.id.username);
        TextView email = (TextView) headerLayout.findViewById(R.id.email);
        ImageView img = (ImageView) headerLayout.findViewById(R.id.profile_image);

        name.setText(tinyDB.getString("vname"));
        email.setText(tinyDB.getString("vmobileno"));
        String imglink = tinyDB.getString("vlogo");
        if (!imglink.equals("")) {
            Picasso.with(getApplicationContext()).load(imglink).into(img);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, activityMainBinding.drawer, ((Toolbar) activityMainBinding.toolbarActionbar), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                hideKeyboard();
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        activityMainBinding.drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }


    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        confirmDialog();
    }

    public void confirmDialog() {
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(ActivityVendorMain.this);
        builderSingle.setTitle("Do you really want to exit?");


        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builderSingle.show();
    }
}
