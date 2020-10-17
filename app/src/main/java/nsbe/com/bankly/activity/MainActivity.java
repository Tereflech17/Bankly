package nsbe.com.bankly.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.LinearSort;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import nsbe.com.bankly.Bankly;
import nsbe.com.bankly.BuildConfig;
import nsbe.com.bankly.R;
import nsbe.com.bankly.model.CapitalPurchase;
import nsbe.com.bankly.model.CapitalPurchaseRequest;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private String account_id;
    private double total_balance = 0;
    private double total_spent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout layout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(item -> {
            layout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.action_analytics:
                    Intent intent = new Intent(this, AnalyticsActivity.class);
                    Observable.just(intent).subscribeOn(Schedulers.io())
                            .delay(250, TimeUnit.MILLISECONDS)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::startActivity, error -> {
                            });
                    break;
                case R.id.action_statements:
                    intent = new Intent(this, StatementActivity.class);
                    Observable.just(intent).subscribeOn(Schedulers.io())
                            .delay(250, TimeUnit.MILLISECONDS)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::startActivity, error -> {
                            });
                    break;
            }
            return false;
        });
        layout.addDrawerListener(toggle = new ActionBarDrawerToggle(this, layout, toolbar, R.string.accounts, R.string.accounts));
        toggle.syncState();

        Bankly.getService().getCustomer(BuildConfig.WILBERT)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    AppCompatTextView name = navigationView.getHeaderView(0).findViewById(R.id.name);
                    name.setText(String.format(Locale.US, "%s %s", res.getFirst_name(), res.getLast_name()));
                }, error -> {
                });

        Bankly.getService().getAccounts(BuildConfig.WILBERT)
                .subscribeOn(Schedulers.io())
                .map(res -> {
                    ArrayList<CapitalPurchase> capitalPurchases = Bankly.getService().getPurchases(res.get(0).get_id()).blockingFirst();
                    StreamSupport.stream(capitalPurchases).filter(filter -> filter.getAmount() > 0 && filter.getStatus().equals("executed")).map(CapitalPurchase::getAmount).forEach(d -> {
                        total_spent += d;
                    });
                    return res;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    AppCompatTextView rewards = navigationView.getHeaderView(0).findViewById(R.id.rewards);
                    rewards.setText(res.get(0).getRewards());
                    AppCompatTextView balance = findViewById(R.id.balance);
                    balance.setText(res.get(0).getBalance());
                    account_id = res.get(0).get_id();
                    total_balance = res.get(0).getTotalBalance();
                }, error -> {
                }, this::initializePie);
        Animator spruceAnimator = new Spruce
                .SpruceBuilder((ViewGroup) findViewById(R.id.cardview).getParent())
                .sortWith(new LinearSort(/*interObjectDelay=*/500L, /*reversed=*/false, LinearSort.Direction.TOP_TO_BOTTOM))
                .animateWith(DefaultAnimations.shrinkAnimator(findViewById(R.id.cardview), /*duration=*/800))
                .start();
    }

    PieChart mChart;

    public void initializePie() {
        mChart = findViewById(R.id.bar_chart);

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });


        setData(3, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setEntryLabelTextSize(12f);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                new IntentIntegrator(this).initiateScan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.e("Scanned", result.getContents());
                Bankly.getUpc().getUPCModel(result.getContents())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(res -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirmation")
                                    .setMessage(String.format(Locale.US, "Do you want to buy \"%s\" for $%.2f", res.getName(), (double) res.getPrice() / 100))
                                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                                        Toast.makeText(this, "MMK Bought", Toast.LENGTH_LONG).show();
                                        Bankly.getService().makePurchase(account_id, CapitalPurchaseRequest.create(res)).subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(response -> {

                                                    new AlertDialog.Builder(this)
                                                            .setTitle("Response")
                                                            .setMessage(response.getMessage())
                                                            .setPositiveButton("Yes", null).show();

                                                    dialogInterface.dismiss();

                                                }, error -> {
                                                });
                                    })
                                    .setNegativeButton("No", (dialogInterface, i) -> {
                                        Toast.makeText(this, "MMK Not Bought", Toast.LENGTH_LONG).show();
                                    }).show();
                        }, error -> {
                            Toast.makeText(this, "We could not find this data", Toast.LENGTH_LONG).show();
                        });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    protected String[] mParties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };


    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        //for (int i = 0; i < count; i++) {
            //entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5), mParties[i % mParties.length], getResources().getDrawable(R.drawable.ic_if_close)));
            //entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5), mParties[i % mParties.length], getResources().getDrawable(R.drawable.ic_if_close)));
        //}

        entries.add(new PieEntry((float) (total_balance / 50000), "Balance"));
        entries.add(new PieEntry((float) (total_spent / 50000), "Spent"));
        entries.add(new PieEntry((float) ((50000 - (total_balance + total_spent)) / 50000), "Your Goal"));


        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Bankly\ndeveloped by Bankly Team");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 6, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 6, s.length() - 7, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 6, s.length() - 7, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 6, s.length() - 7, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 17, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 11, s.length(), 0);
        return s;
    }


}
