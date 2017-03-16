package com.codekidlabs.fontio;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.bruno.Bruno;
import com.codekidlabs.bruno.constants.References;
import com.codekidlabs.bruno.services.ApkBuilder;
import com.codekidlabs.bruno.services.ApkService;
import com.codekidlabs.bruno.services.TtfService;
import com.codekidlabs.bruno.services.ZipService;
import com.codekidlabs.fontio.helpers.FileHelper;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.utils.DiskUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class FontioPreview extends AppCompatActivity {

    private SeekBar mFontSizeSeeker;
    private TextView mFontPreview;
    private GeoTextView mSelectedFontName;
    private CoordinatorLayout coordinatorLayout;

    private ImageButton mPackageButton;

    private String selectedTtf;

    private Bruno mBruno;

    Animation wiggleWiggleWiggle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Menu fontioMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fontio_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FileHelper.createWorkstation();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor ed = sharedPreferences.edit();

        ed.putString(References.FONTIO_DIR_KEY, "/storage/emulated/0/Expansion/packs");
        ed.apply();

        wiggleWiggleWiggle = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_wiggle);


        initUi();

        updateUi();

        mBruno = new Bruno(getApplicationContext(),References.FONTIO_FSF_PATH);

        if(!mBruno.check()) {
            FsfDownloader fsfDownloader = new FsfDownloader(mBruno, this);
            fsfDownloader.execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        if(sharedPreferences.contains("CLEAN")) {
            if(sharedPreferences.getBoolean("CLEAN", false)) {
                cleanFontioDirectory();

                editor.putBoolean("CLEAN", false);
                editor.apply();
            }
        }
    }

    private void cleanFontioDirectory() {
        File dir = new File(References.FONTIO_DIR);
        if(dir.listFiles() != null) {
            for(File file: dir.listFiles())
                file.delete();
        }

        try {
            FileUtils.deleteDirectory(new File(References.FONTIO_FSF_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUi() {

        mFontSizeSeeker = (SeekBar) findViewById(R.id.np_seekbar);
        mFontPreview = (TextView) findViewById(R.id.preview_quote);
        mSelectedFontName = (GeoTextView) findViewById(R.id.selected_font_name);
        mPackageButton = (ImageButton) findViewById(R.id.package_font_button);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.co_ord);

        cleanFontioDirectory();
    }




    private void updateUi() {
        // seekbar
        mFontSizeSeeker.setMax(60);
        mFontSizeSeeker.setProgress(24);

        mFontSizeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFontPreview.setTextSize((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                doTheWiggleAnimation();
            }
        });

        // textview
        mFontPreview.setTextSize((float) mFontSizeSeeker.getProgress());

        // imagebutton
        mPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTtf != null) {
                    PopupMenu popupMenu = new PopupMenu(FontioPreview.this, v);
                    popupMenu.getMenuInflater().inflate(R.menu.package_popup, popupMenu.getMenu());

                    try {
                        Class<?> classPopupMenu = Class.forName(popupMenu
                                .getClass().getName());
                        Field mPopup = classPopupMenu.getDeclaredField("mPopup");
                        mPopup.setAccessible(true);
                        Object menuPopupHelper = mPopup.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                .getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(
                                "setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.subs_font:
                                    ApkService apkBuilder = new ApkService(getApplicationContext(), selectedTtf);
                                    apkBuilder.init(References.SUBSTRATUM_THEME);
                                    break;
                            }

                            return true;
                        }
                    });

                    popupMenu.show();
                } else {
                    Snackbar.make(coordinatorLayout,"Please select font first", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }
            }
        });

        mPackageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ApkService.openApp(getApplicationContext(), "projekt.substratum");
                return true;
            }
        });


    }

    private void doTheWiggleAnimation() {
        mFontPreview.startAnimation(wiggleWiggleWiggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fontio_preview, menu);
        fontioMenu = menu;

        initPrompts();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPrompts();
            }
        }, 310);

        return true;
    }

    private void initPrompts() {
        if(!sharedPreferences.contains(References.PROMPT_CHOOSER)) {
            new MaterialTapTargetPrompt.Builder(FontioPreview.this)
                    .setTarget(fontioMenu.getItem(3).getItemId())
                    .setPrimaryText("Choose Font")
                    .setIcon(R.drawable.alphabetical)
                    .setSecondaryText("select ttf/otf file")
                    .setBackgroundColour(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                            editor.putBoolean(References.PROMPT_CHOOSER, true);
                            editor.apply();


                            if(!sharedPreferences.contains(References.PROMPT_PACKAGE)) {
                                new MaterialTapTargetPrompt.Builder(FontioPreview.this)
                                        .setTarget(mPackageButton)
                                        .setPrimaryText("Package Font")
                                        .setIcon(R.drawable.package_variant_closed)
                                        .setSecondaryText("Single Press - package font \n Long Press - open theme engine")
                                        .setBackgroundColour(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                                        .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                            @Override
                                            public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                                                editor.putBoolean(References.PROMPT_PACKAGE, true);
                                                editor.apply();

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && coordinatorLayout != null && ContextCompat.checkSelfPermission(FontioPreview.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                    Snackbar.make(coordinatorLayout, "Fontio needs your permission to access storage for selecting the font ttf", Snackbar.LENGTH_INDEFINITE)
                                                            .setAction("GRANT", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    ActivityCompat.requestPermissions(FontioPreview.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199);
                                                                }
                                                            }).show();
                                                }
                                            }

                                            @Override
                                            public void onHidePromptComplete() {

                                            }
                                        }).show();
                            }
                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    }).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(FontioPreview.this, Settings.class);
            startActivityForResult(intent, 54);
            return true;
        } else if(id == R.id.action_choose_font) {
            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(this)
                    .withFragmentManager(getSupportFragmentManager())
                    .withMemoryBar(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.FILE_PICKER)
                    .build();

            chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                @Override
                public void onSelect(String s) {
                    if(TtfService.isFont(s)) {
                        Typeface typeface = Typeface.createFromFile(s);
                        mFontPreview.setTypeface(typeface);
                        //set selected font name in bottom bar
                        mSelectedFontName.setText(getSelectedFontName(s));
                        selectedTtf = s;
                    } else {
                        Snackbar.make(coordinatorLayout,"Please choose a ttf/otf file.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("ALRIGHT", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                    }

                }
            });

            chooser.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getSelectedFontName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    public static class FsfDownloader extends AsyncTask<Void,Void,Void> {

        Bruno bruno;
        FontioPreview fontioPreview;

        ProgressDialog progressDialog;

        public FsfDownloader(Bruno bruno, FontioPreview fontioPreview) {
            this.bruno = bruno;
            this.fontioPreview = fontioPreview;

            progressDialog = new ProgressDialog(fontioPreview);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Wait. I think I found cheese ...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            bruno.fetch(References.FONTIO_FSF_PATH);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
