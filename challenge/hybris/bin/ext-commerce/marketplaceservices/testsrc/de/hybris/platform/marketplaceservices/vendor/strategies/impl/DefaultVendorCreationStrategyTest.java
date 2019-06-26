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
package de.hybris.platform.marketplaceservices.vendor.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.strategies.impl.DefaultVendorCreationStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorPromotionRuleEngineContextService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@IntegrationTest
public class DefaultVendorCreationStrategyTest extends ServicelayerTransactionalTest
{

	private static final String VENDOR_CODE = "testvendor";
	private static final String VENDOR_NAME = "Test Vendor";
	private static final String STORE_CODE = "teststore";

	private static final String CATALOG_CODE_SUFFIX = "vendor.product.catalog.code.suffix";
	private static final String CATALOG_NAME_SUFFIX = "vendor.product.catalog.name.suffix";
	private static final String CATALOG_VERSION_NAME = "vendor.product.catalog.version";

	private static final String WAREHOUSE_CODE_SUFFIX = "vendor.warehouse.code.suffix";
	private static final String WAREHOUSE_NAME_SUFFIX = "vendor.warehouse.name.suffix";

	private static final String VENDOR_CATALOG_PERMISSION_GROUP_NAME = "vendor.catalog.permission.group.name";
	private static final String VENDOR_CATALOG_PERMISSION_GROUP_ID = "vendor.catalog.permission.group.id";

	private static final String CATALOG_VERSION_READ_PRINCIPALS = "cmsmanagergroup,customerservicegroup";

	private static final String ENGLISH_ISOCODE = "en";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "defaultVendorCreationStrategy")
	private DefaultVendorCreationStrategy strategy;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "vendorPromotionRuleEngineContextService")
	private VendorPromotionRuleEngineContextService vendorPromotionRuleEngineContextService;

	@Mock
	private LocaleProvider localeProvider;

	private VendorModel vendor;

	private Locale currentLocale;

	@Before
	public void prepare() throws Exception
	{
		currentLocale = new Locale(ENGLISH_ISOCODE);
		localeProvider = Mockito.mock(LocaleProvider.class);
		given(localeProvider.getCurrentDataLocale()).willReturn(currentLocale);

		vendor = new VendorModel();
		getContext(vendor).setLocaleProvider(localeProvider);
		vendor.setCode(VENDOR_CODE);
		vendor.setName(VENDOR_NAME);

		final BaseStoreModel store = modelService.create(BaseStoreModel.class);
		store.setUid(STORE_CODE);
		modelService.save(store);
		importCsv("/marketplaceservices/test/usergroups.impex", "utf-8");

		importCsv("/marketplaceservices/test/testvendor-mappings-test-data.impex", "UTF-8");

		strategy.createVendor(vendor, false);
	}

	@Test
	public void testCreateCatalog()
	{
		final String catalogCode = VENDOR_CODE + getStringForConfiguration(CATALOG_CODE_SUFFIX);
		final String catalogName = VENDOR_CODE + " " + getStringForConfiguration(CATALOG_NAME_SUFFIX);
		final String catalogVersionName = getStringForConfiguration(CATALOG_VERSION_NAME);

		Assert.assertNotNull(vendor.getCatalog());
		Assert.assertEquals(catalogCode, vendor.getCatalog().getId());
		Assert.assertEquals(catalogName, vendor.getCatalog().getName());
		Assert.assertEquals(1, vendor.getCatalog().getCatalogVersions().size());
		Assert.assertEquals(catalogVersionName, vendor.getCatalog().getCatalogVersions().iterator().next().getVersion());
		Assert.assertEquals(vendor.getCatalog().getCatalogVersions().iterator().next(), vendor.getCatalog()
				.getActiveCatalogVersion());
	}

	@Test
	public void testCreateWarehouse()
	{
		final String warehouseCode = VENDOR_CODE + getStringForConfiguration(WAREHOUSE_CODE_SUFFIX);
		final String warehouseName = VENDOR_CODE + " " + getStringForConfiguration(WAREHOUSE_NAME_SUFFIX);

		Assert.assertEquals(1, vendor.getWarehouses().size());
		Assert.assertEquals(warehouseCode, vendor.getWarehouses().iterator().next().getCode());
		Assert.assertEquals(warehouseName, vendor.getWarehouses().iterator().next().getName());
		Assert.assertTrue(baseStoreService.getAllBaseStores().containsAll(vendor.getWarehouses().iterator().next().getBaseStores()));
	}

	@Test
	public void testCreateUserGroups()
	{

		final Collection<UserGroupModel> userGroups = vendor.getUserGroups();
		Assert.assertEquals(1, userGroups.size());
		final UserGroupModel permissionGroup = userService.getUserGroupForUID(VENDOR_CODE
				+ getStringForConfiguration(VENDOR_CATALOG_PERMISSION_GROUP_ID));
		Assert.assertTrue(userGroups.contains(permissionGroup));
		Assert.assertEquals(VENDOR_NAME + " " + getStringForConfiguration(VENDOR_CATALOG_PERMISSION_GROUP_NAME),
				permissionGroup.getLocName());
	}

	@Test
	public void testVendorReadPrincipals()
	{
		final List<PrincipalModel> readPrincipalsForVendor = vendor.getCatalog().getCatalogVersions().iterator().next()
				.getReadPrincipals();
		final List<PrincipalModel> readPrincipalsForConfiguration = new ArrayList<PrincipalModel>();
		final UserModel currentUser = userService.getCurrentUser();
		final UserGroupModel permissionGroup = userService.getUserGroupForUID(VENDOR_CODE
				+ getStringForConfiguration(VENDOR_CATALOG_PERMISSION_GROUP_ID));
		final List<String> configuredUserGroupUidList = getUserGroupUidListForConfiguration();

		for (final String userGroupUid : configuredUserGroupUidList)
		{
			try
			{
				final UserGroupModel usergroup = userService.getUserGroupForUID(userGroupUid);
				Assert.assertTrue(readPrincipalsForVendor.contains(usergroup));
				readPrincipalsForConfiguration.add(usergroup);
			}
			catch (final UnknownIdentifierException e)
			{
			}
		}

		Assert.assertTrue(readPrincipalsForVendor.contains(currentUser));
		Assert.assertTrue(readPrincipalsForVendor.contains(permissionGroup));
		readPrincipalsForConfiguration.add(currentUser);
		readPrincipalsForConfiguration.add(permissionGroup);
		Assert.assertEquals(readPrincipalsForConfiguration.size(), readPrincipalsForVendor.size());
		Assert.assertTrue(readPrincipalsForVendor.containsAll(readPrincipalsForConfiguration));
	}

	protected String getStringForConfiguration(final String key)
	{
		final String value = configurationService.getConfiguration().getString(key);
		return value == null ? "" : value;
	}

	protected List<String> getUserGroupUidListForConfiguration()
	{
		final List<String> configuredUserGroupUidList = new ArrayList();
		configuredUserGroupUidList.addAll(Arrays.asList(CATALOG_VERSION_READ_PRINCIPALS.split(",")));
		return configuredUserGroupUidList;
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}
}
