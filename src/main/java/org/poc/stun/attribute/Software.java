package org.poc.stun.attribute;

public class Software extends MessageAttribute {

    private byte[] software = null;

    public Software() {
        super(SOFTWARE);
    }

    @Override
    protected byte[] getBytes() {
        byte binValue[] = new byte[HEADER_LENGTH + getDataLength()
            //add padding
            + (4 - getDataLength() % 4) % 4];

        generateHeader(binValue, getDataLength());

        //software
        System.arraycopy(software, 0, binValue, 4, getDataLength());

        return binValue;
    }

    @Override
    public int getDataLength() {
        return 0;
    }


    public byte[] getSoftware()
    {
        if (software == null)
            return null;

        byte[] copy = new byte[software.length];
        System.arraycopy(software, 0, copy, 0, software.length);
        return software;
    }

    public void setSoftware(byte[] software)
    {
        if (software == null)
        {
            this.software = null;
            return;
        }

        this.software = new byte[software.length];
        System.arraycopy(software, 0, this.software, 0, software.length);
    }
}
