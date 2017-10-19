package com.dewdrop623.androidcrypt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int SELECT_INPUT_FILE_REQUEST_CODE = 623;
    private static final int SELECT_OUTPUT_DIRECTORY_REQUEST_CODE = 8878;
    private static final int WRITE_FILE_PERMISSION_REQUEST_CODE = 440;

    private boolean useManuallyEnteredFilePath = false;
    private Uri inputFileUri = null;
    private Uri outputFileUri = null;

    private RadioGroup inputFileSelectTypeRadioGroup;
    private EditText manuallyEnteredFileInputPathEditText;
    private TextView inputContentURITextView;
    private ImageButton selectInputFileButton;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox showPasswordCheckbox;
    private TextView fileDestinationDirectoryTextView;
    private ImageButton selectOutputDirectoryButton;
    private EditText outputFileNameEditText;
    private Button encryptDecryptButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        inputFileSelectTypeRadioGroup = (RadioGroup) view.findViewById(R.id.inputFileSelectTypeRadioGroup);
        manuallyEnteredFileInputPathEditText = (EditText) view.findViewById(R.id.manuallyEnteredFileInputPathEditText);
        inputContentURITextView = (TextView) view.findViewById(R.id.inputContentURITextView);
        selectInputFileButton = (ImageButton) view.findViewById(R.id.selectInputFileButton);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPasswordEditText);
        showPasswordCheckbox = (CheckBox) view.findViewById(R.id.showPasswordCheckbox);
        fileDestinationDirectoryTextView = (TextView) view.findViewById(R.id.fileDestinationDirectoryTextView);
        selectOutputDirectoryButton = (ImageButton) view.findViewById(R.id.selectOutputDirectoryButton);
        outputFileNameEditText = (EditText) view.findViewById(R.id.outputFileNameEditText);
        encryptDecryptButton = (Button) view.findViewById(R.id.encryptDecryptButton);

        inputFileSelectTypeRadioGroup.setOnCheckedChangeListener(inputFileSelectTypeRadioGroupOnCheckedChangedListener);
        encryptDecryptButton.setOnClickListener(encryptDecryptButtonOnClickListener);
        showPasswordCheckbox.setOnCheckedChangeListener(showPasswordCheckBoxOnCheckedChangeListener);
        selectOutputDirectoryButton.setOnClickListener(selectOutputDirectoryButtonOnClickListener);
        selectInputFileButton.setOnClickListener(selectInputDirectoryButtonOnClickListener);

        inputFileSelectTypeRadioGroup.check(R.id.selectFileRadioButton);
        setShowPassword(false);

        manuallyEnteredFileInputPathEditText.setText(Environment.getExternalStorageDirectory().getAbsolutePath()+"/");

        checkPermissions();

        return view;
    }

    private RadioGroup.OnCheckedChangeListener inputFileSelectTypeRadioGroupOnCheckedChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            switch (i) {
                case R.id.selectFileRadioButton:
                    makeSelectFileModeActive();
                    break;
                case R.id.manuallyEnterFileRadioButton:
                    makeManuallyEnterPathModeActive();
                    break;
            }
        }
    };

    private CheckBox.OnCheckedChangeListener showPasswordCheckBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            setShowPassword(b);
        }
    };

    private View.OnClickListener selectInputDirectoryButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectInputFile();
        }
    };

    private View.OnClickListener selectOutputDirectoryButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectOutputDirectory();
        }
    };


    private View.OnClickListener encryptDecryptButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validationCheck()) {
                try {
                    StorageAccessFrameworkHelper.getUriInputStream(referenceToThisForAnonymousClassButtonListeners(), inputFileUri).close();
                } catch (IOException ioe) {
                    showError(R.string.ioexception);
                    ioe.printStackTrace();
                }
            }
        }
    };

    /**
     * ask StorageAccessFramework to allow user to pick a file
     */
    private void selectInputFile() {
        StorageAccessFrameworkHelper.safPickFile(this, SELECT_INPUT_FILE_REQUEST_CODE);
    }

    /**
     * ask StorageAccessFramework to allow user to pick a directory
     */
    private void selectOutputDirectory() {
        StorageAccessFrameworkHelper.safPickDirectory(this, SELECT_OUTPUT_DIRECTORY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_INPUT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            inputFileUri = data.getData();
            inputContentURITextView.setText(inputFileUri.getPath());
        } else if (requestCode == SELECT_OUTPUT_DIRECTORY_REQUEST_CODE) {
            outputFileUri = data.getData();
            fileDestinationDirectoryTextView.setText(outputFileUri.getPath());
        } else {
            showError(R.string.error_unexpected_response_from_saf);
        }
    }

    /**
     * called by MainActivity when the Floating Action Button is pressed.
     */
    public void actionButtonPressed() {

    }

    //check for the necessary permissions. destroy and recreate the activity if permissions are asked for so that the files (which couldn't be seen previously) will be displayed
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_FILE_PERMISSION_REQUEST_CODE);
        }
    }

    /*
    * Display an error to the user.
    * */
    private void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    /*
    * Display an error to the user.
    * */
    private void showError(int stringId) {
        Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT).show();
    }

    private MainActivityFragment referenceToThisForAnonymousClassButtonListeners() {
        return this;
    }

    private void setShowPassword(boolean showPassword) {
        int inputType;
        if (showPassword) {
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        } else {
            inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }
        passwordEditText.setInputType(inputType);
        confirmPasswordEditText.setInputType(inputType);
    }

    /**
     * thanks to Sebastiano on stackoverflow.
     * get the file path so it can be displayed in ui
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    /**
     * makes ui visible for selecting a file using storage access framework. when encryption/decryption
     * happens, the member variable for the selected URI will be used to get an input stream.
     * Hides ui for manually entering a file path.
     */
    private void makeSelectFileModeActive() {
        selectInputFileButton.setVisibility(View.VISIBLE);
        inputContentURITextView.setVisibility(View.VISIBLE);
        if (inputFileUri != null) {
            inputContentURITextView.setText(inputFileUri.getEncodedPath());
        }
        manuallyEnteredFileInputPathEditText.setVisibility(View.GONE);
        useManuallyEnteredFilePath = false;
    }

    /**
     *Makes ui visible for manually typing a file path. When encryption/decryption happens,
     * new File(manuallyEnteredFileInputPathEditText.getText().toString()) will be used.
     * Hides the ui for selecting a file with the storage access framework
     */
    private void makeManuallyEnterPathModeActive() {
        selectInputFileButton.setVisibility(View.GONE);
        inputContentURITextView.setVisibility(View.GONE);
        manuallyEnteredFileInputPathEditText.setVisibility(View.VISIBLE);
        useManuallyEnteredFilePath = true;
    }

    /*
    * check if user input is valid before trying to do an operation, return true if it is. Also, notify users of any issues.
    * */
    private boolean validationCheck() {
        boolean valid = true;
        if (inputFileUri == null) {
            showError(R.string.no_input_file_selected);
            valid = false;
        }
        return valid;
    }
}