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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.itteco.samegame.dialogs.GameOverDialog;
import com.itteco.samegame.dialogs.HelpDialog;
import com.itteco.samegame.dialogs.NewGameCallback;
import com.itteco.samegame.dialogs.WelcomeDialog;
import com.itteco.samegame.engine.EngineNotificationCallback;
import com.itteco.samegame.engine.FieldUpdateCallback;
import com.itteco.samegame.engine.GameEngine;
import com.itteco.samegame.engine.GameHistory;
import com.itteco.samegame.configuration.ConfigurationEntity;

/**
 * Main activity
 */
public class SameGameActivity extends Activity implements
		EngineNotificationCallback, NewGameCallback,
		DialogInterface.OnCancelListener, FieldUpdateCallback {
	private SameGameView gameView;
	private GameEngine gameEngine;
	private TextView overallScoreTextView;
	private TextView selectionScoreTextView;
	private ConfigurationEntity configuration;
	private GameHistory history = new GameHistory();

	private static final int GAME_OVER_DIALOG = 20;
	private static final int WELCOME_DIALOG = 21;
	private static final int HELP_DIALOG = 22;

	private GameOverDialog gameOverDialog;
	private WelcomeDialog welcomeDialog;
	private HelpDialog helpDialog;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		configuration = new ConfigurationEntity();
		configuration.updateValues(this);
		gameEngine = new GameEngine(configuration);
		gameView = (SameGameView) findViewById(R.id.gameView);
		gameView.setConfiguration(configuration);
		gameView.setGameEngine(gameEngine);
		gameEngine.setEngineNotificationCallback(this);
		gameEngine.setFieldUpdateCallback(this);
		overallScoreTextView = (TextView) findViewById(R.id.overallScore);
		selectionScoreTextView = (TextView) findViewById(R.id.selectionScore);

		this.findViewById(R.id.undoButton).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						undo();
					}
				});
		this.findViewById(R.id.redoButton).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						redo();
					}
				});
		this.findViewById(R.id.newButton).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						onNewGame();
					}
				});
	}

	public void gameOver() {
		if (!checkIsScoreLarge()) {
			showDialog(GAME_OVER_DIALOG);
		} else {
			final Intent intentScore = new Intent();
			intentScore.setClass(this, ScoreActivity.class);
			ScoreActivity.storeNewScoreValue(this, configuration.getLevel(),
					gameEngine.getOverallScore());
			startActivity(intentScore);
		}
	}

	private boolean checkIsScoreLarge() {
		SharedPreferences settings = getSharedPreferences(
				ScoreActivity.SAME_GAME_SCORE, Activity.MODE_PRIVATE);
		long minScore = settings.getLong(ScoreActivity.MIN_SCORE
				+ configuration.getLevel(), 0);
		return minScore < gameEngine.getOverallScore();
	}

	public void updateScore(long overall, long selected) {
		overallScoreTextView
				.setText(getString(R.string.overall_score, overall));
		if (selected == 0) {
			selectionScoreTextView.setText(R.string.selection_score_no_points);
		} else {
			selectionScoreTextView.setText(getString(R.string.selection_score,
					selected));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WELCOME_DIALOG: {
			welcomeDialog = new WelcomeDialog(this, this);
			welcomeDialog.setOnCancelListener(this);
			return welcomeDialog;
		}
		case GAME_OVER_DIALOG: {
			gameOverDialog = new GameOverDialog(this, this);
			gameOverDialog.setOnCancelListener(this);
			return gameOverDialog;
		}
		case HELP_DIALOG: {
			helpDialog = new HelpDialog(this);
			return helpDialog;
		}
		}
		return null;
	}

	private static final int MENU_NEW_GAME = Menu.FIRST;
	private static final int MENU_SETTINGS = Menu.FIRST + 1;
	private static final int MENU_UNDO = Menu.FIRST + 2;
	private static final int MENU_REDO = Menu.FIRST + 3;
	private static final int MENU_SCORE = Menu.FIRST + 4;
	private static final int MENU_HELP = Menu.FIRST + 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(Menu.NONE, MENU_SETTINGS, MENU_SETTINGS,
				R.string.settings);
		item.setIcon(R.drawable.settings);
		item = menu.add(Menu.NONE, MENU_SCORE, MENU_SCORE, R.string.score);
		item.setIcon(R.drawable.score);
		item = menu.add(Menu.NONE, MENU_HELP, MENU_HELP, R.string.help);
		item.setIcon(R.drawable.help);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case MENU_NEW_GAME:
			onNewGame();
			break;
		case MENU_SETTINGS:
			final Intent intent = new Intent();
			intent.setClass(this, Preferences.class);
			startActivity(intent);
			break;
		case MENU_SCORE: {
			final Intent intentScore = new Intent();
			intentScore.setClass(this, ScoreActivity.class);
			ScoreActivity.clearNewScoreData(this);
			startActivity(intentScore);
			break;
		}
		case MENU_UNDO: {
			undo();
			break;
		}
		case MENU_REDO: {
			redo();
			break;
		}
		case MENU_HELP: {
			showDialog(HELP_DIALOG);
			break;
		}
		}
		return true;
	}

	private void undo() {
		String newFieldValue = history.undo();
		if (newFieldValue != null) {
			gameEngine.loadFromString(newFieldValue);
			gameView.invalidate();
		}
	}

	private void redo() {
		String newFieldValue = history.redo();
		if (newFieldValue != null) {
			gameEngine.loadFromString(newFieldValue);
			gameView.invalidate();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNewGame() {
		history.init(); // must be called before init field
		gameEngine.initField();
		gameView.setCellSize(configuration.getCellSize());
		gameView.init(this);
		gameView.invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onCancel(DialogInterface dialog) {
		if (dialog == welcomeDialog || dialog == gameOverDialog) {
			finish();
		}
	}

	private static final String DATA_STRING = "value";
	private static final String CELL_SIZE = "cell_size";
	private static final String DIFFICLULTY = "difficlulty";

	@Override
	protected void onPause() {
		super.onPause();
		gameView.pause();
		SharedPreferences uiState = getPreferences(0);
		SharedPreferences.Editor editor = uiState.edit();
		editor.putString(DATA_STRING, gameEngine.saveToString());
		editor.putInt(CELL_SIZE, gameView.getCellSize());
		editor.putInt(DIFFICLULTY, configuration.getLevel());
		history.save(editor);
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		configuration.updateValues(this);
		gameView.init(this);
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		history.load(settings);
		String value = settings.getString(DATA_STRING, "");
		if (!"".equals(value)) {
			gameEngine.loadFromString(value);
			if (gameEngine.getGameField().isFieldEmpty()
					|| settings.getInt(DIFFICLULTY, -10) != configuration
							.getLevel()) {
				onNewGame();
			}
			gameView.setCellSize(settings.getInt(CELL_SIZE, configuration
					.getCellSize()));
			gameView.init(this);
		} else {
			showDialog(WELCOME_DIALOG);
		}
	}

	public void onFieldUpdatedEvent() {
		history.add(gameEngine.saveToString());
	}
}