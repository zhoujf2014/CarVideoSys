package com.gtafe.carvideosys;

public class Util {
	public byte[] command(byte type,byte no) {
		byte[] buffer = new byte[13];
		buffer[0] = 0x7E;
		buffer[1] = 0x10;
		buffer[2] = 0x00;
		buffer[3] = 0x00;
		buffer[4] = 0x01;
		buffer[5] = 0x00;
		buffer[6] = 0x00;
		buffer[7] = 0x00;
		buffer[8] = 0x02;
		buffer[9] = type;
		buffer[10] =no;

        byte[] aa={0x10,0x00,0x00,0x01,0x00,0x00,0x00,0x01,type,no};
		byte[] bb=new byte[3];
		get_crc16(aa,aa.length,bb);
		buffer[11] = bb[0];
		buffer[12] = bb[1];

		return buffer;
	}
	
	private byte binary2byte(String[] binaryString){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<binaryString.length;i++){
			sb.append(binaryString[i]);
		}
		String sbstring = sb.toString();
		byte binaryByte = (byte) Integer.parseInt(sbstring, 2);
		return binaryByte;
	}
	
	private String binary2String(String[] binaryString){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<binaryString.length;i++){
			sb.append(binaryString[i]);
		}
		String sbstring = sb.toString();
		return sbstring;
	}
	
	/** 
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit 
     */  
    public static byte[] getBooleanArray(byte b) {  
        byte[] array = new byte[8];  
        for (int i = 7; i >= 0; i--) {  
            array[i] = (byte)(b & 1);  
            b = (byte) (b >> 1);  
        }  
        return array;  
    }  
    
	//字节数组转16进制字符串
    public static String  byteToHexString(byte[] buffer){
    	StringBuilder  stringBuilder = new StringBuilder();
    	if(buffer == null || buffer.length <= 0){
    		return null;
    	}
    	for(int i = 0; i < buffer.length; i++){
    		int j = buffer[i] & 0xFF;
    		String str = Integer.toHexString(j);
    		if(str.length() < 2){
    			stringBuilder.append(0);
    		}
    		stringBuilder.append(str);
    	}
		return stringBuilder.toString();
    }

	// CRC16
	public static int get_crc16(byte[] bufData, int buflen, byte[] pcrc) {
		int ret = 0;
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;

		if (buflen == 0) {
			return ret;
		}
		for (i = 0; i < buflen; i++) {
			CRC ^= ((int) bufData[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
			// System.out.println(Integer.toHexString(CRC));
		}

		System.out.println(Integer.toHexString(CRC));
		pcrc[0] = (byte) (CRC & 0x00ff);
		pcrc[1] = (byte) (CRC >> 8);

		return ret;
	}
}
