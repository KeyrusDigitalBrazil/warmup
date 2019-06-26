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
package de.hybris.platform.kymaintegrationservices.services.impl;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import de.hybris.platform.kymaintegrationservices.dto.CertificateRequestPayload;
import de.hybris.platform.kymaintegrationservices.dto.CertificateResponsePayload;
import de.hybris.platform.kymaintegrationservices.dto.KymaApiData;
import de.hybris.platform.kymaintegrationservices.dto.KymaCertificateCreation;
import de.hybris.platform.kymaintegrationservices.dto.KymaSecurityData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.utils.SecurityUtils;
import de.hybris.platform.kymaintegrationservices.services.CertificateService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import sun.security.pkcs10.PKCS10;
import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.x509.X500Name;


/**
 * Kyma specific implementation of {@link CertificateService}.
 */
public class DefaultCertificateService implements CertificateService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCertificateService.class);
	private static final String DEFAULT_EVENTS_DESTINATION_ID = "kyma-events";
	private static final String EVENTS_DESTINATION_ID_KEY = "kymaintegrationservices.kyma_events_consumed_destination_id";
	private static final String DEFAULT_SERVICES_DESTINATION_ID = "kyma-services";
	private static final String SERVICES_DESTINATION_ID_KEY = "kymaintegrationservices.kyma_services_consumed_destination_id";
	private static final String CERTIFICATE_SIGN_ALGORITHM = "apiregistryservices.certificate.sign.algorithm";

	private ModelService modelService;

	private DestinationService<AbstractDestinationModel> destinationService;

	private RestTemplate restTemplate;

	/**
	 * Default kyma implementation
	 * 
	 * @see CertificateService#retrieveCertificate(URI, ConsumedCertificateCredentialModel)
	 * @param certificateUrl
	 *           Url to retrieve client certificate.
	 * @param certificationCredential
	 *           Credential to be updated.
	 * @return updatedModel
	 * @throws CredentialException
	 *            in case when failed to generate PrivateKey, CSR, Certificate.
	 */
	public ConsumedCertificateCredentialModel retrieveCertificate(final URI certificateUrl,
			final ConsumedCertificateCredentialModel certificationCredential) throws CredentialException
	{
		KeyPair keyPair = null;

		try
		{
			final KymaSecurityData kymaSecurityData = getRestTemplate().getForObject(certificateUrl, KymaSecurityData.class);

			final KymaApiData apiData = kymaSecurityData.getApi();
			final KymaCertificateCreation keyData = kymaSecurityData.getCertificate();

			keyPair = generateKeyPair(keyData);

			certificationCredential.setPrivateKey(encodeToBase64(keyPair.getPrivate().getEncoded()));

			final String certificateText = getCertificate(keyPair, keyData, kymaSecurityData.getCsrUrl());

			verifyCredential(certificateText, keyPair, keyData);
			certificationCredential.setCertificateData(certificateText);

			LOG.info("Kyma Integration Certificate retrieved");
			return updateCredentialAndConsumedDestinations(certificationCredential, apiData);
		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format(
					"Failed to retrieve certificate metadata with URL: [{%s}]. Please make sure if token is still valid.",
					certificateUrl.getHost());
			LOG.error(errorMessage, e);
			throw new CredentialException(errorMessage, e);
		}
		catch (final ModelSavingException e)
		{
			final String errorMessage = String.format(
					"Failed to retrieve certificate metadata with URL: [{%s}]. Error while saving certificate and destination URLs",
					certificateUrl.getHost());
			LOG.error(e.getMessage(), e);
			throw new CredentialException(errorMessage, e);
		}
	}

	protected void verifyCredential(final String certificateText, final KeyPair keyPair, final KymaCertificateCreation keyData)
			throws CredentialException
	{
		final byte[] certBytes = DatatypeConverter.parseBase64Binary(certificateText);

		try
		{
			final X509Certificate cert = SecurityUtils.generateCertificateFromDER(certBytes);

			verifyKeyAlgorithm(cert, keyData);
			verifySubject(cert, keyData);
			verifySignatureAlgorithm(cert);
			verifyKeyPairs(cert, keyPair);
		}
		catch (final CertificateException | NoSuchAlgorithmException | SignatureException | InvalidKeyException | IOException e)
		{
			final String errorMessage = String.format("Credential verification is failed. %s", e.getMessage());
			LOG.error(errorMessage, e);
			throw new CredentialException(errorMessage, e);
		}
		finally
		{
			Arrays.fill(certBytes, (byte) 0);
		}
	}

	protected void verifySubject(final X509Certificate cert, final KymaCertificateCreation keyData)
			throws IOException, CertificateException
	{
		final List<String> expectedSubject = Arrays.asList(new X500Name(keyData.getSubject()).getName().split("\\s*,\\s*"));
		final List<String> certificateSubject = Arrays.asList(cert.getSubjectX500Principal().getName().split("\\s*,\\s*"));

		if (!expectedSubject.containsAll(certificateSubject))
		{
			throw new CertificateException("Certificate subject is not valid");
		}
	}

	protected void verifySignatureAlgorithm(final X509Certificate cert) throws CertificateException
	{
		if (!Config.getString(CERTIFICATE_SIGN_ALGORITHM, "SHA256WithRSA").equalsIgnoreCase(cert.getSigAlgName()))
		{
			throw new CertificateException("Certificate signature algorithm is not valid");
		}
	}

	protected void verifyKeyAlgorithm(final X509Certificate cert, final KymaCertificateCreation keyData)
			throws CertificateException
	{
		final String certificateKeyAlgorithm = cert.getPublicKey().getAlgorithm()
				.concat(String.valueOf(((RSAPublicKeyImpl) cert.getPublicKey()).getModulus().bitLength()));

		if (!certificateKeyAlgorithm.equalsIgnoreCase(keyData.getKeyAlgorithm()))
		{
			throw new CertificateException("Public key algorithm is not valid");
		}
	}

	protected void verifyKeyPairs(final X509Certificate cert, final KeyPair keyPair)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateException
	{
		final byte[] randomBytes = new byte[10000];
		final SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);


		final Signature sig = Signature.getInstance(Config.getString(CERTIFICATE_SIGN_ALGORITHM, "SHA256withRSA"));
		sig.initSign(keyPair.getPrivate());
		sig.update(randomBytes);
		final byte[] signature = sig.sign();

		sig.initVerify(cert.getPublicKey());
		sig.update(randomBytes);

		if (!sig.verify(signature))
		{
			throw new CertificateException("Public key and private key don't match");
		}
	}

	protected ConsumedCertificateCredentialModel updateCredentialAndConsumedDestinations(
			final ConsumedCertificateCredentialModel certificationCredential, final KymaApiData apiData) throws CredentialException
	{
		final String eventsDestinationId = Config.getString(EVENTS_DESTINATION_ID_KEY, DEFAULT_EVENTS_DESTINATION_ID);
		final ConsumedDestinationModel eventsDestination = (ConsumedDestinationModel) getDestinationService()
				.getDestinationById(eventsDestinationId);
		if (eventsDestination == null)
		{
			final String errorMessage = String.format("Missing Events Consumed Destination with id : [{%s}]", eventsDestinationId);
			LOG.error(errorMessage);
			throw new CredentialException(errorMessage);
		}

		final String servicesDestinationId = Config.getString(SERVICES_DESTINATION_ID_KEY, DEFAULT_SERVICES_DESTINATION_ID);
		final ConsumedDestinationModel servicesDestination = (ConsumedDestinationModel) getDestinationService()
				.getDestinationById(servicesDestinationId);
		if (servicesDestination == null)
		{
			final String errorMessage = String.format("Missing Services Consumed Destination with id : [{%s}]",
					servicesDestinationId);
			LOG.error(errorMessage);
			throw new CredentialException(errorMessage);
		}

		if (servicesDestination.getCredential().getId().equals(certificationCredential.getId()))
		{
			servicesDestination.setUrl(apiData.getMetadataUrl());
			servicesDestination.setCredential(certificationCredential);
		}

		if (eventsDestination.getCredential().getId().equals(certificationCredential.getId()))
		{
			eventsDestination.setUrl(apiData.getEventsUrl());
			eventsDestination.setCredential(certificationCredential);
		}
		getModelService().saveAll(certificationCredential, servicesDestination, eventsDestination);
		return certificationCredential;
	}

	protected String encodeToBase64(final byte[] toBeEncoded)
	{
		final byte[] lineSeparator = new byte[]
		{ 13, 10 };
		return Base64.getMimeEncoder(64, lineSeparator).encodeToString(toBeEncoded);
	}

	protected String getCertificate(final KeyPair keyPair, final KymaCertificateCreation keyData, final String csrUrlString)
			throws CredentialException
	{
		final URI csrUrl;
		try
		{
			csrUrl = new URI(csrUrlString);
		}
		catch (final URISyntaxException e)
		{
			throw new CredentialException("Invalid CSR url retrieved", e);
		}

		final ResponseEntity<CertificateResponsePayload> response;
		try
		{
			response = getRestTemplate().postForEntity(csrUrl, generateCertificateRequest(keyPair, keyData),
					CertificateResponsePayload.class);

		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format("Failed to retrieve certificate with URL: [{%s}]", csrUrl.getHost());
			LOG.error(errorMessage, e);
			throw new CredentialException(errorMessage, e);
		}
		return response.getBody().getCrt();

	}

	protected CertificateRequestPayload generateCertificateRequest(final KeyPair keyPair, final KymaCertificateCreation keyData)
			throws CredentialException
	{
		final CertificateRequestPayload request = new CertificateRequestPayload();
		final byte[] csr = generateCSR(keyData.getSubject(), keyPair);

		request.setCsr(encodeToBase64(csr));
		Arrays.fill(csr, (byte) 0);
		return request;
	}

	protected byte[] generateCSR(final String rdnAttributes, final KeyPair keypair) throws CredentialException
	{
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try
		{
			final PrintStream printStream = new PrintStream(outStream, false, "UTF-8");
			final X500Name xnames = new X500Name(rdnAttributes, "RFC2253");
			final PKCS10 csr = new PKCS10(keypair.getPublic());
			final Signature sig = Signature.getInstance(Config.getString(CERTIFICATE_SIGN_ALGORITHM, "SHA256WithRSA"));
			sig.initSign(keypair.getPrivate());
			csr.encodeAndSign(xnames, sig);
			csr.print(printStream);
			printStream.flush();
			printStream.close();
		}
		catch (final GeneralSecurityException | IOException e)
		{
			LOG.error("Cannot create certificate", e);
			throw new CredentialException("Cannot create certificate", e);
		}

		return outStream.toByteArray();
	}

	protected KeyPair generateKeyPair(final KymaCertificateCreation keyData) throws CredentialException
	{
		final String[] keyAlg = keyData.getKeyAlgorithm().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

		final KeyPairGenerator keyPairGenerator;
		try
		{
			keyPairGenerator = KeyPairGenerator.getInstance(keyAlg[0]);
			keyPairGenerator.initialize(Integer.parseInt(keyAlg[1]));
			return keyPairGenerator.generateKeyPair();
		}
		catch (final NoSuchAlgorithmException e)
		{
			LOG.error("Cannot generate Key Pair", e);
			throw new CredentialException("Cannot generate Key Pair", e);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	protected RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	@Required
	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}
}
