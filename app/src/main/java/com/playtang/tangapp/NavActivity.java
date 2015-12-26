package com.playtang.tangapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.playtang.tangapp.login.PlayTangOnLoadingListener;
import com.playtang.tangapp.slider.ChildAnimationExample;
import com.playtang.tangapp.slider.SliderLayout;
import com.playtang.tangapp.slider.SliderTypes.BaseSliderView;
import com.playtang.tangapp.slider.SliderTypes.TextSliderView;
import com.playtang.tangapp.slider.Tricks.InfiniteViewPager;
import com.playtang.tangapp.slider.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.Hashtable;


public class NavActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener,
        PlayTangOnLoadingListener {

    private static final String TAG = "BaseActivity";
    String TITLES[] = {"Home", "Events", "Mail", "All Sports", "Info"};
    int ICONS[] = {R.drawable.ic_action,
            R.drawable.ic_event,
            R.drawable.ic_mail,
            R.drawable.ic_play,
            R.drawable.ic_info};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Abhishek.Anand";
    String EMAIL = "abhishek.anand@playtang.com";
    int PROFILE = R.drawable.avi;
    NavigationView mNavigationView;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    // CustomPagerAdapter mCustomPagerAdapter;
    ActionBarDrawerToggle mDrawerToggle;
    InfiniteViewPager mViewPager;
    private Menu menu;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    private LinearLayout leftArrow;
    private LinearLayout rightArrow;
    private ContactsContract.Profile fbProfile;
    private SliderLayout mDemoSlider;
    private int mCurrentSelectedPosition = -1;
    private int mPrevSelectedPosition = -1;
    private int isItemOnTouch = 0;
    private int currentPageselected;
    private ParseUser currentUser;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        updateMenuTitles();
        //setFBProfile();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        this.menu=menu;
        return true;
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        if(currentUser!=null){
            MenuItem logout = (MenuItem) findViewById(R.id.action_login);
            logout.setTitle("Logout");
        }

        //setFBProfile();
    }

    public void onGroupItemClick(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_login:
                openLogin();
                return true;
            case R.id.action_logout:
                openLogout();
                return true;
            case R.id.action_location:
                onGroupItemClick(item);
                return true;
            case R.id.add_venue:
                addVenue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addVenue(){
        Intent loginIntent= new Intent(this ,PlayTangVenue.class);
        startActivity(loginIntent);

    }

    public void openLogin() {
        Intent intent;
        if (ParseUser.getCurrentUser() == null) {
            intent = new Intent(this, LoginDispatchActivity.class);
            startActivity(intent);
        }
    }

    public void openLogout() {
        if (ParseUser.getCurrentUser()!= null){
            onLoadingStart(true);
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    currentUser = null;
                    updateMenuTitles();
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                    onLoadingFinish();

                    Toast.makeText(NavActivity.this, "You have been successfully logged out", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateMenuTitles() {
        if (ParseUser.getCurrentUser()!=null) {
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
            menu.findItem(R.id.add_venue).setVisible(true);

        } else {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.add_venue).setVisible(false);
        }
    }

    /**
     * Called when we are in progress retrieving some data.
     *
     * @param showSpinner
     *     Whether to show the loading dialog.
     */
    @Override
    public void onLoadingStart(boolean showSpinner) {
        if (showSpinner) {
            progressDialog = ProgressDialog.show(this, null,
                    getString(com.playtang.tangapp.R.string.com_parse_ui_progress_dialog_text), true, false);
        }
    }

    /**
     * Called when we are finished retrieving some data.
     */
    @Override
    public void onLoadingFinish() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_nav, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(fullView);
        //setContentView(R.layout.activity_home_screen);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        if (useToolbar()) {
            toolbar.setLogo(R.mipmap.logo);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_drawer);



            if (isHomePage()) {
                addSlider();
            }

            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

            if(ParseUser.getCurrentUser()!=null){

                NAME=ParseUser.getCurrentUser().get("name").toString();
                EMAIL=ParseUser.getCurrentUser().getEmail();
                //PROFILE=ParseUser.getCurrentUser().get("profilePic")
                //Toast.makeText(getApplicationContext(),"Name :"+NAME+" EMail : "+EMAIL,Toast.LENGTH_SHORT).show();
            }

            mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,getApplicationContext(),mDrawerToggle);        // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
            // And passing the titles,icons,header view name, header view email,
            // and header view profile picture

            mRecyclerView.addOnItemTouchListener(getOnItemTouchListener());


            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

            mRecyclerView.setLayoutManager(mLayoutManager);


            Drawer = (DrawerLayout) findViewById(R.id.activity_container);        // Drawer object Assigned to the view

            handleDrawerToggle();

            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle

            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



        }
        if (isHomePage()) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    protected boolean useToolbar() {
        return true;
    }

    protected boolean isHomePage() {
        return false;
    }


    public void addSlider() {
        leftArrow = new LinearLayout(this);
        rightArrow = new LinearLayout(this);


        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.movePrevPosition(true);
            }
        });


        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.moveNextPosition(true);
            }
        });


        Hashtable<String, Integer> file_maps = new Hashtable<String, Integer>();
        file_maps.put("Search", R.drawable.slider1);
        file_maps.put("Coming Soon", R.drawable.slider3);
        file_maps.put("Register Now", R.drawable.slider2);


        ArrayList<String> header_text = new ArrayList<>();
        header_text.add(0, "Welcome to Playtang");
        header_text.add(1, "Search for a Playground");
        header_text.add(2, "Book Your Time Slot");


        int i = 0;

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .header(header_text.get(i))
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
            i++;
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("playtang", name);
            mDemoSlider.addSlider(textSliderView);

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            //mDemoSlider.setBackgroundColor(0b1000011100001000);
            //DescriptionAnimation descriptionAnimation = new DescriptionAnimation();
            mDemoSlider.setCustomAnimation(new ChildAnimationExample());
            //mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
            //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mViewPager = mDemoSlider.getmViewPager();
            //mDemoSlider.set
            //mDemoSlider.addOnPageChangeListener(this);
            //textSliderView.setOnSliderClickListener()


        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("playtang") + "", Toast.LENGTH_SHORT).show();
        if (slider.getView().getId() == R.id.left_arrow) {
            Toast.makeText(this, slider.getBundle().get("playtang") + "leftarrow", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onArrowClick(LinearLayout arrow) {
        Toast.makeText(this,"Arrow Click",Toast.LENGTH_SHORT).show();
        if(R.id.left_arrow==arrow.getId()){
            Toast.makeText(this,"Left Arrow Click",Toast.LENGTH_SHORT).show();
        }

        if(R.id.right_arrow==arrow.getId()){
            Toast.makeText(this,"Right Arrow Click",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPageselected=position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    private void onTouchNavigationItem(RecyclerView recyclerView, MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

        if(mRecyclerView.getChildAdapterPosition(child)!=0) {


            //Toast.makeText(BaseActivity.this, "Motion Event:  " + motionEvent.getAction(), Toast.LENGTH_SHORT).show();
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                Toast.makeText(NavActivity.this, "OnTOuch :ACTION_UP, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                Drawer.closeDrawers();
                //child.setBackgroundResource(R.drawable.item_row_background);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                Toast.makeText(NavActivity.this, "OnTOuch : ACTION_DOWN, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                Drawer.closeDrawers();
                //child.setBackgroundResource(R.drawable.gradient_background);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Toast.makeText(NavActivity.this, "OnTOuch : ACTION_DOWN, pos:" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                Drawer.closeDrawers();
            }
            Drawer.closeDrawers();
//                Toast.makeText(BaseActivity.this, "OnTOuch : The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean onNavigationItemClick(RecyclerView recyclerView, MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int pos=mRecyclerView.getChildAdapterPosition(child);
        mCurrentSelectedPosition = pos;


        if (child != null && (pos!=0)) {
            if(motionEvent.getAction()==MotionEvent.ACTION_MOVE || motionEvent.getAction()==MotionEvent.ACTION_CANCEL){
                Toast.makeText(NavActivity.this, "ACTION_MOVE /ACTION_CANCEL :" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                Drawer.closeDrawers();
                return true;
            }

            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
            {
                Toast.makeText(NavActivity.this, "ACTION_DOWN" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                return true;
                //
            }

            if(motionEvent.getAction()==MotionEvent.ACTION_UP)
            { Toast.makeText(NavActivity.this, "ACTION_UP" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                //child.setBackgroundResource(R.drawable.gradient_background);
                for(int i=1;i<=mAdapter.getItemCount();i++) {
                    if (mPrevSelectedPosition > 0){
                        if( i!=mPrevSelectedPosition)
                            mRecyclerView.getChildAt(mPrevSelectedPosition).setBackgroundResource(R.drawable.gradient_background);
                    }
                }

                child.setBackgroundResource(R.drawable.item_row_background);
                mPrevSelectedPosition=mCurrentSelectedPosition;
                Drawer.closeDrawers();
                return true;

            }





            return true;

        }
        return false;
    }

    private void handleDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar, R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //mAdapter.bindViewHolder();
                if (mPrevSelectedPosition > 0 && isItemOnTouch!=1){
                    for(int i=1;i<=mAdapter.getItemCount();i++) {

                        if( i!=mPrevSelectedPosition)
                            mRecyclerView.getChildAt(mPrevSelectedPosition).setBackgroundResource(R.drawable.item_row_background);
                        else
                            mRecyclerView.getChildAt(i).setBackgroundResource(R.drawable.gradient_background);

                    }
                }
                invalidateOptionsMenu();
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
/*
                for(int i=1;i<mAdapter.getItemCount();i++) {

                    if (mCurrentSelectedPosition > 0 && i!=mCurrentSelectedPosition){
                        //mRecyclerView.getChildAt(i).setBackgroundResource(R.drawable.gradient_background);
                    }


                }*/
                invalidateOptionsMenu();
                // Code here will execute once drawer is closed

                mPrevSelectedPosition=mCurrentSelectedPosition;
            }


        }; // Drawer Toggle Object Made
    }


    @NonNull
    private RecyclerView.OnItemTouchListener getOnItemTouchListener() {
        return new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                return onNavigationItemClick(recyclerView, motionEvent);
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                isItemOnTouch=1;
                onTouchNavigationItem(recyclerView, motionEvent);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean var1) {

            }
        };
    }

}
