package com.example.guide;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class GuideView extends View {

    private FrameLayout rootView;
    private int screenW;
    private int screenH;
    private Bitmap fgBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private int radius;
    private int maskColor = 0x99000000;
    private boolean touchOutsideCancel = true;
    private OnDismissListener onDismissListener;
    private List<GuideData> guideDataList = new ArrayList<>();

    public static final int SHAPE_CIRCLE = 0;
    public static final int SHAPE_RECT = 1;
    public static final int SHAPE_OVAL = 2;

    public static final int GRAVITY_TOP = Gravity.TOP;                              // 上边居中显示 tipView
    public static final int GRAVITY_BOTTOM = Gravity.BOTTOM;                        // 下边居中显示 tipView
    public static final int GRAVITY_LEFT_TOP = Gravity.LEFT | Gravity.TOP;          // 左上边显示 tipView
    public static final int GRAVITY_LEFT_BOTTOM = Gravity.LEFT | Gravity.BOTTOM;    // 左下边居中显示 tipView
    public static final int GRAVITY_RIGHT_TOP = Gravity.RIGHT | Gravity.TOP;        // 右上边显示 tipView
    public static final int GRAVITY_RIGHT_BOTTOM = Gravity.RIGHT | Gravity.BOTTOM;  // 右下边显示 tipView

    private GuideView(Activity activity) {
        super(activity);
        init(activity);
    }

    private void init(Activity activity) {
        try {
            rootView = (FrameLayout) activity.getWindow().getDecorView();

            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenW = metrics.widthPixels;
            screenH = metrics.heightPixels;

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            mPaint.setARGB(0, 255, 0, 0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.SOLID));
            fgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(fgBitmap);
            mCanvas.drawColor(maskColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (guideDataList == null || guideDataList.isEmpty()) {
                return;
            }

            canvas.drawBitmap(fgBitmap, 0, 0, null);

            for (GuideData guideData : guideDataList) {
                int vWidth = guideData.getTargetView().getWidth();
                int vHeight = guideData.getTargetView().getHeight();
                int padding = guideData.getPadding();
                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;

                Rect rtLocation = getLocationInView(rootView, guideData.getTargetView());
                left = rtLocation.left;
                top = rtLocation.top;
                right = rtLocation.right;
                bottom = rtLocation.bottom;

                switch (guideData.getShapeType()) {
                    case SHAPE_OVAL:
                        RectF rectf = new RectF(left - padding, top - padding, right + padding, bottom + padding);
                        mCanvas.drawOval(rectf, mPaint);
                        break;
                    case SHAPE_RECT:
                        RectF rect = new RectF(left - padding, top - padding, right + padding, bottom + padding);
                        mCanvas.drawRoundRect(rect, 2, 2, mPaint);
                        break;
                    case SHAPE_CIRCLE:
                    default:
                        radius = vWidth > vHeight ? vWidth / 2 + padding / 2 : vHeight / 2 + padding / 2;
                        if (radius < 50) {
                            radius = 100;
                        }
                        mCanvas.drawCircle(left + vWidth / 2, top + vHeight / 2, radius, mPaint);
                        break;
                }

                int arrowTop = 0;
                int margin = guideData.getMargin();
                Bitmap arrowBitmap = guideData.getArrowBitmap();
                Bitmap tipBitmap = guideData.getTipBitmap();

                switch (guideData.getGravity()) {
                    case GRAVITY_LEFT_TOP: // 左上边显示 tipView
                        arrowTop = top + vHeight / 2 - arrowBitmap.getHeight();
                        canvas.drawBitmap(arrowBitmap, left - arrowBitmap.getWidth() - margin , arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left - tipBitmap.getWidth(), arrowTop - tipBitmap.getHeight(), null);
                        }
                        break;
                    case GRAVITY_LEFT_BOTTOM: // 左下边显示 tipView
                        arrowTop = top + vHeight / 2;
                        canvas.drawBitmap(arrowBitmap, left - arrowBitmap.getWidth() - margin , arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left - tipBitmap.getWidth(), arrowTop + arrowBitmap.getHeight(), null);
                        }
                        break;
                    case GRAVITY_RIGHT_TOP: // 右上边显示 tipView
                        arrowTop = top  + vHeight / 2 - arrowBitmap.getHeight();
                        canvas.drawBitmap(arrowBitmap, left + vWidth + margin, arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left + vWidth, arrowTop - tipBitmap.getHeight(), null);
                        }
                        break;
                    case GRAVITY_RIGHT_BOTTOM: // 右下边显示 tipView
                        arrowTop = top  + vHeight / 2;
                        canvas.drawBitmap(arrowBitmap, left + vWidth + margin, arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left + vWidth, arrowTop + arrowBitmap.getHeight(), null);
                        }
                        break;
                    case GRAVITY_TOP:  // 上边居中显示 tipView
                        arrowTop = guideData.getShapeType() == SHAPE_CIRCLE ? top - arrowBitmap.getHeight() - radius / 2 - margin : top - arrowBitmap.getHeight() - margin;
                        canvas.drawBitmap(arrowBitmap, left + vWidth / 2 , arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left + vWidth / 2 - tipBitmap.getWidth() / 2, arrowTop - tipBitmap.getHeight(), null);
                        }
                        break;
                    case GRAVITY_BOTTOM: // 下边居中显示 tipView
                        arrowTop = guideData.getShapeType() == SHAPE_CIRCLE ? bottom + radius / 2 + margin : bottom + margin;
                        canvas.drawBitmap(arrowBitmap, left + vWidth / 2 , arrowTop, null);
                        if (tipBitmap != null) {
                            canvas.drawBitmap(tipBitmap, left + vWidth / 2 - tipBitmap.getWidth() / 2, arrowTop + arrowBitmap.getHeight(), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                try {
                    if (touchOutsideCancel) {
                        this.setVisibility(View.GONE);
                        if (rootView != null) {
                            rootView.removeView(this);
                        }
                        if (this.onDismissListener != null) {
                            onDismissListener.onDismiss();
                        }
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public static GuideView builder(Activity activity) {
        return new GuideView(activity);
    }

    /**
     * 绘制前景画布颜色
     *
     * @param bgColor
     */
    public GuideView setMaskColor(int bgColor) {
        try {
            this.maskColor = ContextCompat.getColor(getContext(),bgColor);
            mCanvas.drawColor(maskColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 添加高亮布局内容
     *
     * @param targetView  高亮 View
     * @param arrowId     箭头图片资源
     * @param tipId       提示图片资源
     * @param gravity     布局显示位置
     */
    public GuideView addHighLight(View targetView, int arrowId, int tipId, int gravity) {
        GuideData guideData = new GuideData(getResources(), targetView, arrowId, tipId);
        guideData.setGravity(gravity);
        guideDataList.add(guideData);
        return this;
    }

    /**
     * 添加高亮布局内容
     *
     * @param targetView  高亮 View
     * @param arrowId     箭头图片资源
     * @param tipId       提示图片资源
     * @param gravity     布局显示位置
     * @param shapeType   高亮形状
     */
    public GuideView addHighLight(View targetView, int arrowId, int tipId, int gravity, int shapeType) {
        GuideData guideData = new GuideData(getResources(), targetView, arrowId, tipId);
        guideData.setGravity(gravity);
        guideData.setShapeType(shapeType);
        guideDataList.add(guideData);
        return this;
    }

    /**
     * 添加高亮布局内容
     *
     * @param guideData  自定义布局内容
     */
    public GuideView addHighLight(GuideData guideData) {
        guideDataList.add(guideData);
        return this;
    }

    /**
     * 设置外部是否关闭，默认关闭
     *
     * @param cancel
     */
    public GuideView setTouchOutsideDismiss(boolean cancel) {
        this.touchOutsideCancel = cancel;
        return this;
    }

    /**
     * 设置关闭监听
     *
     * @param listener
     */
    public GuideView setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
        return this;
    }

    /**
     * 清空画布
     */
    public GuideView clearBackground() {
        if (mCanvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

        mCanvas = new Canvas(fgBitmap);
        mCanvas.drawColor(maskColor);
        return this;
    }

    /**
     * 显示 GuideView
     */
    public void show() {
        if (rootView != null) {
            rootView.addView(this);
        }
    }

    private static Rect getLocationInView(View parent, View child) {
        if (child == null || parent == null) {
            throw new IllegalArgumentException("parent and child can not be null");
        }

        View decorView = null;
        Context context = child.getContext();
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }

        Rect result = new Rect();
        Rect tmpRect = new Rect();

        View tmp = child;

        if (child == parent) {
            child.getHitRect(result);
            return result;
        }

        while (tmp != decorView && tmp != parent) {
            tmp.getHitRect(tmpRect);
            if (!tmp.getClass().equals("NoSaveStateFrameLayout")) {
                result.left += tmpRect.left;
                result.top += tmpRect.top;
            }
            tmp = (View) tmp.getParent();
        }

        result.right = result.left + child.getMeasuredWidth();
        result.bottom = result.top + child.getMeasuredHeight();
        return result;
    }

    public static class GuideData {

        private View targetView;
        private Bitmap arrowBitmap;
        private Bitmap tipBitmap;
        private int padding = 0;  // px
        private int margin = 10;  // px
        private int gravity = GRAVITY_TOP;
        private int shapeType = GuideView.SHAPE_CIRCLE;

        public GuideData(View targetView) {
            this.targetView = targetView;
        }

        public GuideData(Resources resources, View targetView, int arrowId, int tipId) {
            this.targetView = targetView;
            this.arrowBitmap = BitmapFactory.decodeResource(resources, arrowId);
            this.tipBitmap = BitmapFactory.decodeResource(resources, tipId);
        }

        public View getTargetView() {
            return this.targetView;
        }

        public Bitmap getArrowBitmap() {
            return this.arrowBitmap;
        }

        public Bitmap getTipBitmap() {
            return this.tipBitmap;
        }

        public int getPadding() {
            return this.padding;
        }

        public int getMargin() {
            return this.margin;
        }

        public int getGravity() {
            return this.gravity;
        }

        public int getShapeType() {
            return this.shapeType;
        }

        public void setTargetView(View view) {
            this.targetView = view;
        }

        public void setArrowBitmap(Bitmap bitmap) {
            this.arrowBitmap = bitmap;
        }

        public void setTipBitmap(Bitmap bitmap) {
            this.tipBitmap = bitmap;
        }

        public void setPadding(int padding) {
            this.padding = padding;
        }

        public void setMargin(int margin) {
            this.margin = margin;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public void setShapeType(int shapeType) {
            this.shapeType = shapeType;
        }
    }

}
