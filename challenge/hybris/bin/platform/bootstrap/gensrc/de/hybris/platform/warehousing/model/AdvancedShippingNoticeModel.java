/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 26/06/2019 16:55:50                         ---
 * ----------------------------------------------------------------
 *  
 * [y] hybris Platform
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.model;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import java.util.Date;
import java.util.List;

/**
 * Generated model class for type AdvancedShippingNotice first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class AdvancedShippingNoticeModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "AdvancedShippingNotice";
	
	/**<i>Generated relation code constant for relation <code>AdvancedShippingNoticeEntry2AsnRelation</code> defining source attribute <code>asnEntries</code> in extension <code>warehousing</code>.</i>*/
	public static final String _ADVANCEDSHIPPINGNOTICEENTRY2ASNRELATION = "AdvancedShippingNoticeEntry2AsnRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.status</code> attribute defined at extension <code>warehousing</code>. */
	public static final String STATUS = "status";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.externalId</code> attribute defined at extension <code>warehousing</code>. */
	public static final String EXTERNALID = "externalId";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.internalId</code> attribute defined at extension <code>warehousing</code>. */
	public static final String INTERNALID = "internalId";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.releaseDate</code> attribute defined at extension <code>warehousing</code>. */
	public static final String RELEASEDATE = "releaseDate";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.warehouse</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSE = "warehouse";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.pointOfService</code> attribute defined at extension <code>warehousing</code>. */
	public static final String POINTOFSERVICE = "pointOfService";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNotice.asnEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ASNENTRIES = "asnEntries";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public AdvancedShippingNoticeModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public AdvancedShippingNoticeModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _externalId initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _internalId initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _pointOfService initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _releaseDate initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public AdvancedShippingNoticeModel(final String _externalId, final String _internalId, final PointOfServiceModel _pointOfService, final Date _releaseDate)
	{
		super();
		setExternalId(_externalId);
		setInternalId(_internalId);
		setPointOfService(_pointOfService);
		setReleaseDate(_releaseDate);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _externalId initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _internalId initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _pointOfService initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 * @param _releaseDate initial attribute declared by type <code>AdvancedShippingNotice</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public AdvancedShippingNoticeModel(final String _externalId, final String _internalId, final ItemModel _owner, final PointOfServiceModel _pointOfService, final Date _releaseDate)
	{
		super();
		setExternalId(_externalId);
		setInternalId(_internalId);
		setOwner(_owner);
		setPointOfService(_pointOfService);
		setReleaseDate(_releaseDate);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.asnEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the asnEntries
	 */
	@Accessor(qualifier = "asnEntries", type = Accessor.Type.GETTER)
	public List<AdvancedShippingNoticeEntryModel> getAsnEntries()
	{
		return getPersistenceContext().getPropertyValue(ASNENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.externalId</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the externalId - ASN id coming from external provider
	 */
	@Accessor(qualifier = "externalId", type = Accessor.Type.GETTER)
	public String getExternalId()
	{
		return getPersistenceContext().getPropertyValue(EXTERNALID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.internalId</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the internalId - Internal ASN id which is generated by NumberSeries
	 */
	@Accessor(qualifier = "internalId", type = Accessor.Type.GETTER)
	public String getInternalId()
	{
		return getPersistenceContext().getPropertyValue(INTERNALID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.pointOfService</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the pointOfService
	 */
	@Accessor(qualifier = "pointOfService", type = Accessor.Type.GETTER)
	public PointOfServiceModel getPointOfService()
	{
		return getPersistenceContext().getPropertyValue(POINTOFSERVICE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.releaseDate</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the releaseDate - Release date for asn entries
	 */
	@Accessor(qualifier = "releaseDate", type = Accessor.Type.GETTER)
	public Date getReleaseDate()
	{
		return getPersistenceContext().getPropertyValue(RELEASEDATE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.status</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the status - Determines the status of given ASN
	 */
	@Accessor(qualifier = "status", type = Accessor.Type.GETTER)
	public AsnStatus getStatus()
	{
		return getPersistenceContext().getPropertyValue(STATUS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNotice.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.GETTER)
	public WarehouseModel getWarehouse()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSE);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.asnEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the asnEntries
	 */
	@Accessor(qualifier = "asnEntries", type = Accessor.Type.SETTER)
	public void setAsnEntries(final List<AdvancedShippingNoticeEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(ASNENTRIES, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.externalId</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the externalId - ASN id coming from external provider
	 */
	@Accessor(qualifier = "externalId", type = Accessor.Type.SETTER)
	public void setExternalId(final String value)
	{
		getPersistenceContext().setPropertyValue(EXTERNALID, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.internalId</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the internalId - Internal ASN id which is generated by NumberSeries
	 */
	@Accessor(qualifier = "internalId", type = Accessor.Type.SETTER)
	public void setInternalId(final String value)
	{
		getPersistenceContext().setPropertyValue(INTERNALID, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.pointOfService</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the pointOfService
	 */
	@Accessor(qualifier = "pointOfService", type = Accessor.Type.SETTER)
	public void setPointOfService(final PointOfServiceModel value)
	{
		getPersistenceContext().setPropertyValue(POINTOFSERVICE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.releaseDate</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the releaseDate - Release date for asn entries
	 */
	@Accessor(qualifier = "releaseDate", type = Accessor.Type.SETTER)
	public void setReleaseDate(final Date value)
	{
		getPersistenceContext().setPropertyValue(RELEASEDATE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.status</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the status - Determines the status of given ASN
	 */
	@Accessor(qualifier = "status", type = Accessor.Type.SETTER)
	public void setStatus(final AsnStatus value)
	{
		getPersistenceContext().setPropertyValue(STATUS, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNotice.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.SETTER)
	public void setWarehouse(final WarehouseModel value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSE, value);
	}
	
}
