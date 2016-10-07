package com.appteamnith.hillffair.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appteamnith.hillffair.R;
import com.appteamnith.hillffair.adapters.CardAdapter;
import com.appteamnith.hillffair.application.SharedPref;
import com.appteamnith.hillffair.models.NewsfeedModel;
import com.appteamnith.hillffair.models.NewsfeedModel2;
import com.appteamnith.hillffair.utilities.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aditya on 9/13/2016.
 */
public class ProfileTab3 extends Fragment {

    private static final String USER_POST ="post" ;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CardAdapter adapter;
    private ArrayList<NewsfeedModel2> list;
    private TextView noData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_tab3_fragment, container, false);
        noData= (TextView) v.findViewById(R.id.no_data_textview);
        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        adapter = new CardAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPref sharedPref=new SharedPref(getActivity());
        Log.d("id",sharedPref.getUserId());
        if(savedInstanceState==null){
            getData("1",sharedPref.getUserId());
            progressBar.setVisibility(View.VISIBLE);
        }

        else {
            if(savedInstanceState.getParcelableArrayList(USER_POST)!=null)
            {
                recyclerView.setVisibility(View.VISIBLE);
                list=savedInstanceState.getParcelableArrayList(USER_POST);
                adapter.refresh(list);
            }
            else {
                noData.setVisibility(View.VISIBLE);
                noData.setText("No Post Uploaded");
            }

        }
        return v;
    }


    private void getData(String from, String id) {
        Call<NewsfeedModel> getUserNewsFeed = Utils.getRetrofitService().getAllUserNews(from, id);
        getUserNewsFeed.enqueue(new Callback<NewsfeedModel>() {
            @Override
            public void onResponse(Call<NewsfeedModel> call, Response<NewsfeedModel> response) {
                NewsfeedModel data = response.body();
                if (data != null && response.isSuccess()) {
                    if (data.isSuccess()) {

                        list = data.getFeed();
                        if(list.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            adapter.refresh(list);
                        }
                        else {
                            noData.setVisibility(View.VISIBLE);
                            noData.setText("No Post Uploaded");
                        }

                    } else {
                        noData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        noData.setText(data.getError());
                    }
                } else {
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    noData.setText("Please Check Internet Connection");
                }
            }

            @Override

            public void onFailure(Call<NewsfeedModel> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
                noData.setText("Please Check Internet Connection");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(USER_POST,list);
    }
}
