package com.example.heartmatch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.example.musicplayer.R;

/**
 * Circle Round Shape
 */

public class RoundImageView extends ImageView {
    /**
     * Type of image, circle and loop
     */
    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    public static final int TYPE_OVAL = 2;
    /**
     * color and the width of the side of the circle
     */
    private int mBorderColor;
    private float mBorderWidth;
    /**
     * Radius of circle
     */
    private float mCornerRadius;
    private float mLeftTopCornerRadius;
    private float mRightTopCornerRadius;
    private float mLeftBottomCornerRadius;
    private float mRightBottomCornerRadius;

    /**
     * Paint
     */
    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    /**
     * Radius
     */
    private float mRadius;
    /**
     * 3x3 Matrix to adjust the circle
     */
    private Matrix mMatrix;
    /**
     * color the circle
     */
    private BitmapShader mBitmapShader;
    /**
     * view width
     */
    private int mWidth;
    /**
     * Circle display area
     */
    private RectF mRoundRect;

    private Path mRoundPath;


    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);

        type = a.getInt(R.styleable.RoundImageView_type, TYPE_OVAL);
        mBorderColor = a.getColor(R.styleable.RoundImageView_border_color, Color.WHITE);
        mBorderWidth = a.getDimension(R.styleable.RoundImageView_border_width, 0);
        mCornerRadius = a.getDimension(R.styleable.RoundImageView_corner_radius, dp2px(10));
        mLeftTopCornerRadius = a.getDimension(R.styleable.RoundImageView_leftTop_corner_radius, 0);
        mLeftBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_leftBottom_corner_radius,0);
        mRightTopCornerRadius = a.getDimension(R.styleable.RoundImageView_rightTop_corner_radius,0);
        mRightBottomCornerRadius = a.getDimension(R.styleable.RoundImageView_rightBottom_corner_radius,0);

        a.recycle();

        init();

    }

    private void init() {
        mRoundPath = new Path();
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec));
            mRadius = mWidth / 2 - mBorderWidth/2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (type == TYPE_ROUND || type == TYPE_OVAL) {
            mRoundRect = new RectF(mBorderWidth/2, mBorderWidth/2, w - mBorderWidth/2, h - mBorderWidth/2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        if (type == TYPE_ROUND) {
            setRoundPath();

            canvas.drawPath(mRoundPath, mBitmapPaint);

            //paint edge
            canvas.drawPath(mRoundPath, mBorderPaint);
        } else if (type == TYPE_CIRCLE) {

            canvas.drawCircle(mRadius + mBorderWidth/2, mRadius + mBorderWidth/2, mRadius, mBitmapPaint);

            canvas.drawCircle(mRadius + mBorderWidth/2, mRadius + mBorderWidth/2, mRadius, mBorderPaint);

        } else {
            canvas.drawOval(mRoundRect, mBitmapPaint);

            canvas.drawOval(mRoundRect, mBorderPaint);
        }
    }


    private void setRoundPath() {
        mRoundPath.reset();

        /**
         *
         * set mCornerRadius
         */
        if (mLeftTopCornerRadius==0&&
                mLeftBottomCornerRadius==0&&
                mRightTopCornerRadius==0&&
                mRightBottomCornerRadius==0){

            mRoundPath.addRoundRect(mRoundRect,
                    new float[]{mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius,
                            mCornerRadius, mCornerRadius},
                    Path.Direction.CW);

        }else {
            mRoundPath.addRoundRect(mRoundRect,
                    new float[]{mLeftTopCornerRadius, mLeftTopCornerRadius,
                            mRightTopCornerRadius, mRightTopCornerRadius,
                            mRightBottomCornerRadius, mRightBottomCornerRadius,
                            mLeftBottomCornerRadius, mLeftBottomCornerRadius},
                    Path.Direction.CW);
        }

    }


    /**
     * initialize BitmapShader
     */
    private void setUpShader() {

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitamp(drawable);
        // paint bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
            // get bitmap width
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;
            //adjust pic and set it to the center of the circle
            float  dx = (bmp.getWidth()*scale - mWidth) / 2;
            float dy = (bmp.getHeight()*scale - mWidth) / 2;
            mMatrix.setTranslate(-dx, -dy);

        } else if (type == TYPE_ROUND || type == TYPE_OVAL) {

            if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())) {
                // adjust pic if size not fit
                scale = Math.max(getWidth() * 1.0f / bmp.getWidth(),
                        getHeight() * 1.0f / bmp.getHeight());
                float dx= (scale*bmp.getWidth()-getWidth())/2;
                float dy= (scale*bmp.getHeight()-getHeight())/2;
                mMatrix.setTranslate(-dx,-dy);
            }
        }
        mMatrix.preScale(scale, scale);

        mBitmapShader.setLocalMatrix(mMatrix);

        mBitmapShader.setLocalMatrix(mMatrix);
        // set shader
        mBitmapPaint.setShader(mBitmapShader);
    }


    /**
     * drawable to bitmap
     */
    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * set circle type
     *
     */
    public RoundImageView setType(int imageType) {
        if (this.type != imageType) {
            this.type = imageType;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE && this.type != TYPE_OVAL) {
                this.type = TYPE_OVAL;
            }
            requestLayout();
        }
        return this;
    }


    /**
     * set radius
     */
    public RoundImageView setCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }


    public RoundImageView setLeftTopCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mLeftTopCornerRadius != cornerRadius) {
            mLeftTopCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }


    public RoundImageView setRightTopCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mRightTopCornerRadius != cornerRadius) {
            mRightTopCornerRadius = cornerRadius;
            invalidate();
        }
        return this;
    }


    public RoundImageView setLeftBottomCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mLeftBottomCornerRadius != cornerRadius) {
            mLeftBottomCornerRadius = cornerRadius;
            invalidate();
        }
        return  this;
    }


    public RoundImageView setRightBottomCornerRadius(int cornerRadius) {
        cornerRadius = dp2px(cornerRadius);
        if (mRightBottomCornerRadius != cornerRadius) {
            mRightBottomCornerRadius = cornerRadius;
            invalidate();
        }

        return this;
    }


    /**
     * set edge width
     */
    public RoundImageView setBorderWidth(int borderWidth) {
        borderWidth = dp2px(borderWidth);
        if (mBorderWidth != borderWidth) {
            mBorderWidth = borderWidth;
            invalidate();
        }

        return  this;
    }

    /**
     * set edge color
     */
    public RoundImageView setBorderColor(int borderColor) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor;
            invalidate();
        }

        return this;
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
