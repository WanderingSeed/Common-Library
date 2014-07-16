package com.morgan.library.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.morgan.library.app.APPContext;

/**
 * 提供软键盘相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class KeyBoardUtils {

	public static void showKeyBoard(final EditText editText) {
		if (editText == null) {
			return;
		}
		editText.requestFocus();
		editText.post(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) APPContext
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editText,
						InputMethodManager.RESULT_UNCHANGED_SHOWN);
			}

		});
	}

	public static void hideSoftInput(EditText editText) {
		if (editText == null) {
			return;
		}
		InputMethodManager inputMethodManager = (InputMethodManager) APPContext
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}
}
