package com.sample;

/*
 * Created by mohit on 27/04/18.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.sample.modals.ItemPOJO;
import com.sample.prefrence.AppPreferencesHelper;
import com.sample.utils.AlbumStorageDirFactory;
import com.sample.utils.AppConstants;
import com.sample.utils.BaseAlbumDirFactory;
import com.sample.utils.FroyoAlbumDirFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sample.utils.AppConstants.REQUEST_CAMERA;
import static com.sample.utils.AppConstants.REQUEST_GALLERY;
import static com.sample.utils.AppConstants.REQUEST_PERMISSION_CODE_CAMERA;
import static com.sample.utils.AppConstants.REQUEST_PERMISSION_CODE_STORAGE;

public class AddItemActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = AddItemActivity.class.getSimpleName();

    @BindView(R.id.edtName)
    EditText edtName;

    @BindView(R.id.edtDes)
    EditText edtDes;

    @BindView(R.id.edtLocation)
    EditText edtLocation;

    @BindView(R.id.edtCost)
    EditText edtCost;

    @BindView(R.id.imgView)
    ImageView imgView;

    private AppPreferencesHelper appPreferencesHelper;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private ArrayList<ItemPOJO> itemList;
    private String imagePath = "";

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);
        appPreferencesHelper = new AppPreferencesHelper(this, AppConstants.PREF_NAME);

        itemList = getItemList();
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    @OnClick(R.id.btnAdd)
    public void onAddClick() {
        if (edtName.getText().toString().equals("")) {
            showMessage(getString(R.string.enter_name));
            return;
        }

        if (edtDes.getText().toString().equals("")) {
            showMessage(getString(R.string.enter_des));
            return;
        }

        if (edtLocation.getText().toString().equals("")) {
            showMessage(getString(R.string.enter_location));
            return;
        }

        if (edtCost.getText().toString().equals("")) {
            showMessage(getString(R.string.enter_cost));
            return;
        }

        if (imagePath.equals("")) {
            showMessage(getString(R.string.select_image));
            return;
        }

        Random random = new Random();
        Integer id = random.nextInt(100);
        Log.e("id", "id>>" + id);
        ItemPOJO itemPOJO = new ItemPOJO();
        itemPOJO.setId(id);
        itemPOJO.setName(edtName.getText().toString());
        itemPOJO.setDescription(edtDes.getText().toString());
        itemPOJO.setLocation(edtLocation.getText().toString());
        itemPOJO.setCost(edtCost.getText().toString());
        itemPOJO.setImage(imagePath);
        itemList.add(itemPOJO);
        setItemList(itemList);
        showMessage(getString(R.string.item_added));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);
    }

    @OnClick(R.id.imgView)
    public void onImageClick() {
        showDialog();
    }

    // Show Dialog For Select Image
    private void showDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_GALLERY);
        }
    }

    public void cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_CAMERA);
        } else {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                showMessage(getString(R.string.camera_not_support));
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = null;
            try {
                file = new File(setUpPhotoFile().getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        imagePath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String JPEG_FILE_PREFIX = "IMG_";
        String imageFileName = JPEG_FILE_PREFIX + "_" + System.currentTimeMillis();
        File albumF = getAlbumDir();
        String JPEG_FILE_SUFFIX = ".jpg";
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }

    @Nullable
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            showMessage("External storage is not mounted READ/WRITE.");
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public String getAlbumName() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY:
                    final Uri uri = data.getData();
                    if (uri != null) {
                        imgView.setImageResource(0);
                        imagePath = FileUtils.getPath(this, uri);
                        Glide.with(this)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(imgView);
                    }
                    break;

                case REQUEST_CAMERA:
                    imgView.setImageResource(0);
                    Glide.with(this)
                            .load(imagePath)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(imgView);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    Log.e(TAG, "Storage Permission Not Granted");
                    showMessage(getString(R.string.allow_storage_permission));
                }

                break;
            }

            case REQUEST_PERMISSION_CODE_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    Log.e(TAG, "Storage Permission Not Granted");
                    showMessage(getString(R.string.allow_storage_permission));
                }

                break;
            }
        }
    }

    public ArrayList<ItemPOJO> getItemList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ItemPOJO>>() {
        }.getType();
        return gson.fromJson(appPreferencesHelper.getItemListResponse(), type);
    }

    public void setItemList(ArrayList<ItemPOJO> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ItemPOJO>>() {
        }.getType();
        String json = gson.toJson(list, type);
        this.appPreferencesHelper.setItemListResponse(json);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}