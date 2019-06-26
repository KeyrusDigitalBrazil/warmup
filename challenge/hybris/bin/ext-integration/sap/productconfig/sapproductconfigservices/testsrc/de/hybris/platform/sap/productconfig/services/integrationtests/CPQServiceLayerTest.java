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
package de.hybris.platform.sap.productconfig.services.integrationtests;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.SwitchableProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.impl.ServiceConfigurationValueHelperImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.auth.AuthenticationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;


@SuppressWarnings("javadoc")
public abstract class CPQServiceLayerTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(CPQServiceLayerTest.class);

	protected static final String PRODUCT_CODE_YSAP_NOCFG = "YSAP_NOCFG";
	protected static final String PRODUCT_CODE_CPQ_HOME_THEATER = "CPQ_HOME_THEATER";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP = "CPQ_LAPTOP";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP_MUSIC = "CPQ_LAPTOP_MUSIC";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP_MUZAC = "CPQ_LAPTOP_MUZAC";
	protected static final String PRODUCT_CODE_YSAP_SIMPLE_POC = "YSAP_SIMPLE_POC";
	protected static final String PRODUCT_CODE_CONF_PIPE = "CONF_PIPE";
	protected static final String TEST_CONFIGURE_SITE = "testConfigureSite";

	@Resource(name = "cartService")
	protected CartService cartService;
	@Resource(name = "commerceCartService")
	protected CommerceCartService commerceCartService;
	@Resource(name = "productService")
	protected ProductService productService;
	@Resource(name = "sapProductConfigProductUtil")
	protected ConfigurationProductUtil configurationProductUtil;
	@Resource(name = "catalogVersionService")
	protected CatalogVersionService catalogVersionService;
	@Resource(name = "modelService")
	protected ModelService modelService;
	@Resource(name = "userService")
	protected UserService realUserService;
	@Resource(name = "sapProductConfigProviderFactory")
	protected SwitchableProviderFactory providerFactory;
	@Resource(name = "sapProductConfigConfigurationService")
	protected ProductConfigurationService cpqService;
	@Resource(name = "sapProductConfigConfigurationLifecycleStrategy")
	protected ConfigurationLifecycleStrategy configurationLifecycleStrategy;
	@Resource(name = "sapProductConfigAbstractOrderEntryLinkStrategy")
	protected ConfigurationAbstractOrderEntryLinkStrategy cpqAbstractOrderEntryLinkStrategy;
	@Resource(name = "authenticationService")
	protected AuthenticationService authenticationService;
	@Resource(name = "sapProductConfigProductConfigurationCacheAccessService")
	protected ProductConfigurationCacheAccessService productConfigurationCacheAccessService;

	// hybris facades
	@Resource(name = "i18NService")
	protected I18NService i18NService;
	@Resource(name = "flexibleSearchService")
	protected FlexibleSearchService flexibleSearchService;
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	@Resource(name = "baseSiteService")
	protected BaseSiteService baseSiteService;
	@Resource(name = "sapProductConfigDefaultConfigurationService")
	protected ProductConfigurationService cpqServiceNoRules;

	protected CustomerModel customerModel;
	protected ServiceConfigurationValueHelperImpl serviceConfigValueHelper = new ServiceConfigurationValueHelperImpl();

	protected static final KBKey KB_CPQ_HOME_THEATER;
	protected static final KBKey KB_CPQ_LAPTOP;
	protected static final KBKey KB_Y_SAP_SIMPLE_POC;
	protected static final KBKey KB_CPQ_LAPTOP_MUSIC;

	static
	{
		KB_CPQ_HOME_THEATER = new KBKeyImpl(PRODUCT_CODE_CPQ_HOME_THEATER);
		KB_CPQ_LAPTOP = new KBKeyImpl(PRODUCT_CODE_CPQ_LAPTOP);
		KB_Y_SAP_SIMPLE_POC = new KBKeyImpl(PRODUCT_CODE_YSAP_SIMPLE_POC);
		KB_CPQ_LAPTOP_MUSIC = new KBKeyImpl(PRODUCT_CODE_CPQ_LAPTOP_MUSIC);
	}

	protected void importCsvIfExist(final String csvFile, final String encoding) throws Exception
	{
		final InputStream inStream = CPQServiceLayerTest.class.getResourceAsStream(csvFile);

		if (inStream != null)
		{
			inStream.close();
			importCsv(csvFile, encoding);
		}
		else
		{
			LOG.info("file not found: " + csvFile);
		}
	}

	public static void createCoreData() throws Exception
	{
		// copied from ServicelayerTestLogic.createCoredata()
		// we only need this, but do not want to import the impex file (to save testruntime)
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		//ServicelayerTestLogic.createCoreData();
	}

	protected void prepareCPQData() throws Exception
	{
		final long startTime = System.currentTimeMillis();
		LOG.info("CREATING CORE DATA FOR CPQ-TEST....");

		createCoreData();
		importCPQTestData();

		// normally the base site is derived from the request URL via pattern macthing - in integration test mode we set it active manually
		baseSiteService.setCurrentBaseSite(TEST_CONFIGURE_SITE, false);

		//product catalog needs be set
		makeProductCatalogVersionAvailableInSession();

		// during ECP pipeline build all extensions are active, which leads to an inconsistent setup
		ensureNoRulesCPSWithDefaultLifecyclce();

		// default in hybris is DE/EUR
		LOG.info("Tests running with locale: " + i18NService.getCurrentLocale().toString());
		final CurrencyModel cur = i18NService.getCurrentCurrency();
		LOG.info("Tests running with Currency: isoCode=" + cur.getIsocode() + "; sapCode=" + cur.getSapCode());

		final long duration = System.currentTimeMillis() - startTime;
		LOG.info("CPQ DATA READY FOR TEST! (" + duration + "ms)");
	}


	protected void ensureNoRulesCPSWithDefaultLifecyclce() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		if (isDefaultLifecycle() && "ProductConfigurationRuleAwareServiceImpl".equals(cpqService.getClass().getSimpleName()))
		{
			// no compile dependency to rules extension possible/desired, hence we inject via reflection.
			final Class rulesResultUtil = Class
					.forName("de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil");
			final Object defaultRulesUtil = Class
					.forName("de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRulesResultUtilImpl").newInstance();
			cpqService.getClass().getMethod("setRulesResultUtil", rulesResultUtil).invoke(cpqService, defaultRulesUtil);
		}
	}

	protected void makeProductCatalogVersionAvailableInSession()
	{
		final CatalogVersionModel testProductCatalog = catalogVersionService.getAllCatalogVersions().stream()
				.filter(cv -> cv.getActive() && "testConfigureCatalog".equals(cv.getCatalog().getId())).collect(Collectors.toList())
				.get(0);
		catalogVersionService.addSessionCatalogVersion(testProductCatalog);
	}

	protected void useCurrency_USD()
	{
		// force english locale for tests
		i18NService.setCurrentLocale(Locale.ENGLISH);
		// force USD currency for tests
		// do no inject via i18nService, as this causes a class cast exception in jalo layer in some scenarios
		// for example, Hybris price factories read currency directly from jalo session ==> class cast exception Currency <-> CurrencyModel
		// instead inject in jalo layer directly. as i18nservice will do the conversion from Currency to CurrencyModel on the fly
		final Currency usd = C2LManager.getInstance().getCurrencyByIsoCode("USD");
		JaloSession.getCurrentSession().getSessionContext().setCurrency(usd);
	}

	protected void useLocale_EN()
	{
		// force english locale for tests
		i18NService.setCurrentLocale(Locale.ENGLISH);
	}

	protected void importCPQUserData() throws ImpExException
	{
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_userTestData.impex", "utf-8");
		customerModel = getFromPersistence("Select {pk} from {Customer} where {uid}='cpq01@sap.com'");
	}

	protected void importCPQTestData() throws ImpExException, Exception
	{
		LOG.info("CREATING CPQ DATA FOR CPQ-TEST....");
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_testData.impex", "utf-8");
	}

	@Before
	public void initProviders()
	{
		ensureMockProvider();
	}

	public void ensureMockProvider()
	{
		//explicitly references sapProductConfigDefaultPricingParameters and sapProductConfigDefaultProductCsticAndValueParameterProviderMock
		providerFactory.switchProviderFactory("sapProductConfigMockProviderFactory");
	}

	public void ensureSSCProvider()
	{
		providerFactory.switchProviderFactory("sapProductConfigSSCProviderFactory");
	}

	public void ensureCPSProvider()
	{
		//sapProductConfigCPSProviderFactory only references the alias for an analytics provider and may contain the default implementation
		providerFactory.switchProviderFactory("sapProductConfigCPSPCIProviderFactory");
	}

	/**
	 * Reads a model from persistence via flexible search
	 *
	 * @param flexibleSearchSelect
	 * @return Model
	 */
	protected <T> T getFromPersistence(final String flexibleSearchSelect)
	{
		LOG.info("ExcutingQuery: " + flexibleSearchSelect);
		final SearchResult<Object> searchResult = flexibleSearchService.search(flexibleSearchSelect);
		Assert.assertEquals("FlexSearch Query - " + flexibleSearchSelect + ":", 1, searchResult.getTotalCount());
		return (T) searchResult.getResult().get(0);
	}

	@After
	public void tearDown()
	{
		clearProductConfigurationItems();
	}

	protected void clearProductConfigurationItems()
	{
		final String selectAllProductConfigItems = "Select {pk} from {ProductConfiguration}";
		final SearchResult<ProductConfigurationModel> searchResult = flexibleSearchService.search(selectAllProductConfigItems);

		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			LOG.info(searchResult.getTotalCount() + " product configuration entries found");
			for (final ProductConfigurationModel config : searchResult.getResult())
			{
				configurationLifecycleStrategy.releaseSession(config.getConfigurationId());
				modelService.remove(config);
			}
		}
	}

	protected String getLifecycleBeanName() throws AssertionError
	{
		String className = cpqAbstractOrderEntryLinkStrategy.getClass().getSimpleName();
		if (cpqAbstractOrderEntryLinkStrategy.getClass().getSimpleName().startsWith("Dynamic"))
		{
			try
			{
				className = cpqAbstractOrderEntryLinkStrategy.getClass().getMethod("getDelegateBean")
						.invoke(cpqAbstractOrderEntryLinkStrategy).getClass().getSimpleName();
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException ex)
			{
				throw new AssertionError("failed to get delegate bean name from dynamic lifecycle bean", ex);
			}
		}
		return className;
	}

	public boolean isPersistentLifecycle() throws AssertionError
	{
		return getLifecycleBeanName().startsWith("Persistence");
	}

	public boolean isDefaultLifecycle() throws AssertionError
	{
		return getLifecycleBeanName().startsWith("Default");
	}


	protected LifecycleStrategiesTestChecker selectStrategyTestChecker()
	{
		String beanName = null;
		if (isPersistentLifecycle())
		{
			beanName = "sapProductConfigPersistentLifecycleTestChecker";
		}
		else if (isDefaultLifecycle())
		{
			beanName = "sapProductConfigDefaultLifecycleTestChecker";
		}
		else
		{
			throw new AssertionError("Unknown lifecyclce implementation: " + getLifecycleBeanName());
		}
		LOG.info("Running " + this.getClass().getSimpleName() + " with checker " + beanName);
		return (LifecycleStrategiesTestChecker) Registry.getApplicationContext().getBean(beanName);
	}


}
