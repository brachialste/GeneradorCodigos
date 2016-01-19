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

/*
 * Portions Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp;
import org.fedorahosted.freeotp.add.GeneradorCodigo;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMenuItemClickListener {

    private GeneradorCodigo generadorCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        setContentView(R.layout.main);

        generadorCodigo = GeneradorCodigo.getInstance(MainActivity.this);

        // Don't permit screenshots since these might contain OTP codes.
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        menu.findItem(R.id.action_scan).setVisible(ScanActivity.haveCamera());
//        menu.findItem(R.id.action_scan).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_add).setOnMenuItemClickListener(this);
        //menu.findItem(R.id.action_about).setOnMenuItemClickListener(this);
        return true;
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
//        case R.id.action_scan:
//            startActivity(new Intent(this, ScanActivity.class));
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//            return true;

        case R.id.action_add:
            generadorCodigo.generarCodigo(StaticMainHandlerFactory.create(mainHandler));
            return true;

//        case R.id.action_about:
//            startActivity(new Intent(this, AboutActivity.class));
//            return true;
        }

        return false;
    }

    private IStaticMainHandler mainHandler = new IStaticMainHandler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GeneradorCodigo.CODE_OK:
                    Log.d("MainActivity", "CODE_OK");

                    String code = msg.getData().getString(GeneradorCodigo.GOOD_REASON);

                    Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();

                    break;
                case GeneradorCodigo.CODE_FAIL:
                    Log.d("MainActivity", "CODE_FAIL");

                    String mensaje = msg.getData().getString(GeneradorCodigo.FAILURE_REASON);

                    Toast.makeText(MainActivity.this,mensaje,Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        Uri uri = intent.getData();
//        if (uri != null)
//            TokenPersistence.addWithToast(this, uri.toString());
//    }
}
