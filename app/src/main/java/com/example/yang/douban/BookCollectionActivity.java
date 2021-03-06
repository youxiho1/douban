package com.example.yang.douban;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yang.douban.Adapter.CollectionBooksAdapter;
import com.example.yang.douban.Adapter.HotBooksAdapter;
import com.example.yang.douban.Bean.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookCollectionActivity extends AppCompatActivity {

    private List<Book> mBookList;
    private RecyclerView recyclerView;
    private ImageButton ib_back;
    private EasyRefreshLayout easyRefreshLayout;
    private CollectionBooksAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collection);
        adapter = new CollectionBooksAdapter(R.layout.item_collection_books, mBookList);
        ib_back = (ImageButton) findViewById(R.id.ib_book_collection_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
        adapter.setNewData(mBookList);
        initAdapter();
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.emptylist);
        adapter.setHeaderFooterEmpty(true, true);
    }
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_book_collection);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration();
        easyRefreshLayout = (EasyRefreshLayout) findViewById(R.id.easylayout);
        easyRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefreshing() {
                initData();
                initAdapter();
                easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
                    @Override
                    public void complete() {
                        adapter.setNewData(mBookList);
                        easyRefreshLayout.refreshComplete();
                    }
                }, 500);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initAdapter() {
        //firstAdapter.openLoadAnimation();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(BookCollectionActivity.this, BookDetailActivity.class);
                int id = mBookList.get(position).getId();
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Book book = mBookList.get(position);
                int id = book.getId();
                FlowerHttp flowerHttp = new FlowerHttp("http://118.25.40.220/api/toCancelCollect/");
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("type", "books");
                String response = flowerHttp.post(map);
                int result = 0;
                try {
                    result = new JSONObject(response).getInt("rsNum");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result == 0) {
                    showToast("未知错误");
                    return;
                }
                else if(result == -2) {
                    showToast("您没有收藏这本图书");
                    return;
                }
                else if(result == 1) {
                    showToast("取消收藏成功");
                    initData();
                    adapter.setNewData(mBookList);
                    initAdapter();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        mBookList = new ArrayList<>();
        FlowerHttp flowerHttp = new FlowerHttp("http://118.25.40.220/api/getCollectedInfo/");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "books");
        String response = flowerHttp.post(map);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            //jsonArray = new JSONObject(response).getJSONArray("");
            for(int i = 0; i < jsonArray.length(); i++) {
                Book book = new Book();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                book.setAuthor(jsonObject.getString("author"));
                book.setGood_num(jsonObject.getInt("like_num"));
                book.setName(jsonObject.getString("name"));
                book.setPublisher(jsonObject.getString("publisher"));
                book.setText(jsonObject.getString("text"));
                book.setImage("http://118.25.40.220/" + jsonObject.getString("src"));
                book.setId(jsonObject.getInt("id"));
                mBookList.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showToast(String s) {
        Toast.makeText(BookCollectionActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
