package org.zhutou.planedestroyer;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {

    ViewPager airports;
    List<View> airportViews = new ArrayList<View>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        airports = (ViewPager) findViewById(R.id.airports);

        airportViews.add(new AirportView(getApplicationContext()));
        airportViews.add(new AirportMapView(this));
        airports.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return airportViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == (object);
            }

            @Override
            public Object instantiateItem(View collection, int position) {
                ((ViewPager) collection).addView(airportViews.get(position), 0);
                return airportViews.get(position);
            }
            // @Override
            // public Fragment getItem(int position) {
            // return new Fragme
            // airportViews.get(position);
            // }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setCustomTitle(null)
                .setMessage("真的忍心退出这么好玩的游戏？")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();


    }

    public void onClickHandler(View v) {

        AirportView airport = (AirportView) airports.getFocusedChild();
        switch (v.getId()) {
            case R.id.buttonU:
                airport.moveUp();
                break;
            case R.id.buttonD:
                airport.moveDown();
                break;
            case R.id.buttonL:
                airport.moveLeft();
                break;
            case R.id.buttonR:
                airport.moveRight();
                break;
            case R.id.buttonRot:
                airport.rotateLeft();
                break;
            case R.id.start:
                airport.fight();
                break;
        }

    }
}
