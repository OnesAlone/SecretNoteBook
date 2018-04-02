package com.leaveme.notebook;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.leaveme.notebook.ViewAdapter.NoteAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter noteAdapter;
    private RecyclerView list;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Permissinit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,NoteActivity.class);
                startActivity(intent);
            }
        });

        noteAdapter = new NoteAdapter(this);
        list = (RecyclerView)findViewById(R.id.rv_main);
        list.setHasFixedSize(false);
        list.setAdapter(noteAdapter);
        LinearLayoutManager l = new LinearLayoutManager(this);
        list.setLayoutManager(l);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }

    //添加系统权限程序
    private boolean addPermission(List<String> permissionsList, String permission) {
        //判断该应用是否具备要请求的权限
        if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED) {
            //没有该权限，则加入到请求列表
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
                return false;
        }
        return true;
    }
    //创建内容显示对话框
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    //权限请求程序
    private void Permissinit(){
        //需要请求的权限请求字符串列表
        List<String> permissionsNeeded = new ArrayList<String>();
        //权限请求列表
        final List<String> permissionsList = new ArrayList<String>();
        //添加读写存储空间、读取手机状态、拨打电话、读取位置信息、读取精确位置信息这些权限到权限请求列表中
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("\n\r读存储空间");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("\n\r写存储空间");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("\n\r联网");
        //如果权限请求列表中的内容大于0个，则开始请求权限
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                //获取到第一个需要添加请求列表的权限
                String message = "你需要获取已下权限：" + permissionsNeeded.get(0);
                //循环将剩余需要请求的权限加入到请求列表
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            //开始向系统请求权限
            ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }else {
            //程序初始化、加载数据库
        }
    }
    //请求权限返回的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            //判断是否为该软件系统请求的权限信息
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                //判断是否请求成功
            {
                //请求成功，进入到程序初始化、架子按数据库
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else{
//                    //请求失败，提示用户“请求权限失败
//                    Log.e("TAG","请求权限失败");
//                    Toast.makeText(this,"请求权限失败，请手动设置",Toast.LENGTH_LONG).show();
//                    this.finish();
                }
            }break;
        }
    }


}
