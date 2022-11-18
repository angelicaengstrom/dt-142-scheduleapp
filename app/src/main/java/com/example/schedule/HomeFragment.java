package com.example.schedule;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    CardView firstShift;
    CardView secondShift;
    RecyclerView requestRecyclerView;
    HomeActivity home;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        home = (HomeActivity) getActivity();

        secondShift = view.findViewById(R.id.secondShift);
        firstShift = view.findViewById(R.id.firstShift);

        //TextView 'KOMMANDE PASS'
        TextView shiftLabel = view.findViewById(R.id.txtComingShift);
        if(home != null) {
            int shiftAmount = home.getComingShifts().size();
            String comingShiftLabel = shiftLabel.getText() + " (" + shiftAmount + ")";
            shiftLabel.setText(comingShiftLabel);

            //Har användaren bara ett skift:
            if(shiftAmount > 0) { //CARDVIEW 'firstShift'
                firstShift.setVisibility(View.VISIBLE);
                Shift first = home.getComingShifts().get(0);
                //ARBETSDATUM
                TextView date = view.findViewById(R.id.txtFirstShiftDate);
                date.setText(first.getDateString());
                //ARBETSTID
                TextView time = view.findViewById(R.id.txtFirstShiftHours);
                String timeTxt = first.getStartTime() + "\n" + first.getStopTime();
                time.setText(timeTxt);
                //ARBETSTYP
                TextView shift = view.findViewById(R.id.txtFirstShift);
                shift.setText(first.getShift());

                if(shiftAmount > 1){ //CARDVIEW 'secondShift'
                    secondShift.setVisibility(View.VISIBLE);
                    Shift second = home.getComingShifts().get(1);
                    //ARBETSDATUM
                    date = view.findViewById(R.id.txtSecondShiftDate);
                    date.setText(second.getDateString());
                    //ARBETSTID
                    time = view.findViewById(R.id.txtSecondShiftHours);
                    timeTxt = second.getStartTime() + "\n" + second.getStopTime();
                    time.setText(timeTxt);

                    //ARBETSTYP
                    shift = view.findViewById(R.id.txtSecondShift);
                    shift.setText(second.getShift());
                }
            }
        }

        String user = home.getIntent().getStringExtra("id");

        if (user != null) {
            requestRecyclerView = view.findViewById(R.id.requestRecyclerView);
            List<Pair<String,Shift>> requests = home.getRequestToUser(user);
            requestRecyclerView.setAdapter(new RequestRecyclerViewAdapter(home, requests));
            requestRecyclerView.setLayoutManager(new LinearLayoutManager(home));
            TextView requestLabelTxt = view.findViewById(R.id.txtRequests);
            String requestStr = requestLabelTxt.getText() + " (" + requests.size() + ")";
            requestLabelTxt.setText(requestStr);
        }
        return view;
    }
}