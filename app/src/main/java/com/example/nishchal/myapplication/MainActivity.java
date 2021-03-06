package com.example.nishchal.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView img;
    Cursor res;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    String user, email;
    String am_pm = "";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TextView tv1;
    TextView tv2;
    int flag = 0;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    public AlarmManager alarmManager;
    private RecyclerView.Adapter mAdapter;
    int hour1;
    int minute1;

    Dataretrieve m = new Dataretrieve();

    //Oncreation of recycler view and card view
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        final DatabaseOperations db = new DatabaseOperations(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseOperations mydb1 = new DatabaseOperations(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = mNavigationView.getHeaderView(0);
        final TextView tv1 = (TextView) header.findViewById(R.id.tv1);
        final TextView tv2 = (TextView) header.findViewById(R.id.tv2);


        res = db.sort_alpha();


        final ArrayList<Dataretrieve> arrayListToDo = new ArrayList<Dataretrieve>();

        StringBuffer sb = new StringBuffer("");


        try {
            while (res.moveToNext()) {
                Dataretrieve obj = new Dataretrieve();
                obj.setId(res.getInt(0));
                obj.setName(res.getString(1));
                obj.setDate(res.getString(2));
                obj.setTime(res.getString(3));
                obj.setNot(res.getString(4));
                arrayListToDo.add(obj);
            }
         /*  sb.append("NAME :"+arrayListToDo.get(0).getId()+"\n");
                sb.append("date :"+arrayListToDo.get(1).getDate()+"\n");
                sb.append("time :"+arrayListToDo.get(2).getTime()+"\n");
                sb.append("not :"+arrayListToDo.get(3).getNot()+"\n\n");*/

        } finally {
            res.close();
        }

        StringBuilder total = new StringBuilder("");
        try {
            FileInputStream inputStream = openFileInput("user");

            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            r.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        String s1 = "", s2 = "";
        while (i < total.length()) {
            if (total.charAt(i) == '0') {
                s1 = total.substring(0, i);
                s2 = total.substring(i + 1, total.length());
                break;
            }
            i++;
        }
        tv1.setText(s1);
        tv2.setText(s2);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        // specify an adapter (see also next example)
        Dataretrieve df = new Dataretrieve();

        mAdapter = new CustomAdapter(arrayListToDo, "y");
        mRecyclerView.setAdapter(mAdapter);


        final CustomAdapter cv1 = new CustomAdapter(arrayListToDo, "y");
        //  showmsg("data",sb.toString());
        // beautiful piece of code


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.RIGHT) {    //if swipe right

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            db.del(arrayListToDo.get(position).getId());
                            cv1.rem(position);  //then remove item

                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemRemoved(position);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                            return;
                        }
                    }).show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView); //set swipe to recylcerview


        img = (ImageView) header.findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                final LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final TextView tx1 = new TextView(MainActivity.this);
                TextView tx2 = new TextView(MainActivity.this);
                tx1.setText("User name:");
                tx2.setText("email:");

                tx1.setTextColor(Color.BLACK);
                tx2.setTextColor(Color.BLACK);
                final EditText i1 = new EditText(MainActivity.this);
                final EditText i2 = new EditText(MainActivity.this);
                layout.addView(tx1);
                layout.addView(i1);
                layout.addView(tx2);
                layout.addView(i2);

                alert.setView(layout);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String u1, u2;
                        u1 = i1.getText().toString();
                        u2 = i2.getText().toString();


                        user = u1 + '0' + u2;

                        try {
                            FileOutputStream outputStream = openFileOutput("user", Context.MODE_PRIVATE);
                            outputStream.write(user.getBytes());

                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tv1.setText(u1);
                        tv2.setText(u2);


                    }
                });

                alert.show();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in1 = new Intent(MainActivity.this, Dialog_specifications.class);
                MainActivity.this.startActivity(in1);


            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //Sets Notification
    public void setnotif(int selectedHour, int selectedMinute) {
        Log.d("selected time ", " " + selectedHour + ":" + selectedMinute);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        if (selectedHour < 12)
            calendar.set(Calendar.AM_PM, Calendar.AM);
        else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }

        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
               int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.comp_task) {
            Intent in3 = new Intent(MainActivity.this, Completed_tasks.class);
            MainActivity.this.startActivity(in3);

        } else if (id == R.id.incomp_task) {
            Intent in4 = new Intent(MainActivity.this, Incompleted_tasks.class);
            MainActivity.this.startActivity(in4);

        } else if (id == R.id.notif) {
            final Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    setnotif(selectedHour, selectedMinute);

                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Notification Time");
            mTimePicker.show();


        } else if (id == R.id.clr1) {
            DatabaseOperations db = new DatabaseOperations(this);
            db.delful1();
        } else if (id == R.id.clr2) {
            DatabaseOperations db = new DatabaseOperations(this);
            db.delful2();
        } else if (id == R.id.abt_dev) {
            Intent in5 = new Intent(MainActivity.this, About.class);
            MainActivity.this.startActivity(in5);
        }
        return true;
    }

}






