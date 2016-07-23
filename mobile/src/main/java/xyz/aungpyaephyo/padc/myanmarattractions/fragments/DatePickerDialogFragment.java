package xyz.aungpyaephyo.padc.myanmarattractions.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.DecimalFormat;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import xyz.aungpyaephyo.padc.myanmarattractions.events.DataEvent;

/**
 * Created by aung on 6/25/16.
 */
public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public enum ParentType {
        FragmentParent, ActivityParent
    }

    public static DatePickerDialogFragment newInstance(ParentType parentType) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();

        Bundle extras = new Bundle();
        extras.putSerializable("ParentType", parentType);
        fragment.setArguments(extras);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ParentType container = (ParentType) getArguments().getSerializable("ParentType");
        switch (container) {
            case FragmentParent:
                return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getTargetFragment(), year, month, day);
            default:
                return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
        // Create a new instance of DatePickerDialog and return it

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DecimalFormat df = new DecimalFormat("00");
        DataEvent.DatePickedEvent event = new DataEvent.DatePickedEvent(year + "/" + df.format(monthOfYear) + "/" + df.format(dayOfMonth));
//        EventBus.getDefault().post(event);
    }
}