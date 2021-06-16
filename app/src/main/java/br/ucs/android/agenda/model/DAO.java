package br.ucs.android.agenda.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.ucs.android.agenda.objetos.Pessoa;

public class DAO extends SQLiteOpenHelper {
    public DAO(Context context){
        super(context, "banco", null, 7);
    }

    @Override
    //executa o oncreate quando abrimos o app pela primeira vez e o banco ainda nao existe
    public void onCreate (SQLiteDatabase db){
        String sql = "CREATE TABLE pessoa " +
                         "(nome TEXT UNIQUE , " +
                         "telefone String, " +
                         "obs TEXT, " +
                         "foto String);";
            db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql = "DROP TABLE IF EXISTS pessoa;";
           db.execSQL(sql);
           onCreate(db);
    }

    public void inserePessoa (Pessoa pessoa, String pessoaParaAtualizar){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = new ContentValues();

        dados.put("telefone", pessoa.getTelefone());
        dados.put("obs", pessoa.getObs());

        if (!pessoa.getFoto().equals("")) {
            dados.put("foto", pessoa.getFoto());
        }

        if(pessoaParaAtualizar == null){
            dados.put("nome", pessoa.getNome());
        }else{
            dados.put("nome", pessoaParaAtualizar); // colocar pessoa.getnome

        }

       // Log.d("NomeAtualizar  ",pessoaParaAtualizar);
       // Log.d("NovoNome  ",  pessoa.getNome() );

        try {
            // insira ou pule
           db.insertOrThrow("pessoa", null, dados);
            //Log.d("Entrou no try  ", "insere pessoa nova" + pessoa.getNome());

        }catch (SQLiteConstraintException e){
            // se existir alguem com o mesmo nome, ele nao consegue inserir, entao da um update
            dados.put("nome", pessoa.getNome());
            db.update("pessoa", dados, "nome = ?", new String[]{pessoaParaAtualizar});
            //Log.d("Entrou no catch  ", "atualiza pessoa " + pessoaParaAtualizar);
        }
    }
/*
    public void atualizaPessoa (Pessoa pessoa, String pessoaParaAtualizar){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = new ContentValues();
        dados.put("nome", pessoa.getNome());
        dados.put("telefone", pessoa.getTelefone());
        dados.put("obs", pessoa.getObs());
       // Log.d("Recebeu", pessoaParaAtualizar);
       // Log.d("NomenoObejeto", pessoa.getNome());
        db.update("pessoa", dados, "nome = ?",new String[]{pessoaParaAtualizar});
       // Log.d("Executou", "try");

    }*/

    public List<Pessoa> buscaPessoa() {
       SQLiteDatabase db = getReadableDatabase();
       String sql = "SELECT * FROM pessoa ORDER BY nome";

        List<Pessoa> pessoas = new ArrayList<Pessoa>();
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()){
            Pessoa pessoa = new Pessoa();
            pessoa.setNome(c.getString(c.getColumnIndex("nome")));
            pessoa.setTelefone(c.getString(c.getColumnIndex("telefone")));
            pessoa.setObs(c.getString(c.getColumnIndex("obs")));
            pessoa.setFoto(c.getString(c.getColumnIndex("foto")));
            pessoas.add(pessoa);
        }
        return pessoas;
    }

    public void apagaPessoa (String nome){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM pessoa WHERE nome = " + "'" + nome + "'";
        db.execSQL(sql);
    }

    public void apagar (String nomeRecebido){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM pessoa WHERE nome = " + "'" + nomeRecebido + "'";
        db.execSQL(sql);
    }


}
