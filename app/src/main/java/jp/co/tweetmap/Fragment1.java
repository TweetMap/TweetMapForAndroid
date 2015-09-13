/*
 * Copyright 2015 TweetMap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tweetmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.util.CustomInfoAdapter;

import jp.co.util.LogUtil;
import jp.co.util.MapUtil;
import jp.co.util.StationInfo;
import jp.co.util.StationInfoLoader;
import jp.co.util.StationInfoParser;
import jp.co.util.TweeterStatusLoader;
import jp.co.util.TwitterListAdapter;
import jp.co.util.TwitterStatusItem;
import jp.co.util.TwitterUtils;

/**
 * Fragment for displaying map.
 */
public class Fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "Fragment1";

    /** Loader ID */
    private static final int LOADER_ID = 1;

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** GoogleMap */
    private GoogleMap mMap;

    /** Current marker */
    Marker mMarker = null;

    /** HashMap of station name and marker */
    private HashMap<Marker, StationInfo> mEkiMarkerMap;

    /** Center position of the map */
    private CameraPosition mCenterPosition = null;

    /** Twitter item list */
    private ArrayList<TwitterStatusItem> mItemList = null;

    /** AsyncTask for loading tweet and show dialog */
    private TweeterStatusLoader mTweetLoader = null;

    /*************************************************************************************
     *                          Override Methods for Fragment                            *
     *************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onCreate() ###");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            // Save fragment
            this.setRetainInstance(true);
        }
        mEkiMarkerMap = new HashMap<Marker, StationInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onCreateView() ###");
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        // Get map object if mMap is null
        setUpMapIfNeeded();
        // Setup map
//        setUpMap();

        return view;
    }

    @Override
    public void onResume() {
        if (LogUtil.isDebug()) Log.e(TAG, "### onResume() ###");
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LogUtil.isDebug()) Log.e(TAG, "### requestCode : " + requestCode);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MapUtil.REQUEST_CODE_FRAGMENT_1: // Called form StationSearchActivity
                    Bundle bundle = data.getExtras();
                    CameraPosition camerapos = new CameraPosition.Builder()
                            .target(new LatLng(bundle.getDouble("key.latitude"), bundle.getDouble("key.longitude"))).zoom(15.0f).build();
                    // Move camera position
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
                    // Set current position
                    mCenterPosition = camerapos;
                    getNearestStation();
                    break;

                case MapUtil.REQUEST_CODE_TWEET_STATUS_LOADER:
                    mItemList = (ArrayList<TwitterStatusItem>) data.getSerializableExtra("LIST");
                    showTweetList();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment1_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Set a NavUp to toggle icon
        if (LogUtil.isDebug()) Log.e(TAG, "### item : " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.fragment1_action_search:
                Intent intent = new Intent(getActivity(), StationSearchActivity.class);
                startActivityForResult(intent, MapUtil.REQUEST_CODE_FRAGMENT_1);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /*************************************************************************************
     *                          Methods for setup map fragment                           *
     *************************************************************************************/
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map1)).getMap();
            // Check if we were successful in obtaining the map.
            if (null != mMap) {
                MapsInitializer.initialize(getActivity());

                UiSettings settings = mMap.getUiSettings();
                settings.setCompassEnabled(true);
                settings.setMyLocationButtonEnabled(true);
                settings.setZoomControlsEnabled(true);
                settings.setRotateGesturesEnabled(false);
                settings.setScrollGesturesEnabled(true);
                settings.setTiltGesturesEnabled(true);
                settings.setZoomGesturesEnabled(true);

                // Set map type
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // Show current position button
                mMap.setMyLocationEnabled(true);
                // Add InfoWindow listener
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (!TwitterUtils.hasAccessToken(getActivity().getApplicationContext())) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_twitter_not_oauth), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Add CameraChange listener
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        if (mCenterPosition != null) {
                            if (0.5 < calcDistance(mCenterPosition, cameraPosition)) {
                                getNearestStation();
                            }
                            Log.e(getClass().getName(), Double.toString(calcDistance(mCenterPosition, cameraPosition)));
                            mCenterPosition = cameraPosition;
                        }
                    }
                });

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        if (LogUtil.isDebug()) Log.e(TAG, "### mapInit() ###");
        // LocationManager
        LocationManager locationManager = (LocationManager) this.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        double latitude = MapUtil.TOKYO_STATION_LATITUDE;
        double longitude =MapUtil.TOKYO_STATION_LONGITUDE;

        if (null != locationManager ) {
            String bestProv = locationManager.getBestProvider( new Criteria(), true);

            // Get location information from GPS
            Location myLocate = locationManager.getLastKnownLocation(bestProv);
            if ( null == myLocate ) {
                myLocate = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if ( null == myLocate ) {
                myLocate = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if ( null != myLocate ) {
                latitude = myLocate.getLatitude();
                longitude = myLocate.getLongitude();
            }
        }

        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15.0f).build();
        // Move camera position
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
        // Set current position
        mCenterPosition = camerapos;

        getNearestStation();
    }

    /**
     * Calculates distance of cameras coordinate.
     *
     * @param a old camera position.
     * @param b new camera position.
     */
    private double calcDistance(CameraPosition a, CameraPosition b) {
        if (LogUtil.isDebug()) Log.e(TAG, "### calcDistance() ###");
        double lata = Math.toRadians(a.target.latitude);
        double lnga = Math.toRadians(a.target.longitude);

        double latb = Math.toRadians(b.target.latitude);
        double lngb = Math.toRadians(b.target.longitude);

        return MapUtil.EQUATORIAL_RADIUS * Math.acos(Math.sin(lata) * Math.sin(latb) + Math.cos(lata) * Math.cos(latb) * Math.cos(lngb - lnga));
    }

    /**
     * Prepares the URL of the Web API,and get the center position of the map.
     *
     * @see <a href="http://express.heartrails.com/api.html">Web API</a>
     */
    public void getNearestStation() {
        if (LogUtil.isDebug()) Log.e(TAG, "### getNearestStation() ###");
        // Get current center position of map
        CameraPosition cameraPos = mMap.getCameraPosition();

        Bundle bundle = new Bundle();
        bundle.putString("y", Double.toString(cameraPos.target.latitude));
        bundle.putString("x", Double.toString(cameraPos.target.longitude));
        bundle.putString("url", MapUtil.WEB_API_URL);

        // Initialize LoaderManager
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    /**
     * Query search the tweets to be displayed in the list.
     *
     */
    private void updateTweet() {
        if (LogUtil.isDebug())
            Log.e(TAG, "### updateTweet() ###");

        if (TwitterUtils.hasAccessToken(getActivity().getApplicationContext())) {

            StationInfo station = mEkiMarkerMap.get(mMarker);

            Bundle bundle = new Bundle();
            bundle.putString("MESSAGE", getResources().getString(R.string.info_window_loading));
            bundle.putString("NAME",station.name );
            bundle.putString("LINE",station.line );
            bundle.putString("FIELD_TYPE", "HOUR_OF_DAY");
            bundle.putInt("RANGE", -1);
            mTweetLoader = TweeterStatusLoader.newInstance(bundle, this, getActivity().getApplicationContext());
            mTweetLoader.show(getFragmentManager(), "Fragment0");
            mTweetLoader.doAsyncTask();
        }
    }

    /**
     * Shows tweet list.
     *
     */
    private void showTweetList() {
        TwitterListAdapter adapter = new TwitterListAdapter(getActivity(), 0, mItemList);
        String title = mMarker.getTitle() + "\r\n" + "(" + mMarker.getSnippet() + ")" + " - " + mItemList.size() + "件";

        // Set tweet dialog
        TwitterListDialogFragment dialog = new TwitterListDialogFragment();
        dialog.setAdapter(adapter);
        dialog.setTitle(title);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(dialog, null);
        ft.commitAllowingStateLoss();
    }

    /*************************************************************************************
     *                      Override Methods for LoaderCallbacks                         *
     *************************************************************************************/
    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onCreateLoader() ###");
        StationInfoLoader loader = null;

        switch (id) {
            case LOADER_ID:
                // generate request URL
                String url = bundle.getString("url")
                        + "x=" + bundle.getString("x") + "&"
                        + "y=" + bundle.getString("y");
                loader = new StationInfoLoader(getActivity().getApplicationContext(), url);
                // access Web API
                loader.forceLoad();
                break;
            default:
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String body) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onLoadFinished() ###");
        // Can't access Web API, return null
        if (body == null)
            return;

        switch (loader.getId()) {
            case LOADER_ID:
                // Parse response(body)
                StationInfoParser parse = new StationInfoParser();
                parse.loadJson(body);

                // Clear marker
                mMap.clear();
                mEkiMarkerMap.clear();

                // Set markers on map
                for (StationInfo st : parse.getStationInfo()) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(st.y, st.x))
                            .title(st.name)
                            .snippet(st.line)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    // Save station info and marker
                    mEkiMarkerMap.put(marker, st);
                    mMap.setInfoWindowAdapter(new CustomInfoAdapter(getActivity()));
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            mMarker = marker;
                            updateTweet();
                            return false;
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> str) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onLoaderReset() ###");
        // N.O.P
    }
}