package com.android.lun.pdfdemo;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        PdfRenderView pdfRenderView = (PdfRenderView) findViewById(R.id.pdf_view);
        //pdfRenderView.setmPath("sample.pdf");//自己换下
    }
}
