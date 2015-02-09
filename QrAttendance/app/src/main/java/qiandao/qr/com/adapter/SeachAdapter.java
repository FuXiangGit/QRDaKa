package qiandao.qr.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qiandao.qr.com.domain.DaKaInfo;
import qiandao.qr.com.qrattendance.R;

/**
 * Created by rc on 2015/2/4.
 */
public class SeachAdapter extends BaseAdapter {
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    ArrayList<DaKaInfo> dakaList = new ArrayList<DaKaInfo>();
    public SeachAdapter(Context c,ArrayList<DaKaInfo> arrayList) {
        this.mInflater = LayoutInflater.from(c);
        this.dakaList = arrayList;
    }

    @Override
    public int getCount() {
        return dakaList.size();
    }

    @Override
    public Object getItem(int position) {
        return dakaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.daka_list,null);
            holder = new ViewHolder();
//            holder.list_item_gonghao = (TextView) convertView.findViewById(R.id.list_item_gonghao);
//            holder.list_item_name = (TextView) convertView.findViewById(R.id.list_item_name);
            holder.list_item_riqi = (TextView) convertView.findViewById(R.id.list_item_riqi);
            holder.list_item_shangban = (TextView) convertView.findViewById(R.id.list_item_shangban);
            holder.list_item_xiaban = (TextView) convertView.findViewById(R.id.list_item_xiaban);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.list_item_gonghao.setText(dakaList.get(position).getGonghao());
//        holder.list_item_name.setText(dakaList.get(position).getName());
        holder.list_item_riqi.setText(dakaList.get(position).getRiqi());
        //时间
        holder.list_item_shangban.setText(dakaList.get(position).getShangxiatime());
        if(dakaList.get(position).getAttendType().equals("0")) {
            holder.list_item_xiaban.setText("打卡");
        }else {
            holder.list_item_xiaban.setText("拍照");
        }

        return convertView;
    }

    class ViewHolder {
        TextView list_item_gonghao,list_item_name,list_item_riqi,list_item_shangban,list_item_xiaban;
    }
}
