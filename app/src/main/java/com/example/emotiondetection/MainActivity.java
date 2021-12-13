package com.example.emotiondetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    FaceServiceClient restClient = new FaceServiceRestClient("https://faceapibarchelordegree.cognitiveservices.azure.com/face/v1.0/detect?detection_03", "ed8a9e3dafdf490ab81363940b7d6974");

    ImageView imageView;
    Bitmap mBitmap;
    boolean takePhoto = false;
    private ProgressDialog mDialog;
    Face[] faceDetected;
    JSONObject jsonObject,jsonObject1;
    Dialog dialog;
    TextView textView;
    /*The usage of this function is to retrieve bitmap from the camera app.
    Bitmap retrieved from the camera will be converted into input stream
    and will be sent to FACE API.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            detectEmotion(bitmap);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTheme(R.style.Theme_EmotionDetection);
        setContentView(R.layout.activity_main);
        mDialog = new ProgressDialog(this);

        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        imageView = findViewById(R.id.imageView);
        Toast.makeText(getApplicationContext(), "Press 'Detect' to take a picture.", Toast.LENGTH_LONG).show();
        Button btnDetect = findViewById(R.id.btnDetectFace);

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //give permissions
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 0);
                }

            }
        });

      /*  dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button listen = dialog.findViewById(R.id.btn_oky);
        Button cancel = dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });  */
    }

    private void detectEmotion(final Bitmap imageBitmap) {

      //  Button listen = dialog.findViewById(R.id.btn_oky);

//       setContentView(R.layout.custom_dialog);
  //     textView = (TextView) findViewById(R.id.textView);

        final Boolean[] mask = new Boolean[1];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, Face[]> detectEmotion = new AsyncTask<InputStream, String, Face[]>() {
            String exceptionMessage = "";
            @SuppressLint("DefaultLocale")
            @Override
            protected Face[] doInBackground(InputStream... params) {
                try{
                    publishProgress("Detecting face...");
                    Face[] result = restClient.detect(params[0],
                            true, //return face id
                            false,  //return face landmarks
                            // return face attributes
                            new FaceServiceClient.FaceAttributeType[]{
                                    FaceServiceClient.FaceAttributeType.Emotion,
                                    FaceServiceClient.FaceAttributeType.Gender,
                                    FaceServiceClient.FaceAttributeType.Occlusion
                            });


                    for (int i=0;i<result.length;i++) {
                      //  jsonObject.put("Mask" , result[i].faceAttributes.occlusion.mouthOccluded);
                     //   Log.e(TAG, "doInBackground: "+jsonObject.toString()  );
                        mask[0] = result[i].faceAttributes.occlusion.mouthOccluded;
                     //   jsonObject1.put(  (String.valueOf(i)),jsonObject);
                    }
                   // runOnUiThread(() -> Toast.makeText(MainActivity.this,"DATA"+jsonObject1,Toast.LENGTH_LONG).show());

                    List<Double> list = new ArrayList<>();

                    for(int i = 0; i < result.length; i++){
                      //  list.add(result[i].faceAttributes.occlusion.mouthOccluded);
                        list.add(result[i].faceAttributes.emotion.fear);
                        list.add(result[i].faceAttributes.emotion.disgust);
                        list.add(result[i].faceAttributes.emotion.contempt);
                        list.add(result[i].faceAttributes.emotion.anger);
                        list.add(result[i].faceAttributes.emotion.neutral);
                        list.add(result[i].faceAttributes.emotion.surprise);
                        list.add(result[i].faceAttributes.emotion.sadness);
                        list.add(result[i].faceAttributes.emotion.happiness);

                        Collections.sort(list);
                        double maxNum = list.get(list.size() - 1);



                        if(maxNum == result[i].faceAttributes.emotion.anger){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("anger", mask[0], Uri.parse("https://open.spotify.com/playlist/3zdG0UufTpHl9QocAzhgW7?si=0XvIAr8HTp2O3b8K3EMMOA&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Anger"));
                  /*          runOnUiThread(() -> Toast.makeText(MainActivity.this,"Anger",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());
                            listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/3zdG0UufTpHl9QocAzhgW7?si=0XvIAr8HTp2O3b8K3EMMOA&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });        */

                        }
                        else if (maxNum == result[i].faceAttributes.emotion.contempt){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("contempt", mask[0], Uri.parse("https://open.spotify.com/playlist/64CyYYgSxNc5NmWXPVTK2C?si=9KOo7E6rScajc-s5culTOQ&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Contempt"));
              /*              listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/64CyYYgSxNc5NmWXPVTK2C?si=9KOo7E6rScajc-s5culTOQ&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });

                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Contempt",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());    */
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.disgust){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("disgust", mask[0], Uri.parse("https://open.spotify.com/playlist/37i9dQZEVXcUhyZgJu5bYs?si=UV7YjZhxT4e9xbia05WfPw"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Disgust"));
                    /*        listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/37i9dQZEVXcUhyZgJu5bYs?si=UV7YjZhxT4e9xbia05WfPw"));
                                    startActivity(browserIntent);
                                }
                            });
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Disgust",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());         */
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.fear){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("fear", mask[0], Uri.parse("https://open.spotify.com/playlist/26eH5yZdOEeWyFhqL0ZGx5?si=JMjl-FCvS1m1_xnJPJJpJg&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Fear"));
                /*            listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/26eH5yZdOEeWyFhqL0ZGx5?si=JMjl-FCvS1m1_xnJPJJpJg&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Fear",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());        */
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.happiness){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("happiness", mask[0], Uri.parse("https://open.spotify.com/playlist/1Dq3T06j15N6C5UU2lVomt?si=TLnLW8KbSuWjYZxnMUiUww&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Happiness"));
                 /*           listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/1Dq3T06j15N6C5UU2lVomt?si=TLnLW8KbSuWjYZxnMUiUww&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Happiness",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());     */
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.neutral){
                /*            listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/37i9dQZEVXcUhyZgJu5bYs?si=UV7YjZhxT4e9xbia05WfPw"));
                                    startActivity(browserIntent);
                                }
                            });   */
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("neutral", mask[0], Uri.parse("https://open.spotify.com/playlist/37i9dQZEVXcUhyZgJu5bYs?si=UV7YjZhxT4e9xbia05WfPw"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Neutral"));
                          //  runOnUiThread(() -> Toast.makeText(MainActivity.this,"Neutral",Toast.LENGTH_LONG).show());
                            //runOnUiThread(()-> dialog.show());
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.sadness){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("sadness", mask[0], Uri.parse("https://open.spotify.com/playlist/70l89aIt9dlGMeULHQNLga?si=LULPypfRTCmPL4l37xQpaA&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Sadness"));
                   /*         listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/70l89aIt9dlGMeULHQNLga?si=LULPypfRTCmPL4l37xQpaA&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Sadness",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());   */
                        }
                        else if (maxNum == result[i].faceAttributes.emotion.surprise){
                            CustomFragmentDialog customFragmentDialog = new CustomFragmentDialog("surprise", mask[0], Uri.parse("https://open.spotify.com/playlist/7oAV5aUxKfXrMRA5Hv5zVI?si=DoOUQqtCQL2a2HItGLDZhA&utm_source=whatsapp&dl_branch=1"));
                            runOnUiThread(()->customFragmentDialog.show(getSupportFragmentManager(), "Surprise"));
                 /*           listen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/7oAV5aUxKfXrMRA5Hv5zVI?si=DoOUQqtCQL2a2HItGLDZhA&utm_source=whatsapp&dl_branch=1"));
                                    startActivity(browserIntent);
                                }
                            });
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,"Surprise",Toast.LENGTH_LONG).show());
                            runOnUiThread(()-> dialog.show());    */
                        }

                    }

              //      runOnUiThread(()-> dialog.show());

               //     runOnUiThread(() -> Toast.makeText(MainActivity.this,"DATA"+list,Toast.LENGTH_LONG).show());
                   
                    if (result == null) {
                        publishProgress(
                                "Detection Finished. Nothing detected");
                        return null;
                    }
                    Log.e("TAG", "doInBackground: "+"   "+result.length );
                    publishProgress(String.format(
                            "Detection Finished. %d face(s) detected",
                            result.length));

                    return result;
                } catch  (Exception e) {
                    exceptionMessage = String.format(
                            "Detection failed: %s", e.getMessage());
                    return null;
                }



            }

            @Override
            protected void onPreExecute() {
                //TODO: show progress dialog
                mDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... progress) {
                //TODO: update progress
                mDialog.setMessage(progress[0]);
            }

            @Override
            protected void onPostExecute(Face[] result) {
                //TODO: update face frames
                mDialog.dismiss();

                faceDetected = result;

                if (!exceptionMessage.equals("")) {
                    if (faceDetected == null) {
//                                showError(exceptionMessage + "\nNo faces detected.");
                    } else {
//                                showError(exceptionMessage);
                    }
                }
                if (result == null) {
                    if (faceDetected == null) {
//                                showError("No faces detected");
                    }
                }
                Log.e("TAG", "onPostExecute: "+faceDetected );

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(
                        drawFaceRectOnBitmap(imageBitmap, result));
                imageBitmap.recycle();
//                        Toast.makeText(getApplicationContext(), "Now you can identify the person by pressing the \"Identify\" Button", Toast.LENGTH_LONG).show();
                takePhoto = true;
            }
        };

        detectEmotion.execute(inputStream);


    }

    private Bitmap drawFaceRectOnBitmap(Bitmap imageBitmap, Face[] result) {
        Bitmap bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        if(result != null)
        {
            for(Face f : result){
                FaceRectangle faceRectangle = f.faceRectangle;
                canvas.drawRect(faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }
}