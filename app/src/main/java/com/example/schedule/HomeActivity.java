package com.example.schedule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;

import com.example.schedule.databinding.ActivityHomeBinding;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    Fragment homeFragment;
    Fragment scheduleFragment;
    Fragment moreFragment;
    public String amountOfShifts;
    private final ArrayList<Shift> comingShifts = new ArrayList<>();
    private final ArrayList<Shift> allShifts = new ArrayList<>();
    private final HashMap<String, Staff> staff = new HashMap<>();
    private final ArrayList<Request> request = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        homeFragment = new HomeFragment();
        scheduleFragment = new ScheduleFragment();
        moreFragment = new MoreFragment();

        replaceFragment(homeFragment);

        //Hämta inloggad användare
        String user = getIntent().getStringExtra("id");
        if (user != null) {
            insertAllShifts();
            insertComingUserShifts(user);
            insertStaff();
            amountOfShifts = Integer.toString(comingShifts.size());
            insertRequest();
        }else{
            System.out.println("FAIL");
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(homeFragment);
                    break;
                case R.id.schedule:
                    replaceFragment(scheduleFragment);
                    break;
                case R.id.more:
                    replaceFragment(moreFragment);
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public ArrayList<Shift> getComingShifts(){
        return comingShifts;
    }

    public ArrayList<Shift> getAllShifts() { return allShifts; }

    public HashMap<String, Staff> getStaff() { return staff; }

    private void insertAllShifts(){
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 14);
        Shift s3 = new Shift(0, c, LocalTime.of(16,0), LocalTime.of(23,0), "199905063641");
        allShifts.add(s3);
        c = Calendar.getInstance();
        c.set(2022, 10, 20);
        s3 = new Shift(1, c, LocalTime.of(16,0), LocalTime.of(23,0), "199905063641");
        allShifts.add(s3);
        c = Calendar.getInstance();
        c.set(2022, 10, 21);
        s3 = new Shift(2, c, LocalTime.of(11,0), LocalTime.of(14,0), "200001011337");
        allShifts.add(s3);
        s3 = new Shift(3, c, LocalTime.of(16,0), LocalTime.of(23,0), "199905063641");
        allShifts.add(s3);
        s3 = new Shift(4, c, LocalTime.of(16,0), LocalTime.of(23,0), "200001010337");
        allShifts.add(s3);
        s3 = new Shift(5, c, LocalTime.of(16,0), LocalTime.of(23,0), "200101010337");
        allShifts.add(s3);
        c = Calendar.getInstance();
        c.set(2022, 10, 23);
        s3 = new Shift(6, c, LocalTime.of(11,0), LocalTime.of(14,0), "200101010337");
        allShifts.add(s3);
        c = Calendar.getInstance();
        c.set(2022, 10, 21);
        s3 = new Shift(7, c, LocalTime.of(16,0), LocalTime.of(23,0), "200201010337");
        allShifts.add(s3);
        c = Calendar.getInstance();
        c.set(2022, 10, 23);
        s3 = new Shift(8, c, LocalTime.of(11,0), LocalTime.of(14,0), "199905063641");
        allShifts.add(s3);
    }

    private void insertStaff(){
        Staff s1 = new Staff("199905063641", "Angelica Engström", "Angelica.princess@hotmail.com", "0701906338");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("200001011337", "Samuel Greenberg", "Sam.gre@hotmail.com", "07013374200");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("200001010337", "Can Kupeli", "Canku@hotmail.com", "0708883322");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("200101010337", "Marcus Jakobsson", "marjo@hotmail.com", "0708223322");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("200201010337", "Eric Johansson", "errjo@hotmail.com", "0702223377");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("200301010337", "Nikki Wolgers", "nikki@hotmail.com", "0734938271");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("199901010337", "Joel Viggesjö", "jovig@hotmail.com", "0704938271");
        staff.put(s1.getSocialSecurityNumber(), s1);
        s1 = new Staff("199801010337", "Alexander Frid", "frid@hotmail.com", "0763928329");
        staff.put(s1.getSocialSecurityNumber(), s1);
    }

    private void insertComingUserShifts(String user){
        for(Shift s : allShifts){
            if(s.hasNotPassed() && s.getUserId().contains(user)){
                comingShifts.add(s);
            }
        }
    }

    public List<Pair<String,Shift>> getShiftsAtDate(String date){
        List<Pair<String,Shift>> temp = new ArrayList<>();
        for(Shift s : allShifts){
            if(s.getDateString().contains(date)){
                String name = staff.get(s.getUserId()).getName();
                temp.add(new Pair<>(name, s));
            }
        }
        return temp;
    }

    private void insertRequest(){
        Request r1 = new Request("199905063641",4);
        request.add(r1);
        Request r2 = new Request("200201010337", 3);
        request.add(r2);
        /*
        Request r3 = new Request("199905063641", 5);
        request.add(r3);*/
    }

    public List<Pair<String,Shift>> getRequestToUser(String user){
        List<Pair<String,Shift>> temp = new ArrayList<>();
        for(Request r : request){
            if(r.getUserId().contains(user)){
                Shift s = allShifts.get(r.getShiftId());
                if(s.hasNotPassed()) {
                    String name = staff.get(s.getUserId()).getName();
                    temp.add(new Pair<>(name, s));
                }
            }
        }
        return temp;
    }

    public ArrayList<Staff> getNonWorkingStaff(List<Pair<String,Shift>> shifts){
        ArrayList<Staff> temp = new ArrayList<>(staff.values());
        for(Pair<String, Shift> p : shifts){
            for(Staff s : temp){
                if(p.first.contains(s.getName())){
                    temp.remove(s);
                    break;
                }
            }
        }
        return temp;
    }

    public Staff getStaffWithSSN(String ssn){
        return staff.get(ssn);
    }
}