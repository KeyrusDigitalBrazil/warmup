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
package de.hybris.platform.ordercancel;

import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.dao.OrderCancelDao;
import de.hybris.platform.ordercancel.exceptions.OrderCancelDaoException;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.impl.denialstrategies.StateMappingStrategyDependent;
import de.hybris.platform.ordercancel.impl.executors.NotificationServiceAdapterDependent;
import de.hybris.platform.ordercancel.impl.executors.PaymentServiceAdapterDependent;
import de.hybris.platform.ordercancel.impl.executors.WarehouseAdapterDependent;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;


abstract public class AbstractOrderCancelTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(AbstractOrderCancelTest.class.getName());

	@Resource
	private ProductService productService;
	@Resource
	private CartService cartService;
	@Resource
	private UserService userService;
	@Resource
	private OrderService orderService;
	@Resource
	private OrderHistoryService orderHistoryService;
	@Resource
	private ModelService modelService;
	@Resource
	private DefaultOrderCancelService orderCancelService;
	@Resource
	private CalculationService calculationService;

	private OrderModel order;
	private OrderCancelConfigModel configuration;

	private PrincipalModel currentPrincipal;
	private OrderCancelConfigModel config;
	private ProductModel product3;

	private OrderCancelStateMappingStrategy orderCancelStateMappingStrategyOrginal;
	private OrderCancelDao orderCancelDaoOriginal;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Setting up ORDER CANCEL TEST SUITE");
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		this.setConfig(getOrCreateOrderCancelConfig(30));
		setOrder(placeTestOrder());

		orderCancelStateMappingStrategyOrginal = getOrderCancelService().getStateMappingStrategy();
		orderCancelDaoOriginal = getOrderCancelService().getOrderCancelDao();
	}

	protected OrderCancelConfigModel getOrCreateOrderCancelConfig(final int queueLength)
	{
		final OrderCancelConfigModel persistedConfig = getOrderCancelService().getConfiguration();
		if (persistedConfig != null)
		{
			return persistedConfig;
		}
		final OrderCancelConfigModel config = getModelService().create(OrderCancelConfigModel.class);
		config.setOrderCancelAllowed(true);
		config.setPartialCancelAllowed(true);
		config.setPartialOrderEntryCancelAllowed(true);
		config.setCancelAfterWarehouseAllowed(true);
		config.setQueuedOrderWaitingTime(queueLength);
		getModelService().save(config);
		return config;
	}

	protected OrderModel placeTestOrder() throws InvalidCartException, CalculationException
	{
		try
		{
			// remove any potentially existing session cart
			// to avoid errors regarding non-unique cart/order codes (BIT-3733)
			cartService.removeSessionCart();
			final CartModel cart = cartService.getSessionCart();
			final UserModel user = userService.getCurrentUser();
			cartService.addNewEntry(cart, productService.getProductForCode("testProduct1"), 1, null);
			cartService.addNewEntry(cart, productService.getProductForCode("testProduct2"), 2, null);
			setProduct3(productService.getProductForCode("testProduct3"));
			cartService.addNewEntry(cart, getProduct3(), 3, null);

			final AddressModel deliveryAddress = new AddressModel();
			deliveryAddress.setOwner(user);
			deliveryAddress.setFirstname("Der");
			deliveryAddress.setLastname("Buck");
			deliveryAddress.setTown("Muenchen");

			final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
			paymentInfo.setCode("aTotallyUniqueCode");
			paymentInfo.setOwner(cart);
			paymentInfo.setBank("MeineBank");
			paymentInfo.setUser(user);
			paymentInfo.setAccountNumber("34434");
			paymentInfo.setBankIDNumber("1111112");
			paymentInfo.setBaOwner("Ich");

			cart.setDeliveryAddress(deliveryAddress);
			cart.setPaymentInfo(paymentInfo);
			final OrderModel order = orderService.createOrderFromCart(cart);
			modelService.save(order);
			calculationService.calculate(order);
			return order;
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Error placing test order: " + e.getMessage(), e);
			throw e;
		}
	}

	protected OrderCancelRecordModel createCancelRecord(final OrderModel order)
	{
		final OrderCancelRecordModel cancelREcord = getModelService().create(OrderCancelRecordModel.class);
		cancelREcord.setOrder(order);
		getModelService().save(cancelREcord);
		return cancelREcord;
	}

	protected OrderCancelRecordEntryModel createCancelRecordEntry(final OrderModel order)
	{
		OrderCancelRecordModel cancelRecord = null;

		for (final OrderModificationRecordModel record : order.getModificationRecords())
		{
			if (record instanceof OrderCancelRecordModel)
			{
				cancelRecord = (OrderCancelRecordModel) record;
			}
		}
		if (cancelRecord == null)
		{
			cancelRecord = createCancelRecord(order);
		}

		final OrderHistoryEntryModel snaphot = createSnaphot(order);
		final OrderCancelRecordEntryModel entry = getModelService().create(OrderCancelRecordEntryModel.class);
		entry.setModificationRecord(cancelRecord);
		entry.setCode("testEntry");
		entry.setOriginalVersion(snaphot);
		entry.setTimestamp(new Date());
		entry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);

		getModelService().save(entry);
		return entry;
	}

	protected OrderHistoryEntryModel createSnaphot(final OrderModel order)
	{
		final OrderModel version = orderHistoryService.createHistorySnapshot(order);
		orderHistoryService.saveHistorySnapshot(version);

		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);
		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(version);
		historyEntry.setDescription("test Cancel entry created");
		historyEntry.setTimestamp(new Date());
		getModelService().save(historyEntry);
		return historyEntry;
	}

	protected interface RequestOriginStrategyHelper
	{
		PrincipalModel getCallingPrincipal();
	}

	protected final static RequestOriginStrategyHelper REQUEST_ORIGIN_STRATEGY_CSA = new RequestOriginStrategyHelper()
	{

		@Override
		public PrincipalModel getCallingPrincipal()
		{
			return new EmployeeModel();
		}
	};

	protected final static RequestOriginStrategyHelper REQUEST_ORIGIN_STRATEGY_CUSTOMER = new RequestOriginStrategyHelper()
	{
		@Override
		public PrincipalModel getCallingPrincipal()
		{
			return new CustomerModel();
		}
	};

	protected static abstract class AbstractMockOrderCancelStateManagementStrategy implements OrderCancelStateMappingStrategy
	{
		//
	}

	protected final static OrderCancelStateMappingStrategy STATE_STRATEGY_HOLDING_AREA_QUEUE_WAITING = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.PENDINGORHOLDINGAREA;
		}
	};

	/**
	 * Injects state mapping strategy into all spring beans that need it.
	 *
	 * @param targetStrategy
	 */
	protected void injectStateMappingStrategy(final OrderCancelStateMappingStrategy targetStrategy)
	{
		getOrderCancelService().setStateMappingStrategy(targetStrategy);
		for (final OrderCancelDenialStrategy ocds : getOrderCancelService().getCancelDenialStrategies())
		{
			if (ocds instanceof StateMappingStrategyDependent)
			{
				((StateMappingStrategyDependent) ocds).setStateMappingStrategy(targetStrategy);
			}
		}
	}

	protected final static OrderCancelStateMappingStrategy STATE_STRATEGY_SENT_TO_WAREHOUSE = new AbstractMockOrderCancelStateManagementStrategy()
	{
		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.SENTTOWAREHOUSE;
		}
	};

	protected final static OrderCancelStateMappingStrategy STATE_STRATEGY_SHIPPING = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.SHIPPING;
		}
	};

	protected final static OrderCancelStateMappingStrategy STATE_STRATEGY_PARTIALLY_SHIPPED = new AbstractMockOrderCancelStateManagementStrategy()
	{
		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.PARTIALLYSHIPPED;
		}
	};

	protected final static OrderCancelStateMappingStrategy STATE_STRATEGY_IMPOSSIBLE = new AbstractMockOrderCancelStateManagementStrategy()
	{
		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.CANCELIMPOSSIBLE;
		}
	};

	protected static void injectAdaptersDependencies(final OrderCancelNotificationServiceAdapter notificationServiceAdapter,
			final OrderCancelPaymentServiceAdapter paymentServiceAdapter, final OrderCancelWarehouseAdapter warehouseAdapter,
			final DefaultOrderCancelService orderCancelService)
	{
		//		if (warehouseAdapter != null)
		//		{
		//			orderCancelService.setWarehouseAdapter(warehouseAdapter);
		//		}
		//		if (notificationServiceAdapter != null)
		//		{
		//			orderCancelService.setNotificationsAdapter(notificationServiceAdapter);
		//		}
		//		if (paymentServiceAdapter != null)
		//		{
		//			orderCancelService.setPaymentsAdapter(paymentServiceAdapter);
		//		}

		final Map<OrderCancelState, OrderCancelRequestExecutor> requestExecutorsMap = orderCancelService.getRequestExecutorsMap();

		for (final OrderCancelRequestExecutor requestExecutor : requestExecutorsMap.values())
		{
			if (requestExecutor instanceof NotificationServiceAdapterDependent && notificationServiceAdapter != null)
			{
				((NotificationServiceAdapterDependent) requestExecutor).setNotificationServiceAdapter(notificationServiceAdapter);
			}
			if (requestExecutor instanceof PaymentServiceAdapterDependent && paymentServiceAdapter != null)
			{
				((PaymentServiceAdapterDependent) requestExecutor).setPaymentServiceAdapter(paymentServiceAdapter);
			}
			if (requestExecutor instanceof WarehouseAdapterDependent && warehouseAdapter != null)
			{
				((WarehouseAdapterDependent) requestExecutor).setWarehouseAdapter(warehouseAdapter);
			}
		}

		final Map<OrderCancelState, OrderCancelResponseExecutor> responseExecutorsMap = orderCancelService
				.getResponseExecutorsMap();

		for (final OrderCancelResponseExecutor responseExecutor : responseExecutorsMap.values())
		{
			if (responseExecutor instanceof NotificationServiceAdapterDependent && notificationServiceAdapter != null)
			{
				((NotificationServiceAdapterDependent) responseExecutor).setNotificationServiceAdapter(notificationServiceAdapter);
			}
			if (responseExecutor instanceof PaymentServiceAdapterDependent && paymentServiceAdapter != null)
			{
				((PaymentServiceAdapterDependent) responseExecutor).setPaymentServiceAdapter(paymentServiceAdapter);
			}
			if (responseExecutor instanceof WarehouseAdapterDependent && warehouseAdapter != null)
			{
				((WarehouseAdapterDependent) responseExecutor).setWarehouseAdapter(warehouseAdapter);
			}
		}
	}

	protected static class OrderCancelDaoMock implements OrderCancelDao
	{
		private final OrderCancelConfigModel configuration;

		public OrderCancelDaoMock(final OrderCancelConfigModel configuration)
		{
			this.configuration = configuration;
		}

		@Override
		public OrderCancelConfigModel getOrderCancelConfiguration() throws OrderCancelDaoException
		{
			return this.configuration;
		}

		@Override
		public OrderCancelRecordModel getOrderCancelRecord(final OrderModel order) throws OrderCancelDaoException
		{
			return null;
		}

		@Override
		public Collection<OrderCancelRecordEntryModel> getOrderCancelRecordEntries(final EmployeeModel employee)
				throws OrderCancelDaoException
		{
			return null;
		}

		@Override
		public Collection<OrderCancelRecordEntryModel> getOrderCancelRecordEntries(final OrderModel order)
				throws OrderCancelDaoException
		{
			return null;
		}

		@Override
		public OrderEntryCancelRecordEntryModel getOrderEntryCancelRecord(final OrderEntryModel orderEntry,
				final OrderCancelRecordEntryModel cancelEntry)
		{
			return null;
		}
	}

	@After
	public void tearDown()
	{
		//BAM-357  assure proper mapping strategy
		LOG.debug("setting original strategy : " + orderCancelStateMappingStrategyOrginal.getClass().getName());
		injectStateMappingStrategy(orderCancelStateMappingStrategyOrginal);

		getOrderCancelService().setOrderCancelDao(orderCancelDaoOriginal);
	}

	public OrderCancelConfigModel getConfig()
	{
		return config;
	}

	public void setConfig(final OrderCancelConfigModel config)
	{
		this.config = config;
	}

	public PrincipalModel getCurrentPrincipal()
	{
		return currentPrincipal;
	}

	public void setCurrentPrincipal(final PrincipalModel currentPrincipal)
	{
		this.currentPrincipal = currentPrincipal;
	}

	public OrderCancelConfigModel getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final OrderCancelConfigModel configuration)
	{
		this.configuration = configuration;
	}

	public OrderModel getOrder()
	{
		return order;
	}

	public void setOrder(final OrderModel order)
	{
		this.order = order;
	}

	public ProductModel getProduct3()
	{
		return product3;
	}

	public void setProduct3(final ProductModel product3)
	{
		this.product3 = product3;
	}

	public DefaultOrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	public void setOrderCancelService(final DefaultOrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
