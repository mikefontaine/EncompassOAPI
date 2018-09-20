

public class TokenGenerator {
    private static String user;
    private static String password;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String instance_id;
    private static String token;
    TokenGenerator(String un, String pw, String CID, String CS, String IID){

        setUser(un);
        setPassword(pw);
        setClientId(CID);
        setClientSecret(CS);
        setInstance_id(IID);
        setToken("");


    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        TokenGenerator.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        TokenGenerator.password = password;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static void setClientId(String clientId) {
        CLIENT_ID = clientId;
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }

    public static void setClientSecret(String clientSecret) {
        CLIENT_SECRET = clientSecret;
    }

    public static String getInstance_id() {
        return instance_id;
    }

    public static void setInstance_id(String instance_id) {
        TokenGenerator.instance_id = instance_id;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenGenerator.token = token;
    }
    public void generateToken(){

    }
}
