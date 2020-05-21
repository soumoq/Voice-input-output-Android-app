package com.example.root.voiceinputoutout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xeoh.android.texthighlighter.TextHighlighter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTTS;
    private EditText voiceText;
    private SeekBar seekBerPitch;
    private SeekBar seekBerSpeed;
    private Button sayIt;
    private Button check;
    private ProgressBar loding;
    private EditText speakEditText;
    private Button speakButton;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private String text;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //When permission is not granted by user, show them message why this permission is needed.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

            //Give user option to still opt-in the permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        } else {
            // Show user dialog to grant permission to record audio
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        }


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale("en", "IN"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        sayIt.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initilaization failed");
                }
            }
        });


        voiceText = (EditText) findViewById(R.id.voice_text);
        seekBerPitch = (SeekBar) findViewById(R.id.seek_ber_pitch);
        seekBerSpeed = (SeekBar) findViewById(R.id.seek_ber_speed);
        sayIt = (Button) findViewById(R.id.say_it);
        check = (Button) findViewById(R.id.check);
        loding = (ProgressBar) findViewById(R.id.loding);
        speakEditText = (EditText) findViewById(R.id.speak_edit_text);
        speakButton = (Button) findViewById(R.id.speak_button);
        text = selectText();
        voiceText.setText(text);


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("en", "IN"));
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {


            }

            @Override
            public void onPartialResults(Bundle bundle) {

                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    String matchText = matches.get(0).toLowerCase();
                    speakEditText.setText(matchText);

                    if (matchText.equals(text)) {
                        Toast.makeText(MainActivity.this, "Perfect", Toast.LENGTH_LONG).show();
                        text = selectText();
                        voiceText.setText(text);
                    } else {
                        String words1[] = text.split("\\W+");
                        String words2[] = matchText.split("\\W+");
                        ArrayList<String> arr = new ArrayList<String>();
                        try {
                            for (int i = 0; i < words2.length; i++) {
                                for (int j = i; j < words1.length; j++) {
                                    if (words1[j].equals(words2[i])) {
                                        arr.add(words1[j]);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        StringBuffer highLight = new StringBuffer();
                        for (int i = 0; i < arr.size(); i++) {
                            highLight = highLight.append(arr.get(i) + " ");
                        }

                        String stringHighlight = highLight.toString();


                        new TextHighlighter().setBackgroundColor(Color.parseColor("#D6DBDF"))
                                .setForegroundColor(Color.GREEN)
                                .addTarget(voiceText)
                                .highlight(stringHighlight.trim(), TextHighlighter.BASE_MATCHER);

                    }

                }

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        speakButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        speakEditText.setHint("You will see input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        speakEditText.setText("");
                        speakEditText.setHint("Listening...");
                        break;
                }

                return false;
            }
        });


        sayIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(text);
            }
        });


        check.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                boolean available;

                available = checkStatus();
                if (available) {
                    Toast.makeText(MainActivity.this, "Voice data available", Toast.LENGTH_LONG).show();
                } else {
                    downloadData();
                }
            }
        });

    }


    private String selectText() {

        String[] texts = {"he loves fish tacos","there is so much to understand","brad came to dinner with us","i ate dinner","what are you doing", "i am going to school", "this is our home", "the rock is cooking", "i love reading"};
        Random rand = new Random();
        int value = rand.nextInt(texts.length);
        return texts[value];
    }


    private void downloadData() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            /*ImplementAsync implementAsync = new ImplementAsync(this);
            implementAsync.execute();*/
            Toast.makeText(this, "Downloading...", Toast.LENGTH_LONG).show();
            loding.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    boolean status = checkStatus();
                    while (!status) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        status = checkStatus();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Boolean check = checkStatus();
                                if (check) {
                                    Toast.makeText(MainActivity.this, "Download complete", Toast.LENGTH_LONG).show();
                                    loding.setProgress(0);
                                    loding.setVisibility(View.INVISIBLE);
                                } else if (!check) {
                                    Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                }
            }).start();


        } else {
            Toast.makeText(this, "Turn on your internet.", Toast.LENGTH_LONG).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean checkStatus() {

        boolean available = false;
        if (mTTS != null) {
            Locale language = new Locale("en", "IN");
            switch (mTTS.isLanguageAvailable(language)) {
                case TextToSpeech.LANG_AVAILABLE:
                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                    mTTS.setLanguage(language);

                    Voice voice = mTTS.getVoice();
                    if (voice != null) {
                        Set<String> features = voice.getFeatures();
                        if (features != null && !features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) {
                            available = true;
                        }
                    } else {
                        available = false;
                    }
                    //mTTS.setLanguage(language);
                    break;

                case TextToSpeech.LANG_MISSING_DATA:
                case TextToSpeech.LANG_NOT_SUPPORTED:
                default:
                    break;
            }
            Log.i("TTS", "available: " + available);
            //Toast.makeText(getApplicationContext(), "voice data files: " + available, Toast.LENGTH_SHORT).show();
        }
        return available;
    }


    private void speak(String text) {

        if (text != null) {
            float pitch = (float) seekBerPitch.getProgress() / 50;
            if (pitch < 0.1)
                pitch = 0.1f;

            float speed = (float) seekBerSpeed.getProgress() / 50;
            if (speed < 0.1)
                speed = 0.1f;

            mTTS.setPitch(pitch);
            mTTS.setSpeechRate(speed);

            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        } else
            voiceText.setError("You need to enter a text");
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}
