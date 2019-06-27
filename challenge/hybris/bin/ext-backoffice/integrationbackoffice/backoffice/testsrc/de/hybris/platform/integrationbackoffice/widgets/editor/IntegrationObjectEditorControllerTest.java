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
package de.hybris.platform.integrationbackoffice.widgets.editor;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationObjectEditorControllerTest {

    private IntegrationObjectEditorController controller = new IntegrationObjectEditorController();

    @Mock
    private Tree tree;
    @Mock
    private Treeitem treeitem;
    @Mock
    private Listitem listitem;
    @Mock
    private Component uniqueCheckboxComponent;
    @Mock
    private Component autocreateCheckboxComponent;
    @Mock
    private Checkbox uniqueCheckbox;
    @Mock
    private Checkbox autocreateCheckbox;

    @Test
    public void testUpdateAttribute() {
        final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
        attributeDescriptorModel.setQualifier("StockLevel");
        attributeDescriptorModel.setAttributeType(new TypeModel());

        final ComposedTypeModel key = new ComposedTypeModel();
        final ListItemDTO dto = new ListItemDTO(attributeDescriptorModel, false, false, false, false);

        final Map<ComposedTypeModel, List<ListItemDTO>> attributesMap = new HashMap<>();
        attributesMap.put(key, Collections.singletonList(dto));

        controller.setAttributesMap(attributesMap);
        controller.setComposedTypeTree(tree);

        final List<Component> children = new ArrayList<>();
        children.add(null);
        children.add(null);
        children.add(uniqueCheckboxComponent);
        children.add(autocreateCheckboxComponent);

        when(tree.getSelectedItem()).thenReturn(treeitem);
        when(treeitem.getValue()).thenReturn(key);

        when(listitem.getValue()).thenReturn(dto);
        when(listitem.getLabel()).thenReturn("StockLevel");

        when(listitem.getChildren()).thenReturn(children);
        when(uniqueCheckboxComponent.getFirstChild()).thenReturn(uniqueCheckbox);
        when(autocreateCheckboxComponent.getFirstChild()).thenReturn(autocreateCheckbox);

        when(listitem.isSelected()).thenReturn(true);
        when(uniqueCheckbox.isChecked()).thenReturn(true);
        when(autocreateCheckbox.isChecked()).thenReturn(true);

        assertFalse(dto.isSelected());
        assertFalse(dto.isCustomUnique());
        assertFalse(dto.isAutocreate());

        controller.updateAttribute(listitem);

        final ListItemDTO actual = attributesMap.get(key).get(0);
        assertTrue(actual.isSelected());
        assertTrue(actual.isCustomUnique());
        assertTrue(actual.isAutocreate());
    }

}
