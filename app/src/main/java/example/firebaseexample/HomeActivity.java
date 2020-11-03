package example.firebaseexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements UserRecyclerViewAdapter.UserListener {

    RecyclerView recyclerView;
    List<User> list= new ArrayList<>();
    List<String> userKeyList = new ArrayList<>();
    UserRecyclerViewAdapter adapter;
    Button logout,changePassword;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(MainActivity.mSharedPrefName, MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserRecyclerViewAdapter(this,list);
        recyclerView.setAdapter(adapter);
        logout = findViewById(R.id.btn_logout);
        changePassword = findViewById(R.id.btn_change_pass);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("users");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                userKeyList.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.e("snapshot",d.getValue().toString());
                    User user = d.getValue(User.class);
                    list.add(user);
                    userKeyList.add(d.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String user_name = sharedPreferences.getString("name","demo name");
        String user_mob = sharedPreferences.getString("mob","demo mob");
        String user_email = sharedPreferences.getString("email","demo email");
        String image_path = sharedPreferences.getString("profile","");

        Log.e("user",user_name + "  "+user_mob+"   "+user_email);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user_email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "Password Reset Email Sent!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onButtonClicked(String userId,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this User?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //clicked -> No, i'm done
                        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("users").child(userKeyList.get(position));
                        deleteRef.removeValue();
                        list.remove(position);
                        userKeyList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //clicked yes
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();


    }
}
