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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseKeyComparator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValueInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CPSConfigurationParentReferenceStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.Config;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.util.URLEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Default implementation of {@link CharonFacade}. This bean has prototype scope
 */
public class CharonFacadeImpl implements CharonFacade
{
	private static final Logger LOG = Logger.getLogger(CharonFacadeImpl.class);
	private static final String AUTO_CLEANUP_PROP_KEY = "sapproductconfigruntimecps.autocleanup";
	protected static final String ETAG_NOT_FOUND = "No eTag found for config ";
	protected static final String AUTO_CLEAN_UP_FALSE = "false";
	private ConfigurationClientBase clientSetExternally = null;
	private CPSResponseAttributeStrategy responseStrategy;
	private RequestErrorHandler requestErrorHandler;
	private ObjectMapper objectMapper;
	private final Scheduler scheduler = Schedulers.io();
	private YaasServiceFactory yaasServiceFactory;
	private CPSContextSupplier contextSupplier;
	private final CPSTimer timer = new CPSTimer();
	private CPSCache cache;
	private I18NService i18NService;
	private CPSConfigurationParentReferenceStrategy configurationParentReferenceStrategy;
	private CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;
	private KnowledgebaseKeyComparator kbKeyComparator;

	/**
	 * @return the commerceExternalConfigurationStrategy
	 */
	protected CommerceExternalConfigurationStrategy getCommerceExternalConfigurationStrategy()
	{
		return commerceExternalConfigurationStrategy;
	}

	@Override
	public CPSConfiguration createDefaultConfiguration(final KBKey kbKey)
	{
		try
		{
			final CPSCreateConfigInput cloudEngineConfigurationRequest = assembleCreateDefaultConfigurationRequest(kbKey);

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create default configuration): ", cloudEngineConfigurationRequest);
			}
			timer.start("createDefaultConfiguration");
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient().createDefaultConfiguration(
					cloudEngineConfigurationRequest, getI18NService().getCurrentLocale().getLanguage(), getAutoCleanUpFlag());
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			getConfigurationParentReferenceStrategy().addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateDefaultConfigurationError(ex);
		}
	}

	protected CPSCreateConfigInput assembleCreateDefaultConfigurationRequest(final KBKey kbKey)
	{
		final CPSCreateConfigInput cloudEngineConfigurationRequest = new CPSCreateConfigInput();
		cloudEngineConfigurationRequest.setProductKey(kbKey.getProductCode());

		final List<CPSContextInfo> context = getContextSupplier().retrieveContext(kbKey.getProductCode());
		cloudEngineConfigurationRequest.setContext(context);

		return cloudEngineConfigurationRequest;
	}

	@Override
	public String getExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final CPSExternalConfiguration externalConfigStructured = placeGetExternalConfigurationRequest(configId);

			final CPSCommerceExternalConfiguration commerceExternalConfiguration = getCommerceExternalConfigurationStrategy()
					.createCommerceFormatFromCPSRepresentation(externalConfigStructured);

			final String extConfig = getObjectMapper().writeValueAsString(commerceExternalConfiguration);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Output for REST call (get ext configuration): " + extConfig);
			}
			return extConfig;

		}
		catch (final JsonProcessingException e)
		{
			throw new IllegalStateException("External configuration from client cannot be parsed to string", e);
		}
	}

	protected CPSExternalConfiguration placeGetExternalConfigurationRequest(final String configId)
			throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);
			timer.start("getExternalConfiguration/" + configId);
			CPSExternalConfiguration extConfig = null;
			if (cookiesAsString != null)
			{
				extConfig = getClient().getExternalConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1))
						.subscribeOn(getScheduler()).toBlocking().first();
			}
			else
			{
				extConfig = getClient().getExternalConfiguration(configId).subscribeOn(getScheduler()).toBlocking().first();
			}
			timer.stop();
			return extConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetExternalConfigurationError(ex, configId);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex, configId);
			return null;
		}
	}

	/**
	 * @deprecated since 18.11.0 - use {@link CharonFacade#createConfigurationFromExternal(String, String)} instead
	 */
	@Deprecated
	@Override
	public CPSConfiguration createConfigurationFromExternal(final String externalConfiguration)
	{
		return createConfigurationFromExternal(externalConfiguration, null);
	}

	/**
	 * @deprecated since 18.11.0 - use
	 *             {@link CharonFacade#createConfigurationFromExternal(CPSExternalConfiguration, String)} instead
	 */
	@Deprecated
	@Override
	public CPSConfiguration createConfigurationFromExternal(final CPSExternalConfiguration externalConfigStructured)
	{
		return createConfigurationFromExternal(externalConfigStructured, null);
	}

	@Override
	public CPSConfiguration createConfigurationFromExternal(final String externalConfiguration, final String contextProduct)
	{
		final CPSExternalConfiguration externalConfigStructured = convertFromStringToStructured(externalConfiguration);
		return createConfigurationFromExternal(externalConfigStructured, contextProduct);
	}

	@Override
	public CPSConfiguration createConfigurationFromExternal(final CPSExternalConfiguration externalConfigStructured,
			final String contextProduct)
	{
		String productCodeForContext;
		if (StringUtils.isNotEmpty(contextProduct))
		{
			productCodeForContext = contextProduct;
		}
		else
		{
			productCodeForContext = externalConfigStructured.getRootItem().getObjectKey().getId();
		}

		final List<CPSContextInfo> context = getContextSupplier().retrieveContext(productCodeForContext);
		externalConfigStructured.setContext(context);

		try
		{
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create form external configuration): ", externalConfigStructured);
			}
			timer.start("createConfigurationFromExternal");
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient()
					.createRuntimeConfigurationFromExternal(externalConfigStructured, getAutoCleanUpFlag());
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			getConfigurationParentReferenceStrategy().addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateRuntimeConfigurationFromExternalError(ex);
		}
	}

	protected String getAutoCleanUpFlag()
	{
		String autoCleanUp = AUTO_CLEAN_UP_FALSE;
		if (Registry.hasCurrentTenant())
		{
			autoCleanUp = Config.getString(AUTO_CLEANUP_PROP_KEY, AUTO_CLEAN_UP_FALSE);
		}
		return autoCleanUp;
	}

	protected CPSExternalConfiguration convertFromStringToStructured(final String externalConfiguration)
	{
		CPSCommerceExternalConfiguration externalConfigStructuredCommerceFormat;
		try
		{
			externalConfigStructuredCommerceFormat = getObjectMapper().readValue(externalConfiguration,
					CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing from JSON failed", e);

		}
		return getCommerceExternalConfigurationStrategy()
				.extractCPSFormatFromCommerceRepresentation(externalConfigStructuredCommerceFormat);
	}

	@Override
	public void releaseSession(final String configId, final String version)
	{
		final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);

		if (version == null)
		{
			throw new IllegalStateException(ETAG_NOT_FOUND + configId);
		}
		try
		{
			timer.start("releaseSession/" + configId);
			if (cookiesAsString != null)
			{
				getClient().deleteConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1), version)
						.subscribeOn(getScheduler()).toBlocking().first();
			}
			else
			{
				getClient().deleteConfiguration(configId, version).subscribeOn(getScheduler()).toBlocking().first();
			}
			timer.stop();
		}
		catch (final HttpException ex)
		{
			getRequestErrorHandler().processDeleteConfigurationError(ex);
		}
		finally
		{
			getCache().removeCookies(configId);
			getCache().removeConfiguration(configId);
		}
	}

	@Override
	public String updateConfiguration(final CPSConfiguration configuration) throws ConfigurationEngineException
	{
		final CPSItem rootItem = configuration.getRootItem();
		if (rootItem == null)
		{
			throw new IllegalStateException("Root item not available");
		}

		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();
		final MutableBoolean updateWasDone = new MutableBoolean(false);
		final String initialVersion = configuration.getETag();
		return updateCPSCharacteristic(updateWasDone, rootItem, cfgId, initialVersion, itemId);
	}

	protected String updateCPSCharacteristic(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion, final String itemId) throws ConfigurationEngineException
	{

		String updatedVersion = handleUpdateOwnCharacteristics(updateWasDone, item, cfgId, currentVersion, itemId);
		updatedVersion = handleUpdateSubItems(updateWasDone, item, cfgId, updatedVersion);
		return updatedVersion;
	}

	protected String handleUpdateSubItems(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion) throws ConfigurationEngineException
	{
		final List<CPSItem> subItems = item.getSubItems();
		String updatedVersion = currentVersion;
		if (CollectionUtils.isNotEmpty(subItems))
		{
			for (final CPSItem subItem : subItems)
			{
				updatedVersion = updateCPSCharacteristic(updateWasDone, subItem, cfgId, updatedVersion, subItem.getId());
			}
		}
		return updatedVersion;
	}

	protected String handleUpdateOwnCharacteristics(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion, final String itemId) throws ConfigurationEngineException
	{
		final List<CPSCharacteristic> characteristics = item.getCharacteristics();

		String updatedVersion = currentVersion;
		for (final CPSCharacteristic characteristic : characteristics)
		{
			final CPSCharacteristicInput characteristicInput = createCharacteristicInput(characteristic);
			//multiple updates: We cannot always prevent this as in some environments, the above layers need to send multiple updates
			//(e.g. if unconstrained cstics are involved).
			//Still we raise a log warning as this can cause undesired conflict situations
			if (updateWasDone.isTrue())
			{
				LOG.warn("Multiple updates detected in one request, characteristic involved: " + characteristic.getId());
			}
			updatedVersion = updateConfiguration(cfgId, updatedVersion, itemId, characteristic.getId(), characteristicInput);
			updateWasDone.setValue(true);
		}
		return updatedVersion;
	}


	protected CPSCharacteristicInput createCharacteristicInput(final CPSCharacteristic characteristic)
	{
		final CPSCharacteristicInput characteristicInput = new CPSCharacteristicInput();
		characteristicInput.setValues(new ArrayList<>());
		for (final CPSValue value : characteristic.getValues())
		{
			final CPSValueInput valueInput = new CPSValueInput();
			valueInput.setValue(value.getValue());
			valueInput.setSelected(value.isSelected());
			characteristicInput.getValues().add(valueInput);
		}
		return characteristicInput;
	}

	protected String updateConfiguration(final String cfgId, final String version, final String itemId, final String csticId,
			final CPSCharacteristicInput changes) throws ConfigurationEngineException
	{
		final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(cfgId);
		try
		{
			final String csticIdEncoded = encode(csticId);
			if (version == null)
			{
				throw new IllegalStateException(ETAG_NOT_FOUND + cfgId);
			}

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (update configuration): ", changes);
				if (cookiesAsString != null)
				{
					LOG.debug("Cookies as input " + cookiesAsString);
				}
				else
				{
					LOG.debug("No cookies as input ");
				}
				LOG.debug("ETag as input: " + version);
			}

			timer.start("updateConfiguration/" + cfgId);
			Observable<RawResponse> rawResponse = null;
			if (cookiesAsString != null)
			{
				rawResponse = getClient().updateConfiguration(changes, cfgId, itemId, csticIdEncoded, cookiesAsString.get(0),
						cookiesAsString.get(1), version);
			}
			else
			{
				rawResponse = getClient().updateConfiguration(changes, cfgId, itemId, csticIdEncoded, version);
			}
			timer.stop();
			final RawResponse response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
			return getResponseStrategy().retrieveETagAndSaveResponseAttributes(response, cfgId);
		}
		catch (final HttpException e)
		{
			getRequestErrorHandler().processUpdateConfigurationError(e, cfgId);
		}
		catch (final RuntimeException e)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(e, cfgId);
		}
		finally
		{
			getCache().removeConfiguration(cfgId);
		}
		return null;
	}

	protected String encode(final String requestParam)
	{
		if (requestParam == null)
		{
			return null;
		}
		final URLEncoder encoder = new URLEncoder();

		return encoder.encode(requestParam, StandardCharsets.UTF_8);
	}

	@Override
	public CPSConfiguration getConfiguration(final String configId) throws ConfigurationEngineException
	{
		return retrieveConfiguration(configId);
	}

	protected CPSConfiguration retrieveConfiguration(final String configId) throws ConfigurationEngineException
	{
		final CPSConfiguration cachedConfiguration = getCache().getConfiguration(configId);
		if (cachedConfiguration != null)
		{
			return cachedConfiguration;
		}
		return retrieveConfigurationFromClient(configId);
	}

	protected CPSConfiguration retrieveConfigurationFromClient(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);
			timer.start("getConfiguration/" + configId);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("GET reequested for config id " + configId);
				LOG.debug("Cookies as input for request: " + cookiesAsString);
			}
			Observable<RawResponse<CPSConfiguration>> rawResponse = null;
			if (CollectionUtils.isNotEmpty(cookiesAsString))
			{
				rawResponse = getClient().getConfiguration(configId, getI18NService().getCurrentLocale().getLanguage(),
						cookiesAsString.get(0), cookiesAsString.get(1));
			}
			else
			{
				rawResponse = getClient().getConfiguration(configId, getI18NService().getCurrentLocale().getLanguage());
			}
			final CPSConfiguration config = retrieveConfigurationAndSaveResponseAttributes(rawResponse);

			timer.stop();
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (get configuration): ", config);
			}
			getConfigurationParentReferenceStrategy().addParentReferences(config);
			return config;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetConfigurationError(ex, configId);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex, configId);
			return null;
		}
	}

	protected KnowledgebaseKeyComparator getKbKeyComparator()
	{
		return kbKeyComparator;
	}

	@Required
	public void setKbKeyComparator(final KnowledgebaseKeyComparator kbKeyComparator)
	{
		this.kbKeyComparator = kbKeyComparator;
	}


	/**
	 * @param commerceExternalConfigurationStrategy
	 */
	public void setCommerceExternalConfigurationStrategy(
			final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy)
	{
		this.commerceExternalConfigurationStrategy = commerceExternalConfigurationStrategy;

	}

	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}

	/**
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	@Required
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}

	protected ConfigurationClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			return getYaasServiceFactory().lookupService(ConfigurationClient.class);
		}
	}

	/**
	 * Sets charon client from outside. Only used in test environments
	 *
	 * @param newClient
	 *           Charon client representing REST calls for product configuration.
	 */
	public void setClient(final ConfigurationClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	protected YaasServiceFactory getYaasServiceFactory()
	{
		return yaasServiceFactory;
	}

	/**
	 * @param yaasServiceFactory
	 *           the YaasServiceFactory to set
	 */
	@Required
	public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory)
	{
		this.yaasServiceFactory = yaasServiceFactory;
	}

	/**
	 * @return the contextSupplier
	 */
	protected CPSContextSupplier getContextSupplier()
	{
		return contextSupplier;
	}

	/**
	 * @param contextSupplier
	 *           the contextSupplier to set
	 */
	public void setContextSupplier(final CPSContextSupplier contextSupplier)
	{
		this.contextSupplier = contextSupplier;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected CPSCache getCache()
	{
		return cache;
	}

	@Required
	public void setCache(final CPSCache cache)
	{
		this.cache = cache;
	}

	protected void traceJsonRequestBody(final String prefix, final Object obj)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(obj));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}

	protected CPSConfiguration retrieveConfigurationAndSaveResponseAttributes(
			final Observable<RawResponse<CPSConfiguration>> rawResponse)
	{
		//Stateful calls are preferred (otherwise CPS needs to load its data from DB),
		//and this is facilitated via cookies. These cookies can be extracted from the RawResponse.
		final RawResponse<CPSConfiguration> response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
		final CPSConfiguration responseValue = response.content().subscribeOn(getScheduler()).toBlocking().first();
		final String configId = responseValue.getId();
		final String eTag = getResponseStrategy().retrieveETagAndSaveResponseAttributes(response, configId);
		responseValue.setETag(eTag);

		LOG.info("Retrieved configuration with id: " + configId);
		if (LOG.isDebugEnabled())
		{
			traceJsonRequestBody("Output for REST call (create default/from external configuration): ", responseValue);
		}
		getCache().setConfiguration(responseValue.getId(), responseValue);

		//check for an outdated knowledgebase build number
		getKbKeyComparator().retrieveKnowledgebaseBuildSyncStatus(responseValue);

		return responseValue;
	}

	protected CPSResponseAttributeStrategy getResponseStrategy()
	{
		return responseStrategy;
	}

	@Required
	public void setResponseStrategy(final CPSResponseAttributeStrategy responseStrategy)
	{
		this.responseStrategy = responseStrategy;
	}

	protected CPSConfigurationParentReferenceStrategy getConfigurationParentReferenceStrategy()
	{
		return configurationParentReferenceStrategy;
	}

	@Required
	public void setConfigurationParentReferenceStrategy(
			final CPSConfigurationParentReferenceStrategy configurationParentReferenceStrategy)
	{
		this.configurationParentReferenceStrategy = configurationParentReferenceStrategy;
	}

}
