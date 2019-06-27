angular.module('personalizationsmarteditContextMenu', [
        'personalizationsmarteditServicesModule',
        'smarteditRootModule'
    ])
    .factory('personalizationsmarteditContextModal', function(gatewayProxy) {

        var PersonalizationsmarteditContextModal = function() { //NOSONAR
            this.gatewayId = "personalizationsmarteditContextModal";
            gatewayProxy.initForService(this);
        };

        PersonalizationsmarteditContextModal.prototype.openDeleteAction = function() {};

        PersonalizationsmarteditContextModal.prototype.openAddAction = function() {};

        PersonalizationsmarteditContextModal.prototype.openEditAction = function() {};

        PersonalizationsmarteditContextModal.prototype.openEditComponentAction = function() {};

        return new PersonalizationsmarteditContextModal();
    })
    .factory('personalizationsmarteditContextModalHelper', function(personalizationsmarteditContextModal, personalizationsmarteditContextService, personalizationsmarteditComponentHandlerService) {
        var helper = {};

        var getSelectedVariationCode = function() {
            if (personalizationsmarteditContextService.getCombinedView().enabled) {
                return personalizationsmarteditContextService.getCombinedView().customize.selectedVariations.code;
            }
            return personalizationsmarteditContextService.getCustomize().selectedVariations.code;
        };

        var getSelectedCustomization = function(customizationCode) {
            if (personalizationsmarteditContextService.getCombinedView().enabled) {
                var customization = personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization;
                if (!customization && customizationCode) {
                    customization = personalizationsmarteditContextService.getCombinedView().selectedItems.filter(function(elem) {
                        return elem.customization.code === customizationCode;
                    })[0].customization;
                }
                return customization;
            }
            return personalizationsmarteditContextService.getCustomize().selectedCustomization;
        };

        var getSlotsToRefresh = function(containerSourceId) {
            var slotsSelector = personalizationsmarteditComponentHandlerService.getAllSlotsSelector();
            slotsSelector += ' [data-smartedit-container-source-id="' + containerSourceId + '"]'; // space at beginning is important
            var slots = personalizationsmarteditComponentHandlerService.getFromSelector(slotsSelector);
            var slotIds = Array.prototype.slice.call(slots.map(function() {
                return personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(personalizationsmarteditComponentHandlerService.getFromSelector(this));
            }));
            return slotIds;
        };

        helper.openDeleteAction = function(config) {
            var configProperties = angular.fromJson(config.properties);
            var configurationToPass = {};
            configurationToPass.containerId = config.containerId;
            configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
            configurationToPass.slotId = config.slotId;
            configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
            configurationToPass.selectedVariationCode = configProperties.smarteditPersonalizationVariationId || null;
            configurationToPass.selectedCustomizationCode = configProperties.smarteditPersonalizationCustomizationId || null;
            var componentCatalog = configProperties.smarteditCatalogVersionUuid.split('\/');
            configurationToPass.componentCatalog = componentCatalog[0];
            configurationToPass.componentCatalogVersion = componentCatalog[1];
            var contextCustomization = getSelectedCustomization(configurationToPass.selectedCustomizationCode);
            configurationToPass.catalog = contextCustomization.catalog;
            configurationToPass.catalogVersion = contextCustomization.catalogVersion;
            configurationToPass.slotsToRefresh = getSlotsToRefresh(configProperties.smarteditContainerSourceId);

            return personalizationsmarteditContextModal.openDeleteAction(configurationToPass);
        };

        helper.openAddAction = function(config) {
            var configProperties = angular.fromJson(config.properties);
            var configurationToPass = {};
            configurationToPass.componentType = config.componentType;
            configurationToPass.componentId = config.componentId;
            configurationToPass.containerId = config.containerId;
            configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
            configurationToPass.slotId = config.slotId;
            configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
            configurationToPass.selectedVariationCode = getSelectedVariationCode();
            var componentCatalog = configProperties.smarteditCatalogVersionUuid.split('\/');
            configurationToPass.componentCatalog = componentCatalog[0];
            var contextCustomization = getSelectedCustomization();
            configurationToPass.catalog = contextCustomization.catalog;
            configurationToPass.selectedCustomizationCode = contextCustomization.code;
            var slot = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(config.element);
            var slotCatalog = personalizationsmarteditComponentHandlerService.getCatalogVersionUuid(slot).split('\/');
            configurationToPass.slotCatalog = slotCatalog[0];
            configurationToPass.slotsToRefresh = getSlotsToRefresh(configProperties.smarteditContainerSourceId);
            configurationToPass.slotsToRefresh.push(config.slotId);

            return personalizationsmarteditContextModal.openAddAction(configurationToPass);
        };

        helper.openEditAction = function(config) {
            var configProperties = angular.fromJson(config.properties);
            var configurationToPass = {};
            configurationToPass.componentType = config.componentType;
            configurationToPass.componentId = config.componentId;
            configurationToPass.containerId = config.containerId;
            configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
            configurationToPass.slotId = config.slotId;
            configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
            configurationToPass.selectedVariationCode = configProperties.smarteditPersonalizationVariationId || null;
            configurationToPass.selectedCustomizationCode = configProperties.smarteditPersonalizationCustomizationId || null;
            configurationToPass.componentUuid = configProperties.smarteditComponentUuid || null;
            configurationToPass.slotsToRefresh = getSlotsToRefresh(configProperties.smarteditContainerSourceId);

            return personalizationsmarteditContextModal.openEditAction(configurationToPass);
        };

        helper.openEditComponentAction = function(config) {
            var configProperties = angular.fromJson(config.properties);
            var configurationToPass = {};
            configurationToPass.smarteditComponentType = configProperties.smarteditComponentType;
            configurationToPass.smarteditComponentUuid = configProperties.smarteditComponentUuid;
            configurationToPass.smarteditCatalogVersionUuid = configProperties.smarteditCatalogVersionUuid;
            return personalizationsmarteditContextModal.openEditComponentAction(configurationToPass);
        };

        return helper;
    });
