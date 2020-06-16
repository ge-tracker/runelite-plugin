package com.getracker.cerebro;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.Getter;

public class CreateSessionResponse
{
	@Getter
	private String sessionId;
}
