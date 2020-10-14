package com.proj.androiddemonodejs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.proj.androiddemonodejs.retrofit.INodeJS;
import com.proj.androiddemonodejs.retrofit.RetrofitClient;
import com.rengwuxian.materialedittext.MaterialEditText;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    MaterialEditText edt_email,edt_password;
    Button btn_login,btn_register;
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit=RetrofitClient.getInstance();
        myAPI=retrofit.create(INodeJS.class);
        btn_login=findViewById(R.id.login_button);
        btn_register=findViewById(R.id.register_button);
        edt_email=findViewById(R.id.edt_email);
        edt_password=findViewById(R.id.edt_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(edt_email.getText().toString(),edt_password.getText().toString());
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(edt_email.getText().toString(),edt_password.getText().toString());
            }
        });
    }

    private void registerUser(String email, String password) {
        View enter_name_view= LayoutInflater.from(this).inflate(R.layout.enter_name_layout,null);
        new MaterialStyledDialog.Builder(this)
                .setTitle("Register")
                .setDescription("One more step!")
                .setCustomView(enter_name_view)
                .setIcon(R.drawable.ic_user)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Register")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialEditText edt_name=enter_name_view.findViewById(R.id.edt_name);
                        compositeDisposable.add(myAPI.registerUser(email,edt_name.getText().toString(),password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s){
                                        Toast.makeText(MainActivity.this,""+s,Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }
                }).show();
    }

    private void loginUser(String email, String password) {
        compositeDisposable.add(myAPI.loginUser(email,password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s){
                if(s.contains("encrypted_password")){
                    Toast.makeText(MainActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,""+s,Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }
}