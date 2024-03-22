package edu.sdsmt.johnson_n_a.tutorial56;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class LoadingDlg extends DialogFragment {
    /**
     * Id for the image we are loading
     */
    private String catId;

    private final static String ID = "id";


    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        if(bundle != null) {
            catId = bundle.getString(ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.loading);

        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {

        });

        // Create the dialog box
        final AlertDialog dlg = builder.create();

        // Get a reference to the view we are going to load into
        final HatterView view = getActivity().findViewById(R.id.hatterView);
        Cloud cloud = new Cloud();
        cloud.loadFromCloud(view, catId, dlg);

        return dlg;
    }

    public String getCatId() {
        return this.catId;
    }

    public void setCatId(String id) {
        this.catId = id;
    }

    /**
     * Save the instance state
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID, catId);
    }
}
