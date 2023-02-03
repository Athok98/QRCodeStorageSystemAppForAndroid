package com.example.qrappv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity{

    TextView textView;
    EditText editText;
    Button scanBtn;
    Button addBoxBtn;
    Button removeBoxBtn;
    Button addItemBtn;
    Button removeItemBtn;
    Button returnMainBtn;

    Button addItemActivityBtn;

    MyDataBase DB;
    ImageView imageView;
    String nameDB;
    Bitmap imageDB;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan();
        buttonAddBox();
        buttonRemoveBox();
        buttonAddItem();
        buttonRemoveItem();

        DB = new MyDataBase(this);
    }

    public void buttonScan(){
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }
    public void buttonAddBox(){
        addBoxBtn = findViewById(R.id.addBoxBtn);
        addBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBox();
            }
        });
    }
    public void buttonRemoveBox(){
        removeBoxBtn = findViewById(R.id.removeBoxBtn);
        removeBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBox();
            }
        });
    }
    public void buttonAddItem(){
        addItemBtn = findViewById(R.id.addItemBtn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }
    public void buttonRemoveItem(){
        removeItemBtn = findViewById(R.id.removeItemBtn);
        removeItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem();
            }
        });
    }
    public void buttonReturnToMain(){
        returnMainBtn = findViewById(R.id.returnMainBtn);
        returnMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMain();
            }
        });
    }

    public void returnToMain(){
        setContentView(R.layout.activity_main);
        buttonScan();
        buttonAddBox();
        buttonRemoveBox();
        buttonAddItem();
        buttonRemoveItem();
    }

    public void AddItemActivityBtn(){
        addItemActivityBtn = findViewById(R.id.AddItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView = (TextView) findViewById(R.id.textview);
                editText = (EditText) findViewById(R.id.editText);
                imageView = (ImageView) findViewById(R.id.image);

                String name = editText.getText().toString();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.draw);
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                byte[] img = byteArray.toByteArray();

                boolean insert = DB.insertdata(name, img);
                if(insert==true){
                    Toast.makeText(MainActivity.this, "DataSaved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Not Saved", Toast.LENGTH_SHORT).show();
                }

                imageDB = DB.getImage(name);
                nameDB = DB.getName(name);

                imageView.setImageBitmap(imageDB);

            }
        });
    }

    private void addBox(){
        setContentView(R.layout.add_box);

        buttonReturnToMain();
    }

    private void removeBox(){
        setContentView(R.layout.remove_box);
        buttonReturnToMain();
    }

    private void addItem(){

        setContentView(R.layout.add_item);
        AddItemActivityBtn();
        buttonReturnToMain();
    }
    private void removeItem(){
        setContentView(R.layout.remove_item);
        buttonReturnToMain();
    }

    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
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
            }
            else {
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}