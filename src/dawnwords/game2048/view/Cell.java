package dawnwords.game2048.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dawnwords.game2048.MainActivity;
import dawnwords.game2048.R;

public class Cell extends RelativeLayout {

    private int x, y, value;
    private Handler handler;

    // action parameter
    private int moveX, moveY;
    private boolean shouldMerge;

    private static final int MOVE_TIME = 100;
    private static final long SHOW_TIME = 100;

    public Cell(Context context, int x, int y) {
        super(context);
        updatePosition(x, y);
    }

    public Cell(Context context, int x, int y, int value, Handler handler) {
        this(context, x, y);
        this.value = value;
        this.handler = handler;

        TextView content = new TextView(context);
        content.setText("" + value);
        content.setGravity(Gravity.CENTER);
        content.setTextSize(getDimen(R.dimen.font_size));
        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        content.setTextColor(getColor(value < 8 ? R.color.font_basic : R.color.font_merge));

        int size = getDimen(R.dimen.cell_size);
        LayoutParams params = new LayoutParams(size, size);
        params.leftMargin = getMarginLeft();
        params.topMargin = getMarginTop();
        setLayoutParams(params);
        setBackgroundColor(getColor(R.color.bg_2 + log(value)));
        setVisibility(INVISIBLE);
        addView(content);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell c = (Cell) obj;
        return c.x == x && c.y == y && c.value == value;
    }

    @Override
    public String toString() {
        return "Cell [x=" + x + ", y=" + y + ", value=" + value + ", moveX="
                + moveX + ", moveY=" + moveY + ", shouldMerge=" + shouldMerge
                + "]";
    }

    public void performAction() {
        if (shouldMove()) {
            Cell c = new Cell(getContext(), moveX, moveY);
            Animator animator = c.x == this.x ? ObjectAnimator.ofFloat(this,
                    "x", getMarginLeft(), c.getMarginLeft()) : ObjectAnimator
                    .ofFloat(this, "y", getMarginTop(), c.getMarginTop());
            animator.addListener(new AnimatorAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sendEndMessage(MainActivity.ACTION_END);
                }
            });
            animator.setDuration(MOVE_TIME).start();
        } else {
            sendEndMessage(MainActivity.ACTION_END);
        }
    }

    public void show() {
        setVisibility(VISIBLE);
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(this, "alpha", 0f, 1f));
        animator.addListener(new AnimatorAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sendEndMessage(MainActivity.SHOW_END);
            }
        });
        animator.setDuration(SHOW_TIME).start();
    }

    public boolean shouldMerge() {
        return shouldMerge;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int moveX() {
        return moveX;
    }

    public int moveY() {
        return moveY;
    }

    public int value() {
        return value;
    }

    public void setActionParams(int moveX, int moveY, boolean shouldMerge) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.shouldMerge = shouldMerge;
    }

    private int log(int value) {
        int result = -1;
        while (value > 0) {
            value >>= 1;
            result++;
        }
        return result;
    }

    private boolean shouldMove() {
        return shouldMerge || (x != moveX || y != moveY);
    }

    private void sendEndMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = this;
        handler.sendMessage(msg);
    }

    private int getDimen(int id) {
        return (int) getResources().getDimension(id);
    }

    private int getColor(int id) {
        return getResources().getColor(id);
    }

    private int getMarginLeft() {
        return (y + 1) * getDimen(R.dimen.cell_margin) + y
                * getDimen(R.dimen.cell_size);
    }

    private int getMarginTop() {
        return (x + 1) * getDimen(R.dimen.cell_margin) + x
                * getDimen(R.dimen.cell_size);
    }

    private class AnimatorAdapter implements AnimatorListener {
        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }
    }

}
