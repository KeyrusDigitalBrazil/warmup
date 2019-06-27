/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.apiregistryservices.constraints;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.utils.SecurityUtils;
import de.hybris.platform.util.Config;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Validates the certificate and the private key of the given instance of
 * {@link de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel}. Checks structure and expiry
 * date of attributes.
 */
public class ConsumedCertificateCredentialValidator
		implements ConstraintValidator<ConsumedCertificateCredentialValid, ConsumedCertificateCredentialModel>
{
	private static final String CERTIFICATE_RETRIEVAL_DELAY = "apiregistryservices.certificate.retrieval.retry.delay";

	private static final Logger LOG = LoggerFactory.getLogger(ConsumedCertificateCredentialValidator.class);

	@Override
	public void initialize(final ConsumedCertificateCredentialValid consumedCertificateCredentialValid)
	{
		//empty
	}

	@Override
	public boolean isValid(final ConsumedCertificateCredentialModel consumedCertificateCredential,
			final ConstraintValidatorContext constraintValidatorContext)
	{
		byte[] certBytes = new byte[0];
		byte[] keyBytes = new byte[0];
		try
		{
			final String certificateData = consumedCertificateCredential.getCertificateData();
			final String privateKey = consumedCertificateCredential.getPrivateKey();

			if (StringUtils.isNotEmpty(certificateData))
			{
				certBytes = DatatypeConverter.parseBase64Binary(certificateData);
				validateCertificate(SecurityUtils.generateCertificateFromDER(certBytes));
			}

			if (StringUtils.isNotEmpty(privateKey))
			{
				keyBytes = DatatypeConverter.parseBase64Binary(privateKey);
				SecurityUtils.generatePrivateKeyFromDER(keyBytes);
			}
		}
		catch (final CertificateNotYetValidException e)
		{
			LOG.error("Certificate validation failed, please check local time of running system", e);
		}
		catch (final ArrayIndexOutOfBoundsException | CertificateException | CredentialException e)
		{
			LOG.error("Certificate or private key is not valid", e);
			return false;
		}
		finally
		{
			Arrays.fill(certBytes, (byte) 0);
			Arrays.fill(keyBytes, (byte) 0);
		}

		return true;
	}

	protected void validateCertificate(final X509Certificate certificate)
			throws CertificateExpiredException, CertificateNotYetValidException
	{
		try
		{
			certificate.checkValidity();

		}
		catch (final CertificateNotYetValidException e)
		{
			LOG.warn("Retrying Certificate validation", e);
			retryValidation(certificate);
		}
	}

	protected void retryValidation(final X509Certificate certificate)
			throws CertificateExpiredException, CertificateNotYetValidException
	{
		try
		{
			Thread.sleep(Config.getInt(CERTIFICATE_RETRIEVAL_DELAY, 3000));
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}

		certificate.checkValidity();
	}


}
