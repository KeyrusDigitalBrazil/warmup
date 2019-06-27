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
package de.hybris.platform.sap.sapmodel.auditReport;

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static de.hybris.platform.audit.AuditTestHelper.noDuplicatedReportEntries;
import static org.assertj.core.api.Assertions.tuple;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableTest;
import de.hybris.platform.audit.TypeAuditReportConfig;
import de.hybris.platform.audit.demo.AuditTestConfigManager;
import de.hybris.platform.audit.internal.config.AtomicAttribute;
import de.hybris.platform.audit.internal.config.AuditReportConfig;
import de.hybris.platform.audit.internal.config.ReferenceAttribute;
import de.hybris.platform.audit.internal.config.ResolvesBy;
import de.hybris.platform.audit.internal.config.Type;
import de.hybris.platform.audit.internal.config.VirtualAttribute;
import de.hybris.platform.audit.view.AuditViewService;
import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.audit.internal.AuditEnablementService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import org.apache.commons.configuration.Configuration;

@IntegrationTest
public class SAPOrderAuditReportViewTest extends ServicelayerTransactionalBaseTest implements AuditableTest
{
	private static final String ORDER = "Order";
	private static final String ENTRIES = "entries";
	private static final String QUANTITY = "quantity";
	private static final String UNIT = "unit";
	private static final String DATE = "date";
	private static final String PRODUCT = "product";

	private static final String PRODUCT_NAME_EN_1 = "prod_en_1";
	private static final String PRODUCT_NAME_DE_1 = "prod_de_1";
	@Resource
	private ModelService modelService;

	@Resource
	private AuditViewService auditViewService;


	private CatalogModel catalog;
	private CatalogVersionModel catalogVersion;

	@Resource
	private ConfigurationService configurationService;

	private Configuration configuration;

	private AuditEnablementService auditEnablementService;

	private ProductModel product;

	@Before
	public void setUp() throws Exception
	{
		auditEnablementService = new AuditEnablementService();
		configuration = configurationService.getConfiguration();
		configuration.addProperty("audit.saporder.enabled", "true");
		configuration.addProperty("audit.product.enabled", "true");
		configuration.addProperty("audit.order.enabled", "true");
		configuration.addProperty("audit.orderentry.enabled", "true");
		configuration.addProperty("audit.unit.enabled", "true");
		configuration.addProperty("audit.currency.enabled", "true");
		auditEnablementService.refreshConfiguredAuditTypes();
		getOrCreateLanguage("en");
		getOrCreateLanguage("de");

		createCatalogAndCatalogVersion();
	}

	@Test
	public void sapOrderReportView()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(UUID.randomUUID().toString());
		modelService.save(user);


		final CurrencyModel euroCurrency = modelService.create(CurrencyModel.class);
		euroCurrency.setIsocode("EUR");
		euroCurrency.setSymbol("E");

		final CurrencyModel plnCurrency = modelService.create(CurrencyModel.class);
		plnCurrency.setIsocode("PLN");
		plnCurrency.setSymbol("zl");

		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode(UUID.randomUUID().toString());
		unit.setUnitType("pieces");

		modelService.saveAll(euroCurrency, plnCurrency, unit);

		final OrderModel order = modelService.create(OrderModel.class);
		order.setCode("orderTest");
		order.setUser(user);
		final Date firstDate = getDate(2012, 10, 1);
		order.setDate(firstDate);
		order.setCurrency(euroCurrency);
		modelService.save(order);

		final OrderEntryModel orderEntry = modelService.create(OrderEntryModel.class);
		orderEntry.setOrder(order);
		orderEntry.setProduct(product);
		orderEntry.setUnit(unit);
		orderEntry.setQuantity(Long.valueOf(1L));

		modelService.save(orderEntry);

		orderEntry.setQuantity(Long.valueOf(2L));
		modelService.save(orderEntry);

		final SAPOrderModel sapOrder1 = modelService.create(SAPOrderModel.class);
		sapOrder1.setCode("sapOrderCode1");
		sapOrder1.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);

		final SAPOrderModel sapOrder2 = modelService.create(SAPOrderModel.class);
		sapOrder2.setCode("sapOrderCode2");
		sapOrder2.setSapOrderStatus(SAPOrderStatus.CANCELLED_FROM_ERP);

		sapOrder1.setOrder(order);
		modelService.save(sapOrder1);

		sapOrder2.setOrder(order);
		modelService.save(sapOrder2);
		order.setSapOrders(Sets.newHashSet(sapOrder1,sapOrder2));

		modelService.save(order);

		final List<ReportView> reportViews = auditViewService
				.getViewOn(TypeAuditReportConfig.builder().withConfig(createSAPOrderConfig()).withRootTypePk(order.getPk()).withFullReport().build())
				.collect(toList());

        
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("Order").flatExtracting("sapOrder").extracting("code")
				.contains("sapOrderCode1", "sapOrderCode2");
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("Order").flatExtracting("sapOrder").extracting("sapOrderStatus")
				.contains(SAPOrderStatus.SENT_TO_ERP.getCode(), SAPOrderStatus.CANCELLED_FROM_ERP.getCode());
	}

	private AuditReportConfig createSAPOrderConfig()
	{
		final Type sapOrder = Type.builder().withCode("SAPOrder") //
				.withAtomicAttributes( //
									   AtomicAttribute.builder().withQualifier("code").build(), //
									   AtomicAttribute.builder().withQualifier("sapOrderStatus").build() //
				).build();//

		final Type order = Type.builder().withCode("Order") //
				.withAtomicAttributes( //
									   AtomicAttribute.builder().withQualifier("name").build(), //
									   AtomicAttribute.builder().withQualifier("code").build() //
				) //
				.withVirtualAttributes( //
										VirtualAttribute
												.builder().withExpression("sapOrder").withMany(Boolean.TRUE).withType(sapOrder)
												.withResolvesBy( //
																 ResolvesBy.builder().withExpression("order").withResolverBeanId("virtualReferencesResolver").build() //
												).build() //
				)
				.build();


		final AuditReportConfig reportConfig = AuditReportConfig.builder() //
				.withGivenRootType(order) //
				.withName("PersonalDataReport") //
				.withTypes(order,sapOrder) //
				.build();

		return reportConfig;
	}
	private Date getDate(final int year, final int month, final int dayOfMonth)
	{
		final LocalDate firstOrderDate = LocalDate.of(year, month, dayOfMonth);
		final Date firstDate = Date.from(firstOrderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		return firstDate;
	}

	private Object extractDirectReference(final Map<String, Object> o1, final String reference)
	{
		return o1.getOrDefault(reference, Collections.emptyMap());
	}
	private void createCatalogAndCatalogVersion()
	{
		catalog = modelService.create(CatalogModel.class);
		catalog.setId(UUID.randomUUID().toString());

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setVersion(UUID.randomUUID().toString());
		catalogVersion.setCatalog(catalog);

		product = modelService.create(ProductModel.class);
		product.setCode(UUID.randomUUID().toString());

		product.setName(PRODUCT_NAME_EN_1, Locale.ENGLISH);
		product.setName(PRODUCT_NAME_DE_1, Locale.GERMAN);

		product.setCatalogVersion(catalogVersion);


		modelService.saveAll();
	}
}
