package com.codekidlabs.fontio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


public class PreviewTextSheet extends BottomSheetDialogFragment {

    EditText editText;
    ImageButton setButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v = getView();
        editText = (EditText) v.findViewById(R.id.preview_et);
        setButton = (ImageButton) v.findViewById(R.id.change_preview_button);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontioPreview.setPreviewText(editText.getText().toString());
                PreviewTextSheet.this.dismiss();
            }
        });
    }
}
