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
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.testframework.Assert.assertEquals;

@SuppressWarnings("unchecked")
@UnitTest
public class EditorValidatorUnitTest {

    private final ComposedTypeModel c1 = new ComposedTypeModel();
    private final ComposedTypeModel c2 = new ComposedTypeModel();
    private final ComposedTypeModel c3 = new ComposedTypeModel();

    private final AttributeDescriptorModel a1 = new AttributeDescriptorModel();
    private final AttributeDescriptorModel baseADM = new AttributeDescriptorModel();

    @Before
    public void setup() {
        c1.setCode("Comp1");
        c2.setCode("Comp2");
        c3.setCode("Comp3");

        final TypeModel t = new TypeModel();

        t.setCode("");
        a1.setUnique(true);
        a1.setAttributeType(t);
        baseADM.setAttributeType(t);
        baseADM.setUnique(false);
    }

    @Test
    public void validateDefintionsValidTest() {
        final Map<ComposedTypeModel, List<ListItemDTO>> validMap = new HashMap<>();

        final ListItemDTO itemSelect = new ListItemDTO(baseADM, false, true, false, false);
        final ListItemDTO itemNotSelect = new ListItemDTO(baseADM, false, false, false, false);

        final List<ListItemDTO> l1 = new ArrayList<>();
        final List<ListItemDTO> l2 = new ArrayList<>();
        final List<ListItemDTO> l3 = new ArrayList<>();

        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemSelect);

        l2.add(itemNotSelect);
        l2.add(itemNotSelect);
        l2.add(itemSelect);
        l2.add(itemSelect);

        l3.add(itemSelect);
        l3.add(itemNotSelect);
        l3.add(itemNotSelect);
        l3.add(itemSelect);

        validMap.put(c1, l1);
        validMap.put(c2, l2);
        validMap.put(c3, l3);

        assertEquals("", EditorValidator.validateDefinitions(validMap));

    }

    @Test
    public void validateDefinitionsInvalidTest() {
        final Map<ComposedTypeModel, List<ListItemDTO>> invalidMap = new HashMap<>();

        final ListItemDTO itemSelect = new ListItemDTO(baseADM, false, true, false, false);
        final ListItemDTO itemNotSelect = new ListItemDTO(baseADM, false, false, false, false);

        final List<ListItemDTO> l1 = new ArrayList<>();
        final List<ListItemDTO> l2 = new ArrayList<>();
        final List<ListItemDTO> l3 = new ArrayList<>();

        //l1 has nothing

        l2.add(itemNotSelect);
        l2.add(itemNotSelect);
        l2.add(itemSelect);
        l2.add(itemSelect);

        l3.add(itemSelect);
        l3.add(itemNotSelect);
        l3.add(itemNotSelect);
        l3.add(itemSelect);

        invalidMap.put(c1, l1);
        invalidMap.put(c2, l2);
        invalidMap.put(c3, l3);

        assertEquals("Comp1", EditorValidator.validateDefinitions(invalidMap));
    }

    @Test
    public void validateHasKeyValidTest() {
        final Map<ComposedTypeModel, List<ListItemDTO>> validMap = new HashMap<>();

        final ListItemDTO itemUnique = new ListItemDTO(a1, false, true, false, false);
        final ListItemDTO itemCustomUnique = new ListItemDTO(baseADM, true, true, false, false);
        final ListItemDTO itemSelect = new ListItemDTO(baseADM, false, true, false, false);
        final ListItemDTO itemNotSelect = new ListItemDTO(baseADM, false, false, false, false);

        final List<ListItemDTO> l1 = new ArrayList<>();
        final List<ListItemDTO> l2 = new ArrayList<>();
        final List<ListItemDTO> l3 = new ArrayList<>();

        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemUnique);

        l2.add(itemNotSelect);
        l2.add(itemNotSelect);
        l2.add(itemSelect);
        l2.add(itemSelect);
        l2.add(itemCustomUnique);

        l3.add(itemSelect);
        l3.add(itemNotSelect);
        l3.add(itemNotSelect);
        l3.add(itemSelect);
        l3.add(itemUnique);
        l3.add(itemCustomUnique);

        validMap.put(c1, l1);
        validMap.put(c2, l2);
        validMap.put(c3, l3);

        assertEquals("", EditorValidator.validateHasKey(validMap));
    }

    @Test
    public void validateHasKeyInvalidTest() {
        final Map<ComposedTypeModel, List<ListItemDTO>> validMap = new HashMap<>();

        final ListItemDTO itemUnique = new ListItemDTO(a1, false, true, false, false);
        final ListItemDTO itemCustomUnique = new ListItemDTO(baseADM, true, true, false, false);
        final ListItemDTO itemSelect = new ListItemDTO(baseADM, false, true, false, false);
        final ListItemDTO itemNotSelect = new ListItemDTO(baseADM, false, false, false, false);

        final List<ListItemDTO> l1 = new ArrayList<>();
        final List<ListItemDTO> l2 = new ArrayList<>();
        final List<ListItemDTO> l3 = new ArrayList<>();

        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemNotSelect);
        l1.add(itemUnique);

        l2.add(itemNotSelect);
        l2.add(itemNotSelect);
        l2.add(itemSelect);
        l2.add(itemSelect);

        l3.add(itemSelect);
        l3.add(itemNotSelect);
        l3.add(itemNotSelect);
        l3.add(itemSelect);
        l3.add(itemUnique);
        l3.add(itemCustomUnique);

        validMap.put(c1, l1);
        validMap.put(c2, l2);
        validMap.put(c3, l3);

        assertEquals("Comp2", EditorValidator.validateHasKey(validMap));
    }
}
