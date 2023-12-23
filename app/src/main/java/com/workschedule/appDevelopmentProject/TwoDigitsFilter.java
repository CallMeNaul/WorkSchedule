package com.workschedule.appDevelopmentProject;

import android.text.InputFilter;
import android.text.Spanned;

public class TwoDigitsFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String input = dest.toString().substring(dstart);
        String newText = input + source;
        if (newText.length() > 2) {
            return "00";
        }
        return newText;
    }
}