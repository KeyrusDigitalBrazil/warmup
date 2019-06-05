package br.com.keyrus.warmup.facades.product.populator;

import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.core.stamp.service.StampService;
import br.com.keyrus.warmup.facades.stamp.data.StampData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

public class CustomProductPopulator extends ProductPopulator {

    private Converter<StampModel, StampData> stampConverter;
    private StampService stampService;

    @Override
    public void populate(ProductModel source, ProductData target) {

        super.populate(source, target);

        final List<StampModel> stamps = stampService.getBestProductStamps(source);

        target.setStamps(stamps != null ? stampConverter.convertAll(stamps) : null);
    }

    public void setStampConverter(Converter<StampModel, StampData> stampConverter) {
        this.stampConverter = stampConverter;
    }

    public void setStampService(StampService stampService) {
        this.stampService = stampService;
    }
}
