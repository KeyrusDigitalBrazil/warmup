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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ProductCsticAndValueParameterProviderService;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.impl.AbstractCockpitEditorRenderer;


/**
 * Abstract implementation of the characteristic and characteristic value editor in the product configuration rules
 */
public abstract class AbstractProductConfigRuleParameterEditor extends AbstractCockpitEditorRenderer<Object>
{
	@Resource(name = "productCsticAndValueParameterProviderService")
	private ProductCsticAndValueParameterProviderService parameterProviderService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public void render(final Component parent, final EditorContext<Object> context, final EditorListener<Object> listener)
	{
		Validate.notNull("All parameters are mandatory", parent, context, listener);

		final ListModelList<Object> model = prepareModel(context);

		final Combobox box = createCombobox();
		parent.appendChild(box);

		box.setModel(model);
		box.setReadonly(false);
		box.setAutodrop(true);
		box.setDisabled(!context.isEditable());

		box.setItemRenderer(new ProductConfigParameterRenderer());
		box.addEventListener(Events.ON_CHANGE, new ProductConfigParameterOnChangeEventListener(listener));
		box.addEventListener(Events.ON_OPEN, new ProductConfigParameterOnOpenEventListener(listener, box));
	}

	protected ListModelList<Object> prepareModel(final EditorContext<Object> context)
	{
		final Optional<Object> initialValue = Optional.ofNullable(context.getInitialValue());

		final List<Object> allValues = getPossibleValues(context);

		// Add initialValue to allValues, if not already contained
		initialValue.filter(value -> !allValues.contains(value)).ifPresent(value -> allValues.add(0, value));

		final ListModelList<Object> model = new ListModelList<>(allValues);
		initialValue.ifPresent(value -> model.setSelection(Collections.singletonList(value)));

		return model;
	}

	protected Combobox createCombobox()
	{
		return new Combobox();
	}

	protected abstract List<Object> getPossibleValues(final EditorContext<Object> context);


	protected String useBaseProductCodeForChangeableVariant(final String productCode)
	{
		final String query = "SELECT {pk} from {ERPVariantProduct} WHERE {code} = ?productCode AND {changeable} = true";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query,
				Collections.singletonMap("productCode", productCode));
		final SearchResult<ERPVariantProductModel> searchResult = flexibleSearchService.search(searchQuery);
		final List<ERPVariantProductModel> variantProductModelList = searchResult.getResult();
		if (CollectionUtils.isNotEmpty(variantProductModelList))
		{
			final ProductModel baseProduct = variantProductModelList.get(0).getBaseProduct();
			if (baseProduct != null)
			{
				return baseProduct.getCode();
			}
		}
		return productCode;
	}

	/**
	 * @return the parameterProviderService
	 */
	protected ProductCsticAndValueParameterProviderService getParameterProviderService()
	{
		return parameterProviderService;
	}

	/**
	 * @param parameterProviderService
	 *           the parameterProviderService to set
	 */
	public void setParameterProviderService(final ProductCsticAndValueParameterProviderService parameterProviderService)
	{
		this.parameterProviderService = parameterProviderService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
