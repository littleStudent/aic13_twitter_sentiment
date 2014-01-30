package websocket;/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import twitter.scheduler.ThreadPoolScheduler;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint("/websocket/endpoint")
public class WebsocketEndpoint {
    private ArrayList<Session> sessions = new ArrayList<Session>();
    //private HashMap<Integer, Integer> requests = new HashMap<>();

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    // input parameters.
    final int threadCount = 4;
    ThreadPoolScheduler scheduler = new ThreadPoolScheduler(threadCount);

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
    	

//        try {
//            if (session.isOpen()) {
//                String[] msgarr = msg.split(",");
//                int request_id = Integer.parseInt(msgarr[0]);
//                String company = msgarr[1];
//                String start = msgarr[2];
//                String end = msgarr[3];

                //requests.put(Integer.parseInt(session.getId()), request_id);
                //sendWatchData();

                threadPool.execute(new messageWorker(session, msg, last));

                //double result = scheduler.sentimentAnalysis(company, start, end, threadCount);
                //session.getBasicRemote().sendText(request_id + "," + result, last);
                //session.getBasicRemote().sendText(request_id + "," + 5, last);
//            }
//        } catch (IOException e) {
//            try {
//                session.close();
//            } catch (IOException e1) {
//                // Ignore
//            }
//        }
    }

    /*private void sendWatchData() {
        for (int x = 0; x < sessions.size(); x++) {
            Session session = sessions.get(x);
            for(int y = 0; y<3; y++)
                try {
                    session.getBasicRemote().sendText("w," + y%2);
                    Thread.sleep(500);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }
    }*/

    @OnOpen
    public void onOpen(Session session) throws IOException {
        //TODO
        //add session to session list and start sending monitor data to clients
        // -> set boolean to true and start loop
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        //TODO
        //check if no more clients are connected to server
        //if this is the case stop sending monitoring information
        // -> set boolean to false, end loop

        for (int x = 0; x < sessions.size(); x++) {
            if (session.getId() == sessions.get(x).getId()) {
                sessions.remove(x);
                break;
            }
        }
    }

    /*
     * Worker for parallel processing of messages
     * >> gets session, msg and boolean value from onmessage
     * and starts the analysis in a separate thread so that
     * more than one message can be processed at a time.
     */
    private class messageWorker implements Runnable {

        private Session session;
        private String msg;
        private boolean last;


        public messageWorker(Session session, String msg, boolean last) {
            this.session = session;
            this.msg = msg;
            this.last = last;
        }

        @Override
        public void run() {
            String[] msgarr = msg.split(",");
            int request_id = Integer.parseInt(msgarr[0]);
            String company = msgarr[1];
            String start = msgarr[2];
            String end = msgarr[3];

            double result = scheduler.sentimentAnalysis(company, start, end, threadCount);

            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(request_id + "," + result, last);
                }
            } catch (IOException e) {
                try {
                    session.close();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }
}
