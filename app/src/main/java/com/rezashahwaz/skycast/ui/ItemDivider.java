// ItemDivider.java
// RecyclerView decoration that draws the system list divider between items
package com.rezashahwaz.skycast.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class ItemDivider extends RecyclerView.ItemDecoration {
   private final Drawable divider;

   // constructor loads the built-in Android list divider
   ItemDivider(Context context) {
      TypedArray attributes =
         context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
      divider = attributes.getDrawable(0);
      attributes.recycle();
   }

   // draws a divider below every item but the last
   @Override
   public void onDrawOver(Canvas c, RecyclerView parent,
      RecyclerView.State state) {
      super.onDrawOver(c, parent, state);
      int left = parent.getPaddingLeft();
      int right = parent.getWidth() - parent.getPaddingRight();

      for (int i = 0; i < parent.getChildCount() - 1; ++i) {
         View item = parent.getChildAt(i);
         int top = item.getBottom() + ((RecyclerView.LayoutParams)
            item.getLayoutParams()).bottomMargin;
         divider.setBounds(left, top, right,
            top + divider.getIntrinsicHeight());
         divider.draw(c);
      }
   }
}
