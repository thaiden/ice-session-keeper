package org.poc.stun;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.poc.logger.Logger;
import org.poc.logger.LoggerFactory;
import org.poc.stun.util.SaslPrep;

public class SharedSecret {
    public enum CredentialsType {
        SHORT_TERM,
        LONG_TERM
    }


    private static final Logger logger = LoggerFactory.getLogger();

    private String username;
    private String password;
    private String realm;
    private CredentialsType type;

    private byte[] remoteKey = null;


    public SharedSecret(final String username, String password, CredentialsType type) {
        this(username, password, null, type);
    }


    public SharedSecret(final String username, String password, String realm, CredentialsType type) {
        this.username = username;
        this.password = password;
        this.realm = realm;
        this.type = type;

    }

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }

    public String getRealm() {
        return realm;
    }

    public byte[] generateRemoteKey() {
        if (remoteKey == null) {
            // MD5(username ":" realm ":" SASLprep(password))
            StringBuilder keyBuilder = new StringBuilder();

            switch (type) {

                case LONG_TERM:
                    if (username != null)
                        keyBuilder.append(SaslPrep.prepare(username));

                    keyBuilder.append(':');

                    if (realm != null)
                        keyBuilder.append(realm);
                    keyBuilder.append(':');

                    if (password != null) {
                        keyBuilder.append(SaslPrep.prepare(password));

                    }

                    MessageDigest md5;

                    try {
                        md5 = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                        throw new UndeclaredThrowableException(e);
                    }

                    remoteKey = md5.digest(keyBuilder.toString().getBytes());
                    break;

                case SHORT_TERM:
                    remoteKey = SaslPrep.prepare(password).getBytes();
                    break;
            }
        }

        return remoteKey;
    }
}
