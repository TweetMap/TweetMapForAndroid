package jp.co.tweetmap;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

public class MockLocationProvider {
  private String providerName;
  private Context context;

  public MockLocationProvider(@NonNull String providerName, @NonNull Context context) {
    this.providerName = providerName;
    this.context = context;
  }

  public void enable() {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    locationManager.setTestProviderEnabled(providerName, true);
  }

  public void disable() {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    locationManager.setTestProviderEnabled(providerName, false);
  }

  public void setLocation(double lat, double lon) {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    Location mockLocation = new Location(providerName);
    mockLocation.setLatitude(lat);
    mockLocation.setLongitude(lon);
    mockLocation.setAltitude(0);
    mockLocation.setTime(System.currentTimeMillis());
    locationManager.setTestProviderLocation(providerName, mockLocation);
  }
  
  public void prepare() {
    LocationManager locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    locationManager.addTestProvider(providerName, false, false, false, true, true, false, false, 0, 5);
    locationManager.setTestProviderEnabled(providerName, true);
  }

  public void shutdown() {
    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    lm.removeTestProvider(providerName);
  }
}