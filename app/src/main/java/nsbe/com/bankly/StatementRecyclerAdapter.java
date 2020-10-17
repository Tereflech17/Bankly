package nsbe.com.bankly;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nsbe.com.bankly.model.CapitalPurchase;

/**
 * Created by Charlton on 11/11/17.
 */

public class StatementRecyclerAdapter extends RecyclerView.Adapter<StatementRecyclerAdapter.StatementHolder> {

    ArrayList<CapitalPurchase> purchases = new ArrayList<>();

    @Override
    public StatementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_statement_view, parent, false));
    }

    @Override
    public void onBindViewHolder(StatementHolder holder, int position) {
        holder.description.setText(purchases.get(position).getDescription());
        holder.status.setText(purchases.get(position).getStatus());
        holder.date.setText(purchases.get(position).getPurchase_date());
    }

    public void setData(List<CapitalPurchase> data){
        int size = purchases.size();
        purchases.addAll(data);
        notifyItemRangeInserted(size, purchases.size());
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    public class StatementHolder extends RecyclerView.ViewHolder {
        AppCompatTextView status, date, description;

        public StatementHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
        }
    }
}
