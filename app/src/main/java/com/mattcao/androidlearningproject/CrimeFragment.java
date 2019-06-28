package com.mattcao.androidlearningproject;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mattcao.androidlearningproject.databinding.FragmentCrimeBinding;
import com.mattcao.androidlearningproject.entity.Crime;
import com.mattcao.androidlearningproject.entity.CrimeLab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String CRIME_ID = "CRIME_ID";
    private static final String CRIME_DIALOG = "CRIME_DIALOG";

    private Crime mCrime;
    private FragmentCrimeBinding mBinding;

    public static Fragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);

        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        mBinding.crimeTitleEditText.setText(mCrime.getTitle());

        SimpleDateFormat df = new SimpleDateFormat("E, MM dd, yyyy");
        mBinding.crimeDateButton.setText(df.format(mCrime.getDate()));
        mBinding.crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                if (manager != null) {
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.show(manager, CRIME_DIALOG);
                }
            }
        });

        mBinding.crimeSolvedCheckBox.setChecked(mCrime.isSolved());
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
    }
}
