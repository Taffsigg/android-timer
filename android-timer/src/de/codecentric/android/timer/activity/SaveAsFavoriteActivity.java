package de.codecentric.android.timer.activity;

import static de.codecentric.android.timer.util.PreferencesKeysValues.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.persistence.Timer;
import de.codecentric.android.timer.persistence.TimerDatabaseOpenHelper;
import de.codecentric.android.timer.persistence.TimerRepository;
import de.codecentric.android.timer.util.TimeParts;

public class SaveAsFavoriteActivity extends Activity {

	private static final String TAG = SaveAsFavoriteActivity.class.getName();

	private static final String SAVE_TIMER_PARAM_SUFFIX = "save_timer_param";
	static final String SAVE_TIMER_PARAM = APPLICATION_PACKAGE_PREFIX
			+ SAVE_TIMER_PARAM_SUFFIX;

	private TimerRepository timerRepository;

	private EditText editTextName;
	private TextView textViewTime;
	private Button buttonSave;
	private Button buttonCancel;

	private Timer timer;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate [SaveTimerActivity]");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.save_timer);
		TimerDatabaseOpenHelper helper = new TimerDatabaseOpenHelper(this);
		this.timerRepository = new TimerRepository(helper);
		this.fetchViewObjects();
		this.configureButtons();
		this.configureEditText();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume [SaveTimerActivity]");
		super.onResume();
		Intent intent = getIntent();
		this.timer = (Timer) intent.getSerializableExtra(SAVE_TIMER_PARAM);
		if (this.timer == null) {
			this.timer = new Timer(null, 0L);
		}
		this.refreshViewFromTimer();
	}

	private void refreshViewFromTimer() {
		if (this.timer.getName() != null && this.timer.getName().length() > 0) {
			this.editTextName.setText(this.timer.getName());
		} else {
			this.editTextName.setText("");
		}
		this.textViewTime.setText(TimeParts
				.fromMillisExactly(timer.getMillis()).prettyPrint());
	}

	private void fetchViewObjects() {
		this.editTextName = (EditText) this
				.findViewById(R.id.saveTimerEditTextName);
		this.textViewTime = (TextView) this
				.findViewById(R.id.saveTimerTextViewTime);
		this.buttonSave = (Button) this.findViewById(R.id.saveTimerButtonSave);
		this.buttonCancel = (Button) this
				.findViewById(R.id.saveTimerButtonCancel);
	}

	private void configureButtons() {
		this.configureButtonSave();
		this.configureButtonCancel();
	}

	private void configureButtonSave() {
		this.buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onSaveClicked();
			}
		});
	}

	private void configureButtonCancel() {
		this.buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void configureEditText() {
		this.editTextName.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				onEditTextNameTextChanged();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	private void onEditTextNameTextChanged() {
		String name = this.getNameFromEditText();
		boolean canSave = name != null && name.length() > 0;
		this.buttonSave.setEnabled(canSave);
	}

	private void setNameInTimer() {
		String name = this.getNameFromEditText();
		this.timer.setName(name);
	}

	private String getNameFromEditText() {
		return this.editTextName.getText().toString();
	}

	private void onSaveClicked() {
		Log.d(TAG, "onSaveClicked()");
		this.setNameInTimer();
		this.timerRepository.insert(this.timer);
		this.finish();
	}
}