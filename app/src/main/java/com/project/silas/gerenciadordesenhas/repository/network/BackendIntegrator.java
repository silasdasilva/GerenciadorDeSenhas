package com.project.silas.gerenciadordesenhas.repository.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.BuildConfig;
import com.project.silas.gerenciadordesenhas.business.CadastroSiteBusiness;
import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.business.SessionSingletonBusiness;
import com.project.silas.gerenciadordesenhas.business.UsuarioBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToConnectServerException;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToWriteInternalDataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackendIntegrator {
    protected Context contexto;

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;
    public static final int METHOD_PUT = 2;
    public static final int METHOD_DELETE = 3;

    private OkHttpClient client;

    private UsuarioBusiness usuarioBusiness;
    private Usuario usuarioLogado;

    public BackendIntegrator(Context context) {
        this.contexto = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.MINUTES).build();
        this.usuarioBusiness = new UsuarioBusiness(context);
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();
    }

    /**
     * Realiza uma requisição sincrona a API bastando apenas informar o endpoint que se deseja consumir.
     * @param method
     * @param urlEndpoint
     * @param requestPayload
     * @param arquivos
     * @return
     */
    public JSONObject syncRequest(int method, String urlEndpoint, @Nullable Map<String, String> requestPayload, @Nullable Map<String, File> arquivos) throws FailedToConnectServerException {

        if(!this.isInternetAvailable()) throw new FailedToConnectServerException("Erro ao enviar/receber dados api");

        if(requestPayload == null) requestPayload = new HashMap<>();

        Response response = null;
        Request.Builder requestBuilder = new Request.Builder().url(BuildConfig.BASE_URL + urlEndpoint);

        //Classe responsável por controlar modos de energia, utilizado aqui para modo de economia de energia não travar recebimento de dados do webservice
        PowerManager pm = (PowerManager) this.contexto.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock bloqueioDeEspera = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.contexto.getClass().getName());

        try {
            if(method == METHOD_POST) {
                StringBuilder stringBuilder = new StringBuilder();
                String data = "{";
                for (String key : requestPayload.keySet()) {
                    stringBuilder.append("\"" + key + "\":\"" + requestPayload.get(key) + "\", ");
                }
                data += stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1).toString() + "}";

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), data);
                requestBuilder.post(body);
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
        }
    }

    public Bitmap syncRequestLogo(int method, String urlEndpoint, Site siteLogo) throws FailedToConnectServerException {
        Bitmap logoSite = null;

        if(!this.isInternetAvailable()) throw new FailedToConnectServerException("Erro ao enviar/receber dados api");

        Response response = null;
        Request.Builder requestBuilder = new Request.Builder().url(BuildConfig.BASE_URL + urlEndpoint);

        //Classe responsável por controlar modos de energia, utilizado aqui para modo de economia de energia não travar recebimento de dados do webservice
        PowerManager pm = (PowerManager) this.contexto.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock bloqueioDeEspera = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.contexto.getClass().getName());

        try {
            if (method == METHOD_GET) {
                requestBuilder.header("authorization", this.usuarioLogado.getTokenUsuario());
            }
            Request request = requestBuilder.build();
            response = this.client.newCall(request).execute();

            bloqueioDeEspera.acquire();


            /**
             * VERIFICAR PARA TERMINAR
             * */

            String strResponse = response.body().string();
            Log.e("sRequest", urlEndpoint);
            Log.e("sResponse", strResponse);

            byte[] bytesResponse = Base64.decode(strResponse, Base64.DEFAULT);
            logoSite = BitmapFactory.decodeByteArray(bytesResponse, 0, bytesResponse.length);

        } catch (Exception e) {
            e.printStackTrace();
            if(response != null) {
                Log.e("retorno", "Retorno da API: " + response.body().toString());
                return null;
            }
            Log.e("retorno", "Exception: " + e.getMessage());
            throw new FailedToConnectServerException(e.getMessage());
        }
        return logoSite;
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
