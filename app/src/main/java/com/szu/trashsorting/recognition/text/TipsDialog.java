package com.szu.trashsorting.recognition.text;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trashsorting.R;
import com.szu.trashsorting.category.viewpager.TrashTypeEntity;
import com.szu.trashsorting.category.viewpager.TrashTypeUtils;

public class TipsDialog extends Dialog{

    public int type;
    public ImageView imageView;
    public TextView explainTitle,explainContent,contain, tips;
    public TextResultEntity textResultEntity;

    public TipsDialog(Context context, TextResultEntity textResultEntity) {
        super(context, R.style.Theme_AppCompat_Dialog);
        this.textResultEntity = textResultEntity;
        type= textResultEntity.type;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tips);
        explainTitle = findViewById(R.id.tips_type);
        explainContent = findViewById(R.id.tips_explain);
        contain = findViewById(R.id.tips_contain);
        tips = findViewById(R.id.tips_tip);
        imageView = findViewById(R.id.tips_image);

        TrashTypeUtils trashTypeUtils=new TrashTypeUtils();
        TrashTypeEntity trashTypeEntity = trashTypeUtils.getTrash(type);
        imageView.setImageResource(trashTypeEntity.getPicture());
        String typeName=null;
        if(textResultEntity.getType()==0){
            typeName="可回收垃圾";
        }
        if(textResultEntity.getType()==1){
            typeName="有害垃圾";
        }
        if(textResultEntity.getType()==2){
            typeName="湿垃圾";
        }
        if(textResultEntity.getType()==3){
            typeName="干垃圾";
        }
        explainTitle.setText(typeName);
        explainContent.setText(textResultEntity.explain);
        contain.setText(textResultEntity.contain);
        tips.setText(textResultEntity.tip);
    }

}