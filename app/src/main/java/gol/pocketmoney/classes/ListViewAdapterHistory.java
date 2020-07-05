package gol.pocketmoney.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import gol.pocketmoney.R;

public class ListViewAdapterHistory extends ArrayAdapter<details> {

    public ListViewAdapterHistory(Context context, ArrayList<details> history){
        super(context, 0, history);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        details p= getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_history, parent, false);
        }

        TextView tv_calc = (TextView) convertView.findViewById(R.id.tv_calc);
        TextView tv_dist = (TextView) convertView.findViewById(R.id.tv_dist);
        TextView tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tv_credits = (TextView) convertView.findViewById(R.id.tv_credits);
        TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);

        tv_calc.setText(Integer.toString(p.getCalorie()));
        tv_dist.setText(Float.toString(p.getDistance()));
        tv_speed.setText(Float.toString(p.getSpeed()));
        tv_time.setText(new DecimalFormat("0.0").format(p.getTime()));
        tv_credits.setText(Integer.toString(p.getCredit()));
        if(p.getSpeed()==4.2f)
            tv_status.setText("ON-FOOT");
        else
            tv_status.setText("CYCLE");

        return convertView;
    }
}