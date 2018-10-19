package com.dhivi.inc.topo.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.dhivi.inc.topo.R;


public class CustomDialog extends Dialog {
	
	private static CustomDialog dialog;
	public static CustomDialog show(Context context,
                                    CharSequence title, CharSequence message) {
		return show(context, title, message, false);
	}

	public static CustomDialog show(Context context,
                                    CharSequence title, CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static CustomDialog show(Context context,
                                    CharSequence title, CharSequence message, boolean indeterminate,
                                    boolean cancelable, OnCancelListener cancelListener) {
		System.out.println("from CustomProgressDialog show::");
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_progress, null);
		
		 dialog = new CustomDialog(context){
           @Override
           public void onBackPressed() {
           	dialog.dismiss();
           }};
		dialog.setTitle(title);
		dialog.setCancelable(false);
		/*dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);*/
		
		
		/* The next line will add the ProgressBar to the dialog. */
		/*dialog.addContentView(new ProgressBar(context), new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));*/
		dialog.addContentView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();

		return dialog;
	}

	public CustomDialog(Context context) {
		super(context, R.style.ProgressDialog);
	}
}
