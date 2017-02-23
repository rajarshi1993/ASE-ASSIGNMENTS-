package tutorial.maps.google.googlemapsimagesapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    int REQUEST_CAMERA=0;
    int SELECT_FILE=1;
    ImageView userImage ;
    EditText txtAddress;
    String userChoosenTask;

    Bitmap userPhoto=null;
    String userAddress;
    LatLng userCurrentLatLng;
    double userLatitude;
    double userLongitude;

    MyLocation myLocation;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //getActionBar().setTitle("Sign Up");
        userImage = (ImageView) findViewById(R.id.ivUserImage);
        txtAddress=(EditText)findViewById(R.id.etAddress);
    }

    public void onClickOfMapButton(View v)
    {
        myLocation =new MyLocation(this);
        txtAddress.setText(myLocation.userAddress);
    }

    public void onClickOfPhotoButton(View v) {
        //This code redirects to the photo activity.
       selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result=Utility.checkPermission(SignUpActivity.this);
                boolean result=true;
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                     userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);

            }else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);

            }
        }
    }


    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.userPhoto=bm;
        userImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.userPhoto=thumbnail;
        userImage.setImageBitmap(thumbnail);
    }


    public void reDirectToHomePage(View v)
    {
        Intent redirect = new Intent(SignUpActivity.this, HomeScreenActivity.class);

        /*added following code to avoid ** FAILED BINDER TRANSACTION***/
        userImage.buildDrawingCache();
        Bitmap bitProfile = userImage.getDrawingCache();
        Bundle extra =  new Bundle();
        extra.putParcelable("user_photo",bitProfile);
        redirect.putExtras(extra);

       // redirect.putExtra("user_photo",this.userPhoto);
        redirect.putExtra("user_latitude", myLocation.latitute);
        redirect.putExtra("user_longitude", myLocation.longitude);
        redirect.putExtra("user_address", myLocation.userAddress.toString());

        startActivity(redirect);
    }


}
