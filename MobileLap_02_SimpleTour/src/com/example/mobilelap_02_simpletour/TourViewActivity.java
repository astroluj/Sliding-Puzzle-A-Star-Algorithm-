package com.example.mobilelap_02_simpletour;

 import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TourViewActivity extends FragmentActivity {

	final static int ZOOM =18 ;
	
	private TextView tourNameText, latitudeText, longitudeText ;
	
	private Geocoder geocoder ;
	private GoogleMap googleMap ;
	private MarkerOptions marker ;
	private CameraPosition cameraPos ;
	private LatLng latLng ;
	private String tourName ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour_view);
		
		tourNameText =(TextView) findViewById (R.id.tour_view_tour_name) ;
		latitudeText =(TextView) findViewById (R.id.tour_view_latitude) ;
		longitudeText =(TextView) findViewById (R.id.tour_view_longitude) ;
		
		// GeoCoder
		geocoder =new Geocoder (this) ;
		
		// Get Intent Latitue and Longitude, tourName
		latLng =new LatLng (getIntent ().getDoubleExtra("latitude", 0), 
				getIntent ().getDoubleExtra("longitude", 0)) ;
		tourName =getIntent ().getStringExtra("tourName") ;
		// Set GoogleMap
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById (R.id.tour_view_map)).getMap () ;
		googleMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				googleMap.clear();
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				
				setTourMarker (latLng) ;
			}
		});
		// Initial Position
		cameraPos = new CameraPosition.Builder ().target (latLng).zoom (ZOOM).build () ;
		// Moved
		googleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPos)) ;
		
		// Marker
		setTourMarker (latLng) ;
	}
	
	private void setTourInfoText (LatLng latLng, String tourName) {
		try {
			tourNameText.setText (tourName) ;
			latitudeText.setText (latLng.latitude +"") ;
			longitudeText.setText (latLng.longitude +"") ;
		} catch (Exception e) {}
	}
	
	private void setTourMarker (LatLng latLng) {
		try {
			if (marker == null) marker =new MarkerOptions () ;
			
			try {
				tourName =geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0).toString() ;
			} catch (Exception e) {
				tourName ="" ;
			}
			
			marker.position(latLng) ;
			marker.title(tourName) ;
			
			// add Marker
			googleMap.addMarker(marker).showInfoWindow() ;
			
			// Tour Info
			setTourInfoText (latLng, tourName) ;
		} catch (Exception e) {}
	}
}
