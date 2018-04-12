package org.poc.stun;

import java.util.Arrays;
import java.util.Random;

public class TransactionID
{
    /**
     * RFC5289 Transaction ID length.
     */
    public static final int RFC5389_TRANSACTION_ID_LENGTH = 12;

    /**
     * RFC3489 Transaction ID length.
     */
    public static final int RFC3489_TRANSACTION_ID_LENGTH = 16;

    /**
     * The id itself
     */
    private final byte[] transactionID;

    /**
     * Any object that the application would like to correlate to a transaction.
     */
    private Object applicationData = null;

    /**
     * The object to use to generate the rightmost 8 bytes of the id.
     */
    private static final Random random
        = new Random(System.currentTimeMillis());

    /**
     * A hashcode for hashtable storage.
     */
    private int hashCode = 0;

    /**
     * Limits access to <tt>TransactionID</tt> instantiation.
     */
    private TransactionID() {
        this(false);
    }


    private TransactionID(byte[] transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * Limits access to <tt>TransactionID</tt> instantiation.
     *
     * @param rfc3489Compatibility true to create a RFC3489 transaction ID
     */
    private TransactionID(boolean rfc3489Compatibility) {
        transactionID
            = new byte[
            rfc3489Compatibility
                ? RFC3489_TRANSACTION_ID_LENGTH
                : RFC5389_TRANSACTION_ID_LENGTH];
    }

    /**
     * Creates a transaction id object.The transaction id itself is generated
     * using the following algorithm:
     *
     * The first 6 bytes of the id are given the value of
     * <tt>System.currentTimeMillis()</tt>. Putting the right most bits first
     * so that we get a more optimized equals() method.
     *
     * @return A <tt>TransactionID</tt> object with a unique transaction id.
     */
    public static TransactionID createNewTransactionID() {
        TransactionID tid = new TransactionID();

        generateTransactionID(tid, 12);
        return tid;
    }

    public static TransactionID fromBytes(byte[] transactionID) {
        return new TransactionID(transactionID);
    }

    /**
     * Creates a RFC3489 transaction id object.The transaction id itself is
     * generated using the following algorithm:
     *
     * The first 8 bytes of the id are given the value of
     * <tt>System.currentTimeMillis()</tt>. Putting the right most bits first
     * so that we get a more optimized equals() method.
     *
     * @return A <tt>TransactionID</tt> object with a unique transaction id.
     */
    public static TransactionID createNewRFC3489TransactionID()
    {
        TransactionID tid = new TransactionID(true);

        generateTransactionID(tid, 16);
        return tid;
    }

    /**
     * Generates a random transaction ID
     *
     * @param tid transaction ID
     * @param nb number of bytes to generate
     */
    private static void generateTransactionID(TransactionID tid, int nb)
    {
        long left  = System.currentTimeMillis();//the first nb/2 bytes of the id
        long right = random.nextLong();//the last nb/2 bytes of the id
        int b = nb / 2;

        for(int i = 0; i < b; i++)
        {
            tid.transactionID[i]   = (byte)((left  >> (i * 8)) & 0xFFl);
            tid.transactionID[i + b] = (byte)((right >> (i * 8)) & 0xFFl);
        }

        //calculate hashcode for Hashtable storage.
        tid.hashCode =   (tid.transactionID[3] << 24 & 0xFF000000)
            | (tid.transactionID[2] << 16 & 0x00FF0000)
            | (tid.transactionID[1] << 8  & 0x0000FF00)
            | (tid.transactionID[0]       & 0x000000FF);
    }

    /**
     * Returns the transaction id byte array (length 12 or 16 if RFC3489
     * compatible).
     *
     * @return the transaction ID byte array.
     */
    public byte[] getBytes()
    {
        return transactionID;
    }

    /**
     * If the transaction is compatible with RFC3489 (16 bytes).
     *
     * @return true if transaction ID is compatible with RFC3489
     */
    public boolean isRFC3489Compatible()
    {
        return (transactionID.length == 16);
    }

    /**
     * Compares two TransactionID objects.
     * @param obj the object to compare with.
     * @return true if the objects are equal and false otherwise.
     */
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(!(obj instanceof TransactionID))
            return false;

        byte targetBytes[] = ((TransactionID)obj).transactionID;

        return Arrays.equals(transactionID, targetBytes);
    }

    /**
     * Compares the specified byte array with this transaction id.
     * @param targetID the id to compare with ours.
     * @return true if targetID matches this transaction id.
     */
    public boolean equals(byte[] targetID)
    {
        return Arrays.equals(transactionID, targetID);
    }

    /**
     * Returns the first four bytes of the transactionID to ensure proper
     * retrieval from hashtables.
     * @return the hashcode of this object - as advised by the Java Platform
     * Specification
     */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * Returns a string representation of the ID
     *
     * @return a hex string representing the id
     */
    public String toString()
    {
        return TransactionID.toString(transactionID);
    }

    /**
     * Returns a string representation of the ID
     *
     * @param transactionID the transaction ID to convert into <tt>String</tt>.
     *
     * @return a hex string representing the id
     */
    public static String toString(byte[] transactionID)
    {
        StringBuilder idStr = new StringBuilder();

        idStr.append("0x");
        for(int i = 0; i < transactionID.length; i++)
        {

            if((transactionID[i] & 0xFF) <= 15)
                idStr.append("0");

            idStr.append(
                Integer.toHexString(transactionID[i] & 0xFF).toUpperCase());
        }

        return idStr.toString();
    }

    /**
     * Stores <tt>applicationData</tt> in this ID so that we can refer back to
     * it if we ever need to at a later stage (e.g. when receiving a response
     *
     * @param applicationData a reference to the {@link Object} that the
     * application would like to correlate to the transaction represented by
     * this ID.
     */
    public void setApplicationData(Object applicationData)
    {
        this.applicationData = applicationData;
    }

    /**
     * Returns whatever <tt>applicationData</tt> was previously stored in this
     * ID.
     *
     * @return a reference to the {@link Object} that the application may have
     * stored in this ID's application data field.
     */
    public Object getApplicationData()
    {
        return applicationData;
    }
}
