package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.flowershop.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private EditText etGmail, etPassword;
    private TextView tvError, tvForgotPassword;
    private FrameLayout loadingOverlay;
    private LottieAnimationView lottieLoading;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hiển thị tràn viền (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        // 1. Khởi tạo Firebase & Social Login
        mAuth = FirebaseAuth.getInstance();
        setupSocialLogins();

        // 2. Ánh xạ View
        initViews();

        // 3. Chạy Animation khởi động
        setupStartupAnimations();
    }

    private void setupSocialLogins() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void initViews() {
        loadingOverlay = findViewById(R.id.loadingOverlay);
        lottieLoading = findViewById(R.id.lottieLoading);
        etGmail = findViewById(R.id.etGmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        findViewById(R.id.btnLogin).setOnClickListener(this::performLoginWithAnimation);
        findViewById(R.id.btnRegister).setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));

        // THAY ĐỔI TẠI ĐÂY: Chuyển sang Activity Quên mật khẩu mới
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        setupSocialButtons();
    }

    private void setupSocialButtons() {
        ImageView btnGoogle = findViewById(R.id.btnGoogleLogin);
        ImageView btnFB = findViewById(R.id.btnFacebookLogin);
        ImageView btnX = findViewById(R.id.btnXLogin);

        if (btnGoogle != null) btnGoogle.setOnClickListener(v -> handleSocialClick(v, this::signInWithGoogle));
        if (btnFB != null) btnFB.setOnClickListener(v -> handleSocialClick(v, this::signInWithFacebook));
        if (btnX != null) btnX.setOnClickListener(v -> handleSocialClick(v, this::signInWithX));
    }

    private void handleSocialClick(View v, Runnable action) {
        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            v.animate().scaleX(1f).scaleY(1f).setDuration(100);
            action.run();
        });
    }

    // --- LOGIC SOCIAL LOGIN (GIỮ NGUYÊN) ---

    private void signInWithX() {
        showLoading(true);
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    showLoading(false);
                    navigateToMenu(mAuth.getCurrentUser());
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this, "Lỗi X: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, mCallbackManager, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override public void onCancel() { }
            @Override public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        showLoading(true);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            showLoading(false);
            if (task.isSuccessful()) navigateToMenu(mAuth.getCurrentUser());
        });
    }

    private void signInWithGoogle() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            showLoading(true);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showLoading(false);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            showLoading(false);
            if (task.isSuccessful()) navigateToMenu(mAuth.getCurrentUser());
        });
    }

    private void navigateToMenu(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("username", user.getEmail());
            intent.putExtra("fullname", user.getDisplayName());
            startActivity(intent);
            finish();
        }
    }

    private void showLoading(boolean isShow) {
        if (loadingOverlay != null) loadingOverlay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    // --- ANIMATIONS & LOGIN LOGIC (GIỮ NGUYÊN) ---

    private void setupStartupAnimations() {
        View lottie = findViewById(R.id.lottieTop);
        View welcome = findViewById(R.id.tvWelcome);
        View card = findViewById(R.id.cardLayout);
        Button btnLogin = findViewById(R.id.btnLogin);

        if (lottie != null) lottie.setAlpha(0f);
        if (welcome != null) { welcome.setAlpha(0f); welcome.setTranslationY(-50f); }
        if (card != null) { card.setAlpha(0f); card.setTranslationY(100f); }
        if (btnLogin != null) { btnLogin.setAlpha(0f); btnLogin.setScaleX(0.8f); btnLogin.setScaleY(0.8f); }

        if (lottie != null) lottie.animate().alpha(1f).setDuration(600).start();
        if (welcome != null) welcome.animate().alpha(1f).translationY(0f).setStartDelay(300).setDuration(500).start();
        if (card != null) card.animate().alpha(1f).translationY(0f).setStartDelay(600).setDuration(500).start();
        if (btnLogin != null) btnLogin.animate().alpha(1f).scaleX(1f).scaleY(1f).setStartDelay(900).setDuration(400).start();
    }

    private void performLoginWithAnimation(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            v.animate().scaleX(1f).scaleY(1f).setDuration(100);
            login();
        });
    }

    private void login() {
        String email = etGmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui lòng nhập đầy đủ Gmail và mật khẩu!");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        showLoading(true);
        tvError.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        navigateToMenu(mAuth.getCurrentUser());
                    } else {
                        tvError.setText("Gmail hoặc mật khẩu không chính xác!");
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
    }
}