package kr.nbit.nsoldier.nsoldier;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            NetworkTask networkTask = new NetworkTask(email, password);
            networkTask.execute();
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private HttpURLConnection con = null;
        private StringBuffer str_param = new StringBuffer();

        ProgressDialog asyncDialog = new ProgressDialog(LoginActivity.this);


        public NetworkTask(String id, String pw) {
            str_param.append("id=").append(id).append("&");
            str_param.append("pw=").append(pw);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로그인중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://app.nbit.kr/app/login.php");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDefaultUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);

                OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
                os.write(str_param.toString());
                os.flush();

                con.connect();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String line;
                String page = "";
                while ((line = reader.readLine()) != null)
                    page += line;

                return page.replace("\n", "");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            asyncDialog.dismiss();

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            if(s.equals("true")) {
                SettingManager.ID = mEmailView.getText().toString();
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "올바른 계정을 입력하십시오.", Toast.LENGTH_LONG).show();
            }
        }
    }
}