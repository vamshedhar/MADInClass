package io.vamshedhar.areacalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView selectedShape;
    private TextView resultText;
    private TextView length2Label;
    private EditText length1EditText;
    private EditText length2EditText;

    private static final double PI = 3.1416;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedShape = (TextView) findViewById(R.id.selectedFigureLabel);
        resultText = (TextView) findViewById(R.id.resultTextView);
        length2Label = (TextView) findViewById(R.id.length2Label);
        length1EditText = (EditText) findViewById(R.id.length1EditText);
        length2EditText = (EditText) findViewById(R.id.length2EditText);

    }

    public void hideLength2Components(){
        length2Label.setVisibility(View.INVISIBLE);
        length2EditText.setVisibility(View.INVISIBLE);
        resultText.setText(getResources().getString(R.string.resultText));
    }

    public void showLength2Components(){
        length2Label.setVisibility(View.VISIBLE);
        length2EditText.setVisibility(View.VISIBLE);
        resultText.setText(getResources().getString(R.string.resultText));
    }

    public void onTriangleClick(View view){
        selectedShape.setText(getResources().getString(R.string.triangle));
        showLength2Components();
    }

    public void onSquareCircleClick(View view){
        selectedShape.setText(getResources().getString(R.string.square));
        hideLength2Components();
    }

    public void onCircleClick(View view){
        selectedShape.setText(getResources().getString(R.string.circle));
        hideLength2Components();
    }

    public void onClearClick(View view) {
        selectedShape.setText(getResources().getString(R.string.selectedFigureText));
        showLength2Components();

        length1EditText.setText(getResources().getString(R.string.length1Value));
        length2EditText.setText(getResources().getString(R.string.length2Value));
    }

    public void calculateTriangleArea(){
        double length1 = Double.parseDouble(length1EditText.getText().toString());
        double length2 = Double.parseDouble(length2EditText.getText().toString());

        double area = length1 * length2 / 2;

        resultText.setText(area + "");
    }

    public void calculateSquareArea(){
        double length1 = Double.parseDouble(length1EditText.getText().toString());

        double area = length1 * length1;

        resultText.setText(area + "");
    }

    public void calculateCircleArea(){
        double length1 = Double.parseDouble(length1EditText.getText().toString());

        double area = PI * length1 * length1;

        resultText.setText(area + "");
    }

    public void onCalculateClick(View view){
        String shape = selectedShape.getText().toString();

        try{
            if(shape.equals(getResources().getString(R.string.circle))){
                calculateCircleArea();
            } else if(shape.equals(getResources().getString(R.string.triangle))){
                calculateTriangleArea();
            } else if(shape.equals(getResources().getString(R.string.square))){
                calculateSquareArea();
            } else {
                Toast.makeText(this, getResources().getString(R.string.selectAShapeError), Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e){
            Toast.makeText(this, getResources().getString(R.string.invalidNumberError), Toast.LENGTH_LONG).show();
        }


    }
}
