package com.example.teknasyon.testing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        final EditText eTRegisterEmail = findViewById(R.id.eTRegisterEmail);
        final EditText eTRegisterPassword = findViewById(R.id.eTRegisterPassword);
        Button bSignupConfirm = findViewById(R.id.bSignupConfirm);

        bSignupConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!((eTRegisterEmail.getText().toString().equals("")) || (eTRegisterPassword.getText().toString().equals(""))))
                {
                    createNewAccount(eTRegisterEmail.getText().toString(), eTRegisterPassword.getText().toString());
                }//end if
                else
                {
                    showAlertDialog("Hata!", "E-Posta ve Parola Alanları Boş", "Tamam");
                }//end else
            }
        });

    }//end onCreate

    private void createNewAccount(final String email, final String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(getBaseContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                    finish();
                }//end if
                else
                {
                    try
                    {
                        throw task.getException();
                    }//end try
                    catch(FirebaseAuthWeakPasswordException e)
                    {
                        showAlertDialog("Hata!", "Şifre 6 karakter veya daha uzun olmalıdır", "Tamam");
                    }//end catch
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        showAlertDialog("Hata!", "Girilen Bilgiler Hatalı", "Tamam");
                    }//end catch
                    catch (FirebaseAuthUserCollisionException e)
                    {
                        showAlertDialog("Hata!", "Bu E-Posta adresi zaten kayıtlı", "Tamam");
                    }//end catch
                    catch (Exception e)
                    {
                        showAlertDialog("Hata!", "Bilinmeyen Hata", "Tamam");
                    }//end catch
                }//end else
            }
        });
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
