package qiandao.qr.com.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import qiandao.qr.com.tools.AES;

/**
 * Created by rc on 2015/2/2.
 */
public class SharedPreferenceStorage {
    /*
    * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
    */
    static String cKey = "3Rz68FS5ZFKMvJAK";

    /*
     * 存储用户名user_id
	 */
    public static void saveLoginUserInfo(Context context, User user) throws Exception {
        SharedPreferences sp = context.getSharedPreferences("QRUser",
                context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString("user_id", user.getId());
        et.putString("username", user.getUsername());
        et.putString("myphone", user.getMyphone());
        et.putString("gonghao", user.getGonghao());
        //密码输入
        String cSrc = user.getUserpassword() + "";
        System.out.println(cSrc);
        System.out.println("最早的：" + cSrc);
        // 加密
        String enString;
        try {
            enString = AES.Encrypt(cSrc, cKey);
            System.out.println("加密后的字串是：" + enString);
            et.putString("userpassword", enString);
            // 解密
            String DeString = AES.Decrypt(enString, cKey);
            System.out.println("解密后的字串是：" + DeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        et.putString("islogin", user.getUserpassword());
        et.commit();
    }

    /*
     * 取用户名user_id
	 */
    public static User getLoginUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("QRUser",
                context.MODE_PRIVATE);
        if (sp.getString("username", null) != null) {
            Log.d("way", sp.getString("myphone", null) + "电话");
            String myPassWord = sp.getString("userpassword", null);
            // 解密
            String userpass = null;
            try {
                userpass = AES.Decrypt(myPassWord, cKey);
                System.out.println("解密后的字串是：" + userpass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new User(sp.getString("user_id", null), sp.getString(
                    "username", null), sp.getString("myphone", null),
                    sp.getString("gonghao", null),
                    userpass, sp.getString("islogin", null));
        } else {
            return new User("0");
        }
    }
}
