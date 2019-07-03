package com.mattcao.androidlearningproject;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mattcao.androidlearningproject.databinding.FragmentCrimeListBinding;
import com.mattcao.androidlearningproject.entity.Crime;
import com.mattcao.androidlearningproject.entity.CrimeLab;
import com.mattcao.androidlearningproject.util.DateUtil;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private static final String SUBTITLE_VISIBLE = "SUBTITLE_VISIBLE";

    private RecyclerView mRecyclerView;
    private FragmentCrimeListBinding mBinding;
    private CrimeAdapter mCrimeAdapter;
    private boolean mSubtitleVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
        mRecyclerView = mBinding.recyclerCrimeView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SUBTITLE_VISIBLE);
        }
        updateUI();
        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem item = menu.findItem(R.id.show_subtitle);
        item.setTitle(mSubtitleVisible ? "HIDE SUBTITLE" : "SHOW SUBTITLE");
    }

    private void updateTitle() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int count = crimeLab.getCrimes().size();

        //与设备语言设置有关，需要把设备设置成英语或其他才能正常显示
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, count, count);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().setSubtitle(subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimes.size() == 0) {
            mBinding.noRecordsTextView.setVisibility(View.VISIBLE);
            mBinding.recyclerCrimeView.setVisibility(View.GONE);
        } else {
            mBinding.noRecordsTextView.setVisibility(View.GONE);
            mBinding.recyclerCrimeView.setVisibility(View.VISIBLE);
            if (mCrimeAdapter == null) {
                mCrimeAdapter = new CrimeAdapter(crimes);
                mRecyclerView.setAdapter(mCrimeAdapter);
            } else {
                mCrimeAdapter.setCrimes(crimes);
                mCrimeAdapter.notifyDataSetChanged();
            }
        }
        updateTitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
        private ImageView mCrimeSolvedImageView;
        private TextView mTitleTextView;
        private TextView mDateTextView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            //mListItemCrimeBinding = DataBindingUtil.inflate(inflater, R.layout.list_item_crime, parent, false);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved_image_view);
            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.delete_crime_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                    updateUI();
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            //mListItemCrimeBinding.crimeDate.setText(mCrime.getDate().toString());
            //mListItemCrimeBinding.crimeTitle.setText(mCrime.getTitle());
            mTitleTextView.setText(mCrime.getTitle());

            mDateTextView.setText(DateUtil.formatDate(mCrime.getDate()));

            mCrimeSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
