package lk.mytodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    HomeFragment firstFragment = new HomeFragment();
    AddTaskFragment secondFragment = new AddTaskFragment();
    CompletedTasksFragment thirdFragment = new CompletedTasksFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.todo_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, firstFragment).commit();
            return true;
        } else if (itemId == R.id.profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, secondFragment).commit();
            return true;
        } else if (itemId == R.id.setting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, thirdFragment).commit();
            return true;
        }
        return false;
    }
}
