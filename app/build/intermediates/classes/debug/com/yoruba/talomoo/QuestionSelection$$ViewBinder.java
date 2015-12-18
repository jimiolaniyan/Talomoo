// Generated code from Butter Knife. Do not modify!
package com.yoruba.talomoo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class QuestionSelection$$ViewBinder<T extends com.yoruba.talomoo.QuestionSelection> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624126, "field 'myToolbar'");
    target.myToolbar = finder.castView(view, 2131624126, "field 'myToolbar'");
  }

  @Override public void unbind(T target) {
    target.myToolbar = null;
  }
}
