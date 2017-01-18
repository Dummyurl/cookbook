package com.robotemplates.cookbook.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.fragment.RecipeListFragment;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.preferences.Preference;


public class LogOutDialogFragment extends DialogFragment implements Constant {
    public static LogOutDialogFragment newInstance() {
        LogOutDialogFragment fragment = new LogOutDialogFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setRetainInstance(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // cancelable on touch outside
        if (getDialog() != null) getDialog().setCanceledOnTouchOutside(true);
    }


    @Override
    public void onDestroyView() {
        // http://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setTitle(R.string.dialog_logout_title)
                .setMessage(Html.fromHtml(getResources().getString(R.string.dialog_logout_message)))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        if (Preference.getTag(getActivity()).equals(TAG_FACEBOOK)) {
                            RecipeListFragment.getInstance().signOutFacebook();

                        } else if (Preference.getTag(getActivity()).equals(TAG_GOOGLE)) {
                            RecipeListFragment.getInstance().signOutGoogle();
                        }


                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // create dialog from builder
        final AppCompatDialog dialog = builder.create();

        // override positive button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        return dialog;
    }

}
