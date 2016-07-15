package hr.ericsson.pegasus.backend;

import java.util.Arrays;

/**
 * <H1>CustomStr</H1>
 * <HR>
 * Implementation of simple string backed by byte buffer.<BR>
 * <BR>
 * This class is <B>not</B> thread-safe.<BR>
 * <BR>
 * This class has {@link Comparable} interface implemented. 
 * <HR>
 * @author eigorde
 *
 */
public class CustomStr implements Comparable<CustomStr> {

	private byte[] value;
	private int len;
	
	/**
	 * New instance directly built from {@link String} object. String is
	 * converted to upper case and all spaces are removed.
	 * @param str
	 */
	public CustomStr(String str)
	{
		/*
		 * Strip spaces and make it upper case.
		 */
		this.value = str.replaceAll(" ", "").replaceAll("\n", "").toUpperCase().getBytes();
		this.len = value.length;
	}

	/**
	 * New instance which is wrapped around provided byte buffer.<BR>
	 * <B>NOTE:</B> This instance is not backed up by byte buffer.
	 * @param value byte array
	 */
	public CustomStr(byte[] value)
	{
		/*
		 * No strip.
		 */
		this.value = value;
		this.len = value.length;
	}
	
	/**
	 * New instance which makes blind copy of existing byte buffer.
	 * @param str existing instance of {@link CustomStr}
	 */
	public CustomStr(CustomStr str) {
		byte[] source = str.getValue();
		int len = source.length;
		
		this.value = new byte[len];
		this.len = len;
		
		System.arraycopy(source, 0, this.value, 0, len);
	}

	/**
	 * Direct access to byte buffer.
	 * @return byte buffer
	 */
	public byte[] getValue()
	{
		return value;
	}
	
	@Override
	public int compareTo(CustomStr str1) {

		/*
		 * Reverse compare, from last byte to first byte.
		 */
		for (int i = len - 1, j = str1.len - 1; i >= 0 && j >= 0; i--, j--)
		{
			if (value[i] < str1.value[j])
			{
				return -1;
			}
			else if (value[i] > str1.value[j])
			{
				return 1;
			}
		}
		
		if (len > str1.len)
		{
			return 1;
		}
		else if (len < str1.len)
		{
			return -1;
		}
		
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + len;
		result = prime * result + Arrays.hashCode(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomStr other = (CustomStr) obj;
		if (len != other.len)
			return false;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new String(value);
	}

	/**
	 * Find position of character in buffer.
	 * @param chr char to search for
	 * @return position or -1 if not found
	 */
	public int indexOf(char chr) {
		for (int i = 0; i < len; i++)
		{
			if (value[i] == chr)
			{
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Build new instance of this class as a result of substring.
	 * @param start starting position
	 * @return new instance of {@link CustomStr} with it's own backed byte buffer
	 */
	public CustomStr substring(int start) {
		if (start >= 0 && start < len)
		{
			byte[] value = new byte[len - start];
			System.arraycopy(this.value, start, value, 0, value.length);
			return new CustomStr(value);
		}
		return null;
	}

	/**
	 * Return string length. Note that buffer might be longer than
	 * the value returned by this function.
	 * 
	 * @return number of bytes / chars in buffer.
	 */
	public int length() {
		return len;
	}
	
	/**
	 * Get single char / byte at position.
	 * 
	 * @param index position
	 * @return character
	 */
	public byte charAt(int index) {
		return value[index];
	}
	
	/**
	 * Compare end of strings / buffers.
	 * @param str string to compare with
	 * @return <I>false</I> if they do not match, otherwise <I>true</I>
	 */
	public boolean endsWith(CustomStr str) {

		int i1 = this.length();
		int i2 = str.length();
		
		if (i1 < 1 || i2 < 1) {
			return false;
		}
		
		for (i1 = i1 - 1, i2 = i2 - 1; i1 >= 0 && i2 >= 0; i1--, i2-- ) {
			if (str.charAt(i2) != this.charAt(i1)) {
				return false;
			}
		}
			
		return true;
	}

}
