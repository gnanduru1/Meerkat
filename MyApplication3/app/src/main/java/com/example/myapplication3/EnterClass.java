package com.example.myapplication3;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.Iterator;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import android.os.Handler;
import android.util.Log;

public class EnterClass extends AppCompatActivity {
    public TreeMap<String, Integer> roster;
    public TreeMap<String, Boolean> check;
    public ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    public static final int SERVERPORT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_class);

        roster = new TreeMap<String, Integer>();
        check = new TreeMap<String, Boolean>();

        roster.put("Ganesh", 0);
        roster.put("Srikar", 1);
        roster.put("Ritesh", 2);
        roster.put("Kevin", 3);
        check.put("Ganesh", false);
        check.put("Srikar", false);
        check.put("Ritesh", false);
        check.put("Kevin", false);

        createLayout(roster);
        updateAttendance();

        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();


    }

    public void createLayout(TreeMap<String, Integer> args){
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        constraintLayout.setId(R.id.right);
        setContentView(constraintLayout);

        TextView heading = new TextView(this);
        heading.setText("Attendance List");
        heading.setTextSize(40);
        heading.setId(R.id.text-1);
        ConstraintLayout.LayoutParams par = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        heading.setLayoutParams(par);
        constraintLayout.addView(heading);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(heading.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 16);
        constraintSet.connect(heading.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 16);
        constraintSet.connect(heading.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 16);
        constraintSet.applyTo(constraintLayout);
        View root = heading.getRootView();
        root.setBackgroundColor(Color.parseColor("#D7BEE6"));

        String temp = "";
        int count = 0;
        Iterator it = args.keySet().iterator();
        while(it.hasNext()) {
            temp = (String)it.next();
            TextView text = new TextView(this);
            text.setText(temp);
            text.setTextSize(25);
            text.setId(R.id.text+roster.get(temp));
            par = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            text.setLayoutParams(par);
            constraintLayout.addView(text);

            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(text.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 180+60*count);
            constraintSet.connect(text.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 16);
            constraintSet.connect(text.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 16);

            constraintSet.applyTo(constraintLayout);
            count++;
        }
    }

    public void updateAttendance() {
        TextView text = (TextView) findViewById(R.id.text - 1);
        text.setTextColor(Color.BLACK);
        Iterator it = roster.keySet().iterator();
        String name = "";
        while (it.hasNext()) {
            name = (String) it.next();
            text = (TextView) findViewById(R.id.text + roster.get(name));
            if (check.get(name))
                text.setTextColor(Color.GREEN);
            else
                text.setTextColor(Color.RED);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
//                    text.setText("Error");
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                Log.d("Tag","Success1");

            } catch (IOException e) {
//                Log.d("ErrorTag","GaneshE");
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    Log.d("Tag","Success2");
                    String read = input.readLine();

                    Log.d("Tag", read);

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    Log.d("Tag","ErrorREEE");
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            Log.d("Run tag", "Got it to UI");
            if (check.containsKey(msg.trim())) {
                check.put(msg, !check.get(msg));
            }
            updateAttendance();
        }
    }
}

