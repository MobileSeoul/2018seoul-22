package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private CardView lodgeCard, mealCard, placeCard, cultureCard, boardCard;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //defining Cards
        lodgeCard = rootView.findViewById(R.id.lodge_section);
        mealCard = rootView.findViewById(R.id.meal_section);
        placeCard = rootView.findViewById(R.id.place_section);
        cultureCard = rootView.findViewById(R.id.culture_section);
        boardCard = rootView.findViewById(R.id.board_section);
        //Add Click listener to the cards

        lodgeCard.setOnClickListener(this);
        mealCard.setOnClickListener(this);
        placeCard.setOnClickListener(this);
        cultureCard.setOnClickListener(this);
        boardCard.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch (view.getId()) {
            case R.id.lodge_section :
                i = new Intent(this.getActivity(), lodge_list.class);
                startActivity(i);
                break;
            case R.id.meal_section :
                i = new Intent(this.getActivity(), food_list.class);
                startActivity(i);
                break;
            case R.id.place_section :
                i = new Intent(this.getActivity(), place_list.class);
                startActivity(i);
                break;
            case R.id.culture_section :
                i = new Intent(this.getActivity(), culture_list.class);
                startActivity(i);
                break;
            case R.id.board_section :
                i = new Intent(this.getActivity(), BoardFragment.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}