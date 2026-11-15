package com.example.medique.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application Properties Configuration
 * Defines custom properties used in application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Twilio twilio = new Twilio();
    private Webauthn webauthn = new Webauthn();
    private File file = new File();
    private Razorpay razorpay = new Razorpay();

    // Getters and Setters
    public Twilio getTwilio() {
        return twilio;
    }

    public void setTwilio(Twilio twilio) {
        this.twilio = twilio;
    }

    public Webauthn getWebauthn() {
        return webauthn;
    }

    public void setWebauthn(Webauthn webauthn) {
        this.webauthn = webauthn;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Razorpay getRazorpay() {
        return razorpay;
    }

    public void setRazorpay(Razorpay razorpay) {
        this.razorpay = razorpay;
    }

    // Nested classes for property groups
    public static class Twilio {
        private String accountSid;
        private String authToken;
        private String phoneNumber;

        public String getAccountSid() {
            return accountSid;
        }

        public void setAccountSid(String accountSid) {
            this.accountSid = accountSid;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class Webauthn {
        private Rp rp = new Rp();

        public Rp getRp() {
            return rp;
        }

        public void setRp(Rp rp) {
            this.rp = rp;
        }

        public static class Rp {
            private String id;
            private String name;
            private String origin;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getOrigin() {
                return origin;
            }

            public void setOrigin(String origin) {
                this.origin = origin;
            }
        }
    }

    public static class File {
        private String uploadDir;

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }
    }

    public static class Razorpay {
        private Key key = new Key();

        public Key getKey() {
            return key;
        }

        public void setKey(Key key) {
            this.key = key;
        }

        public static class Key {
            private String id;
            private String secret;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }
        }
    }
}
