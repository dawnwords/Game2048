package dawnwords.game2048.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dawnwords.game2048.R;

/**
 * Created by Dawnwords on 14-3-16.
 */
public class ResultView extends RelativeLayout {
    private TextView gameMessage;

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        gameMessage = new TextView(context);
        gameMessage.setTextSize(dimen(R.dimen.message_size));
        gameMessage.setGravity(Gravity.CENTER);
        gameMessage.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(gameMessage);
        reset();
    }

    public void win() {
        show(R.color.you_win, R.color.font_win, R.string.you_win);
    }

    public void lose() {
        show(R.color.game_over, R.color.font_basic, R.string.game_over);
    }

    public void reset() {
        setVisibility(GONE);
    }

    private void show(int bgColor, int textColor, int message) {
        gameMessage.setTextColor(color(textColor));
        gameMessage.setText(message);
        setBackgroundResource(bgColor);
        setVisibility(VISIBLE);
    }


    public int color(int id) {
        return getContext().getResources().getColor(id);
    }

    public int dimen(int id) {
        return (int) getContext().getResources().getDimension(id);
    }

}
