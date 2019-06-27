package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.BooleanUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Populates {@link ProductData#soldIndividually} field of product DTO.
 */
public class ProductSoldIndividuallyPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{

	@Override
	public void populate(SOURCE productModel, TARGET productData) throws ConversionException
	{
		validateParameterNotNullStandardMessage("productData", productData);
		validateParameterNotNullStandardMessage("productModel", productModel);

		productData.setSoldIndividually(BooleanUtils.toBoolean(productModel.getSoldIndividually()));
	}
}
