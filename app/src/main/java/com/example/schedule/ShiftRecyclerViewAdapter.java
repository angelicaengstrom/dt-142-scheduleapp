package com.example.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<ShiftRecyclerViewAdapter.MyViewHolder> {
    Context context;
    List<Pair<String,Shift>> shifts;
    public ShiftRecyclerViewAdapter(Context context, List<Pair<String,Shift>>  shifts){
        this.context = context;
        this.shifts = shifts;
    }

    @NonNull
    @Override
    public ShiftRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate our layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_shift, parent, false);
        return new ShiftRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftRecyclerViewAdapter.MyViewHolder holder, int position) {
        //assigning values to the views we created in the recyvler_view_shift layout file
        //based on the position of the recycler view
        String hoursStr = shifts.get(position).second.getStartTime() + "\n" + shifts.get(position).second.getStopTime();
        holder.hours.setText(hoursStr);
        String shiftStr = shifts.get(position).second.getShift();
        holder.shift.setText(shiftStr);
        holder.date = shifts.get(position).second.getDateString();
        holder.id = shifts.get(position).second.getId();

        if(shiftStr == "Kvällspass"){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.lateShift));
        }else{
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.earlyShift));
        }
        String userID = ((Activity) context).getIntent().getStringExtra("id");

        //OM ANVÄNDAREN HAR PASS SKAPAS KNAPP
        if(shifts.get(position).second.getUserId().contains(userID) && shifts.get(position).second.hasNotPassed()){
            holder.swap.setVisibility(View.VISIBLE);
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.dark_orange));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Du vill skicka");
                    ViewGroup subView = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.card_view_shift, null, false);
                    ArrayList<Staff> nonWorkers = ((HomeActivity) context).getNonWorkingStaff(shifts);
                    try{
                        ((TextView)subView.findViewById(R.id.txtcardViewShiftDate)).setText(holder.date);
                        ((TextView)subView.findViewById(R.id.cardViewShift)).setText(holder.shift.getText());
                        ((TextView)subView.findViewById(R.id.cardViewShiftHours)).setText(holder.hours.getText());
                        ((TextView)subView.findViewById(R.id.cardViewShiftHours)).setBackground(holder.linearLayout.getBackground());
                        ((LinearLayout)subView.findViewById(R.id.cardViewLinLay)).setBackground(holder.linearLayout.getBackground());
                        if(!nonWorkers.isEmpty()){
                            ArrayAdapter<Staff> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, nonWorkers);
                            ((Spinner)subView.findViewById(R.id.spinnerWorkers)).setAdapter(adapter);
                            holder.idTo = ((Spinner)subView.findViewById(R.id.spinnerWorkers)).getSelectedItemPosition();
                            holder.ssnTo = nonWorkers.get(holder.idTo).getSocialSecurityNumber();

                            builder.setView(subView);

                            builder.setPositiveButton("SKICKA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.idTo = ((Spinner)subView.findViewById(R.id.spinnerWorkers)).getSelectedItemPosition();
                                    holder.ssnTo = nonWorkers.get(holder.idTo).getSocialSecurityNumber();
                                    String success = "Förfrågan skickad från ShiftID: " + holder.id + " to " + holder.ssnTo;
                                    Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("AVBRYT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog ad = builder.create();
                            ad.show();
                        }else{
                            Toast.makeText(context, "Alla jobbar denna dagen!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            });
        }
        holder.name.setText(shifts.get(position).first);
    }

    @Override
    public int getItemCount() {
        //recyclerview wants to know number of items in total you want to displayed
        return shifts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //grabbing the views from our recycler_view_shift layout file
        //Kinda like the onCreate method
        TextView hours, name, shift;
        String date;
        int id;
        String ssnTo;
        int idTo;
        LinearLayout linearLayout;
        ImageView swap;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hours = itemView.findViewById(R.id.txtShiftHours);
            name = itemView.findViewById(R.id.txtShiftName);
            shift = itemView.findViewById(R.id.txtShift);
            linearLayout = itemView.findViewById(R.id.layoutShift);
            swap = itemView.findViewById(R.id.btnSwapShift);
        }
    }
}
