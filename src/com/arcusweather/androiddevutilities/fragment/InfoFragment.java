package com.arcusweather.androiddevutilities.fragment;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcusweather.androiddevutilities.R;
import com.arcusweather.androiddevutilities.activity.MainActivity;

public class InfoFragment extends Fragment {
	public int widgetId;
	public Context appContext;
	public View thisView;
	public Location mFusedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View w = inflater.inflate(R.layout.fragment_info, null,false);
        MainActivity ma = (MainActivity) getActivity();
        Location curLoc = ma.getCurrentLocation();
        
        TextView curLocView = (TextView) w.findViewById(R.id.current_location);
        TextView fusedLocView = (TextView) w.findViewById(R.id.fused_location);
        TextView geocoderView = (TextView) w.findViewById(R.id.geocoder);

        String curLocText = "Current Location\n";
        String fusedLocText = "Fused Location\n";
        String geocoderText = "Geocoder info\n";
        
        if(curLoc == null)
        {
        	curLocText = "Could not find location";
        }
        else
        {
        	curLocText += "Provider: " + curLoc.getProvider() + "\n";
        	curLocText += "Lat: " + curLoc.getLatitude() + "\n";
        	curLocText += "Long: " + curLoc.getLongitude() + "\n";
        	curLocText += "Time: " + new Date(curLoc.getTime()) + "\n";
        	
        	String lat = String.valueOf(curLoc.getLatitude());
        	String lon = String.valueOf(curLoc.getLongitude());

			Geocoder gcd = new Geocoder(this.getActivity(), Locale.getDefault());
			geocoderText += "geocoder present: " + Geocoder.isPresent() + "\n";
			if(!lat.equals(new String("")) && !lon.equals(new String("")))
			{
				try {
					@SuppressWarnings("unused")
					List<Address> addresses = null;
					addresses = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
					geocoderText += "using geocoder\n";
				} catch (IOException e1) {
					geocoderText += "Not using geocoder\n";
				}
			}
        }
        
        if(mFusedData == null)
        {
	        Location fusedLoc = ma.getFusedLocation();
	        if(ma.getLocationClient().isConnected())
	        {
	        	if(fusedLoc != null)
	        	{
		        	fusedLocText += "Provider: " + fusedLoc.getProvider() + "\n";
		        	fusedLocText += "Lat: " + fusedLoc.getLatitude() + "\n";
		        	fusedLocText += "Long: " + fusedLoc.getLongitude() + "\n";
		        	fusedLocText += "Time: " + new Date(fusedLoc.getTime()) + "\n";
	        	}
	        	else
	        	{
	        		fusedLocText += "No Fused Location\n";
	        	}
	        }
	        else if(ma.getLocationClient().isConnecting())
	        {
	        	fusedLocText += "Connecting\n";
	        }
        }
        else
        {
        	fusedLocText += "Provider: " + mFusedData.getProvider() + "\n";
        	fusedLocText += "Lat: " + mFusedData.getLatitude() + "\n";
        	fusedLocText += "Long: " + mFusedData.getLongitude() + "\n";
        	fusedLocText += "Time: " + new Date(mFusedData.getTime()) + "\n";
        }
        
        curLocView.setText(curLocText);
        fusedLocView.setText(fusedLocText);
        geocoderView.setText(geocoderText);

        thisView = w;
        return w;
    }
    
    public void setFusedData(Location fusedData)
    {
    	mFusedData = fusedData;
    }
}