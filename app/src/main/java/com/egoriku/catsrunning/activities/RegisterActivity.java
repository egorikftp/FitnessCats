package com.egoriku.catsrunning.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.egoriku.catsrunning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String DOUBLE_PASSWORD_KEY = "DOUBLE_PASSWORD_KEY";
    private static final String HAVE_ACCOUNT_TEXT = "HAVE_ACCOUNT_TEXT";
    private static final String BTN_REGISTER_TEXT = "BTN_REGISTER_TEXT";
    private static final String TOOLBAR_TEXT = "TOOLBAR_TEXT";

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutDoublePassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextDoublePassword;
    private Button loginBtn;
    private Button registerBtn;
    private LinearLayout linearLayoutRegister;

    private String emptyEmail;
    private String emptyPassword;
    private String emptyDoublePassword;
    private String errorEmail;
    private String passwordsDontMatch;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    private String textRegistration;
    private String textLogin;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.text_input_layout_password);
        inputLayoutDoublePassword = (TextInputLayout) findViewById(R.id.text_input_layout_double_password);
        editTextEmail = (EditText) findViewById(R.id.entrance_reg_email);
        editTextPassword = (EditText) findViewById(R.id.entrance_reg_password);
        editTextDoublePassword = (EditText) findViewById(R.id.entrance_reg_double_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        registerBtn = (Button) findViewById(R.id.btn_register);
        linearLayoutRegister = (LinearLayout) findViewById(R.id.linear_layout_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        loginBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        emptyEmail = getString(R.string.empty_email);
        emptyPassword = getString(R.string.empty_password);
        emptyDoublePassword = getString(R.string.empty_double_password);
        errorEmail = getString(R.string.error_email);
        passwordsDontMatch = getString(R.string.password_dont_match);

        textRegistration = getString(R.string.btn_text_registration);
        textLogin = getString(R.string.btn_text_login);

        registerBtn.setText(textRegistration);
        loginBtn.setText(textLogin);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(textRegistration);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginBtn.getText().toString().equals(textLogin)) {
                    inputLayoutDoublePassword.setVisibility(View.GONE);
                    loginBtn.setText(textRegistration);
                    registerBtn.setText(textLogin);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(textLogin);
                    }
                    return;
                }

                if (loginBtn.getText().toString().equals(textRegistration)) {
                    inputLayoutDoublePassword.setVisibility(View.VISIBLE);
                    loginBtn.setText(textLogin);
                    registerBtn.setText(textRegistration);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(textRegistration);
                    }
                    return;
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerBtn.getText().equals(textRegistration) && !tryRegister() && isNetworkAvailable(getApplicationContext())) {
                    showProgressDialog();
                    firebaseAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dismissProgressDialog();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        dismissProgressDialog();
                                        Snackbar.make(linearLayoutRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                if (registerBtn.getText().equals(textLogin) && !tryLogin() && isNetworkAvailable(getApplicationContext())) {
                    showProgressDialog();
                    firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dismissProgressDialog();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        dismissProgressDialog();
                                        Snackbar.make(linearLayoutRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    private boolean tryLogin() {
        boolean hasError = false;
        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);

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
        return hasError;
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


    private boolean showProgressDialog() {
        if (progressDialog != null) {
            return false;
        }

        progressDialog = ProgressDialog.show(
                this,
                getString(R.string.progress_dialog_register_title),
                getString(R.string.progress_dialog_register_message),
                true,
                false
        );
        return true;
    }


    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Snackbar.make(linearLayoutRegister, getString(R.string.no_ethernet_connection), Snackbar.LENGTH_SHORT).show();
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EMAIL_KEY, inputLayoutEmail.getVisibility());
        outState.putInt(PASSWORD_KEY, inputLayoutPassword.getVisibility());
        outState.putInt(DOUBLE_PASSWORD_KEY, inputLayoutDoublePassword.getVisibility());
        outState.putString(BTN_REGISTER_TEXT, registerBtn.getText().toString());
        outState.putString(HAVE_ACCOUNT_TEXT, loginBtn.getText().toString());
        outState.putString(TOOLBAR_TEXT, toolbar.getTitle().toString());
        super.onSaveInstanceState(outState);
    }


    @SuppressWarnings("WrongConstant")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        inputLayoutEmail.setVisibility(savedInstanceState.getInt(EMAIL_KEY));
        inputLayoutPassword.setVisibility(savedInstanceState.getInt(PASSWORD_KEY));
        inputLayoutDoublePassword.setVisibility(savedInstanceState.getInt(DOUBLE_PASSWORD_KEY));
        registerBtn.setText(savedInstanceState.getString(BTN_REGISTER_TEXT));
        loginBtn.setText(savedInstanceState.getString(HAVE_ACCOUNT_TEXT));
        toolbar.setTitle(savedInstanceState.getString(TOOLBAR_TEXT));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
