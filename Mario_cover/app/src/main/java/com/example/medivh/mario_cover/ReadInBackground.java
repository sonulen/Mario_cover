package com.example.medivh.mario_cover;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Medivh on 20.12.2016.
 */
class ReadInBackground extends AsyncTask<Void, Void, String> {

    static String info = "";
    String url = "";

    public ReadInBackground(String ur) {
        url = ur;
    }

    @Override
    protected String doInBackground(Void... voids) {
        //тут все выполняется в фоновом потоке
        try {
            return executeHttpGet(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String params) {
        //тут выполняется после завершения фонового потока в основном
        //так же тут можно делать операции с интерфейсом, если нужно
        info = params;
        getInfo();
    }

    //метод, который получает данные по ссылке
    public static String executeHttpGet(String uri) throws Exception
    {
        String result = "";
        try
        {
            URL url = new URL(uri);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null)
            {
                result +=str;
            }
            in.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String getInfo() {
        return info;
    }
}
