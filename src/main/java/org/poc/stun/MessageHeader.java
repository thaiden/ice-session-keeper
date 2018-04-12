package org.poc.stun;

import java.util.*;

import org.poc.stun.attribute.Attribute;
import org.poc.stun.attribute.AttributeFactory;
import org.poc.stun.attribute.ContentDependentAttribute;
import org.poc.stun.net.StackProperties;
import org.poc.stun.util.Utility;
import org.poc.stun.util.UtilityException;

public class MessageHeader implements Encodable{
    /**
     * Describes which attributes are present in which messages.  An
     * M indicates that inclusion of the attribute in the message is
     * mandatory, O means its optional, C means it's conditional based on
     * some other aspect of the message, and N/A means that the attribute is
     * not applicable to that message type.
     *
     * For classic STUN :
     *
     *
     *                                         Binding  Shared  Shared  Shared        <br/>
     *                       Binding  Binding  Error    Secret  Secret  Secret        <br/>
     *   Att.                Req.     Resp.    Resp.    Req.    Resp.   Error         <br/>
     *                                                                  Resp.         <br/>
     *   _____________________________________________________________________        <br/>
     *   MAPPED-ADDRESS      N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   RESPONSE-ADDRESS    O        N/A      N/A      N/A     N/A     N/A           <br/>
     *   CHANGE-REQUEST      O        N/A      N/A      N/A     N/A     N/A           <br/>
     *   SOURCE-ADDRESS      N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   CHANGED-ADDRESS     N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   USERNAME            O        N/A      N/A      N/A     M       N/A           <br/>
     *   PASSWORD            N/A      N/A      N/A      N/A     M       N/A           <br/>
     *   MESSAGE-INTEGRITY   O        O        N/A      N/A     N/A     N/A           <br/>
     *   ERROR-CODE          N/A      N/A      M        N/A     N/A     M             <br/>
     *   UNKNOWN-ATTRIBUTES  N/A      N/A      C        N/A     N/A     C             <br/>
     *   REFLECTED-FROM      N/A      C        N/A      N/A     N/A     N/A           <br/>
     *   XOR-MAPPED-ADDRESS  N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   XOR-ONLY            O        N/A      N/A      N/A     N/A     N/A           <br/>
     *   SOFTWARE            N/A      O        O        N/A     O       O             <br/>
     *
     */

    /**
     * STUN request code.
     */
    public static final char STUN_REQUEST         = 0x0000;

    /**
     * STUN indication code.
     */
    public static final char STUN_INDICATION      = 0x0010;

    /**
     * STUN success response code.
     */
    public static final char STUN_SUCCESS_RESP    = 0x0100;

    /**
     * STUN error response code.
     */
    public static final char STUN_ERROR_RESP      = 0x0110;

    /**
     * TURN Send request.
     */
    public static final char OLD_DATA_INDICATION = 0x0115;

    /**
     * STUN binding method.
     */
    public static final char STUN_METHOD_BINDING = 0x0001;

    public static final char BINDING_REQUEST               =
        (STUN_METHOD_BINDING | STUN_REQUEST);

    public final static char BINDING_RESPONSE = 0x0101;

    public final static char BINDING_ERROR_RESPONSE = 0x0111;

    public static final byte[] MAGIC_COOKIE = { 0x21, 0x12, (byte)0xA4, 0x42 };

    protected char messageType = 0x0000;

    public static final int DEFAULT_HEADER_LENGTH = 20;
    public static final int RFC3489_TRANSACTION_ID_LENGTH = 16;
    private static final int TRANSACTION_ID_LENGTH = 12;

    private final LinkedHashMap<Character, Attribute> attributes = new LinkedHashMap<>();
    private TransactionID transactionID;

    private static boolean rfc3489CompatibilityMode = false;

	public MessageHeader(char type) {
		this.messageType = type;

        generateTransactionId();
	}

    private void putAttribute(Attribute attribute)
        throws IllegalArgumentException
    {
        synchronized(attributes)
        {
            attributes.put(attribute.getType(), attribute);
        }
    }
    public Attribute getAttribute(char type) {
        synchronized(attributes)
        {
            return attributes.getOrDefault(type, null);
        }
    }

	public Collection<Attribute> getAttributes() {
        synchronized(attributes)
        {
            return new LinkedList<>(attributes.values());
        }
    }

    public Attribute removeAttribute(int attributeType)
    {
        synchronized(attributes)
        {
            return attributes.remove(attributeType);
        }
    }


    public Attribute getMessageAttribute(int type) {
		return attributes.getOrDefault(type, null);
	}

	public void addAttribute(Attribute attribute) {
		putAttribute(attribute);
	}

    public TransactionID getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(TransactionID transactionID) {
        this.transactionID = transactionID;
    }

	public void generateTransactionId() {
		transactionID = TransactionID.createNewTransactionID();
	}

	public byte[] getTransactionId() {
		if (transactionID != null) {
            return transactionID.getBytes();
        } else {
            return new byte[0];
        }
	}

//    private byte[] bytes() throws IOException {
//        int messageLength = 0;
//        for (Attribute attr : getAttributes()) {
//            messageLength += attr.getDataLength();
//            messageLength += (4 - messageLength % 4) % 4;
//        }
//
//        if (messageLength > 0xffff) throw new IOException("To many attributes. Message length to long.");
//
//        byte retBytes[] = new byte[messageLength + DEFAULT_HEADER_LENGTH];
//        Utility.nullifyArray(retBytes, retBytes.length);
//
//        int intType = getTypeAsInt();
//        retBytes[0] = (byte) ((0xff00 & intType) >> 8);
//        retBytes[1] = (byte) (0x00ff & intType);
//
//        retBytes[2] = (byte) ((0xff00 & messageLength) >> 8);
//        retBytes[3] = (byte) (0x00ff & messageLength);
//
//        System.arraycopy(MAGIC_COOKIE, 0, retBytes, 4, MAGIC_COOKIE.length);
//        System.arraycopy(transactionID.getBytes(), 0, retBytes, 4 + MAGIC_COOKIE.length, TRANSACTION_ID_LENGTH);
//
//        return retBytes;
//    }

    public int getDataLength()
    {
        char length = 0;

        Collection<Attribute> attrs = getAttributes();
        for (Attribute attr : attrs)
        {
            int attrLen = attr.getDataLength() + Attribute.HEADER_LENGTH;

            //take attribute padding into account:
            attrLen += (4 - (attrLen % 4)) % 4;

            length += attrLen;
        }
        return length;
    }


    private void prepareForEncoding()
    {
        //remove MESSAGE-INTEGRITY and FINGERPRINT attributes so that we can
        //make sure they are added at the end.
        Attribute msgIntAttr = removeAttribute(Attribute.MESSAGE_INTEGRITY);
        Attribute fingerprint = removeAttribute(Attribute.FINGERPRINT);

        //add a SOFTWARE attribute if the user said so, and unless they did it
        //themselves.
        String software = System.getProperty(StackProperties.SOFTWARE);

        if (getAttribute(Attribute.SOFTWARE) == null
            && software != null && software.length() > 0)
        {
            putAttribute(AttributeFactory
                             .createSoftwareAttribute(software.getBytes()));
        }

        //re-add MESSAGE-INTEGRITY if there was one.
        if (msgIntAttr != null)
        {
            putAttribute(msgIntAttr);
        }

        //add FINGERPRINT if there was one or if user told us to add it
        //everywhere.
        if (fingerprint == null
            && Boolean.getBoolean(StackProperties.ALWAYS_SIGN))
        {
            fingerprint = AttributeFactory.createFingerprintAttribute();
        }

        if (fingerprint != null)
        {
            putAttribute(fingerprint);
        }
    }

    public static MessageHeader decode(byte[] data) throws Exception {
        try {
            byte[] typeArray = new byte[2];
            System.arraycopy(data, 0, typeArray, 0, 2);
            int type = Utility.twoBytesToInteger(typeArray);

            byte[] transactionId = new byte[TRANSACTION_ID_LENGTH];
            System.arraycopy(data, 8, transactionId, 0, TRANSACTION_ID_LENGTH);

            MessageHeader header = new MessageHeader((char) type);
            header.setTransactionID(TransactionID.fromBytes(transactionId));

            return header;

        } catch (UtilityException ue) {
            throw new Exception("Parsing error");
        }
    }

//    public static MessageHeader decode(byte binMessage[], char offset, char arrayLen) throws StunException {
//        int originalOffset = offset;
//        arrayLen = (char) Math.min(binMessage.length, arrayLen);
//
//        if (binMessage == null || arrayLen - offset < DEFAULT_HEADER_LENGTH) {
//            throw new StunException(StunException.ILLEGAL_ARGUMENT,
//                                    "The given binary array is not a valid StunMessage");
//        }
//
//        char messageType = (char) ((binMessage[offset++] << 8)
//            | (binMessage[offset++] & 0xFF));
//
//        MessageHeader message;
//        /* 0x0115 is a old TURN DATA indication message type */
//        if (isResponseType(messageType) && messageType != OLD_DATA_INDICATION) {
//            message = new MessageHeader(messageType);
//        }
//
//        int length = (char)((binMessage[offset++] << 8)
//            | (binMessage[offset++]  & 0xFF));
//
//        /* copy the cookie */
//        byte cookie[] = new byte[4];
//        System.arraycopy(binMessage, offset, cookie, 0, 4);
//        offset += 4;
//
//        boolean rfc3489Compat = false;
//
//        if(!Arrays.equals(MAGIC_COOKIE, cookie))
//        {
//            rfc3489Compat = true;
//        }
//
//        if(arrayLen - offset - TRANSACTION_ID_LENGTH < length)
//        {
//            throw
//                new StunException(
//                    StunException.ILLEGAL_ARGUMENT,
//                    "The given binary array does not seem to contain"
//                        + " a whole StunMessage: given "
//                        + ((int) arrayLen)
//                        + " bytes of "
//                        + message.getName()
//                        + " but expecting "
//                        + (offset + TRANSACTION_ID_LENGTH + length));
//        }
//
//        byte tranID[] = new byte[TRANSACTION_ID_LENGTH];
//        System.arraycopy(binMessage, offset, tranID, 0, TRANSACTION_ID_LENGTH);
//        try
//        {
//            if(rfc3489Compat)
//            {
//                byte rfc3489TranID[] = new byte[TRANSACTION_ID_LENGTH + 4];
//                System.arraycopy(cookie, 0, rfc3489TranID, 0, 4);
//                System.arraycopy(tranID, 0, rfc3489TranID, 4,
//                                 TRANSACTION_ID_LENGTH);
//                message.setTransactionID(TransactionID.fromBytes(rfc3489TranID));
//            }
//            else
//            {
//                message.setTransactionID(TransactionID.fromBytes(tranID);
//            }
//        }
//        catch (StunException exc)
//        {
//            throw new StunException( StunException.ILLEGAL_ARGUMENT,
//                                     "The given binary array does not seem to "
//                                         + "contain a whole StunMessage", exc);
//        }
//
//        offset += TRANSACTION_ID_LENGTH;
//
//        while(offset - DEFAULT_HEADER_LENGTH < length)
//        {
//            Attribute att = AttributeDecoder.decode(
//                binMessage, offset, (char)(length - offset));
//
//            performAttributeSpecificActions(att, binMessage,
//                                            originalOffset, offset);
//
//            message.putAttribute(att);
//            offset += att.getDataLength() + Attribute.HEADER_LENGTH;
//
//            //now also skip any potential padding that might have come with
//            //this attribute.
//            if((att.getDataLength() % 4) > 0)
//            {
//                offset += (4 - (att.getDataLength() % 4));
//            }
//        }
//
//        return message;
//    }

    public byte[] encode() {
        prepareForEncoding();

        int dataLength = getDataLength();

        byte binMsg[] = new byte[DEFAULT_HEADER_LENGTH + dataLength];
        int offset = 0;

        // STUN Message Type
        binMsg[offset++] = (byte) (getMessageType() >> 8);
        binMsg[offset++] = (byte) (getMessageType() & 0xFF);

        // Message Length
        final int messageLengthOffset = offset;

        offset += 2;

        byte tranID[] = getTransactionId();

        if (tranID.length == TRANSACTION_ID_LENGTH) {
            System.arraycopy(MAGIC_COOKIE, 0, binMsg, offset, 4);
            offset += 4;
            System.arraycopy(tranID, 0, binMsg, offset, TRANSACTION_ID_LENGTH);
            offset += TRANSACTION_ID_LENGTH;
        } else {
            /* RFC3489 behavior */
            System.arraycopy(tranID, 0, binMsg, offset,
                             RFC3489_TRANSACTION_ID_LENGTH);
            offset += RFC3489_TRANSACTION_ID_LENGTH;
        }

        int dataLengthForContentDependentAttribute = 0;
        Vector<Map.Entry<Character, Attribute>> v = new Vector<>();
        Iterator<Map.Entry<Character, Attribute>> iter;

        synchronized (attributes) {
            v.addAll(attributes.entrySet());
        }

        iter = v.iterator();

        while (iter.hasNext()) {
            Attribute attribute = iter.next().getValue();
            int attributeLength
                = attribute.getDataLength() + Attribute.HEADER_LENGTH;

            //take attribute padding into account:
            attributeLength += (4 - attributeLength % 4) % 4;
            dataLengthForContentDependentAttribute += attributeLength;

            //special handling for message integrity and fingerprint values
            byte[] binAtt;

            if (attribute instanceof ContentDependentAttribute) {
                /*
                 * The "Message Length" seen by a ContentDependentAttribute is
                 * up to and including the very Attribute but without any other
                 * Attribute instances after it.
                 */
                binMsg[messageLengthOffset]
                    = (byte) (dataLengthForContentDependentAttribute >> 8);
                binMsg[messageLengthOffset + 1]
                    = (byte) (dataLengthForContentDependentAttribute & 0xFF);
                binAtt
                    = ((ContentDependentAttribute) attribute).encode(binMsg, 0, offset);
            } else {
                binAtt = attribute.encode();
            }

            System.arraycopy(binAtt, 0, binMsg, offset, binAtt.length);
            /*
             * Offset by attributeLength and not by binAtt.length because
             * attributeLength takes the attribute padding into account and
             * binAtt.length does not.
             */
            offset += attributeLength;
        }

        // Message Length
        binMsg[messageLengthOffset] = (byte) (dataLength >> 8);
        binMsg[messageLengthOffset + 1] = (byte) (dataLength & 0xFF);

        return binMsg;
    }


    public static boolean isErrorResponseType(char type)
    {
        return ((type & 0x0110) == STUN_ERROR_RESP);
    }

    public static boolean isSuccessResponseType(char type)
    {
        return ((type & 0x0110) == STUN_SUCCESS_RESP);
    }

    public static boolean isResponseType(char type)
    {
      /* return (((type >> 8) & 1) != 0); */
        return (isSuccessResponseType(type) || isErrorResponseType(type));
    }

    public static boolean isIndicationType(char type)
    {
        return ((type & 0x0110) == STUN_INDICATION);
    }

    public static boolean isRequestType(char type)
    {
      /* return !isResponseType(type); */
        return ((type & 0x0110) == STUN_REQUEST);
    }

    public char getMessageType()
    {
        return messageType;
    }
}
