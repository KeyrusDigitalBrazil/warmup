package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EditorUtilsUnitTest
{
    private static final String COLLECTION_TYPE = "CollectionItem";

    @Mock
    private ReadService readService;

    @Before
    public void setup() {
        doReturn(true).when(readService).isCollectionType(COLLECTION_TYPE);
    }

    @Test
    public void convertIntegrationObjectToDTOMap() {
        final IntegrationObjectModel object = integrationObject(
                item("TypeSystemUnique",
                        attribute(descriptor("String", true), false, false),
                        attribute(descriptor("ReferencedItem", false), false, true),
                        attribute(descriptor(COLLECTION_TYPE, false), false, false)),
                item("CustomUnique",
                        attribute(descriptor("String", false), true, false),
                        attribute(descriptor("Integer", null), true, false)),
                item("DoubleUnique",
                        attribute(descriptor("String", true), true, false)),
                item("NonUnique",
                        attribute(descriptor("Integer", null), null, null)));

        final Map<ComposedTypeModel, List<ListItemDTO>> dtoMap = EditorUtils.convertIntegrationObjectToDTOMap(readService, object);

        assertThat(dtoMap.keySet())
                .extracting("code").containsExactlyInAnyOrder("TypeSystemUnique", "CustomUnique", "DoubleUnique", "NonUnique");
        assertThat(extractDtoForItemType(dtoMap, "TypeSystemUnique"))
                .extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected", "collection", "autocreate")
                .containsExactlyInAnyOrder(
                        tuple("String", false, true, false, false),
                        tuple("ReferencedItem", false, true, false, true),
                        tuple(COLLECTION_TYPE, false, true, true, false));
        assertThat(extractDtoForItemType(dtoMap, "CustomUnique"))
                .extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected", "collection", "autocreate")
                .containsOnly(
                        tuple("String", true, true, false, false),
                        tuple("Integer", true, true, false, false));
        assertThat(extractDtoForItemType(dtoMap, "DoubleUnique"))
                .extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected", "collection", "autocreate")
                .containsExactlyInAnyOrder(tuple("String", false, true, false, false));
        assertThat(extractDtoForItemType(dtoMap, "NonUnique"))
                .extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected", "collection", "autocreate")
                .containsExactlyInAnyOrder(tuple("Integer", false, true, false, false));
    }

    private IntegrationObjectModel integrationObject(final IntegrationObjectItemModel... items) {
        final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
        doReturn(asSet(items)).when(object).getItems();
        return object;
    }

    private IntegrationObjectItemModel item(final String code, final IntegrationObjectItemAttributeModel... attributes) {
        final IntegrationObjectItemModel item = mock(IntegrationObjectItemModel.class);
        doReturn(composedTypeModel(code)).when(item).getType();
        doReturn(asSet(attributes)).when(item).getAttributes();
        return item;
    }

    private ComposedTypeModel composedTypeModel(final String code) {
        final ComposedTypeModel model = mock(ComposedTypeModel.class);
        doReturn(code).when(model).getCode();
        return model;
    }

    private IntegrationObjectItemAttributeModel attribute(final AttributeDescriptorModel descriptor, final Boolean unique, final Boolean create) {
        final IntegrationObjectItemAttributeModel attribute = mock(IntegrationObjectItemAttributeModel.class);
        doReturn(descriptor).when(attribute).getAttributeDescriptor();
        doReturn(unique).when(attribute).getUnique();
        doReturn(create).when(attribute).getAutoCreate();
        return attribute;
    }

    private AttributeDescriptorModel descriptor(final String type, final Boolean unique) {
        final TypeModel typeModel = COLLECTION_TYPE.equals(type)
                ? collectionType(type)
                : typeModel(type);
        final AttributeDescriptorModel descriptor = mock(AttributeDescriptorModel.class);
        doReturn(typeModel).when(descriptor).getAttributeType();
        doReturn(unique).when(descriptor).getUnique();
        return  descriptor;
    }

    private CollectionTypeModel collectionType(final String type) {
        final CollectionTypeModel model = mock(CollectionTypeModel.class);
        doReturn(type).when(model).getItemtype();
        doReturn(typeModel(type)).when(model).getElementType();
        return model;
    }

    private TypeModel typeModel(final String type) {
        final TypeModel model = mock(TypeModel.class);
        doReturn(type).when(model).getCode();
        doReturn(type).when(model).getItemtype();
        return model;
    }

    @SafeVarargs
    private final <T> Set<T> asSet(final T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    private List<ListItemDTO> extractDtoForItemType(final Map<ComposedTypeModel, List<ListItemDTO>> map, final String type) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().getCode().equals(type))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());
    }
}