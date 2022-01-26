package com.moon.ec3joseluishuamani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.moon.ec3joseluishuamani.model.Pokemon;
import com.moon.ec3joseluishuamani.model.PokemonRespuesta;
import com.moon.ec3joseluishuamani.pokeapi.PokeapiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity {

   private static final String TAG="POKEDEX";
    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;

    private int offset;

    private  boolean aptoCargar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter=new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    int visibleItemCount= layoutManager.getChildCount();
                    int totalItemCount= layoutManager.getItemCount();
                    int pastVisibleItems= layoutManager.findFirstVisibleItemPosition();

                    if(aptoCargar){
                        if((visibleItemCount + pastVisibleItems)>=totalItemCount){
                            Log.i(TAG,"Eso es todo mi querido aventurero.");

                            aptoCargar=false;
                            offset +=20;
                            obtenerDatos(offset);
                        }
                    }
                }
            }
        });

        retrofit=new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        aptoCargar=true;
        offset=0;
        obtenerDatos(offset);
    }

    private void obtenerDatos(int offset) {
        PokeapiService service=retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall=service.obtenerListaPokemon(20,offset);

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoCargar=true;
                if(response.isSuccessful()){
                    PokemonRespuesta pokemonRespuesta= response.body();
                    ArrayList<Pokemon> listaPokemon= pokemonRespuesta.getResults();

                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);
                }
                else {
                    Log.e(TAG,"onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoCargar=true;
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

}