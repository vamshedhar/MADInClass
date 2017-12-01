package io.vamshedhar.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

public class SelectAvatar extends AppCompatActivity {

    HashMap imageIdMap = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);

        imageIdMap.put(R.id.female1, R.drawable.avatar_f_1);
        imageIdMap.put(R.id.female2, R.drawable.avatar_f_2);
        imageIdMap.put(R.id.female3, R.drawable.avatar_f_3);
        imageIdMap.put(R.id.male1, R.drawable.avatar_m_1);
        imageIdMap.put(R.id.male2, R.drawable.avatar_m_2);
        imageIdMap.put(R.id.male3, R.drawable.avatar_m_3);
    }

    public void onImageClick(View view){
        Intent intent = new Intent();
        intent.putExtra(CreateContactActivity.PROFILE_PIC_KEY, (int) imageIdMap.get(view.getId()));
        setResult(RESULT_OK, intent);
        finish();
    }
}
