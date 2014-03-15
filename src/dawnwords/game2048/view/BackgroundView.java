package dawnwords.game2048.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import dawnwords.game2048.MainActivity;
import dawnwords.game2048.R;

/**
 * Created by Dawnwords on 14-3-16.
 */
public class BackgroundView extends RelativeLayout {
    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i <= MainActivity.BOARD_SIZE; i++) {
            addLine(i, false);
            addLine(i, true);
        }
    }


    private void addLine(int i, boolean isVertical) {
        int margin = getDimen(R.dimen.cell_margin);
        int size = getDimen(R.dimen.cell_size);

        View view = new View(getContext());
        view.setBackgroundColor(getColor(R.color.dark));
        view.setLayoutParams(isVertical ? verticalLayoutParam(i, margin, size) : horizontalLayoutParam(i, margin, size));
        addView(view);
    }

    private LayoutParams verticalLayoutParam(int i, int margin, int size) {
        LayoutParams params = new LayoutParams(margin, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = (margin + size) * i;
        params.topMargin = 0;
        return params;
    }

    private LayoutParams horizontalLayoutParam(int i, int margin, int size) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, margin);
        params.leftMargin = 0;
        params.topMargin = (margin + size) * i;
        return params;
    }


    private int getColor(int id) {
        return getResources().getColor(id);
    }

    private int getDimen(int id) {
        return (int) getResources().getDimension(id);
    }
}
