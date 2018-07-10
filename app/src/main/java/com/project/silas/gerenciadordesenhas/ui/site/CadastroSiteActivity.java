package com.project.silas.gerenciadordesenhas.ui.site;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.helpers.CustomDialog;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.managers.CadastroSiteManager;
import com.project.silas.gerenciadordesenhas.managers.TelaPrincipalManager;
import com.project.silas.gerenciadordesenhas.ui.main.TelaPrincipalActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CadastroSiteActivity extends AppCompatActivity {

    @BindView(R.id.iv_item_logo_tela_principal)
    protected ImageView ivLogoSiteCadastroSite;

    @BindView(R.id.tiet_nome_cadastro_site)
    protected TextInputEditText tietNomeCadastroSite;

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

    public static final String CHAVE_REGISTRO_SITE = "atualizacaoSite";

    private CadastroSiteManager cadastroSiteManager;
    private AlertDialog.Builder alerta;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_site_activity);
        ButterKnife.bind(this);

        this.cadastroSiteManager = new CadastroSiteManager(this);
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();

        if (getIntent().getExtras() != null) {
            this.siteModificacao = getIntent().getParcelableExtra(CHAVE_REGISTRO_SITE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            preencheProperties();
        }

        btSalvarCadastroSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirProgressDialog();
                if (totalAlteracoes() == 3) {
                    if (siteModificacao != null) {
                        siteModificacao.setIdUsuario(String.valueOf(SessionSingletonBusiness.getUsuario().getId()))
                                .setNomeSite(tietNomeCadastroSite.getText().toString())
                                .setUrlSite(tietUrlCadastroSite.getText().toString())
                                .setLoginSite(tietLoginCadastroSite.getText().toString())
                                .setSenhaSite(tietSenhaCadastroSite.getText().toString());

                        cadastroSiteManager.atualizaSite(siteModificacao, new OperationListener<Site>() {
                            @Override
                            public void onSuccess(Site result) {
                                Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                                setResult(RESULT_OK, intent);
                                progressDialog.dismiss();
                                finish();
                                Toast.makeText(CadastroSiteActivity.this, "Site atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable error) {
                                super.onError(error);
                                error.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(CadastroSiteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    cadastroSiteManager.insereSite(siteModificacao, new OperationListener<Site>() {
                        @Override
                        public void onSuccess(Site result) {
                            Intent intent = new Intent(CadastroSiteActivity.this, TelaPrincipalActivity.class);
                            setResult(RESULT_OK, intent);
                            progressDialog.dismiss();
                            finish();
                            Toast.makeText(CadastroSiteActivity.this, "Site inserido com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable error) {
                            super.onError(error);
                            error.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(CadastroSiteActivity.this, "Erro ao inserir site", Toast.LENGTH_SHORT).show();
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

    private void preencheProperties() {
        if (siteModificacao != null) {
            tietNomeCadastroSite.setText(this.siteModificacao.getNomeSite());
            tietUrlCadastroSite.setText(this.siteModificacao.getUrlSite());
            tietLoginCadastroSite.setText(this.siteModificacao.getLoginSite());
            tietSenhaCadastroSite.setText(this.siteModificacao.getSenhaSite());

            if (siteModificacao.getCaminhoFoto() != null && !siteModificacao.getCaminhoFoto().equals("null") && !siteModificacao.getCaminhoFoto().equals("")){
                new TelaPrincipalManager(this).buscarLogoSite(siteModificacao, new OperationListener<Bitmap>(){
                    @Override
                    public void onSuccess(Bitmap result) {
                        if (result != null){
                            ivLogoSiteCadastroSite.setImageBitmap(result);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        Toast.makeText(CadastroSiteActivity.this, "Foto não encontrada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void exibirProgressDialog(){
        this.progressDialog = new CustomDialog(this).progress();
        this.progressDialog.setMessage(getString(R.string.st_mensagem_progressdialog_tela_principal));
        this.progressDialog.show();
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
        if (this.siteModificacao != null) {
            if (!tietUrlCadastroSite.getText().toString().equals("") && !tietUrlCadastroSite.getText().toString().equals("null")
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
        } else {
            if (!tietUrlCadastroSite.getText().toString().equals("") && !tietUrlCadastroSite.getText().toString().equals("null")) alteracoes++;
            if (!tietLoginCadastroSite.getText().toString().equals("") && !tietLoginCadastroSite.getText().toString().equals("null")) alteracoes++;
            if (!tietSenhaCadastroSite.getText().toString().equals("") && !tietSenhaCadastroSite.getText().toString().equals("null")) alteracoes++;
            this.siteModificacao = new Site().setNomeSite(tietNomeCadastroSite.getText().toString())
                    .setUrlSite(tietUrlCadastroSite.getText().toString())
                    .setLoginSite(tietLoginCadastroSite.getText().toString())
                    .setSenhaSite(tietSenhaCadastroSite.getText().toString());
        }
        return alteracoes;
    }
}
