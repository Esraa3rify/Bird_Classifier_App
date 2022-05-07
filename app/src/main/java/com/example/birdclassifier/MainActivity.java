package com.example.birdclassifier;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.birdclassifier.ml.Birdclassifiertwo;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private Button select, predict;
    private TextView tv;
    private Bitmap img;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgView = (ImageView) findViewById(R.id.capturePhotoImageView);
        tv = (TextView) findViewById(R.id.textView);
        select = (Button) findViewById(R.id.selectPicBtn);
        predict = (Button) findViewById(R.id.predictPicBtn);


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                Bitmap imageBitmap = null;
                try {
                    imageBitmap = UriToBitmap(result);
                } catch (IOException e) {

                    e.printStackTrace();
                }


                imgView.setImageBitmap(imageBitmap);
                outPutGenerator(imageBitmap);
            }
        });


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGetContent.launch("image/*");

            }
        });


//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+tv.getText().toString()));
//
//                startActivity(i);
//            }
//        });


    }


    private void outPutGenerator(Bitmap image){
        try {
            Birdclassifiertwo model = Birdclassifiertwo.newInstance(MainActivity.this);

            //create input for reference
            TensorImage Image = TensorImage.fromBitmap(image);

             //runs model inference and get results
            Birdclassifiertwo.Outputs outputs = model.process(Image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            int index=0;
            float max=probability.get(0).getScore();

            for(int i=0;i<probability.size();i++)
            {
                if(max>probability.get(i).getScore()){

                    max=probability.get(i).getScore();
                    index=i;
                }

            }

            Category output=probability.get(index);
            tv.setText(output.getLabel());



            //release model resource if no longer used
            model.close();




        } catch (IOException e) {
            // TODO Handle the exception
        }

    }







    private Bitmap UriToBitmap(Uri result) throws IOException {

        return MediaStore.Images.Media.getBitmap(this.getContentResolver(),result);
    }


}


//        actvityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult) {
//
//        });
//        }
//
//          predict.setOnClickListener(new View.OnClickListener() {
//           @Override
//         public void onClick(View v) {
//
//           outPutGenerator(imageBitmap);
//
//           }
//
//
//
//    private void outPutGenerator(Bitmap imageBitmap) {
//
//        try {
//            Birdclassifiertwo model = Birdclassifiertwo.newInstance(MainActivity.this);
//
//            // Creates inputs for reference.
//            // TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
//
//            TensorImage Image = TensorImage.fromBitmap(imageBitmap);
////            tensorImage.load(img);
////            ByteBuffer byteBuffer = tensorImage.getBuffer();
////
////            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            Birdclassifiertwo.Outputs outputs = model.process(Image);
//            List<Category> probability = outputs.getProbabilityAsCategoryList();
//
//            // Releases model resources if no longer used.
//            model.close();
//
//
//           // tv.setText(outputFeature0.getFloatArray()[0] + "\n" + outputFeature0.getFloatArray()[1]);
//
//
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
//
//    }
//
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 100)
//        {
//
//            imgView.setImageURI(data != null ? data.getData() : null);
//
//            Uri uri = data.getData();
//            try {
//                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//}
