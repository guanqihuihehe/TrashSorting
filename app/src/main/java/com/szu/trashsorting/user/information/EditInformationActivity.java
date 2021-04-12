package com.szu.trashsorting.user.information;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.database.UserDBHelper;
import com.szu.trashsorting.R;

import static com.szu.trashsorting.common.Constant.currentUserCollege;
import static com.szu.trashsorting.common.Constant.currentUserGrade;
import static com.szu.trashsorting.common.Constant.currentUserName;
import static com.szu.trashsorting.common.Constant.currentUserNickname;
import static com.szu.trashsorting.common.Constant.currentUserPassword;
import static com.szu.trashsorting.common.Constant.currentUserStuNo;

public class EditInformationActivity extends BaseActivity {

    public ImageView goBack;
    public EditText editContent;
    public TextView changeTitle;
    public Button buttonConfirm, buttonCancel;
    public SQLiteDatabase userDB;
    int type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmydata);
        goBack =findViewById(R.id.goback);
        editContent =findViewById(R.id.edit_content);
        buttonConfirm =findViewById(R.id.make_confirm);
        buttonCancel =findViewById(R.id.make_cancle);
        changeTitle =findViewById(R.id.change_title);
        UserDBHelper userDBHelper = new UserDBHelper(this, "user.db", null, 2);
        userDB = userDBHelper.getWritableDatabase();
        //获取从上个页面传来的类型并显示本页
        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);
        switch (type){
            case 1:
                changeTitle.setText("修改昵称");
                break;
            case 2:
                changeTitle.setText("修改密码");
                break;
            case 3:
                changeTitle.setText("修改学院");
                break;
            case 4:
                changeTitle.setText("修改年级");
                break;
            case 5:
                changeTitle.setText("修改学号");
                break;
        }

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content= editContent.getText().toString();
                ContentValues values = new ContentValues();
                //修改条件
                String whereClause = "name=?";
                //修改添加参数
                String[] whereArgs={currentUserName};
                if(!content.equals("")){
                    switch (type){
                        case 1:
                            values.put("nickname",content);
                            currentUserNickname =content;
                            break;
                        case 2:
                            values.put("password",content);
                            currentUserPassword =content;
                            break;
                        case 3:
                            values.put("college",content);
                            currentUserCollege =content;
                            break;
                        case 4:
                            values.put("grade",content);
                            currentUserGrade =content;
                            break;
                        case 5:
                            values.put("stu_no",content);
                            currentUserStuNo =content;
                            break;

                    }
                    userDB.update("User",values,whereClause,whereArgs);
                }
                else {
                    Toast.makeText(EditInformationActivity.this, "修改值不能为空", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

}
