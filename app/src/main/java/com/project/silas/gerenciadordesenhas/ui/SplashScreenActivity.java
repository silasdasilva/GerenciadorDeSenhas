package com.project.silas.gerenciadordesenhas.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.managers.InicializacaoManager;

public class SplashScreenActivity extends AppCompatActivity{

    private InicializacaoManager inicializacaoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_activity);

        this.inicializacaoManager = new InicializacaoManager(this);
        this.inicializacaoManager.inializarDados(new OperationListener<Void>() {
            @Override
            public void onSuccess(Void result) {

                Log.i("inicialActivity", "Banco criado!");

                inicializacaoManager.buscaTotalUsuarios(new OperationListener<Integer>(){
                    @Override
                    public void onSuccess(Integer result) {
                        if (result > 0){
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
                });
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                error.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Erro ao iniciar aplicativo!", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
}
