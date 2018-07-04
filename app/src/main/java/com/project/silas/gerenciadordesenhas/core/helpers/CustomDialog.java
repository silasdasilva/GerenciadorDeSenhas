package com.project.silas.gerenciadordesenhas.core.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationListener;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ansilva on 29/08/2016.
 * Utilize essa classe como auxílio na criação de alertas.
 * Ela utiliza chaining para facilitar a customização.
 * Ex de utilização:
 *  => new CustomDialog(this).success("Salvo com sucesso").show();
 *  => new CustomDialog(this).error(R.string.core_msg_error).show();
 *  => new CustomDialog(this).success(R.string.core_msg_ok).withOkButton(new OperationListener<Void>(){ ...}).show();
 *  => new CustomDialog(this).success(R.string.core_msg_ok).withPositiveButton("Texto Customizado do botão", new OperationListener(){...}).show();
 *
 */
public class CustomDialog extends AlertDialog.Builder {
    public static final int DIALOG_ERROR = 0;
    public static final int DIALOG_SUCCESS = 1;
    public static final int DIALOG_INFO = 2;
    public static final int DIALOG_WARNING = 3;
    public static final int DIALOG_CONFIRM = 4;

    public static final int BUTTON_POSITIVE = 5;
    public static final int BUTTON_NEGATIVE = 6;

    private static List<AlertDialog> INSTANCES = new ArrayList<AlertDialog>();

    public CustomDialog(Context context) {
        super(context);
        this.defaultDialog();

    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.defaultDialog();
    }

    public CustomDialog defaultDialog()
    {
        this.setTitle(R.string.core_customdialog_defaultTitle);
        return this;
    }


    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        for(AlertDialog iDialog : INSTANCES) {
            iDialog.dismiss(); //evitando leak de memória
        }
        INSTANCES.add(dialog);
        return dialog;
    }

    /**
     * Atalho para criação de um alerta de confirmação. Informe a mensagem e um listener, que será configurado um alerta com a mensagem, o botão de ok, cancelar e o listener ouvindo ambos os botões.
     * @param msgId
     * @param listener
     * @return
     */
    public CustomDialog confirm(int msgId, OperationListener<Void> listener)
    {
        return this.confirm(getContext().getText(msgId), listener);
    }
    public CustomDialog confirm(CharSequence msg, OperationListener<Void> listener)
    {
        return this.withOkButton(listener)
                .withCancelButton(listener)
                .setMessage(msg, DIALOG_CONFIRM);
    }

    /**
     * Permite exibir uma caixa de confirmação customizando os botões de ok e cancel.
     * @param msgId
     * @param okButtonText
     * @param cancelButtonText
     * @param listener
     * @return
     */
    public CustomDialog confirm(int msgId, int okButtonText, int cancelButtonText, OperationListener listener)
    {
        return this.confirm(getContext().getText(msgId), getContext().getText(okButtonText), getContext().getText(cancelButtonText), listener);
    }
    public CustomDialog confirm(CharSequence msg, CharSequence okButtonText, CharSequence cancelButtonText, OperationListener listener)
    {
        return this.setMessage(msg, DIALOG_CONFIRM)
                .withPositiveButton(okButtonText, listener)
                .withNegativeButton(cancelButtonText, listener);
    }


    /**
     * Exibição do tipo de mensagem que deseja. O tipo deve ser uma constante dessa própria classe
     * @param msgId
     * @param msgType
     * @return
     */
    public CustomDialog setMessage(int msgId, int msgType)
    {
        return this.setMessage(getContext().getText(msgId), msgType);
    }


    /**
     * Exibição do tipo de mensagem que deseja. O tipo deve ser uma constante dessa própria classe
     * @param msgText
     * @param msgType
     * @return
     */
    public CustomDialog setMessage(CharSequence msgText, int msgType)
    {
        switch (msgType) {
            case DIALOG_CONFIRM:
                this.setIcon(android.R.drawable.ic_dialog_alert);
                break;
            case DIALOG_ERROR:
                this.setIcon(android.R.drawable.ic_dialog_alert);
                break;
            case DIALOG_INFO:
                this.setIcon(android.R.drawable.ic_dialog_info);
                break;
            case DIALOG_SUCCESS:
                this.setIcon(android.R.drawable.ic_dialog_info);
                break;
            case DIALOG_WARNING:
                this.setIcon(android.R.drawable.ic_dialog_alert);
                break;
            default:
                throw new InvalidParameterException();
        }
        this.setMessage(msgText);
        return this;
    }

    /**
     * Adiciona um botão de OK padrão no alerta. Recebe um listener onde é disparado o método onSuccess desse listener, quando o usuário clica no ok.
     * @param listener
     * @return
     */
    public CustomDialog withOkButton(final OperationListener<Void> listener)
    {
        return this.withPositiveButton(R.string.core_customdialog_defaultOkButton, listener);
    }

    /**
     * Adicinoa um botão de Cancelar padrão no alerta. Recebe um listener onde é disparado o método onCancel desse listener quando o usuário clica no cancelar.
     * @param listener
     * @return
     */
    public CustomDialog withCancelButton(final OperationListener<Void> listener)
    {
        return this.withNegativeButton(R.string.core_customdialog_defaultCancelButton, listener);
    }


    /**
     * Adiciona um botão positivo com texto customizado. Recebe um listener, onde é disparado o método onSuccess quando o usuário apertar esse botão.
     * @param text
     * @param listener
     * @return
     */
    public CustomDialog withPositiveButton(CharSequence text, final OperationListener<Void> listener)
    {
        return this.withCustomButton(text, BUTTON_POSITIVE, listener);
    }

    /**
     * Adiciona um botão positivo com texto customizado. Recebe um listener, onde é disparado o método onSuccess quando o usuário apertar esse botão.
     * @param text
     * @param listener
     * @return
     */
    public CustomDialog withPositiveButton(int text, final OperationListener<Void> listener)
    {
        return this.withPositiveButton(getContext().getString(text), listener);
    }

    /**
     * Adiciona um botão negativo com texto customizado. Recebe um listener, onde é disparado o método onCancel quando o usuário apertar esse botão.
     * @param text
     * @param listener
     * @return
     */
    public CustomDialog withNegativeButton(CharSequence text, final OperationListener<Void> listener)
    {
        return this.withCustomButton(text, BUTTON_NEGATIVE, listener);
    }

    /**
     * Adiciona um botão negativo com texto customizado. Recebe um listener, onde é disparado o método onCancel quando o usuário apertar esse botão.
     * @param text
     * @param listener
     * @return
     */
    public CustomDialog withNegativeButton(int text, final OperationListener<Void> listener)
    {
        return this.withNegativeButton(getContext().getString(text), listener);
    }


    /**
     * Criação de um progressDialog padrão.
     * Exemplo:
     *  new CustomDialog(this).progress().show();
     * @return
     */
    public ProgressDialog progress() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getContext().getString(R.string.core_customdialog_defaultProgressmessage));
        return progressDialog;
    }



    private CustomDialog withCustomButton(CharSequence text, int buttonType, final OperationListener<Void> listener)
    {
        if(buttonType == BUTTON_NEGATIVE) {
            this.setNegativeButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(listener == null) return;
                    listener.onCancel();
                }
            });
            return this;
        }

        if(buttonType == BUTTON_POSITIVE) {
            this.setPositiveButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(listener == null) return;
                    listener.onSuccess(null);
                }
            });
            return this;
        }
        throw new InvalidParameterException();
    }
}
