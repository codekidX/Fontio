package com.codekidlabs.fontio;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


public class PreviewTextSheet extends BottomSheetDialogFragment {

    SharedPreferences preferences;

    EditText editText;
    ImageButton setButton;

    RelativeLayout sheetContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v = getView();
        sheetContent = (RelativeLayout) v.findViewById(R.id.content_sheet_preview);
        editText = (EditText) v.findViewById(R.id.preview_et);
        setButton = (ImageButton) v.findViewById(R.id.change_preview_button);

        editText.setTypeface(GeoTextView.getGeoTypeface(getContext()));

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontioPreview.setPreviewText(editText.getText().toString());
                PreviewTextSheet.this.dismiss();
            }
        });


        if(preferences.getBoolean("dm_switch", false)) {
            sheetContent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_dark));

            setButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_dark_buddy));
            setButton.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white));

            editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.bg_dark_text));
            editText.setTextColor(Color.WHITE);
        }
    }
}
