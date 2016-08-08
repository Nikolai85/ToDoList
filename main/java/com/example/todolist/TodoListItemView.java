package com.example.todolist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;


public class TodoListItemView extends TextView{

    private Paint marginPaint;
    private Paint linePaint;
    private int paperColor;
    private float margin;

    public TodoListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TodoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TodoListItemView(Context context) {
        super(context);
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {
        Resources myResources = getResources();

        // Кисть для рисования
        marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        marginPaint.setColor(myResources.getColor(R.color.notepad_margin, null));
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(myResources.getColor(R.color.notepad_lines, null));

        // Цвет фона для листа и ширина кромки
        paperColor = myResources.getColor(R.color.notepad_paper, null);
        margin = myResources.getDimension(R.dimen.notepad_margin);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // Фоновый цвет для листа
        canvas.drawColor(paperColor);

        // Направляющие линии
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(),
                getMeasuredHeight(), linePaint);

        canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);

        canvas.save();
        canvas.translate(margin, 0);

        super.onDraw(canvas);
        canvas.restore();
    }
}
