package com.pcjz.lems.ui.workrealname;

import android.view.View;
import android.widget.AbsListView;

import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.base.BaseListAdapter;
import com.pcjz.lems.business.base.BaseListFragment;
import com.pcjz.lems.business.common.animator.CurtainAnimator;

/**
 * Created by ${YGP} on 2017/4/15.
 */

public class WorkbenchBaseListFragment extends BaseFragment {
    protected CurtainAnimator curtainAnimator;

    @Override
    protected void initView(View view) {
        if (curtainAnimator == null) {
            curtainAnimator = new CurtainAnimator();
        }
        curtainAnimator.init();
    }

    protected View getAnimatorView() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    /**
     * 窗帘式卷起和拉下
     *
     * @param isHideForceOpen
     */
    protected void updateView(boolean isHideForceOpen) {
        curtainAnimator.setViewAnimator(getAnimatorView());
        curtainAnimator.start(isHideForceOpen);
    }
}
