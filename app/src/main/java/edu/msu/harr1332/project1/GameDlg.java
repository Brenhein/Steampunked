package edu.msu.harr1332.project1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.msu.harr1332.project1.Cloud.Cloud;

public class GameDlg extends DialogFragment {

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.load_game_title);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.catalog_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });


        final AlertDialog dlg = builder.create();

        // Find the list view
        ListView list = (ListView)view.findViewById(R.id.listGame);

        // Create an adapter
        final Cloud.CatalogAdapter adapter = new Cloud.CatalogAdapter(list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Get the id of the one we want to load
                final String catId = adapter.getId(position);
                final LobbyActivity lobbyActivity = (LobbyActivity) getActivity();
                final View view2 = view;
                final View view3 = getActivity().findViewById(R.id.gameLobby);
                // Dismiss the dialog box
                dlg.dismiss();

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Cloud cloud = new Cloud();
                        final boolean ok = cloud.joinFromCloud(username, Integer.parseInt(catId));
                        if(!ok) {
                            /*
                             * If we fail to save, display a toast
                             */
                            // Please fill this in...
                            view3.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view3.getContext(), "failure to join", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            lobbyActivity.finishJoin(Integer.parseInt(catId));

                            /*
                            // Success!
                            if(getContext() instanceof LobbyActivity) {
                                ((LobbyActivity)getContext()).finishJoin();
                            }
                            Intent intent = new Intent(LobbyActivity., GameBoardOnlineActivity.class);
                            intent.putExtra("entry_code", 1);
                            startActivity(intent);*/
                        }
                    }
                }).start();


            }

        });

        return dlg;
    }

}
