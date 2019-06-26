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

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.services.WriteService;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorBlacklists;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorTrimmer;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorValidator;
import de.hybris.platform.integrationbackoffice.widgets.modals.CreateIntegrationObjectModalData;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import org.fest.util.Arrays;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.convertIntegrationObjectToDTOMap;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createListItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createTreeItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.filterAttributesForAttributesMap;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.filterAttributesForTree;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getCollectionAttributes;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getRootIntegrationObjectItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.isInTreeChildren;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.updateDTOs;

/**
 * Controls the functionality of the editor
 */
public final class IntegrationObjectEditorController extends DefaultWidgetController {

    @WireVariable
    private transient WriteService writeService;
    @WireVariable
    private transient ReadService readService;
    @WireVariable
    private transient CockpitEventQueue cockpitEventQueue;

    private Tree composedTypeTree;
    private Listbox attributesListBox;

    private Deque<ComposedTypeModel> ancestors;
    private static final int MAX_DEPTH = 5;
    private int currentDepth;

    private transient Map<ComposedTypeModel, List<ListItemDTO>> attributesMap;

    private IntegrationObjectModel selectedIntegrationObject;

    public void setComposedTypeTree(final Tree composedTypeTree) {
        this.composedTypeTree = composedTypeTree;
    }

    public void setAttributesListBox(final Listbox attributesListBox) {
        this.attributesListBox = attributesListBox;
    }

    public void setAttributesMap(final Map<ComposedTypeModel, List<ListItemDTO>> attributesMap) {
        this.attributesMap = attributesMap;
    }

    @Override
    public void initialize(final Component component) {
        super.initialize(component);
    }

    @GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
    public void handleIntegrationObjectCreatedEvent(final CockpitEvent event) {
        if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance)) {
            clearSelectedIntegrationObject();
        }
    }

    @SocketEvent(socketId = "integrationObject")
    public void loadIntegrationObject(final IntegrationObjectModel integrationObject) {
        selectedIntegrationObject = integrationObject;
        sendOutput("sendActionState", true);
        final IntegrationObjectItemModel root = getRootIntegrationObjectItem(selectedIntegrationObject);
        if (root != null) {
            createTree(root.getType(), convertIntegrationObjectToDTOMap(readService, selectedIntegrationObject));
            final Map<ComposedTypeModel, List<ListItemDTO>> trimmedMap = EditorTrimmer.trimMap(readService, attributesMap, composedTypeTree);
            if (!("").equals(EditorValidator.validateHasKey(trimmedMap))) {
                Messagebox.show(getLabel("integrationbackoffice.editMode.warning.title.serviceLoadedNeedsFurtherConfig"),
                        getLabel("integrationbackoffice.editMode.warning.msg.serviceLoadedNeedsFurtherConfig"),
                        1, Messagebox.EXCLAMATION);
            }
        } else {
            clearTree();
            Messagebox.show(getLabel("integrationbackoffice.editMode.warning.msg.invalidObjectLoaded"),
                    getLabel("integrationbackoffice.editMode.warning.title.invalidObjectLoaded"),
                    1, Messagebox.EXCLAMATION);
        }
    }

    @SocketEvent(socketId = "createIntegrationObjectEvent")
    public void saveNewIntegrationObject(final CreateIntegrationObjectModalData data) {
        createTree(data.getComposedTypeModel(), Collections.emptyMap());
        selectedIntegrationObject = writeService.createIntegrationObject(data.getName(), data.getType());
        final Map<ComposedTypeModel, List<ListItemDTO>> trimmedMap = EditorTrimmer.trimMap(readService, attributesMap, composedTypeTree);
        final IntegrationObjectModel ioModel = writeService.createDefinitions(selectedIntegrationObject, trimmedMap);
        writeService.persistDefinitons(ioModel);
        cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECT_CREATED_EVENT, selectedIntegrationObject, null));

        if (!("").equals(EditorValidator.validateHasKey(trimmedMap))) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.warning.title.serviceCreatedNeedsFurtherConfig"),
                    getLabel("integrationbackoffice.editMode.warning.msg.serviceCreatedNeedsFurtherConfig"),
                    1, Messagebox.EXCLAMATION);
        } else {
            Messagebox.show(getLabel("integrationbackoffice.editMode.info.msg.serviceCreated"),
                    getLabel("integrationbackoffice.editMode.info.title.serviceCreated"),
                    1, Messagebox.INFORMATION);
        }
    }

    @SocketEvent(socketId = "saveEvent")
    public void updateIntegrationObject(final String message) {
        final Map<ComposedTypeModel, List<ListItemDTO>> trimmedAttributesMap = EditorTrimmer.trimMap(readService, attributesMap, composedTypeTree);
        if (validation(trimmedAttributesMap)) {
            final IntegrationObjectModel ioModel = writeService.createDefinitions(selectedIntegrationObject, trimmedAttributesMap);
            // No problems with IO
            if (getRootIntegrationObjectItem(ioModel) != null) {
                Messagebox.show(getLabel("integrationbackoffice.editMode.info.msg.saveConfirmation"),
                        getLabel("integrationbackoffice.editMode.info.title.saveConfirmation"),
                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                        new String[]{getLabel("integrationbackoffice.editMode.button.saveDefinition")},
                        null, null, clickEvent -> {
                            if (clickEvent.getButton() == Messagebox.Button.OK) {
                                writeService.persistDefinitons(ioModel);
                                cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT,
                                        selectedIntegrationObject, null));
                            }
                        });
            }else {
                Messagebox.show(getLabel("integrationbackoffice.editMode.warning.msg.saveConfirmation"),
                        getLabel("integrationbackoffice.editMode.warning.title.saveConfirmation"),
                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                        new String[]{getLabel("integrationbackoffice.editMode.button.saveDefinition")},
                        null, null, clickEvent -> {
                            if (clickEvent.getButton() == Messagebox.Button.OK) {
                                writeService.persistDefinitons(ioModel);
                                cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT,
                                        selectedIntegrationObject, null));
                            }
                        });
            }
        }
    }

    @SocketEvent(socketId = "refreshEvent")
    public void refreshButtonOnClick(final String message) {
        clearSelectedIntegrationObject();
    }

    @SocketEvent(socketId = "receiveDelete")
    public void deleteActionOnPerform() {
        if (selectedIntegrationObject != null) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.info.msg.deleteConfirmation", Arrays.array(selectedIntegrationObject.getCode())),
                    getLabel("integrationbackoffice.editMode.info.title.deleteConfirmation"),
                    new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                    null, null, null, clickEvent -> {
                        if (clickEvent.getButton() == Messagebox.Button.OK) {
                            writeService.deleteIntegrationObject(selectedIntegrationObject);
                            cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_DELETED_EVENT,
                                    selectedIntegrationObject, null));
                        }
                    });
        }
    }

    @SocketEvent(socketId = "metadataModalEvent")
    public void sendCurrentIntegrationObject(final String message) {
        sendOutput("openMetadataViewer", selectedIntegrationObject);
    }

    @ViewEvent(componentID = "composedTypeTree", eventName = Events.ON_SELECT)
    public void composedTypeTreeOnSelect() {
        populateListBox(composedTypeTree.getSelectedItem().getValue());
    }

    private void clearSelectedIntegrationObject() {
        clearTree();
        selectedIntegrationObject = null;
    }

    private boolean validation(final Map<ComposedTypeModel, List<ListItemDTO>> trimmedAttributesMap) {
        final String VALIDATION_MESSAGE_TITLE = getLabel("integrationbackoffice.editMode.error.title.validation");
        String validationError;
        validationError = EditorValidator.validateDefinitions(trimmedAttributesMap);
        if (!("").equals(validationError)) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.definitionValidation", Arrays.array(validationError)),
                    VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
            return false;
        }
        validationError = EditorValidator.validateHasKey(trimmedAttributesMap);
        if (!("").equals(validationError)) {
            Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.uniqueValidation", Arrays.array(validationError)),
                    VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
            return false;
        }
        return true;
    }

    private void createTree(final ComposedTypeModel rootType, final Map<ComposedTypeModel, List<ListItemDTO>> existingDefinitions) {
        clearTree();
        ancestors = new ArrayDeque<>();
        ancestors.push(rootType);

        final Treechildren rootLevel = composedTypeTree.getTreechildren();
        final Treeitem rootTreeItem = createTreeItem(null, rootType, true);
        rootLevel.appendChild(rootTreeItem);

        attributesMap = new HashMap<>();
        populateAttributesMap(rootType);

        currentDepth = 0;
        populateTree(rootTreeItem, existingDefinitions);
        loadExistingDefinitions(existingDefinitions);

        rootTreeItem.setSelected(true);
        Events.sendEvent(Events.ON_SELECT, composedTypeTree, rootTreeItem);
    }

    private void clearTree() {
        composedTypeTree.getTreechildren().getChildren().clear();
        attributesListBox.getItems().clear();
    }

    private void populateTree(final Treeitem parent, final Map<ComposedTypeModel, List<ListItemDTO>> existingDefinitions) {
        currentDepth++;
        final ComposedTypeModel parentType = parent.getValue();
        final Set<AttributeDescriptorModel> filteredAttributes = filterAttributesForTree(readService, parentType);
        final Set<AttributeDescriptorModel> existingCollections = getCollectionAttributes(existingDefinitions.get(parentType));
        filteredAttributes.addAll(existingCollections);
        filteredAttributes.stream()
                .sorted((attribute1, attribute2) -> attribute1.getQualifier().compareToIgnoreCase(attribute2.getQualifier()))
                .forEach(attribute -> {
                    final ComposedTypeModel attributeType = readService.getComplexTypeForAttributeDescriptor(attribute);
                    if (!ancestors.contains(attributeType) && attributeType != null && !EditorBlacklists.getTypesBlackList().contains(attributeType.getCode())) {
                        ancestors.addFirst(attributeType);

                        if (!attributesMap.containsKey(attributeType)) {
                            populateAttributesMap(attributeType);
                        }

                        final Treeitem treeitem = createTreeItem(attribute.getQualifier(), attributeType, false);
                        if (parent.getTreechildren() == null) {
                            parent.appendChild(new Treechildren());
                        }
                        parent.getTreechildren().appendChild(treeitem);

                        if (currentDepth <= MAX_DEPTH) {
                            populateTree(treeitem, existingDefinitions);
                        }

                        ancestors.pollFirst();
                    }
                });
        currentDepth--;
    }

    private void populateAttributesMap(final ComposedTypeModel typeModel) {
        if (attributesMap.get(typeModel) == null) {
            final List<ListItemDTO> dtoList = new ArrayList<>();
            final Set<AttributeDescriptorModel> filteredAttributes = filterAttributesForAttributesMap(readService, typeModel);
            filteredAttributes.forEach(attribute -> {
                final boolean isSelected = attribute.getUnique() && !attribute.getOptional();
                final boolean isCollection = readService.isCollectionType(attribute.getAttributeType().getItemtype());
                dtoList.add(new ListItemDTO(attribute, false, isSelected, isCollection, false));
            });
            attributesMap.put(typeModel, dtoList);
        }
    }

    private void loadExistingDefinitions(final Map<ComposedTypeModel, List<ListItemDTO>> existingDefinitions) {
        existingDefinitions.forEach((key, value) -> attributesMap.forEach((key2, value2) -> {
            if (key2.equals(key)) {
                attributesMap.replace(key2, updateDTOs(value2, value));
            }
        }));
    }

    private void populateListBox(final ComposedTypeModel key) {
        attributesListBox.getItems().clear();

        attributesMap.get(key).stream()
                .sorted((dto1, dto2) -> dto1.getAttributeDescriptor().getQualifier().compareToIgnoreCase(dto2.getAttributeDescriptor().getQualifier()))
                .forEach(dto -> {
                    final boolean isComplex = readService.isComplexType(dto.getType());
                    final Listitem listItem = createListItem(dto, isComplex);
                    final Checkbox uniqueCheckbox = (Checkbox) listItem.getChildren().get(2).getFirstChild();
                    final Checkbox autocreateCheckbox = (Checkbox) listItem.getChildren().get(3).getFirstChild();
                    listItem.addEventListener(Events.ON_CLICK, new ListItemEventListener(listItem));
                    uniqueCheckbox.addEventListener(Events.ON_CHECK, new CheckboxEventListener(uniqueCheckbox, listItem));
                    autocreateCheckbox.addEventListener(Events.ON_CHECK, new CheckboxEventListener(autocreateCheckbox, listItem));
                    attributesListBox.appendChild(listItem);
                });
    }

    void updateAttribute(final Listitem listitem) {
        final ListItemDTO dto = listitem.getValue();
        final List<Component> components = listitem.getChildren();
        final Checkbox uCheckbox = ((Checkbox) components.get(2).getFirstChild());
        final Checkbox aCheckbox = ((Checkbox) components.get(3).getFirstChild());
        dto.setSelected(listitem.isSelected());
        dto.setCustomUnique(uCheckbox.isChecked());
        dto.setAutocreate(aCheckbox.isChecked());
    }

    private void checkTreeNodeForCollection(final ListItemDTO dto) {
        if (dto.isCollection() && readService.isComplexType(dto.getType())) {
            final Treechildren nodeChildren = composedTypeTree.getSelectedItem().getTreechildren();
            final String attributeQualifier = dto.getAttributeDescriptor().getQualifier();
            if (!isInTreeChildren(attributeQualifier, nodeChildren)) {
                createTreeNodeForCollection(dto);
            }
        }
    }

    private void createTreeNodeForCollection(final ListItemDTO attribute) {
        final ComposedTypeModel type = (ComposedTypeModel) attribute.getType();
        final Treeitem treeItem = createTreeItem(attribute.getAttributeDescriptor().getQualifier(), type, false);
        final Treeitem parent = composedTypeTree.getSelectedItem();
        if (parent.getTreechildren() == null) {
            parent.appendChild(new Treechildren());
        }
        parent.getTreechildren().appendChild(treeItem);
        populateAttributesMap(type);
        currentDepth = 0;
        ancestors.clear();
        populateTree(treeItem, Collections.emptyMap());
    }

    private final class ListItemEventListener implements EventListener<Event> {
        private final Listitem listItem;

        ListItemEventListener(final Listitem listItem) {
            this.listItem = listItem;
        }

        @Override
        public void onEvent(final Event event) {
            if (!listItem.isDisabled()) {
                final ListItemDTO dto = listItem.getValue();

                if (listItem.isSelected()) {
                    checkTreeNodeForCollection(dto);
                } else {
                    final List<Component> components = listItem.getChildren();
                    if (!dto.getAttributeDescriptor().getUnique()) {
                        ((Checkbox) components.get(2).getFirstChild()).setChecked(false);
                    }
                    ((Checkbox) components.get(3).getFirstChild()).setChecked(false);
                }

                updateAttribute(listItem);
            }
        }
    }

    private final class CheckboxEventListener implements EventListener<Event> {
        private final Checkbox checkbox;
        private final Listitem listItem;

        CheckboxEventListener(final Checkbox checkbox, final Listitem listItem) {
            this.checkbox = checkbox;
            this.listItem = listItem;
        }

        @Override
        public void onEvent(final Event event) {
            if (!listItem.isDisabled()) {
                if (checkbox.isChecked()) {
                    listItem.setSelected(true);
                    checkTreeNodeForCollection(listItem.getValue());
                }

                updateAttribute(listItem);
            }
        }
    }

}
