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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationSessionContainer;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationUpdateAdapter;
import de.hybris.platform.sap.productconfig.runtime.ssc.SSCEnginePopertiesInitializer;
import de.hybris.platform.sap.productconfig.runtime.ssc.SolvableConflictAdapter;
import de.hybris.platform.sap.productconfig.runtime.ssc.TextConverter;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigPartOf;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKbProfilesData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigPartOf;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.custdev.projects.fbs.slc.pricing.spc.api.SPCConstants.DataModel;
import com.sap.sce.kbrt.imp.c_ext_cfg_imp;


/**
 * Base class for the default implementation of the {@link ConfigurationProvider}.
 */
public abstract class BaseConfigurationProviderSSCImpl implements ConfigurationProvider
{
	/**
	 *
	 */
	private static final String FIND_KB_TIMER_START_STRING = "findKB";

	protected static final String TYPE_MARA = "MARA";

	private static final String CANNOT_FIND_KNOWLEDGE_BASE_VERSION_FOR_PRODUCT = "Cannot find knowledge base version for product [";

	private static final String MESSAGE_CANNOT_INITIALIZE_PRICING_CONTEXT = "Cannot initialize pricing context";

	private static final Logger LOG = Logger.getLogger(BaseConfigurationProviderSSCImpl.class);

	protected static final String CONFIG = "CONFIG";


	private static final boolean SET_RICH_CONFIG_ID = true;

	private final SSCTimer timer = new SSCTimer();
	private ConfigurationContextAndPricingWrapper contextAndPricingWrapper;
	private ConfigModelFactory configModelFactory;
	private I18NService i18NService;
	private TextConverter textConverter;
	private SolvableConflictAdapter conflictAdapter = null;
	private ConfigurationUpdateAdapter configurationUpdateAdapter = null;
	private ConfigurationSessionContainer configurationSessionContainer = null;
	private SSCEnginePopertiesInitializer enginePropertiesInitializer;

	private static final ThreadLocal<DateFormat> kbDateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

	/**
	 * @param configurationSessionContainer
	 *           configuration session container
	 */
	public void setConfigurationSessionContainer(final ConfigurationSessionContainer configurationSessionContainer)
	{
		this.configurationSessionContainer = configurationSessionContainer;
	}

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		final ConfigModel configModel;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Init new config session for: " + kbKey.toString());
		}

		final String configId = initializeDefaultConfiguration(kbKey);
		configModel = fillConfigModel(configId);
		updateProductCode(configModel, kbKey.getProductCode());

		return configModel;
	}

	@Override
	public boolean updateConfiguration(final ConfigModel configModel)
	{

		final String qualifiedId = configModel.getId();

		final String plainId = retrievePlainConfigId(qualifiedId);
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		return configurationUpdateAdapter.updateConfiguration(configModel, plainId, session);
	}

	protected List<String> getValuesToBeAssigned(final CsticModel csticModel)
	{
		return csticModel.getAssignedValues().stream()//
				.map(CsticValueModel::getName)//
				.collect(Collectors.toList());
	}

	protected List<String> getValuesPreviouslyAssigned(final ICsticData csticData)
	{
		return Arrays.asList(csticData.getCsticValues()).stream()//
				.filter(a -> a.getValueAssigned().booleanValue())//
				.map(ICsticValueData::getValueName)//
				.collect(Collectors.toList());
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String qualifiedConfigId)
	{
		final ConfigModel configModel;

		configModel = fillConfigModel(qualifiedConfigId);

		return configModel;
	}

	/**
	 * @param qualifiedId
	 * @return String external configuration as XML
	 */
	@Override
	public String retrieveExternalConfiguration(final String qualifiedId)
	{
		final IConfigSessionClient session = retrieveConfigSession(qualifiedId);
		final String configId = retrievePlainConfigId(qualifiedId);
		final String configItemInfoXML;
		try
		{
			timer.start("getConfigItemInfoXML");
			configItemInfoXML = session.getConfigItemInfoXML(configId, false);
			timer.stop();
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot retrieve external configuration XML", e);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Created XML configuration :" + configItemInfoXML);
		}

		return configItemInfoXML;
	}

	protected String initializeDefaultConfiguration(final KBKey kbKey)
	{
		IConfigSession session = null;
		String configId = null;

		try
		{
			session = createSession(kbKey);
			configId = createConfig(kbKey, session);
			preparePricingContext(session, configId, kbKey);
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot initialize default configuration", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException(MESSAGE_CANNOT_INITIALIZE_PRICING_CONTEXT, e);
		}

		final String sessionId = session.getSessionId();
		final String qualifiedId = retrieveQualifiedId(sessionId, configId);

		holdConfigSession(qualifiedId, session);

		return qualifiedId;
	}

	protected String createConfig(final KBKey kbKey, final IConfigSession session) throws IpcCommandException
	{
		final String configId;

		final IKbProfilesData[] profiles;

		String kbProfile = null;

		String productId = null;
		String productType = null;

		String kbLogsys = kbKey.getKbLogsys();
		String kbName = kbKey.getKbName();
		String kbVersion = kbKey.getKbVersion();

		if (kbName != null && kbVersion != null && kbLogsys != null)
		{
			timer.start("getProfilesOfKB");
			profiles = session.getProfilesOfKB(kbLogsys, kbName, kbVersion);
			timer.stop();
			kbProfile = profiles[0].getKbProfile();
		}
		else
		{
			productId = kbKey.getProductCode();
			productType = SapproductconfigruntimesscConstants.PRODUCT_TYPE_MARA;
			kbName = null;
			kbVersion = null;
			kbLogsys = null;
		}

		// We need Hashtable here as SSC expects it for the context map
		Hashtable<String, String> context = null;
		if (contextAndPricingWrapper != null)
		{
			context = contextAndPricingWrapper.retrieveConfigurationContext(kbKey);
		}

		final String kbDateStr = getFormattedDate(kbKey);

		// Instantiate product configuration
		timer.start("createConfig");
		configId = session.createConfig(null, productId, productType, kbLogsys, kbName, kbVersion, kbProfile, null, kbDateStr, null,
				null, context, SET_RICH_CONFIG_ID);
		timer.stop();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Created [CONFIG_ID='" + configId + "']");
		}

		return configId;
	}

	protected String getFormattedDate(final KBKey kbKey)
	{
		return kbDateFormat.get().format(kbKey.getDate());
	}

	protected abstract ConfigModel fillConfigModel(final String qualifiedId);

	protected int calculateStaticDomainLength(final IConfigSession session, final String configId, final String instanceId,
			final String csticName) throws IpcCommandException
	{
		int length = 0;
		final OrchestratedInstance oInstance = session.getInstanceLocal(configId, instanceId);
		final OrchestratedCstic oCstic = oInstance.getCstic(csticName);
		final String[] staticDomain = oCstic.getStaticDomain();
		if (staticDomain != null)
		{
			length = staticDomain.length;
		}
		return length;
	}

	/**
	 * @param csticModel
	 * @param containesValueSetByUser
	 */
	protected void adjustCsticAuthor(final CsticModel csticModel, final boolean containesValueSetByUser)
	{
		if (csticModel.getAuthor().equalsIgnoreCase(CsticModel.AUTHOR_USER))
		{
			if (csticModel.getAssignedValues().isEmpty())
			{
				csticModel.setAuthor(CsticModel.AUTHOR_NOAUTHOR);
			}
			else if (!containesValueSetByUser)
			{
				csticModel.setAuthor(CsticModel.AUTHOR_DEFAULT);
			}
		}
	}

	protected String retrieveQualifiedId(final String sessionId, final String configId)
	{
		return sessionId + "@" + configId;
	}

	protected String retrievePlainConfigId(final String qualifiedId)
	{
		return qualifiedId.split("@")[1];
	}

	protected String retrieveSessionId(final String qualifiedId)
	{
		return qualifiedId.split("@")[0];
	}

	protected void holdConfigSession(final String qualifiedId, final IConfigSession configSession)
	{
		configurationSessionContainer.storeConfiguration(qualifiedId, configSession);
	}

	protected IConfigSession retrieveConfigSession(final String qualifiedId)
	{
		return configurationSessionContainer.retrieveConfigSession(qualifiedId);
	}

	/**
	 * @param contextAndPricingWrapper
	 *           configuration context and pricing wrapper
	 */
	@Required
	public void setContextAndPricingWrapper(final ConfigurationContextAndPricingWrapper contextAndPricingWrapper)
	{
		this.contextAndPricingWrapper = contextAndPricingWrapper;
	}

	protected void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException
	{
		if (contextAndPricingWrapper != null)
		{
			contextAndPricingWrapper.preparePricingContext(session, configId, kbKey);
		}
	}

	protected void retrievePrice(final IConfigSession session, final ConfigModel configModel) throws InteractivePricingException
	{
		if (contextAndPricingWrapper != null)
		{
			final String configId = retrievePlainConfigId(configModel.getId());
			contextAndPricingWrapper.processPrice(session, configId, configModel);
		}
	}

	/**
	 * Creates session
	 *
	 * @param kbKey
	 *           Not used in default implementation but provided since subclasses can make use of it.
	 * @return config session
	 * @throws IpcCommandException
	 */
	public IConfigSession createSession(final KBKey kbKey) throws IpcCommandException
	{
		final String runtimEnv = System.getProperty(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT);
		if (runtimEnv == null)
		{
			System.getProperties().put(SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT,
					SapproductconfigruntimesscConstants.RUNTIME_ENVIRONMENT_HYBRIS);
		}

		return createSSCSession();

	}

	protected IConfigSession createSSCSession() throws IpcCommandException
	{

		final IConfigSession session = new ConfigSessionImpl();

		final String language = i18NService.getCurrentLocale().getLanguage().toUpperCase(Locale.ENGLISH);
		final String sessionId = UUID.randomUUID().toString();
		timer.start("createSession");
		session.createSession("true", sessionId, null, false, false, language);
		timer.stop();
		session.setPricingDatamodel(DataModel.CRM);
		session.disableDeltaOutput();


		if (LOG.isDebugEnabled())
		{
			LOG.debug("Create SSC session with ID: " + session.getSessionId());
		}

		return session;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel configModel;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Init new config session for: " + extConfig.getKbKey().getProductCode());
		}

		final String configId = initializeConfigurationFromExternalSource(extConfig);
		configModel = fillConfigModel(configId);
		updateProductCode(configModel, extConfig.getKbKey().getProductCode());

		return configModel;
	}

	protected void updateProductCode(final ConfigModel configModel, final String productCode)
	{
		final KBKey oldKey = configModel.getKbKey();
		configModel.setKbKey(
				new KBKeyImpl(productCode, oldKey.getKbName(), oldKey.getKbLogsys(), oldKey.getKbVersion(), oldKey.getDate()));
	}

	protected String initializeConfigurationFromExternalSource(final Configuration extConfig)
	{
		IConfigSession session = null;
		String configId = null;

		try
		{
			final KBKey kbKey = extConfig.getKbKey();
			session = createSession(kbKey);

			setExternalContext(session, extConfig);

			configId = createConfigFromExternalSource(session, extConfig);

			preparePricingContext(session, configId, kbKey);
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot initialize configuration from external source", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException(MESSAGE_CANNOT_INITIALIZE_PRICING_CONTEXT, e);
		}

		final String sessionId = session.getSessionId();
		final String qualifiedId = retrieveQualifiedId(sessionId, configId);

		holdConfigSession(qualifiedId, session);

		return qualifiedId;
	}

	protected void setExternalContext(final IConfigSession session, final Configuration extConfig) throws IpcCommandException
	{
		// SSC performs a cast to Hashtable internally, therefore we don't use
		// Map/HashMap
		final Hashtable<String, String> context = new Hashtable<>();
		for (final ContextAttribute contextAttribute : extConfig.getContextAttributes())
		{
			context.put(contextAttribute.getName(), contextAttribute.getValue());
		}
		session.setContext(context);
	}

	protected boolean isNotProvided(final String attribute)
	{
		return attribute == null || attribute.isEmpty();
	}

	protected String createConfigFromExternalSource(final IConfigSession session, final Configuration extConfig)
			throws IpcCommandException
	{

		final KBKey kbKey = extConfig.getKbKey();

		String kbName = kbKey.getKbName();
		String kbVersion = kbKey.getKbVersion();
		String kbLogsys = kbKey.getKbLogsys();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// retrieve kb name, version, logsys (if not provided) for desired date
		if ((isNotProvided(kbName) || isNotProvided(kbVersion) || isNotProvided(kbLogsys))
				&& !sdf.format(kbKey.getDate()).equalsIgnoreCase(sdf.format(new Date())))
		{
			timer.start(FIND_KB_TIMER_START_STRING);
			final IKnowledgeBaseData[] kbData = session.findKnowledgeBases(extConfig.getRootInstance().getObjectType(),
					kbKey.getProductCode(), null, sdf.format(kbKey.getDate()), null, null, null, null, true);
			timer.stop();
			if (kbData != null)
			{
				if (kbData.length > 1)
				{
					LOG.warn("Create configuration from external Source: More than one knowledgebase was found for product "
							+ kbKey.getProductCode() + " and date " + kbKey.getDate());
				}
				kbName = kbData[0].getKbName();
				kbVersion = kbData[0].getKbVersion();
				kbLogsys = kbData[0].getKbLogsys();
			}
		}

		final String language = i18NService.getCurrentLocale().getLanguage().toUpperCase(Locale.ENGLISH);

		// prepare config container
		final IConfigContainer configContainer = new ConfigContainer();
		final IConfigHeader configHeader = new ConfigHeader();
		configHeader.setRootId(extConfig.getRootInstance().getId());

		configHeader.setKbName(kbName);
		configHeader.setKbVersion(kbVersion);
		configHeader.setKbLanguage(language);

		configContainer.setConfigHeader(configHeader);
		configContainer.setProductId(kbKey.getProductCode());
		configContainer.setProductLogSys(kbLogsys);
		configContainer.setProductType(extConfig.getRootInstance().getObjectType());

		// parts of
		int i = 0;
		final IConfigPartOf[] partOfs = new ConfigPartOf[extConfig.getPartOfRelations().size()];
		for (final PartOfRelation extPartOf : extConfig.getPartOfRelations())
		{
			partOfs[i] = new ConfigPartOf();
			partOfs[i].setParentId(extPartOf.getParentInstId());
			partOfs[i].setInstId(extPartOf.getInstId());
			partOfs[i].setPosNr(extPartOf.getPosNr());
			partOfs[i].setObjType(extPartOf.getObjectType());
			partOfs[i].setClassType(extPartOf.getClassType());
			partOfs[i].setObjKey(extPartOf.getObjectKey());
			partOfs[i].setAuthor(extPartOf.getAuthor());
			i++;
		}
		configContainer.setArrConfigPartOf(partOfs);

		// instances
		i = 0;
		final InstanceData[] instances = new InstanceData[extConfig.getInstances().size()];
		for (final Instance extInstance : extConfig.getInstances())
		{
			instances[i] = new InstanceData();
			instances[i].setInstId(extInstance.getId());
			instances[i].setObjType(extInstance.getObjectType());
			instances[i].setClassType(extInstance.getClassType());
			instances[i].setObjKey(extInstance.getObjectKey());
			instances[i].setObjTxt(extInstance.getObjectText());
			instances[i].setInstAuthor(extInstance.getAuthor());
			instances[i].setSalesQty(extInstance.getQuantity());
			instances[i].setSalesQtyUnit(extInstance.getQuantityUnit());
			instances[i].setIsInstConsistent(Boolean.valueOf(extInstance.isConsistent()));
			instances[i].setIsInstComplete(Boolean.valueOf(extInstance.isComplete()));
			i++;
		}
		configContainer.setArrInstanceContainer(instances);

		// cstics / values
		final ICsticData[] cstics = prepareCsticDataArrayFromExternalSource(extConfig);
		configContainer.setArrCsticContainer(cstics);

		// recreate configuration
		timer.start("createConfig");
		final String configId = session.recreateConfig(configContainer);
		timer.stop();

		return configId;
	}

	protected ICsticData[] prepareCsticDataArrayFromExternalSource(final Configuration extConfig)
	{
		final Map<String, Map<String, List<CharacteristicValue>>> instCsticValueMap = new HashMap<>();

		for (final CharacteristicValue extCsticValue : extConfig.getCharacteristicValues())
		{
			final String instId = extCsticValue.getInstId();
			final String csticName = extCsticValue.getCharacteristic();

			Map<String, List<CharacteristicValue>> csticValueMap = instCsticValueMap.get(instId);
			if (csticValueMap == null)
			{
				csticValueMap = new HashMap<>();
				instCsticValueMap.put(instId, csticValueMap);
			}

			List<CharacteristicValue> valueList = csticValueMap.get(csticName);
			if (valueList == null)
			{
				valueList = new ArrayList<>();
				csticValueMap.put(csticName, valueList);
			}
			valueList.add(extCsticValue);
		}

		final List<ICsticData> csticList = new ArrayList<>();

		for (final Map.Entry<String, Map<String, List<CharacteristicValue>>> entryInst : instCsticValueMap.entrySet())
		{
			final String instId = entryInst.getKey();
			final Map<String, List<CharacteristicValue>> csticValueMap = entryInst.getValue();
			for (final Map.Entry<String, List<CharacteristicValue>> entryCstic : csticValueMap.entrySet())
			{
				final String csticName = entryCstic.getKey();
				final List<CharacteristicValue> valueList = entryCstic.getValue();

				final ICsticData csticData = new CsticData();
				csticList.add(csticData);
				csticData.setInstanceId(instId);
				final ICsticHeader csticHeader = new CsticHeader();
				csticData.setCsticHeader(csticHeader);
				csticHeader.setCsticName(csticName);

				final ICsticValueData[] csticValues = new ICsticValueData[valueList.size()];
				csticData.setCsticValues(csticValues);

				int i = 0;
				for (final CharacteristicValue value : valueList)
				{
					addCsticValue(csticHeader, csticValues, i, value);
					i++;
				}
			}
		}

		final ICsticData[] cstics = new ICsticData[csticList.size()];
		csticList.toArray(cstics);

		return cstics;
	}

	protected void addCsticValue(final ICsticHeader csticHeader, final ICsticValueData[] csticValues, final int i,
			final CharacteristicValue value)
	{
		if (i == 0)
		{
			csticHeader.setCsticLname(value.getCharacteristicText());
		}
		final ICsticValueData csticValueData = new CsticValueData();
		csticValueData.setValueName(value.getValue());
		csticValueData.setValueLname(value.getValueText());
		csticValueData.setValueAuthor(value.getAuthor());
		csticValues[i] = csticValueData;

	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		final ConfigModel configModel;

		try
		{
			final IConfigSession session = createSession(kbKey);

			if (contextAndPricingWrapper != null)
			{
				final Map<String, String> context = contextAndPricingWrapper.retrieveConfigurationContext(kbKey);
				session.setContext(context);
			}

			timer.start("createFromExternal");
			final String configId = session.recreateConfigFromXml(extConfig);
			timer.stop();
			preparePricingContext(session, configId, kbKey);

			final String qualifiedId = retrieveQualifiedId(session.getSessionId(), configId);

			holdConfigSession(qualifiedId, session);
			configModel = fillConfigModel(qualifiedId);
			updateProductCode(configModel, kbKey.getProductCode());

			return configModel;

		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Could not create configuration from external representation", e);
		}
		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException(MESSAGE_CANNOT_INITIALIZE_PRICING_CONTEXT, e);
		}
	}

	@Override
	public void releaseSession(final String sessionId)
	{
		configurationSessionContainer.releaseSession(sessionId);
	}

	/**
	 *
	 * @return sessionMap
	 */
	public Map<String, IConfigSession> getSessionMap()
	{
		return configurationSessionContainer.getSessionMap();
	}

	/**
	 *
	 * @return contextAndPricingWrapper
	 */
	protected ConfigurationContextAndPricingWrapper getContextAndPricingWrapper()
	{
		return contextAndPricingWrapper;
	}

	/**
	 *
	 * @param configModelFactory
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	/**
	 *
	 * @return configModelFactory
	 */
	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}

	/**
	 *
	 * @return textConverter
	 */
	protected TextConverter getTextConverter()
	{
		return textConverter;
	}

	/**
	 *
	 * @param textConverter
	 */
	@Required
	public void setTextConverter(final TextConverter textConverter)
	{
		this.textConverter = textConverter;
	}

	/**
	 *
	 * @return timer
	 */
	protected SSCTimer getTimer()
	{
		return timer;
	}

	/**
	 * @return the configurationUpdateAdapter
	 */
	public ConfigurationUpdateAdapter getConfigurationUpdateAdapter()
	{
		return configurationUpdateAdapter;
	}

	/**
	 * @param configurationUpdateAdapter
	 *           the configurationUpdateAdapter to set
	 */
	public void setConfigurationUpdateAdapter(final ConfigurationUpdateAdapter configurationUpdateAdapter)
	{
		this.configurationUpdateAdapter = configurationUpdateAdapter;
	}

	/**
	 *
	 * @return i18NService
	 */
	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 *
	 * @param i18nService
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * @return the conflictAdapter
	 */
	public SolvableConflictAdapter getConflictAdapter()
	{
		return conflictAdapter;
	}

	/**
	 * @param conflictAdapter
	 *           the conflictAdapter to set
	 */
	public void setConflictAdapter(final SolvableConflictAdapter conflictAdapter)
	{
		this.conflictAdapter = conflictAdapter;
	}

	@Override
	public boolean isKbForDateExists(final String productCode, final Date kbDate)
	{
		final IKnowledgeBaseData[] kbVersions = findKBs(new KBKeyImpl(productCode, null, null, null, kbDate), true);
		if (kbVersions != null && kbVersions.length >= 1)
		{
			if (kbVersions.length > 1)
			{
				LOG.warn("Get KB by date: More than one knowledgebase version was found for product " + productCode + " and date "
						+ kbDate);
			}
			return true;
		}

		LOG.error(CANNOT_FIND_KNOWLEDGE_BASE_VERSION_FOR_PRODUCT + productCode + "] and Date[" + kbDate + "]");
		return false;
	}



	@Override
	public boolean isKbVersionExists(final KBKey kbKey)
	{
		final IKnowledgeBaseData[] kbVersions = findKBs(kbKey, false);
		return null != findKBInList(kbKey, kbVersions);
	}


	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		final IKnowledgeBaseData[] kbVersions = findKBs(kbKey, true);
		return null != findKBInList(kbKey, kbVersions);
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		final ExternalConfigurationParser parser = new ExternalConfigurationParser();
		parser.parse(extractConfigurationFromXml(externalConfig));
		return new KBKeyImpl(productCode, parser.get_kb_name(), null, parser.get_kb_version());
	}


	protected IKnowledgeBaseData[] findKBs(final KBKey kbKey, final boolean considerDate)
	{
		String kbDateString = null;
		if (considerDate)
		{
			kbDateString = kbDateFormat.get().format(kbKey.getDate());
		}
		final String productCode = kbKey.getProductCode();
		IKnowledgeBaseData[] kbVersions = null;
		try
		{
			timer.start(FIND_KB_TIMER_START_STRING);
			kbVersions = callSSCtoFindKBs(kbKey, kbDateString);
			timer.stop();
		}
		catch (final IpcCommandException e)
		{
			LOG.error(CANNOT_FIND_KNOWLEDGE_BASE_VERSION_FOR_PRODUCT + productCode + "] and Date[" + kbKey.getDate() + "]", e);
		}
		return kbVersions;
	}

	protected IKnowledgeBaseData[] callSSCtoFindKBs(final KBKey kbKey, final String kbDateString) throws IpcCommandException
	{
		final IKnowledgeBaseData[] kbVersions;
		final IConfigSession configSession = createSession(kbKey);
		kbVersions = configSession.findKnowledgeBases(TYPE_MARA, kbKey.getProductCode(), null, kbDateString, null, null, null, null,
				false);
		return kbVersions;
	}

	protected IKnowledgeBaseData findKBInList(final KBKey kbKey, final IKnowledgeBaseData[] allKBVersions)
	{
		if (allKBVersions != null)
		{

			for (final IKnowledgeBaseData kbdEntry : allKBVersions)
			{
				if (kbdEntry.getKbName().equals(kbKey.getKbName()) && kbdEntry.getKbVersion().equals(kbKey.getKbVersion()))
				{
					return kbdEntry;
				}
			}
		}
		return null;
	}

	protected String extractConfigurationFromXml(final String extConfiguration)
	{
		final int index1 = extConfiguration.indexOf("<CONFIGURATION");
		final int index2 = extConfiguration.indexOf("</CONFIGURATION>", index1);
		return index1 == -1 || index2 == -1 ? "" : extConfiguration.substring(index1, index2 + "</CONFIGURATION>".length());
	}

	protected static class ExternalConfigurationParser extends c_ext_cfg_imp
	{
		public void parse(final String extConfiguration)
		{
			cfg_ext_load_data_from_string(extConfiguration);
		}
	}

	protected SSCEnginePopertiesInitializer getEnginePropertiesInitializer()
	{
		return enginePropertiesInitializer;
	}

	/**
	 * @param enginePropertiesInitializer
	 *           engine property initializer
	 */
	public void setEnginePropertiesInitializer(final SSCEnginePopertiesInitializer enginePropertiesInitializer)
	{
		this.enginePropertiesInitializer = enginePropertiesInitializer;
	}
}
