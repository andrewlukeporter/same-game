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
package com.itteco.samegame.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.itteco.samegame.R;

/**
 * Used to
 * User: szotov
 */
public class NewScoreDialog extends Dialog implements View.OnClickListener {
    private View okButton;
    private View cancelButton;
    private EditText editNameField;
    private TextView scoreTextView;

    private NewScoreCallback newScoreCallback;
    private int level;
    private long score;
    private String initName;


    public NewScoreDialog(Context context, NewScoreCallback callback, String initName, long score, int level) {
        super(context);
        newScoreCallback = callback;
        this.level = level;
        this.score = score;
        this.initName = initName;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            score = savedInstanceState.getLong("score", score);
            level = savedInstanceState.getInt("level", level);
        }

        setTitle("Add Your Name");

        setContentView(R.layout.new_score_dialog);

        scoreTextView = (TextView) findViewById(R.id.score);
        scoreTextView.setText(Long.toString(score));

        okButton = findViewById(R.id.ok);
        okButton.setOnClickListener(this);

        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);

        editNameField = (EditText) findViewById(R.id.name);
        editNameField.setText(initName);
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View v) {
        if (v == okButton) {
            newScoreCallback.onSaveScore(editNameField.getText().toString(), score, level);
            dismiss();
        } else if (v == cancelButton) {

            cancel();
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt("level", level);
        bundle.putLong("score", score);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        score = bundle.getLong("score", score);
        level = bundle.getInt("level", level);
    }


}
