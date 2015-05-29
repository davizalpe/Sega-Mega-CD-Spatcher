package main;

import javax.xml.bind.DatatypeConverter;

public class ByteUtils {

	/**
	 * Copies an array from the specified source array "fileData", beginning at
	 * the specified position in "offset", to the specified position of the
	 * destination array "subFileData" with len size. A subsequence of array
	 * components are copied from the source array.
	 * 
	 * @param fileData
	 * @param offset
	 * @param len
	 * @return subFileData generated
	 */
	public static byte[] subByte(byte[] fileData, int offset, int len) {
		byte[] subFileData = new byte[len];

		System.arraycopy(fileData, offset, subFileData, 0, len);

		return subFileData;
	}

	/**
	 * Finds the first occurrence of the pattern in the text and returns the
	 * found position or -1 if it is not found
	 * 
	 * @param data
	 * @param pattern
	 * @return
	 */
	public static int indexOf(byte[] data, byte[] pattern) {
		int[] failure = computeFailure(pattern);

		int j = 0;
		if (data.length == 0)
			return -1;

		for (int i = 0; i < data.length; i++) {
			while (j > 0 && pattern[j] != data[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == data[i]) {
				j++;
			}
			if (j == pattern.length) {
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	/**
	 * Computes the failure function using a boot-strapping process, where the
	 * pattern is matched against itself.
	 */
	private static int[] computeFailure(byte[] pattern) {
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && pattern[j] != pattern[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == pattern[i]) {
				j++;
			}
			failure[i] = j;
		}

		return failure;
	}

	/**
	 * Converts an array of bytes to String format with blank spaces
	 * @param fileData
	 * @return
	 */
	public static String toHexStringWithSpaces(byte[] fileData) {
		StringBuilder sb = new StringBuilder();
		for (byte b : fileData) {
			sb.append(String.format("%02X ", b));
		}

		return sb.toString();
	}

	/**
	 * Converts an array of bytes to String format
	 * @param array
	 * @return
	 */
	public static String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	/**
	 * Converts a string to an array of bytes
	 * @param s
	 * @return
	 */
	public static byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	/**
	 * Search origFileSize in source
	 * @param source
	 * @param replace
	 * @param offset
	 * @param origFileSize
	 */
	public static void replaceFile(byte[] source, byte[] replace, int offset,
			int origFileSize) {
		int len = replace.length;

		int j = 0;
		for (int i = offset; i < offset + len; i++) {
			source[i] = replace[j];
			j++;
		}

		// Replace with 0x00 if new file is less than original
		if (origFileSize > len) {
			byte[] zeros = toByteArray("00");

			// TODO replace j for len var
			for (int i = offset + j; i < offset + origFileSize; i++) {
				source[i] = zeros[0];
			}
		}
	}

	/**
	 * Converts a byte from little endian to big endian
	 * @param i
	 * @return
	 */
	public static int little2big(int i) {
		return ((i & 0xff) << 24) + ((i & 0xff00) << 8) + ((i & 0xff0000) >> 8)
				+ ((i >> 24) & 0xff);
	}

}
