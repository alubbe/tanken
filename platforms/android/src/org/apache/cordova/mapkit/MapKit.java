package org.apache.cordova.mapkit;

import java.util.Hashtable;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MapKit extends CordovaPlugin {

    protected ViewGroup root; // original Cordova layout
    protected RelativeLayout main; // new layout to support map
    protected MapView mapView;
    private CallbackContext cCtx;
    private String TAG = "MapKitPlugin";
    
    protected Hashtable<String, JSONObject> current_pins;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        main = new RelativeLayout(cordova.getActivity());
        current_pins = new Hashtable<String, JSONObject>();
    }

    public void showMap(final JSONObject options) {
        try {
        	final MapKit me = this;
        	
            cordova.getActivity().runOnUiThread(new Runnable() {
                MapKit mapkit = me;
            	@Override
                public void run() {
                    double latitude = 0, longitude = 0;
                    int marginTop = 200, marginBottom = 150;
                    try {
                        marginTop = options.getInt("marginTop");
                        marginBottom = options.getInt("marginBottom");
                        latitude = options.getDouble("lat");
                        longitude = options.getDouble("lon");
                    } catch (JSONException e) {
                        LOG.e(TAG, "Error reading options");
                    }
                    mapView = new MapView(cordova.getActivity(),
                            new GoogleMapOptions());
                    
                    root = (ViewGroup) webView.getParent();
                    root.removeView(webView);
                    main.addView(webView);

                    cordova.getActivity().setContentView(main);

                    try {
                        MapsInitializer.initialize(cordova.getActivity());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL,
                            RelativeLayout.TRUE);
                    params.setMargins(0, marginTop, 0, marginBottom);

                    mapView.setLayoutParams(params);
                    mapView.onCreate(null);
                    mapView.onResume(); // FIXME: I wish there was a better way
                                        // than this...
                    main.addView(mapView);
                    
                    // Moving the map to lot, lon
                    mapView.getMap().moveCamera(
                            CameraUpdateFactory.newLatLngZoom(new LatLng(
                                    latitude, longitude), 15));
                    
                    //disabling the infoWindow since this app will not use them
                    //mapView.getMap().setInfoWindowAdapter(new MapKitInfoWindow(mapkit));

                    // instead, invoke a JS callback whenever a marker gets touched
                    mapView.getMap().setOnMarkerClickListener(new OnMarkerClickListener() {
                        
                        public boolean onMarkerClick(Marker marker) {
                            int marker_id = -1;
                            JSONObject marker_options = current_pins.get(marker.getId());
                            try {
                                marker_id = marker_options.getInt("id");
                            } catch (Exception e) {
                                e.printStackTrace();
                                cCtx.error("MapKitPlugin::onMarkerClick(): marker ID could not be retrieved");
                            }
                            LOG.e(TAG, "marker " + Integer.toString(marker_id) + " touched");
                            webView.loadUrl("javascript:window.alert('Marker " + Integer.toString(marker_id) + " touched!')");
                            return true;
                        }
                    });

                    
                    cCtx.success();
                }
            });
            
            
        } catch (Exception e) {
            e.printStackTrace();
            cCtx.error("MapKitPlugin::showMap(): An exception occured");
        }
        
        
    }

    private void hideMap() {
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mapView != null) {
                        mapView.onDestroy();
                        main.removeView(webView);
                        main.removeView(mapView);
                        root.addView(webView);
                        cordova.getActivity().setContentView(root);
                        mapView = null;
                        cCtx.success();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            cCtx.error("MapKitPlugin::hideMap(): An exception occured");
        }
    }

    public void addMapPins(final JSONArray pins) {
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mapView != null) {
                        try {
                            for (int i = 0, j = pins.length(); i < j; i++) {
                                double latitude = 0, longitude = 0;
                                JSONObject options = pins.getJSONObject(i);
                                latitude = options.getDouble("lat");
                                longitude = options.getDouble("lon");

                                MarkerOptions mOptions = new MarkerOptions();

                                mOptions.position(new LatLng(latitude,
                                                             longitude));
                                if(options.has("title")) {
                                    mOptions.title(options.getString("title"));
                                }
                                if(options.has("snippet")) {
                                    mOptions.snippet(options.getString("snippet"));
                                }
                                if(options.has("icon")) {
                                    BitmapDescriptor bDesc = getBitmapDescriptor(options, "icon");
                                    if(bDesc != null) {
                                      mOptions.icon(bDesc);
                                    }
                                }

                                
                                // adding Marker
                                // This is to prevent non existing asset resources to crash the app
                                try {
                                	Marker m = mapView.getMap().addMarker(mOptions);
                                	current_pins.put(m.getId(), options);
                                } catch(NullPointerException e) {
                                    LOG.e(TAG, "An error occurred when adding the marker. Check if icon exists");
                                }
                            }
                            cCtx.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LOG.e(TAG, "An error occurred while reading pins");
                            cCtx.error("An error occurred while reading pins");
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            cCtx.error("MapKitPlugin::addMapPins(): An exception occured");
        }
    }

    protected String getResourceFrom(final JSONObject iconOption, String tagName ) {
        try {
            Object o = iconOption.get(tagName);
            String type = null, resource = null;
            if( o.getClass().getName().equals("org.json.JSONObject" ) ) {
                JSONObject icon = (JSONObject)o;
                if(icon.has("type") && icon.has("resource")) {
                    type = icon.getString("type");
                    resource = icon.getString("resource");
                    if(type.equals("asset")) {
                        return resource;
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    
    protected BitmapDescriptor getBitmapDescriptor( final JSONObject iconOption, String tagName ) {
        try {
        	String resource = getResourceFrom(iconOption, tagName);
            if (resource != null) {
            	return BitmapDescriptorFactory.fromAsset(resource);
            } else {
            	Object o = iconOption.get(tagName);
            	 if(!o.getClass().getName().equals("org.json.JSONObject" ) ) {
            		 return BitmapDescriptorFactory.defaultMarker(Float.parseFloat(o.toString()));
            	 }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public void clearMapPins() {
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mapView != null) {
                        mapView.getMap().clear();
                        current_pins.clear();
                        
                        cCtx.success();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            cCtx.error("MapKitPlugin::clearMapPins(): An exception occured");
        }
    }

    public void changeMapType(final JSONObject options) {
        try{
            cordova.getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if( mapView != null ) {
                        int mapType = 0;
                        try {
                            mapType = options.getInt("mapType");
                        } catch (JSONException e) {
                            LOG.e(TAG, "Error reading options");
                        }

                        //Don't want to set the map type if it's the same
                        if(mapView.getMap().getMapType() != mapType) {
                            mapView.getMap().setMapType(mapType);
                        }
                    }

                    cCtx.success();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            cCtx.error("MapKitPlugin::changeMapType(): An exception occured ");
        }
    }

    public boolean execute(String action, JSONArray args,
            CallbackContext callbackContext) throws JSONException {
        cCtx = callbackContext;
        if (action.compareTo("showMap") == 0) {
            showMap(args.getJSONObject(0));
        } else if (action.compareTo("hideMap") == 0) {
            hideMap();
        } else if (action.compareTo("addMapPins") == 0) {
            addMapPins(args.getJSONArray(0));
        } else if (action.compareTo("clearMapPins") == 0) {
            clearMapPins();
        } else if( action.compareTo("changeMapType") == 0 ) {
            changeMapType(args.getJSONObject(0));
        }
        LOG.d(TAG, action);

        return true;
    }

    @Override
    public void onPause(boolean multitasking) {
        LOG.d(TAG, "MapKitPlugin::onPause()");
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        LOG.d(TAG, "MapKitPlugin::onResume()");
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume(multitasking);
    }

    @Override
    public void onDestroy() {
        LOG.d(TAG, "MapKitPlugin::onDestroy()");
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }
}
