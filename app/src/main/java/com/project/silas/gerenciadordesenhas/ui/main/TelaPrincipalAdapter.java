package com.project.silas.gerenciadordesenhas.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.managers.TelaPrincipalManager;

public class TelaPrincipalAdapter extends RecyclerView.Adapter<TelaPrincipalAdapter.ViewHolder> {

    private Context contexto;
    private TelaPrincipalManager telaPrincipalManager;
    private Cursor cursor;

    private Site siteSelecionado;
    private int posicaoClicada = -1;

    public TelaPrincipalAdapter(Context context, TelaPrincipalManager telaPrincipalManager, String queryPesquisa, OperationListener<Void> listenerUI){
        this.contexto = context;
        this.telaPrincipalManager = telaPrincipalManager;
        this.setHasStableIds(true);
        preencheLista(queryPesquisa, listenerUI);
    }

    @Override
    public long getItemId(int position) {
        if (this.getItemCount() <= 0 || position < 0) return -1;
        this.cursor.moveToPosition(position);
        if (this.cursor.isAfterLast()) this.cursor.moveToPrevious();
        return new Site(this.cursor).getId();
    }

    @NonNull
    @Override
    public TelaPrincipalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tela_principal_item_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TelaPrincipalAdapter.ViewHolder holder, int position) {

        Log.i("telaPrincipalAdapter", "Posição Retornada: " + position);

        if (this.cursor.getCount() > 0) {
            this.cursor.moveToPosition(position);
            Site site = new Site(this.cursor);

            holder.bindInspecao(site);
        }
    }

    @Override
    public int getItemCount() {
        return this.cursor == null ? 0 : this.cursor.getCount();
    }

    public void preencheLista(String queryPesquisa, final OperationListener<Void> listener) {
        this.telaPrincipalManager.buscarLogins(queryPesquisa, new OperationListener<Cursor>(){
            @Override
            public void onSuccess(Cursor result) {
                cursor = result;
                notifyDataSetChanged();
                listener.onSuccess(null);
            }
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                Log.i("telaPrincipalAdapter", "Erro ao carregar lista: " + error);
                listener.onError(error);
            }
        });
    }

    public void procuraSite(long idSiteSelecionado){
        Log.i("telaPrincipalAdapter", "Id Clicado: " + idSiteSelecionado);
        this.cursor.moveToFirst();
        while (!this.cursor.isAfterLast()){
            Site site = new Site(this.cursor);
            if (site.getId() == idSiteSelecionado){
                setPosicaoClicada(this.cursor.getPosition());
                setSiteSelecionado(new Site(this.cursor));
                Log.i("telaPrincipalAdapter", "Site Selecionado: " + this.siteSelecionado.getId());
                return;
            }
            this.cursor.moveToNext();
        }
        setPosicaoClicada(-1);
        setSiteSelecionado(null);
    }

    public void setPosicaoClicada (int positionClick){
        this.posicaoClicada = positionClick;
    }

    public int getPosicaoClicada () {
        return this.posicaoClicada;
    }

    public void setSiteSelecionado(Site site) {
        this.siteSelecionado = site;
    }

    public Site getSiteSelecionado() {
        return this.siteSelecionado;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivItemLogoTelaPrincipal;
        private TextView tvItemNomeTelaPrincipal;
        private TextView tvItemUrlTelaPrincipal;
        private TextView tvItemLoginTelaPrincipal;
        private Site site;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItemLogoTelaPrincipal = itemView.findViewById(R.id.iv_item_logo_tela_principal);
            tvItemNomeTelaPrincipal = itemView.findViewById(R.id.tv_item_nome_tela_principal);
            tvItemUrlTelaPrincipal = itemView.findViewById(R.id.tv_item_url_tela_principal);
            tvItemLoginTelaPrincipal = itemView.findViewById(R.id.tv_item_login_tela_principal);
        }


        private void bindInspecao(final Site site) {
            this.site = site;

            tvItemNomeTelaPrincipal.setText(this.site.getNomeSite());
            tvItemUrlTelaPrincipal.setText(this.site.getUrlSite());
            tvItemLoginTelaPrincipal.setText(this.site.getLoginSite());

            TelaPrincipalAdapter.this.telaPrincipalManager.buscarLogoSite(this.site, new OperationListener<Bitmap>(){
                @Override
                public void onSuccess(Bitmap result) {
                    if (result != null){
                        site.setLogoSite(result);
                        ivItemLogoTelaPrincipal.setImageBitmap(result);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    super.onError(error);
                    error.printStackTrace();
                    Toast.makeText(TelaPrincipalAdapter.this.contexto, "Erro ao carregar logo do site! Verifique sua internet", Toast.LENGTH_SHORT).show();
                }
            });

            customizarSelecao();
        }

        private void customizarSelecao() {
            if (getSiteSelecionado() != null && getSiteSelecionado().getId() == this.site.getId()) {
                this.itemView.setBackgroundColor(this.itemView.getResources().getColor(R.color.cardviewSelectedBackground));
            } else {
                this.itemView.setBackgroundColor(this.itemView.getResources().getColor(R.color.cardviewNormalBackground));
            }
        }
    }
}
