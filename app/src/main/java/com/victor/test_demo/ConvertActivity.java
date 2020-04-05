package com.victor.test_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import vip.upya.lib.sfof.SelectFileOrFolderDialog;

public class ConvertActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        textView = findViewById(R.id.filename);
    }

    public void choosefile(View view) {
        new SelectFileOrFolderDialog(this, true, SelectFileOrFolderDialog.CHOICEMODE_ONLY_FILE,
                new SelectFileOrFolderDialog.OnSelectFileOrFolderListener() {
                    @Override
                    public void onSelectFileOrFolder(List<File> selectedFileList) {
                        textView.setText(selectedFileList.get(0).getAbsolutePath());
                    }
                }).show();
    }

    public void upload(View view) {

    }
}
