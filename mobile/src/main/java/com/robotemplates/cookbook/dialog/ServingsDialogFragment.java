package com.robotemplates.cookbook.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.robotemplates.cookbook.R;


public class ServingsDialogFragment extends DialogFragment
{
	private static final String ARGUMENT_SERVINGS = "servings";
	
	private View mRootView;
	private int mServings;
	private ServingsDialogListener mListener;
	
	
	public interface ServingsDialogListener
	{
		void onServingsDialogPositiveClick(DialogFragment dialog, int servings);
	}
	
	
	public static ServingsDialogFragment newInstance(int servings)
	{
		ServingsDialogFragment fragment = new ServingsDialogFragment();
		
		// arguments
		Bundle arguments = new Bundle();
		arguments.putInt(ARGUMENT_SERVINGS, servings);
		fragment.setArguments(arguments);
		
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setRetainInstance(true);
		
		// handle fragment arguments
		Bundle arguments = getArguments();
		if(arguments != null)
		{
			handleArguments(arguments);
		}
		
		// set callback listener
		try
		{
			mListener = (ServingsDialogListener) getTargetFragment();
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException(getTargetFragment().toString() + " must implement " + ServingsDialogListener.class.getName());
		}
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// cancelable on touch outside
		if(getDialog()!=null) getDialog().setCanceledOnTouchOutside(true);
		
		// restore saved state
		handleSavedInstanceState();
	}
	
	
	@Override
	public void onDestroyView()
	{
		// http://code.google.com/p/android/issues/detail?id=17423
		if(getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// save current instance state
		super.onSaveInstanceState(outState);

		EditText countEditText = (EditText) mRootView.findViewById(R.id.dialog_servings_count);

		mServings = Integer.parseInt(countEditText.getText().toString());
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mRootView = inflater.inflate(R.layout.dialog_servings, null);
		
		builder
		.setTitle(R.string.dialog_servings_title)
		.setView(mRootView)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				EditText countEditText = (EditText) mRootView.findViewById(R.id.dialog_servings_count);
				int servings = Integer.parseInt(countEditText.getText().toString());

				if(servings>0)
				{
					mListener.onServingsDialogPositiveClick(ServingsDialogFragment.this, servings);
					dialog.dismiss();
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// do nothing
			}
		});
		
		// create dialog from builder
		final Dialog dialog = builder.create();

		// show soft keyboard
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		return dialog;
	}


	private void handleArguments(Bundle arguments)
	{
		if(arguments.containsKey(ARGUMENT_SERVINGS))
		{
			mServings = (int) arguments.get(ARGUMENT_SERVINGS);
		}
	}
	
	
	private void handleSavedInstanceState()
	{
		EditText countEditText = (EditText) mRootView.findViewById(R.id.dialog_servings_count);
		countEditText.setText(Integer.toString(mServings));
		countEditText.setSelection(countEditText.getText().length());
	}
}
