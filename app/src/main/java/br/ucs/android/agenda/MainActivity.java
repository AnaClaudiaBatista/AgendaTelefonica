package br.ucs.android.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.ucs.android.agenda.model.DAO;
import br.ucs.android.agenda.adapter.RecyclerViewAdapter;
import br.ucs.android.agenda.objetos.Pessoa;

public class MainActivity extends AppCompatActivity {

    EditText editTextNome;
    EditText editTextTelefone;
    EditText editTextObs;
    FloatingActionButton botaoAddPessoa;
    Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;


    List<Pessoa> pessoas =  new ArrayList<>();

    List<String> nomes = new ArrayList<>();
    List<String> telefones = new ArrayList<>();
    List<String> obs = new ArrayList<>();
    List<String> fotos = new ArrayList<>();

    String[] dados_nomes = new String[] {};
    String[] dados_telefones = new String[] {};
    String[] dados_obs = new String[] {};
    String[] dados_fotos = new String[] {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        editTextNome = findViewById(R.id.editTextNome);
        recyclerView = findViewById(R.id.recyclerView);
        botaoAddPessoa = findViewById(R.id.botaoAddPessoa);
        buscaNoBanco();

        //metodo para buscar os usuarios da lista
        editTextNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textoDigitado = s.toString();

                //copiando a lista de pessoas q vem do banco
                List<Pessoa> pessoasCopia = new ArrayList<>(pessoas);

                nomes = new ArrayList<>();
                telefones = new ArrayList<>();
                obs = new ArrayList<>();
                fotos = new ArrayList<>();

                dados_nomes = new String[] {};
                dados_telefones = new String[] {};
                dados_obs = new String[] {};
                dados_fotos = new String[] {};

                for(Pessoa pessoaBuscada : pessoasCopia){
                    //busca se o que esta sendo digitado contem na variavel nome
                    if(pessoaBuscada.getNome().contains(textoDigitado)){
                        nomes.add(pessoaBuscada.getNome());
                        telefones.add(String.valueOf(pessoaBuscada.getTelefone()));
                        obs.add(String.valueOf(pessoaBuscada.getObs()));
                        fotos.add(String.valueOf(pessoaBuscada.getFoto()));
                    }
                }
                    dados_nomes     = nomes.toArray(new String[0]);
                    dados_telefones = telefones.toArray(new String[0]);
                    dados_obs = obs.toArray(new String[0]);
                    dados_fotos = fotos.toArray(new String[0]);

                recyclerViewAdapter = new RecyclerViewAdapter(context, dados_nomes, dados_telefones, dados_obs, dados_fotos);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });


        //adicionar novo contato
        botaoAddPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarPessoa(view);
            }
        });

        //swipe para apagar ou abrir a tela para editar contato
         new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
             @Override
             public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                 return false;
             }

             @Override
             public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                 final int position = viewHolder.getAdapterPosition(); //obter a posição que é deslizar

                 if (direction == ItemTouchHelper.RIGHT) {    //se deslizar para a direita

                     AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //alerta para confirmar para apagar
                     builder.setMessage("Tem certeza que deseja excluir?");    //definir mensagem

                     builder.setPositiveButton("Remover", new DialogInterface.OnClickListener() { //when click on DELETE
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             recyclerViewAdapter.notifyItemRemoved(position);
                             String posicaoNome = nomes.get(position);
                             pessoas.remove(position);
                             apagar(posicaoNome);
                             buscaNoBanco();
                             Log.d("apagou", "o " + (position + 1) + ", " + posicaoNome);

                             Toast.makeText(MainActivity.this, "Contato Apagado", Toast.LENGTH_SHORT).show();

                             return;
                         }
                     }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {  //não removendo itens se o cancelamento for feito
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             recyclerViewAdapter.notifyItemRemoved( position + 1);    //notifica o Adaptador RecyclerView que os dados no adaptador foram removidos em uma posição particular.
                             recyclerViewAdapter.notifyItemRangeChanged( position, recyclerViewAdapter.getItemCount());   //notifica o Adaptador RecyclerView que as posições do elemento no adaptador foram alteradas da posição (índice do elemento removido para o final da lista), atualize-o.
                             return;
                         }
                     }).show();  //mostrar diálogo de alerta
                 }
                 else{

                         Intent intent = new Intent(context, AdicionaContato.class);
                         intent.putExtra("nome", nomes.get(position));
                         intent.putExtra("telefone", telefones.get(position));
                         intent.putExtra("obs", obs.get(position));
                         intent.putExtra("foto", fotos.get(position));
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         recyclerView.getContext().startActivity(intent);


                 }
             }
         }).attachToRecyclerView(recyclerView);


    }



    // busca os dados no banco e atualiza a tela
    private void buscaNoBanco() {
        DAO dao2 = new DAO(getApplicationContext());
        pessoas =  dao2.buscaPessoa();

        nomes = new ArrayList<>();
        telefones = new ArrayList<>();
        obs = new ArrayList<>();
        fotos = new ArrayList<>();

        dados_nomes = new String[] {};
        dados_telefones = new String[] {};
        dados_obs = new String[] {};
        dados_fotos = new String[] {};

        for(Pessoa pessoaBuscada : pessoas){
            nomes.add(pessoaBuscada.getNome());
            telefones.add(String.valueOf(pessoaBuscada.getTelefone()));
            obs.add(String.valueOf(pessoaBuscada.getObs()));
            fotos.add(String.valueOf(pessoaBuscada.getFoto()));
        }
        dados_nomes     = nomes.toArray(new String[0]);
        dados_telefones = telefones.toArray(new String[0]);
        dados_obs = obs.toArray(new String[0]);
        dados_fotos = fotos.toArray(new String[0]);


        recyclerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(context, dados_nomes, dados_telefones, dados_obs, dados_fotos);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // toda ver que atualizar ou fizer alguma alteração em outra tela e depois voltar pra mainActivity
    // (Ex. Atualizar dados e dps voltar pra lista) ele atualiza o banco denovo
    @Override
    public void onResume(){
        super.onResume();
        buscaNoBanco();
    }

    /*Metodo pra chamar a segunda pagina para add pessoa*/
    public void adicionarPessoa(View view){
        Intent intent = new Intent (this, AdicionaContato.class);
        startActivity(intent);
    }

    //apagar swipe
    private void apagar(String posicao) {
        DAO dao = new DAO(getApplicationContext());
        dao.apagar(posicao);

    }
}