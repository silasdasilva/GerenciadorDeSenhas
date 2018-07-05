package com.project.silas.gerenciadordesenhas.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public TelaPrincipalAdapter(Context context, TelaPrincipalManager telaPrincipalManager, Usuario usuarioLogado, String queryPesquisa, OperationListener<Void> listenerUI){
        this.contexto = context;
        this.telaPrincipalManager = telaPrincipalManager;
        this.setHasStableIds(true);
        preencheLista(usuarioLogado, queryPesquisa, listenerUI);
    }

    @Override
    public long getItemId(int position) {
        this.cursor.moveToPosition(position);
        return new Site(this.cursor).setId(this.cursor.getLong(this.cursor.getColumnIndex(Site.Metadata.PK_ALIAS))).getId();
    }

    @NonNull
    @Override
    public TelaPrincipalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int parent) {
        return new ViewHolder(LayoutInflater.from(this.contexto).inflate(R.layout.tela_principal_item_activity, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull TelaPrincipalAdapter.ViewHolder viewHolder, int position) {

        Log.i("telaPrincipalAdapter", "Posição Retornada: " + position);

        if (this.cursor.getCount() > 0) {
            this.cursor.moveToPosition(position);
            Site site = new Site(this.cursor).setId(this.cursor.getLong(this.cursor.getColumnIndex(Site.Metadata.PK_ALIAS))));

            holder.bindInspecao(site);
        }
    }

    @Override
    public int getItemCount() {
        return this.cursor != null ? this.cursor.getCount() : 0;
    }

    public void preencheLista(Usuario usuarioLogado, String queryPesquisa, final OperationListener<Void> listener) {
        this.telaPrincipalManager.buscarLogins(usuarioLogado, queryPesquisa, new OperationListener<Cursor>(){
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
            if (this.cursor.getLong(this.cursor.getColumnIndex(Site.Metadata.PK_ALIAS)) == idSiteSelecionado){
                setPosicaoClicada(this.cursor.getPosition());
                setSiteSelecionado(new Site(this.cursor).setId(this.cursor.getLong(this.cursor.getColumnIndex(Site.Metadata.PK_ALIAS)))
                        .setUsuario(new Usuario(this.cursor).setId(this.cursor.getLong(this.cursor.getColumnIndex(Usuario.Metadata.PK_ALIAS))));

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

        private TextView tvItemUrlTelaPrincipal;
        private TextView tvItemEmailTelaPrincipal;
        private TextView tvItemSenhaTelaPrincipal;
        private Site site;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemUrlTelaPrincipal = itemView.findViewById(R.id.tv_item_url_tela_principal);
            tvItemEmailTelaPrincipal = itemView.findViewById(R.id.tv_item_email_tela_principal);
            tvItemSenhaTelaPrincipal = itemView.findViewById(R.id.tv_item_senha_tela_principal);
        }
    }

    private void bindInspecao(Site site) {
        this.site = site;

        tvItemUrlTelaPrincipal.setText(site.getUrlSite);
        tvItemEmailTelaPrincipal.setText(site.getEmailSite());
        tvItemSenhaTelaPrincipal.setText(site.getSenhaSite());

        customizarSelecao();
    }

    private void customizarSelecao() {
        if (getSiteSelecionado() != null && getSiteSelecionado().getId().equals(this.site.getId())){
            this.itemView.setBackgroundColor(this.itemView.getResources().getColor(R.color.cardviewSelectedBackground));
        } else {
            this.itemView.setBackgroundColor(this.itemView.getResources().getColor(R.color.cardviewNormalBackground));
        }
    }*/
}
