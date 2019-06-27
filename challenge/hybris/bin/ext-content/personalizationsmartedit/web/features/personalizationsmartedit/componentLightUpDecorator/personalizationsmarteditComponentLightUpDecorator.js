angular.module('personalizationsmarteditComponentLightUpDecorator', [
        'yjqueryModule',
        'personalizationsmarteditTemplates',
        'personalizationsmarteditServicesModule',
        'componentHandlerServiceModule'
    ])
    .directive('personalizationsmarteditComponentLightUp',
        function(
            yjQuery,
            personalizationsmarteditContextService,
            personalizationsmarteditComponentHandlerService,
            CONTAINER_SOURCE_ID_ATTR,
            OVERLAY_COMPONENT_CLASS,
            CONTAINER_TYPE_ATTRIBUTE,
            ID_ATTRIBUTE,
            TYPE_ATTRIBUTE,
            CATALOG_VERSION_UUID_ATTRIBUTE) {

            return {
                templateUrl: 'personalizationsmarteditComponentLightUpDecoratorTemplate.html',
                restrict: 'C',
                transclude: true,
                replace: false,
                scope: {
                    smarteditComponentId: '@',
                    smarteditComponentType: '@'
                },
                link: function($scope, element) {

                    var CONTAINER_TYPE = 'CxCmsComponentContainer';
                    var ACTION_ID_ATTR = 'data-smartedit-personalization-action-id';
                    var PARENT_CONTAINER_SELECTOR = '[class~="' + OVERLAY_COMPONENT_CLASS + '"][' + CONTAINER_SOURCE_ID_ATTR + '][' + CONTAINER_TYPE_ATTRIBUTE + '="' + CONTAINER_TYPE + '"]';
                    var PARENT_CONTAINER_WITH_ACTION_SELECTOR = '[class~="' + OVERLAY_COMPONENT_CLASS + '"][' + CONTAINER_TYPE_ATTRIBUTE + '="' + CONTAINER_TYPE + '"][' + ACTION_ID_ATTR + ']';
                    var COMPONENT_SELECTOR = '[' + ID_ATTRIBUTE + '][' + CATALOG_VERSION_UUID_ATTRIBUTE + '][' + TYPE_ATTRIBUTE + ']';

                    var isComponentSelected = function() {
                        var elementSelected = false;
                        if (angular.isArray(personalizationsmarteditContextService.getCustomize().selectedVariations)) {
                            var containerId = personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(element);
                            elementSelected = yjQuery.inArray(containerId, personalizationsmarteditContextService.getCustomize().selectedComponents) > -1;
                        }
                        return elementSelected;
                    };

                    var isVariationComponentSelected = function(component) {
                        var elementSelected = false;
                        var customize = personalizationsmarteditContextService.getCustomize();
                        if (customize.selectedCustomization && customize.selectedVariations) {
                            var container = component.closest(PARENT_CONTAINER_WITH_ACTION_SELECTOR);
                            elementSelected = container.length > 0;
                        }
                        return elementSelected;
                    };

                    $scope.getPersonalizationComponentBorderClass = function() {
                        var component = element.parent().closest(COMPONENT_SELECTOR);
                        var container = component.closest(PARENT_CONTAINER_SELECTOR);
                        container.toggleClass("perso__component-decorator", isVariationComponentSelected(component));
                        container.toggleClass("hyicon hyicon-checkedlg perso__component-decorator-icon", isVariationComponentSelected(component));
                        container.toggleClass("personalizationsmarteditComponentSelected", isComponentSelected());
                    };
                }
            };
        });
