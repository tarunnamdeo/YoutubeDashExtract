package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.formate.Format;
import com.example.myapplication.formate.YouTubeFormate;
import com.example.myapplication.formate.YtFile;
import com.example.myapplication.model.YTMedia;
import com.example.myapplication.model.YTSubtitles;
import com.example.myapplication.model.YoutubeMeta;
import com.example.myapplication.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
     EditText edit;
     Button button;
    private ArrayList<String> urls_li;
    SparseArray<YtFile> ytFiles = new SparseArray<>();
    YouTubeFormate youTubeFormate=new YouTubeFormate();

    SparseArray<YtFile> ytFiles2 = new SparseArray<>();

    SparseArray<YtFile> ytVideomux = new SparseArray<>();

    private LinearLayout mainLayout;

//    private List<YtFragmentedVideo> formatsToShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        edit=findViewById(R.id.edit);

        mainLayout = findViewById(R.id.main_layout);

        urls_li = new ArrayList<>();
        edit.setText("https://youtu.be/zjMtaw2mrrc");
        edit.setHint("id or url");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Extracting", Toast.LENGTH_LONG).show();

                new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner(){

                    @Override
                    public void onExtractionDone(List<YTMedia> adativeStream, final List<YTMedia> muxedStream, List<YTSubtitles> subtitles, YoutubeMeta meta) {

                        //url to get subtitle
                        //String subUrl=subtitles.get(0).getBaseUrl();

                        for (YTMedia mux:muxedStream) {
//                            urls_li.add(mux.getUrl());
                            YtFile newFile2mux = new YtFile(youTubeFormate.FORMAT_MAP.get(mux.getItag()), mux.getUrl());
                            ytVideomux.put(mux.getItag(), newFile2mux);
                        }

                        for (YTMedia media:adativeStream) {
                            if(media.isVideo()){
                                Log.d("video2", String.valueOf(media.getItag()));
                                YtFile newFile2 = new YtFile(youTubeFormate.FORMAT_MAP.get(media.getItag()), media.getUrl());
                                ytFiles2.put(media.getItag(), newFile2);


                                //is video
                            }else{
                                //is audio


//                                youTubeFormate.FORMAT_MAP.get(media.getItag());
                                YtFile newFile = new YtFile(youTubeFormate.FORMAT_MAP.get(media.getItag()), media.getUrl());
                                ytFiles.put(media.getItag(), newFile);
                                Log.d("video3", String.valueOf(media.getItag()));
//



                            }
                        }

                        // Iterate over itags
                        for (int i = 0, itag; i < ytFiles.size(); i++) {
                            itag = ytFiles.keyAt(i);
                            // ytFile represents one file with its url and meta data
                            YtFile ytFile = ytFiles.get(itag);

                            // Just add videos in a decent format => height -1 = audio
                            if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                                myaddButtonToMainLayout(meta.getTitle(), ytFile);
                            }
                        }

                        for (int i=0,itag3;i<ytVideomux.size();i++){
                            itag3 = ytVideomux.keyAt(i);
                            YtFile ytFile = ytVideomux.get(itag3);
                            addvideobtn(meta.getTitle(),ytFile);
                        }



//                        ForVideo


                        for (int i = 0, itag2; i < ytFiles2.size(); i++) {
                            itag2 = ytFiles2.keyAt(i);
                            YtFile ytFile = ytFiles2.get(itag2);
                            Log.d("formatex", String.valueOf(ytFile));
//                            myaddButtonToMainLayout(meta.getTitle(), ytFile);
                            addvideobtn(meta.getTitle(),ytFile);
                        }




//                        urls_li.clear();
//                        for (YTMedia c:muxedStream) {
//
//                            urls_li.add(c.getUrl());
//                            adapter.notifyDataSetChanged();
//                        }
//                        for (YTMedia media:adativeStream) {
//
//                            urls_li.add(media.getUrl());
//                            adapter.notifyDataSetChanged();
//                        }
//                        Toast.makeText(getApplicationContext(), meta.getTitle(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), meta.getAuthor(), Toast.LENGTH_LONG).show();
                        if (adativeStream.isEmpty()) {
                            LogUtils.log("null ha");
                            return;
                        }
                        if (muxedStream.isEmpty()) {
                            LogUtils.log("null ha");
                            return;
                        }
                        String url = muxedStream.get(0).getUrl();
                        Log.d("ssss",url);
//                        PlayVideo(url);


                    }


                    @Override
                    public void onExtractionGoesWrong(final ExtractorException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();


                    }
                }).useDefaultLogin().Extract(edit.getText().toString());

            }
        });
    }


    private void myaddButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        // Display some buttons and let the user choose the format
try {

    String btnText;
    if (ytfile.getFormat().getAudioBitrate() != -1) {

        btnText = "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s";

    }else {
        btnText = "No Audio";
    }

    if (btnText.equals("No Audio")) {
        Log.d("noaudio", "noaudio");
    } else {


        Button btn = new Button(this);
        btn.setText(btnText);
        Log.d("btnn", btnText);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
                } else {
                    filename = videoTitle + "." + ytfile.getFormat().getExt();
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//                    mydownloadFromUrl(ytfile.getUrl(), videoTitle, filename);
                // finish();
                Log.d("urlss", ytfile.getUrl());
            }
        });
        mainLayout.addView(btn);
    }
}catch (Exception e){
    e.printStackTrace();
}
    }


private void addvideobtn(final String title, final YtFile ytFile){
try {

//    String btnText = (ytFile.getFormat().getHeight() == -1) ? "Audio " +
//            ytFile.getFormat().getAudioBitrate() + " kbit/s" :
//            ytFile.getFormat().getHeight() + "p";
//    btnText += (ytFile.getFormat().isDashContainer()) ? " dash" : "";

    String btnText = (ytFile.getFormat().getFps() == 60) ? ytFile.getFormat().getHeight() + "p60" :
            ytFile.getFormat().getHeight() + "p" + "  " + ytFile.getFormat().getExt();
    btnText += (ytFile.getFormat().isDashContainer()) ? " dash " : " ";

    Button btn = new Button(this);
    btn.setText(btnText);
    btn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String filename;
            if (title.length() > 55) {
                filename = title.substring(0, 55);
            } else {
                filename = title;
            }
            filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
            filename += (ytFile.getFormat().getHeight() == -1) ? "" : "-" + ytFile.getFormat().getHeight() + "p";
            String downloadIds = "";
            boolean hideAudioDownloadNotification = false;
            Toast.makeText(MainActivity.this, filename, Toast.LENGTH_SHORT).show();
            Log.d("urlsch",ytFile.getFormat()+   ytFile.getUrl());

//
//            if (ytFrVideo.audioFile != null)
//                cacheDownloadIds(downloadIds);
            // finish();
        }
    });
    mainLayout.addView(btn);
}catch (Exception e){
    e.printStackTrace();
}

}

}

