package com.project.silas.gerenciadordesenhas.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.silas.gerenciadordesenhas.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SitesInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_title_into_tela_principal)
    protected TextView tvTitleInfoTelaPrincipal;

    @BindView(R.id.tv_info_tela_principal)
    protected TextView tvInfoTelaPrincipal;

    public static final String CHAVE_INTENT_TOTALSITES = "totalSites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sites_info_activity);
        ButterKnife.bind(this);

        tvTitleInfoTelaPrincipal.setText(R.string.st_title_info_tela_principal);

        if (getIntent().getExtras().get(CHAVE_INTENT_TOTALSITES) != null){
            tvInfoTelaPrincipal.setText(String.valueOf(": " + getIntent().getExtras().getInt(CHAVE_INTENT_TOTALSITES)));
        }
    }
}
