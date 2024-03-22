package edu.sdsmt.johnson_n_a.tutorial56;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class SaveDlg extends DialogFragment implements Cloud.CatalogAdapter.CatalogCallback{

    private AlertDialog dlg;

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Set the title
        builder.setTitle(R.string.saveDialogTitle);

        View view = inflater.inflate(R.layout.save_dlg, null);
        builder.setView(view);

        // Add an OK button
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            EditText editName = dlg.findViewById(R.id.editName);
            save(editName.getText().toString());
        });

        // Create the dialog box
        dlg = builder.create();

        return dlg;
    }

    /**
     * Actually save the hatting
     *
     * @param name name to save it under
     */
    private void save(final String name) {
        Log.i("Emily", "time to save this name: " + name);
        if (!(getActivity() instanceof HatterActivity)) {
            return;
        }
        HatterActivity activity = (HatterActivity) getActivity();
        HatterView view = activity.findViewById(R.id.hatterView);
        Cloud cloud = new Cloud();
        cloud.saveToCloud(name, view);
    }

    @Override
    public void callback(Cloud.Item catItem) {

    }
}
