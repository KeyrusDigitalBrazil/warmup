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
package de.hybris.platform.marketplaceservices.vendor.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;



/**
 *
 */
@IntegrationTest
public class DefaultVendorDaoTest extends ServicelayerTransactionalTest
{
	private static final String VENDOR1_CODE = "vendor1";
	private static final String VENDOR2_CODE = "vendor2";
	private static final String VENDOR3_CODE = "vendor3";
	private static final String PRODUCT_CODE = "00000101";
	private static final String VENDOR1_CATALOG = "vendor1Catalog";
	private static final String VENDOR2_CATALOG = "vendor2Catalog";
	private static final String CONSIGNMENT_CODE = "a10001000";
	private static final String UNIT_CODE_TYPE = "pieces";

	@Resource(name = "vendorDao")
	private VendorDao vendorDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	private CatalogVersionModel catalogVersion1;
	private CatalogModel catalog;
	private VendorModel vendor1, vendor2, vendor3;

	@Before
	public void prepare()
	{
		vendor1 = new VendorModel();
		catalog = new CatalogModel();
		catalog.setId(VENDOR1_CATALOG);
		vendor1.setCatalog(catalog);
		vendor1.setCode(VENDOR1_CODE);
		vendor1.setActive(true);
		final CatalogModel catalog1 = new CatalogModel();
		catalog1.setId(VENDOR1_CATALOG);
		catalogVersion1 = new CatalogVersionModel();
		catalogVersion1.setActive(true);
		catalogVersion1.setVersion("Online");
		catalogVersion1.setCatalog(catalog1);
		vendor1.setCatalog(catalog1);


		vendor2 = new VendorModel();
		vendor2.setCode(VENDOR2_CODE);
		vendor2.setActive(true);

		final CatalogModel catalog2 = new CatalogModel();
		catalog2.setId(VENDOR2_CATALOG);
		final CatalogVersionModel catalogVersion2 = new CatalogVersionModel();
		catalogVersion2.setActive(true);
		catalogVersion2.setVersion("Online");
		catalogVersion2.setCatalog(catalog2);
		vendor2.setCatalog(catalog2);

		vendor3 = new VendorModel();
		vendor3.setCode(VENDOR3_CODE);
		vendor3.setActive(false);

		modelService.save(vendor1);
		modelService.save(vendor2);
		modelService.save(vendor3);
	}

	@Test
	public void testFindVendorByCode()
	{
		final Optional<VendorModel> vendor = vendorDao.findVendorByCode(VENDOR1_CODE);

		assertTrue(vendor.isPresent());
		assertEquals(VENDOR1_CODE, vendor.get().getCode());
	}

	@Test
	public void testFindActiveVendor()
	{
		final List<VendorModel> activeVendors = vendorDao.findActiveVendors();

		assertEquals(2, activeVendors.size());
		final List<VendorModel> compareList = new ArrayList<>(activeVendors);
		compareList.sort(Comparator.comparing(VendorModel::getCode));
		assertEquals(VENDOR1_CODE, compareList.get(0).getCode());
		assertEquals(VENDOR2_CODE, compareList.get(1).getCode());
	}

	@Test
	public void testFindVendorByProductCode()
	{
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		product.setCatalogVersion(catalogVersion1);
		modelService.save(product);

		final Optional<VendorModel> vendor = vendorDao.findVendorByProduct(product);

		assertTrue(vendor.isPresent());
		assertEquals(VENDOR1_CODE, vendor.get().getCode());
	}

	@Test
	public void testFindActiveCatalogs()
	{
		final List<CatalogModel> activeCatalogs = vendorDao.findActiveCatalogs();
		assertEquals(2, activeCatalogs.size());
		final List<CatalogModel> compareList = new ArrayList<>(activeCatalogs);
		compareList.sort(Comparator.comparing(CatalogModel::getId));
		assertEquals(VENDOR1_CATALOG, compareList.get(0).getId());
		assertEquals(VENDOR2_CATALOG, compareList.get(1).getId());
	}


	@Test
	public void testFindActiveCatalogVersions()
	{
		final List<CatalogVersionModel> activeCatalogVersions = vendorDao.findActiveCatalogVersions();
		assertEquals(2, activeCatalogVersions.size());
	}

	@Test
	public void testFindVendorByConsignmentCode()
	{
		createConsignmentForVendor(vendor1);
		final Optional<VendorModel> vendor = vendorDao.findVendorByConsignmentCode(CONSIGNMENT_CODE);

		assertTrue(vendor.isPresent());
		assertEquals(VENDOR1_CODE, vendor.get().getCode());
	}

	@Test
	public void testFindPendingConsignmentEntryByVendor()
	{
		final ConsignmentModel consignment = createConsignmentForVendor(vendor1);
		createConsignmentEntryForVendor(consignment);
		final List<ConsignmentEntryModel> consignmententries = vendorDao.findPendingConsignmentEntryForVendor(VENDOR1_CODE);
		assertFalse(consignmententries.isEmpty());
		
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		modelService.save(consignment);
		assertTrue(vendorDao.findPendingConsignmentEntryForVendor(VENDOR1_CODE).isEmpty());
	}

	@Test
	public void testFindPagedActiveVendors()
	{
		final PageableData pageableData = createPageableData(0, 1, "byNameAsc");
		final SearchPageData<VendorModel> vendors = vendorDao.findPagedActiveVendors(pageableData);
		assertEquals(1, vendors.getResults().size());
		assertEquals(1, vendors.getPagination().getPageSize());
		assertEquals(2, vendors.getPagination().getTotalNumberOfResults());
		assertEquals(2, vendors.getSorts().size());
		assertEquals("byNameAsc", vendors.getPagination().getSort());
	}


	/**
	 * extract a common method to create consignment
	 *
	 * @param vendor
	 *           the target vendor
	 */
	protected ConsignmentModel createConsignmentForVendor(final VendorModel vendor)
	{
		final CustomerModel customer = new CustomerModel();
		customer.setUid(UUID.randomUUID().toString());

		final AddressModel shippingAddress = new AddressModel();
		shippingAddress.setOwner(customer);

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode(PRODUCT_CODE);
		warehouse.setVendor(vendor);

		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setWarehouse(warehouse);
		consignment.setCode(CONSIGNMENT_CODE);
		consignment.setStatus(ConsignmentStatus.WAITING);
		consignment.setShippingAddress(shippingAddress);
		modelService.save(consignment);

		return consignment;
	}

	/**
	 * extract a common method to create consignment
	 *
	 * @param vendor
	 *           the target vendor
	 */
	protected void createConsignmentEntryForVendor(final ConsignmentModel consignment)
	{
		Long quantity = new Long(2);
		ZoneDeliveryModeModel zdm;
		ZoneDeliveryModeValueModel val;
		OrderModel order;
		CurrencyModel currency;
		ZoneModel zone;
		BaseStoreModel store;
		CountryModel country;
		CustomerModel customer;
		
		zdm = new ZoneDeliveryModeModel();
		zdm.setCode("zdm");
		zdm.setName("zdmName", new Locale("en"));
		zdm.setNet(Boolean.TRUE);
		zdm.setActive(Boolean.TRUE);
		modelService.save(zdm);

		zone = new ZoneModel();
		zone.setCode("south");
		modelService.save(zone);

		country = new CountryModel();
		country.setName("china", new Locale("en"));
		country.setIsocode("zh");
		modelService.save(country);

		final Set<CountryModel> countries = new HashSet<>();
		countries.add(country);
		zone.setCountries(countries);

		currency = new CurrencyModel();
		currency.setName("USD", new Locale("en"));
		currency.setSymbol("$");
		currency.setIsocode("en");
		modelService.save(currency);

		val = new ZoneDeliveryModeValueModel();
		val.setDeliveryMode(zdm);
		val.setZone(zone);
		val.setCurrency(currency);
		val.setMinimum(1d);
		val.setValue(10d);
		modelService.save(val);

		store = new BaseStoreModel();
		store.setName("teststore", new Locale("en"));
		final Set<DeliveryModeModel> zdms = new HashSet<>();
		zdms.add(zdm);
		store.setDeliveryModes(zdms);
		store.setUid("teststore");
		modelService.save(store);

		customer = new CustomerModel();
		customer.setUid("");

		order = new OrderModel();
		order.setCode("0000000001");
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setNet(Boolean.TRUE);
		order.setStore(store);
		order.setUser(customer);
		modelService.save(order);
		
		final ProductModel product = new ProductModel();
		product.setCatalogVersion(catalogVersion1);
		product.setCode(PRODUCT_CODE);
		product.setName("Mock Product", Locale.ENGLISH);
		product.getName(Locale.ENGLISH);
		modelService.save(product);
		
		final UnitModel unit = new UnitModel();
		unit.setCode(UNIT_CODE_TYPE);
		unit.setUnitType(UNIT_CODE_TYPE);
		unit.setName("Mock Unit", Locale.ENGLISH);
		modelService.save(unit);
		
		final OrderEntryModel orderentry = new OrderEntryModel();
		orderentry.setProduct(product);
		orderentry.setUnit(unit);
		orderentry.setQuantity(quantity);
		orderentry.setOrder(order);
		modelService.save(orderentry);
		
		final ConsignmentEntryModel consignmententry = new ConsignmentEntryModel();
		consignmententry.setConsignment(consignment);
		consignmententry.setQuantity(quantity);
		consignmententry.setOrderEntry(orderentry);
		
		modelService.save(consignmententry);

	}
	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}
}
