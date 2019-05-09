package com.example.pna.authencationsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    public static FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;
    public static String cur_room;

    TextView txtv_test;
    public static Socket mSocket, mSocketRoom;

    EditText edit_id, edit_pass;
    Button btn_signin, btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            init();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        actionSocket();
        startActivity(new Intent(MainActivity.this,ListRoomActivity.class));

    }
    private void actionSocket() {
        mSocketRoom.on("connectToRoom", listenerRoom);
        mSocket.on("connectToRoom", listenerRoom);
    }

    private void init() throws URISyntaxException {
        String nsp = "/namespace123";
        //String uri = "https://andt-caro.herokuapp.com";
        String uri = "http://192.168.43.244:4000";

        progressBar = findViewById(R.id.progress);
        mAuth = FirebaseAuth.getInstance();


        Manager manager = new Manager(new URI(uri));
        //mSocket = manager.socket(nsp);
        mSocket = IO.socket(uri);
        mSocket.connect();

        mSocketRoom = IO.socket(uri);
        mSocketRoom.connect();

        edit_id = findViewById(R.id.editId);
        edit_pass = findViewById(R.id.editPass);
        btn_signin = findViewById(R.id.btnSignIn);
        btn_signup = findViewById(R.id.btnSignUp);

    }

    Emitter.Listener listenerRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        String content = ob.getString("roomNo");
                        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void onClickSignIn(View view) {
        String email, pass;
        email = edit_id.getText().toString().trim();
        pass = edit_pass.getText().toString();
        if (pass.length() != 0 && email.length() != 0) {
            signIn(email,pass);
        }
    }

    public void onClickSignUp(View view) {
        startActivity(new Intent(MainActivity.this,SignUpActivity.class));
    }

    private void signIn(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,ListRoomActivity.class));
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this,"Sai id/password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        mSocket.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
