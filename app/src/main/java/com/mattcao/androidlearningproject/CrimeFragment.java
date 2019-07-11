package com.mattcao.androidlearningproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mattcao.androidlearningproject.databinding.FragmentCrimeBinding;
import com.mattcao.androidlearningproject.entity.Crime;
import com.mattcao.androidlearningproject.entity.CrimeLab;
import com.mattcao.androidlearningproject.util.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String CRIME_ID = "CRIME_ID";
    private static final String CRIME_DIALOG = "CRIME_DIALOG";
    private static final int DATE_REQUEST_CODE = 0;
    private static final int PICK_CONTACT_CODE = 1;
    private static final int CRIME_PHOTO_CODE = 2;

    private Crime mCrime;
    private FragmentCrimeBinding mBinding;
    private File mCrimePhoto;

    public static Fragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    private String getCrimeReport() {
        String solvedString = getString(mCrime.isSolved() ? R.string.crime_report_solved : R.string.crime_report_unsolved);

        String dateString = DateUtil.formatDate(mCrime.getDate());

        String suspect = getString(mCrime.getSuspect() == null ? R.string.crime_report_no_suspect : R.string.crime_report_suspect);

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);
        mCrimePhoto = CrimeLab.getCrimeLab(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity())
                .updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);
        initUI();

        return mBinding.getRoot();
    }

    private void initUI() {
        PackageManager packageManager = getActivity().getPackageManager();

        mBinding.crimeTitleEditText.setText(mCrime.getTitle());

        final Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断设备上是否有储存照片的地方以及是否有照相应用
        boolean canTakePhoto = (mCrimePhoto != null && photoIntent.resolveActivity(packageManager) != null);
        mBinding.crimeCameraButton.setEnabled(canTakePhoto);
        mBinding.crimeCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.mattcao.androidlearningproject.fileprovider", mCrimePhoto);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(photoIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(photoIntent, CRIME_PHOTO_CODE);
            }
        });

        mBinding.crimeDateButton.setText(DateUtil.formatDate(mCrime.getDate()));
        mBinding.crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                if (manager != null) {
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, DATE_REQUEST_CODE);
                    dialog.show(manager, CRIME_DIALOG);
                }
            }
        });

        mBinding.crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mBinding.crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContactIntent, PICK_CONTACT_CODE);
            }
        });

        if (mCrime.getSuspect() != null) {
            mBinding.crimeSuspectButton.setText(mCrime.getSuspect());
        }

        if (packageManager.resolveActivity(pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mBinding.crimeSuspectButton.setEnabled(false);
        }

        mBinding.crimeSolvedCheckBox.setChecked(mCrime.isSolved());
        mBinding.crimeTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.crimeTitleTextView.setText(s.toString());
                mCrime.setTitle(s.toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == DATE_REQUEST_CODE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mBinding.crimeDateButton.setText(DateUtil.formatDate(mCrime.getDate()));
        } else if (requestCode == PICK_CONTACT_CODE) {
            Uri contactUri = data.getData();
            String[] queryField = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryField, null, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mBinding.crimeSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }
}

