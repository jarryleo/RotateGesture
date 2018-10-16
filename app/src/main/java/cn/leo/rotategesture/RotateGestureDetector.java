package cn.leo.rotategesture;

import android.view.MotionEvent;
import android.view.View;

/**
 * 旋转手势代理类
 *
 * @author : Jarry Leo
 * @date : 2018/10/16 11:25
 */
public class RotateGestureDetector implements View.OnTouchListener {
    /**
     * 是否开启惯性旋转(默认关闭)
     */
    private boolean mIsInertia;
    /**
     * 循环旋转(默认开启)
     */
    private boolean mIsCycle = true;
    /**
     * 当前旋转角度
     */
    private int mRotateAngle;
    /**
     * 角度偏移值
     */
    private int mOffsetAngle;
    /**
     * 设置起始角,非循环旋转有效
     */
    private int mStartAngle;
    /**
     * 设置结束角,非循环旋转有效
     */
    private int mEndAngle = 360;
    /**
     * 上次旋转角度
     */
    private int mLastAngle;
    /**
     * 是否正在旋转
     */
    private boolean mIsRotate;
    /**
     * 代理需要旋转的view
     * 也可以不设置代理view,只代理触摸事件获取旋转角度
     */
    private View mDetectorView;
    /**
     * 旋转角度回调
     */
    private OnRotateListener mOnRotateListener;


    public RotateGestureDetector(View detectorView) {
        mDetectorView = detectorView;
        mDetectorView.setOnTouchListener(this);
    }

    /**
     * 设置是否开启惯性旋转
     */
    public RotateGestureDetector setInertia(boolean inertia) {
        mIsInertia = inertia;
        return this;
    }

    /**
     * 设置是否开启循环转动
     */
    public RotateGestureDetector setCycle(boolean cycle) {
        mIsCycle = cycle;
        return this;
    }

    /**
     * 设置角度偏移
     */
    public RotateGestureDetector setOffsetAngle(int offsetAngle) {
        mOffsetAngle = offsetAngle;
        return this;
    }

    /**
     * 设置起始角度
     */
    public RotateGestureDetector setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        return this;
    }

    /**
     * 设置结束角度
     */
    public RotateGestureDetector setEndAngle(int endAngle) {
        mEndAngle = endAngle;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            return doOnePointerRotate(event);
        } else if (pointerCount == 2) {
            return doTwoPointerRotate(event);
        }
        return false;
    }

    /**
     * 一根手指绕中心点旋转
     */
    private boolean doOnePointerRotate(MotionEvent ev) {
        int pivotX = mDetectorView.getWidth() / 2;
        int pivotY = mDetectorView.getHeight() / 2;
        float deltaX = ev.getX(0) - pivotX;
        float deltaY = ev.getY(0) - pivotY;
        int degrees = (int) Math.round(Math.toDegrees(Math.atan2(deltaY, deltaX)));
        doEvent(ev, pivotX, pivotY, degrees);
        return true;
    }

    /**
     * 两根手指绕中心点旋转
     */
    private boolean doTwoPointerRotate(MotionEvent ev) {
        int pivotX = (int) (ev.getX(0) + ev.getX(1)) / 2;
        int pivotY = (int) (ev.getY(0) + ev.getY(1)) / 2;
        float deltaX = ev.getX(0) - ev.getX(1);
        float deltaY = ev.getY(0) - ev.getY(1);
        int degrees = (int) Math.round(Math.toDegrees(Math.atan2(deltaY, deltaX)));
        doEvent(ev, pivotX, pivotY, degrees);
        return true;
    }

    private void doEvent(MotionEvent ev, int pivotX, int pivotY, int degrees) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastAngle = degrees;
                mIsRotate = false;
                break;
            case MotionEvent.ACTION_UP:
                mIsRotate = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mLastAngle = degrees;
                mIsRotate = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                mIsRotate = false;
                upRotate(pivotX, pivotY);
                mLastAngle = degrees;
                break;
            case MotionEvent.ACTION_MOVE:
                mIsRotate = true;
                int degreesValue = degrees - mLastAngle;
                if (degreesValue > 45) {
                    rotate(-5, pivotX, pivotY);
                } else if (degreesValue < -45) {
                    rotate(5, pivotX, pivotY);
                } else {
                    rotate(degreesValue, pivotX, pivotY);
                }
                mLastAngle = degrees;
                break;
            default:
                break;
        }
    }

    /**
     * 实时旋转回调
     */
    private void rotate(int degree, int pivotX, int pivotY) {
        mRotateAngle += degree;
        if (mIsCycle) {
            if (mRotateAngle > 360) {
                mRotateAngle -= 360;
            } else if (mRotateAngle < 0) {
                mRotateAngle += 360;
            }
        } else {
            if (mRotateAngle < mStartAngle) {
                mRotateAngle = mStartAngle;
            } else if (mRotateAngle > mEndAngle) {
                mRotateAngle = mEndAngle;
            }
        }
        if (mOnRotateListener != null) {
            mOnRotateListener.onRotate(mRotateAngle + mOffsetAngle, pivotX, pivotY);
        }
    }

    /**
     * 手指抬起回调
     */
    private void upRotate(int pivotX, int pivotY) {
        //todo
    }

    public void setOnRotateListener(OnRotateListener onRotateListener) {
        mOnRotateListener = onRotateListener;
    }

    public interface OnRotateListener {
        /**
         * 旋转回调
         *
         * @param angle  旋转的角度
         * @param pivotX 旋转中心点x坐标
         * @param pivotY 旋转中心点y坐标
         */
        void onRotate(int angle, int pivotX, int pivotY);
    }

}
