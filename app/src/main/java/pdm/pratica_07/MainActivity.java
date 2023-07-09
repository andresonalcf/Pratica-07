package pdm.pratica_07;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ForecastTask.ForecastListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void buttonOkClick(View view) {
        String cityName = ((EditText)findViewById(R.id.edit_city)).getText().toString();
        new ForecastTask(this).execute(cityName);
    }

    @Override
    public void showForecast(List<String> forecast) {
        if (forecast == null) {
            String cityName = ((EditText)findViewById(R.id.edit_city)).getText().toString();
            Toast toast = Toast.makeText(this, "Previsão não encontrada para " + cityName,
                        Toast.LENGTH_SHORT);
            toast.show();
        } else {
            ArrayList<CharSequence> data = new ArrayList<>(forecast);
            Intent intent = new Intent(this, ForecastActivity.class);
            intent.putCharSequenceArrayListExtra("data", data);
            startActivity(intent);
        }}


}