package project.college.myapplication.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import project.college.myapplication.R;
import project.college.myapplication.bean.ProfileBean;
import project.college.myapplication.util.DatabaseHandler;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    @ViewById
    TextView tvName, tvAadhaar, tvGender, tvYob, tvAddress;

    @ViewById
    EditText etMobile, etPin;

    @ViewById
    Button btnRegister;
    private IntentIntegrator qrScan;

    @AfterViews
    protected void init(){
        try{

            new IntentIntegrator(this).initiateScan();
        //qrScan.initiateScan();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerProfile();
            }
        });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{

Log.d("inside onAc","yesy");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    JSONObject obj = new JSONObject(result.getContents());

                    tvName.setText(obj.optString("name"));
                    tvAadhaar.setText(obj.optString("uid"));
                    tvGender.setText(obj.optString("gender"));
                    tvYob.setText(obj.optString("yob"));
                    tvAddress.setText(obj.optString("co")+","+obj.optString("loc")+","+obj.optString("vtc")+","+obj.optString("po")+","+obj.optString("dist")+","+obj.optString("state")+","+obj.optString("pc"));


                    //setting values to textviews

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                   // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /*public static String readQRCode(String filePath, String charset, Map hintMap)
            throws FileNotFoundException, IOException, NotFoundException {
        //BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,
                hintMap);
        return qrCodeResult.getText();
    }
    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }*/


    private void registerProfile() {
        String name = tvName.getText().toString();
        String aadhaar = tvAadhaar.getText().toString();
        String yob = tvYob.getText().toString();
        String address = tvAddress.getText().toString();
        String gender = tvGender.getText().toString();
        String mobile = etMobile.getText().toString();
        String pin = etPin.getText().toString();

        if(TextUtils.isEmpty(mobile)){
            etMobile.setError("Mobile is mandatory");
            etMobile.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(pin)){
            etPin.setError("Pin is mandatory");
            etPin.requestFocus();
            return;
        }
        DatabaseHandler db = new DatabaseHandler(this);

        // Inserting Contacts
        Log.d("Insert: ", "Inserting pin.."+pin);
        db.addProfile(new ProfileBean(Integer.parseInt(pin), Long.parseLong(aadhaar), Long.parseLong(mobile), name, gender, yob, address));
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        ProfileBean contacts = db.getProfile();



        Log.d("name",contacts.getName());
        //Toast.makeText(this,contacts.getPin(),Toast.LENGTH_LONG);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Success");
        alertDialogBuilder.setMessage("User Registered.");
        alertDialogBuilder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
