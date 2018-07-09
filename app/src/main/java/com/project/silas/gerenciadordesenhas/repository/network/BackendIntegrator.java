package com.project.silas.gerenciadordesenhas.repository.network;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.project.silas.gerenciadordesenhas.BuildConfig;
import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.business.UsuarioBusiness;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToConnectServerException;

import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BackendIntegrator {
    protected Context context;
    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;
    public static final int METHOD_PUT = 2;
    public static final int METHOD_DELETE = 3;


    private OkHttpClient client;

    private UsuarioBusiness usuarioBusiness;

    public BackendIntegrator(Context context) {
        this.context = context;
        client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(60, TimeUnit.MINUTES).build();
        usuarioBusiness = new UsuarioBusiness(context);
    }


    /**
     * Realiza uma requisição sincrona ao servidor bastando apenas informar o endpoint que se deseja consumir.
     * @param method
     * @param urlEndpoint
     * @param requestPayload
     * @return
     * @throws FailedToConnectServerException
     */
    public JSONObject syncRequest(int method, String urlEndpoint, Map<String, String> requestPayload) throws FailedToConnectServerException {
        return syncRequest(method, urlEndpoint, requestPayload, null);
    }

    /**
     * Realiza uma requisição sincrona ao servidor bastando apenas informar o endpoint que se deseja consumir.
     * @param method
     * @param urlEndpoint
     * @param requestPayload
     * @param arquivos
     * @return
     */
    public JSONObject syncRequest(int method, String urlEndpoint, Map<String, String> requestPayload, Map<String, File> arquivos) throws FailedToConnectServerException {

        Log.e("nova", "Nova request solicitada, url: " + BuildConfig.BASE_URL + urlEndpoint + ". PayLoad: " + requestPayload);
        if(!this.isInternetAvailable()) throw new FailedToConnectServerException("Erro ao enviar/receber dados api");

        if(requestPayload == null) requestPayload = new HashMap<>();

        okhttp3.Response response = null;
        Request.Builder requestBuilder = new Request.Builder().url(BuildConfig.BASE_URL + urlEndpoint);

        //Classe responsável por controlar modos de energia, utilizado aqui para modo de economia de energia não travar recebimento de dados da API
        PowerManager pm = (PowerManager) this.context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock bloqueioDeEspera = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.context.getClass().getName());

        try {
            if(method == METHOD_POST) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                /*if(arquivos != null && arquivos.size() > 0) {
                    FileBusiness fileBusiness = new FileBusiness(context);
                    for(int f = 0; f < arquivos.size(); f++) {
                        String key = (String) arquivos.keySet().toArray()[f];
                        File arquivo = arquivos.get(key);
                        String mimetype = fileBusiness.getMimeType(arquivo);
                        builder.addFormDataPart(key, arquivo.getName(), RequestBody.create(MediaType.parse(mimetype), arquivo));
                    }
                }*/
                for(int i = 0; i < requestPayload.size(); i++) {
                    String key = String.valueOf(requestPayload.keySet().toArray()[i]);
                    builder.addFormDataPart(key, requestPayload.get(key));
                }

                RequestBody body = builder.build();

                requestBuilder.post(body);
                requestBuilder.header("content-type", "application/json");
            }
            if (method == METHOD_GET){
                requestBuilder.get();
                requestBuilder.header("authorization", requestPayload.get("token"));
            }
            Request request = requestBuilder.build();
            response = this.client.newCall(request).execute();

            bloqueioDeEspera.acquire();

            String strResponse = response.body().string();
            Log.e("sRequest", urlEndpoint);
            Log.e("sResponse", strResponse);
            JSONObject retorno = new JSONObject(strResponse);

            if(retorno.has("token")) {
                this.usuarioBusiness.atualizaToken(retorno.optString("token"));
            }
            return retorno;
        } catch (Exception e) {

            if(response != null) {
                Log.e("retorno", "Retorno da API: " + response.body().toString());
            }
            Log.e("retorno", "Exception: " + e.getMessage());
            e.printStackTrace();
            throw new FailedToConnectServerException(e.getMessage());
        } finally {
            if (bloqueioDeEspera.isHeld()) bloqueioDeEspera.release();
        }
    }

    /**
     * Retorna true se existe conectividade do dispositivo com o domínio da empresa.
     * @return
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName(BuildConfig.DOMAIN);
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
}
