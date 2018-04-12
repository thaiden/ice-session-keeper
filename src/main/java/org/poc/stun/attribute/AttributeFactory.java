package org.poc.stun.attribute;


import org.poc.stun.SharedSecret;

public class AttributeFactory {
    public static Fingerprint createFingerprintAttribute() {

        return new Fingerprint();
    }

    public static Software createSoftwareAttribute(byte software[]) {
        Software attribute = new Software();

        attribute.setSoftware(software);
        return attribute;
    }

    public static Integrity createIntegrityAttributes(SharedSecret sharedSecret) {
        return new Integrity(sharedSecret);
    }

    public static Username createUsernameAttribute(String lFrag, String rFrag) {
        return new Username(lFrag, rFrag);
    }

    public static Username createUsernameAttribute(String username) {
        return new Username(username);
    }

    public static Priority createPriorityAttribute(long priority) {
        return new Priority(priority);
    }

}