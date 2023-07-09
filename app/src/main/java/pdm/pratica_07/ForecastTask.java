package pdm.pratica_07;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ForecastTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = ForecastTask.class.getSimpleName();
    private List<String> forecast = null;

    private ForecastListener listener = null;
    private final String APPID = "a11ac945f2360e8cf7d496e7cb53dc00";
    public interface ForecastListener { void showForecast(List<String> forecast);}
    public ForecastTask(ForecastListener listener) { this.listener = listener; }

    /*O construtor ForecastTask recebe um objeto do tipo ForecastListener e atribui-o ao campo listener.*/

   /* A classe ForecastTask estende AsyncTask<String, Void, List<String>>.
     Onde String é o tipo de entrada, Void é o tipo usado para atualizar o progresso e
     List<String> é o tipo de retorno da tarefa assíncrona.

    O campo LOG_TAG é uma string usada para identificar as mensagens de log da classe.
    A lista forecast é inicializada como null para armazenar os dados da previsão do tempo.

    A interface ForecastListener é definida com um método showForecast(List<String> forecast).
    Essa interface será usada para notificar o resultado da previsão do tempo para o ouvinte.

    Se ocorrerem exceções durante o processamento, como erros de conexão ou parsing JSON,
    os erros serão registrados nos logs.*/


    /* O método doInBackground é sobrescrito para realizar o trabalho em segundo plano.
    Ele recebe uma string como parâmetro que representa a localização para a qual a previsão do tempo será solicitada.
    Dentro do método doInBackground, é criada uma conexão HTTP com a API do OpenWeatherMap e é
    feita uma solicitação GET para obter a previsão do tempo.
    A URL da solicitação é construída usando a classe Uri.Builder e os parâmetros necessários são adicionados.
    A resposta da API é lida em um InputStream, convertido em uma string e
    passado para o método ForecastParser.getDataFromJson() para extrair os dados da previsão.*/

    @Override
    protected List<String> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String locationString = params[0];
        String forecastJson = null;
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https");
            builder.authority("api.openweathermap.org");
            builder.appendPath("data/2.5/forecast/daily");
            builder.appendQueryParameter("q", locationString);
            builder.appendQueryParameter("mode", "json");
            builder.appendQueryParameter("units","metric");
            builder.appendQueryParameter("cnt","7");
            builder.appendQueryParameter("APPID", APPID);
            URL url = new URL(builder.build().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                forecastJson = null;
            } else {
                forecastJson = buffer.toString();
            }
            forecast = ForecastParser.getDataFromJson(forecastJson, 7);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error ", e);
        } finally{
            if (urlConnection != null) urlConnection.disconnect();
            if (reader != null) {
                try { reader.close(); }
                catch (final IOException e){ Log.e(LOG_TAG, "Error closing stream", e);}
            }
        }
        return forecast;
    }

    /* O método onPostExecute é chamado após a conclusão do método doInBackground.
    Ele recebe o resultado da previsão do tempo como uma lista de strings.
    Neste método, os dados da previsão do tempo são impressos no log e o método showForecast do
     objeto listener é chamado para notificar o ouvinte com os dados da previsão.*/

    @Override
    protected void onPostExecute(List<String> resultStrs) {
        for (String s : resultStrs)
            Log.v(LOG_TAG, "Forecast entry: " + s);
        listener.showForecast(resultStrs);
    }
}
