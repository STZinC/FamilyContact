package com.example.calls.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calls.ContactDetailActivity;
import com.example.calls.R;
import com.wickerlabs.logmanager.LogObject;
import com.wickerlabs.logmanager.LogsManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    private List<LogObject> logs;
    private List<Integer> CountList;
    private Context context;
    private int resource;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View logView;
        CardView phoneLog;
        TextView phone;
        TextView duration;
        TextView date;
        TextView count;
        ImageView imageView;
        Button button;

        public ViewHolder(View row) {
            super(row);
            logView = row;
            phoneLog = (CardView) row.findViewById(R.id.phoneNumLog);
            phone = (TextView) row.findViewById(R.id.phoneNum);
            duration = (TextView) row.findViewById(R.id.callDuration);
            date = (TextView) row.findViewById(R.id.callDate);
            count = (TextView) row.findViewById(R.id.phoneNumCounts);
            imageView = (ImageView) row.findViewById(R.id.callImage);
            button = (Button) row.findViewById(R.id.button);
        }
    }

    public LogsAdapter(List<LogObject> calllLogs) {
        this.CountList = new LinkedList<Integer>();
        this.logs = new LinkedList<LogObject>();
        LogsAggregation(calllLogs);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.log_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //设置点击效果card拨号
        holder.logView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String tel = "tel:" + logs.get(position).getNumber();
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse(tel));

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "You denied the Call Permission",Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(call);


            }
        });
        //设置点击button切换到联系人
        holder.button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Toast.makeText(context, logs.get(position).getContactName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(),ContactDetailActivity.class);
                String phoneNum= "TEL:"+logs.get(position).getNumber();
                intent.putExtra("index",phoneNum);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LogObject log = logs.get(position);
        Date date1 = new Date(log.getDate());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.ERA_FIELD, DateFormat.SHORT);
        holder.phone.setText(log.getContactName());
        holder.duration.setText(log.getCoolDuration());
        holder.date.setText(dateFormat.format(date1));

        String countText = "";
        //countText= countText+ "("+String.valueOf(CountList.size())+")";

        if(CountList.get(position)>1){
            countText = countText+ "("+String.valueOf(CountList.get(position))+")";
        }
        holder.count.setText(countText);
        switch (log.getType()) {
            //改变通话记录里的图标
            case LogsManager.INCOMING:
                holder.imageView.setImageResource(R.drawable.received);
                break;
            case LogsManager.OUTGOING:
                holder.imageView.setImageResource(R.drawable.sent);
                break;
            case LogsManager.MISSED:
                holder.imageView.setImageResource(R.drawable.missed);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.cancelled);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }
    //判断日期是否相同
    public boolean CompareDate(Date date1, Date date2){
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        if(ymd.format(date1).equals(ymd.format(date2))  ) {
            return true;
        }
        return false;
    }
    //聚合Logs列表
    public void LogsAggregation(List<LogObject> callLogs){
        int tmpCount=1;
        for(int i = 0; i < callLogs.size(); i++){
            if(i < callLogs.size()-1) {
                Date date1 = new Date(callLogs.get(i).getDate());
                Date date2 = new Date(callLogs.get(i + 1).getDate());
                if (callLogs.get(i).getContactName().equals(callLogs.get(i + 1).getContactName()))
                    if (CompareDate(date1, date2)) {
                        tmpCount++;
                        continue;
                    }
            }
            logs.add(callLogs.get(i));
            CountList.add(tmpCount);
            tmpCount = 1;
        }
        Collections.reverse(logs);
        Collections.reverse(CountList);

    }
}

