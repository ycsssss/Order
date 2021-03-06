package com.ycs.order.shopActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ManagerShopActivity extends AppCompatActivity {
    private MyUser user;
    private TextView shopname;
    private TextView shopaddress;
    private ImageView icon;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private  File output;
    private File iconfile;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_shop);
        getSupportActionBar().hide();
        user=MyUser.getCurrentUser(MyUser.class);
        shopname = findViewById(R.id.infoNickname);
        shopaddress = findViewById(R.id.shopAddress);
        icon = findViewById(R.id.pic);
        RelativeLayout shopnamelayout=(RelativeLayout) findViewById(R.id.name_layout);
        RelativeLayout addresslayout=findViewById(R.id.shop_address);
        RelativeLayout iconlayout=findViewById(R.id.icon);
        ImageView fanhui=findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ManagerShopActivity.this, MainBusinessActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        find();

        shopnamelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog =new AlertDialog.Builder(ManagerShopActivity.this).create();
                final EditText et=new EditText(ManagerShopActivity.this);
                dialog.setView(et);
                dialog.setTitle("修改店名");
                dialog.setMessage("请输入新的店铺名：");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String text = et.getText().toString();
                        if (!("".equals(text))) {
                            LoadingUtil.Loading_show(ManagerShopActivity.this);
                            Store s=new Store();
                            s.setS_name(text);
                            s.update(user.getBusinessid(), new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(ManagerShopActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                        LoadingUtil.Loading_close();
                                        shopname.setText(text);
                                    }else{
                                        LoadingUtil.Loading_close();
                                        Toast.makeText(ManagerShopActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }


                            });
                        }else{

                            Toast.makeText(ManagerShopActivity.this,"店名为空，修改昵称未成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

            }
        });
        shopaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog =new AlertDialog.Builder(ManagerShopActivity.this).create();
                final EditText et=new EditText(ManagerShopActivity.this);
                dialog.setView(et);
                dialog.setTitle("修改店铺地址");
                dialog.setMessage("请输入新店铺地址：");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String text = et.getText().toString();
                        if (!("".equals(text))) {
                            LoadingUtil.Loading_show(ManagerShopActivity.this);
                            Store s=new Store();
                            s.setShop_address(text);
                            s.update(user.getBusinessid(), new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(ManagerShopActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                        LoadingUtil.Loading_close();
                                        shopaddress.setText(text);
                                    }else{
                                        LoadingUtil.Loading_close();
                                        Toast.makeText(ManagerShopActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }


                            });
                        }else{
                            Toast.makeText(ManagerShopActivity.this,"店铺地址为空，修改昵称未成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        iconlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items3 = new String[]{"拍照获取图片",  "相册导入图片"};//创建item
                AlertDialog alertDialog3 = new AlertDialog.Builder(ManagerShopActivity.this)
                        .setTitle("更改头像")
                        .setItems(items3, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    takePhone();
                                }else{
                                    choosePhone();
                                }

                            }
                        })
                        .create();
                alertDialog3.show();
            }
        });

        TextView logout =findViewById(R.id.Logoutshop);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog=new AlertDialog.Builder(ManagerShopActivity.this).create();
                dialog.setTitle("注销店铺");
                dialog.setMessage("确认注销店铺？注销后店铺数据将被清除");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadingUtil.Loading_show(ManagerShopActivity.this);
                        Store s=new Store();
                        s.setObjectId(user.getBusinessid());
                        s.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    MyUser u=new MyUser();
                                    u.setBusinessid("");
                                    u.setIcon(null);
                                    u.update(user.getObjectId(),new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(ManagerShopActivity.this,"您的店铺已注销",Toast.LENGTH_SHORT);
                                                File file=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                                                try {
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                                Intent intent=new Intent(ManagerShopActivity.this,MainBusinessActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                LoadingUtil.Loading_close();

                                            }else{
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(ManagerShopActivity.this,"注销失败"+e.getMessage(),Toast.LENGTH_SHORT);
                                            }

                                        }
                                    });

                                }else{
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(ManagerShopActivity.this,"注销失败"+e.getMessage(),Toast.LENGTH_SHORT);
                                }
                            }
                        });

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

            }
        });
    }
    public  void find() {
        File iconfile;
        BmobQuery<Store> s = new BmobQuery<>();
        s.getObject(user.getBusinessid(), new QueryListener<Store>() {
            @Override
            public void done(Store store, BmobException e) {
                if (e == null) {
                    shopname.setText(store.getS_name());
                    shopaddress.setText(store.getShop_address());
                } else {
                    shopname.setText("无法查询到店铺名");
                    shopaddress.setText("无法查询到店铺地址");
                }
            }
        });
        iconfile = new File(Environment.getExternalStorageDirectory() + "/userImage.jpg");
        if (iconfile != null) {
            Uri i = Uri.fromFile(iconfile);
            try {
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(i));
                icon.setImageBitmap(b);
            } catch (FileNotFoundException ex) {
                Log.e("获取头像失败", ex.getMessage());
            }
        }
    }

    public void takePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

        }else {
            try{
                takePhoto();
            }catch (Exception e){
                Log.e("错误提示",e.getMessage());
            }

        }

    }

    public void choosePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        }else {
            choosePhoto();
        }
    }
    void takePhoto(){
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file=new File(Environment.getExternalStorageDirectory(),"拍照");
        if(!file.exists()){
            file.mkdir();
        }
        output=new File(file,System.currentTimeMillis()+".jpg");
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
            imageUri = Uri.fromFile(output);
        }else{
            imageUri=FileProvider.getUriForFile(ManagerShopActivity.this, "com.ycs.order.provider", output);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }

    void choosePhoto(){
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            /**
             * 拍照的请求标志
             */
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    try {

//                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        final BmobFile file;
                        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
                            file = new BmobFile(uri2File(imageUri));
                        }else{
                            file = new BmobFile(new File(getFilePathForN(ManagerShopActivity.this,imageUri)));
                        }
                        file.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    user.setIcon(file);
                                    user.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                BmobFile iconn = user.getIcon();
                                                iconn.download(new File(Environment.getExternalStorageDirectory() + "/userImage.jpg"), new DownloadFileListener() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if (e == null) {
                                                            icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                        } else {
                                                            Log.e("获取头像失败", e.getMessage());
                                                        }
                                                    }

                                                    @Override
                                                    public void onProgress(Integer integer, long l) {

                                                    }
                                                });
                                                Toast.makeText(ManagerShopActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ManagerShopActivity.this, "更新失败1" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e("提示", e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ManagerShopActivity.this, "更新失败2" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("提示", e.getMessage());
                                }
                            }
                        });

                    } catch (Exception ex) {

                        Log.d("tag", ex.getMessage());
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    try {
                        /**
                         * 该uri是上一个Activity返回的
                         */
                        Uri uri = data.getData();
                        final Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        final BmobFile file=new BmobFile(uri2File(uri));
                        file.upload(new UploadFileListener(){
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    MyUser userr=new MyUser();
                                    userr.setIcon(file);
                                    userr.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                icon.setImageBitmap(bit);
                                                BmobFile iconn=user.getIcon();
                                                File f=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                                                if(f.exists()){
                                                    try{
                                                        f.delete();
                                                    }catch (Exception ee){

                                                    }
                                                }
                                                iconn.download(new File(Environment.getExternalStorageDirectory()+"/userImage.jpg"),new DownloadFileListener() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if(e == null){
                                                            Toast.makeText(ManagerShopActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Log.e("获取头像失败",e.getMessage());
                                                        }
                                                    }
                                                    @Override
                                                    public void onProgress(Integer integer, long l) {

                                                    }
                                                });

                                            }else{
                                                Toast.makeText(ManagerShopActivity.this,"更新失败1"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                Log.e("提示",e.getMessage());
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(ManagerShopActivity.this, "更新失败2"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("提示",e.getMessage());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("tag",e.getMessage());
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("提示", "失败");
                }

                break;

            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePhoto();
            } else
            {
                Toast.makeText(ManagerShopActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(ManagerShopActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }
    private static String getFilePathForN(Context context, Uri uri) {
        try {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            File file = new File(context.getFilesDir(), name);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            returnCursor.close();
            inputStream.close();
            outputStream.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
