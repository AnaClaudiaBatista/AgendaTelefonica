package br.ucs.android.agenda.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ucs.android.agenda.AdicionaContato;
import br.ucs.android.agenda.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    Context context;
    List<String> nomes = new ArrayList<>();
    String[] telefones;
    String[] obs;
    String[] fotos;
    View viewOnCreate;
    ViewHolder viewHolderLocal;
    RecyclerView recyclerView;


    public RecyclerViewAdapter(Context contextRecebido, String[] nomesRecebidos, String[]telefonesRecebidos, String[]ObsRecebidos, String[]FotosRecebidas){
        context   = contextRecebido;
        nomes.addAll(Arrays.asList(nomesRecebidos));
        telefones = telefonesRecebidos;
        obs       = ObsRecebidos;
        fotos     = FotosRecebidas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textNome;
        public TextView textTelefone;
        public TextView textObs;
        public CircleImageView icone;


        public ViewHolder(View itemView) {
            super(itemView);
            textNome     = itemView.findViewById(R.id.textNome);
            textTelefone = itemView.findViewById(R.id.textTelefone);
            textObs      = itemView.findViewById(R.id.editTextObs);
            icone        = itemView.findViewById(R.id.icone);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Log.d("Teste: ", "Click");
                }
            });*/
        }

        @Override
        public void onClick(View v) {

        }
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewOnCreate    = LayoutInflater.from(context).inflate(R.layout.recyclerview_itens, parent, false);
        viewHolderLocal = new ViewHolder(viewOnCreate);
        return viewHolderLocal;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textNome.setText(nomes.get(position));
        holder.textTelefone.setText(telefones[position]);

        if (fotos[position].equals("") || fotos[position].equals("null")) {
            holder.icone.setImageResource(R.mipmap.user);
        } else {
            byte[] imageBytes;
            imageBytes = Base64.decode(fotos[position], Base64.DEFAULT);
            Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.icone.setImageBitmap(imagemDecodificada);
        }


        viewOnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            // ao clicar na linha entra em uma nova activity com os dados detalhados da pessoa
            public void onClick(View v) {
                Intent intent = new Intent(context, AdicionaContato.class);
                intent.putExtra("nome", nomes.get(position));
                intent.putExtra("telefone", telefones[position]);
                intent.putExtra("obs", obs[position]);
                intent.putExtra("foto", fotos[position]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });


                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        viewOnCreate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            // ao clicar na linha entra em uma nova activity com os dados detalhados da pessoa
                            public void onClick(View v) {
                                Intent intent = new Intent(context, AdicionaContato.class);
                                intent.putExtra("nome", nomes.get(position));
                                intent.putExtra("telefone", telefones[position]);
                                intent.putExtra("obs", obs[position]);
                                intent.putExtra("foto", fotos[position]);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);
                            }
                        });

                    }

                }).attachToRecyclerView(recyclerView);



    }


/*
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); //obter a posição que é deslizar

            viewOnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                // ao clicar na linha entra em uma nova activity com os dados detalhados da pessoa
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdicionaContato.class);
                    intent.putExtra("nome", nomes.get(position));
                    intent.putExtra("telefone", telefones[position]);
                    intent.putExtra("obs", obs[position]);
                    intent.putExtra("foto", fotos[position]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }
    };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
    ItemTouchHelper.att (viewOnCreate); //definir deslize para nova vista

*/



    @Override
    public int getItemCount() {
        return nomes.size();
    }


}

