package kr.ac.knu.bist.wheather_parse.Activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
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

import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.Interface.InterfaceBinding;
import kr.ac.knu.bist.wheather_parse.NaverMap.NMapPOIflagType;
import kr.ac.knu.bist.wheather_parse.DataRequest.LocationParse;
import kr.ac.knu.bist.wheather_parse.NaverMap.NMapViewerResourceProvider;
import kr.ac.knu.bist.wheather_parse.NaverMap.searchBuffer;
import kr.ac.knu.bist.wheather_parse.R;


public class MapActivity extends NMapActivity implements InterfaceBinding {


    private ImageButton sbtn, searchMyLocation;
    private EditText addressEdit;
    private ArrayList<searchBuffer> search;
    private int itemNbr;
    private String[] items;
    private ImageButton locationSave;
    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "ew6Z1LJo5gxRQGKHrAXM";// 애플리케이션 클라이언트 아이디 값
    private NMapLocationManager nMapLocationManager;
    private NGeoPoint nGeoPoint, myGeoPoint, currnetGeopoint;
    private NMapController mapController;
    private NMapCompassManager nMapCompassManager;
    private NMapMyLocationOverlay nMapMyLocationOverlay;
    private NMapViewerResourceProvider nMapViewerResourceProvider;
    private NMapOverlayManager nMapOverlayManager;
    private NMapPOIdata poIdata;
    private RelativeLayout mapLinearLayout;
    private TextView locationTextView;
    private NMapPOIitem item;
    private LocationParse locationParse;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private searchBuffer myLocationPoint;
    private LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMapDataProviderListener(this);
        setContentView(R.layout.activity_map);

        createObject();
        /*GPS 설정 뒤에 액티비티 재 질행해야 하는 문제 해결해야함.*/
        checkOnGPS();//GPS 켜져있는지 체크
        
        
        setMapView();/*MapView Setting*/
        


        connectXml();

        nMapLocationManager.enableMyLocation(true);/*현재 위치 불러오기*/

        /*create my location overlay*/
        nMapMyLocationOverlay = nMapOverlayManager.createMyLocationOverlay(nMapLocationManager,nMapCompassManager);
        mapLinearLayout.addView(mMapView);
        nGeoPoint = mapController.getMapCenter();
        nMapLocationManager.setOnLocationChangeListener(this);

        /*save키 클릭 시 location 저장*/
        locationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("주소 저장");
                builder.setMessage("선택하신 주소가 " + myLocationPoint.getAddress() + " 이(가) 맞습니까?");
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteLocation();/*기존 것을 삭제하고 저장*/
                        saveLocation(myLocationPoint.getSearchX(),myLocationPoint.getSearchY());

                        Intent intent = new Intent(MapActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        addressEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addressEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE)
                {
                    addressSearch();
                    return true;
                }
                return false;
            }
        });

        searchMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG","searchMyLocation");
                try {
                    mapController.setMapCenter(currnetGeopoint);
                    mapController.setZoomLevel(11);
                    item.setPoint(currnetGeopoint);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        //검색버튼 클릭시 이벤트
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","click");
                addressSearch();
                mapController.setZoomLevel(11);
            }
        });



    }

    private void checkOnGPS() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){//GPS 켜져 있는지 체크
            Toast.makeText(getApplicationContext(),"GPS 설정을 켜주세요.",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(i);
        }
    }

    public void connectXml(){
        sbtn =(ImageButton) findViewById(R.id.sbtn);
        addressEdit=(EditText)findViewById(R.id.edit);
        locationSave=(ImageButton)findViewById(R.id.save);
        searchMyLocation = (ImageButton)findViewById(R.id.imageButton);
        mapLinearLayout = (RelativeLayout) findViewById(R.id.mapLinearLayout);
        locationTextView = (TextView)findViewById(R.id.locationText);
    }

    private void addressSearch(){/*검색 기능*/
        if ( addressEdit.getText().toString().length() == 0 ) {
                    /*주소 입력이 공백일 때 처리할 내용*/
            Toast.makeText(getApplicationContext(),"주소를 입력해 주세요.",Toast.LENGTH_LONG).show();
            //Log.d("TAG","공백OK");
        } else {
            if(item ==null){
                        /*만약 마커가 없으면 마커를 임시로 생성.*/
                createMarker(30,30);
            }
                    /*검색 버튼을 누르면 키보드를 숨김.*/
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(addressEdit.getWindowToken(), 0);

                    /*주소 입력이 공백이 아닐 때 주소에 대한 정보 ReQuest보내기 */
            locationParse(addressEdit.getText().toString());

            //mapController.setMapCenter(search.getSearchX(),search.getSearchY());
            //NGeoPoint temppoint = new NGeoPoint(search.getSearchX(),search.getSearchY());
            if(search==null){ /*잘못된 주소를 입력 했을 때 (예시) 없는 주소, 이상한 문자*/
                Toast.makeText(getApplicationContext(),"주소를 정확히 입력해 주세요.",Toast.LENGTH_LONG).show();

            }else if(search.size()==1){ /*입력된 값이 하나의 주소만을 찾았을 때*/
                locationTextView.setText(search.get(0).getAddress());
                            /*그 주소로 map접근*/
                mapController.setZoomLevel(10);
                mapController.setMapCenter(search.get(0).getSearchX(),search.get(0).getSearchY());
                NGeoPoint tempPoint = new NGeoPoint(search.get(0).getSearchX(),search.get(0).getSearchY());
                myLocationPoint = new searchBuffer(search.get(0).getSearchX(),search.get(0).getSearchY(),search.get(0).getAddress());
                item.setPoint(tempPoint);
            }
            else{ /*입력된 값이 여러개의 주소를 찾았을 때*/
                locationSelectDialog(search);
                Toast.makeText(getApplicationContext(),"여러개의 주소가 검색",Toast.LENGTH_LONG).show();
            }
            //item.setPoint(temppoint);
            //Log.d("TAG","in Button x:"+search.getSearchX()+"y:"+search.getSearchY());
        }

    }

    private void locationSelectDialog(final ArrayList<searchBuffer> search){/*여러개의 주소가 검색될 경우 선택하도록 하는 Dialog*/

        items = new String[search.size()];

        for(int i=0;i<search.size();i++){
            items[i] = search.get(i).getAddress();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("주소 선택");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemNbr =which;
            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                locationTextView.setText(items[itemNbr]);
                /*검색한 위치로 mapCenter를 변경*/
                mapController.setZoomLevel(10);
                mapController.setMapCenter(search.get(itemNbr).getSearchX(),search.get(itemNbr).getSearchY());
                NGeoPoint tempPoint = new NGeoPoint(search.get(itemNbr).getSearchX(),search.get(itemNbr).getSearchY());
                myLocationPoint = new searchBuffer(search.get(itemNbr).getSearchX(),search.get(itemNbr).getSearchY(),search.get(itemNbr).getAddress());
                item.setPoint(tempPoint);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //검색한 주소의 위치 좌표로 받아오기
    public void locationParse(final String address){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("TAG","address ="+address);
                    search=locationParse.getLocation(address);
//                    for(int i=0;i<search.size();i++)
//                    Log.d("TAG","넘어온 값 ="+search.get(i).getSearchX() + search.get(i).getSearchY()+ search.get(i).getAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        //mMapView.setBuiltInAppControl(true);/*네이버 지도 앱 실행 버튼 생성*/
    }

    public void createMarker(double lat, double lon){/*마커생성*/
        Log.d("TAG","create Marker");
        Log.d("TAG","Location"+lat+"/"+lon);
        int markerId = NMapPOIflagType.PIN;
        int markerId2 = NMapPOIflagType.SPOT;
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
        locationParse = new LocationParse();
        nMapLocationManager = new NMapLocationManager(this);
        nMapCompassManager = new NMapCompassManager(this);
        nGeoPoint = new NGeoPoint();
        mMapView = new NMapView(this);
        nMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        nMapOverlayManager = new NMapOverlayManager(this,mMapView,nMapViewerResourceProvider);
        mapController = mMapView.getMapController();
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

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
    public void saveLocation(double logitude, double latitude){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putString("LATITUDE",latitude+"");
        editor.putString("LONGITUDE",logitude+"");
        editor.commit();
    }
    public void deleteLocation(){
        if(editor!=null) {
            editor.remove("Location");
            editor.commit();
        }
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
    public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {/*나의 현재위치가 바뀔 경우 호출됨*/
        Log.d("TAG", "onLocationChanged");
        if (nMapLocationManager.isMyLocationFixed()) {
            Log.d("TAG", "변경됨");
            mapController.setMapCenter(nGeoPoint.longitude, nGeoPoint.latitude);
            nGeoPoint = mapController.getMapCenter();
            currnetGeopoint = nGeoPoint;
            myLocationPoint = new searchBuffer(nGeoPoint.longitude,nGeoPoint.latitude,"");
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
        item.setTitle("현재 위치");/*마커 아이템 수정*/
        myLocationPoint = new searchBuffer(myGeoPoint.getLongitude(),myGeoPoint.getLatitude(),nMapPlacemark.toString());
    }

    @Override
    public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {/*마커 위치를 바꾸면 수행함*/
        NGeoPoint point = nMapPOIitem.getPoint();

        /*좌표를 주소로 변환하는 메소드*/
        /*onReverseGeocoderResponse : CallBack Method 참고*/
        findPlacemarkAtLocation(point.getLongitude(),point.getLatitude());
        Log.d("TAG","저장될 위도 경도"+point.getLongitude()+"/"+point.getLatitude());
        myGeoPoint=point;

    }


}
