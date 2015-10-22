package com.khevents.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.khevents.ui.CreateEventActivity;
import com.khevents.R;
import com.khevents.data.Event;
import com.khevents.network.RequestManager;
import com.utils.framework.collections.NavigationList;
import com.utilsframework.android.AndroidUtilities;
import com.utilsframework.android.fab.FloatingActionButton;
import com.utilsframework.android.view.DatePickerButton;
import com.utilsframework.android.view.KeyboardIsShownListener;

/**
 * Created by CM on 6/20/2015.
 */
public class AllEventsListFragment extends EventsListFragment {
    public static final int CREATE_EVENT = 1;
    private CheckBox dateFilterCheckbox;
    private DatePickerButton datePickerButton;
    private boolean datePickerButtonCheckedListenerCalled = false;
    private KeyboardIsShownListener keyboardIsShownListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dateFilterCheckbox = (CheckBox) view.findViewById(R.id.date_filter_checkbox);
        datePickerButton = (DatePickerButton) view.findViewById(R.id.filter_date);
        dateFilterCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!datePickerButtonCheckedListenerCalled) {
                    datePickerButton.performClick();
                    datePickerButtonCheckedListenerCalled = true;
                } else if (datePickerButton.getVisibility() == View.VISIBLE) {
                    updateNavigationListWithLastFilter();
                }

                datePickerButton.setVisibility(View.VISIBLE);
            }
        });

        datePickerButton.setOnDateChangedListener(new DatePickerButton.OnDateChangedListener() {
            @Override
            public void onDateChanged() {
                if (dateFilterCheckbox.isChecked()) {
                    updateNavigationListWithLastFilter();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.attachToListView(getListView());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtilities.startActivityForResult(AllEventsListFragment.this,
                        CreateEventActivity.class, CREATE_EVENT);
            }
        });

        keyboardIsShownListener = new KeyboardIsShownListener(getActivity()) {
            @Override
            protected void onKeyboardStateChanged(boolean isShown) {
                floatingActionButton.setVisibility(isShown ? View.INVISIBLE : View.VISIBLE);
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        keyboardIsShownListener.destroy();
    }

    @Override
    protected NavigationList<Event> getNavigationList(RequestManager requestManager, String filter) {
        if (!dateFilterCheckbox.isChecked()) {
            return requestManager.getEvents(filter);
        } else {
            long date = datePickerButton.getDate();
            return requestManager.getEvents(date, filter);
        }
    }

    @Override
    protected int getRootLayout() {
        return R.layout.events_list_fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_EVENT && resultCode == Activity.RESULT_OK) {
            updateNavigationListWithLastFilter();
        }
    }

    @Override
    protected boolean hasSearchMenu() {
        return true;
    }
}
