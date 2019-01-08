package com.eaiggi.api.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.util.StringUtils;

import com.eaiggi.api.exception.BaseException;

public class CommonUtils {
	public static boolean isValidFile(String uri) throws BaseException {
		if (StringUtils.isEmpty(uri)) {
			return false;
		}
		Path path = Paths.get(uri);
		if (!Files.exists(path) || !Files.isReadable(path)) {
			return false;
		}
		return true;
	}
}
