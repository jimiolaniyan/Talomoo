// Generated code from Butter Knife. Do not modify!
package com.yoruba.talomoo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class CategoryActivity$$ViewBinder<T extends com.yoruba.talomoo.CategoryActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624029, "field 'll'");
    target.ll = finder.castView(view, 2131624029, "field 'll'");
    view = finder.findRequiredView(source, 2131624028, "field 'headerText'");
    target.headerText = finder.castView(view, 2131624028, "field 'headerText'");
    view = finder.findRequiredView(source, 2131624032, "method 'handleSettingsButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.handleSettingsButton();
        }
      });
    view = finder.findRequiredView(source, 2131624031, "method 'clickCallBacks'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.clickCallBacks(p2, p3);
        }
      });
  }

  @Override public void unbind(T target) {
    target.ll = null;
    target.headerText = null;
  }
}
