package sapphyx.gsd.com.drywall.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import sapphyx.gsd.com.drywall.R;

public class PageIndicator extends View implements ViewPager.OnPageChangeListener {

    private int actualPosition;
    private float offset;
    private int size;
    private ViewPager viewPager;

    private IndicatorEngine engine;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        engine = new IndicatorEngine();

        engine.onInitEngine(this, context);
        size = 2;
    }

    public int getTotalPages() {
        return size;
    }

    public int getActualPosition() {
        return actualPosition;
    }

    public float getPositionOffset() {
        return offset;
    }

    public void notifyNumberPagesChanged() {
        size = viewPager.getAdapter().getCount();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        engine.onDrawIndicator(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(engine.getMeasuredWidth(widthMeasureSpec, heightMeasureSpec), engine.getMeasuredHeight(widthMeasureSpec, heightMeasureSpec));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        actualPosition = position;
        offset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * You must call this AFTER setting the Adapter for the ViewPager, or it won't display the right amount of points.
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        size = viewPager.getAdapter().getCount();
        invalidate();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }

    private static class IndicatorEngine {

        private Context context;

        private PageIndicator indicator;

        private Paint selectedPaint;
        private Paint unselectedPaint;

        public int getMeasuredHeight(int widthMeasuredSpec, int heightMeasuredSpec) {
            return getPixelsFromDp(context, 8);
        }

        public int getMeasuredWidth(int widthMeasuredSpec, int heightMeasuredSpec) {
            return getPixelsFromDp(context, 8 * (indicator.getTotalPages() * 2 - 1));
        }

        public void onInitEngine(PageIndicator indicator, Context context) {
            this.indicator = indicator;
            this.context = context;

            selectedPaint = new Paint();
            unselectedPaint = new Paint();

            selectedPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
            unselectedPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            selectedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            unselectedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }

        public void onDrawIndicator(Canvas canvas) {
            int height = indicator.getHeight();

            for(int i = 0; i < indicator.getTotalPages(); i++) {
                int radius;
                if(i == indicator.getActualPosition() + 1) {
                    radius = getPixelsFromDp(context, 4 * (1 - indicator.getPositionOffset()));
                } else if (i == indicator.getActualPosition()) {
                    radius = getPixelsFromDp(context, 4 * (indicator.getPositionOffset()));
                } else {
                    radius = getPixelsFromDp(context, 4);
                }
                int x = getPixelsFromDp(context, 4) + getPixelsFromDp(context, 16 * i);
                canvas.drawCircle(x, height/2, radius, unselectedPaint);
            }

            int firstX;
            int secondX;

            firstX = getPixelsFromDp(context, 4 + indicator.getActualPosition() * 16);

            if(indicator.getPositionOffset() > .5f) {
                firstX += getPixelsFromDp(context, 16 * (indicator.getPositionOffset() - .5f) * 2);
            }

            secondX = getPixelsFromDp(context, 4 + indicator.getActualPosition() * 16);

            if(indicator.getPositionOffset() < .5f) {
                secondX += getPixelsFromDp(context, 16 * indicator.getPositionOffset() * 2);
            } else {
                secondX += getPixelsFromDp(context, 16);
            }

            canvas.drawCircle(firstX, getPixelsFromDp(context, 4), getPixelsFromDp(context, 4), selectedPaint);
            canvas.drawCircle(secondX, getPixelsFromDp(context, 4), getPixelsFromDp(context, 4), selectedPaint);
            canvas.drawRect(firstX, 0, secondX, getPixelsFromDp(context, 8), selectedPaint);
        }
    }
}