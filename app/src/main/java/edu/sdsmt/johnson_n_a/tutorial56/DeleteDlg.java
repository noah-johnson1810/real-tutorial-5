package edu.sdsmt.johnson_n_a.tutorial56;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeleteDlg extends DialogFragment implements Cloud.CatalogAdapter.CatalogCallback {

    private static final String TAG_CONFIRM_DIALOG = "confirm_dialog";
    private AlertDialog confirmDialog;
    private String itemName;
    private String itemId;

    /**
     * Create the dialog box
     *
     * @param savedInstanceState the saved state
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_fm_title);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.catalog_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            // Cancel just closes the dialog box
            dialog.dismiss();
        });

        AlertDialog dlg = builder.create();

        RecyclerView list = view.findViewById(R.id.listHattings);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        final Cloud.CatalogAdapter adapter = new Cloud.CatalogAdapter(list, this);
        list.setAdapter(adapter);

        return dlg;
    }

    @Override
    public void callback(Cloud.Item catItem) {
        itemName = catItem.name;
        itemId = catItem.id;
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.confirmDeleteDialogTitle);
        confirmBuilder.setMessage(getString(R.string.confirmDeleteDialogBodyText) + " " + catItem.name + "?");
        confirmBuilder.setPositiveButton("Yes", (dialog, which) -> {
            Cloud cloud = new Cloud();
            final HatterView view = getActivity().findViewById(R.id.hatterView);
            cloud.deleteFromCloud(catItem.id, view);
            dialog.dismiss();
            dismiss();
        });
        confirmBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        // Save the confirm dialog to restore later
        confirmDialog = confirmBuilder.create();
        confirmDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Dismiss the confirm dialog to prevent leaked window
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore the confirm dialog if it was previously shown
        if (confirmDialog != null) {
            confirmDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of the confirm dialog
        if (confirmDialog != null && confirmDialog.isShowing()) {
            outState.putBoolean(TAG_CONFIRM_DIALOG, true);
            // Save the catItem
            outState.putString("itemName", itemName);
            outState.putString("itemId", itemId);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore the state of the confirm dialog
        if (savedInstanceState != null && savedInstanceState.getBoolean(TAG_CONFIRM_DIALOG)) {
            Cloud.Item catItem = new Cloud.Item();
            catItem.name = savedInstanceState.getString("itemName");
            catItem.id = savedInstanceState.getString("itemId");
            callback(catItem); // Show the confirm dialog again
        }
    }
}
