package com.project.silas.gerenciadordesenhas.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SitesInfoActivity extends AppCompatActivity {

    public static final String CHAVE_INTENT_TOTALSITES = "totalSites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sites_info_activity);
    }
}
