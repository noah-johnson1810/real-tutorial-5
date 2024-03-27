package edu.sdsmt.johnson_n_a.tutorial56;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.net.MalformedURLException;
import java.net.URL;


public class PictureDlg extends DialogFragment {

    /**
     * Retain the last entered URL so we can just backspace to change the
     * image if we like.
     */
    private static String lastUrl = "";

    /**
     * Reference to the dialog box that is made in this class
     */
    private AlertDialog dlg;

    /**
     * image request launcher for the activity sicne the dialog
     * box will be dismissed before the results are returned
     */
    private final ActivityResultLauncher<String> launcher;

    public PictureDlg(ActivityResultLauncher<String> activityLauncher) {
        launcher = activityLauncher;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {

        //reset if needed
        if (lastUrl.isEmpty())
            lastUrl = requireActivity().getResources().getString(R.string.mclaury_url);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Set the title
        builder.setTitle(R.string.url_dlg_heading);

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.url_dlg, null));

        // Add action buttons
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            // Do we have a valid URL?
            // By default the Ok button dismisses the dialog box. I want to
            // do error checking first, so this captures the click message
            // and replaces the regular code with my code that can check
            // before dismissing
            EditText urlText = dlg.findViewById(R.id.editUrl);
            try {
                assert urlText != null;
                URL url = new URL(urlText.getText().toString());
                Uri uri = Uri.parse(url.toExternalForm());
                lastUrl = url.toExternalForm();
                dlg.dismiss();
                if (getActivity() instanceof HatterActivity) {
                    ((HatterActivity) getActivity()).setUri(uri);
                }
            } catch (MalformedURLException e) {
                // If invalid, force the user to try again
                urlText.requestFocus();
                urlText.selectAll();
            }
        });

        builder.setNeutralButton(R.string.gallery, (dialog, which) -> {
            // Pass the selection process off to the gallery
            launcher.launch("image/*");
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
        });//just close

        // Create the dialog box
        dlg = builder.create();

        //wait until the dialog layout has loaded, then set the url
        dlg.setOnShowListener((temp) -> {
            if (bundle == null) {
                EditText urlText = dlg.findViewById(R.id.editUrl);
                assert urlText != null;
                urlText.setText(lastUrl);
                urlText.selectAll();
            }
        });


        return dlg;
    }

}