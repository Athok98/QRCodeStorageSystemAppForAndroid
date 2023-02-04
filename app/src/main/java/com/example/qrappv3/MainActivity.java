package com.example.qrappv3;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1234;
    private static final int CAPTURE_CODE = 1001;
    private int cameraMode = 0; //mode that a camera is on: 0-beginning; 1-scan code; 2-take picture; 3 - scan box
    private boolean ifSearchBoxFlag = false;
    TextView textView, textViewQRCode, textFoundBox;

    EditText editText, editTextAddBox;
    Button scanBtn, addBoxBtn, removeBoxBtn, addItemBtn, removeItemBtn, returnMainBtn, addItemActivityBtn, takePictureBtn, addBoxBtnInternal, scanCodeBoxBtn;
    MyDataBase DB = new MyDataBase(this);

    ImageView imageView;
    String nameDB, qr_id;
    Bitmap imageDB;

    boolean ifAddBox, ifAddItem;
    String scanQrResult = "Your QR code";

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ifAddBox = false;
        ifAddItem = false;
        ifSearchBoxFlag = false;
        setContentView(R.layout.activity_main);
        buttonScan();
        buttonAddBox();
        buttonRemoveBox();
        buttonAddItem();
        buttonRemoveItem();

        //DB = new MyDataBase(this);
    }

    public void buttonScan() {
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            searchBox();
            }
        });
    }

    public void buttonAddBox() {
        addBoxBtn = findViewById(R.id.addBoxBtn);
        addBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBox();
            }
        });
    }

    public void buttonRemoveBox() {
        removeBoxBtn = findViewById(R.id.removeBoxBtn);
        removeBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBox();
            }
        });
    }

    public void buttonAddItem() {
        addItemBtn = findViewById(R.id.addItemBtn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    public void scanCodeBoxBtn() {
        scanCodeBoxBtn = findViewById(R.id.scanCodeBox);
        scanCodeBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }

    public void buttonRemoveItem() {
        removeItemBtn = findViewById(R.id.removeItemBtn);
        removeItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem();
            }
        });
    }

    public void buttonReturnToMain() {
        returnMainBtn = findViewById(R.id.returnMainBtn);
        returnMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMain();
            }
        });
    }
    public void returnToMain() {
        cameraMode = 0;
        ifAddBox = false;
        ifAddItem = false;
        ifSearchBoxFlag = false;
        setContentView(R.layout.activity_main);
        buttonScan();
        buttonAddBox();
        buttonRemoveBox();
        buttonAddItem();
        buttonRemoveItem();
    }
    public void buttonTakePicture() {
        takePictureBtn = findViewById(R.id.takePictureBtn);
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = (ImageView) findViewById(R.id.imageView);
                //addItemActivityBtn = Button <-
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
    }

    public void addBoxActivityBtn(){
        editTextAddBox = (EditText) findViewById(R.id.editTextAddBox);
        addBoxBtnInternal = findViewById(R.id.addBoxBtnInternal);
        addBoxBtnInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editText = editTextAddBox.getText().toString();
                String qrText = textViewQRCode.getText().toString();

                boolean insert = DB.insertDataBox(editText, qrText);
                if (insert == true) {
                    Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Data Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void addItemActivityBtn() {
        addItemActivityBtn = findViewById(R.id.AddItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                textView = (TextView) findViewById(R.id.textview);
                editText = (EditText) findViewById(R.id.editText);
                imageView = (ImageView) findViewById(R.id.imageView);

                String name = editText.getText().toString();
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache(); //BitmapFactory.decodeResource(getResources(), R.drawable.draw);
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                byte[] img = byteArray.toByteArray();

                boolean insert = DB.insertdata(name, img, qr_id);

                if (insert == true) {
                    Toast.makeText(MainActivity.this, "DataSaved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Data Not Saved", Toast.LENGTH_SHORT).show();
                }
                imageDB = DB.getImage(qr_id);
                nameDB = DB.getName(qr_id);
                imageView.setImageBitmap(imageDB);
            }
        });
    }
    private void searchBox() {
        ifAddBox= false;
        ifSearchBoxFlag = true;
        cameraMode = 3;

        setContentView(R.layout.search_box);
        textFoundBox = (TextView) findViewById(R.id.textFoundBox);
        scanCode();
        buttonReturnToMain();

    }
    private void setTextViewQRCode(){
        textViewQRCode = (TextView) findViewById(R.id.textViewQRCode);
        textViewQRCode.setText(scanQrResult);
    }
    private void addBox() {
        ifAddBox = true;
        cameraMode = 1;
        scanQrResult = "You QR code";

        setContentView(R.layout.add_box);

        addBoxActivityBtn();
        scanCodeBoxBtn();
        buttonReturnToMain();
        setTextViewQRCode();
    }
    private void removeBox() {
        setContentView(R.layout.remove_box);
        buttonReturnToMain();
    }
    private void addItem() {
        cameraMode = 2;
        ifAddItem = true;

        setContentView(R.layout.add_item);
        scanCode();
        addItemActivityBtn();
        buttonTakePicture();
        buttonReturnToMain();
    }

    private void removeItem() {
        setContentView(R.layout.remove_item);
        buttonReturnToMain();
    }

    private void scanCode() {
        cameraMode = 1;

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        integrator.initiateScan();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camintent, CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && cameraMode == 2) {
            imageView.setImageURI(image_uri);
        }
        if (cameraMode == 1 || cameraMode == 3) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(result.getContents());
                    builder.setTitle("Scanning result:");
                    builder.setPositiveButton("Scan again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            scanCode();
                        }
                    }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    if (ifAddBox == true) {
                        textViewQRCode.setText(result.getContents().toString());
                    }
                    if (ifAddItem == true) {
                        qr_id = result.getContents().toString();
                    }
                    if (ifSearchBoxFlag == true) {
                        textFoundBox.setText(result.getContents().toString());
                    }
                } else {
                    Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}