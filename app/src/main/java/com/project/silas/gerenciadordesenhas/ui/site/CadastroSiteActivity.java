package com.project.silas.gerenciadordesenhas.ui.site;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.managers.CadastroSiteManager;
import com.project.silas.gerenciadordesenhas.ui.main.TelaPrincipalActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CadastroSiteActivity extends AppCompatActivity {

    @BindView(R.id.tv_exclusao_cadastro_site)
    protected TextView tvExclusaoCadastroSite;

    @BindView(R.id.tiet_url_cadastro_site)
    protected TextInputEditText tietUrlCadastroSite;

    @BindView(R.id.tiet_login_cadastro_site)
    protected TextInputEditText tietLoginCadastroSite;

    @BindView(R.id.tiet_senha_cadastro_site)
    protected TextInputEditText tietSenhaCadastroSite;

    @BindView(R.id.bt_salvar_cadastro_site)
    protected Button btSalvarCadastroSite;

    @BindView(R.id.bt_cancelar_cadastro_site)
    protected Button btCancelarCadastroSite;

    private Usuario usuarioLogado;
    private Site siteModificacao;

    public static final String CHAVE_INSERCAO_SITE = "insercaoSite";
    public static final String CHAVE_ATUALIZACAO_SITE = "atualizacaoSite";
    public static final String CHAVE_EXCLUSAO_SITE = "exclusaoSite";

    private String chaveUsada;

    private CadastroSiteManager cadastroSiteManager;
    private AlertDialog.Builder alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_site_activity);
        ButterKnife.bind(this);

        this.cadastroSiteManager = new CadastroSiteManager(this);
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();

        if (getIntent().getExtras().get(CHAVE_INSERCAO_SITE) != null) {
            this.chaveUsada = CHAVE_INSERCAO_SITE;
            this.siteModificacao = getIntent().getParcelableExtra(CHAVE_INSERCAO_SITE);
        }

        if (getIntent().getExtras().get(CHAVE_ATUALIZACAO_SITE) != null) {
            this.chaveUsada = CHAVE_ATUALIZACAO_SITE;
            this.siteModificacao = getIntent().getParcelableExtra(CHAVE_ATUALIZACAO_SITE);
        }

        if (getIntent().getExtras().get(CHAVE_EXCLUSAO_SITE) != null) {
            this.chaveUsada = CHAVE_EXCLUSAO_SITE;
            this.siteModificacao = getIntent().getParcelableExtra(CHAVE_EXCLUSAO_SITE);
            tvExclusaoCadastroSite.setVisibility(View.VISIBLE);
            tietUrlCadastroSite.setVisibility(View.GONE);
            tietLoginCadastroSite.setVisibility(View.GONE);
            tietSenhaCadastroSite.setVisibility(View.GONE);
            btSalvarCadastroSite.setText(R.string.st_excluir_tela_principal);
        }

        btSalvarCadastroSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalAlteracoes() == 3) {
                    siteModificacao.setUrlSite(tietUrlCadastroSite.getText().toString())
                            .setLoginSite(tietLoginCadastroSite.getText().toString())
                            .setSenhaSite(tietSenhaCadastroSite.getText().toString());

                    if (chaveUsada.equals(CHAVE_INSERCAO_SITE)) {
                        cadastroSiteManager.insereSite(siteModificacao, new OperationListener<Site>() {
                            @Override
                            public void onSuccess(Site result) {
                                Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                                setResult(RESULT_OK, intent);
                                finish();
                                Toast.makeText(CadastroSiteActivity.this, "Site inserido com sucesso!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable error) {
                                super.onError(error);
                                error.printStackTrace();
                                Toast.makeText(CadastroSiteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    if (chaveUsada.equals(CHAVE_ATUALIZACAO_SITE)) {
                        cadastroSiteManager.atualizaSite(siteModificacao, new OperationListener<Site>() {
                            @Override
                            public void onSuccess(Site result) {
                                Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                                setResult(RESULT_OK, intent);
                                finish();
                                Toast.makeText(CadastroSiteActivity.this, "Site atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable error) {
                                super.onError(error);
                                error.printStackTrace();
                                Toast.makeText(CadastroSiteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }

                if (chaveUsada.equals(CHAVE_EXCLUSAO_SITE)) {
                    cadastroSiteManager.excluiSite(siteModificacao, new OperationListener<Site>() {
                        @Override
                        public void onSuccess(Site result) {
                            Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                            setResult(RESULT_OK, intent);
                            finish();
                            Toast.makeText(CadastroSiteActivity.this, "Site excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable error) {
                            super.onError(error);
                            error.printStackTrace();
                            Toast.makeText(CadastroSiteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btCancelarCadastroSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { //configurando botão voltar
            case android.R.id.home:
                onBackPressed();//alerta se já tiver alteração de dados
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if (chaveUsada.equals(CHAVE_EXCLUSAO_SITE) || totalAlteracoes() <= 0) {
                Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
                return;
            }

            this.alerta = new AlertDialog.Builder(this);
            this.alerta.setTitle(getString(R.string.st_alerta_cadastro_site))
                    .setMessage(getString(R.string.st_mensagem_sair_cadastro_site))
                    .setPositiveButton(getString(R.string.st_sim_cadastro_site), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.st_nao_cadastro_site), new DialogInterface.OnClickListener() {
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

    private int totalAlteracoes() {
        int alteracoes = 0;
        if (!tietUrlCadastroSite.getText().toString().equals("")  && !tietUrlCadastroSite.getText().toString().equals("null")
                && !tietUrlCadastroSite.getText().toString().equals(this.siteModificacao.getUrlSite())) {
            alteracoes++;
        }
        if (!tietLoginCadastroSite.getText().toString().equals("") && !tietLoginCadastroSite.getText().toString().equals("null")
                && !tietLoginCadastroSite.getText().toString().equals(this.siteModificacao.getLoginSite())) {
            alteracoes++;
        }
        if (!tietSenhaCadastroSite.getText().toString().equals("") && !tietSenhaCadastroSite.getText().toString().equals("null")
                && !tietSenhaCadastroSite.getText().toString().equals(this.siteModificacao.getSenhaSite())) {
            alteracoes++;
        }
        return alteracoes;
    }
}
