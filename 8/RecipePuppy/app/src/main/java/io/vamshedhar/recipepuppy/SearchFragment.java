package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment implements IngredientsRecyclerAdapter.IngredientsInterface {

    private OnFragmentInteractionListener mListener;

    ArrayList<String> ingredients;
    EditText dishName;

    private RecyclerView ingredientsList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(MainActivity.TAG, "onActivityCreated Called");

        ingredients = new ArrayList<>();
        ingredients.add("");
        ingredientsList = getView().findViewById(R.id.ingredientsList);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ingredientsList.setLayoutManager(mLayoutManager);
        dishName = getView().findViewById(R.id.dishName);

        getView().findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (dishName.getText().toString().trim().equals("")){
                Toast.makeText(getActivity(), "Please enter a dish name!", Toast.LENGTH_SHORT).show();
            } else if (ingredients.size() <= 1){
                Toast.makeText(getActivity(), "Please add ingredients!", Toast.LENGTH_SHORT).show();
            } else{
                mListener.onSearchData(dishName.getText().toString(), ingredients);
            }
            }
        });

        loadIngredients();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ingredients.size() == 1){
            dishName.setText("");
            dishName.requestFocus();
        }

    }

    public void loadIngredients(){
        adapter = new IngredientsRecyclerAdapter(getActivity(), ingredients, this);
        ingredientsList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAddClick(String value) {
        if(value.equals("")){
            Toast.makeText(getActivity(), "Please enter an ingredient to Add!", Toast.LENGTH_SHORT).show();
        } else if (ingredients.size() == 6){
            Toast.makeText(getActivity(), "You can add upto 5 ingredients only!", Toast.LENGTH_SHORT).show();
        } else {
            ingredients.add(ingredients.size() - 1, value);
            loadIngredients();
        }
    }

    @Override
    public void onRemoveClick(int position) {
        ingredients.remove(position);
        loadIngredients();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchData(String name, ArrayList<String> ingredients);
    }
}
