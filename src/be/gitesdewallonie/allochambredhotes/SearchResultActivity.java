package be.gitesdewallonie.allochambredhotes;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.gitesdewallonie.allochambredhotes.classes.CurrentLocation;
import be.gitesdewallonie.allochambredhotes.classes.MyItemizedOverlay;
import be.gitesdewallonie.allochambredhotes.classes.Place;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class SearchResultActivity extends FragmentActivity {
	ArrayList<JsonElement> items = new ArrayList<JsonElement>();
	private MapController controller;
	private MapView mapview;
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

	ArrayList<Marker> arrMarkers = new ArrayList<Marker>();
	ArrayList<String> arrIds = new ArrayList<String>();

	public GoogleMap GooglemapView;

	private ListView listview;
	Handler handler = new Handler();
	CurrentLocation location;
	Gson gson = new Gson();
	private MyLocationOverlay overlay;
	private ProgressDialog dialog;
	Boolean found = false;

	private void initMyLocation() {
		final ProgressDialog dialog = ProgressDialog.show(this,
		                                                  getString(R.string.find_your_location),
		                                                  getResources().getString(R.string.please_wait));
		GetCurrentLocation();
		dialog.dismiss();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (location == null)
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								dialog.dismiss();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			}
		}).start();

	}

	void CheckInternetConnection() {
		if (!Lib.isConnected(this)) {
			new AlertDialog.Builder(SearchResultActivity.this)
					.setMessage(getString(R.string.no_connection))
					.setPositiveButton(getResources().getString(R.string.exit),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton(getResources().getString(R.string.try_again),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									CheckInternetConnection();
								}
							}).show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	 @Override
	 protected void onResume() {
		 super.onResume();
		 if (!checkPlayServices()) {
             Toast.makeText(SearchResultActivity.this, getResources().getString(R.string.update_googleps), Toast.LENGTH_LONG).show();
		 }
	 }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		if(checkPlayServices())
		{
			setContentView(R.layout.activity_result);
			setUpMapIfNeeded();

			listview = (ListView) findViewById(R.id.listView1);
			String key = getIntent().getStringExtra("key");

			if (key == null) {
				initMyLocation();
			} else {
				StartSearch(key);
			}
		}

	}

	private boolean checkPlayServices() {
	    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            showErrorDialog(status);
            } else {
            Toast.makeText(this, getResources().getString(R.string.not_supported), Toast.LENGTH_LONG).show();
            finish();
            }
            return false;
        }
		return true;
	}

	void showErrorDialog(int code) {
		 GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}


	private void StartSearch(final String key) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dialog = ProgressDialog.show(SearchResultActivity.this,
						getString(R.string.loading), getResources().getString(R.string.please_wait));
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String url = "http://www.allochambredhotes.be/getMobileClosestHebs?LANGUAGE="
							+ getString(R.string.url_language)
							+ "&address="
							+ URLEncoder.encode(key);
					String x = Lib.GetHtml(url);
					Gson gson = new Gson();
					JsonObject ob = gson.fromJson(x, JsonObject.class);
					location = gson.fromJson(ob.get("search_location"),
							CurrentLocation.class);
					JsonArray results = ob.getAsJsonArray("results");
					for (int i = 0; i < results.size(); i++) {
						JsonElement item = results.get(i);
						if (item.isJsonObject()) {
							Place place = gson.fromJson(item, Place.class);
							Log.i(place.name, place.description);
						}
						if (item.isJsonArray()) {
							Place[] places = gson.fromJson(item, Place[].class);
							Log.i(places.length + "", places[0].name);
						}
						items.add(item);
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							listview.setAdapter(new MyAdapter(
									SearchResultActivity.this,
									R.layout.list_item, items));
							MyItemizedOverlay pinOverlay = new MyItemizedOverlay(
									getResources().getDrawable(R.drawable.pin),
									SearchResultActivity.this);
							pinOverlay.location = location;
							Gson gson = new Gson();
							int latitude = 0;
							int longitude = 0;
							LatLngBounds.Builder builder = new LatLngBounds.Builder();

							for (int i = 0; i < items.size(); i++) {

								JsonElement item = items.get(i);
								Place place = null;
								Place[] places = null;
								if (item.isJsonObject())
									place = gson.fromJson(item, Place.class);
								if (item.isJsonArray()) {
									places = gson.fromJson(item, Place[].class);
									place = places[0];
								}
								int[] ids = { R.drawable.pin1, R.drawable.pin2,
										R.drawable.pin3, R.drawable.pin4,
										R.drawable.pin5, R.drawable.pin6,
										R.drawable.pin7 };

								latitude += place.latitude;
								longitude += place.longitude;

                                Marker mlistMarkers = GooglemapView.addMarker(new MarkerOptions()
                                      .position(new LatLng(place.longitude,place.latitude))
                                      .title(place.name)
                                      .snippet(place.address.address)
                                      .icon(BitmapDescriptorFactory
                                      .fromResource(ids[i])));

								builder.include(mlistMarkers.getPosition());

								arrIds.add(mlistMarkers.getId());
								arrMarkers.add(mlistMarkers);
							}

							if (items.size() > 0) {
							    LatLngBounds bounds = builder.build();
						        int padding = 100;
							    try{
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    GooglemapView.animateCamera(cu);
                                } catch(Exception ex) {}
							}

							Display display = getWindowManager()
									.getDefaultDisplay();
							int width = display.getWidth();
							int height = display.getHeight();


                            try{
                                FrameLayout fl = (FrameLayout)findViewById(R.id.MapFramelayout);
                                fl.setVisibility(View.VISIBLE);
                                LayoutParams p1 = (LayoutParams) fl.getLayoutParams();
                                int[] x = new int[2];
                                fl.getLocationOnScreen(x);
                                int mapHeight = (int) (height - x[1] - dpToPx(52 * (items.size()+1)));
                                if (mapHeight > (0.75 * height)) {
                                    mapHeight = (int) Math.round(0.75 * height);
                                }
                                p1.height = mapHeight;
                                fl.setLayoutParams(p1);
                            } catch(Exception ex)
                            {
                             Toast.makeText(getApplicationContext(), "" + ex.getMessage(),
                                            Toast.LENGTH_LONG).show();
                            }

						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog alertDialog1 = new AlertDialog.Builder(
									SearchResultActivity.this).create();

							alertDialog1.setTitle(R.string.opps);
							alertDialog1.setMessage(getResources().getString(
									R.string.no_result_found));
							alertDialog1.setButton("OK",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});

							// Showing Alert Message
							alertDialog1.show();
							dialog.dismiss();
						}
					});

				}
			}
		}).start();
	}

	int dpToPx(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
	}

	int pxToDp(int px) {
		return (int) (px / getResources().getDisplayMetrics().density);
	}


	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class MyAdapter extends ArrayAdapter<JsonElement> {
		List<JsonElement> items;
		ImageLoader imageLoader = ImageLoader.getInstance();
		private ImageLoaderConfiguration config;
		private DisplayImageOptions defaultOptions;

		public MyAdapter(Context context, int textViewResourceId,
				List<JsonElement> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			items = objects;
			defaultOptions = new DisplayImageOptions.Builder().displayer(
					new RoundedBitmapDisplayer(dpToPx(15))).build();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = convertView;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.list_item, parent, false);
			}
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			ImageView img_thumb = (ImageView) row.findViewById(R.id.img_thumb);
			JsonElement item = items.get(position);
			if (item.isJsonObject()) {
				final Place place = gson.fromJson(item, Place.class);
				tv1.setText((position + 1) + ". " + place.name);
				tv2.setVisibility(View.GONE);
				imageLoader.displayImage(place.thumb, img_thumb);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchResultActivity.this,
								PlaceDetailsActivity.class);
						intent.putExtra("place", place);
						intent.putExtra("location", location);
						startActivity(intent);
					}
				});
			}
			if (item.isJsonArray()) {
				final Place[] places = gson.fromJson(item, Place[].class);
				tv1.setText((position + 1) + ". " + places[0].name);
				tv2.setVisibility(View.VISIBLE);
				tv2.setText(places.length + "");

				imageLoader.displayImage(places[0].thumb, img_thumb);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchResultActivity.this,
								GroupedActivity.class);
						intent.putExtra("places", places);
						intent.putExtra("location", location);
						startActivity(intent);
					}
				});
			}
			return row;
		}
	}


	 private void setUpMapIfNeeded() {
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (GooglemapView == null) {
	            // Try to obtain the map from the SupportMapFragment.
                GooglemapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();
	            // Check if we were successful in obtaining the map.
	            if (GooglemapView != null) {
	                setUpMap();
	            }
	        }
	    }

	    private void setUpMap() {
	        // Hide the zoom controls as the button panel will cover it.
            GooglemapView.getUiSettings().setMyLocationButtonEnabled(true);
            GooglemapView.getUiSettings().setZoomControlsEnabled(true);
            GooglemapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            GooglemapView.setMyLocationEnabled(true);
            GooglemapView.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.854393, 4.369812), 7));


            GooglemapView.setInfoWindowAdapter(new InfoWindowAdapter() {

	            private final View window = getLayoutInflater().inflate(
	                    R.layout.location_info, null);


	            @Override
	            public View getInfoWindow(Marker marker) {

	                String title = marker.getTitle();

	                String strdata = marker.getSnippet();

	                TextView txtTitle = ((TextView) window
	                        .findViewById(R.id.textView1));

	                TextView txtdata = ((TextView) window
	                        .findViewById(R.id.textView2));

	                if (title != null) {
	                    // Spannable string allows us to edit the formatting of the
	                    // text.
	                    SpannableString titleText = new SpannableString(title);
	                    txtTitle.setText(titleText);
	                } else {
	                    txtTitle.setText("");
	                }

	                if (strdata != null) {
	                    // Spannable string allows us to edit the formatting of the
	                    // text.
	                    SpannableString titleData = new SpannableString(strdata);
	                    txtdata.setText(titleData);
	                } else {
                        txtdata.setText("");
	                }
	                return window;
	            }

	            @Override
	            public View getInfoContents(Marker marker) {
	                // this method is not called if getInfoWindow(Marker) does not
	                // return null
	                return null;
	            }

	        });

            GooglemapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    // TODO Auto-generated method stub

                    try
                    {
                        int index = arrMarkers.indexOf(marker);

                        JsonElement item = items.get(index);

                        if (item.isJsonObject()) {
                            final Place place = gson.fromJson(item, Place.class);
                            Intent intent = new Intent(SearchResultActivity.this,
                                    PlaceDetailsActivity.class);
                            intent.putExtra("place", place);
                            intent.putExtra("location", location);
                            startActivity(intent);
                        }
                        if (item.isJsonArray()) {
                            final Place[] places = gson.fromJson(item, Place[].class);
                            Intent intent = new Intent(SearchResultActivity.this,
                                    GroupedActivity.class);
                            intent.putExtra("places", places);
                            intent.putExtra("location", location);
                            startActivity(intent);
                        }
                    }
                    catch(Exception ex)
                    {

                    }

                }
            });

        }


	    private void GetCurrentLocation() {
	        double[] d = getlocation();
	        if(d[0]==0 && d[1]==0)
	        {
                // Center on Belgium
                d[0]=50.854393;
                d[1]=4.369812;
	        }
		    try{
                GooglemapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d[0], d[1]), 10));
			} catch(Exception ex) {}
	        StartSearch(d[0]+","+ d[1]);
	    }

	    public double[] getlocation() {
	        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        List<String> providers = lm.getProviders(true);

	        Location l = null;
	        for (int i = 0; i < providers.size(); i++) {
	            l = lm.getLastKnownLocation(providers.get(i));
	            if (l != null)
	                break;
	        }
	        double[] gps = new double[2];

	        if (l != null) {
	            gps[0] = l.getLatitude();
	            gps[1] = l.getLongitude();
	        }
	        return gps;
	    }


	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    switch (requestCode) {
			    case REQUEST_CODE_RECOVER_PLAY_SERVICES:
			    if (resultCode == RESULT_CANCELED) {
			    Toast.makeText(this, getResources().getString(R.string.googleps_installed),
			    Toast.LENGTH_SHORT).show();
			    finish();
			    }
		    return;

		    }
		    super.onActivityResult(requestCode, resultCode, data);
	    }
}
