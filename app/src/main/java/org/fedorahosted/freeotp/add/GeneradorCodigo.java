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
import org.fedorahosted.freeotp.TokenCode;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GeneradorCodigo{

    private static GeneradorCodigo generadorCodigo;
    private Context context;
    public static final int CODE_OK = 1;
    public static final int CODE_FAIL = 2;
    private Handler mHandler;
    public static final String FAILURE_REASON = "failure_reason";
    public static final String GOOD_REASON = "good_reason";

    private String algo;
    private byte[] secret;
    private int digits;
    private long counter;
    private int period;

    /**
     *
     * @param context
     * @return
     */
    public static synchronized GeneradorCodigo getInstance(Context context) {
        if (generadorCodigo == null) {
            generadorCodigo = new GeneradorCodigo(context);
        }

        return generadorCodigo;
    }

    /**
     *
     * @param context
     */
    private GeneradorCodigo(Context context) {
        this.context = context;
        // algoritmo
        algo = "sha1";
        // digitos
        digits = 6;
        // periodo
        period = 20; // 20 segundos // TODO: Cambiar por el bueno
        // secret
        secret = "6A4E94A11E9E723F690B60C3B351EEB4".getBytes(); //TODO: Obtener el android ID
    }

    public void generarCodigo(Handler handler){
        this.mHandler = handler;

        TokenCode codes = generateCodes();

        Log.d("AddActivity ", "CODE = " + codes.getCurrentCode());

        String codigo = codes.getCurrentCode();

        Message msg = mHandler.obtainMessage(CODE_OK);
        Bundle bundle = new Bundle();
        bundle.putString(GOOD_REASON,codigo);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    private String getHOTP(long counter) {
        // Encode counter in network byte order
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(counter);

        // Create digits divisor
        int div = 1;
        for (int i = digits; i > 0; i--)
            div *= 10;

        // Create the HMAC
        try {
            Mac mac = Mac.getInstance("Hmac" + algo);
            mac.init(new SecretKeySpec(secret, "Hmac" + algo));

            // Do the hashing
            byte[] digest = mac.doFinal(bb.array());

            // Truncate
            int binary;
            int off = digest[digest.length - 1] & 0xf;
            binary = (digest[off] & 0x7f) << 0x18;
            binary |= (digest[off + 1] & 0xff) << 0x10;
            binary |= (digest[off + 2] & 0xff) << 0x08;
            binary |= (digest[off + 3] & 0xff);
            binary = binary % div;

            // Zero pad
            String hotp = Integer.toString(binary);
            while (hotp.length() != digits)
                hotp = "0" + hotp;

            return hotp;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    // NOTE: This may change internal data. You MUST save the token immediately.
    public TokenCode generateCodes() {

        Log.d("Token","generateCodes ");
        long cur = System.currentTimeMillis();

        Log.d("Token","TOTP ");
        long counter = cur / 1000 / period;

        return new TokenCode(getHOTP(counter + 0),
                (counter + 0) * period * 1000,
                (counter + 1) * period * 1000);

    }
}
