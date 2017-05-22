package kr.ac.knu.bist.wheather_parse;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapItemizedOverlay;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import kr.ac.knu.bist.wheather_parse.Permission.NMapViewerResourceProvider;

public class MapActivity extends NMapActivity implements InterfaceBinding {
    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "Se40Ha1PV_88isquNRJk";// 애플리케이션 클라이언트 아이디 값
    private NMapLocationManager nMapLocationManager;
    private NGeoPoint nGeoPoint;
    private NMapController mapController;
    private NMapCompassManager nMapCompassManager;
    private NMapMyLocationOverlay nMapMyLocationOverlay;
    private NMapViewerResourceProvider nMapViewerResourceProvider;
    private NMapOverlayManager nMapOverlayManager;
    private NMapPOIdata poIdata;
    private LinearLayout mapLinearLayout;
    private TextView locationTextView;
    private NMapPOIitem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMapDataProviderListener(this);
        setContentView(R.layout.activity_map);
        createObject();
        setMapView();/*MapView Setting*/
        mapLinearLayout = (LinearLayout)findViewById(R.id.mapLinearLayout);
        locationTextView = (TextView)findViewById(R.id.locationText);
        if(nMapLocationManager.enableMyLocation(true)){/*현재 위치 불러오기*/
        }else{
        }
        /*create my location overlay*/
        nMapMyLocationOverlay = nMapOverlayManager.createMyLocationOverlay(nMapLocationManager,nMapCompassManager);
        mapLinearLayout.addView(mMapView);
        nGeoPoint = mapController.getMapCenter();

        nMapLocationManager.setOnLocationChangeListener(this);
    }

    public void setMapView(){
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setScalingFactor((float) 3.0, false/*true인 경우 고해상도 데이터 2배가량 더 소모*/);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setOnMapStateChangeListener(this);/*맵의 상태가 바뀌면 호출*/
        mMapView.setBuiltInZoomControls(true, null);
    }

    public void createMarker(double lat, double lon){/*마커생성*/
        Log.d("TAG","create Marker");
        Log.d("TAG","Location"+lat+"/"+lon);
        int markerId = NMapPOIflagType.PIN;
        poIdata = new NMapPOIdata(1, nMapViewerResourceProvider);
        poIdata.beginPOIdata(1);
        item = poIdata.addPOIitem(lon, lat, "현재 위치", markerId, 0);
        item.setPoint(mapController.getMapCenter());
        item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH|NMapPOIitem.FLOATING_DRAG);
        item.setRightButton(false);
        poIdata.endPOIdata();
        NMapPOIdataOverlay poIdataOverlay = nMapOverlayManager.createPOIdataOverlay(poIdata,null);
        poIdataOverlay.setOnFloatingItemChangeListener(this);/*onPointChanged*/
    }

    public void createObject(){/*객체 생성*/
        nMapLocationManager = new NMapLocationManager(this);
        nMapCompassManager = new NMapCompassManager(this);
        nGeoPoint = new NGeoPoint();
        mMapView = new NMapView(this);
        nMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        nMapOverlayManager = new NMapOverlayManager(this,mMapView,nMapViewerResourceProvider);
        mapController = mMapView.getMapController();

    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        /*지도가 초기화 된 후 호출*/
        Log.d("TAG",nGeoPoint.getLatitude()+"");
        if(nMapError==null){    //error없음
            Toast.makeText(this,"지도를 움직여 주소를 지정하세요",Toast.LENGTH_LONG).show();
        }else{
            Log.e("TAG","onMapInitHandler: error="+nMapError.toString());
        }
    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
        /*map center가 바뀌면 호출*/
        Log.d("TAG", "MAPCENTERChange");
        nGeoPoint = mapController.getMapCenter();
        Log.d("TAG", nGeoPoint.getLatitude() + "/" + nGeoPoint.getLongitude());
        /*나중에 이 액티비티에서 수정을 하면 아래의 동작을 수행하도록 변경하여야 함.*/
    }
    public void saveLocation(){
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
            nGeoPoint = mapController.getMapCenter();
            createMarker(nGeoPoint.getLatitude(),nGeoPoint.getLongitude());
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

    @Override
    public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
        if (nMapError != null) {
            Log.d("TAG", "Failed to findPlacemarkAtLocation: error=" + nMapError.toString());
            return;
        }
        locationTextView.setText(nMapPlacemark.toString());
        item.setTitle("현재 위치:"+nMapPlacemark.toString());/*마커 아이템 수정*/
    }

    @Override
    public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {/*마커 위치를 바꾸면 수행함*/
        NGeoPoint point = nMapPOIitem.getPoint();

        /*좌표를 주소로 변환하는 메소드*/
        /*onReverseGeocoderResponse : CallBack Method 참고*/
        findPlacemarkAtLocation(point.getLongitude(),point.getLatitude());
        saveLocation();

    }
}
