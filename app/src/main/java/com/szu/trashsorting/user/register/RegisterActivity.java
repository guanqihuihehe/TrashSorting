package com.szu.trashsorting.user.register;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.szu.trashsorting.R;
import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.database.SearchRecordDBHelper;
import com.szu.trashsorting.common.database.UserDBHelper;
import com.szu.trashsorting.user.login.LoginActivity;

public class RegisterActivity extends BaseActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText rePasswordEdit;
    private Button registerButton;
    public ImageView backToLogin;

    private UserDBHelper userDBHelper;
    private SearchRecordDBHelper searchRecordDBHelper;

    private String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        accountEdit = (EditText) findViewById(R.id.register_account);
        passwordEdit = (EditText) findViewById(R.id.register_password);
        rePasswordEdit = (EditText) findViewById(R.id.re_password);
        registerButton = (Button) findViewById(R.id.register);
        backToLogin=findViewById(R.id.backToLogin);
        userDBHelper = new UserDBHelper(this, "user.db", null, 2);
        searchRecordDBHelper=new SearchRecordDBHelper(this,"record.db",null,2);
        rePasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rePasswordEdit.setBackgroundResource(R.drawable.et_underline_selected);
                String password1 = passwordEdit.getText().toString();
                String password2 = rePasswordEdit.getText().toString();
                if(!password1.equals(password2)){
                    rePasswordEdit.setBackgroundResource(R.drawable.et_underline_selected);
                }

            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAccount();
            }
        });
    }

    public void checkAccount(){

        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String re_password= rePasswordEdit.getText().toString();

        //??????1 ??????????????????
        if(!re_password.equals(password)){
            Toast.makeText(RegisterActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        //??????2 ????????????
        if(!checkName(account)){
            Toast.makeText(RegisterActivity.this, "??????????????????6???10????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!checkPwd(password)){
            Toast.makeText(RegisterActivity.this, "???????????????6???20????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        //??????3 ???????????????
        SQLiteDatabase userDB = userDBHelper.getWritableDatabase();
        Cursor cursor = userDB.query("User", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // ??????Cursor??????????????????????????????
                String check_name = cursor.getString(cursor.getColumnIndex("name"));
                String check_password = cursor.getString(cursor.getColumnIndex("password"));
                if(check_name.equals(account))
                {
                    cursor.close();
                    Toast.makeText(RegisterActivity.this, "???????????? "+account+" ?????????", Toast.LENGTH_SHORT).show();
                    return;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        //????????????????????????????????????
        ContentValues userDBValues = new ContentValues();
        userDBValues.put("name",account);
        userDBValues.put("password",password);
        userDB.insert("User",null,userDBValues);

        //???????????????????????????
        SQLiteDatabase recordDB = searchRecordDBHelper.getWritableDatabase();
        ContentValues recordDBValues = new ContentValues();
        recordDBValues.put("name",account);
        recordDBValues.put("record1","");
        recordDBValues.put("record2","");
        recordDBValues.put("record3","");
        recordDBValues.put("record4","");
        recordDB.insert("SearchRecord",null,recordDBValues);

        Toast.makeText(RegisterActivity.this, "???????????????????????? "+account, Toast.LENGTH_SHORT).show();

        //????????????????????????????????????intent
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("name",account);
        intent.putExtra("password",password);
        startActivity(intent);
        finish();

    }

    /**
     * ??????????????? 6???10????????????????????????????????????????????????
     *  @param name
     *  @return
     */
    public static boolean checkName(String name) {
        String regExp = "^[\\w_]{6,9}$";
        return name.matches(regExp);
    }

    /**
     * ???????????? 6???20????????????????????????????????????????????????
     *  @param pwd
     *  @return
     */
    public static boolean checkPwd(String pwd) {
        String regExp = "^[\\w_]{6,20}$";
        return pwd.matches(regExp);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
