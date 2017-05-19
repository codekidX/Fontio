package com.codekidlabs.fontio;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codekidlabs.bruno.Bruno;
import com.codekidlabs.bruno.constants.References;
import com.codekidlabs.bruno.gfont.GoogleFonts;
import com.codekidlabs.bruno.services.CacheService;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class FlowingMenuFragment extends Fragment {

    private static ListView mFlowingMenuList;

    public static String ttfPath;

    private CacheService cacheService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheService = new CacheService(getActivity().getApplicationContext());
        cacheService.initGoogleFontCache();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.flowing_menu_list, container, false);

        mFlowingMenuList = (ListView) root.findViewById(R.id.menu_list_view);

        RequestGoogleFonts requestGoogleFonts = new RequestGoogleFonts(getActivity().getApplicationContext());
        requestGoogleFonts.execute();


        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cacheService.clearGoogleFontCache();
    }

    private class FontDownloader extends AsyncTask<Void,Void,Void> {

        private final ProgressDialog progressDialog;
        String url;
        String fontName;

        FontDownloader(String url, String fontName) {
            this.url = url;
            this.fontName = fontName;

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Previewing font ...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            ttfPath = cacheService.getGoogleFontCache() + "/" + fontName + ".ttf";
            cacheService.download(url, new File(ttfPath));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            FontioPreview.selectedTtf = ttfPath;
            FontioPreview.setPreviewTypeface();
            FontioPreview.setSelectedFontName(fontName + ".ttf");
        }
    }

    private class RequestGoogleFonts extends AsyncTask<Void, Void, Void> {
        ArrayList<String> fontsList;

        Context context;

        RequestGoogleFonts(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fontsList = new ArrayList<String>();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final GoogleFonts googleFonts = new GoogleFonts();
            fontsList = googleFonts.getGoogleFontNames(googleFonts.request());

            Log.e("RESPONSE", googleFonts.request());

            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFlowingMenuList.setAdapter(new FlowingMenuAdapter(context, fontsList));

                        mFlowingMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                JSONObject clickedObject = googleFonts.getJSONObjectFromFontArray(position);
                                final String clickedUrl = googleFonts.getRegularFontUrl(clickedObject);
//                            Log.e("CLICKED_FONT_URL", googleFonts.getRegularFontUrl(clickedObject));

                                FontioPreview.mDrawer.closeMenu();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        FontDownloader fontDownloader = new FontDownloader(clickedUrl, fontsList.get(position));
                                        fontDownloader.execute();
                                    }
                                }, 200);
                            }
                        });
                    }
                });
            }

            return null;
        }
    }
}
