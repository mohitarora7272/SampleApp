package com.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sample.adapters.ItemAdapter;
import com.sample.modals.ItemPOJO;
import com.sample.prefrence.AppPreferencesHelper;
import com.sample.utils.AppConstants;
import com.sample.utils.RecyclerItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.btnAddItem)
    FloatingActionButton btnAddItem;

    @BindView(R.id.tvNoResult)
    TextView tvNoResult;

    private ArrayList<ItemPOJO> itemList;
    private AppPreferencesHelper appPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        appPreferencesHelper = new AppPreferencesHelper(this, AppConstants.PREF_NAME);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnAddItem.getVisibility() == View.VISIBLE) {
                    btnAddItem.hide();
                } else if (dy < 0 && btnAddItem.getVisibility() != View.VISIBLE) {
                    btnAddItem.show();
                }
            }
        });

        setOnItemClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemList = getItemList();
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        Log.e("ss", "ss>>" + itemList.size());
        setAdapter();
    }

    // Row click listener
    private void setOnItemClickListener() {
        recycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, recycleView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("Id", itemList.get(position).getId());
                intent.putExtra("pos", position);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    // Set Adapter For Recycle View
    private void setAdapter() {
        if (itemList != null && itemList.size() > 0) {
            tvNoResult.setVisibility(View.GONE);
            recycleView.setVisibility(View.VISIBLE);
            ItemAdapter itemAdapter = new ItemAdapter(this, itemList);
            recycleView.setAdapter(itemAdapter);
        } else {
            tvNoResult.setVisibility(View.VISIBLE);
            recycleView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnAddItem)
    public void onAddItemClick() {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    public ArrayList<ItemPOJO> getItemList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ItemPOJO>>() {
        }.getType();
        return gson.fromJson(appPreferencesHelper.getItemListResponse(), type);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}