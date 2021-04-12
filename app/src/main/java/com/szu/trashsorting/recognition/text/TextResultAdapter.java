package com.szu.trashsorting.recognition.text;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trashsorting.R;

import java.util.List;


public class TextResultAdapter extends BaseAdapter {

    public List<TextResultEntity> functions;
    public TextResultAdapter(List<TextResultEntity> functions){
        this.functions = functions;
    }
    public int id;
    public String typeName=null;

    @Override
    public int getCount() {
        return functions.size();
    }

    @Override
    public Object getItem(int position) {
        return functions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder= null;
        if(convertView==null)
        {
            viewHolder= new ViewHolder();
            convertView=View.inflate(parent.getContext(),R.layout.fragment_item,null);
            viewHolder.resultName = convertView.findViewById(R.id.result_name);
            viewHolder.resultType = convertView.findViewById(R.id.result_type);
            viewHolder.resultTypeIcon = convertView.findViewById(R.id.result_type_icon);
            convertView.setTag(viewHolder);//viewholder里面的数据放入converview
        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        TextResultEntity result_item=functions.get(position);

        if(result_item.getType()==0){
            id=R.mipmap.recyclables_item;
            typeName="可回收垃圾";
        }
        if(result_item.getType()==1){
            id = R.mipmap.hazardouswaste_item;
            typeName="有害垃圾";
        }
        if(result_item.getType()==2){
            id = R.mipmap.wetgarbage_item;
            typeName="湿垃圾";
        }
        if(result_item.getType()==3){
            id = R.mipmap.dryrefuse_item;
            typeName="干垃圾";
        }

        viewHolder.resultName.setText(result_item.getName());
        viewHolder.resultTypeIcon.setImageResource(id);
        viewHolder.resultType.setText(String.valueOf(typeName));
//        viewHolder.resultexplain.setText("\n分类解释:"+result_item.getExplain()+"\n");
//        viewHolder.resulttip.setText("投放提示："+result_item.getTip()+"\n");
        return convertView;
    }

     static class ViewHolder{
        TextView resultName;
        ImageView resultTypeIcon;
        TextView resultType;
    }

}
