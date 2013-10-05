package org.apache.cordova.mapkit;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class MapKitInfoWindow implements InfoWindowAdapter {
	private MapKit mapkit;
	
	public MapKitInfoWindow(MapKit parent) {
	    this.mapkit = parent;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		Context context = mapkit.cordova.getActivity();
		
		LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mainLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		ImageView icon = new ImageView(context);
		
		LinearLayout verticalLayout = new LinearLayout(context);
		verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		verticalLayout.setOrientation(LinearLayout.VERTICAL);
		
		TextView title = new TextView(context);
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		title.setTypeface(null, Typeface.BOLD);
		title.setTextColor(Color.BLACK);
		
		TextView snippet = new TextView(context);
		snippet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		snippet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		snippet.setTextColor(Color.BLACK);
		
		title.setText(arg0.getTitle());
		snippet.setText(arg0.getSnippet());
		
		JSONObject options = mapkit.current_pins.get(arg0.getId());
		
		if ( options != null && options.has("image") ) {
			String resource = mapkit.getResourceFrom(options, "image");
            if(resource != null) {
				try {
					InputStream path = mapkit.cordova.getActivity().getAssets().open(resource);
					Bitmap bit = BitmapFactory.decodeStream(path);

	                int width = 50;
	                int height = 50;
					if (options.has("width")) {
						width = options.getInt("width");
					}
					if (options.has("height")) {
						height = options.getInt("height");
					}
	                
					icon.setLayoutParams(new LinearLayout.LayoutParams(width, height, 1));

	                icon.setImageBitmap(bit);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
		}
		
		verticalLayout.addView(title);
		verticalLayout.addView(snippet);
		
		mainLayout.addView(icon);
		mainLayout.addView(verticalLayout);
		
		return mainLayout;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}

}
