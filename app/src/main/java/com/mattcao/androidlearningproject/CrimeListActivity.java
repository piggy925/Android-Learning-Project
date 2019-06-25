package com.mattcao.androidlearningproject;

import android.support.v4.app.Fragment;

import com.mattcao.androidlearningproject.ui.SingleFragmentActivity;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
