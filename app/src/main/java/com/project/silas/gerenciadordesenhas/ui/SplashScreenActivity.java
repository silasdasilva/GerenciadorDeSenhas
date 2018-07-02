package com.project.silas.gerenciadordesenhas.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.managers.InicializacaoManager;

public class SplashScreenActivity extends AppCompatActivity{

    private int confere;
    private InicializacaoManager inicializacaoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_activity);

        this.inicializacaoManager = new InicializacaoManager(this);
        this.inicializacaoManager.inializarDados(new OperationListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    /**CONTINUAR*/
                    Toast.makeText(SplashScreenActivity.this, "Cadastrar Usuário", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashScreenActivity.this, CadastroUsuariosActivity.class));
                }
                Toast.makeText(SplashScreenActivity.this, "Já existem Usuários cadastrados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreenActivity.this, LoginUsuariosActivity.class));
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                error.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Erro ao iniciar aplicativo. Mensagem: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
}
