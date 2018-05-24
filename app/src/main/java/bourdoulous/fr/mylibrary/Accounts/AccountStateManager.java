package bourdoulous.fr.mylibrary.Accounts;


/**
 * Cette classe permet de savoir quel utilisateur
 * est couramment connecté
 */

public class AccountStateManager {
    private int state = 0; // connected = 1; disconnected = 0
    private String userConnected;
    private static AccountStateManager singleton = null;

    private AccountStateManager(){}

    public static AccountStateManager getSingleton(){
        if ( singleton == null){
           singleton = new AccountStateManager();
        }
        return singleton;
    }

    public void disconnect(){
        userConnected = null;
        singleton = null;
    }

    public void connect(String account){
        userConnected = account;
        state = 1;
    }

    public int getState(){
        return state;
    }

    public String getAccountConnected(){
        return userConnected;
    }

}
