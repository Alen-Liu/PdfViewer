package org.vudroid.core;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

public class Page {
	final int index;
	RectF bounds;
	public PageTreeNode node;
	private DocumentView documentView;
	private final TextPaint textPaint = textPaint();
	private final Paint fillPaint = fillPaint();
	private final Paint strokePaint = strokePaint();

	Page(DocumentView documentView, int index) {
		this.documentView = documentView;
		this.index = index;
		node = new PageTreeNode(documentView, new RectF(0, 0, 1, 1), this, 1,
				null);
		fillPaint.setColor(Color.WHITE); // 绘制前颜色
		// logo = BitmapFactory.decodeFile("/sdcard/logo.png");
	}

    public Bitmap getPreBitmap(){
        return node.getPreBitmap();
    }
	private float aspectRatio;

	float getPageHeight(int mainWidth, float zoom) {
		return mainWidth / getAspectRatio() * zoom;
	}

	public int getTop() {
		return Math.round(bounds.top);
	}

	@SuppressLint("SdCardPath")
	public void draw(Canvas canvas) {
		if (!isVisible()) {
			return;
		}
		canvas.drawRect(bounds, fillPaint);
		// canvas.drawBitmap(logo, bounds.centerX() - logo.getWidth() / 2,
		// bounds.centerY() - logo.getHeight() / 2-50, textPaint);
		// canvas.d
		canvas.drawText("Page " + (index + 1), bounds.centerX(),
				bounds.centerY(), textPaint); // 加载前显示的页码
		node.draw(canvas);
		canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.top,
				strokePaint);
		canvas.drawLine(bounds.left, bounds.bottom, bounds.right,
				bounds.bottom, strokePaint);
	}

	private Paint strokePaint() {
		final Paint strokePaint = new Paint();
		strokePaint.setColor(Color.BLACK);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(2);
		return strokePaint;
	}

	private Paint fillPaint() {
		final Paint fillPaint = new Paint();
		fillPaint.setColor(Color.GRAY);
		fillPaint.setStyle(Paint.Style.FILL);
		return fillPaint;
	}

	private TextPaint textPaint() {
		final TextPaint paint = new TextPaint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setTextSize(24);
		paint.setTextAlign(Paint.Align.CENTER);
		return paint;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(float aspectRatio) {
		if (this.aspectRatio != aspectRatio) {
			this.aspectRatio = aspectRatio;
			documentView.invalidatePageSizes();
		}
	}

	public boolean isVisible() {
		return RectF.intersects(documentView.getViewRect(), bounds);
	}

	public void setAspectRatio(int width, int height) {
		setAspectRatio(width * 1.0f / height);
	}

	void setBounds(RectF pageBounds) {
		bounds = pageBounds;
		node.invalidateNodeBounds();
	}

	public void updateVisibility() {
		node.updateVisibility();
	}

	public void invalidate() {
		node.invalidate();
	}
}
