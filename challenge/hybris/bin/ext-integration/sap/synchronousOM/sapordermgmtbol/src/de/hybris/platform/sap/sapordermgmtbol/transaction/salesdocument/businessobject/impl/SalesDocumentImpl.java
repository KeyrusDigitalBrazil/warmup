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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.hook.SalesDocumentHook;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SalesDocumentBaseImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesTransactionsFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.OrderMgmtMessage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.util.SalesDocumentUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.sap.tc.logging.Severity;


/**
 * Common superclass for all sales documents.
 *
 */
@SuppressWarnings("squid:ClassCyclomaticComplexity")
public abstract class SalesDocumentImpl extends SalesDocumentBaseImpl<ItemList, Item, Header> implements SalesDocument
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(SalesDocumentImpl.class.getName());


	protected TransactionConfiguration transactionConfiguration = null;

	protected SalesDocumentBackend backendService = null;

	protected boolean alreadyInitialized = false;
	protected boolean externalToOrder;

	private SessionService sessionService;

	protected boolean checkCatalogNecessary = true;

	/**
	 * redemption value
	 */
	protected String redemptionValue;

	protected boolean determinationRequired;

	protected boolean grossValueAvailable = true;

	protected boolean netValueAvailable = true;

	protected boolean pricesTraced = false;

	/**
	 * This list stores items with alternative products. For these items events should only be fired when a product is
	 * selected (see int msg 385863 2010).
	 */
	protected final List<TechKey> itemsWithAlternativeProductList = new ArrayList();

	private boolean updateMissing = false;

	private SalesTransactionsFactory salesFactory;

	private Converter converter;

	private boolean backendWasUp = false;

	private boolean backendWasDown = false;

	private List<SalesDocumentHook> salesDocumentHooks;

	@Override
	public boolean isCheckCatalogNecessary()
	{
		return checkCatalogNecessary;
	}

	@Override
	public void setCheckCatalogNecessary(final boolean checkCatalogNecessary)
	{
		this.checkCatalogNecessary = checkCatalogNecessary;
	}

	/**
	 * Sets the document type on the document header depending on the Java class instance of the Sales Document
	 * (evaluated with <i><code>instanceof</code> </i>).
	 *
	 * @throws CommunicationException
	 */
	protected void adaptHeaderDocumentType() throws CommunicationException
	{

		switch (getDocumentType())
		{
			case BASKET:
				getHeader().setDocumentType(DocumentType.BASKET);
				break;
			case ORDER:
				getHeader().setDocumentType(DocumentType.ORDER);
				break;
			case QUOTATION:
				getHeader().setDocumentType(DocumentType.QUOTATION);
				break;
			case RFQ:
				getHeader().setDocumentType(DocumentType.RFQ);
				break;
			default:
				getHeader().setDocumentType(null);

		}

	}

	/**
	 * Adds a shipTo to the shipTo list<br>
	 *
	 * @param shipTo
	 *           shipTo to add
	 */
	public void addShipTo(final ShipTo shipTo)
	{
		shipToList.add(shipTo);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #clearShipTos()
	 */
	@Override
	public void clearShipTos()
	{
		shipToList.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #createBillTo()
	 */
	@Override
	public BillTo createBillTo()
	{
		return getSalesTransactionsFactory().createBillTo();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.interf. SalesDocumentBase#createItem()
	 */
	@Override
	public Item createItem()
	{
		final Item item = getSalesTransactionsFactory().createSalesDocumentItem();

		item.setParentId(TechKey.EMPTY_KEY);

		return item;
	}

	private SalesTransactionsFactory getSalesTransactionsFactory()
	{
		if (null == salesFactory)
		{
			salesFactory = (SalesTransactionsFactory) genericFactory
					.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TRANSACTIONS_FACTORY);
		}

		return salesFactory;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #createShipTo()
	 */
	@Override
	public ShipTo createShipTo()
	{
		return getSalesTransactionsFactory().createShipTo();
	}

	/**
	 * Deletes the item with the given Techkey
	 *
	 * @param techKey
	 * @throws CommunicationException
	 */
	protected void deleteItemInt(final TechKey techKey) throws CommunicationException
	{
		// now delete the item
		try
		{
			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemInBackend(this, techKey);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #deleteItems(com.sap.wec.tc.core.common.TechKey[])
	 */
	@Override
	public void removeItems(final TechKey[] techKeys) throws CommunicationException
	{
		sapLogger.entering("deleteItems()");
		if ((techKeys == null) || (techKeys.length == 0))
		{
			sapLogger.debug("techKeys is null or contains no entries. Exit method");
			return;
		}

		// now delete the items
		try
		{

			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemsInBackend(this, techKeys, getTransactionConfiguration());
			afterDeleteItemInBackend(Arrays.asList(techKeys));
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.impl. SalesDocumentBaseImpl#destroy()
	 */
	@Override
	public void destroy()
	{
		setUpdateMissing(false);
		setDirty(true);
		pricesTraced = false;
		if (getHeader() != null)
		{
			getHeader().setDirty(true);
		}
		try
		{
			// deletion of cookie logic removed

			// only destroy the backend object if the service exists
			// otherwise we have a useless call that creates a backend
			// object to destroy itself immediately afterwards...
			if (backendService != null)
			{
				getBackendService().destroyBackendObject();
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Error while destroying SalesDocument", e);
		}
		super.destroy();
		shipToList = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #destroyContent()
	 */
	@Override
	public void destroyContent() throws CommunicationException
	{

		sapLogger.entering("destroyContent()");

		alreadyInitialized = false;
		pricesTraced = false;
		try
		{
			getBackendService().emptyInBackend(this);
			setDirty(true);
			getHeader().setDirty(true);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	/**
	 * Resets the entire document to empty state.<br>
	 *
	 * @throws CommunicationException
	 *            in case of aback-end error
	 */
	public void emptyContent() throws CommunicationException
	{

		sapLogger.entering("emptyContent()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("emptyContent(): transactionConfiguration = " + transactionConfiguration);
		}

		try
		{

			alreadyInitialized = false;
			pricesTraced = false;
			getBackendService().emptyInBackend(this);

			// clone the PartnerList as this will be cleared otherwise (call by
			// reference)
			final PartnerList partnerList = getHeader().getPartnerList().clone();
			init(partnerList, getHeader().getProcessType());

			setDirty(true);
			getHeader().setDirty(true);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}


	/**
	 * Method retrieving the backend object for the object. This method is abstract because every concrete subclass of
	 * <code>SalesDocument</code> may use its own implementation of a backend object.
	 *
	 * @return Backend object to be used
	 */
	protected abstract SalesDocumentBackend getBackendService() throws BackendException;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #getSalesDocumentType()
	 */
	@Override
	public SalesDocumentType getDocumentType() throws CommunicationException
	{
		try
		{
			return getBackendService().getSalesDocumentType();
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
			return SalesDocumentType.UNKNOWN;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #getTransactionConfiguration()
	 */
	@Override
	public TransactionConfiguration getTransactionConfiguration()
	{
		return transactionConfiguration;
	}

	/**
	 * Initializes a sales document
	 *
	 * @param partnerList
	 *           Partner list
	 * @param processType
	 *           Sales document type
	 * @throws CommunicationException
	 */
	public void init(final PartnerList partnerList, final String processType) throws CommunicationException
	{

		sapLogger.entering("init()");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("init(): transactionConfiguration = " + transactionConfiguration + ", processtype = " + processType);
		}

		clear();
		clearShipTos();

		if (partnerList != null)
		{
			getHeader().setPartnerList(partnerList);
		}
		if (processType != null)
		{
			getHeader().setProcessType(processType);
		}

		// set the correct salesdocument type
		adaptHeaderDocumentType();

		setPersistentInBackend(false);

		if (!alreadyInitialized)
		{
			try
			{
				// let the backend implementation decide if it is dirty. The
				// backend ca reset this flag.
				setDirty(true);
				getHeader().setDirty(true);

				alreadyInitialized = true;
				getBackendService().createInBackend(transactionConfiguration, this);
			}
			catch (final BackendException ex)
			{
				BusinessObjectHelper.splitException(ex);
			}

		}

		sapLogger.exiting();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument#init(de.hybris.platform
	 * .sap.sapordermgmtbol.order.businessobject.interf.PartnerList)
	 */
	@Override
	public void init(final PartnerList partnerList) throws CommunicationException
	{
		init(partnerList, (String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE));

	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #init(com.sap.hybris.app.esales.module.transaction.businessobject.interf. SalesDocument,
	 * com.sap.wec.app.common.module.businesspartner.businessobject .interf.BusinessPartnerManager, java.lang.String,
	 * boolean)
	 */
	@SuppressWarnings("squid:ClassCyclomaticComplexity")
	@Override
	public void init(final SalesDocument source, final String processType) throws BusinessObjectException
	{

		sapLogger.entering("init()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("init(): posd = " + source + ", transactionConfiguration = " + transactionConfiguration
					+ ", processtype = " + processType);
		}
		clear();

		// copy the header (this is a deep copy, so also including partner list,
		// etc)
		setHeader((Header) source.getHeader().clone());

		// Cloning also clones the document type, so the document type needs to
		// be adjusted afterwards.
		adaptHeaderDocumentType();

		if (processType != null)
		{
			getHeader().setProcessType(processType);
		}

		// we have to copy the extension information because it gets lost,
		// when the header is cloned and the new document is initialized.
		final Map<String, Object> extCopy = SalesDocumentUtil.createExtensionMap(source.getHeader().getExtensionDataValues());

		createInBackend();

		getHeader().setExtensionMap(extCopy);
		getItemList().clear();

		final StringBuilder debugOutput = processItems(source);
		if (debugOutput != null && sapLogger.isDebugEnabled())
		{
			sapLogger.debug(debugOutput.toString());
		}

		setPersistentInBackend(false);

		sapLogger.exiting();
	}

	/**
	 * @param source
	 * @return
	 */
	protected StringBuilder processItems(final SalesDocument source)
	{

		// Copy the items. Because of the fact, that the CRM has problems
		// dealing with items containing too much data, I do not use the
		// clone() method but copy the relevant fields manually
		StringBuilder debugOutput = null;
		if (sapLogger.isDebugEnabled())
		{
			debugOutput = new StringBuilder("\nitem copy process");
		}
		for (int i = 0; i < source.getItemList().size(); i++)
		{
			final Item oldItem = source.getItemList().get(i);

			if (oldItem.getParentId().isInitial())
			{

				if (debugOutput != null && sapLogger.isDebugEnabled())
				{
					debugOutput.append("\n item will be copied");
				}

				final Item newItem = createItem();
				// if camp det should be executed anyway, even for a copied item
				// the execCampDet flag must be set to true
				newItem.setCopiedFromOtherItem(true);
				SalesDocumentUtil.copyAttributesOfItem(oldItem, newItem);

				// sub-items if any
				Date reqDeliveryDate = null;
				reqDeliveryDate = oldItem.getReqDeliveryDate();
				// set reqDeliveryDate, LatestDlvDate required for main and
				newItem.setReqDeliveryDate(reqDeliveryDate);

				// copy extension informations
				if (oldItem.getExtensionDataValues() != null)
				{
					// we have to copy the extension information because it gets
					// lost, when the header is cloned and the new document is
					// initialized.
					final Map<String, Object> extCopy2 = SalesDocumentUtil.createExtensionMap(oldItem.getExtensionDataValues());
					newItem.setExtensionMap(extCopy2);
				}


				newItem.setHandle(String.valueOf(i + 2));

				getItemList().add(newItem);
			}
			else
			{
				if (debugOutput != null && sapLogger.isDebugEnabled())
				{
					debugOutput.append("\n item won't be copied");
				}
			}
		}
		return debugOutput;
	}

	/**
	 * @throws BusinessObjectException
	 */
	protected void createInBackend() throws BusinessObjectException
	{
		try
		{
			alreadyInitialized = true;
			getBackendService().createInBackend(transactionConfiguration, this);
		}
		catch (final BackendException beEcx)
		{
			// throw BO Exception attaching messages of the BE Exception in
			// order to display them on the UI
			sapLogger.traceThrowable(Severity.DEBUG, beEcx.getMessage(), beEcx);
			final BusinessObjectException boEcx = new BusinessObjectException();
			final MessageList msgList = beEcx.getMessageList();
			if (msgList != null)
			{
				for (int i = 0; i < msgList.size(); i++)
				{
					boEcx.addMessage(msgList.get(i));
				}
			}
			throw boEcx;
		}
	}

	@Override
	public boolean isExistingInBackend()
	{
		return (getHeader().getSalesDocNumber() != null) && !getHeader().getSalesDocNumber().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.interf. SalesDocumentBase#isDeterminationRequired()
	 */
	@Override
	public boolean isDeterminationRequired()
	{
		return determinationRequired;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isExternalToOrder()
	 */
	@Override
	public boolean isExternalToOrder()
	{
		return externalToOrder;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isGrossValueAvailable()
	 */
	@Override
	public boolean isGrossValueAvailable()
	{
		return grossValueAvailable;
	}

	@Override
	public boolean isMultipleAddressesSupported() throws BusinessObjectException
	{
		try
		{
			return getBackendService().isMultipleAddressesSupported();
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isNetValueAvailable()
	 */
	@Override
	public boolean isNetValueAvailable()
	{
		return netValueAvailable;
	}

	@Override
	public boolean isUpdateMissing()
	{
		return updateMissing;
	}

	/**
	 * Merges identical products (this is told from the product ID) and states whether changes have been done
	 *
	 * @return did we do changes?
	 * @throws CommunicationException
	 */
	@SuppressWarnings("squid:S134")
	protected boolean mergeIdenticalProducts() throws CommunicationException
	{

		/*
		 * Since the update of ERP backend does not read the updated data from the backend by default, we have to do it
		 * here explicitly because we need the data for the merge
		 */
		read();

		// items that will contain the sum of merging products
		final List<Item> mergeItems = new ArrayList();
		final List<TechKey> discardedItemKeys = new LinkedList();

		/*
		 * Second step: go through the map, find all list with more than one entries and merge them and store in duplicate
		 * list to remove them in step three from the document
		 */
		final Iterator<List<Item>> iterPossDuplicates = getDublicatesForItems().values().iterator();
		while (iterPossDuplicates.hasNext())
		{


			final List<Item> items = iterPossDuplicates.next();

			if (items.size() > 1)
			{

				Item merged = null; // item which we keep
				double sumQty = 0;
				int discarded = 0;

				final Iterator<Item> iterItems = items.iterator();
				while (iterItems.hasNext())
				{

					final Item item = iterItems.next();
					if (merged == null)
					{
						merged = item;
						sumQty = merged.getQuantity().doubleValue();
					}
					else
					{
						// merge only allowed combination
						// merge only if items do not exceed quantity limit
						final double itemQty = item.getQuantity().doubleValue();
						if (merged.isMergeSupported(item))
						{
							sumQty += itemQty;
							discardedItemKeys.add(item.getTechKey());
							++discarded;
						}
					}

				}

				mergingItem(mergeItems, merged, sumQty, discarded);
			}
		}

		/*
		 * Third step: remove duplicates from SalesDocument and update the SalesDocument in BE
		 */
		removeItems(discardedItemKeys.toArray(new TechKey[discardedItemKeys.size()]));
		return !mergeItems.isEmpty();
	}

	/**
	 * @param mergeItems
	 * @param merged
	 * @param sumQty
	 * @param discarded
	 */
	protected void mergingItem(final List<Item> mergeItems, final Item merged, final double sumQty, final int discarded)
	{
		if (discarded > 0)
		{
			merged.setQuantity(BigDecimal.valueOf(sumQty));
			mergeItems.add(merged);
		}
	}

	protected Map<String, List<Item>> getDublicatesForItems()
	{
		final Map<String, List<Item>> itemMap = new HashMap();
		final Iterator<Item> iterItem = getItemList().iterator();
		// First step: to run through the itemlist and store all items with same
		// productId and unit in lists
		while (iterItem.hasNext())
		{
			final Item toCheck = iterItem.next();
			// Child items do not form duplicates
			final String itemKey = createItemKey(toCheck);
			if (itemKey == null || !TechKey.isEmpty(toCheck.getParentId()) || toCheck.isConfigurable())
			{
				continue;
			}
			// toCheck is eligible for duplicates
			List<Item> possibleDuplicates = itemMap.get(itemKey); // find
			if (possibleDuplicates == null)
			{
				possibleDuplicates = new ArrayList();
				itemMap.put(itemKey, possibleDuplicates);
			}
			possibleDuplicates.add(toCheck);

		}
		return itemMap;
	}



	protected String createItemKey(final Item toCheck)
	{
		final TechKey productGUID = toCheck.getProductGuid();

		if ((null == productGUID) || productGUID.isInitial())
		{
			return null;
		}

		return productGUID.toString() + toCheck.getUnit() + toCheck.getPartnerListData().getAllToString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #read()
	 */
	@Override
	public void read() throws CommunicationException
	{
		this.read(false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #read(boolean)
	 */
	@Override
	public void read(final boolean force) throws CommunicationException
	{
		sapLogger.entering("read(boolean force)");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("Read is called with force=" + force);
		}

		try
		{
			// new version
			if (isDirty() || getHeader().isDirty() || force)
			{
				getBackendService().readFromBackend(this, transactionConfiguration, force);
				setDirty(false);
				getHeader().setDirty(false);
			}

		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #readForUpdate()
	 */
	@Override
	public void readForUpdate() throws CommunicationException
	{
		sapLogger.entering("readForUpdate()");
		// for backward compatibility
		this.readForUpdate(false);
		sapLogger.exiting();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #readForUpdate(boolean)
	 */
	@Override
	public void readForUpdate(final boolean force) throws CommunicationException
	{
		sapLogger.entering("readForUpdate(boolean force)");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("ReadForUpdate is called with force=" + force);
		}

		try
		{

			// read and lock from Backend
			if (isDirty() || getHeader().isDirty() || force)
			{
				getBackendService().readForUpdateFromBackend(this);
				getHeader().setDirty(false);
			}
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #
	 * removeItem(com.sap.wec.app.common.module.transaction.item.businessobject. interf.Item)
	 */
	@Override
	public void removeItem(final Item item) throws CommunicationException
	{
		sapLogger.entering("removeItem(Item item)");

		// now delete the item
		try
		{
			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemInBackend(this, item.getTechKey());
			afterDeleteItemInBackend(item.getTechKey());
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}


	/**
	 * @param techKey
	 */
	private void afterDeleteItemInBackend(final TechKey techKey)
	{
		if (getSalesDocumentHooks() != null)
		{
			for (final SalesDocumentHook salesDocumentImplHook : getSalesDocumentHooks())
			{
				salesDocumentImplHook.afterDeleteItemInBackend(techKey);
			}
		}

	}

	/** Abstract Dummy implementation of interface method */
	@Override
	abstract public boolean saveAndCommit() throws CommunicationException;

	@Override
	public void setDeterminationRequired(final boolean isDeterminationRequired)
	{
		this.determinationRequired = isDeterminationRequired;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #setExternalToOrder(boolean)
	 */
	@Override
	public void setExternalToOrder(final boolean isExternalToOrder)
	{
		this.externalToOrder = isExternalToOrder;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setGrossValueAvailable(boolean)
	 */
	@Override
	public void setGrossValueAvailable(final boolean isGrossValueAvailable)
	{
		this.grossValueAvailable = isGrossValueAvailable;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setNetValueAvailable(boolean)
	 */
	@Override
	public void setNetValueAvailable(final boolean isNetValueAvailable)
	{
		this.netValueAvailable = isNetValueAvailable;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setShipToList(java.util.List)
	 */
	@Override
	public void setShipToList(final List<ShipTo> shipToList)
	{
		this.shipToList = shipToList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setBillToList(java.util.List)
	 */
	@Override
	public void setBillToList(final List<BillTo> billToList)
	{
		this.billToList = billToList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setTransactionConfiguration(com.sap.wec.app.esales.module.transaction.
	 * businessobject.interf.TransactionConfiguration)
	 */
	@Override
	public void setTransactionConfiguration(final TransactionConfiguration transConf)
	{
		transactionConfiguration = transConf;
	}

	@Override
	public void setUpdateMissing(final boolean updateMissing)
	{
		this.updateMissing = updateMissing;
	}

	/**
	 * Returns a string representation of the object
	 *
	 * @return String representation
	 */
	@Override
	public String toString()
	{
		return super.toString() + " SalesDocumentImpl[shipToList=" + shipToList + "]";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #
	 * update(com.sap.wec.app.common.module.businesspartner.businessobject.interf .BusinessPartnerManager)
	 */
	@Override
	public void update() throws CommunicationException
	{
		sapLogger.entering("update()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("update(): transactionConfiguration = " + transactionConfiguration);
		}

		final List<TechKey> itemsToDelete = SalesDocumentUtil.determineItemsToDelete(getItemList());
		final List<TechKey> configurableItemsToRelease = determineConfigurableItemsToRelease();

		Item modifiedItem = null;
		Item addedItem = null;
		Item removedItem = null;
		// buffer previous messages
		MessageList bufferedMessages = this.getMessageList();

		try
		{
			/**
			 * Do some checks before the update is happening Update in backend can be skipped Specific BOs might redefine
			 * this method
			 */
			// this is only for compatibility reasons - the BE should
			// determine whether the bo is dirty or not
			setDirty(true);
			getHeader().setDirty(true);
			prepareItemsWithChangedProducts();

			modifiedItem = findModifiedItem();
			addedItem = findAddedItem();
			removedItem = findRemovedItem();

			// remove messages related to modified items
			bufferedMessages = SalesDocumentUtil.filterPreviousMessages(bufferedMessages, modifiedItem);

			// remove message related to added items
			bufferedMessages = SalesDocumentUtil.filterPreviousMessages(bufferedMessages, addedItem);

			// remove message related to removed items
			bufferedMessages = SalesDocumentUtil.filterPreviousMessages(bufferedMessages, removedItem);

			if (modifiedItem == null && addedItem == null && removedItem == null)
			{
				bufferedMessages = new MessageList();
			}
			updateInBackend(itemsToDelete, configurableItemsToRelease);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		catch (final BusinessObjectException e)
		{
			throw new CommunicationException("Update failed", e);

		}

		final boolean changesDone = mergeIdenticalProductsIfRequired();
		if (changesDone)
		{
			try
			{
				getBackendService().updateInBackend(this, transactionConfiguration);
			}
			catch (final BackendException e)
			{
				BusinessObjectHelper.splitException(e);
			}
			read();
		}
		pricesTraced = false;

		// enhance new messages with current product techKey
		SalesDocumentUtil.addTeckKeyToMessages(addedItem, getMessageList());

		// enhance new messages with current product techKey
		SalesDocumentUtil.addTeckKeyToMessages(modifiedItem, getMessageList());

		boolean isProdSubsMsg = false;
		List<String> arglst = null;
		for (final Message cartInfo : bufferedMessages)
		{
			//Don't add messages that are assigned explicitly to checkout
			if (cartInfo instanceof OrderMgmtMessage)
			{
				final OrderMgmtMessage orderMgmtMessage = (OrderMgmtMessage) cartInfo;

				if (("sapordermgmt.erp.ui.rfc.messages.label.v2167".equals(orderMgmtMessage.getResourceKey())))
				{
					final String[] arr = orderMgmtMessage.getResourceArgs();
					arglst = Arrays.asList(arr);
					isProdSubsMsg = true;
				}
			}
		}

		// add buffered messages to current product messages
		addBufferedMessages(bufferedMessages, isProdSubsMsg, arglst);

		// backend case
		sapLogger.exiting();
	}

	/**
	 * @param bufferedMessages
	 * @param isProdSubsMsg
	 * @param arglst
	 */
	private void addBufferedMessages(final MessageList bufferedMessages, final boolean isProdSubsMsg, final List<String> arglst)
	{
		if (!isProdSubsMsg)
		{
			addBufferedMessages(bufferedMessages);
		}
		else if (!this.getItemList().isEmpty())
		{
			for (final Item item : this.getItemList())
			{
				if ((arglst != null) && arglst.contains(item.getProductId()))
				{
					addBufferedMessages(bufferedMessages);
				}
			}

		}
	}

	/**
	 * @param itemsToDelete
	 * @param configurableItemsToRelease
	 * @throws BackendException
	 * @throws CommunicationException
	 * @throws BusinessObjectException
	 */
	protected void updateInBackend(final List<TechKey> itemsToDelete, final List<TechKey> configurableItemsToRelease)
			throws BackendException, CommunicationException, BusinessObjectException
	{
		getBackendService().updateInBackend(this, transactionConfiguration, itemsToDelete);
		afterUpdateItemInBackend(configurableItemsToRelease);
		updateMissing = false;

		final Map<TechKey, Message> messages = SalesDocumentUtil.checkQuantityUOM(getItemList(), converter);

		if (SalesDocumentUtil.applyAlternativeProducts(getItemList()) || !messages.isEmpty())
		{
			getBackendService().updateInBackend(this, transactionConfiguration);
		}

		for (final Entry<TechKey, Message> entry : messages.entrySet())
		{
			final Item itm = getItemList().get(entry.getKey());
			final Message message = entry.getValue();
			if (itm != null)
			{
				itm.addMessage(message);
			}
			else
			{
				getHeader().addMessage(message);
			}
		}
	}

	@SuppressWarnings("squid:S1698")
	private Item findModifiedItem()
	{

		final Iterator<Item> items = this.getItemList().iterator();

		while (items.hasNext())
		{
			final Item item = items.next();
			if ((item.getQuantity() != item.getOldQuantity()) && !item.isConfigurable())
			{
				return item;
			}
		}

		return null;
	}

	private Item findRemovedItem()
	{

		final Iterator<Item> items = this.getItemList().iterator();

		while (items.hasNext())
		{
			final Item item = items.next();

			if (item.isProductEmpty() && !TechKey.isEmpty(item.getTechKey()) && !item.isConfigurable())
			{
				return item;
			}
		}

		return null;
	}

	private Item findAddedItem()
	{

		final Iterator<Item> items = this.getItemList().iterator();

		while (items.hasNext())
		{
			final Item item = items.next();

			if (item.getTechKey().isInitial() && !item.isConfigurable())
			{
				return item;
			}
		}

		return null;
	}


	// merge buffered messages to new message
	private void addBufferedMessages(final MessageList bufferedMessges)
	{

		final MessageList newMessages = this.getMessageList();

		final Iterator<Message> messages = bufferedMessges.iterator();

		while (messages.hasNext())
		{
			newMessages.add(messages.next());
		}

		this.bobMessages = newMessages;
	}



	protected List<TechKey> determineConfigurableItemsToRelease()
	{

		return this.getItemList().stream()
				.filter(item -> item.isProductEmpty() && !TechKey.isEmpty(item.getTechKey()) && item.isConfigurable())
				.map(entry -> entry.getTechKey()).collect(Collectors.toList());

	}

	protected boolean mergeIdenticalProductsIfRequired() throws CommunicationException
	{
		// by default merge is de-activated for any document. In case of Basket
		// this method is overridden
		return false;
	}

	protected void prepareItemsWithChangedProducts()
	{
		final Iterator<Item> iterItem = getItemList().iterator();

		while (iterItem.hasNext())
		{
			SalesDocumentUtil.prepareItemWithChangedProduct(iterItem.next());
		}
	}

	@Override
	public TechKey getSoldToGuid()
	{
		final PartnerListEntry soldToData = getHeader().getPartnerList().getSoldToData();
		if (null == soldToData)
		{
			return null;
		}
		else
		{
			return soldToData.getPartnerTechKey();
		}
	}

	@Override
	public void setSoldToGuid(final TechKey techKeySoldTo)
	{
		setSoldToGuid(techKeySoldTo, "");
	}

	@Override
	public void setSoldToGuid(final TechKey techKeySoldTo, final String soldToId)
	{
		final PartnerListEntry entry = getHeader().getPartnerList().createPartnerListEntry(techKeySoldTo, soldToId);
		getHeader().getPartnerList().setSoldToData(entry);

	}

	@Override
	public void clearItemBuffer()
	{
		try
		{
			getBackendService().clearItemBuffer();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not establish BackendService", e);

		}
	}


	@Override
	public boolean hasPredecessorOfSpecificType(final DocumentType docType)
	{
		final List<ConnectedDocument> predecessors = getHeader().getPredecessorList();

		for (final ConnectedDocument predecessor : predecessors)
		{
			if (predecessor.getDocType().equals(docType))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void setInitialized(final boolean b)
	{
		alreadyInitialized = b;

	}

	@Override
	public boolean isInitialized()
	{
		return alreadyInitialized;
	}


	@Override
	public boolean isItemBasedAvailability()
	{
		try
		{
			return getBackendService().isItemBasedAvailability();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);

		}
	}

	@Override
	public void setConverter(final Converter converter)
	{
		this.converter = converter;
	}


	@Override
	public void validate() throws CommunicationException
	{
		try
		{
			getBackendService().validate(this);
			setDirty(false);
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
		}


	}



	/**
	 * Releases configuration sessions for all items provided
	 *
	 * @param itemsToDelete
	 *           List of item TechKeys
	 */
	protected void afterDeleteItemInBackend(final List<TechKey> itemsToDelete)
	{
		if (getSalesDocumentHooks() != null)
		{
			for (final SalesDocumentHook salesDocumentImplHook : getSalesDocumentHooks())
			{
				salesDocumentImplHook.afterDeleteItemInBackend(itemsToDelete);
			}
		}

	}

	/**
	 * Releases configuration sessions for all items provided
	 *
	 * @param itemsToDelete
	 *           List of item TechKeys
	 */
	protected void afterUpdateItemInBackend(final List<TechKey> itemsToDelete)
	{
		if (getSalesDocumentHooks() != null)
		{
			for (final SalesDocumentHook salesDocumentImplHook : getSalesDocumentHooks())
			{
				salesDocumentImplHook.afterUpdateItemInBackend(itemsToDelete);
			}
		}

	}

	@Override
	public void afterDeleteItemInBackend()
	{
		if (getSalesDocumentHooks() != null)
		{
			for (final SalesDocumentHook salesDocumentImplHook : getSalesDocumentHooks())
			{
				salesDocumentImplHook.afterDeleteItemInBackend(this.getItemList());
			}
		}
	}

	@Override
	public boolean isBackendDown()
	{
		if (isBackendWasDown())
		{
			return checkBackendNeverWasUp();
		}

		try
		{
			final boolean currentlyDown = getBackendService().isBackendDown();
			if (!currentlyDown)
			{
				setBackendWasUp(true);
				return false;
			}
			else
			{
				setBackendWasDown(true);
				return checkBackendNeverWasUp();
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);
		}
	}

	/**
	 * Checks whether backend was up previously and thus a runtime exception needs to be raised
	 *
	 * @return true if backend was never up before
	 */
	boolean checkBackendNeverWasUp()
	{
		if (isBackendWasUp())
		{
			sessionService.closeCurrentSession();
			throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
		}
		else
		{
			return true;
		}
	}



	void setBackendWasUp(final boolean b)
	{
		this.backendWasUp = b;
	}


	boolean isBackendWasUp()
	{
		return this.backendWasUp;
	}

	void setBackendWasDown(final boolean b)
	{
		this.backendWasDown = b;
	}


	boolean isBackendWasDown()
	{
		return this.backendWasDown;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the salesDocumentHooks
	 */
	public List<SalesDocumentHook> getSalesDocumentHooks()
	{
		return salesDocumentHooks;
	}

	/**
	 * @param salesDocumentHooks
	 *           the salesDocumentHooks to set
	 */
	public void setSalesDocumentHooks(final List<SalesDocumentHook> salesDocumentHooks)
	{
		this.salesDocumentHooks = salesDocumentHooks;
	}


}
