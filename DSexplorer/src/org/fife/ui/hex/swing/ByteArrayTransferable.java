package org.fife.ui.hex.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;

/**
 * A <code>Transferable</code> that transfers an array of bytes.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ByteArrayTransferable implements Transferable
{
    
    private static final DataFlavor[] FLAVORS = {DataFlavor.stringFlavor, DataFlavor.plainTextFlavor,};
    private byte[]                    bytes;
                                      
    private int                       offset;
                                      
    /**
     * Creates a transferable object.
     *
     * @param bytes
     *            The bytes to transfer.
     */
    public ByteArrayTransferable(int offset, byte[] bytes)
    {
        this.offset = offset;
        if (bytes != null)
        {
            this.bytes = bytes.clone();
        }
        else
        {
            this.bytes = new byte[0];
        }
    }
    
    /**
     * Returns the number of bytes being transferred.
     *
     * @return The number of bytes being transferred.
     * @see #getOffset()
     */
    public int getLength()
    {
        return bytes.length;
    }
    
    /**
     * Returns the offset of the first byte being transferred.
     *
     * @return The offset of the first byte.
     * @see #getLength()
     */
    public int getOffset()
    {
        return offset;
    }
    
    /**
     * Returns the data being transferred in a format specified by the <code>DataFlavor</code>.
     *
     * @param flavor
     *            Dictates in what format the data should be returned.
     * @throws UnsupportedFlavorException
     *             If the specified flavor is not supported.
     * @throws IOException
     *             If an IO error occurs.
     * @see DataFlavor#getRepresentationClass()
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (flavor.equals(ByteArrayTransferable.FLAVORS[0]))
        {
            return new String(bytes); // Use platform default charset.
        }
        else if (flavor.equals(ByteArrayTransferable.FLAVORS[1]))
        {
            return new StringReader(new String(bytes));
        }
        throw new UnsupportedFlavorException(flavor);
    }
    
    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in. The array is ordered according to preference for providing the data (from most richly descriptive to least descriptive).
     *
     * @return An array of data flavors in which this data can be transferred.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return ByteArrayTransferable.FLAVORS.clone();
    }
    
    /**
     * Returns whether a data flavor is supported.
     *
     * @param flavor
     *            The flavor to check.
     * @return Whether the specified flavor is supported.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        for (int i = 0; i < ByteArrayTransferable.FLAVORS.length; i++)
        {
            if (flavor.equals(ByteArrayTransferable.FLAVORS[i]))
            {
                return true;
            }
        }
        return false;
    }
    
}