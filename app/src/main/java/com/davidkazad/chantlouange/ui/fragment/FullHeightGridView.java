package com.davidkazad.chantlouange.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * GridView qui se mesure en hauteur complète (toutes les lignes visibles),
 * permettant de le placer à l'intérieur d'un ScrollView ou NestedScrollView.
 */
public class FullHeightGridView extends GridView {

    public FullHeightGridView(Context context) {
        super(context);
    }

    public FullHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullHeightGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Forcer la mesure en hauteur illimitée → toutes les lignes sont affichées
        int expandedHeightSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandedHeightSpec);
    }
}
