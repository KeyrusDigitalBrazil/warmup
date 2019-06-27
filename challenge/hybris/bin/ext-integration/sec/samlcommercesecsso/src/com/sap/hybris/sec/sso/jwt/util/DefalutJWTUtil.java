/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sec.sso.jwt.util;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonNumber;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.sap.hybris.sec.sso.constants.SamlcommercesecssoConstants;




/**
 *
 */
public final class DefalutJWTUtil
{
	private static final Logger LOG = Logger.getLogger(DefalutJWTUtil.class);

	private UserService userService;
	private ConfigurationService configurationService;

	final String split_pattern = "\\.";
	final String roleAgent = "ROLE_AGENT";

	/**
	 * @param token
	 * @return userDetails
	 */
	public UserDetails getUserDetails(final String token)
	{
		final String username = getUserNameFromJWTToken(token,"user_name");
		final List<SimpleGrantedAuthority> authList = getAuthorities(roleAgent);
		//passing empty password
		final User user = new User(username, "", authList);
		return user;
	}

	private List<SimpleGrantedAuthority> getAuthorities(final String role)
	{
		final List<SimpleGrantedAuthority> authList = new ArrayList<>();

		authList.add(new SimpleGrantedAuthority(roleAgent));
		if (role != null)
		{
			authList.add(new SimpleGrantedAuthority(role));
		}

		return authList;
	}

	public String getUserNameFromJWTToken(final String token,final String fieldName)
	{
		try
		{
			final JsonObject json = getTokenClaimJSON(token);
			return json.getString(fieldName);

		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			LOG.error(e);
			return "";

		}
	}
	
	
	private boolean hasTokenExpired(final String authToken) {
		final JsonNumber exp = getExpTimeFromJWTToken(authToken);
		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
		if(exp!=null && timeSeconds<=exp.longValue())
		{
			return true;
		}
		return false;
	}
	
	private JsonNumber getExpTimeFromJWTToken(final String token)
	{
		String expereField = "exp";
		try
		{
			final JsonObject json = getTokenClaimJSON(token);
			return json.getJsonNumber(expereField);

		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			LOG.error(e);
			return null;

		}
	}
	
	private JsonObject getTokenClaimJSON(final String token) {
		final String[] split = token.split(split_pattern);
		final byte[] decodeBase64 = Base64.decodeBase64(split[1]);
		final String claim = new String(decodeBase64);
		final JsonObject json = Json.createReader(new StringReader(claim)).readObject();
		return json;
	}
	
	//check if the token is valid or not
	public boolean isValidToken(String authToken){
		try {
			return isTokenSignatureValid(authToken) && hasTokenExpired(authToken);
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnsupportedEncodingException e) {
			LOG.error(e);
		}
		return false;
	}

	private boolean isTokenSignatureValid(final String authToken)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
	{
		final String[] tokenParts = authToken.split(split_pattern);
		final String text = tokenParts[0] + "." + tokenParts[1];
		final String signedText = tokenParts[2];

		final String appPublicKeyText = configurationService.getConfiguration()
				.getString(SamlcommercesecssoConstants.JWT_PUBLIC_KEY);
		final String jwtTokenBeginPattern = SamlcommercesecssoConstants.JWT_PUBLIC_KEY_BEGIN;
		final String jwtTokenEndPattern = SamlcommercesecssoConstants.JWT_PUBLIC_KEY_END;

		final PublicKey publicKey = getKey(appPublicKeyText.replace(jwtTokenBeginPattern, "").replace(jwtTokenEndPattern, ""));

		final Signature sig = Signature.getInstance(SamlcommercesecssoConstants.JWT_SIGNATURE_ALGO);
		sig.initVerify(publicKey);
		sig.update(text.getBytes());

		return sig.verify(decodeUrlSafe(signedText.getBytes("UTF-8")));
	}

	private byte[] decodeUrlSafe(final byte[] data)
	{
		final byte[] encode = Arrays.copyOf(data, data.length);
		for (int i = 0; i < encode.length; i++)
		{
			if (encode[i] == '-')
			{
				encode[i] = '+';
			}
			else if (encode[i] == '_')
			{
				encode[i] = '/';
			}
		}
		return java.util.Base64.getDecoder().decode(encode);
	}

	private PublicKey getKey(final String key)
	{
		try
		{
			String utfString = "UTF-8";
			String rsaString = "RSA";
			final byte[] byteKey = decodeUrlSafe(key.getBytes(utfString));
			final X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);			
			final KeyFactory kf = KeyFactory.getInstance(rsaString);

			return kf.generatePublic(X509publicKey);
		}
		catch (final Exception e)
		{
			LOG.error(e);
			return null;
		}
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	

}
