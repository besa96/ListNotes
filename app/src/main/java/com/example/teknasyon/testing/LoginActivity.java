package com.example.teknasyon.testing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        final EditText eTEmail = findViewById(R.id.eTEmail);
        final EditText eTPassword = findViewById(R.id.eTPassword);
        final Button bLogin = findViewById(R.id.bLogin);
        final Button bSingup = findViewById(R.id.bSignup);

        bLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!((eTEmail.getText().toString().equals("")) || (eTPassword.getText().toString().equals(""))))
                {
                    logIn(eTEmail.getText().toString(), eTPassword.getText().toString());
                }//end if
                else
                {
                    showAlertDialog("Hata!", "E-Posta ve Parola Alanları Boş", "Tamam");
                }//end else
            }
        });

        bSingup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signUp();
            }
        });

    }//end onCreate

    @Override
    protected void onStart()
    {
        super.onStart();
        //check if there is an already logged in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        validateUser(currentUser);
    }

    private void logIn(final String email, final String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    validateUser(currentUser);
                }//end if
                else
                {
                    try
                    {
                        throw task.getException();
                    }//end catch
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        showAlertDialog("Hata!", "Böyle Bir Kullanıcı Yok", "Tamam");
                    }//end catch
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        showAlertDialog("Hata!", "Girilen Bilgiler Hatalı", "Tamam");
                    }//end catch
                    catch (Exception e)
                    {
                        showAlertDialog("Hata!", "Bilinmeyen Hata", "Tamam");
                    }//end catch
                }//end else
            }
        });
    }

    private void signUp()
    {
        final Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(signupIntent);
    }

    public void validateUser(final FirebaseUser user)
    {
        if (user != null)
        {
            final Intent loggedInIntent = new Intent(LoginActivity.this, MainActivity.class);
            //loggedInIntent.putExtra("user", user);
            startActivity(loggedInIntent);
            finish();
        }//end if
        else
        {
            Log.e("Login", "validateUser Failed");
        }//end else
    }

    private void showAlertDialog(final String title, final String message, final String button)
    {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }//end if
        else
        {
            builder = new AlertDialog.Builder(this);
        }//end else
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}//end activity