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

public class LoadDlg extends DialogFragment implements Cloud.CatalogAdapter.CatalogCallback {

    /**
     * Create the dialog box
     *
     * @param savedInstanceState the saved state
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());// Set the title
        builder.setTitle(R.string.load_fm_title);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
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
        LoadingDlg loadDlg = new LoadingDlg();
        loadDlg.setCatId(catItem.id);
        loadDlg.show(getParentFragmentManager(), "loading");
        this.dismiss();
    }
}
