package bourdoulous.fr.mylibrary.Utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.Accounts.LoginActivity;
import bourdoulous.fr.mylibrary.Accounts.UpdateAccountActivity;
import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.Library.AccountActivity;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.SearchABook.SearchActivity;


/**
 * Created by bourd on 20/03/2018.
 */

public class MenuManager {
    private AccountStateManager accountStateManager = AccountStateManager.getSingleton();
    private AppCompatActivity activity;

    MenuManager(AppCompatActivity activity){
        this.activity = activity;
    }


    public void accountSpace() {
        Intent intent;
        switch(accountStateManager.getState()){
            case 0: // actuellement aucun compte actif => soit se connecter soit s'enregistrer
                intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                break;
            case 1:  // compte actif => account caracteristiques + favoris...
                intent = new Intent(activity, AccountActivity.class);
                activity.startActivity(intent);
        }
    }

    public void do_a_research() {

        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }





    public void gears() {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle("Réglages");
        dialog.setContentView(R.layout.gears_account_layout);
        dialog.show();

        LinearLayout deleteAccountLayout = dialog.findViewById(R.id.delete_account);
        LinearLayout updateAccountLayout = dialog.findViewById(R.id.update_account_information);

        //DELETE LISTENER
        deleteAccountLayout.setOnClickListener(new View.OnClickListener() {

            private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch(i){
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            new MyDatabaseHelper(activity).deleteAccount(AccountStateManager.getSingleton().getAccountConnected());
                            AccountStateManager.getSingleton().disconnect();
                            dialogInterface.dismiss();
                            dialog.dismiss();
                            Intent intent = new Intent(activity,LoginActivity.class);
                            activity.startActivity(intent);
                    }
                }
            };

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.sure_want_delete_account)
                        .setNegativeButton(R.string.no,listener)
                        .setPositiveButton(R.string.yes,listener);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button nButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nButton.setTextColor(Color.BLACK);

                Button pButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pButton.setTextColor(Color.BLACK);

            }
        });

        updateAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(activity,UpdateAccountActivity.class);
                activity.startActivity(intent);
            }
        });


    }

    public void disconnect(){
        AccountStateManager.getSingleton().disconnect();
        Intent intent = new Intent(activity,LoginActivity.class);
        activity.startActivity(intent);
    }

}