package com.example.szidonialaszlo.imagefromgalleryproject;

import android.app.Activity;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;

public class MainActivity extends Activity {
    private  static int RESULT_LOAD_IMAGE=1;
    private static int TAKE_PICTURE_REQUEST = 2;
    private  static int PICK_FILE_REQUEST_CODE=3;

    ImageView imageView;
    Button buttonLoadImage, buttonLoadFile, buttonTakePicture, buttonUpload;
    TextView filePathView;
    EditText pictureName;
    String nameOfPicture;
    private String selectedFilePath;
    private  ImageFragment imageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imgView);
       // filePathView = (TextView)findViewById(R.id.filePathTextView);
        pictureName = (EditText) findViewById(R.id.pictureName);

        buttonUpload = (Button) findViewById(R.id.uploadBtn);
        buttonTakePicture = (Button) findViewById(R.id.takePictureBtn);  //take photo
       // buttonLoadImage  = (Button) findViewById(R.id.buttonLoadPicutre);
        buttonLoadFile = (Button) findViewById(R.id.buttonLoadFile);

        imageFragment = new ImageFragment();
        /*buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent i  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Intent i  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                Intent i= new Intent();
                i.setType("images/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                System.out.println("ButtonLoadImage \n Dir="+Environment.getExternalStorageDirectory().getAbsolutePath()+"  ExternalContentUri= "+MediaStore.Images.Media.EXTERNAL_CONTENT_URI+"   InternalContentUri="+MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                //startActivityForResult(i,RESULT_LOAD_IMAGE);
                startActivityForResult(Intent.createChooser(i,"Choose picture..."),RESULT_LOAD_IMAGE);
            }
        });*/

        buttonLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose file to upload..."),PICK_FILE_REQUEST_CODE);


            }
        });

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                System.out.println("buttonTakePicture ---> TAKE_PICTURE_REQUEST="+TAKE_PICTURE_REQUEST);
                startActivityForResult(intent,TAKE_PICTURE_REQUEST);
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("-------------------------------Upload button------------------------------");
                nameOfPicture = pictureName.getText().toString();
                System.out.println(">>>>>>> NameOfPicture= "+nameOfPicture);
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera");

                String path=  "";
                try {
                    path = createImageFile(nameOfPicture,dir).getAbsolutePath();
                    System.out.println(">>>>>>> fc -> Path = "+path);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(path);
                    Uri contentUri = Uri.fromFile(f);
                    System.out.println(">>> Intent -> f="+f+"    Uri="+contentUri);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //getting back selected image details in main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            System.out.println("RESULT_LOAD_IMAGE == OK");
            System.out.println("Uri =>"+selectedImageUri);
            System.out.println("Path =>"+FileUtils.getPath(this,selectedImageUri));

            Bitmap selectedImage = null;

                try { //mukodik

                    final InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    System.out.println("-------------------------------------------------------->>>>>>>>>>>>>>>>>>>   EncodedImage");
                    //System.out.println("EncodedImage =="+encodedImage);

                } catch (IOException e) {
            }
            imageView.setImageBitmap(selectedImage);
            //image
        } else */if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && null != data && data.getData() != null) {
            //Uri selectedFile = data.getData();

            System.out.println("PICK_FILE_REQUEST_CODE = OK");
            Uri selectedFileUri =data.getData();
            String path = FileUtils.getPath(this,selectedFileUri);
              System.out.println("SelectedFile URI=" + selectedFileUri.toString()+ "  PATH="+path);


           File file = new File(path);
            System.out.println(" File absolutepath = "+ Environment.getExternalStorageDirectory().getAbsolutePath()+"     file.getPath() = "+file.getPath()+"   PathParent="+file.getParent()+ "  Name="+file.getName() );

            try {
                InputStream inputStream  = getContentResolver().openInputStream(selectedFileUri);
                byte[] inputData = getBytes(inputStream);
                String encImage = Base64.encodeToString(inputData,Base64.DEFAULT);
                System.out.println("InputData="+inputData);
                System.out.println("------------------------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>> EncodedFile");
                //System.out.println("EncodedFile ="+encImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //file
        } else//upload
            if (requestCode == 2 && resultCode == RESULT_OK  && data.getData() == null ) {
                System.out.println("Take picture >>> requestCode="+requestCode+"  resultCode="+resultCode+"  data.getData="+data.getData());
                Bundle extras = data.getExtras();
                System.out.println("extras ==> "+extras.get("data"));
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                System.out.println("----------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>   imageBitmap="+imageBitmap);
                imageView.setImageBitmap(imageBitmap); //megkapja az imageView a kepet


            }

    }


    private File createImageFile(String nameFile,File directory) throws IOException {
        System.out.println("NameFile="+nameFile+"   Directory="+directory);
        String imgName = "JPEG_"+nameFile+"_";

        System.out.println("IMG Name="+imgName);
        File imageFile = File.createTempFile(imgName,".jpg",directory);
        return imageFile;
    }

        private String encodeImage(Bitmap bm){

            System.out.println("Bitmap = "+bm);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b,Base64.DEFAULT);
            return encImage;


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int buffSize = 1024;
        byte[] buffer = new byte[buffSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1){
            baos.write(buffer,0,len);
        }

        return baos.toByteArray();
    }
}
