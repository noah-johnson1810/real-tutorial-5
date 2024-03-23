package edu.sdsmt.johnson_n_a.tutorial56;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HatterActivity extends AppCompatActivity {

    /**
     * Key for the parameters in the bundle
     */
    private static final String PARAMETERS = "parameters";

    /**
     * Request code for storage permission
     */
    private static final int NEED_PERMISSIONS = 1;

    /**
     * flags for a out of sequence event where the activities saves it state, but hasn't fully reloaded
     */
    private boolean alreadySaved = false;
    private boolean pendingConfirmation = false;
    /**
     * Activity launchers for color selection
     */
    private ActivityResultLauncher<Intent> colorResultLauncher;

    /**
     * Activity launcher for getting an image from the gallery
     */
    private ActivityResultLauncher<String> imageResultLauncher;

    /**
     * The hatter view object
     */
    private HatterView hatterView = null;
    /**
     * The color select button
     */
    private Button colorButton = null;
    /**
     * The feather checkbox
     */
    private CheckBox featherCheck = null;
    /**
     * The hat choice spinner
     */
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MonitorFirebase monitor = MonitorFirebase.INSTANCE;

        setContentView(R.layout.activity_hatter);

        /*
         * Get some of the views we'll keep around
         */
        hatterView = findViewById(R.id.hatterView);
        colorButton = findViewById(R.id.buttonColor);
        featherCheck = findViewById(R.id.checkFeather);
        spinner = findViewById(R.id.spinnerHat);

        /*
         *register activity launchers
         */
        ActivityResultContracts.StartActivityForResult contract = new ActivityResultContracts.StartActivityForResult();
        colorResultLauncher = registerForActivityResult(contract, (result) -> {
            int resultCode = result.getResultCode();
            if (resultCode == Activity.RESULT_OK) {
                Intent data = result.getData();
                hatterView.setColor(data.getIntExtra(ColorSelectActivity.SELECTOR_COLOR, 0));
            }
        });

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), (result) -> {
            if (result != null) {
                setUri(result);
            }
        });


        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        String[] hats = getResources().getStringArray(R.array.hats_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, hats);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                hatterView.setHat(pos);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        /*
         * Restore any state
         */
        if (savedInstanceState != null) {
            hatterView.getFromBundle(PARAMETERS, savedInstanceState);
            spinner.setSelection(hatterView.getHat());
        }

        /*
         * Ensure the user interface is up to date
         */
        updateUI();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case NEED_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserPicture();

                } else {
                    // permission denied, boo! Tell the users the no images can be loaded
                    Toast.makeText(getApplicationContext(), R.string.denied,
                            Toast.LENGTH_SHORT).show(); //make the dialog box
                }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        hatterView.putToBundle(PARAMETERS, outState);

        alreadySaved = true;
    }

    public void setUri(Uri uri) {
        hatterView.setImageUri(uri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alreadySaved = false;

        //awaiting a confirmation dialog bos after saving the state?
        if (pendingConfirmation) {
            getUserPicture();
        }
    }

    /**
     * Handle a Picture button press
     *
     * @param view the button that causes the event
     */
    public void onPicture(View view) {
        String permission;

        //change in image permission in Tiramisu and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        //still checking for both just in case
        boolean permissions = ActivityCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED;

        if (permissions) {

            AlertDialog.Builder builder = new AlertDialog.Builder((HatterActivity.this));
            builder.setMessage(R.string.permissionRational);

            //make the OK button ask for permission
            builder.setPositiveButton(android.R.string.ok, (dialog, id) -> ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission}, NEED_PERMISSIONS));

            builder.create().show();

        } else {
            getUserPicture();
        }
    }


    /**
     * method to setup and show picture dialog
     */
    private void getUserPicture() {
        if (alreadySaved) {
            pendingConfirmation = true;
        } else {
            // Bring up the picture selection dialog box
            PictureDlg dialog = new PictureDlg(imageResultLauncher);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    /**
     * Handle the feather checkmark press
     *
     * @param view the view
     */
    public void onFeather(View view) {
        hatterView.setFeather(featherCheck.isChecked());
        updateUI();
    }

    /**
     * Start up the activity to choose a color
     *
     * @param view the view
     */
    public void onColor(View view) {
        //any target
        Intent intent = new Intent(this, ColorSelectActivity.class);
        colorResultLauncher.launch(intent);
    }

    /**
     * Ensure the user interface components match the current state
     */
    public void updateUI() {
        spinner.setSelection(hatterView.getHat());
        featherCheck.setChecked(hatterView.getFeather());
        colorButton.setEnabled(hatterView.getHat() == HatterView.HAT_CUSTOM);
    }

    /**
     * Called when it is time to create the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hatter, menu);
        return true;
    }

    /**
     * Handle options menu selections
     *
     * @param item Menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_reset) {
            hatterView.reset();
            return true;

        } else if (itemId == R.id.menu_load) {
            LoadDlg dlg2 = new LoadDlg();
            dlg2.show(getSupportFragmentManager(), "load");
            return true;
        } else if (itemId == R.id.menu_save) {
            SaveDlg saveDlg = new SaveDlg();
            saveDlg.show(getSupportFragmentManager(), "save");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}