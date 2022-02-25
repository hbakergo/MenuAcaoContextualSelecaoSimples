package baker.com.menuacaocontextualselecaosimples;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.view.ActionMode; //nunca importar esse, dá erro no (mActionModeCallback) que fique registrado aqui
import androidx.appcompat.view.ActionMode; //importar esse, é o correto para cuncionar o "mActionModeCallback"
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listViewPessoas;
    private ArrayAdapter<Pessoa> listaAdapter;
    private ArrayList<Pessoa> listaPessoas;

   // private androidx.appcompat.view.ActionMode actionMode;
    private ActionMode actionMode;
    private int posicaoSelecionada = -1;
    private View viewSelecionada;

    //Menu de Ação Contextual
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){

        //vai inflar o menu de ação contextual
        //é chamado uma vez apenas antes do menu ser exibido a primeira vez
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.principal_item_selecionado, menu);
            return true;  // retornar true para o menu ser exibido, se retornar false interrompe
        }

        //depois que o usuário acessa pela segunda vez, é passado somente por aqui no prepare
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //quando clico em alguma ação do menu
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.menuItemAlterar:
                    alterarPessoa();
                    mode.finish(); //usado para fechar o menu
                    return true;
                case R.id.menuItemExcluir:
                    excluirPessoa();
                    mode.finish(); //usado para fechar o menu
                    return false;
                default:
                    return false;
            }
        }

        //quando eu saio do menu e ele vai ser destruído
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(viewSelecionada != null){
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelecionada = null;

            listViewPessoas.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewPessoas = findViewById(R.id.listViewPessoas);

        listViewPessoas.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {
                        posicaoSelecionada = position;
                        alterarPessoa();

                    }
                }
        );

        listViewPessoas.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //trata o click longo no item
        listViewPessoas.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {
                        if(actionMode != null){
                            return false;
                        }
                        posicaoSelecionada = position;

                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listViewPessoas.setEnabled(false);

                        //linha que realmente abre o action mode
                        actionMode = startSupportActionMode(mActionModeCallback);
                        return true;
                    }

                });

        popularLista();

    }

    private void popularLista(){
        listaPessoas = new ArrayList<>();

        listaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaPessoas);
        listViewPessoas.setAdapter(listaAdapter);
    }

    private void excluirPessoa(){
        listaPessoas.remove(posicaoSelecionada);
        listaAdapter.notifyDataSetChanged();
    }

    private void alterarPessoa(){
        Pessoa pessoa = listaPessoas.get(posicaoSelecionada);

        PessoaActivity.alterarPessoa(this, pessoa);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();

            String nome = bundle.getString(PessoaActivity.NOME);

            if(requestCode == PessoaActivity.ALTERAR){
                Pessoa pessoa = listaPessoas.get(posicaoSelecionada);
                pessoa.setNome(nome);

                posicaoSelecionada = -1;
            }else{
                listaPessoas.add(new Pessoa(nome));
            }
            listaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItemNovo:
                PessoaActivity.novaPessoa(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}