package com.project.silas.gerenciadordesenhas.ui.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.helpers.CustomDialog;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.managers.CadastroUsuariosManager;
import com.project.silas.gerenciadordesenhas.ui.main.TelaPrincipalActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CadastroUsuariosActivity extends AppCompatActivity {

    @BindView(R.id.tiet_cadastro_usuarios_nome)
    protected TextInputEditText tietFormCadastroNome;

    @BindView(R.id.tiet_cadastro_usuarios_email)
    protected TextInputEditText tietFormCadastroEmail;

    @BindView(R.id.tiet_cadastro_usuarios_senha)
    protected TextInputEditText tietFormCadastroSenha;

    @BindView(R.id.bt_salvar_cadastro_usuarios)
    protected Button btFormCadastroSalvar;

    @BindView(R.id.bt_cancelar_cadastro_usuarios)
    protected Button btFormCadastroCancelar;

    private CadastroUsuariosManager cadastroUsuariosManager;
    private AlertDialog.Builder alerta;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_usuarios_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Cadastro de Usuário");

        this.cadastroUsuariosManager = new CadastroUsuariosManager(this);

        btFormCadastroSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tietFormCadastroNome.getText().toString().equals("") && tietFormCadastroEmail.getText().toString().equals("") && tietFormCadastroSenha.getText().toString().equals("")) {
                    Toast.makeText(CadastroUsuariosActivity.this, "Preencha seus dados para efetuar o cadastro!", Toast.LENGTH_SHORT).show();
                    return;
                }

                exibirProgressDialog();
                cadastroUsuariosManager.cadastrarUsuario(new Usuario().setNomeUsuario(tietFormCadastroNome.getText().toString())
                        .setEmailUsuario(tietFormCadastroEmail.getText().toString())
                        .setSenhaUsuario(tietFormCadastroSenha.getText().toString()),
                        new OperationListener<Usuario>(){
                    @Override
                    public void onSuccess(Usuario usuarioCadastrado) {
                        Toast.makeText(CadastroUsuariosActivity.this, "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                        SessionSingletonBusiness.setUsuario(usuarioCadastrado);
                        startActivity(new Intent(CadastroUsuariosActivity.this, TelaPrincipalActivity.class));
                        progressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        error.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(CadastroUsuariosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btFormCadastroCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            this.alerta = new AlertDialog.Builder(this);
            this.alerta.setTitle(getString(R.string.st_alerta_login_usuarios))
                    .setMessage(getString(R.string.st_mensagem_sair_cadastro_usuarios))
                    .setPositiveButton(getString(R.string.st_sim_cadastro_usuarios), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.st_nao_cadastro_usuarios), new DialogInterface.OnClickListener() {
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

    private void exibirProgressDialog(){
        this.progressDialog = new CustomDialog(this).progress();
        this.progressDialog.setMessage(getString(R.string.st_mensagem_progressdialog_tela_principal));
        this.progressDialog.show();
    }
}
