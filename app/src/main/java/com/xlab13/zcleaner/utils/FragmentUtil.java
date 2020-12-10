package com.xlab13.zcleaner.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xlab13.zcleaner.R;


public class FragmentUtil {
    private static void replaceFragment(FragmentManager manager, Fragment fragment,
                                        boolean addToBackStack, boolean useLeftRightAnimations) {
            FragmentTransaction fTrans;
            fTrans = manager.beginTransaction();
            if (useLeftRightAnimations) {
                //fTrans.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left, R.animator.enter_from_left, R.animator.exit_to_right);
            } else {
                fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }
            if (addToBackStack) {
                fTrans.addToBackStack(null);
            } else {
                try {
                    manager.popBackStackImmediate();
                } catch (IllegalStateException ignored){

                }
            }
            fTrans.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
            fTrans.commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager manager, Fragment fragment, boolean addToBackStack) {
        replaceFragment(manager, fragment, addToBackStack, false);
    }
}