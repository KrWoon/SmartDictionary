package com.explain.ListViewUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.explain.R;

import java.util.ArrayList;

/**
 * Created by 박지운 on 2018-10-24.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<ListData> mListData = new ArrayList<ListData>();

    public ListViewAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(String mWord, String mTitle, String mDate, String mLink) {
        ListData addInfo = null;
        addInfo = new ListData();
        addInfo.mWord = mWord;
        addInfo.mTitle = mTitle;
        addInfo.mDate = mDate;
        addInfo.mLink = mLink;

        mListData.add(addInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.left_row, null);

            holder.mWord = (TextView) convertView.findViewById(R.id.mWord);
            holder.mText = (TextView) convertView.findViewById(R.id.mText);
            holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
            holder.mButton = (Button) convertView.findViewById(R.id.mButton);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final ListData mData = mListData.get(position);

        holder.mWord.setText(mData.mWord);
        holder.mText.setText(mData.mTitle);
        holder.mDate.setText(mData.mDate);

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.mLink));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
