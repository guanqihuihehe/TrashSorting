package com.szu.trashsorting.user.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szu.trashsorting.R;
import com.szu.trashsorting.common.database.SearchRecordDBHelper;
import com.szu.trashsorting.user.register.RegisterActivity;
import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.MainActivity;
import com.szu.trashsorting.common.database.UserDBHelper;

import static com.szu.trashsorting.common.Constant.currentUserName;
import static com.szu.trashsorting.common.Constant.currentUserPassword;

public class LoginActivity extends BaseActivity {
    
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private TextView jumpToRegister;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private UserDBHelper userDBHelper;
    private SearchRecordDBHelper searchRecordDBHelper;
    private SQLiteDatabase db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LinearLayout linearLayout=findViewById(R.id.bg_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        jumpToRegister=findViewById(R.id.jumpToRegister);

        userDBHelper = new UserDBHelper(this, "user.db", null, 2);
        searchRecordDBHelper=new SearchRecordDBHelper(this,"record.db",null,2);
        SharedPreferences current_user=this.getSharedPreferences("current_user",MODE_MULTI_PROCESS);
        String alreadyLoginUser=current_user.getString("name","");
        String alreadyLoginPassword=current_user.getString("password","");


        Intent loginIntent=getIntent();
        String myName=loginIntent.getStringExtra("name");
        String myPassword=loginIntent.getStringExtra("password");
        if (myName!=null) {
            accountEdit.setText(myName);
            passwordEdit.setText(myPassword);
        }

        if(alreadyLoginUser!=null&&!alreadyLoginUser.equals("")){
            currentUserName =alreadyLoginUser;
            currentUserPassword =alreadyLoginPassword;
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        jumpToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                int flag1=0,flag2=0;
                db = userDBHelper.getWritableDatabase();
                Cursor cursor = db.query("User", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        // 遍历Cursor对象，取出数据并打印
                        String checkName = cursor.getString(cursor.getColumnIndex("name"));
                        String checkPassword = cursor.getString(cursor.getColumnIndex("password"));
                        if(checkName.equals(account))
                        {
                            flag1=1;
                            if(checkPassword.equals(password))
                            {
                                flag2=1;
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                currentUserName =account;
                                currentUserPassword =password;
                                SharedPreferences current_user=getSharedPreferences("current_user",  MODE_MULTI_PROCESS);
                                SharedPreferences.Editor  editor = current_user.edit();
                                editor.clear();
                                editor.putString("name",account);
                                editor.putString("password",password);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                cursor.close();
                                finish();
                                return;
                            }
                            break;
                        }
                    } while (cursor.moveToNext());
                }

                if(flag1==0)
                {
                    Toast.makeText(LoginActivity.this, "此账号尚未注册", Toast.LENGTH_SHORT).show();
                }
                else if(flag2==0)
                {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




}

