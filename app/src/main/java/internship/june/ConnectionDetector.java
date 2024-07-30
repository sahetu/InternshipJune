package internship.june;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean networkConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public boolean networkDisconnected() {
        if (networkConnected()) {
            return true;
        } else {
            final Dialog dialog = new Dialog(_context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.connection_checker);
            Button retry = (Button) dialog.findViewById(R.id.dialog_ok);
            Button connect = (Button) dialog.findViewById(R.id.dialog_cancel);
            dialog.show();
            dialog.setCancelable(false);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    networkConnected();
                }
            });
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    _context.startActivity(new Intent(Settings.ACTION_SETTINGS));

                }
            });
            return false;
        }

    }
}