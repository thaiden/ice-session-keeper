package org.poc.stun.attribute;


public interface Attribute {
    int HEADER_LENGTH = 4;
    /* STUN attributes */
    /**
     * Mapped address attribute.
     */
    char MAPPED_ADDRESS = 0x0001;

    /**
     * Response address attribute.
     */
    char RESPONSE_ADDRESS = 0x0002;

    /**
     * Change request attribute.
     */
    char CHANGE_REQUEST = 0x0003;

    /**
     * Source address attribute.
     */
    char SOURCE_ADDRESS = 0x0004;

    /**
     * Changed address attribute.
     */
    char CHANGED_ADDRESS = 0x0005;

    /**
     * Username attribute.
     */
    char USERNAME = 0x0006;

    /**
     * Password attribute.
     */
    char PASSWORD = 0x0007;

    /**
     * Message integrity attribute.
     */
    char MESSAGE_INTEGRITY = 0x0008;

    /**
     * Error code attribute.
     */
    char ERROR_CODE = 0x0009;

    /**
     * Unknown attributes attribute.
     */
    char UNKNOWN_ATTRIBUTES = 0x000a;

    /**
     * Reflected from attribute.
     */
    char REFLECTED_FROM = 0x000b;

    /**
     * Realm attribute.
     */
    char REALM = 0x0014;

    /**
     * Nonce attribute.
     */
    char NONCE = 0x0015;

    /**
     * XOR Mapped address attribute.
     */
    char XOR_MAPPED_ADDRESS = 0x0020;

    /**
     * XOR only attribute.
     */
    char XOR_ONLY = 0x0021;

    /**
     * Software attribute.
     */
    char SOFTWARE = 0x8022;

    /**
     * Alternate server attribute.
     */
    char ALTERNATE_SERVER = 0x8023;

    /**
     * Fingerprint attribute.
     */
    char FINGERPRINT = 0x8028;

    /**
     * Unknown optional attribute.
     */
    char UNKNOWN_OPTIONAL_ATTRIBUTE = 0x8000;

    /* TURN attributes */
    /**
     * Channel number attribute.
     */
    char CHANNEL_NUMBER = 0x000c;

    /**
     * Lifetime attribute.
     */
    char LIFETIME = 0x000d;

    /**
     * XOR peer address attribute.
     */
    char XOR_PEER_ADDRESS = 0x0012;

    /**
     * Data attribute.
     */
    char DATA = 0x0013;

    /**
     * XOR relayed address attribute.
     */
    char XOR_RELAYED_ADDRESS = 0x0016;

    /**
     * Requested Address Family attribute.
     */
    char REQUESTED_ADDRESS_FAMILY = 0X0017;

    /**
     * Even port attribute.
     */
    char EVEN_PORT = 0x0018;

    /**
     * Requested transport attribute.
     */
    char REQUESTED_TRANSPORT = 0x0019;

    /**
     * Don't fragment attribute.
     */
    char DONT_FRAGMENT = 0x001a;

    /**
     * Reservation token attribute.
     */
    char RESERVATION_TOKEN = 0x0022;

    /**
     * Connection Id attribute.
     * TURN TCP support attribute
     */
    char CONNECTION_ID = 0x002a;

    /* Old TURN attributes */
    /**
     * Magic cookie attribute.
     */
    char MAGIC_COOKIE = 0x000f;

    /**
     * Destination address attribute.
     */
    char DESTINATION_ADDRESS = 0x0011;

    /**
     * Destination address attribute.
     */
    char REMOTE_ADDRESS = 0x0012;

    /* ICE attributes */
    /**
     * Priority attribute.
     */
    char PRIORITY = 0x0024;

    /**
     * Use candidate attribute.
     */
    char USE_CANDIDATE = 0x0025;

    /**
     * ICE controlled attribute.
     */
    char ICE_CONTROLLED = 0x8029;

    /**
     * ICE controlling attribute.
     */
    char ICE_CONTROLLING = 0x802a;


    byte[] encode();
    byte[] getEncodedContent();
    char getType();
    int getDataLength();
}
