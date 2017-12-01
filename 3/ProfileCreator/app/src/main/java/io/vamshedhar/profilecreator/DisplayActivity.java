package io.vamshedhar.profilecreator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * InClass 03
 * @author Vamshedhar Reddy Chintala
 * @author Anjani Nalla
 */
public class DisplayActivity extends AppCompatActivity {

    TextView nameValue;
    TextView emailValue;
    TextView deptValue;
    TextView moodValue;
    ImageView moodImage, profilePicValue;

    int[] currentMoodImagesArray;
    int[] currentMoodTextArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        currentMoodImagesArray = new int[]{R.drawable.angry, R.drawable.sad, R.drawable.happy, R.drawable.awesome};
        currentMoodTextArray = new int[]{R.string.angry, R.string.sad, R.string.happy, R.string.awesome};

        nameValue = (TextView) findViewById(R.id.nameValue);
        emailValue = (TextView) findViewById(R.id.emailValue);
        deptValue = (TextView) findViewById(R.id.departmentValue);
        moodValue = (TextView) findViewById(R.id.moodValue);
        moodImage = (ImageView) findViewById(R.id.moodImageValue);
        profilePicValue = (ImageView) findViewById(R.id.profilePicValue);

        if(getIntent().getExtras() != null){
            User user = (User) getIntent().getExtras().getSerializable(MainActivity.USER_KEY);

            nameValue.setText(user.name);
            emailValue.setText(user.email);
            deptValue.setText(user.department);
            moodValue.setText(getResources().getString(R.string.moodStart) + " " + getResources().getString(currentMoodTextArray[user.curentMood]));
            moodImage.setImageResource(currentMoodImagesArray[user.curentMood]);
            profilePicValue.setImageResource(user.profileImage);

        }
    }
}
