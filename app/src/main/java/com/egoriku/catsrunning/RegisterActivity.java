package com.egoriku.catsrunning;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutDoublePassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextDoublePassword;
    private TextView textViewHaveAccount;
    private Button btnRegister;

    private String emptyEmail;
    private String emptyPassword;
    private String emptyDoublePassword;
    private String errorEmail;
    private String passwordsDontMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.text_input_layout_password);
        inputLayoutDoublePassword = (TextInputLayout) findViewById(R.id.text_input_layout_double_password);
        editTextEmail = (EditText) findViewById(R.id.entrance_reg_email);
        editTextPassword = (EditText) findViewById(R.id.entrance_reg_password);
        editTextDoublePassword = (EditText) findViewById(R.id.entrance_reg_double_password);
        textViewHaveAccount = (TextView) findViewById(R.id.already_have_account_text_view);
        btnRegister = (Button) findViewById(R.id.btn_register);

        emptyEmail = getString(R.string.empty_email);
        emptyPassword = getString(R.string.empty_password);
        emptyDoublePassword = getString(R.string.empty_double_password);
        errorEmail = getString(R.string.error_email);
        passwordsDontMatch = getString(R.string.password_dont_match);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tryRegister()) {
                    Toast.makeText(getApplicationContext(), "Можно регаться", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean tryRegister() {
        boolean hasError = false;

        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        inputLayoutDoublePassword.setError(null);

        if (editTextEmail.getText().toString().trim().length() == 0) {
            inputLayoutEmail.setError(emptyEmail);
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()) {
            inputLayoutEmail.setError(errorEmail);
            hasError = true;
        }

        if (editTextPassword.getText().toString().trim().length() == 0) {
            inputLayoutPassword.setError(emptyPassword);
            hasError = true;
        }

        if (editTextDoublePassword.getText().toString().trim().length() == 0) {
            inputLayoutDoublePassword.setError(emptyDoublePassword);
            hasError = true;
        } else if (!editTextPassword.getText().toString().equals(editTextDoublePassword.getText().toString())) {
            inputLayoutDoublePassword.setError(passwordsDontMatch);
            hasError = true;
        }
        return hasError;
    }
}
