/**
 * Copyright (c) 2009 Itteco Software (http://itteco.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.itteco.samegame;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.itteco.samegame.dialogs.NewScoreCallback;
import com.itteco.samegame.dialogs.NewScoreDialog;
import com.itteco.samegame.geocade.EmptyGeocadeData;
import com.itteco.samegame.geocade.GeocadeData;
import com.itteco.samegame.geocade.GeocadeUploadActivity;

import java.util.ArrayList;

public class ScoreActivity extends TabActivity implements NewScoreCallback, View.OnClickListener {
    // if false - then no upload button will be in score list
    private static final boolean UPDATE_GEOCADE = false;

    public static final String NEW_SCORE = "com.itteco.samegame.ScoreActivity.NEW_SCORE";
    public static final String NEW_SCORE_LEVEL = "com.itteco.samegame.ScoreActivity.NEW_SCORE_LEVEL";
    public static final String SAME_GAME_SCORE = "com.itteco.samegame.ScoreActivity.Score";

    public static final String MIN_SCORE = "MIN_SCORE";
    public static final String NEW_SCORE_ADDED = "NEW_SCORE_ADDED";
    private static final int NEW_SCORE_DIALOG = 20;

    private static final int LEVELS_COUNT = 3;
    private static final int MAX_SCORE_COUNT = 300;
    private ArrayList<ScoreEntity>[] score = new ArrayList[LEVELS_COUNT];
    private String defaultName = "";
    private TabHost tabHost;
    private TextView textView[] = null;
    private String nameForDialog;
    private long scoreForDialog;
    private int levelForDialog;
    private ListView listView1;
    private ListView listView2;
    private ListView listView3;
    private GeocadeData geocadeData = new EmptyGeocadeData();
//    private GeocadeData geocadeData = new GeocadeDataProduction();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        for (int i = 0; i < LEVELS_COUNT; i++) {
            score[i] = new ArrayList<ScoreEntity>(MAX_SCORE_COUNT);
        }
        updateViews();
    }

    private void showNewScoreDialog() {
        showDialog(NEW_SCORE_DIALOG);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == NEW_SCORE_DIALOG) {
            if (scoreForDialog >= 0 && levelForDialog >= 0) {
                return new NewScoreDialog(this, this, nameForDialog, scoreForDialog, levelForDialog);
            }
        }
        return null;
    }

    /**
     * Constans used in saving / restoring procedures
     */
    private static final String NAME_STRING = "Name[";
    private static final String SCORE_STRING = "Score[";
    private static final String UPLOADED_STRING = "Uploaded[";
    private static final String DEFAULE_SCORE_NAME_STRING = "DEFAULT_SCORE_NAME";

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences uiState = getSharedPreferences(SAME_GAME_SCORE, MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();

        for (int level = 0; level < LEVELS_COUNT; level++) {
            for (int i = 0; i < MAX_SCORE_COUNT; i++) {
                String appendName = "" + level + "/" + i + "]";
                if (i < score[level].size()) {
                    editor.putString(NAME_STRING + appendName, score[level].get(i).getName());
                    editor.putLong(SCORE_STRING + appendName, score[level].get(i).getScore());
                    editor.putBoolean(UPLOADED_STRING + appendName, score[level].get(i).isUploaded());
                } else {
                    editor.putString(NAME_STRING + appendName, null);
                    editor.putLong(SCORE_STRING + appendName, 0);
                    editor.putBoolean(UPLOADED_STRING + appendName, true);
                }
            }
            if (score[level].size() >= MAX_SCORE_COUNT) {
                editor.putLong(MIN_SCORE + level, score[level].get(MAX_SCORE_COUNT - 1).getScore());
            } else {
                editor.putLong(MIN_SCORE + level, 0);
            }
        }
        Log.v("ScoreActivity-defaultName2", defaultName);
        editor.putString(DEFAULE_SCORE_NAME_STRING, defaultName);
        editor.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences(SAME_GAME_SCORE, MODE_PRIVATE);
        long scoreValue;
        boolean uploaded;
        for (int level = 0; level < LEVELS_COUNT; level++) {
            score[level].clear();
            for (int i = 0; i < MAX_SCORE_COUNT; i++) {
                String appendName = "" + level + "/" + i + "]";
                String name = settings.getString(NAME_STRING + appendName, null);
                if (name != null) {
                    scoreValue = settings.getLong(SCORE_STRING + appendName, 0);
                    uploaded = settings.getBoolean(UPLOADED_STRING + appendName, true);
                    score[level].add(new ScoreEntity(level, name, scoreValue, uploaded));
                }
            }
        }
        updateGui();

        scoreForDialog = settings.getLong(NEW_SCORE, -1);
        levelForDialog = settings.getInt(NEW_SCORE_LEVEL, -1);

        if (scoreForDialog >= 0 && levelForDialog >= 0) {
            defaultName = settings.getString(DEFAULE_SCORE_NAME_STRING, "");
            Log.v("ScoreActivity-defaultName1", defaultName);
            nameForDialog = defaultName;
            tabHost.setCurrentTab(levelForDialog);
            showNewScoreDialog();
        }
    }

    private void updateGui() {
        ((BaseAdapter) listView1.getAdapter()).notifyDataSetInvalidated();
        ((BaseAdapter) listView2.getAdapter()).notifyDataSetInvalidated();
        ((BaseAdapter) listView3.getAdapter()).notifyDataSetInvalidated();
        ((BaseAdapter) listView1.getAdapter()).notifyDataSetChanged();
        ((BaseAdapter) listView2.getAdapter()).notifyDataSetChanged();
        ((BaseAdapter) listView3.getAdapter()).notifyDataSetChanged();
    }

    private void addNewScore(String name, long score, int level) {
        ArrayList<ScoreEntity> scores = this.score[level];
        int i = 0;
        for (ScoreEntity entity : scores) {
            if (entity.getScore() < score) {
                scores.add(i, new ScoreEntity(level, name, score, !UPDATE_GEOCADE));
                updateGui();
                tabHost.invalidate();
                return;
            }
            i++;
        }
        scores.add(new ScoreEntity(level, name, score, !UPDATE_GEOCADE));
        updateGui();
        tabHost.invalidate();
    }

    public void onSaveScore(String userName, long score, int level) {
        addNewScore(userName, score, level);
        defaultName = userName;
        clearNewScoreData(this);
    }

    public void onCancelScore() {
        clearNewScoreData(this);
    }

    public static void clearNewScoreData(Context context) {
        storeNewScoreValue(context, -1, -1);
    }


    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateViews();
    }

    private void updateViews() {
        setContentView(R.layout.score);
        tabHost = getTabHost();
        //int currentTab = tabHost.getCurrentTab();


        tabHost.addTab(tabHost.newTabSpec("tab_test1").setIndicator("",
                getResources().getDrawable(R.drawable.score_tab2)).setContent(R.id.ll1));
        tabHost.addTab(tabHost.newTabSpec("tab_test2").setIndicator("",
                getResources().getDrawable(R.drawable.score_tab3)).setContent(R.id.ll2));
        tabHost.addTab(tabHost.newTabSpec("tab_test3").setIndicator("",
                getResources().getDrawable(R.drawable.score_tab4)).setContent(R.id.ll3));

        //tabHost.setCurrentTab(currentTab);
        if (textView == null) {
            textView = new TextView[LEVELS_COUNT];
        }
        listView1 = (ListView) findViewById(R.id.listview1);
        listView2 = (ListView) findViewById(R.id.listview2);
        listView3 = (ListView) findViewById(R.id.listview3);
        listView1.setAdapter(new EfficientAdapter(this, score[0]));
        listView2.setAdapter(new EfficientAdapter(this, score[1]));
        listView3.setAdapter(new EfficientAdapter(this, score[2]));
        updateGui();
        tabHost.setCurrentTab(levelForDialog);

    }

    public void onClick(View view) {
        if (!(view instanceof Button)) {
            return;
        }
        EfficientAdapter.ViewHolder item = (EfficientAdapter.ViewHolder) view.getTag();
        if (item != null) {
            long scoreToSend = item.getSe().getScore();
            int scoreLevel = item.getSe().getScoreLevel();

            Double lat = 0d, lng = 0d;
            Location location = getLocation();
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            item.getSe().setUploaded(true);
            openPage(geocadeData.getUrl(scoreToSend, scoreLevel, getPhoneId(), lat, lng));
        }
    }


    private void openPage(String url) {
        final Intent intent = new Intent();
        intent.setClass(this, GeocadeUploadActivity.class);
        intent.putExtra(GeocadeUploadActivity.URL, url);
        startActivity(intent);
    }

    private String getPhoneId() {
        TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyMgr.getDeviceId();
    }

    private Location getLocation() {
        //---use the LocationManager class to obtain GPS locations---
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = tryToGetLocation(locationManager, LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = tryToGetLocation(locationManager, LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private Location tryToGetLocation(LocationManager lm, String provider) {
        try {
            return lm.getLastKnownLocation(provider);
        } catch (Exception ex) {
            return null;
        }

    }


    public static void storeNewScoreValue(Context context, int level, long overallScore) {
        SharedPreferences uiState = context.getSharedPreferences(SAME_GAME_SCORE, MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putLong(NEW_SCORE, overallScore);
        editor.putInt(NEW_SCORE_LEVEL, level);
        editor.commit();
    }


    private static final int MENU_BACK_TO_GAME = Menu.FIRST;
    private static final int MENU_CLEAR_SCORE = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_BACK_TO_GAME, MENU_BACK_TO_GAME, R.string.return_to_game);
        menu.add(Menu.NONE, MENU_CLEAR_SCORE, MENU_CLEAR_SCORE, R.string.clear_score_log);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_BACK_TO_GAME:
                backToGame();
                break;
            case MENU_CLEAR_SCORE:
                clearScore();
                break;
        }
        return true;
    }

    private void backToGame() {
        finish();
    }

    private void clearScore() {
        for (int i = 0; i < score.length; i++) {
            if (score[i] != null) {
                score[i].clear();
            }
        }
        updateGui();
    }

    public class ScoreEntity {
        private String name;
        private long score;
        private boolean uploaded;
        private int scoreLevel;

        public ScoreEntity(ScoreEntity entity) {
            this(entity.getScoreLevel(), entity.getName(), entity.getScore(), entity.isUploaded());
        }

        public ScoreEntity(int scoreLevel, String name, long score, boolean uploaded) {
            this.scoreLevel = scoreLevel;
            this.name = name;
            this.score = score;
            this.uploaded = uploaded;
        }

        public int getScoreLevel() {
            return scoreLevel;
        }

        public void setScoreLevel(int scoreLevel) {
            this.scoreLevel = scoreLevel;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getScore() {
            return score;
        }

        public void setScore(long score) {
            this.score = score;
        }

        public boolean isUploaded() {
            return uploaded;
        }

        public void setUploaded(boolean uploaded) {
            this.uploaded = uploaded;
        }
    }


    private static class EfficientAdapter extends BaseAdapter {
        ArrayList<ScoreEntity> DATA;
        private LayoutInflater mInflater;
        private ScoreActivity sa;


        public EfficientAdapter(ScoreActivity context, ArrayList<ScoreEntity> data) {
            DATA = data;
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            sa = context;
        }

        /**
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            if (DATA == null) return 0;
            return DATA.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.score_list_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.scoreNr = (TextView) convertView.findViewById(R.id.id_nr);
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.score = (TextView) convertView.findViewById(R.id.score);
                holder.button = (Button) convertView.findViewById(R.id.upload);
                holder.button.setOnClickListener(sa);
                holder.button.setTag(holder);
                // save holder to button to make it possible to get data to upload data
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.updateValues(position,
                    DATA.get(position).getName(),
                    DATA.get(position).score,
                    DATA.get(position).isUploaded(),
                    DATA.get(position)
            );
            return convertView;
        }

        static class ViewHolder {
            TextView scoreNr;
            TextView userName;
            TextView score;
            Button button;

            ScoreEntity se;


            public void updateValues(int nr, String name, long scoreValue, boolean uploaded, ScoreEntity se) {
                this.se = se;
                scoreNr.setText(Integer.toString(nr + 1));
                userName.setText(name);
                score.setText(Long.toString(scoreValue));
                if (!UPDATE_GEOCADE) {
                    // do not display 
                    button.setVisibility(View.GONE);
                    return;
                }
                if (uploaded) {
                    button.setVisibility(View.INVISIBLE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }


            public ScoreEntity getSe() {
                return se;
            }
        }
    }
}