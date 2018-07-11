package com.project.silas.gerenciadordesenhas.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.helpers.CustomDialog;
import com.project.silas.gerenciadordesenhas.managers.InicializacaoManager;
import com.project.silas.gerenciadordesenhas.ui.user.LoginUsuariosActivity;

public class SplashScreenActivity extends AppCompatActivity{

    private InicializacaoManager inicializacaoManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        exibirProgressDialog();

        this.inicializacaoManager = new InicializacaoManager(this);
        this.inicializacaoManager.inializarDados(new OperationListener<Void>() {
            @Override
            public void onSuccess(Void result) {

                Toast.makeText(SplashScreenActivity.this, "Faça seu login!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreenActivity.this, LoginUsuariosActivity.class));

                //Log.i("inicialActivity", "Banco criado!");

                /*inicializacaoManager.buscaTotalUsuarios(new OperationListener<Integer>(){
                    @Override
                    public void onSuccess(Integer result) {
                        if (result > 0){
                            if (SessionSingletonBusiness.getUsuario() != null){
                                Usuario usuarioLogado = SessionSingletonBusiness.getUsuario();
                                Log.i("inicialActivity", "O usuário " + usuarioLogado.getNomeUsuario() + " logado!");
                                Toast.makeText(SplashScreenActivity.this, "Bem-vindo de volta Sr " + usuarioLogado.getNomeUsuario(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SplashScreenActivity.this, TelaPrincipalActivity.class));
                                return;
                            }
                            Log.i("inicialActivity", "Já existem " + result + " Usuários cadastrados!");
                            Toast.makeText(SplashScreenActivity.this, "Faça seu login!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashScreenActivity.this, LoginUsuariosActivity.class));
                            return;
                        }
                        Toast.makeText(SplashScreenActivity.this, "Cadastre um Usuário para continuar!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashScreenActivity.this, CadastroUsuariosActivity.class));
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        error.printStackTrace();
                        Toast.makeText(SplashScreenActivity.this, "Erro ao buscar usuários!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SplashScreenActivity.this, "Tente cadastrar novamente!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashScreenActivity.this, CadastroUsuariosActivity.class));
                    }
                });*/
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                error.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Erro ao iniciar aplicativo!", Toast.LENGTH_SHORT).show();
            }
        });
        this.progressDialog.dismiss();
        finish();
    }

    /**
     * Exibe a caixa de loading com a mensagem padrão.
     */
    private void exibirProgressDialog(){
        this.progressDialog = new CustomDialog(this).progress();
        this.progressDialog.setMessage(getString(R.string.core_customdialog_defaultProgressmessage));
        this.progressDialog.show();
    }
}
