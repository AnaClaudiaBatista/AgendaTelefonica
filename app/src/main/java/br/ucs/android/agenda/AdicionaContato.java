package br.ucs.android.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import br.ucs.android.agenda.model.DAO;
import br.ucs.android.agenda.objetos.Pessoa;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdicionaContato extends AppCompatActivity {

    CircleImageView icone;
    ImageView imagemLigar;
    ImageView imagemWhatsApp;
    EditText id_nome_recebido, id_telefone_recebido, id_obs_recebido;
    Button buttonVoltar, buttonAtualizar, buttonExcluir;
    String ultimoCaracterDigitado = "";
    String fotoString = "";
    String nomeRecebido;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_pessoa);

        //Widgets do primeiro lauout
        icone    = findViewById(R.id.icone);
        id_nome_recebido = findViewById(R.id.editTextNome);
        id_telefone_recebido = findViewById(R.id.editTextTelefone);
        id_obs_recebido = findViewById(R.id.editTextObs);
        imagemLigar = findViewById(R.id.imagemLigar);
        imagemWhatsApp = findViewById(R.id.imagemWhatsApp);
        //Widgets do segundo layout
        buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonExcluir = findViewById(R.id.buttonExcluir);
        buttonAtualizar = findViewById(R.id.buttonAtualizar);


       Intent intent = getIntent();

        if(intent.getStringExtra("foto") != null) {
            if (intent.getStringExtra("foto").equals("") || intent.getStringExtra("foto").equals("null")) {
                icone.setImageResource(android.R.drawable.ic_menu_camera);
            } else {
                byte[] imageBytes;
                imageBytes = Base64.decode(intent.getStringExtra("foto"), Base64.DEFAULT);
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                icone.setImageBitmap(imagemDecodificada);
            }
        }

        // para abrir a camera e selecionar foto
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder selecionaFoto = new AlertDialog.Builder(AdicionaContato.this);
                selecionaFoto.setTitle("Origem da foto:");
                selecionaFoto.setMessage("Selecione a origem da foto!");
                selecionaFoto.setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                });

                selecionaFoto.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*"); // qqrt tipo de formato
                        startActivityForResult(intent, 2);
                    }
                });
                selecionaFoto.create().show();
            }
        });
        nomeRecebido = intent.getStringExtra("nome"); // recebe o nome que sera editado/excluido no banco
        id_nome_recebido.setText(intent.getStringExtra("nome"));
        id_telefone_recebido.setText(intent.getStringExtra("telefone"));
        id_obs_recebido.setText(intent.getStringExtra("obs"));

        // botão de mandar mensagem pelo wpp
        imagemWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + id_telefone_recebido.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // botão de fazer ligacao
        imagemLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + id_telefone_recebido.getText().toString());
                Intent intent = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(intent);
            }
        });

        //mascara para o telefone
        id_telefone_recebido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Integer tamTelefone = id_telefone_recebido.getText().toString().length();
                if (tamTelefone > 1) {
                    ultimoCaracterDigitado = id_telefone_recebido.getText().toString().substring(tamTelefone - 1);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer  tamTelefone = id_telefone_recebido.getText().toString().length();
                if(tamTelefone ==2){
                    if(!ultimoCaracterDigitado.equals(" ")){
                        id_telefone_recebido.append(" ");
                    } else{
                        id_telefone_recebido.getText().delete(tamTelefone - 1, tamTelefone);
                    }
                }else if (tamTelefone == 8){
                    if(!ultimoCaracterDigitado.equals("-")){
                        id_telefone_recebido.append("-");
                    } else{
                        id_telefone_recebido.getText().delete(tamTelefone - 1, tamTelefone);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(id_nome_recebido.getText().toString().equals("") || id_telefone_recebido.getText().toString().equals(""))) {
                    atualizaPessoa();

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmaExclusao = new AlertDialog.Builder(AdicionaContato.this);
                confirmaExclusao.setTitle("Atenção!");
                confirmaExclusao.setMessage("Tem certeza que deseja excluir "+ nomeRecebido +" ?");
                confirmaExclusao.setCancelable(false);
                confirmaExclusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apagaPessoa();
                    }
                });
                confirmaExclusao.setNegativeButton("Não", null);
                confirmaExclusao.create().show();

            }
        });

        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //swipe para apagar ou abrir a tela para editar contato
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                Intent intent = new Intent (AdicionaContato.this, MainActivity.class);
                startActivity(intent);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //obter a posição que é deslizar
                Intent intent = new Intent (AdicionaContato.this, MainActivity.class);
                startActivity(intent);


                }

        }).attachToRecyclerView(recyclerView);

    }

    private void atualizaPessoa() {
        DAO dao = new DAO(getApplicationContext());
        Pessoa pessoaParaAtualizar = new Pessoa();
        pessoaParaAtualizar.setNome(id_nome_recebido.getText().toString());
        pessoaParaAtualizar.setTelefone(id_telefone_recebido.getText().toString());
        pessoaParaAtualizar.setObs(id_obs_recebido.getText().toString());
        pessoaParaAtualizar.setFoto("");
        pessoaParaAtualizar.setFoto(fotoString);
        dao.inserePessoa(pessoaParaAtualizar, nomeRecebido);
        dao.close();
        finish();
    }

    private void apagaPessoa() {
        DAO dao = new DAO(getApplicationContext());
        dao.apagaPessoa(nomeRecebido);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent dados) {
        super.onActivityResult(requestCode, resultCode, dados);
        if (requestCode == 1){
            try {
                Bitmap fotoSalva = (Bitmap) dados.getExtras().get("data");
                Bitmap fotoRedimensionada = Bitmap.createScaledBitmap(fotoSalva, 256,256, true);
                icone.setImageBitmap(fotoRedimensionada);

                //transformar em array de byte
                byte[] fotoBytes;

                //armazena a foto comprimida
                ByteArrayOutputStream stremFotoBytes = new ByteArrayOutputStream();

                //comprime a foto e transforma em png
                fotoRedimensionada.compress(Bitmap.CompressFormat.PNG, 90, stremFotoBytes);

                //transforma em array de bytes a foto comprimida
                fotoBytes = stremFotoBytes.toByteArray();

                //String q vai ser armazenada no banco
                fotoString = Base64.encodeToString(fotoBytes, Base64.DEFAULT);

            }catch (Exception e){

            }
        } else

        if (requestCode == 2){
            try {
                Uri imageUri = dados.getData();
                Bitmap fotoGaleria = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap fotoRedimensionada = Bitmap.createScaledBitmap(fotoGaleria, 256,256, true);
                icone.setImageBitmap(fotoRedimensionada);

                //transformar em array de byte
                byte[] fotoBytes;

                //armazena a foto comprimida
                ByteArrayOutputStream stremFotoBytes = new ByteArrayOutputStream();

                //comprime a foto e transforma em png
                fotoRedimensionada.compress(Bitmap.CompressFormat.PNG, 90, stremFotoBytes);

                //transforma em array de bytes a foto comprimida
                fotoBytes = stremFotoBytes.toByteArray();

                //String q vai ser armazenada no banco
                fotoString = Base64.encodeToString(fotoBytes, Base64.DEFAULT);

            }catch (Exception e){

            }
        }

    }
}