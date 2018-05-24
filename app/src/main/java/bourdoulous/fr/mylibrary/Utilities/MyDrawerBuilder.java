package bourdoulous.fr.mylibrary.Utilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.Accounts.MyImageUtils;
import bourdoulous.fr.mylibrary.WelcomeActivities.MainActivity;
import bourdoulous.fr.mylibrary.R;

public class MyDrawerBuilder {
    private DrawerBuilder drawerBuilder;



    /*
    defaultSelected : 1 -> MainActivity : HOME
                      2 -> SearchActivity : RECHERCHER UN LIVRE
                      3 -> AccountActivity : MA BIBLIOTHEQUE
     */
    public MyDrawerBuilder(final AppCompatActivity activity, int defaultSelected) {

        Bitmap bmp = new MyImageUtils(activity,null).getOriginalBitmap();
        Drawable drawable;
        if(bmp == null){
            drawable = activity.getResources().getDrawable(R.drawable.avatar_logo);
        }else{
             drawable = new BitmapDrawable(bmp);
        }

        final AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.gradient_blue)
                .withDividerBelowHeader(true)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName( AccountStateManager.getSingleton().getAccountConnected())
                                .withIcon(drawable),
                        // MANAGE ACCOUNT
                        new ProfileSettingDrawerItem()
                                .withIcon(R.drawable.ic_settings_black)
                                .withName(R.string.account_settings)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        new MenuManager(activity).gears();
                                        return false;
                                    }
                                })
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();


        drawerBuilder = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(accountHeader)
                .withSelectedItemByPosition(defaultSelected)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home),
                        new PrimaryDrawerItem().withName(R.string.search),
                        new PrimaryDrawerItem().withName(R.string.my_space),
                        new DividerDrawerItem().withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.disconnect)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch(position){
                            case 1:
                                activity.startActivity(new Intent(activity,MainActivity.class));
                                break;
                            case 2:
                                new MenuManager(activity).do_a_research();
                                break;
                            case 3:
                                new MenuManager(activity).accountSpace();
                                break;
                            case 5:
                                new MenuManager(activity).disconnect();
                                activity.finish();

                        }
                        return false;
                    }
                });

    }


    public DrawerBuilder getDrawerBuilder() {
        return drawerBuilder;
    }
}
