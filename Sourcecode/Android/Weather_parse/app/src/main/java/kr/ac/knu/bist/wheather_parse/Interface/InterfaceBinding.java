package kr.ac.knu.bist.wheather_parse.Interface;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

/**
 * Created by BIST120 on 2017-05-22.
 */

public interface InterfaceBinding extends NMapView.OnMapStateChangeListener, NMapOverlayManager.OnCalloutOverlayListener, NMapLocationManager.OnLocationChangeListener, NMapActivity.OnDataProviderListener, NMapPOIdataOverlay.OnFloatingItemChangeListener {

}
