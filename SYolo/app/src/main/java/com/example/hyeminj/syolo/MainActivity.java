package com.example.hyeminj.syolo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity  /*implements View.OnClickListener*/{

    private BottomNavigationView mMainNav;
    private RelativeLayout mMainFrame;
    private ProgressDialog nDialog;
    private HomeFragment homeFragment;
    private LikeFragment likeFragment;
    private SafetyFragment safetyFragment;
    private MoreFragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (RelativeLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.main_nav);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        homeFragment = new HomeFragment();
        likeFragment = new LikeFragment();
        safetyFragment = new SafetyFragment();
        moreFragment = new MoreFragment();

        setFragment(homeFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ((item.getItemId())) {

                    case R.id.nav_home:

                        mMainNav.setItemBackgroundResource(R.color.colorWhite);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_like:
                        mMainNav.setItemBackgroundResource(R.color.colorWhite);
                        setFragment(likeFragment);
                        return true;

                    case R.id.nav_safety:
                        mMainNav.setItemBackgroundResource(R.color.colorWhite);
                        setFragment(safetyFragment);
                        return true;

                    case R.id.nav_more:
                        mMainNav.setItemBackgroundResource(R.color.colorWhite);
                        setFragment(moreFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
