package com.example.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.json.RequestAPI;
import com.example.schedule.json.Shift2;
import com.example.schedule.json.ShiftAPI;
import com.example.schedule.json.UpdateResponse;

import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * En klass som ärver av RecyclerView.Adapter
 * Innefattar metoder till hemsidans revyclerview adapter för att visa förfrågningar till användaren
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.MyViewHolder>{
    Context context;
    /**En lista av skift samt namn på arbetaren som skickat förfrågan
     * */
    List<Pair<String,Shift>> shifts;
    HTTPRequests httpRequests = HTTPRequests.getInstance();
    //Retrofitter<RequestAPI> requestAPIRetrofitter = new Retrofitter<>();

    /** Konstruktor som tilldelar skift och context
     */
    public RequestRecyclerViewAdapter(Context context, List<Pair<String,Shift>> shifts){
        this.context = context;
        this.shifts = shifts;
    }

    @NonNull
    @Override
    public RequestRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_request, parent, false);
        return new RequestRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecyclerViewAdapter.MyViewHolder holder, int position) {
        String hoursStr = shifts.get(position).second.getStartTime() + "\n" + shifts.get(position).second.getStopTime();
        holder.hours.setText(hoursStr);

        String shiftStr = shifts.get(position).second.getShift();
        holder.shift.setText(shiftStr);

        String dateStr = shifts.get(position).second.getDateString();
        holder.date.setText(dateStr);

        holder.name.setText(shifts.get(position).first);

        holder.accept.setId(shifts.get(position).second.getId());
        holder.decline.setId(shifts.get(position).second.getId());

        holder.accept.setOnClickListener(new AcceptRequestListener());

        holder.decline.setOnClickListener(new DeclineRequestListener());
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView hours, name, shift, date;
        ImageButton accept, decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hours = itemView.findViewById(R.id.txtRequestShiftHours);
            date = itemView.findViewById(R.id.txtRequestShiftDate);
            name = itemView.findViewById(R.id.txtRequestFrom);
            shift = itemView.findViewById(R.id.txtRequestShift);
            accept = itemView.findViewById(R.id.btnAcceptRequest);
            decline = itemView.findViewById(R.id.btnDeclineRequest);
        }
    }

    public class AcceptRequestListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            String userID = ((Activity) context).getIntent().getStringExtra("id");
            if(userID != null){
                httpRequests.acceptRequest(userID, view);
            }
        }
    }

    public class DeclineRequestListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String userID = ((Activity) context).getIntent().getStringExtra("id");
            if(userID != null){
                httpRequests.deleteRequest(userID, view);
            }
        }
    }
}
