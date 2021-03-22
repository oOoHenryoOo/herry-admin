package me.zhengjie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CodeUtil {

	public static byte[] EToA = hexToBytes(getEDBToAsc());
	public static byte[] AToE = hexToBytes(getAscToEDB());

	public static String getAscToEDB() {
		StringBuffer sb = new StringBuffer();
		sb.append("00010203372D2E2F1605250B0C0D0E0F");
		sb.append("101112133C3D322618193F271C1D1E1F");
		sb.append("405A7F7B5B6C507D4D5D5C4E6B604B61");
		sb.append("F0F1F2F3F4F5F6F7F8F97A5E4C7E6E6F");
		sb.append("7CC1C2C3C4C5C6C7C8C9D1D2D3D4D5D6");
		sb.append("D7D8D9E2E3E4E5E6E7E8E9ADE0BD5F6D");
		sb.append("79818283848586878889919293949596");
		sb.append("979899A2A3A4A5A6A7A8A9C04FD0A107");
		sb.append("202122232415061728292A2B2C090A1B");
		sb.append("30311A333435360838393A3B04143EE1");
		sb.append("41424344454647484951525354555657");
		sb.append("58596263646566676869707172737475");
		sb.append("767778808A8B8C8D8E8F909A9B9C9D9E");
		sb.append("9FA0AAABAC4AAEAFB0B1B2B3B4B5B6B7");
		sb.append("B8B9BABBBC6ABEBFCACBCCCDCECFDAdB");
		sb.append("DCDDDEDFEAEBECEDEEEFFAFBFCFDFEFF");
		return sb.toString();
	}

	public static String getEDBToAsc() {
		StringBuffer sb = new StringBuffer();
		sb.append("000102039C09867F978D8E0B0C0D0E0F");
		sb.append("101112139D8508871819928F1C1D1E1F");
		sb.append("80818283840A171B88898A8B8C050607");
		sb.append("909116939495960498999A9B14159E1A");
		sb.append("20A0A1A2A3A4A5A6A7A8D52E3C282B7C");
		sb.append("26A9AAABACADAEAFB0B121242A293B5E");
		sb.append("2D2FB2B3B4B5B6B7B8B9E52C255F3E3F");
		sb.append("BABBBCBDBEBFC0C1C2603A2340273D22");
		sb.append("C3616263646566676869C4C5C6C7C8C9");
		sb.append("CA6A6B6C6D6E6F707172CBCCCDCECFD0");
		sb.append("D17E737475767778797AD2D3D45BD6D7");
		sb.append("D8D9DADBDCDDDEDFE0E1E2E3E45DE6E7");
		sb.append("7B414243444546474849E8E9EAEBECED");
		sb.append("7D4A4B4C4D4E4F505152EEEFF0F1F2F3");
		sb.append("5C9F535455565758595AF4F5F6F7F8F9");
		sb.append("30313233343536373839FAFBFCFDFEFF");
		return sb.toString();
	}

	public static byte[] hexToBytes(char[] hex) {
		int length = hex.length / 2;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << 4) | low;
			if (value > 127)
				value -= 256;
			raw[i] = (byte) value;
		}
		return raw;
	}

	public static byte[] hexToBytes(String hex) {
		return hexToBytes(hex.toCharArray());
	}

	/**
	 * byte :: ASCII->EBCDIC
	 */
	public static int ASCIIToEBCDIC(int ascii) {
		return AToE[ascii & 0xff] & 0xff;
	}

	/**
	 * byte :: EBCDIC->ASCII
	 */
	public static int EBCDICToASCII(int ebcdic) {
		return EToA[ebcdic & 0xff] & 0xff;
	}

	/**
	 * byte[] :: ASCII->EBCDIC
	 */
	public static byte[] ASCIIToEBCDIC(byte[] ascii) {
		byte[] tobytes = new byte[ascii.length];
		for (int i = 0; i < ascii.length; i++)
			tobytes[i] = (byte) ASCIIToEBCDIC(ascii[i]);
		return tobytes;
	}

	/**
	 * byte[] :: EBCDIC->ASCII
	 */
	public static byte[] EBCDICToASCII(byte[] ebcdic) {
		byte[] tobytes = new byte[ebcdic.length];
		for (int i = 0; i < ebcdic.length; i++)
			tobytes[i] = (byte) EBCDICToASCII(ebcdic[i]);
		return tobytes;
	}

	/**
	 * String :: ASCII->EBCDIC
	 */
	public static String ASCIIToEBCDIC(String ascii) throws Exception {
		return new String(ASCIIToEBCDIC(ascii.getBytes("iso-8859-1")),
				"iso-8859-1");
	}

	/**
	 * String :: EBCDIC->ASCII
	 */
	public static String EBCDICToASCII(String ebcdic) throws Exception {
		return new String(EBCDICToASCII(ebcdic.getBytes("iso-8859-1")),
				"iso-8859-1");
	}

	/**
	 * File :: ASCII->EBCDIC
	 */
	public static void ASCIIToEBCDIC(String fromfile, String tofile) {
		try {
			FileInputStream in = new FileInputStream(new File(fromfile));
			FileOutputStream out = new FileOutputStream(new File(tofile));
			int tempint, i = 0;
			byte[] tempbytes = new byte[in.available()];
			while ((tempint = in.read()) != -1)
				tempbytes[i++] = (byte) tempint;
			out.write(ASCIIToEBCDIC(tempbytes));
			in.close();
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * File :: EBCDIC->ASCII
	 */
	public static void EBCDICToASCII(String fromfile, String tofile) {
		try {
			FileInputStream in = new FileInputStream(new File(fromfile));
			FileOutputStream out = new FileOutputStream(new File(tofile));
			int tempint, i = 0;
			byte[] tempbytes = new byte[in.available()];
			while ((tempint = in.read()) != -1)
				tempbytes[i++] = (byte) tempint;
			out.write(EBCDICToASCII(tempbytes));
			in.close();
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			String srcStr = "Y2I10000CIBF01  0000000006000000000000000000000 ?DP      START?TEST ACCOUNT"
					+ "000348                     ?0000000009387.93+?0000000000000.00+?0000000000000.00"
					+ "+?0000000000000.00+?00000000?00000000?0.00000000+?00000000?0000000009387.93+?000"
					+ "0000009387.93+?00000000000.00+?00000000000.00+?0000000000000.00+?0000000000000.0"
					+ "0+?0000000000000.00+?0000000000000.00+?0000000009387.93+?0000000000001.00+?00000"
					+ "00000001.82+?0000000000000.00+?0000000000000.00+?0000000000000.00+?0000000000000"
					+ ".00+?0000000000000.00+?0000000000000.00+?0000000000000.00+?0000000000000.00+?000"
					+ "006?END";
			String srcStr1 = "3èò????????????@@???????????????????????????????@j?×@@@@@@?????j????@???????@???ó??@@@@@@@@@@@@@@@@@@@@@j?????????ùó?÷KùóNj?????????????K??Nj?????????????K??Nj?????????????K??Nj????????j????????j?K????????Nj????????j?????????ùó?÷KùóNj?????????ùó?÷KùóNj???????????K??Nj???????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????ùó?÷KùóNj?????????????K??Nj?????????????K?òNj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj?????????????K??Nj??????j???";
			String outStr = ASCIIToEBCDIC(srcStr);
			System.out.println(outStr);
			System.out.println(EBCDICToASCII(srcStr1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
