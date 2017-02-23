package tutorial.maps.google.googlemapsimagesapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    public void redirectToSignUpPage(View v) {
        Intent redirect = new Intent(WelcomeHomeActivity.this, SignUpActivity.class);
        startActivity(redirect);
    }

    public void redirectToSignInPage(View v) {
        Intent redirect = new Intent(WelcomeHomeActivity.this, SignUpActivity.class);
        startActivity(redirect);
    }

}