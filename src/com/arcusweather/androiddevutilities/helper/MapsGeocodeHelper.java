package com.arcusweather.androiddevutilities.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;

public class MapsGeocodeHelper {
	
    public Address getAltLocation(String inputAddress) throws JSONException
    {
    	JSONObject jAddress = getLocationInfo(inputAddress);
    	String status = jAddress.getString("status");
    	if(!status.equals(new String("OK")))
    	{
    		return null;
    	}
    	
        Address mgaAddress = new Address(null);
        JSONArray components = ((JSONArray)jAddress.get("results")).getJSONObject(0).getJSONArray("address_components");
    	String city = "";
    	String state = "";
    	String country_short = "";
    	String country_long = "";
    	String display_city_state = "";
    	
    	Double latitude = ((JSONArray)jAddress.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        Double longitude = ((JSONArray)jAddress.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");
        
        mgaAddress.setLatitude(latitude);
        mgaAddress.setLongitude(longitude);
        
        //System.out.println(components);
    	
    	for (int i=0; i<components.length(); i++) 
    	{
    		JSONObject j = components.getJSONObject(i);
    		JSONArray t = j.getJSONArray("types");
    		if(t != null && t.length() > 0)
    		{
	    		if(t.getString(0).equals(new String("locality")))
	    		{
	    			city = j.getString("long_name");
	    			mgaAddress.setLocality(city);
	    		}
	    		if(t.getString(0).equals(new String("administrative_area_level_1")))
	    		{
	    			state = j.getString("long_name");
	    			mgaAddress.setAdminArea(state);
	    		}
	    		if(t.getString(0).equals(new String("country")))
	    		{
	    			country_short = j.getString("short_name");
	    			country_long = j.getString("long_name");
	    			mgaAddress.setCountryName(country_long);
	    			mgaAddress.setCountryCode(country_short);
	    		}
    		}
    	}
    	
    	return mgaAddress;
    }
    
    public static JSONObject getLocationInfo(String address) {
    	address = address.trim().replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" +address+"&ka&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
        }
        return jsonObject;
    }
}
