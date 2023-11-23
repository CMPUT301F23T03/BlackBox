package com.example.blackbox;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextCharacterLimitWatcher implements TextWatcher {

    private EditText editText;
    private int maxLetterLimit;

    public EditTextCharacterLimitWatcher(EditText editText, int maxLetterLimit) {
        this.editText = editText;
        this.maxLetterLimit = maxLetterLimit;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
        // This method is called to notify you that characters within s are about to be replaced with new text with length after.
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        // This method is called to notify you that somewhere within s, the text has been replaced with new text containing length after.
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // This method is called to notify you that the characters within s changed.
        if (editable.length() > maxLetterLimit) {
            // If the length exceeds the limit, truncate the text
            String truncatedText = editable.toString().substring(0, maxLetterLimit);
            editText.setText(truncatedText);
            editText.setSelection(truncatedText.length()); // Move the cursor to the end
        }
    }
}
