package com.project2022.emotiondiary;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private final int color;
    //private final Drawable drawable;

    public EventDecorator(Collection<CalendarDay> dates, int color){
        this.color = color;
        //drawable = context.getContext().getDrawable(R.drawable.ic_baseline_menu_book_24);
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
       //view.setSelectionDrawable(drawable);
    }
}
