package com.android.lun.pdfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentView;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.pdfdroid.codec.PdfContext;
import org.vudroid.pdfdroid.codec.PdfDocument;
import org.vudroid.pdfdroid.codec.PdfPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alen-Liu on 2016/4/23.
 */
public class PdfRenderView extends FrameLayout {
    private DocumentView documentView;
    private DecodeService decodeService;
    private CurrentPageModel currentPageModel;
    private String mPath;
    private boolean isMoveable = false;
    /**
     * 基本结构体， 不要删掉
     * */
    public PdfRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PdfRenderView(Context context) {
        super(context);
    }

    public PdfRenderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 在这里可以设置要打开的pdf文件
     * */
    public void setmPath(String mPath) {
        this.mPath = mPath;
        if (!mPath.equals("") && mPath != null) {
            initView();
        }
    }

    /**
     * 在不销毁当前pdfrenderview 的前提下， 重新打开另一个文件。
     * */
    public void reopenPath(String mPath){
        this.mPath = mPath;
        if (!mPath.equals("") && mPath != null) {
            decodeService.setContainerView(null);
            removeView(documentView);
            decodeService = null;
            initView();
        }
    }

    /**
     * 在这里控制是否将touch 事件传递到下面的document view
     * 用于在特殊情况下 禁用用户滑动等操作
     * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isMoveable)return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 获取当前滑动到的页
     * */
    public int getCurrentPage() {
        if (documentView != null) {
            return documentView.getCurrentPage();
        } else {
            return -1;
        }
    }

    /**
     * 获取当前移动的位置
     * */
    public int getScrolledX(){
        return documentView.getScrollX();
    }
    public int getScrolledY(){
        return documentView.getScrollY();
    }

    /**
     * 获取当前放大缩小的比例
     * */
    public float getScaled(){
        return documentView.getZoomModel().getZoom();
    }

    /**
     * 移动到某个特殊的位置
     * */
    public void setScrolledPostion(int x, int y){
        documentView.scrollTo(x, y);
    }

    /**
     * 放大缩小文档
     * */
    public void setScaled(float scaled){
        documentView.getZoomModel().setZoom(scaled);
    }

    /**
     * 移动到某一页
     * */
    public void moveToPage(int page) {
        if (documentView == null) {
            return;
        } else {
            documentView.goToPage(page);
        }
    }

    /**
     * 判断是否正在滚动
     * */
    public boolean ifScrolling() {
        return !documentView.isSrollerFinished();
    }

    /**
     * 这个函数是用于获取每一页的预览图的。这样可以用listview 展示预览图。
     * 如果文档页数很多 最好是自己优化一下， 否则可能会OOM
     * */
    public List<Bitmap> getPages(){
        PdfContext pdf_conext = new PdfContext();
        PdfDocument d = (PdfDocument) pdf_conext.openDocument(mPath);
//        RectF rf = new RectF();
//        rf.bottom = rf.right = (float)1.0;
//        .renderBitmap(275, 210, rf);
        List<Bitmap> result = new ArrayList<Bitmap>();
        for(int i = 0; i < d.getPageCount(); i++){
            PdfPage page = (PdfPage) d.getPage(i);
            RectF rf = new RectF();
            rf.bottom = rf.right = (float)1.0;
            Bitmap bitmap = page.renderBitmap(275, 210, rf);
            result.add(i, bitmap);
        }
        return result;
    }

    /**
     * 初始化pdfview 必做的操作
     * */
    private void initView() {
        ZoomModel zoomModel = new ZoomModel();
        DecodingProgressModel progressModel = new DecodingProgressModel();
        currentPageModel = new CurrentPageModel();

        progressModel.addEventListener(this);
        currentPageModel.addEventListener(this);

        documentView = new DocumentView(getContext(), zoomModel, progressModel, currentPageModel);
        zoomModel.addEventListener(documentView);
        documentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        initDecodeService();
        decodeService.setContentResolver(getContext().getContentResolver());
        decodeService.setContainerView(documentView);
        documentView.setDecodeService(decodeService);
        decodeService.open(mPath);
        documentView.showDocument();
        addView(documentView);
    }

    private void initDecodeService() {
        if (decodeService == null) {
            decodeService = new DecodeServiceBase(new PdfContext());
        }
    }

}