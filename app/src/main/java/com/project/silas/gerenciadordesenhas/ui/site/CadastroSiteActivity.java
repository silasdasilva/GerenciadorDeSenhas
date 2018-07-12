package com.project.silas.gerenciadordesenhas.ui.site;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
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

    public static final String CHAVE_REGISTRO_SITE = "registroSite";

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
            preencheProperties();
        }

        btSalvarCadastroSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirProgressDialog();
                if (siteModificacao != null) {
                    if (totalAlteracoes() > 0) {
                        siteModificacao.setNomeSite(tietNomeCadastroSite.getText().toString())
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
                                siteModificacao = getIntent().getParcelableExtra(CHAVE_REGISTRO_SITE);
                                Toast.makeText(CadastroSiteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(CadastroSiteActivity.this, "Modifique os dados para salvar!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (totalAlteracoes() == 4) {
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
                            siteModificacao = new Site();
                            Toast.makeText(CadastroSiteActivity.this, "Erro ao inserir site", Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressDialog.dismiss();
                    return;
                }
                if (totalAlteracoes() == 0) Toast.makeText(CadastroSiteActivity.this, "Preencha os campos para continuar!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
        if (this.siteModificacao != null) {
            tietNomeCadastroSite.setText(this.siteModificacao.getNomeSite());
            tietUrlCadastroSite.setText(this.siteModificacao.getUrlSite());
            tietLoginCadastroSite.setText(this.siteModificacao.getLoginSite());
            tietSenhaCadastroSite.setText(this.siteModificacao.getSenhaSite());

            if (this.siteModificacao.getCaminhoFoto() != null && !this.siteModificacao.getCaminhoFoto().equals("null") && !this.siteModificacao.getCaminhoFoto().equals("")){
                this.cadastroSiteManager.buscarLogoSite(this.siteModificacao, new OperationListener<Bitmap>(){
                    @Override
                    public void onSuccess(Bitmap result) {
                        if (result != null){
                            ivLogoSiteCadastroSite.setImageBitmap(result);
                            return;
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

            if (totalAlteracoes() > 0) {
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
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            super.onBackPressed();
        }
    }

    private int totalAlteracoes() {
        int alteracoes = 0;
        if (this.siteModificacao != null) {
            if (!tietNomeCadastroSite.getText().toString().equals("") && !tietNomeCadastroSite.getText().toString().equals("null")
                    && !tietNomeCadastroSite.getText().toString().equals(this.siteModificacao.getNomeSite())) {
                alteracoes++;
            }
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
            if (!tietNomeCadastroSite.getText().toString().equals("") && !tietNomeCadastroSite.getText().toString().equals("null")) alteracoes++;
            if (!tietUrlCadastroSite.getText().toString().equals("") && !tietUrlCadastroSite.getText().toString().equals("null")) alteracoes++;
            if (!tietLoginCadastroSite.getText().toString().equals("") && !tietLoginCadastroSite.getText().toString().equals("null")) alteracoes++;
            if (!tietSenhaCadastroSite.getText().toString().equals("") && !tietSenhaCadastroSite.getText().toString().equals("null")) alteracoes++;
            this.siteModificacao = new Site()
                    .setNomeSite(tietNomeCadastroSite.getText().toString())
                    .setUrlSite(tietUrlCadastroSite.getText().toString())
                    .setLoginSite(tietLoginCadastroSite.getText().toString())
                    .setSenhaSite(tietSenhaCadastroSite.getText().toString());
        }
        return alteracoes;
    }
}
