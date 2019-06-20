package com.mattcao.androidlearningproject.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mattcao.androidlearningproject.R;
import com.mattcao.androidlearningproject.databinding.FragmentCrimeBinding;
import com.mattcao.androidlearningproject.entity.Crime;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private FragmentCrimeBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);

        mBinding.crimeDateButton.setText(mCrime.getDate().toString());
        mBinding.crimeDateButton.setEnabled(false);

        mBinding.crimeTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.crimeTitleTextView.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.crimeSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return mBinding.getRoot();
    }
}
