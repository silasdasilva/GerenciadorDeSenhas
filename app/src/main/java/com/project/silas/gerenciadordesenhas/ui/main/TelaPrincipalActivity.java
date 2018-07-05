package com.project.silas.gerenciadordesenhas.ui.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.helpers.CustomDialog;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.managers.TelaPrincipalManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TelaPrincipalActivity extends AppCompatActivity {

    @BindView(R.id.rv_tela_principal)
    protected RecyclerView rvTelaPrincpal;

    @BindView(R.id.fabmenu_tela_principal)
    protected FloatingActionMenu fabMenuTelaPrincipal;

    @BindView(R.id.fab_inserir_tela_principal)
    protected FloatingActionButton fabInserirTelaPrincipal;

    @BindView(R.id.fab_editar_tela_principal)
    protected FloatingActionButton fabEditarTelaPrincipal;

    @BindView(R.id.fab_excluir_tela_principal)
    protected FloatingActionButton fabExcluirTelaPrincipal;

    @BindView(R.id.cv_lista_vazia_tela_princpal)
    protected ConstraintLayout cvListaVaziaTelaPrincipal;

    public static final String TAG_USUARIO_LOGADO = "usuarioLogado";

    private Usuario usuarioLogado;
    private TelaPrincipalAdapter adaptador;
    private ProgressDialog progressDialog;
    private GridLayoutManager gridLayoutManager;

    private long idSiteSelecionado = -1;
    private String ultimaPesquisa = "";
    private int posicaoParaRolagem = 0;
    private int posicaoParaSelecionar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal_activity);
        ButterKnife.bind(this);
        rvTelaPrincpal.setHasFixedSize(true);

        if (getIntent().getExtras() != null){
            this.usuarioLogado = getIntent().getParcelableExtra(TAG_USUARIO_LOGADO);
            this.getSupportActionBar().setTitle(this.usuarioLogado.getNomeUsuario());
        } else {
            Toast.makeText(this, "erro ao carregar dados do Usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        configuraLayoutAndAdapter();

        rvTelaPrincpal.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(), rvTelaPrincpal, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == adaptador.getPosicaoClicada()) posicaoParaSelecionar = -1;
                else posicaoParaSelecionar = position;

                idSiteSelecionado = adaptador.getItemId(posicaoParaSelecionar); //se for posicão -1 (para desclicar) retorna id 0
                configuraItemSelecionado();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                onItemClick(view, position);
            }
        }));

        rvTelaPrincpal.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    fabMenuTelaPrincipal.showMenuButton(true);
                    posicaoParaRolagem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0 && fabMenuTelaPrincipal.isShown()) {
                    fabMenuTelaPrincipal.close(true);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Override
    public void onBackPressed() {
        try {

            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle(getString(R.string.st_alerta_login_usuarios))
                    .setMessage(getString(R.string.st_mensagem_sair_login_usuarios))
                    .setPositiveButton(getString(R.string.st_sim_login_usuarios), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.st_nao_login_usuarios), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();

        } catch (Throwable e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        MenuItem menuItemPesquisa = menu.findItem(R.id.menu_servicos_rgn_pesquisar);
        MenuItem menuItemInfo = menu.findItem(R.id.menu_servicos_rgn_info);

        final SearchView viewPesquisar = (SearchView) menuItemPesquisa.getActionView();
        viewPesquisar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                exibirProgressDialog();
                adaptador.preencheLista(usuarioLogado, query, new OperationListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        ultimaPesquisa = query;
                        atualizaView();
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        error.printStackTrace();
                        atualizaView();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0 || newText.equals("")){
                    Toast.makeText(TelaPrincipalActivity.this, "Digite algo para buscar", Toast.LENGTH_SHORT).show();
                    onQueryTextSubmit(newText);
                }
                return false;
            }
        });

        viewPesquisar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                exibirProgressDialog();
                atualizaView();
                return false;
            }
        });

        menuItemInfo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int totalSites = adaptador.getItemCount();

                Log.i("telaPrincipalActivity", "Total de Sites: " + totalSites);

                Intent intent = new Intent(TelaPrincipalActivity.this, SitesInfoActivity.class);
                intent.putExtra(SitesInfoActivity.CHAVE_INTENT_TOTALSITES, totalSites);
                startActivity(intent);

                return false;
            }
        });
        return super.onCreatePanelMenu(featureId, menu);
    }

    /**
     * Com a reescrita deste método é possível recriar a tela ao rotacionar Tablet
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        configuraLayoutAndAdapter();
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Muda a aparência do card selecionado de acordo com as properties do adaptador e abre ou fecha os Fabs.
     */

    private void configuraItemSelecionado(){
        Site siteSelecionado = this.adaptador.getSiteSelecionado();
        if (siteSelecionado == null) {
            fabEditarTelaPrincipal.setVisibility(View.VISIBLE);
            fabExcluirTelaPrincipal.setVisibility(View.VISIBLE);
            fabMenuTelaPrincipal.open(true);
            this.adaptador.procuraSite(this.idSiteSelecionado);
        } else {
            this.adaptador.procuraSite(this.idSiteSelecionado);
            if (this.adaptador.getSiteSelecionado() == null) fabMenuTelaPrincipal.close(true);
        }
        this.adaptador.notifyDataSetChanged();
    }

    /**
     * Exibe a caixa de loading com a mensagem padrão.
     */
    private void exibirProgressDialog(){
        this.progressDialog = new CustomDialog(this).progress();
        this.progressDialog.setMessage(getString(R.string.st_mensagem_progressdialog_tela_principal));
        this.progressDialog.show();
    }

    /**
     * Método responsável por instanciar o gerenciador de Layout e o adaptador de lista
     */
    private void configuraLayoutAndAdapter(){
        exibirProgressDialog();
        this.gridLayoutManager = new GridLayoutManager(this, 1);
        rvTelaPrincpal.setLayoutManager(this.gridLayoutManager);

        this.adaptador = new TelaPrincipalAdapter(this, new TelaPrincipalManager(this), this.usuarioLogado, this.ultimaPesquisa, new OperationListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                rvTelaPrincpal.setAdapter(adaptador);
                Log.i("telaPrincipalActivity", "Última posição clicada: " + posicaoParaSelecionar
                        + "\nÚltima posição de rolagem: " + posicaoParaRolagem
                        + "\nid Site selecionada: " + idSiteSelecionado);

                if (idSiteSelecionado >= 0) {
                    configuraItemSelecionado();
                    if (posicaoParaSelecionar != adaptador.getPosicaoClicada()) posicaoParaRolagem = adaptador.getPosicaoClicada(); //casos de baixa/reset a posição na lista muda
                }

                if (posicaoParaRolagem >= 0) gridLayoutManager.scrollToPosition(posicaoParaRolagem);
                atualizaView();
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                atualizaView();
                Toast.makeText(TelaPrincipalActivity.this, "Erro ao carregar lista!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método que decide quando exibir o recyclerview ou o layout com mensagem de "Ítens não encontrados".
     */

    private void atualizaView() {

        if (this.adaptador.getItemCount() > 0){
            rvTelaPrincpal.setVisibility(View.VISIBLE);
            cvListaVaziaTelaPrincipal.setVisibility(View.INVISIBLE);
        } else {
            cvListaVaziaTelaPrincipal.setVisibility(View.VISIBLE);
            rvTelaPrincpal.setVisibility(View.INVISIBLE);
            fabMenuTelaPrincipal.close(true);
            fabExcluirTelaPrincipal.setVisibility(View.GONE);
            fabEditarTelaPrincipal.setVisibility(View.GONE);
        }
        if (this.progressDialog != null && this.progressDialog.isShowing()) this.progressDialog.dismiss();
    }
}
