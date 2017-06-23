package com.egoriku.catsrunning.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.models.Firebase.UserInfo;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.egoriku.catsrunning.utils.UserInfoPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.USER_INFO;

public class AddUserInfoActivity extends AppCompatActivity {
    private EditText growthView;
    private EditText weigthView;
    private Button btnReady;
    private FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_info);
        growthView = (EditText) findViewById(R.id.activity_add_user_info_growth);
        weigthView = (EditText) findViewById(R.id.activity_add_user_info_weigth);
        btnReady = (Button) findViewById(R.id.activity_add_user_info_btn_ready);
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEror()) {
                    Toast.makeText(AddUserInfoActivity.this, getString(R.string.activity_add_user_info_empty), Toast.LENGTH_LONG).show();
                    return;
                }

                int growth;
                int weight;
                try {
                    growth = Integer.parseInt(growthView.getText().toString().trim());
                    weight = Integer.parseInt(weigthView.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(AddUserInfoActivity.this, getString(R.string.activity_add_user_info_not_digit), Toast.LENGTH_LONG).show();
                    return;
                }

                sendUserData(growth, weight);
            }
        });
    }

    private void sendUserData(int growth, int weight) {
        new UserInfoPreferences(this).writeUserData(growth, weight);

        UserInfo userInfo = new UserInfo(growth, weight, 21);
        FirebaseUtils.getInstance().getFirebaseDatabase()
                .child(USER_INFO)
                .child(user.getUid())
                .setValue(userInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Snackbar.make(btnReady, getString(R.string.activity_add_user_info_error_save) + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    finish();
                }
            }
        });
    }

    private boolean checkEror() {
        boolean isError = false;

        if (growthView.getText().toString().trim().length() == 0) {
            isError = true;
        }

        if (weigthView.getText().toString().trim().length() == 0) {
            isError = true;
        }
        return isError;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AddUserInfoActivity.class));
    }
}
