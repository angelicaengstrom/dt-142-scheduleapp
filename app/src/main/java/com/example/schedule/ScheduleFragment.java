package com.example.schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ScheduleFragment extends Fragment {
    RecyclerView recyclerView;
    HomeActivity home;
    TextView selectedMonthTxt;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        home = (HomeActivity) getActivity();
        TextView labelSelectedDay = view.findViewById(R.id.txtCalenderView);
        recyclerView = view.findViewById(R.id.shiftRecyclerView);
        selectedMonthTxt = view.findViewById(R.id.labelCalenderView);

        SimpleDateFormat monthFormat = new SimpleDateFormat("d MMM");
        Calendar cal = Calendar.getInstance();
        String formatedDate = monthFormat.format(cal.getTime());
        ShiftRecyclerViewAdapter shiftRecyclerViewAdapter = new ShiftRecyclerViewAdapter(home, home.getShiftsAtDate(formatedDate));
        recyclerView.setAdapter(shiftRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(home));

        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMMM");
        labelSelectedDay.setText(dayFormat.format(cal.getTime()));

        SimpleDateFormat yearFormat = new SimpleDateFormat("MMMM yyyy");
        selectedMonthTxt.setText(yearFormat.format(cal.getTime()));

        //TEST
        CompactCalendarView compactCalendarView = view.findViewById(R.id.compactcalendar_view);

        setEvents(home.getComingShifts(), compactCalendarView);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = home.getApplicationContext();
                labelSelectedDay.setText(dayFormat.format(dateClicked));

                String date = monthFormat.format(dateClicked);
                ShiftRecyclerViewAdapter shiftRecyclerViewAdapter = new ShiftRecyclerViewAdapter(home, home.getShiftsAtDate(date));
                System.out.println(home.getShiftsAtDate(date).size());
                recyclerView.setAdapter(shiftRecyclerViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(home));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                selectedMonthTxt.setText(yearFormat.format(firstDayOfNewMonth));
            }
        });

        LinearLayout linearLayout = view.findViewById(R.id.layoutCalenderTxt);
        ImageView arrow = view.findViewById(R.id.arrowCalenderView);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compactCalendarView.getVisibility() == View.VISIBLE){
                    compactCalendarView.setVisibility(View.GONE);
                    arrow.animate().rotation(0).start();
                }else{
                    compactCalendarView.setVisibility(View.VISIBLE);
                    arrow.animate().rotation(180).start();
                }
            }
        });
        return view;
    }

    private void setEvents(ArrayList<Shift> shifts, CompactCalendarView compactCalendarView){
        Calendar c;
        int color = getResources().getColor(R.color.lateShift);
        for(Shift s : shifts){
            c = Calendar.getInstance();
            c.set(s.getDate().get(Calendar.YEAR), s.getDate().get(Calendar.MONTH), s.getDate().get(Calendar.DAY_OF_MONTH));
            if(s.getShift().contains("Kvällspass")){
                color = getResources().getColor(R.color.lateShift);
            }else{
                color = getResources().getColor(R.color.earlyShift);
            }
            Event e = new Event(color, c.getTimeInMillis());
            compactCalendarView.addEvent(e);
        }
    }
}