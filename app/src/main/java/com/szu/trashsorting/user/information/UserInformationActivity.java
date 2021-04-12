package com.szu.trashsorting.user.information;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.util.RoundBitmapUtil;
import com.example.trashsorting.R;

import java.io.File;

import static com.szu.trashsorting.common.Constant.currentUserCollege;
import static com.szu.trashsorting.common.Constant.currentUserGrade;
import static com.szu.trashsorting.common.Constant.currentUserNickname;
import static com.szu.trashsorting.common.Constant.currentUserStuNo;
import static com.szu.trashsorting.common.Constant.currentIconFile;
import static com.szu.trashsorting.common.Constant.currentIconPath;

/**
 * 个人信息
 */
public class UserInformationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack, ivHead;
    private RelativeLayout rlHead;
    private LinearLayout rlName, rlPassword, rlCollege, rlGrade, rlStuNo;
    private TextView tvHead, tvName, tvGrade, tvCollege, tvStuNo, tvPassword;
    private Bitmap myIconBitmap;
    private final String TAG="UserInformationActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_me);
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
        if(myIconBitmap!=null){
            myIconBitmap.recycle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
    }

    public void refreshData(){
        if(currentIconFile !=null){
            File tempFile= new File(currentIconFile);
            if(tempFile!=null&&tempFile.list().length!=0){
                Bitmap bitmap = BitmapFactory.decodeFile(currentIconPath);
                myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
                bitmap.recycle();
                ivHead.setImageBitmap(myIconBitmap);
            }
            else{
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.smile);
//                Bitmap bitmap = bitmapDrawable.getBitmap();
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.smile);
                myIconBitmap=RoundBitmapUtil.toRoundBitmap(bitmap);
                bitmap.recycle();
                ivHead.setImageBitmap(myIconBitmap);
            }
        }
        else{
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.smile);
//            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.smile);
            myIconBitmap=RoundBitmapUtil.toRoundBitmap(bitmap);
            bitmap.recycle();
            ivHead.setImageBitmap(myIconBitmap);
        }

        if(currentUserNickname !=null){
            tvName.setText(currentUserNickname);
        }
        if(currentUserCollege !=null){
            tvCollege.setText(currentUserCollege);
        }
        if(currentUserGrade !=null){
            tvGrade.setText(currentUserGrade);
        }
        if(currentUserStuNo !=null){
            tvStuNo.setText(currentUserStuNo);
        }
    }

    public void initView() {
        ivBack = findViewById(R.id.iv_back);
        rlName = findViewById(R.id.rl_name);
        rlHead = findViewById(R.id.rl_head);
        rlGrade = findViewById(R.id.rl_grade);
        rlCollege = findViewById(R.id.rl_college);
        rlStuNo = findViewById(R.id.rl_stu_no);
        rlPassword = findViewById(R.id.rl_password);
        ivHead = findViewById(R.id.iv_head);
        tvName = findViewById(R.id.tv_name);
        tvGrade = findViewById(R.id.tv_grade);
        tvCollege = findViewById(R.id.tv_college);
        tvPassword = findViewById(R.id.tv_password);
        tvStuNo = findViewById(R.id.tv_stu_no);
        tvHead =findViewById(R.id.tv_head);

        ivBack.setOnClickListener(this);
        rlName.setOnClickListener(this);
        rlHead.setOnClickListener(this);
        rlPassword.setOnClickListener(this);
        rlCollege.setOnClickListener(this);
        rlGrade.setOnClickListener(this);
        rlStuNo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent jumpIntent;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_head:
                jumpIntent = new Intent(UserInformationActivity.this, ChangeIconActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.rl_name:
                jumpIntent = new Intent(UserInformationActivity.this, EditInformationActivity.class);
                jumpIntent.putExtra("type",1);
                startActivity(jumpIntent);
                break;
            case R.id.rl_password:
                jumpIntent = new Intent(UserInformationActivity.this, EditInformationActivity.class);
                jumpIntent.putExtra("type",2);
                startActivity(jumpIntent);
                break;
            case R.id.rl_college:
                jumpIntent = new Intent(UserInformationActivity.this, EditInformationActivity.class);
                jumpIntent.putExtra("type",3);
                startActivity(jumpIntent);
                break;
            case R.id.rl_grade:
                jumpIntent = new Intent(UserInformationActivity.this, EditInformationActivity.class);
                jumpIntent.putExtra("type",4);
                startActivity(jumpIntent);
                break;
            case R.id.rl_stu_no:
                jumpIntent = new Intent(UserInformationActivity.this, EditInformationActivity.class);
                jumpIntent.putExtra("type",5);
                startActivity(jumpIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}