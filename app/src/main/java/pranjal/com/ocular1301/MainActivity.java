package pranjal.com.ocular1301;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;

import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Slide;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skydoves.ElasticButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import client.yalantis.com.foldingtabbar.FoldingTabBar;

import static android.Manifest.permission_group.STORAGE;
import static android.R.attr.defaultHeight;
import static android.R.attr.defaultWidth;
import static android.R.attr.numberPickerStyle;
import static android.R.attr.windowMinWidthMajor;

public class MainActivity extends ActionBarActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,TextToSpeech.OnInitListener,SpeechRecognizerManager.OnResultListener{
    private static File destination;
Context mContext;
    FrameLayout bottom_toolbar;
    Button refresh;
Uri uri;
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    TextToSpeech TTS;
    String text;
    private final int REQUEST_PDF = 1;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private static final int PHOTO_REQUEST = 10;
    private Uri imageUri;
    private TextRecognizer detector;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private SpeechRecognizerManager mSpeechRecognizerManager;
LinearLayout translate,speak,pdf,search;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottom_toolbar=(FrameLayout)findViewById(R.id.toolbar_bottom);
bottom_toolbar.setVisibility(View.INVISIBLE);
        TTS = new TextToSpeech(this, this);
        textValue = (TextView) findViewById(R.id.text_value);
        refresh=(Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        translate=(LinearLayout)findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Loading translator", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Start.class);
                String text = textValue.getText().toString().replace("\n", " ").replace("\r", " ");
                i.putExtra("myString", text);
                startActivity(i);

            }
        });
        speak=(LinearLayout)findViewById(R.id.speak);
    speak.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = textValue.getText().toString();
            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(getApplicationContext(), "Speaking", Toast.LENGTH_SHORT).show();
        }
    });
        search=(LinearLayout)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String term = textValue.getText().toString();
                    intent.putExtra(SearchManager.QUERY, term);
                    startActivity(intent);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();

            }
        });
        pdf=(LinearLayout)findViewById(R.id.pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPDF();

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        statusMessage = (TextView) findViewById(R.id.status_message);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_text).setOnClickListener(this);

        ElasticButton gallery=(ElasticButton)findViewById(R.id.show_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GalIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);
            }
        });

        mSpeechRecognizerManager =new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        bottom_toolbar.setVisibility(View.INVISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i); // Handle the camera action
            finish();
        } else if (id == R.id.barcode_scan) {
            Intent i = new Intent(getApplicationContext(), BarcodeCameraActivity.class);
            startActivity(i);

        } else if (id == R.id.Dashboard) {
            final ProgressDialog pDialog = ProgressDialog.show(MainActivity.this,"Loading", "Please wait..",true);
            new Thread() {
                public void run() {
                    try{
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 2000) {
                            sleep(100);
                            waited += 100;
                        }
                        startActivity(new Intent(MainActivity.this,DashboardActivity.class));

                    } catch (InterruptedException e) {
                        // do nothing
                    }
                    pDialog.dismiss();  }
            }.start();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
//openPopup();


            final CharSequence[] items = {"Mobile Vision", "Take Picture"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Action");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                  switch(item){
                      case 0:                // launch Ocr capture activity.
                          Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                          intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
                          intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());
                          startActivityForResult(intent, RC_OCR_CAPTURE);
break;
                      case 1:
                          takePicture();
                          break;

                  }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text);
bottom_toolbar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    TTS.speak("No Text Captured.",TextToSpeech.QUEUE_FLUSH,null);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }  else if (requestCode == 2) {

            if (data != null) {

                uri = data.getData();

                ImageCropFunction();

            }

        }else if (requestCode == 1) {
                  if(data!= null){
                      Bundle extras = data.getExtras();
                      try {
                          Bitmap bitmap = (Bitmap)extras.get("data");
                          if (detector.isOperational() && bitmap != null) {
                              Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                              SparseArray<TextBlock> textBlocks = detector.detect(frame);
                              String blocks = "";
                              String lines = "";
                              String words = "";
                              for (int index = 0; index < textBlocks.size(); index++) {
                                  //extract scanned text blocks here
                                  TextBlock tBlock = textBlocks.valueAt(index);
                                  blocks = blocks + tBlock.getValue() + "\n" + "\n";
                                  for (Text line : tBlock.getComponents()) {
                                      //extract scanned text lines here
                                      lines = lines + line.getValue() + "\n";
                                      for (Text element : line.getComponents()) {
                                          //extract scanned text words here
                                          words = words + element.getValue() + ", ";
                                      }
                                  }
                              }
                              if (textBlocks.size() == 0) {
                                  statusMessage.setText(R.string.ocr_failure);
                              } else {
                                  textValue.setText(textValue.getText() + lines + "\n");
                                  statusMessage.setText(R.string.ocr_success);
                                  bottom_toolbar.setVisibility(View.VISIBLE);
                              }
                          } else {
                              statusMessage.setText(R.string.ocr_error);
                          }
                      } catch (Exception e) {
                          Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                                  .show();
                      }
                  }

            }else {
            if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
                launchMediaScanIntent();
                try {
                    Bitmap bitmap = decodeBitmapUri(this, imageUri);
                    if (detector.isOperational() && bitmap != null) {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> textBlocks = detector.detect(frame);
                        String blocks = "";
                        String lines = "";
                        String words = "";
                        for (int index = 0; index < textBlocks.size(); index++) {
                            //extract scanned text blocks here
                            TextBlock tBlock = textBlocks.valueAt(index);
                            blocks = blocks + tBlock.getValue() + "\n" + "\n";
                            for (Text line : tBlock.getComponents()) {
                                //extract scanned text lines here
                                lines = lines + line.getValue() + "\n";
                                for (Text element : line.getComponents()) {
                                    //extract scanned text words here
                                    words = words + element.getValue() + ", ";
                                }
                            }
                        }
                        if (textBlocks.size() == 0) {
                            statusMessage.setText(R.string.ocr_failure);
                        } else {
                            textValue.setText(textValue.getText() + lines + "\n");
                            statusMessage.setText(R.string.ocr_success);
                            bottom_toolbar.setVisibility(View.VISIBLE);
                        }
                    } else {
                        statusMessage.setText(R.string.ocr_error);

                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }



    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = TTS.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // R.id.speak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }

    //Date Format
    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());

        return df.format(date);
    }


    //Create PDF
    private Intent mShareIntent;
    PdfDocument document;
    private OutputStream os;


    public void createPDF() {
        // Create a shiny new (but blank) PDF document in memory
        document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2000, numberPickerStyle).create();
        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);


        // repaint the user's text into the page
        View content = findViewById(R.id.text_value);
        content.draw(page.getCanvas());

        // do final processing of the page
        document.finishPage(page);
        try {
            File pdfDirPath = new File(Environment.getExternalStorageDirectory() + "/Ocular/");
            if (!pdfDirPath.exists()) {
                pdfDirPath.mkdir();
            }
            String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
            File file = new File(pdfDirPath, name + ".pdf");
            os = new FileOutputStream(file);
            document.writeTo(os);
            document.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
        Toast.makeText(getApplicationContext(), "PDF Saved! View Dashboard.", Toast.LENGTH_SHORT).show();

        return;

    }


    private void takePicture() {
        // Use a folder to store all results

        File folder = new File(Environment.getExternalStorageDirectory() + "/Ocular/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Generate the path for the next photo
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(folder, name + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = Uri.fromFile(destination);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PHOTO_REQUEST);
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, textValue.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }




    //Crop image
    public void ImageCropFunction() {

        // Image Crop Code
        try {
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            File folder = new File(Environment.getExternalStorageDirectory() + "/Ocular/");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Generate the path for the next photo
            String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
            destination = new File(folder, name + ".jpg");


            CropIntent.setData(uri);
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX",16);
            CropIntent.putExtra("aspectY", 5);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(destination));

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        for(String command:commands)
        {
            if (command.equals("detect text")){

                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());
                TTS.speak("Tap on the screen to extract the text shown on camera.", TextToSpeech.QUEUE_FLUSH, null);
                startActivityForResult(intent, RC_OCR_CAPTURE);

                return;
            }

            if (command.equals("speak")){

                String text = textValue.getText().toString();
                TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                return;
            }

            if (command.equals("translate")){

                Intent i = new Intent(getApplicationContext(), Start.class);
                TTS.speak("Opening Translator.", TextToSpeech.QUEUE_FLUSH, null);
                String text = textValue.getText().toString().replace("\n", " ").replace("\r", " ");
                i.putExtra("myString", text);
                startActivity(i);
                return;
            }
            if (command.equals("barcode")){

                Intent i = new Intent(getApplicationContext(), BarcodeCameraActivity.class);
             TTS.speak("Barcode Activity Opened.",TextToSpeech.QUEUE_FLUSH,null);
                startActivity(i);

                  return;
            }
            if (command.equals("Dashboard")) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                TTS.speak("Dashboard Activity.", TextToSpeech.QUEUE_FLUSH, null);
                startActivity(i);

                return;
            }else{
                TTS.speak("Invalid Command!",TextToSpeech.QUEUE_FLUSH,null);
                return;
            }
        }

        }

    }
