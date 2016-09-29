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
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.LOGIN;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String DOUBLE_PASSWORD_KEY = "DOUBLE_PASSWORD_KEY";
    private static final String NAME_KEY = "NAME_KEY";
    private static final String SURNAME_KEY = "SURNAME_KEY";
    private static final String SIGN_GOOGLE_BTN_KEY = "SIGN_GOOGLE_BTN_KEY";
    private static final String HAVE_ACCOUNT_TEXT = "HAVE_ACCOUNT_TEXT";
    private static final String BTN_REGISTER_TEXT = "BTN_REGISTER_TEXT";
    private static final String TOOLBAR_TEXT = "TOOLBAR_TEXT";
    private static final int RC_SIGN_IN = 4706;
    public static final String KEY_LOGIN_EXTRA = "KEY_LOGIN_EXTRA";

    private GoogleApiClient googleApiClient;

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutDoublePassword;
    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutSurname;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextDoublePassword;
    private EditText editTextName;
    private EditText editTextSurname;
    private TextView loginBtn;
    private Button registerBtn;
    private SignInButton signInGoogleBtn;
    private LinearLayout linearLayoutRegister;

    private String emptyEmail;
    private String emptyPassword;
    private String emptyDoublePassword;
    private String errorEmail;
    private String emptyName;
    private String emptySurname;
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
        inputLayoutName = (TextInputLayout) findViewById(R.id.text_input_layout_name);
        inputLayoutSurname = (TextInputLayout) findViewById(R.id.text_input_layout_surname);

        editTextEmail = (EditText) findViewById(R.id.entrance_reg_email);
        editTextPassword = (EditText) findViewById(R.id.entrance_reg_password);
        editTextDoublePassword = (EditText) findViewById(R.id.entrance_reg_double_password);
        editTextName = (EditText) findViewById(R.id.entrance_reg_name);
        editTextSurname = (EditText) findViewById(R.id.entrance_reg_surname);

        loginBtn = (TextView) findViewById(R.id.text_view_login);
        registerBtn = (Button) findViewById(R.id.btn_register);
        signInGoogleBtn = (SignInButton) findViewById(R.id.sign_in_google_btn);
        linearLayoutRegister = (LinearLayout) findViewById(R.id.linear_layout_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        loginBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        emptyEmail = getString(R.string.empty_email);
        emptyPassword = getString(R.string.empty_password);
        emptyDoublePassword = getString(R.string.empty_double_password);
        errorEmail = getString(R.string.error_email);
        emptyName = getString(R.string.empty_name);
        emptySurname = getString(R.string.empty_surname);
        passwordsDontMatch = getString(R.string.password_dont_match);

        textRegistration = getString(R.string.btn_text_registration);
        textLogin = getString(R.string.btn_text_login);

        registerBtn.setText(textLogin);
        loginBtn.setText(textRegistration);
        setSupportActionBar(toolbar);
        inputLayoutDoublePassword.setVisibility(View.GONE);
        inputLayoutName.setVisibility(View.GONE);
        inputLayoutSurname.setVisibility(View.GONE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(textLogin);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        initializeGoogle();

        signInGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                App.getInstance().getState().setStartTaskAuthentification(true);
                signIn();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginBtn.getText().toString().equals(textLogin)) {
                    inputLayoutDoublePassword.setVisibility(View.GONE);
                    inputLayoutName.setVisibility(View.GONE);
                    inputLayoutSurname.setVisibility(View.GONE);
                    loginBtn.setText(textRegistration);
                    registerBtn.setText(textLogin);
                    signInGoogleBtn.setVisibility(View.VISIBLE);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(textLogin);
                    }
                    return;
                }

                if (loginBtn.getText().toString().equals(textRegistration)) {
                    inputLayoutDoublePassword.setVisibility(View.VISIBLE);
                    inputLayoutName.setVisibility(View.VISIBLE);
                    inputLayoutSurname.setVisibility(View.VISIBLE);
                    loginBtn.setText(textLogin);
                    registerBtn.setText(textRegistration);
                    signInGoogleBtn.setVisibility(View.GONE);

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
                    App.getInstance().getState().setStartTaskAuthentification(true);
                    firebaseAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(String.format("%s %s", editTextSurname.getText().toString().trim(), editTextName.getText().toString().trim()))
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dismissProgressDialog();
                                                            App.getInstance().getState().setStartTaskAuthentification(false);
                                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                            finish();
                                                        } else {
                                                            dismissProgressDialog();
                                                            App.getInstance().getState().setStartTaskAuthentification(false);
                                                            Snackbar.make(linearLayoutRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        dismissProgressDialog();
                                        App.getInstance().getState().setStartTaskAuthentification(false);
                                        Snackbar.make(linearLayoutRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                if (registerBtn.getText().equals(textLogin) && !tryLogin() && isNetworkAvailable(getApplicationContext())) {
                    showProgressDialog();
                    App.getInstance().getState().setStartTaskAuthentification(true);
                    firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dismissProgressDialog();
                                        App.getInstance().getState().setStartTaskAuthentification(false);
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class).putExtra(KEY_LOGIN_EXTRA, LOGIN));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    } else {
                                        dismissProgressDialog();
                                        App.getInstance().getState().setStartTaskAuthentification(false);
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
        inputLayoutName.setError(null);
        inputLayoutSurname.setError(null);

        if (editTextEmail.getText().toString().trim().length() == 0) {
            inputLayoutEmail.setError(emptyEmail);
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()) {
            inputLayoutEmail.setError(errorEmail);
            hasError = true;
        }

        if (editTextName.getText().toString().trim().length() == 0) {
            inputLayoutName.setError(emptyName);
            hasError = true;
        }

        if (editTextSurname.getText().toString().trim().length() == 0) {
            inputLayoutSurname.setError(emptySurname);
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


    private void initializeGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                dismissProgressDialog();
                App.getInstance().getState().setStartTaskAuthentification(false);
                Snackbar.make(linearLayoutRegister, R.string.register_activity_snackbar_error_auth_google, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dismissProgressDialog();
                            App.getInstance().getState().setStartTaskAuthentification(false);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class).putExtra(KEY_LOGIN_EXTRA, LOGIN));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            dismissProgressDialog();
                            App.getInstance().getState().setStartTaskAuthentification(false);
                            Snackbar.make(linearLayoutRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }

                });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (App.getInstance().getState() == null) {
            App.getInstance().createState();
        }

        if (App.getInstance().getState().isStartTaskAuthentification()) {
            showProgressDialog();
        }
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
        outState.putInt(NAME_KEY, inputLayoutName.getVisibility());
        outState.putInt(SURNAME_KEY, inputLayoutSurname.getVisibility());
        outState.putInt(SIGN_GOOGLE_BTN_KEY, signInGoogleBtn.getVisibility());

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
        inputLayoutName.setVisibility(savedInstanceState.getInt(NAME_KEY));
        inputLayoutSurname.setVisibility(savedInstanceState.getInt(SURNAME_KEY));
        signInGoogleBtn.setVisibility(savedInstanceState.getInt(SIGN_GOOGLE_BTN_KEY));

        registerBtn.setText(savedInstanceState.getString(BTN_REGISTER_TEXT));
        loginBtn.setText(savedInstanceState.getString(HAVE_ACCOUNT_TEXT));
        toolbar.setTitle(savedInstanceState.getString(TOOLBAR_TEXT));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
