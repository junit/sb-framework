package org.chinasb.common.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

/**
 * MD5摘要工具类
 * @author zhujuan
 *
 */
public class ChecksumUtility {
	private static final Logger logger = LoggerFactory
			.getLogger(ChecksumUtility.class.getName());

	/*
	 * Calculate checksum of a File using MD5 algorithm
	 */
	public static String checkSum(File file) {
		String checksum = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			while ((numOfBytesRead = fis.read(buffer)) > 0) {
				md.update(buffer, 0, numOfBytesRead);
			}
			return HexDumpUtility.toHexString(md.digest());
		} catch (FileNotFoundException ex) {
			logger.error(null, ex);
		} catch (IOException ex) {
			logger.error(null, ex);
		} catch (NoSuchAlgorithmException ex) {
			logger.error(null, ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				logger.error(null, ex);
			}
		}
		return checksum;
	}

	/*
	 * Calculate checksum of a String using MD5 algorithm
	 */
	public static String checkSum(String target) {
		return DigestUtils.md5DigestAsHex(target.getBytes());
	}
}
