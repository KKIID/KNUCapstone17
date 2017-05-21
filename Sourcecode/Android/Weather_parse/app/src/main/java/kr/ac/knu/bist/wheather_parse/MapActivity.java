package kr.ac.knu.bist.wheather_parse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapItemizedOverlay;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlappedPOIdataHandler;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import kr.ac.knu.bist.wheather_parse.Permission.NMapViewerResourceProvider;

public class MapActivity extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapOverlayManager.OnCalloutOverlayListener, NMapLocationManager.OnLocationChangeListener {
    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "Se40Ha1PV_88isquNRJk";// 애플리케이션 클라이언트 아이디 값
    private NMapLocationManager nMapLocationManager;
    private NGeoPoint nGeoPoint;
    private NMapController mapController;
    private NMapOverlayItem nMapOverlayItem;
    private NMapMyLocationOverlay nMapMyLocationOverlay;
    private NMapViewerResourceProvider nMapViewerResourceProvider;
    private NMapOverlayManager nMapOverlayManager;
    private NMapItemizedOverlay nMapItemizedOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nMapLocationManager = new NMapLocationManager(this);
        nGeoPoint = new NGeoPoint();
        mMapView = new NMapView(this);
        if(nMapLocationManager.enableMyLocation(true)){/*현재 위치 불러오기*/

        }else{

        }
        nMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        nMapOverlayManager = new NMapOverlayManager(this, mMapView,nMapViewerResourceProvider);

        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setScalingFactor((float) 3.0, false/*true인 경우 고해상도 데이터 2배가량 더 소모*/);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mapController = mMapView.getMapController();
        mMapView.setOnMapStateChangeListener(this);/*맵의 상태가 바뀌면 호출*/
        nGeoPoint = mapController.getMapCenter();
        mMapView.setBuiltInZoomControls(true, null);
        nMapLocationManager.setOnLocationChangeListener(this);


    }


    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        /*지도가 초기화 된 후 호출*/
        Log.d("TAG",nGeoPoint.getLatitude()+"");

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
        /*map center가 바뀌면 호출*/
        Log.d("TAG", "MAPCENTERChange");
        nGeoPoint = mapController.getMapCenter();
        nMapOverlayItem = new NMapOverlayItem(nGeoPoint,"현재 위치","",getResources().getDrawable(R.drawable.ic_pin_01));
        nMapOverlayItem.setPoint(nGeoPoint);
        nMapOverlayItem.setVisibility(NMapOverlayItem.VISIBLE);

        Log.d("TAG", nGeoPoint.getLatitude() + "/" + nGeoPoint.getLongitude());
        /*나중에 이 액티비티에서 수정을 하면 아래의 동작을 수행하도록 변경하여야 함.*/
        SharedPreferences preferences = getSharedPreferences("LOCATION",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        editor.putString("LATITUDE",nGeoPoint.getLatitude()+"");
        editor.putString("LONGITUDE",nGeoPoint.getLongitude()+"");
        editor.commit();
    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }


    @Override
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect) {
        return null;
    }

    @Override
    public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
        Log.d("TAG", "onLocationChanged");
        if (nMapLocationManager.isMyLocationFixed()) {
            Log.d("TAG", "변경됨");
            mapController.setMapCenter(nGeoPoint.longitude, nGeoPoint.latitude);
        } else {
            Log.d("TAG", "변경안됨");
        }
        return false;
    }

    @Override
    public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

    }

    @Override
    public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

    }
}
