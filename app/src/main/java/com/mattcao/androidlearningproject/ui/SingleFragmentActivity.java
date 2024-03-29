package com.mattcao.androidlearningproject.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.mattcao.androidlearningproject.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
        }

        //解决旋转屏幕时报错： java.lang.IllegalStateException: Fragment already added
        if (!fragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
