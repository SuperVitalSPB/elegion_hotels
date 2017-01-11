package ru.supervital.elegion.hotels;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendURL extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "elegion.SendURL";

    int PROGRESS_DLG_ID = 0;

    public String 			        mUrl;
    public Map <String, String> Params;
    public String 			        sMessage = "";
    public AppCompatActivity        mActivity;
    public HotelsListAdapter        mAdapter;


    /**
     * конструктор
     */
    public SendURL(String aUrl, Map<String, String> aParams, Boolean Result){
        super();
        mUrl = aUrl;
        Params = aParams;
    }

    /**
     * перед выполнением
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (Params!=null) {
            for(String sKey : Params.keySet()){
                String sK = String.format("${%s}", sKey);
                mUrl = mUrl.replace(sK, Params.get(sKey));
            }
        }
    }

    /**
     * в отдельном потоке
     * * @throws IOException
     */
    @Override
    protected Boolean doInBackground(String... aStrings) {
        Log.d(TAG, "Request: " + this.mUrl);
        publishProgress(new Void[]{});
        //DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        //HttpGet httpGet = new HttpGet(this.mUrl);
        try {
            //sMessage = EntityUtils.toString((HttpEntity)defaultHttpClient.execute((HttpUriRequest)httpGet).getEntity());
            URL url = new URL(this.mUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            StringBuffer chaine = new StringBuffer("");
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
            sMessage = chaine.toString();
            return true;
        }
        catch (Throwable e) {
            Log.e(TAG, "Request failed: " + this.mUrl);
            sMessage = e.getLocalizedMessage();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * после выполнения
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        ShowDialog(false);

        if (!this.isCancelled()) {
            super.onPostExecute(success);
        }

        if (!success) {
            String sMsg = "Сервер, подлец, не вернул данные!!!!\n";
            sMsg = sMsg + sMessage;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Ошибка");
            builder.setMessage(sMsg);
            builder.setCancelable(true);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * нажали отменить
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        ShowDialog(false);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        ShowDialog(true);
    }

    void ShowDialog(boolean  aShow){
        if (mActivity == null) return; // добавить проверку, есть ли реализация метода onCreateDialog
        if (aShow)
            mActivity.showDialog(PROGRESS_DLG_ID);
        else
            mActivity.dismissDialog(PROGRESS_DLG_ID);
    }
}

