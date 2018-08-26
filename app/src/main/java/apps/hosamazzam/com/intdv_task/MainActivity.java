package apps.hosamazzam.com.intdv_task;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;

import apps.hosamazzam.com.intdv_task.helpers.IntentHelper;
import apps.hosamazzam.com.intdv_task.helpers.Utility;
import apps.hosamazzam.com.intdv_task.models.User;
import apps.hosamazzam.com.intdv_task.views.AuthActivity;

public class MainActivity extends AuthActivity implements AuthActivity.onGoLoginListener {

    private SignInButton mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        prepareView();
        listener();
    }

    public void initView() {
        mSignInButton = findViewById(R.id.sign_in_button);
        if (Utility.getUserLoginState(this)) {
            IntentHelper.goTOAndFinish(this, HomeActivity.class, null);
        }
    }

    public void prepareView() {
        registerGoLoginListener(this);
    }

    public void listener() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogle();
            }
        });
    }

    @Override
    public void onSuccess(GoogleSignInAccount account) {
        User user = new User();
        user.setId(account.getId());
        user.setIdToken(account.getId());
        user.setEmail(account.getIdToken());
        user.setFamilyName(account.getFamilyName());
        user.setDisplayName(account.getDisplayName());

        Utility.registerUserLogin(this, user);
        IntentHelper.goTOAndFinish(this, HomeActivity.class, null);
    }
}
