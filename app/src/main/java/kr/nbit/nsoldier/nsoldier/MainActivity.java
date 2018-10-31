package kr.nbit.nsoldier.nsoldier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    //데이터 저장 경로
    public final String PREFERENCE = "kr.nbit.nsoldier";

    //입대 날짜
    private int[] in_date = {2018, 1, 1};
    //전역 날짜
    private int[] out_date = {2019, 1, 1};

    //계급별 개월수
    private int sol_1 = 4;
    private long sol1_left = 0;
    private double sol1_per = 0.0;
    private int sol_2 = 7;
    private long sol2_left = 0;
    private double sol2_per = 0.0;
    private int sol_3 = 7;
    private long sol3_left = 0;
    private double sol3_per = 0.0;
    private long sol4_left = 0;
    private double sol4_per = 0.0;

    //복무일
    long leftdays = 0;
    //전역일
    long rightdays = 0;

    //총
    long sum = 100;

    //퍼센트
    double leftper = 0.0;
    double rightper = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreference 를 선언한다.
        // 저장했을때와 같은 key로 xml에 접근한다.
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

        // key에 해당한 value를 불러온다.
        // 두번째 매개변수는 , key에 해당하는 value값이 없을 때에는 이 값으로 대체한다.
        SettingManager.MAIN_ID = pref.getString("MAIN_ID", SettingManager.ID);
        SettingManager.CHANGE_ID = SettingManager.MAIN_ID;

        //버튼 이벤트
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(mainIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.CHANGE_ID))){
            SettingManager.TARGET_ID = SettingManager.CHANGE_ID;
            NetworkTask networkTask = new NetworkTask();
            networkTask.execute();
        }
        else refresh();
    }

    public void refresh() {
        TextView tv = findViewById(R.id.textView16);
        tv.setText(SettingManager.TARGET_NAME+"님의 정보");

        if((SettingManager.IN_DATE.equalsIgnoreCase("null")) || (SettingManager.IN_DATE.equalsIgnoreCase("null"))){
            tv = findViewById(R.id.textView5);
            tv.setText("입대일을 설정해주십시오.");
            tv = findViewById(R.id.textView6);
            tv.setText("전역일을 설정해주십시오.");
            tv = findViewById(R.id.textView);
            tv.setText("복무일");
            tv = findViewById(R.id.textView2);
            tv.setText("전역일");
            ProgressBar bar = findViewById(R.id.progressBar);
            bar.setProgress(0);
            bar = findViewById(R.id.progressBar2);
            bar.setProgress(0);
            tv = findViewById(R.id.textView4);
            tv.setText("이병");
            tv = findViewById(R.id.textView14);
            tv.setText("일병");
            tv = findViewById(R.id.textView7);
            tv.setText("상병");
            tv = findViewById(R.id.textView8);
            tv.setText("병장");
            tv = findViewById(R.id.textView9);
            tv.setText("");
            tv = findViewById(R.id.textView15);
            tv.setText("");
            tv = findViewById(R.id.textView10);
            tv.setText("");
            tv = findViewById(R.id.textView12);
            tv.setText("");

            bar = findViewById(R.id.progressBar3);
            bar.setProgress(0);
            bar = findViewById(R.id.progressBar6);
            bar.setProgress(0);
            bar = findViewById(R.id.progressBar4);
            bar.setProgress(0);
            bar = findViewById(R.id.progressBar5);
            bar.setProgress(0);
            return;
        }

        //변수 새로고침
        in_date[0] = Integer.valueOf(SettingManager.IN_DATE.split("-")[0]);
        in_date[1] = Integer.valueOf(SettingManager.IN_DATE.split("-")[1]);
        in_date[2] = Integer.valueOf(SettingManager.IN_DATE.split("-")[2]);

        out_date[0] = Integer.valueOf(SettingManager.OUT_DATE.split("-")[0]);
        out_date[1] = Integer.valueOf(SettingManager.OUT_DATE.split("-")[1]);
        out_date[2] = Integer.valueOf(SettingManager.OUT_DATE.split("-")[2]);

        sol_1 = SettingManager.SOL1;
        sol_2 = SettingManager.SOL2;
        sol_3 = SettingManager.SOL3;


        //날짜 계산
        try {
            //전역일 계산
            Calendar out_today = Calendar.getInstance();
            Calendar out_dday = Calendar.getInstance();

            out_dday.set(out_date[0], out_date[1] - 1, out_date[2]);

            leftdays = (out_dday.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);


            //복무일 계산
            Calendar in_today = Calendar.getInstance();
            Calendar in_dday = Calendar.getInstance();

            in_dday.set(in_date[0], in_date[1] - 1, in_date[2]);

            rightdays = (in_today.getTimeInMillis()/86400000) - (in_dday.getTimeInMillis()/86400000);

            sum = leftdays + rightdays;
            leftper = (double)leftdays / (double)sum * 100d;
            rightper = (double)rightdays / (double)sum * 100d;

            if(leftdays <= 0) {
                leftdays = 0;
                rightdays = (out_dday.getTimeInMillis()/86400000) - (in_dday.getTimeInMillis()/86400000);
                leftper = 0.0d;
                rightper = 100.0d;
            }



            //계급별 일자 계산

            Calendar sol1_day = Calendar.getInstance();
            sol1_day.set(in_date[0], in_date[1] - 1, in_date[2]);

            Calendar sol2_day = Calendar.getInstance();
            sol2_day.set(in_date[0], in_date[1] - 1, 1);
            sol2_day.add(Calendar.MONTH, sol_1);

            Calendar sol3_day = Calendar.getInstance();
            sol3_day.set(in_date[0], in_date[1] - 1, 1);
            sol3_day.add(Calendar.MONTH, sol_1 + sol_2);

            Calendar sol4_day = Calendar.getInstance();
            sol4_day.set(in_date[0], in_date[1] - 1, 1);
            sol4_day.add(Calendar.MONTH, sol_1 + sol_2 + sol_3);

            sol1_left = (sol2_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            sol2_left = (sol3_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            sol3_left = (sol4_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            sol4_left = (out_dday.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);

            //이병
            if(sol1_left <= 0) sol1_per = 100;
            else sol1_per = 100d - ((double)sol1_left / (double)((sol2_day.getTimeInMillis()/86400000) - (sol1_day.getTimeInMillis()/86400000)) * 100d);

            //일병
            if(sol2_left <= 0) sol2_per = 100;
            else if(sol1_left <= 0){
                sol2_per = 100d - ((double)sol2_left / (double)((sol3_day.getTimeInMillis()/86400000) - (sol2_day.getTimeInMillis()/86400000)) * 100d);
            }
            else {
                sol2_per = -1;
                sol2_left = (sol2_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            }

            //상병
            if(sol3_left <= 0) sol3_per = 100;
            else if(sol2_left <= 0){
                sol3_per = 100d -((double)sol3_left / (double)((sol4_day.getTimeInMillis()/86400000) - (sol3_day.getTimeInMillis()/86400000)) * 100d);
            }
            else {
                sol3_per = -1;
                sol3_left = (sol3_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            }

            //병장
            if(sol4_left <= 0) sol4_per = 100;
            else if(sol3_left <= 0){
                sol4_per = 100d - ((double)sol4_left / (double)((out_dday.getTimeInMillis()/86400000) - (sol4_day.getTimeInMillis()/86400000)) * 100d);
            }
            else {
                sol4_per = -1;
                sol4_left = (sol4_day.getTimeInMillis()/86400000) - (out_today.getTimeInMillis()/86400000);
            }


            //전역/복무일 출력
            tv = findViewById(R.id.textView5);
            tv.setText(Long.toString(rightdays) + "일 했습니다 " +String.format(" (%.2f%%)", rightper));
            tv = findViewById(R.id.textView6);
            tv.setText(Long.toString(leftdays) + "일 남았습니다" +String.format(" (%.2f%%)", leftper));
            tv = findViewById(R.id.textView);
            tv.setText("복무일"+String.format(" (%d.%d.%d)", in_date[0], in_date[1], in_date[2]));
            tv = findViewById(R.id.textView2);
            tv.setText("전역일"+String.format(" (%d.%d.%d)", out_date[0], out_date[1], out_date[2]));


            ProgressBar bar = findViewById(R.id.progressBar);
            bar.setProgress((int)rightper);
            bar = findViewById(R.id.progressBar2);
            bar.setProgress((int)rightper);




            //계급별 일자 출력
            tv = findViewById(R.id.textView4);
            tv.setText("이병"+String.format(" (%d.%d.%d)", sol1_day.get(Calendar.YEAR), sol1_day.get(Calendar.MONTH) + 1, sol1_day.get(Calendar.DAY_OF_MONTH)));
            tv = findViewById(R.id.textView14);
            tv.setText("일병"+String.format(" (%d.%d.%d)", sol2_day.get(Calendar.YEAR), sol2_day.get(Calendar.MONTH) + 1, sol2_day.get(Calendar.DAY_OF_MONTH)));
            tv = findViewById(R.id.textView7);
            tv.setText("상병"+String.format(" (%d.%d.%d)", sol3_day.get(Calendar.YEAR), sol3_day.get(Calendar.MONTH) + 1, sol3_day.get(Calendar.DAY_OF_MONTH)));
            tv = findViewById(R.id.textView8);
            tv.setText("병장"+String.format(" (%d.%d.%d)", sol4_day.get(Calendar.YEAR), sol4_day.get(Calendar.MONTH) + 1, sol4_day.get(Calendar.DAY_OF_MONTH)));

            tv = findViewById(R.id.textView9);
            if(sol1_per == 100) tv.setText("완료");
            else if(sol1_per < 0){
                tv.setText("진급 전까지 "+Long.toString(sol1_left) + tv.getText().toString());
                sol1_per = 0;
            }
            else tv.setText(Long.toString(sol1_left) + "일 남았습니다" + String.format(" (%.2f%%)", sol1_per));
            tv = findViewById(R.id.textView15);
            if(sol2_per == 100) tv.setText("완료");
            else if(sol2_per < 0){
                tv.setText("진급 전까지 "+Long.toString(sol2_left) + "일 남았습니다");
                sol2_per = 0;
            }
            else tv.setText(Long.toString(sol2_left) + "일 남았습니다" +String.format(" (%.2f%%)", sol2_per));
            tv = findViewById(R.id.textView10);
            if(sol3_per == 100) tv.setText("완료");
            else if(sol3_per < 0){
                tv.setText("진급 전까지 "+Long.toString(sol3_left) + "일 남았습니다");
                sol3_per = 0;
            }
            else tv.setText(Long.toString(sol3_left) + "일 남았습니다" +String.format(" (%.2f%%)", sol3_per));
            tv = findViewById(R.id.textView12);
            if(sol4_per == 100) tv.setText("완료");
            else if(sol4_per < 0){
                tv.setText("진급 전까지 "+Long.toString(sol4_left) + "일 남았습니다");
                sol4_per = 0;
            }
            else tv.setText(Long.toString(sol4_left) + "일 남았습니다" +String.format(" (%.2f%%)", sol4_per));

            bar = findViewById(R.id.progressBar3);
            bar.setProgress((int)sol1_per);
            bar = findViewById(R.id.progressBar6);
            bar.setProgress((int)sol2_per);
            bar = findViewById(R.id.progressBar4);
            bar.setProgress((int)sol3_per);
            bar = findViewById(R.id.progressBar5);
            bar.setProgress((int)sol4_per);

            tv = findViewById(R.id.textView19);
            tv.setText(SettingManager.MEMO);

            //이스터 에그
            if(rightdays < 0) {
                tv = findViewById(R.id.textView5);
                tv.setText("입대 "+(rightdays * -1)+"일 전입니다.");
                tv = findViewById(R.id.textView6);
                tv.setText("");

                tv = findViewById(R.id.textView9);
                tv.setText("");
                tv = findViewById(R.id.textView15);
                tv.setText("");
                tv = findViewById(R.id.textView10);
                tv.setText("");
                tv = findViewById(R.id.textView12);
                tv.setText("");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private HttpURLConnection con = null;
        private StringBuffer str_param = new StringBuffer();

        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);


        public NetworkTask() {
            str_param.append("id=").append(SettingManager.TARGET_ID).append("&");
            str_param.append("id2=").append(SettingManager.ID);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://app.nbit.kr/app/settings.php");
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
                Toast.makeText(getApplicationContext(), "네트워크 처리 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                String[] strs = s.split("@@");
                SettingManager.IN_DATE = strs[0];
                SettingManager.OUT_DATE = strs[1];
                SettingManager.SOL1 = Integer.valueOf(strs[2]);
                SettingManager.SOL2 = Integer.valueOf(strs[3]);
                SettingManager.SOL3 = Integer.valueOf(strs[4]);
                SettingManager.TARGET_NAME = strs[5];
                SettingManager.FRIENDS = strs[6];
                SettingManager.MEMO = strs[7].replace("[br]", "\n");

                refresh();

            }catch (Exception e) {
                Toast.makeText(getApplicationContext(), "데이터 처리 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            asyncDialog.dismiss();
        }
    }
}
