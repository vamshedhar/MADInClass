package io.vamshedhar.profilecreator;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * InClass 03
 * @author Vamshedhar Reddy Chintala
 * @author Anjani Nalla
 */
public class MainActivity extends AppCompatActivity {

    public static final int PROFILE_PIC_CODE = 10001;
    public static final String PROFILE_PIC_KEY = "SELECTED_PROFILE_PIC";
    public static final String USER_KEY = "USER";


    String name, email, department;
    int profileImage, currentMoodId;

    SeekBar currentMoodBar;
    ImageView currentMood;
    ImageView profilePicture;
    EditText nameTextBox;
    EditText emailTextBox;
    RadioGroup departmentGroup;

    TextView currentMoodText;

    int[] currentMoodImagesArray;
    int[] currentMoodTextArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentMoodImagesArray = new int[]{R.drawable.angry, R.drawable.sad, R.drawable.happy, R.drawable.awesome};
        currentMoodTextArray = new int[]{R.string.angry, R.string.sad, R.string.happy, R.string.awesome};

        currentMoodBar = (SeekBar) findViewById(R.id.currentMoodBar);
        currentMood = (ImageView) findViewById(R.id.currentMoodImageView);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        nameTextBox = (EditText) findViewById(R.id.nameTextBox);
        emailTextBox = (EditText) findViewById(R.id.emailTextBox);
        departmentGroup = (RadioGroup) findViewById(R.id.departmentRadioGroup);

        currentMoodText = (TextView) findViewById(R.id.currentMood);

        profileImage = R.drawable.select_avatar;

        RadioButton selectedDept = (RadioButton) findViewById(departmentGroup.getCheckedRadioButtonId());
        department = selectedDept.getText().toString();
        currentMoodText.setText(getResources().getString(currentMoodTextArray[currentMoodBar.getProgress()]));


        departmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton selectedDept = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                department = selectedDept.getText().toString();
            }
        });

        currentMoodBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentMoodId = i;
                currentMood.setImageResource(currentMoodImagesArray[currentMoodId]);
                currentMoodText.setText(getResources().getString(currentMoodTextArray[currentMoodId]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void onSubmitClick(View view){
        name = nameTextBox.getText().toString();
        email = emailTextBox.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please provide a valid Name!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!isValidEmail(email)){
            Toast.makeText(this, "Please provide a valid Email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if(profileImage == R.drawable.select_avatar){
            Toast.makeText(this, "Please select an Avatar!", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User(name, email, department, profileImage, currentMoodId);

        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        intent.putExtra(USER_KEY, user);
        startActivity(intent);
    }

    public void onProfilePictureClick(View view){
        Intent intent = new Intent(MainActivity.this, SelectAvatar.class);
        startActivityForResult(intent, PROFILE_PIC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PROFILE_PIC_CODE){
            if(resultCode == RESULT_OK){

                profileImage = data.getExtras().getInt(PROFILE_PIC_KEY);
                profilePicture.setImageResource(profileImage);
            }
        }
    }
}
