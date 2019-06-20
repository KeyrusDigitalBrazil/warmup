package br.com.keyrus.warmup.core.service;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface StampService {

        void createStamp(final File file);

        List<StampModel> listProductStamps(ProductModel product);
}
