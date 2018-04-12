package org.poc.stun.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utility {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static byte integerToOneByte(int value) throws UtilityException {
		if ((value > Math.pow(2,15)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^15");
		}
		return (byte)(value & 0xFF);
	}
	
	public static byte[] integerToTwoBytes(int value) throws UtilityException {
		byte[] result = new byte[2];
		if ((value > Math.pow(2,31)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^31");
		}
        result[0] = (byte)((value >>> 8) & 0xFF);
        result[1] = (byte)(value & 0xFF);
		return result; 
	}
	
	public static byte[] integerToFourBytes(int value) throws UtilityException {
		byte[] result = new byte[4];
		if ((value > Math.pow(2,63)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^63");
		}
        result[0] = (byte)((value >>> 24) & 0xFF);
		result[1] = (byte)((value >>> 16) & 0xFF);
		result[2] = (byte)((value >>> 8) & 0xFF);
        result[3] = (byte)(value & 0xFF);
		return result; 
	}
	
	public static int oneByteToInteger(byte value) throws UtilityException {
		return (int)value & 0xFF;
	}
	
	public static int twoBytesToInteger(byte[] value) throws UtilityException {
		if (value.length < 2) {
			throw new UtilityException("Byte array too short!");
		}
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        return ((temp0 << 8) + temp1);
	}
	
	public static long fourBytesToLong(byte[] value) throws UtilityException {
		if (value.length < 4) {
			throw new UtilityException("Byte array too short!");
		}
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
		int temp2 = value[2] & 0xFF;
		int temp3 = value[3] & 0xFF;
        return (((long)temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
	}

    public static void nullifyArray(byte [] bytes, int length) {
        for (int count = 0; count < length; count++) {
            bytes[count] = 0;
        }
    }

    public static void setValueInArray(int length, byte value, int module, byte[] bytes) {
        for (int i=length; i<(length + length % module); i++) {
            bytes[i] = value;
        }
    }

    public static byte[] hmac(byte key[], byte text[]) {
        return hmac(key, text, 64);
    }

	public static byte[] hmac(byte key[], byte text[], int blockSize) {
		byte key0[];
		if (key.length==blockSize) key0 = key;
		else if (key.length < blockSize) {
			key0 = new byte[blockSize];
            System.arraycopy(key, 0, key0, 0, key.length);
			for (int i=key.length; i<blockSize; i++) key0[i] = 0;
		}
		else {
			byte h[] = hash(key);
			key0 = new byte[blockSize];
			int len = blockSize;

            assert h != null;

            if (h.length<blockSize) len = h.length;
            System.arraycopy(h, 0, key0, 0, len);
			for (int i=len; i<blockSize; i++) key0[i] = 0;
		}

		byte ipad[] = new byte[key0.length];
		for (int i=0; i<key0.length; i++) ipad[i] = (byte) (key0[i] ^ 0x36);

		byte res[] = new byte[ipad.length + text.length];
        System.arraycopy(ipad, 0, res, 0, ipad.length);
        System.arraycopy(text, 0, res, ipad.length, text.length);
		byte h[] = hash(res);

		byte opad[] = new byte[key0.length];
		for (int i=0; i<key0.length; i++) opad[i] = (byte) (key0[i] ^ 0x5c);

		res = new byte[opad.length+h.length];
        System.arraycopy(opad, 0, res, 0, opad.length);
        System.arraycopy(h, 0, res, opad.length, h.length);

		h = hash(res);

		return h;
	}

    private static byte[] hash(byte input[]) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }

        md.update(input);
        return md.digest();
    }

    public static String toString(byte[] bytes)
    {
        if (bytes == null)
            return null;
        else
        {
            try
            {
                return new String(bytes, "UTF-8");
            }
            catch (UnsupportedEncodingException ueex)
            {
                throw new UndeclaredThrowableException(ueex);
            }
        }
    }

    public static byte[] getBytes(String s)
    {
        if (s == null)
            return null;
        else
        {
            try
            {
                return s.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException ueex)
            {
                throw new UndeclaredThrowableException(ueex);
            }
        }
    }

    public static byte[] calculateHmacSha1(byte[] message,
                                           int    offset,
                                           int    length,
                                           byte[] key)
        throws IllegalArgumentException
    {
        byte[] hmac;

        try
        {
            // get an HMAC-SHA1 key from the raw key bytes
            SecretKeySpec signingKey
                = new SecretKeySpec(key, HMAC_SHA1_ALGORITHM);
            // get an HMAC-SHA1 Mac instance and initialize it with the key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);

            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] macInput = new byte[length];

            //doFinal seems incapable to only work with a part of an array
            //so we'd need to create an array that only contains what we
            //actually need to work with.
            System.arraycopy(message, offset, macInput, 0, length);
            hmac = mac.doFinal(macInput);
        }
        catch (Exception exc)
        {
            throw new IllegalArgumentException(
                "Could not create HMAC-SHA1 request encoding: ", exc);
        }
        return hmac;
    }

    public static void generateHeader(byte[] dest, char type, int length) {
        // Type
        dest[0] = (byte) (type >> 8);
        dest[1] = (byte) (type & 0x00FF);

        // Length
        dest[2] = (byte) (length >> 8);
        dest[3] = (byte) (length & 0x00FF);

    }

}
