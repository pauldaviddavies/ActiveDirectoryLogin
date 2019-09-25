package activedirectorylogin;

import ad.Ad;

/**
 * @author Paul Davies
 */
public class ActiveDirectoryLogin {
    public static void main(String[] args) {
        String username = "kenson";
        String password = "password@1";
        Ad ad = new Ad(username, password);
        if(ad.login()) {
            System.out.println("Login Successful");
            // System.out.println("Email: "+ad.getSearchControls(username, null).getAttributes().get("givenname").toString());
        }
        else {
            System.out.println("Failed to log in for the below reasons:\n-Username or password incorrect\n-Account is disabled\n-Or you have not been created in the AD.");
        }
    }
    
}
