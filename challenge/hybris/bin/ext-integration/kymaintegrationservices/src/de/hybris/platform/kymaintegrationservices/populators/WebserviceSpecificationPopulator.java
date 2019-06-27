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
package de.hybris.platform.kymaintegrationservices.populators;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.kymaintegrationservices.dto.ApiSpecificationData;
import de.hybris.platform.kymaintegrationservices.dto.CredentialsData;
import de.hybris.platform.kymaintegrationservices.dto.OAuthData;
import de.hybris.platform.kymaintegrationservices.dto.ServiceRegistrationData;
import de.hybris.platform.kymaintegrationservices.utils.JsonRetriever;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import static de.hybris.platform.kymaintegrationservices.utils.KymaApiExportHelper.getDestinationId;


/**
 * Kyma specific implementation of Populator that populates Webservice specification DTO.
 * {@link ServiceRegistrationData}
 */
public class WebserviceSpecificationPopulator implements Populator<ExposedDestinationModel, ServiceRegistrationData>
{
	private static final String DEFAULT_PROVIDER = "SAP Hybris";
	private static final String PROVIDER_PROP = "kymaintegrationservices.kyma-specification-provider";

	private ObjectMapper jacksonObjectMapper;

	@Override
	public void populate(final ExposedDestinationModel source, final ServiceRegistrationData target)
	{
		target.setDescription(source.getEndpoint().getDescription());
		target.setName(source.getEndpoint().getName());
		target.setIdentifier(getDestinationId(source));
		target.setProvider(Config.getString(PROVIDER_PROP, DEFAULT_PROVIDER));
		target.setApi(extractApiSpecification(source));
	}

	/**
	 * @param source
	 *           ApiConfiguration
	 * @return ApiSpecification (url, api documentation, credentials)
	 */
	protected ApiSpecificationData extractApiSpecification(final ExposedDestinationModel source)
	{
		final ApiSpecificationData apiSpecification = new ApiSpecificationData();
		apiSpecification.setTargetUrl(source.getUrl());
		final CredentialsData credentials = new CredentialsData();
		credentials.setOauth(extractOAuth(source.getCredential()));
		apiSpecification.setCredentials(credentials);

		final String spec;

		spec = StringUtils.isEmpty(source.getEndpoint().getSpecData()) ? getSpecFromUrl(source.getEndpoint().getSpecUrl())
				: source.getEndpoint().getSpecData();

		try
		{

			apiSpecification.setSpec(getJacksonObjectMapper().readTree(spec));
		}
		catch (final IOException e)
		{
			throw new ConversionException(String.format("Invalid spec for the Exposed Destination with id : %s", source.getId()), e);
		}

		return apiSpecification;
	}

	protected String getSpecFromUrl(final String specUrl)
	{
		final String spec;
		try
		{
			spec = JsonRetriever.urlToJson(specUrl);
		}
		catch (final IOException | IllegalArgumentException e)
		{
			throw new ConversionException(String.format("Invalid spec URL : %s", specUrl), e);
		}
		if (StringUtils.isEmpty(spec))
		{
			throw new ConversionException(String.format("No Exposed Destination spec found by given URL : %s", specUrl));
		}
		return spec;
	}

	/**
	 * @param source
	 *           ApiConfiguration
	 * @return OAuth credentials
	 */
	protected OAuthData extractOAuth(final AbstractCredentialModel source)
	{
		if (source instanceof ExposedOAuthCredentialModel)
		{
			final ExposedOAuthCredentialModel credentialModel = (ExposedOAuthCredentialModel) source;
			if (credentialModel.getOAuthClientDetails() == null)
			{
				throw new ConversionException("ExposedOAuthCredential must have OAuthClientDetails");
			}
			final OAuthData oauth = new OAuthData();
			oauth.setClientId(credentialModel.getOAuthClientDetails().getClientId());
			oauth.setClientSecret(credentialModel.getPassword());
			oauth.setUrl(credentialModel.getOAuthClientDetails().getOAuthUrl());
			return oauth;
		}
		else
		{
			return null;
		}

	}

	protected ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	@Required
	public void setJacksonObjectMapper(final ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}

}
