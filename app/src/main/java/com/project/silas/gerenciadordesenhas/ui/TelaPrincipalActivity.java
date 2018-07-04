package com.project.silas.gerenciadordesenhas.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

import butterknife.ButterKnife;

public class TelaPrincipalActivity extends AppCompatActivity {

    public static final String TAG_USUARIO_LOGADO = "usuarioLogado";

    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal_activity);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null){
            this.usuarioLogado = getIntent().getParcelableExtra(TAG_USUARIO_LOGADO);
            this.getActionBar().setTitle(this.usuarioLogado.getNomeUsuario());
        } else {
            Toast.makeText(this, "erro ao carregar dados do Usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Chegou na tela principal", Toast.LENGTH_SHORT).show();

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
}
