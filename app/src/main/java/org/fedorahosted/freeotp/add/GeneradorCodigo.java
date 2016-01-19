/*
 * FreeOTP
 *
 * Authors: Nathaniel McCallum <npmccallum@redhat.com>
 *
 * Copyright (C) 2013  Nathaniel McCallum, Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp.add;
import java.util.Locale;
import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.Token;
import org.fedorahosted.freeotp.TokenCode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;

public class GeneradorCodigo{

    private static GeneradorCodigo generadorCodigo;
    private Context context;
    public static final int CODE_OK = 1;
    public static final int CODE_FAIL = 2;
    private Handler mHandler;
    public static final String FAILURE_REASON = "failure_reason";
    public static final String GOOD_REASON = "good_reason";

    public static synchronized GeneradorCodigo getInstance(Context context) {
        if (generadorCodigo == null) {
            generadorCodigo = new GeneradorCodigo(context);
        }

        return generadorCodigo;
    }


    private GeneradorCodigo(Context context) {

        this.context = context;

    }

    public void generarCodigo(Handler handler){
        this.mHandler = handler;

        String tarjeta = "4522105577707772";
        String android_id = "6A4E94A11E9E723F690B60C3B351EEB4";
        String secret_ = "AAAAAAAA";
        String sha = "sha1";
        int tiempo = 20;
        int digit = 6;

        String issuer = Uri.encode(tarjeta);
        String label = Uri.encode(android_id);
        String secret = Uri.encode(secret_);
        String algorithm = sha;
        int interval = (tiempo);
        int digits = digit;

        String uri = String.format(Locale.US,
                "otpauth://%sotp/%s:%s?secret=%s&algorithm=%s&digits=%d&period=%d",
                "t", issuer, label,
                secret, algorithm, digits, interval);

        try {
            Gson gson = new Gson();
            Token token = new Token(uri);
            TokenCode codes = token.generateCodes();


            Log.d("AddActivity ", "ID = " + token.getID());
            Log.d("AddActivity ", "GSON = " + gson.toJson(token));
            Log.d("AddActivity ", "CODE = " + codes.getCurrentCode());

            String codigo = codes.getCurrentCode();



            Message msg = mHandler.obtainMessage(CODE_OK);
            Bundle bundle = new Bundle();
            bundle.putString(GOOD_REASON,codigo);
            msg.setData(bundle);
            mHandler.sendMessage(msg);




        } catch (Token.TokenUriInvalidException e) {

            Message msg = mHandler.obtainMessage(CODE_FAIL);
            Bundle bundle = new Bundle();
            bundle.putString(FAILURE_REASON, "No se pudo generar código");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            //Toast.makeText(this, R.string.invalid_token, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }





}
