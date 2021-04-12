package com.szu.trashsorting.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.szu.trashsorting.common.util.RoundBitmapUtil;
import com.example.trashsorting.R;
import com.szu.trashsorting.user.download.ShowMyDownloadActivity;
import com.szu.trashsorting.user.information.UserInformationActivity;
import com.szu.trashsorting.user.location.LocationActivity;

import java.io.File;

import static com.szu.trashsorting.common.Constant.currentCity;
import static com.szu.trashsorting.common.Constant.currentUserCollege;
import static com.szu.trashsorting.common.Constant.currentUserGrade;
import static com.szu.trashsorting.common.Constant.currentUserNickname;
import static com.szu.trashsorting.common.Constant.currentIconFile;
import static com.szu.trashsorting.common.Constant.currentIconPath;

public class UserManageFragment extends Fragment {

    private final String TAG="UserManageFragment";
    private ImageView imageView;
    private TextView nicknameView,location;
    private TextView college_grade;
    private Context mainContext;
    private Bitmap myIconBitmap;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG,"重新创建界面");

        View root = inflater.inflate(R.layout.fragment_mine, container, false);
        mainContext=getActivity();
        LinearLayout changeMyInfo = root.findViewById(R.id.change_my_info);
        LinearLayout myDownload = root.findViewById(R.id.mydownload);
        LinearLayout myLocation = root.findViewById(R.id.mylocation);

        changeMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, UserInformationActivity.class);
                mainContext.startActivity(intent);
            }
        });

        myDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, ShowMyDownloadActivity.class);
                mainContext.startActivity(intent);
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, LocationActivity.class);
                mainContext.startActivity(intent);
            }
        });

        final Button offline=root.findViewById(R.id.force_offline);

        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.trashsorting.FORCE_OFFLINE");
                LocalBroadcastManager.getInstance(mainContext).sendBroadcast(intent);
//                mainContext.sendBroadcast(intent);
            }
        });

        location=root.findViewById(R.id.location);
        imageView=root.findViewById(R.id.myicon);
        nicknameView =root.findViewById(R.id.mynickname);
        college_grade=root.findViewById(R.id.mycollege_grade);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!myIconBitmap.isRecycled()&&myIconBitmap!=null){
            myIconBitmap.recycle();
        }
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    public void refreshData(){
        if(currentIconFile !=null){
            File tempFile= new File(currentIconFile);
            if(tempFile.list()!=null){
                if(tempFile.list().length!=0){
                    Bitmap bitmap = BitmapFactory.decodeFile(currentIconPath);
                    myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
                    imageView.setImageBitmap(myIconBitmap);
                    bitmap.recycle();
                }
                else{
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.smile);
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.smile);
                    myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
                    imageView.setImageBitmap(myIconBitmap);
                    bitmap.recycle();
                }
            }
            else {
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.smile);
//                Bitmap bitmap = bitmapDrawable.getBitmap();
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.smile);
                myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
                imageView.setImageBitmap(myIconBitmap);
                bitmap.recycle();
            }
        }
        else{
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.smile);
//            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.smile);
            myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
            imageView.setImageBitmap(myIconBitmap);
            bitmap.recycle();
        }

        if(currentUserNickname !=null){
            nicknameView.setText(currentUserNickname);
        }
        if(currentUserCollege !=null&& currentUserGrade !=null){
            String res= currentUserCollege +"  "+ currentUserGrade;
            college_grade.setText(res);
        }
        location.setText(currentCity);

    }
}
