/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingfacades.asn.converters.populator;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.warehousing.asn.strategy.WarehouseSelectionForAsnStrategy;
import de.hybris.platform.warehousing.comment.WarehousingCommentService;
import de.hybris.platform.warehousing.data.comment.WarehousingCommentContext;
import de.hybris.platform.warehousing.data.comment.WarehousingCommentEventType;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;
import de.hybris.platform.warehousingfacades.asn.data.AsnEntryData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for populating {@link AsnData} into {@link AdvancedShippingNoticeModel}
 */
public class AsnModelPopulator implements Populator<AsnData, AdvancedShippingNoticeModel>
{
	protected static final String ASN_COMMENT_SUBJECT = "Advanced Shipping Notice";

	private AbstractConverter<AsnEntryData, AdvancedShippingNoticeEntryModel> asnEntryModelConverter;
	private WarehouseService warehouseService;
	private PointOfServiceService pointOfServiceService;
	private GuidKeyGenerator guidKeyGenerator;
	private WarehousingCommentService<AdvancedShippingNoticeModel> asnCommentService;
	private WarehouseSelectionForAsnStrategy warehouseSelectionForAsnStrategy;

	@Override
	public void populate(final AsnData source, final AdvancedShippingNoticeModel target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setExternalId(source.getExternalId());
			if (source.getPointOfServiceName() != null)
			{
				target.setPointOfService(getPointOfServiceService().getPointOfServiceForName(source.getPointOfServiceName()));
			}
			target.setReleaseDate(source.getReleaseDate());
			populateWarehouse(source, target);
			if (CollectionUtils.isNotEmpty(source.getAsnEntries()))
			{
				final List<AdvancedShippingNoticeEntryModel> asnEntries = source.getAsnEntries().stream()
						.map(getAsnEntryModelConverter()::convert).collect(Collectors.toList());
				asnEntries.stream().forEach(asnEntry -> asnEntry.setAsn(target));
				target.setAsnEntries(asnEntries);
			}
			populateComment(source, target);
		}
	}

	/**
	 * Populates {@link WarehouseModel} from {@link AsnData}
	 *
	 * @param target
	 * 		the {@link AdvancedShippingNoticeModel}
	 * @param source
	 * 		the {@link AsnData}
	 */
	protected void populateWarehouse(final AsnData source, final AdvancedShippingNoticeModel target)
	{
		final WarehouseModel warehouse;
		if (StringUtils.isNotEmpty(source.getWarehouseCode()))
		{
			warehouse = getWarehouseService().getWarehouseForCode(source.getWarehouseCode());
		}
		else
		{
			warehouse = getWarehouseSelectionForAsnStrategy().getDefaultWarehouse(target);
			if (warehouse == null)
			{
				throw new ConversionException(
						String.format("Unable to determine warehouse for ASN with externalId=%s", source.getExternalId()));
			}
		}
		target.setWarehouse(warehouse);
	}

	/**
	 * Populates {@link CommentModel} from {@link AsnData}
	 *
	 * @param source
	 * 		the {@link AsnData}
	 * @param target
	 * 		the {@link AdvancedShippingNoticeModel}
	 */
	protected void populateComment(final AsnData source, final AdvancedShippingNoticeModel target)
	{
		if (StringUtils.isNotEmpty(source.getComment()))
		{
			target.setComments(new ArrayList<>());
			final WarehousingCommentContext commentContext = new WarehousingCommentContext();
			commentContext.setCommentType(WarehousingCommentEventType.CREATE_ASN_COMMENT);
			commentContext.setItem(target);
			commentContext.setSubject(ASN_COMMENT_SUBJECT);
			commentContext.setText(source.getComment());
			final String code = "asn_" + getGuidKeyGenerator().generate().toString();
			target.setComments(Lists.newArrayList(getAsnCommentService().createAndSaveComment(commentContext, code)));
		}
	}

	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected GuidKeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final GuidKeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	protected WarehousingCommentService<AdvancedShippingNoticeModel> getAsnCommentService()
	{
		return asnCommentService;
	}

	@Required
	public void setAsnCommentService(final WarehousingCommentService<AdvancedShippingNoticeModel> asnCommentService)
	{
		this.asnCommentService = asnCommentService;
	}

	protected AbstractConverter<AsnEntryData, AdvancedShippingNoticeEntryModel> getAsnEntryModelConverter()
	{
		return asnEntryModelConverter;
	}

	@Required
	public void setAsnEntryModelConverter(
			final AbstractConverter<AsnEntryData, AdvancedShippingNoticeEntryModel> asnEntryModelConverter)
	{
		this.asnEntryModelConverter = asnEntryModelConverter;
	}

	protected WarehouseSelectionForAsnStrategy getWarehouseSelectionForAsnStrategy()
	{
		return warehouseSelectionForAsnStrategy;
	}

	@Required
	public void setWarehouseSelectionForAsnStrategy(final WarehouseSelectionForAsnStrategy warehouseSelectionForAsnStrategy)
	{
		this.warehouseSelectionForAsnStrategy = warehouseSelectionForAsnStrategy;
	}
}
