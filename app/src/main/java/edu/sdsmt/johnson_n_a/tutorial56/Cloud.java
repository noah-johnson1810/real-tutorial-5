package edu.sdsmt.johnson_n_a.tutorial56;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Cloud {

    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final static DatabaseReference hattingsList = database.getReference("hattings").child(MonitorFirebase.INSTANCE.getUserUid());

    /**
     * method to load a specific hatting
     *
     * @param view  The view we are loading the hatting into
     * @param catId the id of the hatting
     * @param dlg   the dialog box showing the loading state
     */
    public void loadFromCloud(final HatterView view, String catId, final Dialog dlg) {


        DatabaseReference myRef = hattingsList.child(catId);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                view.loadJSON(dataSnapshot);
                dlg.dismiss();
                if (view.getContext() instanceof HatterActivity)
                    ((HatterActivity) view.getContext()).updateUI();
                view.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error condition!
                view.post(() -> {
                    Toast.makeText(view.getContext(), R.string.loading_fail, Toast.LENGTH_SHORT).show();
                    dlg.dismiss();
                });
            }
        });
    }

    /**
     * An class that holds a line's contents for later updating
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data line
        public final View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    /**
     * Nested class to store one catalog row underlying data
     */
    public static class Item {
        public String name = "";
        public String id = "";
    }

    /**
     * An adapter so that list boxes can display a list of filenames from
     * the cloud server.
     */
    public static class CatalogAdapter extends RecyclerView.Adapter<ViewHolder> {
        /**
         * The items we display in the list box. Initially this is
         * null until we get items from the server.
         */
        private final ArrayList<Item> items;

        public final CatalogCallback clickEvent;

        /**
         * Constructor
         */
        public CatalogAdapter(final View view, CatalogCallback click) {
            items = getCatalog(view);
            clickEvent = click;
        }

        public ArrayList<Item> getCatalog(final View view) {
            ArrayList<Item> newItems = new ArrayList<>();

            //connect to the database (hattings child)

            database.goOnline();
            DatabaseReference myRef = hattingsList;


            // Read from the database
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //look at each child
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Item tempItem = new Item();
                        tempItem.name = child.child("name").getValue().toString();
                        tempItem.id = child.getKey();
                        newItems.add(tempItem);
                    }

                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            // Tell the adapter the data set has been changed
                            notifyItemRangeChanged(0, newItems.size());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Error condition!
                    view.post(() -> Toast.makeText(view.getContext(), R.string.catalog_fail, Toast.LENGTH_SHORT).show());
                }
            });
            return newItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.view.setOnClickListener(v -> {
                Item catItem = getItem(position);

                // let the client class do its job
                clickEvent.callback(catItem);
            });

            TextView tv = holder.view.findViewById(R.id.textItem);
            String text = items.get(position).name;
            tv.setText(text);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public Item getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public interface CatalogCallback {
            void callback(Cloud.Item catItem);
        }
    }

    /**
     * Save a hatting to the cloud.
     *
     * @param name name to save under
     * @param view view we are getting the data from
     */
    public void saveToCloud(String name, HatterView view) {
        name = name.trim();
        if (name.length() == 0) {
            /*
             *  If we fail to save, display a toast to let the user know the save failed
             */
            Toast.makeText(view.getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            // please fill in code here
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String key = hattingsList.push().getKey();
            DatabaseReference finalHattingsList = hattingsList.child(key);
            finalHattingsList.child("name").setValue(name, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    // Error condition!
                    /*
                     * make a toast
                     */
                    Toast.makeText(view.getContext(), "Unable to Save Data", Toast.LENGTH_SHORT).show();
                } else {
                    view.saveJSON(finalHattingsList);
                }
            });
            Toast.makeText(view.getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
