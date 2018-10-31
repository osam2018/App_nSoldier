package kr.nbit.nsoldier.nsoldier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<SettingData> m_oData = null;
    private int nListCnt = 0;

    public ListAdapter(ArrayList<SettingData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        return nListCnt;
    }

    @Override
    public SettingData getItem(int position)
    {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.list_view, parent, false);
        }

        TextView oTextTitle = convertView.findViewById(R.id.textTitle);
        TextView oTextDate = convertView.findViewById(R.id.textDate);

        oTextTitle.setText(m_oData.get(position).key);
        oTextDate.setText(m_oData.get(position).value);
        return convertView;
    }
}