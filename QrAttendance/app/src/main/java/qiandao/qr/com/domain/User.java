package qiandao.qr.com.domain;

/**
 * Created by rc on 2015/2/2.
 */
public class User {
    private String id;
    private String username;
    private String myphone;
    private String gonghao;
    private String userpassword;
    private String islogin;

    public User(String id, String username, String myphone, String gonghao, String userpassword, String islogin) {
        this.id = id;
        this.username = username;
        this.myphone = myphone;
        this.gonghao = gonghao;
        this.userpassword = userpassword;
        this.islogin = islogin;
    }

    public User(String islogin) {
        this.islogin = islogin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMyphone() {
        return myphone;
    }

    public void setMyphone(String myphone) {
        this.myphone = myphone;
    }

    public String getGonghao() {
        return gonghao;
    }

    public void setGonghao(String gonghao) {
        this.gonghao = gonghao;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getIslogin() {
        return islogin;
    }

    public void setIslogin(String islogin) {
        this.islogin = islogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", myphone='" + myphone + '\'' +
                ", gonghao='" + gonghao + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", islogin='" + islogin + '\'' +
                '}';
    }
}
