package com.mobdeve.s15.group8.mobdeve_mp.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ExpandableListView

/*
* FIXME: Possibly not great for performance :(
* Hello may alam ba kayong way para magfit ng heterogenous scrollable content na kasama
* yung recyclerview/listview/expandablelistview huhu pasabi nalang if ever thank youu
* */
class UnscrollableExpandableListView : ExpandableListView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val customHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, customHeightMeasureSpec)
        val params = layoutParams
        params.height = measuredHeight
    }
}


