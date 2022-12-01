package com.example.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import com.example.schedule.json.Request2;
import com.example.schedule.json.RequestAPI;
import com.example.schedule.json.ShiftAPI;
import com.example.schedule.json.UpdateResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * En klass som ärver av RecyclerView.Adapter
 * Innefattar metoder till kalendersidans recyclerview adapter för att visa arbetspass under ett visst datum
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ShiftRecyclerViewAdapter extends RecyclerView.Adapter<ShiftRecyclerViewAdapter.ShiftViewHolder> {
    Context context;
    /**En lista av skift samt namn på arbetaren
     * */
    List<Pair<String,Shift>> shifts;
    Retrofitter<RequestAPI> requestAPIRetrofitter = new Retrofitter<>();

    /** Konstruktor som tilldelar skift och context
     */
    public ShiftRecyclerViewAdapter(Context context, List<Pair<String,Shift>> shifts){
        this.context = context;
        this.shifts = shifts;
    }

    @NonNull
    @Override
    public ShiftRecyclerViewAdapter.ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_shift, parent, false);
        return new ShiftRecyclerViewAdapter.ShiftViewHolder(view);
    }

    /** Överskriver RecyclerView.Adapter metoden och sätter värden på ViewHolderns medlemmar
     * @param holder viewHolder
     * @param position index på elementen
     */
    @Override
    public void onBindViewHolder(@NonNull ShiftRecyclerViewAdapter.ShiftViewHolder holder, int position) {
        String hoursStr = shifts.get(position).second.getStartTime() + "\n" + shifts.get(position).second.getStopTime();
        holder.hours.setText(hoursStr);

        String shiftStr = shifts.get(position).second.getShift();
        holder.shift.setText(shiftStr);

        holder.name.setText(shifts.get(position).first);

        holder.date = shifts.get(position).second.getDateString();
        holder.shiftId = shifts.get(position).second.getId();

        String userID = ((Activity) context).getIntent().getStringExtra("id");
        if(shifts.get(position).second.getUserId().contains(userID) && shifts.get(position).second.hasNotPassed()){
            holder.swap.setVisibility(View.VISIBLE);
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.dark_orange));
            holder.linearLayout.setOnClickListener(new tradeShiftListener(holder));
        }else{
            holder.linearLayout.setBackgroundColor(shifts.get(position).second.isLate() ? context.getResources().getColor(R.color.lateShift) : context.getResources().getColor(R.color.earlyShift));
        }
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    /**
     * Klass som ärver av RecyclerView.ViewHolder och innefattar de element som ska erhållas i en RecyclerView
     */
    public static class ShiftViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView name, hours, shift;
        ImageView swap;
        String date;
        int shiftId;
        /**
         * Konstruktor av ViewHoldern
         * @param itemView vy över elementen som används
         */
        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            hours = itemView.findViewById(R.id.txtShiftHours);
            name = itemView.findViewById(R.id.txtShiftName);
            shift = itemView.findViewById(R.id.txtShift);
            linearLayout = itemView.findViewById(R.id.layoutShift);
            swap = itemView.findViewById(R.id.btnSwapShift);
        }
    }

    /**
     * En klass som implementerar interfacet View.OnClickListener
     */
    public class tradeShiftListener implements View.OnClickListener{
        private final ShiftViewHolder holder;
        /**
         * Lista över alla som inte arbetar utifrån de skift som finns
         */
        private final ArrayList<Staff> nonWorkers;

        /**
         * Konstruktor som tilldelar holder och nonWorkers
         * @param holder den specifika ViewHoldern
         */
        tradeShiftListener(ShiftViewHolder holder){
            this.holder = holder;
            int year = shifts.get(0).second.getDate(Calendar.YEAR);
            int month = shifts.get(0).second.getDate(Calendar.MONTH) + 1;
            int day = shifts.get(0).second.getDate(Calendar.DAY_OF_MONTH);
            nonWorkers = ((HomeActivity) context).getNonWorkingStaff(year + "-" + month + "-" + day);
        }

        /** Överskriver interfacets onClick metod som innefattar ett popup fönster ifall tryckt
         * @param view vyn som var tryckt.
         */
        @Override
        public void onClick(View view) {
            if(!nonWorkers.isEmpty()) {
                AlertDialog alertDialog = createAlertDialog();
                alertDialog.show();
            }else{
                Toast.makeText(context, "Alla jobbar denna dagen!", Toast.LENGTH_SHORT).show();
            }
        }

        /** Bygger ett popupfönster som möjliggör att användaren kan välja en ickearbetare att byta sitt pass med
         * @return popupfönstret
         */
        private AlertDialog createAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Du vill skicka");
            ViewGroup subView = (ViewGroup) ((Activity) context).getLayoutInflater().inflate(R.layout.card_view_shift, null, false);
            try{
                ((TextView)subView.findViewById(R.id.txtcardViewShiftDate)).setText(holder.date);
                ((TextView)subView.findViewById(R.id.cardViewShift)).setText(holder.shift.getText());
                ((TextView)subView.findViewById(R.id.cardViewShiftHours)).setText(holder.hours.getText());
                subView.findViewById(R.id.cardViewShiftHours).setBackground(holder.linearLayout.getBackground());
                subView.findViewById(R.id.cardViewLinLay).setBackground(holder.linearLayout.getBackground());

                ArrayAdapter<Staff> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, nonWorkers);
                ((Spinner)subView.findViewById(R.id.spinnerWorkers)).setAdapter(adapter);

                builder.setView(subView);

                builder.setPositiveButton("SKICKA", (dialogInterface, i) -> sendRequest(((Spinner)subView.findViewById(R.id.spinnerWorkers)).getSelectedItemPosition()));
                builder.setNegativeButton("AVBRYT", (dialogInterface, i) -> dialogInterface.cancel());
            }catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            return builder.create();
        }

        /** Skickar en förfrågan till den utvalda kollegan
         * @param currIndex position hämtat från popupfönstrets spinner
         */
        private void sendRequest(int currIndex) {
            String ssn = nonWorkers.get(currIndex).getSocialSecurityNumber();

            RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

            //TEMPORÄR

            UpdateResponse updateResponse = new UpdateResponse();
            updateResponse.setSsn(ssn);
            updateResponse.setId(holder.shiftId);

            Call<String> call = requestAPI.sendRequest(updateResponse);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    Toast.makeText(context, R.string.request_sent, Toast.LENGTH_SHORT).show();
                    System.out.println(response.body());

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }
    }
}
