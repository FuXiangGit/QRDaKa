package qiandao.qr.com.domain;

/**
 * Created by rc on 2015/2/4.
 */
public class DaKaInfo {
    private String gonghao;
    private  String name;
    private String riqi;
    private String shangxiatime;
    private String isshangban;

    public DaKaInfo(String gonghao, String name, String riqi, String shangxiatime, String isshangban) {
        this.gonghao = gonghao;
        this.name = name;
        this.riqi = riqi;
        this.shangxiatime = shangxiatime;
        this.isshangban = isshangban;
    }

    public String getGonghao() {
        return gonghao;
    }

    public void setGonghao(String gonghao) {
        this.gonghao = gonghao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRiqi() {
        return riqi;
    }

    public void setRiqi(String riqi) {
        this.riqi = riqi;
    }

    public String getShangxiatime() {
        return shangxiatime;
    }

    public void setShangxiatime(String shangxiatime) {
        this.shangxiatime = shangxiatime;
    }

    public String getIsshangban() {
        return isshangban;
    }

    public void setIsshangban(String isshangban) {
        this.isshangban = isshangban;
    }
}
