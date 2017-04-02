package finalyearproject.nearu.useractivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import finalyearproject.nearu.R;
import finalyearproject.nearu.databinding.ActivityMainBinding;
import finalyearproject.nearu.fragments.FragmentHome;
import finalyearproject.nearu.fragments.FragmentMyFavShops;
import finalyearproject.nearu.fragments.FragmentProfile;
import finalyearproject.nearu.helper.TinyDB;
import finalyearproject.nearu.vendoractivities.BaseActivity;

/**
 * Created by deepakgavkar on 18/02/17.
 */
public class ActivityMain extends BaseActivity {

    public static TextView name, email;
    public static de.hdodenhof.circleimageview.CircleImageView img;
    public static ActivityMainBinding activityMainBinding;
    TinyDB tinyDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(ActivityMain.this, R.layout.activity_main);
        setSupportActionBar((Toolbar) activityMainBinding.toolbarActionbar);
        tinyDB = new TinyDB(this);

        replaceFragment(new FragmentHome());

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
                    case R.id.home:
                        replaceFragment(new FragmentHome());
                        return true;

                    case R.id.profile:
                        replaceFragment(new FragmentProfile());
                        return true;

                    case R.id.favshops:
                        replaceFragment(new FragmentMyFavShops());
                        return true;

                    case R.id.logout:
                        tinyDB.remove("uid");
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
                        tinyDB.remove("fav");

                        Intent i = new Intent(ActivityMain.this, ActivityLogin.class);
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
        name = (TextView) headerLayout.findViewById(R.id.username);
        email = (TextView) headerLayout.findViewById(R.id.email);
        img = (de.hdodenhof.circleimageview.CircleImageView) headerLayout.findViewById(R.id.profile_image);

        name.setText(tinyDB.getString("uname"));
        email.setText(tinyDB.getString("umobileno"));
        String imglink = tinyDB.getString("uprofile");
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
        android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(ActivityMain.this);
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
