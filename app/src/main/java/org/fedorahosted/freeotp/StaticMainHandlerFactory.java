package org.fedorahosted.freeotp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by sgcmovil on 19/01/16.
 */
public class StaticMainHandlerFactory {

    // Debug
    private static final String TAG = "MainHandlerFactory";

    public static StaticHandler create(IStaticMainHandler ref) {
        return new StaticHandler(ref);
    }

    // This has to be nested.
    static class StaticHandler extends Handler {
        WeakReference<IStaticMainHandler> weakRef;

        public StaticHandler(IStaticMainHandler ref) {
            this.weakRef = new WeakReference<IStaticMainHandler>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakRef.get() == null) {
                Log.e(TAG, "StaticMainHandlerFactory. Referencia perdida");
                //throw new RuntimeException("Something goes wrong.");
            } else {
                weakRef.get().handleMessage(msg);
            }
        }
    }
}
