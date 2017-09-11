package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetUserMsgResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.widget.CollapsibleTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class MyLeaveMessageAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<List<GetUserMsgResponse.DataList>> lists;
    //    private List<List<String>> lists=new ArrayList<List<String>>();
    Handler handler;

    public MyLeaveMessageAdapter(Context context, List<List<GetUserMsgResponse.DataList>> lists) {
        this.context = context;
        this.lists = lists;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }

    public void refresh() {
        handler.sendMessage(new Message());
    }

    @Override
    public int getGroupCount() {
        return lists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lists.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lists.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.leave_message_item, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_time = (TextView) convertView
                    .findViewById(R.id.tv_time);
            groupHolder.tv_title_name = (TextView) convertView
                    .findViewById(R.id.tv_title_name);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.tv_title_name.setText(lists.get(groupPosition).get(0).msg_title);
        groupHolder.tv_time.setText(DateUtils.milliToSimpleDateTime(lists.get(groupPosition).get(0).msg_time*1000));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ItemHolder itemHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.expand_leave_message_item, null);
            itemHolder = new ItemHolder();
            itemHolder.tv_msg_content=(TextView)convertView.findViewById(R.id.tv_msg_content);
            itemHolder.tv_reply_content=(TextView)convertView.findViewById(R.id.tv_reply_content);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.tv_msg_content.setText(lists.get(groupPosition).get(childPosition).msg_content);
        itemHolder.tv_reply_content.setText(lists.get(groupPosition).get(childPosition).reply_content);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        TextView tv_title_name;
        TextView tv_time;
    }

    class ItemHolder {
        TextView tv_msg_content;
        TextView tv_reply_content;
    }
}


