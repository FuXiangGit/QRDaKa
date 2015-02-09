package qiandao.qr.com.qrattendance;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import qiandao.qr.com.adapter.SeachAdapter;
import qiandao.qr.com.domain.DaKaInfo;
import qiandao.qr.com.domain.SharedPreferenceStorage;
import qiandao.qr.com.domain.User;
import qiandao.qr.com.tools.Constant;


public class SeacherActivity extends ActionBarActivity {

    @ViewInject(R.id.ugonghao)
    private TextView ugonghao;
    @ViewInject(R.id.uname)
    private TextView uname;
    @ViewInject(R.id.list_kaoqing)
    ListView list_kaoqing;
    ArrayList<DaKaInfo> dakaList = new ArrayList<DaKaInfo>();
    String gonghao,name;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seacher);
        ViewUtils.inject(this);
        context = this;
        User user = SharedPreferenceStorage.getLoginUser(this);
        gonghao = user.getGonghao();
        name = user.getUsername();
        ugonghao.setText(gonghao);
        uname.setText(name);
        initList();
    }

    private void initList() {
        final String url = Constant.path + "oasys/userinfoAction!queryPeraff.action";
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("gonghao", gonghao);
        http.send(HttpRequest.HttpMethod.POST, url,params, new RequestCallBack<Object>() {
            @Override
            public void onStart() {
                Log.d("jack", "开始上传" + url+"工号"+gonghao);
            }
            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                String res = objectResponseInfo.result.toString();
                Log.d("jack", res);
                dakaList.clear();
                JSONArray jsonArray = null;
                JSONObject userJson = null;
                try {
                    jsonArray = new JSONArray(res);
                    for(int i=0;i<jsonArray.length();i++){
                        userJson = jsonArray.getJSONObject(i);
                        String riqi = userJson.getString("cardDate");
                        String dakatime = userJson.getString("cardTime");
                        //onoff ：0上班 ，1下班
                        String onoff = userJson.getString("onOff");
                        //attendType ：0打卡1拍照
                        String attendType  = userJson.getString("attendType");

                        Log.d("jack",riqi+"------"+dakatime+"-----"+onoff);
                        DaKaInfo daKaInfo = new DaKaInfo(gonghao,name,riqi,dakatime,onoff,attendType);
                        dakaList.add(daKaInfo);
                    }
                    SeachAdapter seachAdapter = new SeachAdapter(context,dakaList);
                    list_kaoqing.setAdapter(seachAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(SeacherActivity.this, "链接网络失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

}
