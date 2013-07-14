package com.arcusweather.androiddevutilities.activity;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.arcusweather.androiddevutilities.R;
import com.arcusweather.androiddevutilities.adapter.MainFragmentPagerAdapter;
import com.arcusweather.androiddevutilities.location.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	MainFragmentPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
    LocationClient mLocationClient;
    Location mCurrentLocation;
    Location mFusedLocation;
	private GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		//mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onResume()
	{
		if(gps == null)
		{
			gps = new GPSTracker(this);
		}
        setupLocationUpdates(gps);
	    super.onResume();
	}

	@Override
    protected void onPause()
	{
    	gps.stopUsingGPS();
		super.onPause();
	}

    public void refresh(MenuItem item)
	{
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}

	public Location getFusedLocation()
	{
        Location location = null;
		location = getLastFusedLocation();
		return location;
	}

    public Location getCurrentLocation()
    {
        Location location = null;

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);  
        String provider = mlocManager.getBestProvider(criteria, true);
        System.out.println("best provider is " + provider);
        String gpsprovider = "gps";
        if(provider == null)
        {
        	return location;
        }
        
        if(provider.equals(gpsprovider))
        {
        	boolean hasgpsloc = true;
	        Date now = new Date();
	        Long now_ms = now.getTime();
	        Long last_ms;
	        Long diff_ms = null;
        	try
        	{
		        location = mlocManager.getLastKnownLocation(provider);     
		        last_ms = location.getTime();
		        diff_ms = now_ms - last_ms;
        	}
        	catch(Exception e)
        	{
        		hasgpsloc = false;
        	}
        
        	if(hasgpsloc == false)
        	{
		        if(mlocManager.isProviderEnabled("network"))
	        	{
		        	location = mlocManager.getLastKnownLocation("network");     
	        	}
		        else
		        {
		        	location = mlocManager.getLastKnownLocation("passive");     
		        }
        	}
        	else if(diff_ms >= 60000)
	        {
		        if(mlocManager.isProviderEnabled("network"))
	        	{
		        	//System.out.println("more than 1 minutes, going to network");
		        	location = mlocManager.getLastKnownLocation("network");     
	        	}
		        else
		        {
		        	//System.out.println("more than 1 minutes, network, not enabled");
			        location = mlocManager.getLastKnownLocation(provider);     
		        }
	        }
        }
        else
        {
        	//System.out.println("not gps");
	        location = mlocManager.getLastKnownLocation(provider);     
        }
    		
        return location;
    }
    
    public Void setupLocationUpdates(GPSTracker gps)
    {
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);  
        if(gps.isGPSEnabled)
        {
	        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, gps);  
        }
        else if(gps.isNetworkEnabled)
        {
	        mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, gps);  
        }
        else
        {
	        mlocManager.requestLocationUpdates( LocationManager.PASSIVE_PROVIDER, 0, 0, gps);  
        }
        return null;
    }
    
    public Void stopLocationUpdates()
    {
        GPSTracker gps = new GPSTracker(this);
        gps.stopUsingGPS();
        return null;
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
        this.mSectionsPagerAdapter.setData(mFusedLocation);
        this.mSectionsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onConnected(Bundle arg0) {
        getLocation();
        this.mSectionsPagerAdapter.setData(mFusedLocation);
        this.mSectionsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDisconnected() {
        this.mSectionsPagerAdapter.setData(mFusedLocation);
        this.mSectionsPagerAdapter.notifyDataSetChanged();
	}
	
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     */
    public void getLocation() {
        if (servicesConnected()) {
        	if(mLocationClient.isConnected())
        	{
	            mFusedLocation = mLocationClient.getLastLocation();
        	}
        }
    }
    
    public Location getLastFusedLocation()
    {
    	return mFusedLocation;
    }
    
    public LocationClient getLocationClient()
    {
    	return mLocationClient;
    }

}
