package kr.nbit.nsoldier.nsoldier;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

    //데이터 저장 경로
    public final String PREFERENCE = "kr.nbit.nsoldier";

    private ListView m_oListView = null;

    @Override
    public void onStop() {
        super.onStop();
        if (SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)) {
            NetworkTask networkTask = new NetworkTask();
            networkTask.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final ArrayList<SettingData> oData = new ArrayList<>();
        oData.add(new SettingData("메모 편집", ""));
        oData.add(new SettingData("입대일", SettingManager.IN_DATE));
        oData.add(new SettingData("전역일", SettingManager.OUT_DATE));
        oData.add(new SettingData("이병 복무 개월", SettingManager.SOL1 + "개월"));
        oData.add(new SettingData("일병 복무 개월", SettingManager.SOL2 + "개월"));
        oData.add(new SettingData("상병 복무 개월", SettingManager.SOL3 + "개월"));
        oData.add(new SettingData("로그인 후 띄울 친구 정보", SettingManager.MAIN_ID));
        oData.add(new SettingData("정보 페이지 전환", SettingManager.TARGET_ID));
        oData.add(new SettingData("친구 추가", ""));
        oData.add(new SettingData("친구 삭제", ""));
        oData.add(new SettingData("설정 초기화", ""));

        // ListView, Adapter 생성 및 연결 ------------------------
        m_oListView = findViewById(R.id.listView);
        final ListAdapter oAdapter = new ListAdapter(oData);
        m_oListView.setAdapter(oAdapter);

        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)))
                            Toast.makeText(getApplicationContext(), "본인 정보로 전환 후 시도하십시오", Toast.LENGTH_LONG).show();
                        else
                            DialogMemoPicker();
                    case 1:
                    case 2:
                        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)))
                            Toast.makeText(getApplicationContext(), "본인 정보로 전환 후 시도하십시오", Toast.LENGTH_LONG).show();
                        else
                            DialogDatePicker(position);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)))
                            Toast.makeText(getApplicationContext(), "본인 정보로 전환 후 시도하십시오", Toast.LENGTH_LONG).show();
                        else
                            DialogNumberPicker(position);
                        break;
                    case 6:
                    case 7:
                        DialogMainPicker(position);
                        break;
                    case 8:
                        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)))
                            Toast.makeText(getApplicationContext(), "본인 정보로 전환 후 시도하십시오", Toast.LENGTH_LONG).show();
                        else
                            DialogFriendPicker();
                        break;
                    case 9:
                        if (!(SettingManager.TARGET_ID.equalsIgnoreCase(SettingManager.ID)))
                            Toast.makeText(getApplicationContext(), "본인 정보로 전환 후 시도하십시오", Toast.LENGTH_LONG).show();
                        else
                            DialogMainPicker(position);
                        break;
                    case 10:
                        //설정 초기화
                        SettingManager.IN_DATE = "null";
                        SettingManager.OUT_DATE = "null";
                        SettingManager.SOL1 = 4;
                        SettingManager.SOL2 = 7;
                        SettingManager.SOL3 = 7;
                        SettingManager.MAIN_ID = SettingManager.ID;
                        SettingManager.TARGET_ID = "";
                        SettingManager.CHANGE_ID = SettingManager.ID;
                        SettingManager.TARGET_NAME = "";
                        SettingManager.FRIENDS = SettingManager.ID + ",";
                        SettingManager.MEMO = "NULL";
                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                        // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                        SharedPreferences.Editor editor = pref.edit();

                        // key값에 value값을 저장한다.
                        // String, boolean, int, float, long 값 모두 저장가능하다.
                        editor.putString("MAIN_ID", SettingManager.ID);

                        // 메모리에 있는 데이터를 저장장치에 저장한다.
                        editor.commit();

                        //설정 저장
                        NetworkTask networkTask = new NetworkTask();
                        networkTask.execute();

                        finish();
                        break;
                }
            }
        });

        //버튼 이벤트
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void DialogMainPicker(int code) {
        final String[] list = SettingManager.FRIENDS.split(",");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("친구 선택");

        if (code == 6) {
            builder.setItems(list, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                    // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                    SharedPreferences.Editor editor = pref.edit();

                    // key값에 value값을 저장한다.
                    // String, boolean, int, float, long 값 모두 저장가능하다.
                    editor.putString("MAIN_ID", list[which]);

                    // 메모리에 있는 데이터를 저장장치에 저장한다.
                    editor.commit();

                    kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                    adap.getItem(6).value = list[which];
                    adap.notifyDataSetChanged();

                    dialog.dismiss();
                }
            });
        } else if (code == 7) {
            builder.setItems(list, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SettingManager.CHANGE_ID = list[which];
                    finish();
                }
            });
        } else if (code == 9) {
            builder.setItems(list, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(SettingManager.ID.equalsIgnoreCase(list[which])){
                        Toast.makeText(getApplicationContext(), "본인은 삭제할 수 없습니다", Toast.LENGTH_LONG).show();
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(String temp : list){
                        if (!(temp.equalsIgnoreCase(list[which]))) {
                            sb.append(temp);
                            sb.append(",");
                        }
                    }
                    SettingManager.FRIENDS = sb.toString();
                    if(SettingManager.MAIN_ID.equalsIgnoreCase(list[which])) {
                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                        // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                        SharedPreferences.Editor editor = pref.edit();

                        // key값에 value값을 저장한다.
                        // String, boolean, int, float, long 값 모두 저장가능하다.
                        editor.putString("MAIN_ID", SettingManager.ID);

                        // 메모리에 있는 데이터를 저장장치에 저장한다.
                        editor.commit();

                        kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                        adap.getItem(6).value = list[which];
                        adap.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "친구 삭제 완료!", Toast.LENGTH_LONG).show();
                }
            });
        }

        builder.show();
    }

    private void DialogDatePicker(int code) {
        if (code == 1) {
            DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        // onDateSet method
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SettingManager.IN_DATE = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                            kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                            adap.getItem(1).value = SettingManager.IN_DATE;
                            adap.notifyDataSetChanged();

                        }
                    };
            DatePickerDialog alert;
            try{
                alert = new DatePickerDialog(this, mDateSetListener,
                        Integer.valueOf(SettingManager.IN_DATE.split("-")[0]), Integer.valueOf(SettingManager.IN_DATE.split("-")[1]), Integer.valueOf(SettingManager.IN_DATE.split("-")[2]));
            } catch(Exception e){
                Calendar c = Calendar.getInstance();
                alert = new DatePickerDialog(this, mDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            }
            alert.show();
        } else if (code == 2) {
            DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        // onDateSet method
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SettingManager.OUT_DATE = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                            kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                            adap.getItem(2).value = SettingManager.OUT_DATE;
                            adap.notifyDataSetChanged();

                        }
                    };
            DatePickerDialog alert;
            try {
                alert = new DatePickerDialog(this, mDateSetListener,
                        Integer.valueOf(SettingManager.OUT_DATE.split("-")[0]), Integer.valueOf(SettingManager.OUT_DATE.split("-")[1]), Integer.valueOf(SettingManager.OUT_DATE.split("-")[2]));
            } catch(Exception e){
                Calendar c = Calendar.getInstance();
                alert = new DatePickerDialog(this, mDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            }

            alert.show();
        }
    }

    public void DialogNumberPicker(int code) {
        final EditText edittext = new EditText(this);
        edittext.setSingleLine(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("데이터 입력");
        builder.setMessage("해당 계급으로 몇 개월 복무하는지 입력하시오.");
        builder.setView(edittext);

        if (code == 3) {
            builder.setPositiveButton("입력",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int month = 0;
                            try {
                                month = Integer.valueOf(edittext.getText().toString());
                                if (!(0 < month && month <= 12))
                                    Integer.valueOf("A");

                                SettingManager.SOL1 = month;
                                kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                                adap.getItem(3).value = SettingManager.SOL1 + "개월";
                                adap.notifyDataSetChanged();
                                dialog.dismiss();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), "1부터 12사이의 숫자를 입력해주십시오.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (Exception e) {
                                dialog.dismiss();
                            }
                        }
                    });
        }
        if (code == 4) {
            builder.setPositiveButton("입력",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int month = 0;
                            try {
                                month = Integer.valueOf(edittext.getText().toString());
                                if (!(0 < month && month <= 12))
                                    Integer.valueOf("A");

                                SettingManager.SOL2 = month;
                                kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                                adap.getItem(4).value = SettingManager.SOL2 + "개월";
                                adap.notifyDataSetChanged();
                                dialog.dismiss();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), "1부터 12사이의 숫자를 입력해주십시오.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (Exception e) {
                                dialog.dismiss();
                            }
                        }
                    });
        }
        if (code == 5) {
            builder.setPositiveButton("입력",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int month = 0;
                            try {
                                month = Integer.valueOf(edittext.getText().toString());
                                if (!(0 < month && month <= 12))
                                    Integer.valueOf("A");

                                SettingManager.SOL3 = month;
                                kr.nbit.nsoldier.nsoldier.ListAdapter adap = (kr.nbit.nsoldier.nsoldier.ListAdapter) m_oListView.getAdapter();
                                adap.getItem(5).value = SettingManager.SOL3 + "개월";
                                adap.notifyDataSetChanged();
                                dialog.dismiss();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), "1부터 12사이의 숫자를 입력해주십시오.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (Exception e) {
                                dialog.dismiss();
                            }
                        }
                    });
        }

        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void DialogMemoPicker() {
        final EditText edittext = new EditText(this);
        edittext.setText(SettingManager.MEMO);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("메모 입력");
        builder.setMessage("메모를 입력하시오.");
        builder.setView(edittext);

        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SettingManager.MEMO = edittext.getText().toString();
                    }
                });

        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void DialogFriendPicker() {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("친구 추가");
        builder.setMessage("추가할 아이디를 입력하시오.");
        builder.setView(edittext);

        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String[] strs = SettingManager.FRIENDS.split(",");
                            for(String str : strs) {
                                if(str.equalsIgnoreCase(edittext.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "이미 친구 목록에 있는 친구입니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            Network2Task networkTask = new Network2Task(edittext.getText().toString());
                            networkTask.execute();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "친구 추가 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });

        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private HttpURLConnection con = null;
        private StringBuffer str_param = new StringBuffer();

        public NetworkTask() {
            str_param.append("id=").append(SettingManager.ID).append("&");
            str_param.append("in_date=").append(SettingManager.IN_DATE).append("&");
            str_param.append("out_date=").append(SettingManager.OUT_DATE).append("&");
            str_param.append("sol1=").append(SettingManager.SOL1).append("&");
            str_param.append("sol2=").append(SettingManager.SOL2).append("&");
            str_param.append("sol3=").append(SettingManager.SOL3).append("&");
            str_param.append("friend=").append(SettingManager.FRIENDS).append("&");
            str_param.append("memo=").append(SettingManager.MEMO.replace("\n", "[br]"));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://app.nbit.kr/app/save.php");
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
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!(s.equals("true"))) {
                Toast.makeText(getApplicationContext(), "데이터 동기화 실패!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class Network2Task extends AsyncTask<Void, Void, String> {

        private HttpURLConnection con = null;
        private StringBuffer str_param = new StringBuffer();
        private String keyword = "";

        public Network2Task(String keyword) {
            this.keyword = keyword;
            str_param.append("id=").append(keyword);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://app.nbit.kr/app/findId.php");
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
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("false")) {
                Toast.makeText(getApplicationContext(), "올바른 아이디를 입력하시오!", Toast.LENGTH_LONG).show();
            }
            else if(s.equalsIgnoreCase("true")){
                SettingManager.FRIENDS += keyword+",";
                Toast.makeText(getApplicationContext(), "친구 추가 완료!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "에러 발생! 아마 DB 쪽일걸?", Toast.LENGTH_LONG).show();
            }
        }
    }
}
