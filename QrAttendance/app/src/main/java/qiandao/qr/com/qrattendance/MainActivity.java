package qiandao.qr.com.qrattendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dingwei.location.LocationActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.timessquare.CalendarPickerView;
import com.zxing.activity.CaptureActivity;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.qiandao.myapplication.MyApplication;
import qiandao.qr.com.domain.SharedPreferenceStorage;
import qiandao.qr.com.domain.User;
import qiandao.qr.com.tools.Constant;


public class MainActivity extends ActionBarActivity {

    @ViewInject(R.id.qr_return)
    public TextView qr_return;
    @ViewInject(R.id.user_task)
    private TextView user_task;
    //时间
    private static final int msgKey1 = 1;
    @ViewInject(R.id.mytime)
    private TextView mTime;
    //百度地图
    private LocationClient mLocationClient;
    @ViewInject(R.id.txt_location)
    private TextView LocationResult;
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";
    String loginName, loginPass;
    //打卡参数
    String daka_type, dizhi, onoff, gonghao, name;
    Double jingdu, weidu;
    //拍照
    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    File file;
    Bitmap myphoto;
    String pictureDir;

    //日历
    @ViewInject(R.id.calendar_view)
    private CalendarPickerView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        //日历展示
        initRiLi();
        //判断是否已经登陆过
        isLogin();
        //时间显示
        new TimeThread().start();
        //定位控制
        myLocation();

    }

    private void initRiLi() {
        final Calendar thisMonth = Calendar.getInstance();
        thisMonth.set(Calendar.DAY_OF_MONTH, 1);
        final Calendar nextMonth = Calendar.getInstance();
        nextMonth.set(Calendar.DAY_OF_MONTH,nextMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.init(thisMonth.getTime(), nextMonth.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    private void isLogin() {
        if (SharedPreferenceStorage.getLoginUser(this).getIslogin().equals("0")) {
            Intent toLoginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(toLoginIntent);
        } else {
            User user = SharedPreferenceStorage.getLoginUser(this);
            loginName = user.getMyphone();
            loginPass = user.getUserpassword();
            gonghao = user.getGonghao();
            name = user.getUsername();
            loginHttp();
        }
    }

    private void myLocation() {
        mLocationClient = ((MyApplication) getApplication()).mLocationClient;
        ((MyApplication) getApplication()).mLocationResult = LocationResult;
        InitLocation();
        //开始定位
        mLocationClient.start();
    }


    @OnClick({R.id.daka, R.id.ding_wei, R.id.photo})
    public void MainClick(View v) {
        switch (v.getId()) {
            case R.id.daka:
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 100);
                daka_type = "0";
                onoff = "0";
                break;
            case R.id.ding_wei:
                Intent openLocation = new Intent(MainActivity.this, SeacherActivity.class);
                startActivity(openLocation);
                break;
            case R.id.photo:
                takephoto();
                break;
        }
    }

    private void takephoto() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
           String saveDir = Environment.getExternalStorageDirectory()
                       + "/daka";
                File dir = new File(saveDir);
              if (!dir.exists()) {
                        dir.mkdir();
                    }
                file = new File(saveDir, "temp.jpg");
                file.delete();
                if (!file.exists()) {
                        try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                              Log.d("jack","文件存储有问题");
                                return;
                            }
                    }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, PHOTO_GRAPH);
            } else {
                Toast.makeText(MainActivity.this,
                                "请确保内存卡正常", Toast.LENGTH_LONG)
                        .show();
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("jack", "resultCode" + resultCode + "requestCode" + requestCode);
        if (resultCode == NONE)
            return;
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {

            if (requestCode == 100) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                qr_return.setText(scanResult);
                httpDaKa();
            }

            // 拍照
            if (requestCode == PHOTO_GRAPH) {
                if (file != null && file.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    myphoto = BitmapFactory.decodeFile(file.getPath(), options);
                    pictureDir = file.getPath();
                    Toast.makeText(this, "有文件了"+pictureDir,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "文件为空",
                            Toast.LENGTH_LONG).show();
                }
                daka_type = "1";
                onoff = "0";
                //拍照后上传
                httpDaKa();
            }
        }

    }

    //打卡接口
    private void httpDaKa() {

        dizhi = ((MyApplication) getApplication()).Ddizhi;
        jingdu = ((MyApplication) getApplication()).Djingdu;
        weidu = ((MyApplication) getApplication()).Dweidu;
        final String url = Constant.path + "oasys/userinfoAction!daka.action";
        RequestParams params = new RequestParams();
        params.addBodyParameter("type", daka_type);
        params.addBodyParameter("dizhi", dizhi);
        params.addBodyParameter("jingdu", String.valueOf(jingdu));
        params.addBodyParameter("weidu", String.valueOf(weidu));
        params.addBodyParameter("onoff", onoff);
        params.addBodyParameter("gonghao", gonghao);
        params.addBodyParameter("name", name);
        if(daka_type.equals("1")) {
            Log.d("jack",pictureDir+"上传图片》》》》》》》》》》》》》》"+pictureDir);
            params.addBodyParameter("pic", new File(pictureDir));
        }


        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onStart() {
                Log.d("jack", "开始上传" + url);
//                Log.d("jack", "daka" + daka_type + dizhi + String.valueOf(jingdu) + String.valueOf(weidu) + onoff + gonghao + name);
            }

            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                Log.d("jack", "上传成功");
                String res = objectResponseInfo.result.toString();
                Log.d("jack", res);
                JSONObject jsonObject = null;
                JSONObject userJson = null;
                try {
                    jsonObject = new JSONObject(res);
                    int isSuccess = jsonObject.optInt("retcode");
                    String timejieguo=null;
                    if (isSuccess == 1) {
                        Time nowTime = new Time();
                        nowTime.setToNow();
                        int hour = nowTime.hour;
                        int minute = nowTime.minute;
                        if(hour>0&&hour<=6) {
                            timejieguo = "凌晨好！";
                        }else if(hour>6&&hour<=9){
                            timejieguo = "早上好！";
                        }else if((hour>9&&hour<=11)||(hour>11&&hour<12&&minute<=30)){
                            timejieguo="上午好！";
                        }else if((hour>11&&hour<12&&minute>30)||(hour>=12&&hour<13)||(hour>13&&hour<14&&minute<=30)){
                            timejieguo="中午好！";
                        }else if((hour>13&&hour<14&&minute>30)||(hour>=14&&hour<17)||(hour>17&&hour<18&&minute<=30)){
                            timejieguo="下午好！";
                        }else if((hour>17&&hour<18&&minute>30)||(hour>=18&&hour<19)){
                            timejieguo="傍晚好！";
                        }else{
                            timejieguo="晚上好！";
                        }
                        Toast.makeText(MainActivity.this, name+","+timejieguo, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "打卡失败！", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MainActivity.this, "链接网络失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        //再次登录验证
        isLogin();
    }

    //主界面时间显示方法
    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    String strBeginDate = simpleDateFormat.format(new Date());
                    mTime.setText(strBeginDate);

                    break;

                default:
                    break;
            }
        }
    };

    //百度定位
    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(3000);//设置发起定位请求的间隔时间为3000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    //是否登陆或者修改了密码
    private void loginHttp() {
        final String url = Constant.path + "oasys/userinfoAction!userLogin.action";
        RequestParams params = new RequestParams();
        params.addBodyParameter("tel", loginName);
        params.addBodyParameter("pass", loginPass);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onStart() {
                Log.d("jack", "开始上传" + url);
                Log.d("jack", "开始上传" + loginName + loginPass);
            }

            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                Log.d("jack", "上传成功");
                String res = objectResponseInfo.result.toString();
                Log.d("jack", res);
                JSONObject jsonObject = null;
                JSONObject userJson = null;
                JSONArray obj2List = null;
                JSONObject jsonObj2 = null;
                try {
                    jsonObject = new JSONObject(res);
                    int isSuccess = jsonObject.optInt("retcode");
                    if (isSuccess == 1) {
                        userJson = jsonObject.getJSONObject("obj");
                        String uID = userJson.optString("id");
                        String uName = userJson.optString("name");
                        String uPhone = userJson.optString("tel");
                        String uGongHao = userJson.optString("attr5");
                        User user = new User(uID, uName, uPhone, uGongHao, loginPass, "1");
                        Log.d("jack", user.toString());
                        try {
                            SharedPreferenceStorage.saveLoginUserInfo(
                                    MainActivity.this, user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //返回的值
                        obj2List = jsonObject.getJSONArray("obj2");
                        String strTask = "";
                        for(int i = 0;i<obj2List.length();i++){
                            strTask = strTask+obj2List.getString(i)+"\n";
                            Log.d("jack",obj2List.getString(i));
                        }
                        user_task.setText(strTask);
                    } else {
                        Toast.makeText(MainActivity.this, "链接失败或者密码修改，请重新登陆！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MainActivity.this, "链接网络失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

}
