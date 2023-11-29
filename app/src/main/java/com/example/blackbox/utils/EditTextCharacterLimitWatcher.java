package com.example.blackbox.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * EditTextCharacterLimitWatcher is a TextWatcher implementation that monitors an EditText's
 * input and enforces a maximum character limit. If the length of the input exceeds the specified
 * limit, the watcher will truncate the text to the limit and set it back to the EditText,
 * ensuring that the character limit is not exceeded.
 */
public class EditTextCharacterLimitWatcher implements TextWatcher {

    /**
     * The EditText to which this watcher is attached.
     */
    private EditText editText;

    /**
     * The maximum character limit allowed for the EditText.
     */
    private int maxLetterLimit;

    /**
     * Constructs an EditTextCharacterLimitWatcher.
     *
     * @param editText      The EditText to monitor.
     * @param maxLetterLimit The maximum character limit allowed.
     */
    public EditTextCharacterLimitWatcher(EditText editText, int maxLetterLimit) {
        this.editText = editText;
        this.maxLetterLimit = maxLetterLimit;
    }

    /**
     * This method is called to notify you that characters within the input sequence
     * are about to be replaced with new text with a specified length.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
        // This method is called to notify you that characters within s are about to be replaced with new text with length after.
    }

    /**
     * This method is called to notify you that somewhere within the input sequence,
     * the text has been replaced with new text containing a specified length.
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        // This method is called to notify you that somewhere within s, the text has been replaced with new text containing length after.
    }

    /**
     * This method is called to notify you that the characters within the input sequence
     * have changed. It enforces the maximum character limit by truncating the text if necessary.
     */
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
