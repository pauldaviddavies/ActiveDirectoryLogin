package ad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * @author Paul Davies
 */
public class Ad {
    private final String username;
    private final String passowrd;
    private final Properties properties;
    
    public Ad(String username, String password){
        properties = new Properties();
        getProperties();
        
        this.username = createUsername(username);
        this.passowrd = password;
    }
    
    private Properties createConnectionProperties() {
        Properties prop = new Properties();
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        prop.put(Context.PROVIDER_URL, this.createUrl());
        prop.put(Context.SECURITY_AUTHENTICATION,"simple");
        prop.put(Context.SECURITY_PRINCIPAL, this.getUsername());
        prop.put(Context.SECURITY_CREDENTIALS,this.getPassword());
        
        return prop;
    }
    public boolean login() {
        try {
            DirContext ctx = new InitialDirContext(createConnectionProperties());
            ctx.close();
            return true;
        }
        catch(NamingException e){
            return false;
        }
    }
    
    private String getUsername() {
        return this.username;
    }
    
    private String getPassword() {
        return this.passowrd;
    }
    public SearchResult getSearchControls(String search, String searchBase) {
        DirContext context = connectToAd();
        if(context != null){
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningObjFlag(true);
        
        String[] returningAttribute = new String[4];
        returningAttribute[0] = "sAMAccountName";
        returningAttribute[1] = "mail";
        returningAttribute[2] = "sn";
        returningAttribute[3] = "givenname";
        
        searchControls.setReturningAttributes(returningAttribute);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String baseFilter = "(&((&(objectCategory=Person)(objectClass=User)))";
        String filter = baseFilter;
        filter += "(samaccountname="+search+"))";
        NamingEnumeration answer;
        SearchResult searchResult;
        try{
            String base = (null == searchBase) ? createUrl() : createDC();
            answer = context.search(base, filter, searchControls);
            searchResult = (SearchResult)answer.next();
            
            return searchResult;
        }
        catch(NamingException e){
            System.out.println("Error creating search object "+e.getMessage());
            return null;
        }
        }
        else {
            return null;
        }
        
    }
    
    private void getProperties() {
        try{
            FileInputStream fileInputStream = new FileInputStream("F:\\compulynx\\ad\\ActiveDirectoryLogin\\src\\ad\\config.properties");
            this.properties.load(fileInputStream);
        }
        catch(FileNotFoundException e) {
            System.out.println("Properties file not found");
        }
        catch(IOException e){
            System.out.println("Error reading the properties file");
        }
    }
    
    private String getServer() {
        return this.properties.getProperty("server");
    }
    
    private String getDomain() {
        return this.properties.getProperty("domain");
    }
    
    private DirContext connectToAd() {
        try{
            return new InitialDirContext(createConnectionProperties());
        }
        catch(NamingException e) {
            return null;
        }
    }
    
    private String createUrl() {
        return "ldap://"+this.getServer()+"/"+createDC();
    }
    
    private String createUsername(String username) {
        if(!username.contains("@")) {
            username = username+"@"+this.getDomain();
        }
        
        return username;
    }
    
    private String createDC() {
        char[] namePair = this.getDomain().toUpperCase().toCharArray();
        String dn = "CN=Users,DC=";
        for (int i = 0; i < namePair.length; i++) {
            if (namePair[i] == '.') {
                    dn += ",DC=" + namePair[++i];
            } else {
                    dn += namePair[i];
            }
        }
      return dn;
    }
}
