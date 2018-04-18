package com.leaveme.notebook;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.leaveme.notebook.ViewAdapter.NoteAdapter;
import com.leaveme.notebook.ViewAdapter.OnStartDragListener;
import com.leaveme.notebook.ViewAdapter.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    private NoteAdapter noteAdapter;
    private RecyclerView list;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private ItemTouchHelper mItemTouchHelper;

    FingerprintManager manager;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;

    private ImageDialog imageDialog;
    private int position;

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
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,NoteActivity.class);
                startActivity(intent);
            }
        });

        noteAdapter = new NoteAdapter(MainActivity.this,this);
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (isFinger()) {
                    imageDialog = new ImageDialog(MainActivity.this,R.style.dialog,"请进行指纹验证").
                            setTitle("请进行指纹验证");
                    imageDialog.setCanceledOnTouchOutside(false);
                    imageDialog.setCancelable(false);
                    imageDialog.show();
                    startListening(null);
                }
            }
        });
        list = (RecyclerView)findViewById(R.id.rv_main);
        list.setHasFixedSize(false);
        list.setAdapter(noteAdapter);
        LinearLayoutManager l = new LinearLayoutManager(this);
        list.setLayoutManager(l);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(noteAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(list);

        manager = (FingerprintManager)this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager)this.getSystemService(Context.KEYGUARD_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("onQueryTextSubmit",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("onQueryTextChange",newText);
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(MainActivity.this, "onMenuItemActionExpand方法执行了...", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(MainActivity.this, "onMenuItemActionCollapse方法执行了...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
        if (!addPermission(permissionsList, Manifest.permission.USE_FINGERPRINT))
            permissionsNeeded.add("\n\r使用指纹识别");
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
                //请求成功
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else{
//                    //请求失败，提示用户“请求权限失败
                    Log.e("TAG","请求权限失败");
//                    Toast.makeText(this,"请求权限失败，请手动设置",Toast.LENGTH_LONG).show();
//                    this.finish();
                }
            }break;
        }
    }


    public boolean isFinger() {
        //用来检查是否有指纹识别权限
        if(checkCallingOrSelfPermission(Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED) {
            if (!manager.isHardwareDetected()) {
                Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!mKeyManager.isKeyguardSecure()) {
                Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!manager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            if (imageDialog!=null){
                imageDialog.dismiss();
                imageDialog = null;
            }
            Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();
            showAuthenticationScreen();
        }
        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            if (imageDialog!=null){
                imageDialog.dismiss();
                imageDialog = null;
            }
            noteAdapter.openNote();
        }
        @Override
        public void onAuthenticationFailed() {
            if (imageDialog!=null){
                imageDialog.dismiss();
                imageDialog = null;
            }
            Toast.makeText(MainActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
        }
    };
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            if (imageDialog!=null){
                imageDialog.dismiss();
                imageDialog = null;
            }
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);

    }

    private void showAuthenticationScreen() {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "指纹识别失败次数过多，请输入锁屏密码");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                noteAdapter.openNote();
            } else {
                Toast.makeText(this, "密码验证失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void Log(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
