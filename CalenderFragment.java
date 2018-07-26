package com.example.afaf.inclcapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.afaf.inclcapp.helper_database.appointment_Model;
import com.example.afaf.inclcapp.helper_database.appointment_helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalenderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.EventClickListener {
    public static int appointmentID;
    appointment_helper db;
    private WeekView mWeekView;
    private ArrayList<WeekViewEvent> mNewEvents;

    // TODO: Rename and change types and number of parameters
    public static CalenderFragment newInstance(String param1, String param2) {
        CalenderFragment fragment = new CalenderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new appointment_helper(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeekView = (WeekView) getView().findViewById(R.id.weekView);

        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);

        mNewEvents = new ArrayList<WeekViewEvent>();
    }

    private ArrayList<WeekViewEvent> getNewEvents(int year, int month) {

        // Get the starting point and ending point of the given month. We need this to find the
        // events of the given month.
        Calendar startOfMonth = Calendar.getInstance();
        startOfMonth.set(Calendar.YEAR, year);
        startOfMonth.set(Calendar.MONTH, month - 1);
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);
        Calendar endOfMonth = (Calendar) startOfMonth.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);

//            Calendar startCal = Calendar.getInstance();
//            Calendar todayCal = Calendar.getInstance();
//        WeekViewEvent event1 = new WeekViewEvent(0, "yy", startCal, todayCal);
//        mNewEvents.add(event1);


        List<appointment_Model> modelList = db.getAllAppointmentsFuture("FUTURE");
        appointment_Model ap = null;


        for (int i = 0; i < modelList.size(); i++) {

            ap = modelList.get(i);
            System.out.println(ap.getStartTime());


            String clinic = ap.getClinic();
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {
                d = output.parse(ap.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // select time
            String ST = ap.getStartTime();
            Date datetime = null;
            try {
                datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ST);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newString = new SimpleDateFormat("H").format(datetime);
            int shour = Integer.parseInt(newString);

            String ET = ap.getEndTime();
            Date datetime1 = null;
            try {
                datetime1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ET);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newString1 = new SimpleDateFormat("H").format(datetime1);
            int ehour = Integer.parseInt(newString1);

            //  int duration = (int) Math.round(hst * 60)+het;
            Calendar startCal = Calendar.getInstance();
            Calendar todayCal = Calendar.getInstance();

            String noofcust = ap.getNumOfCustomer();
            appointmentID = ap.getId();

            Date date = d;

            startCal.setTime(date);
            startCal.add(Calendar.HOUR, shour);
            //   startCal.add(Calendar.MINUTE,het);
            Calendar endTime = (Calendar) startCal.clone();
            endTime.add(Calendar.HOUR, ehour - shour);

            WeekViewEvent event = new WeekViewEvent(0, clinic + '\n' + "NO. OF Customers: " + noofcust, startCal, endTime);
            event.setId(Long.valueOf(appointmentID));
            // Long.valueOf(appointmentID).longValue();
            //event.setLocation(appointmentID);


            if (todayCal.getTimeInMillis() > startCal.getTimeInMillis()) {
                event.setColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            mNewEvents.add(event);

        }


        // Find the events that were added by tapping on empty view and that occurs in the given
        // time frame.
        ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : mNewEvents) {
            if (event.getEndTime().getTimeInMillis() > startOfMonth.getTimeInMillis() &&
                    event.getStartTime().getTimeInMillis() < endOfMonth.getTimeInMillis()) {
                events.add(event);
            }
        }
        return events;
    }


    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        ArrayList<WeekViewEvent> newEvents = getNewEvents(newYear, newMonth);
        events.addAll(newEvents);
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        event.getId();
        MainActivity.fabflag = false;
        String clinicName = event.getName();
        Calendar starttime = event.getStartTime();
        StringTokenizer st = new StringTokenizer(clinicName, "\n");
        String s = null, ss = null, sss = null;
        List<appointment_Model> model = null;
        while (st.hasMoreTokens()) {
            s = st.nextToken();
            ss = st.nextToken();

        }
        String b = event.getLocation();
        Long l = event.getId();
        String x = Long.toString(l);
        int y = Integer.parseInt(x);

        model = db.geEventAppointment(s, y);

        if (model.size() != 0) {
            String appDate = model.get(0).getDate();
            String appDoctor = model.get(0).getDoctor();
            String appClinic = model.get(0).getClinic();
            String appStartTime = model.get(0).getStartTime();
            String appEndTime = model.get(0).getEndTime();
            String appNumOfCust = model.get(0).getNumOfCustomer();
            String appNumOfServedCust = model.get(0).getNumOfServedCustomer();
            String appStatus = model.get(0).getAppointmentStatus();

            Intent i = new Intent(getActivity(), appointment_activity.class);

            i.putExtra("id", model.get(0).getId() + "");
            i.putExtra("appDate", appDate);
            i.putExtra("appDoctor", appDoctor);
            i.putExtra("appClinic", appClinic);
            i.putExtra("appStartTime", appStartTime);
            i.putExtra("appEndTime", appEndTime);
            i.putExtra("appNumOfCust", appNumOfCust);
            i.putExtra("appNumOfServedCust", appNumOfServedCust);
            i.putExtra("appStatus", appStatus);

            // ------------------------------------------

            startActivity(i);
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
