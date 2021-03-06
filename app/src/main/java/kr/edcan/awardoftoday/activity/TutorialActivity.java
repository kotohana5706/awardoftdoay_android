package kr.edcan.awardoftoday.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import kr.edcan.awardoftoday.R;
import kr.edcan.awardoftoday.data.User;
import kr.edcan.awardoftoday.utils.DeveloperService;
import kr.edcan.awardoftoday.utils.NetworkService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
/**
 * Created by Junseok Oh on 2015-10-11.
 */
public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    NetworkService service;
    RestAdapter restAdapter;
    String name, apikey;
    boolean isParent;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ImageView confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefault();
        setRest();
        if (isParent) {
            setContentView(R.layout.activity_tutoria_parentl);
            onParent();
        } else {
            setContentView(R.layout.activity_tutorial_child);
            onChild();
        }
    }
    private void setRest() {
        DeveloperService d = new DeveloperService(getApplicationContext());
        String END_POINT = d.getEndpoint();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .build();
        service = restAdapter.create(NetworkService.class);
    }

    private void onChild() {
        confirm = (ImageView) findViewById(R.id.tutorial_child_complete);
        confirm.setOnClickListener(this);
    }

    private void onParent() {
        confirm = (ImageView) findViewById(R.id.tutorial_find_child);
        confirm.setOnClickListener(this);
    }

    private void setDefault() {
        sharedPref = getSharedPreferences("AwardOfToday", 0);
        editor = sharedPref.edit();
        isParent = sharedPref.getBoolean("isParent", false);
        name = sharedPref.getString("name", "");
        apikey = sharedPref.getString("apikey", "");
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(TutorialActivity.this)
                .title("오늘의 어린이상!")
                .content("정말로 종료하시겠습니까?")
                .positiveText("확인")
                .negativeText("취소")
                .negativeColor(Color.parseColor("#8B8B8B"))
                .positiveColor(Color.parseColor("#F499B8"))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        finish();
                    }
                })
                .show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tutorial_child_complete:
                service.checkMyParent(apikey, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        Toast.makeText(getApplicationContext(), "부모님과 연결되었어요!", Toast.LENGTH_SHORT).show();
                        editor.putString("targetName", user.targetName);
                        editor.putString("targetApikey", user.targetApikey);
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(error.getResponse().getStatus()==422) Toast.makeText(getApplicationContext(),
                                "아직 부모님이랑 연결이 되지 않았어요!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.tutorial_find_child:
                startActivity(new Intent(getApplicationContext(), FindMyChildActivity.class));
                finish();
                break;
        }
    }
}
