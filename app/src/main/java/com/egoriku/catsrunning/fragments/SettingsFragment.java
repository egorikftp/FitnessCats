package com.egoriku.catsrunning.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.DebugApplication;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.models.Firebase.UserInfo;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.USER_INFO;

public class SettingsFragment extends Fragment implements ValueEventListener {

    private static final String KEY_EDIT_MODE = "key_edit_mode";

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText growth;
    private EditText weight;
    private EditText age;
    private TextView userName;
    private TextView userEmail;

    private boolean isEditMode;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isEditMode = savedInstanceState.getBoolean(KEY_EDIT_MODE);
            changeEditMode();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        setHasOptionsMenu(true);

        growth = (EditText) view.findViewById(R.id.settings_growth);
        weight = (EditText) view.findViewById(R.id.settings_weight);
        age = (EditText) view.findViewById(R.id.settings_age);
        userName = (TextView) view.findViewById(R.id.settings_user_name);
        userEmail = (TextView) view.findViewById(R.id.settings_user_email);

        final DebugApplication.TogglableHeapDumper heapDumper = ((DebugApplication) getActivity().getApplicationContext()).getDumper();
        final Button leakCanary = (Button) view.findViewById(R.id.leak_canary);
        leakCanary.setBackgroundColor(heapDumper.isEnabled() ? ContextCompat.getColor(getContext(), R.color.primary_dark) : ContextCompat.getColor(getContext(), R.color.accent));
        leakCanary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leakCanary.setBackgroundColor(heapDumper.toggle() ? ContextCompat.getColor(getContext(), R.color.primary_dark) : ContextCompat.getColor(getContext(), R.color.accent));
            }
        });

        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }

        FirebaseUtils.getDatabaseReference()
                .child(USER_INFO)
                .child(user.getUid())
                .addListenerForSingleValueEvent(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, R.id.edit_user_info, Menu.NONE, "").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem likedItem = menu.findItem(R.id.edit_user_info);
        likedItem.setIcon(isEditMode
                ? AppCompatResources.getDrawable(getContext(), R.drawable.ic_vec_check_ready)
                : AppCompatResources.getDrawable(getContext(), R.drawable.ic_vec_edit_user_info));
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_user_info:
                if (isEditMode) {
                    FirebaseUtils.saveUserInfo(getUserInfo(), getContext());
                }
                isEditMode = !isEditMode;
                invalidateOptionsMenu(getActivity());
                changeEditMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private UserInfo getUserInfo() {
        int growthInfo = 0;
        int weightInfo = 0;
        int ageInfo = 0;

        try {
            growthInfo = Integer.parseInt(growth.getText().toString().trim());
            weightInfo = Integer.parseInt(weight.getText().toString().trim());
            ageInfo = Integer.parseInt(age.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.activity_add_user_info_not_digit), Toast.LENGTH_LONG).show();
        }

        return new UserInfo(ageInfo, growthInfo, weightInfo);
    }

    private void changeEditMode() {
        if (isEditMode) {
            ((TracksActivity) getActivity()).animateToolbar(
                    R.color.settings_toolbar_color2,
                    R.color.settings_toolbar_color_dark2);
        } else {
            ((TracksActivity) getActivity()).animateToolbar(
                    R.color.settings_toolbar_color,
                    R.color.settings_toolbar_color_dark);
        }

        age.setEnabled(isEditMode);
        growth.setEnabled(isEditMode);
        weight.setEnabled(isEditMode);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
        if (userInfo == null) {
            growth.setText(0);
            weight.setText(0);
            age.setText(0);
        } else {
            growth.setText(String.valueOf(userInfo.getGrowth()));
            weight.setText(String.valueOf(userInfo.getWeight()));
            age.setText(String.valueOf(userInfo.getAge()));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Timber.d(databaseError.getMessage());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_EDIT_MODE, isEditMode);
    }
}
