package nsbe.com.bankly.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import nsbe.com.bankly.Bankly;
import nsbe.com.bankly.BuildConfig;
import nsbe.com.bankly.R;
import nsbe.com.bankly.StatementRecyclerAdapter;
import nsbe.com.bankly.model.CapitalPurchase;

public class StatementActivity extends AppCompatActivity {

    private StatementRecyclerAdapter adapter;
    double total_spent = 0;
    String total_balance = "$0.00";
    private Animator spruceAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        Toolbar toolbar = findViewById(R.id.toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        new Spruce.SpruceBuilder(findViewById(R.id.cardview))
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(findViewById(R.id.cardview), 800),
                        ObjectAnimator.ofFloat(findViewById(R.id.cardview), "translationX", -findViewById(R.id.cardview).getWidth(), 0f).setDuration(800))
                .start();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                initSpruce();
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter = new StatementRecyclerAdapter());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Bankly.getService().getAccounts(BuildConfig.WILBERT)
                .subscribeOn(Schedulers.io())
                .flatMap(res -> {
                    total_balance = res.get(0).getBalance();
                    return Bankly.getService().getPurchases(res.get(0).get_id());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    adapter.setData(res);
                    total_spent = 0;
                    StreamSupport.stream(res).filter(filter -> filter.getAmount() > 0 && filter.getStatus().equals("executed")).map(CapitalPurchase::getAmount).forEach(d -> {
                        total_spent += d;
                    });
                }, error -> {

                }, () -> {
                    AppCompatTextView balance = findViewById(R.id.balance);
                    AppCompatTextView spent = findViewById(R.id.spent);
                    balance.setText(total_balance);
                    spent.setText(String.format(Locale.US, "$%.2f", total_spent));
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSpruce() {
        spruceAnimator = new Spruce.SpruceBuilder(findViewById(R.id.recycler_view))
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(findViewById(R.id.recycler_view), 800),
                        ObjectAnimator.ofFloat(findViewById(R.id.recycler_view), "translationX", -findViewById(R.id.recycler_view).getWidth(), 0f).setDuration(800))
                .start();
    }
}
