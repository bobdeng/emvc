package com.handwin.web;
import java.io.*;
import javax.servlet.http.*;


/**
 *
 * @author BillG
 */
public class Tools {
    
    /** Creates a new instance of Tools */
    public Tools() {
    }
    
    public static void writeInt(OutputStream stream, int nData)
        throws IOException 
    {
        stream.write(nData);
        stream.write(nData>>8);
        stream.write(nData>>16);
        stream.write(nData>>24);
    }
    public static void writeShort(OutputStream stream, int nData)
        throws IOException 
    {
        stream.write(nData);
        stream.write(nData>>8);        
    }
    public static void writeString(OutputStream stream, String strMsg)
        throws IOException 
    {
        short sLen = (strMsg == null) ? 0 : (short)strMsg.length();
        writeShort(stream, sLen);
        if (sLen>0)        
            stream.write(strMsg.getBytes());          
    }    

    public static void writeUTF(OutputStream stream, String strMsg)
        throws IOException 
    {
        short sLen = (strMsg == null) ? 0 : (short)strMsg.length();
        writeShort(stream, sLen);        
        
        for (short i=0 ; i<sLen; i++)
        {
            int nData = strMsg.charAt(i);
            stream.write(nData);
            stream.write(nData>>8);
        }            
    }    
    
    public static void writeByteArray(OutputStream stream, byte[] szData)
        throws IOException 
    {
        short sLen = (szData == null) ? 0 : (short)szData.length;
        writeShort(stream, sLen);
        if (sLen>0)        
            stream.write(szData);          
    }
    
/*    public static void writeFiles(OutputStream stream, String strPath, int[] aynFile)
        throws IOException
    {    
        if (aynFile==null)
            return;
        byte szBuffer[] = new byte[512]; 
        for(int i=0;i < aynFile.length;i++)
        {                           
            String strFileName = Integer.toHexString(aynFile[i]) + ".dat";
            File file = new File(strPath, strFileName);
            FileInputStream fin = new FileInputStream(file);            
            int nReadLen = 0;                
            while((nReadLen = fin.read(szBuffer))!=-1)
            {
                stream.write(szBuffer, 0, nReadLen);
            }            
            fin.close();            
       }  
    }
 */
    public static int getParamInt(HttpServletRequest request, String strParm, int nBase, int nDefault)
    {
        String strTemp = request.getParameter(strParm);
        return strTemp==null ? nDefault : Integer.parseInt(strTemp, nBase);
    }
}
