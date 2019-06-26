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
(function() {

    var dragAndDropPageObject = {};

    var contextualMenu = e2e.componentObjects.componentContextualMenu;
    var componentMenu = e2e.componentObjects.componentMenu;
    var storefront = e2e.componentObjects.storefront;

    dragAndDropPageObject.structure = {
        slots: {
            TOP_HEADER_SLOT: 'topHeaderSlot',
            SEARCH_BOX_SLOT: 'searchBoxSlot',
            BOTTOM_HEADER_SLOT: 'bottomHeaderSlot',
            FOOTER_SLOT: 'footerSlot'
        },
        components: {
            COMPONENT1: 'component1',
            COMPONENT2: 'component2',
            COMPONENT3: 'component3',
            COMPONENT4: 'component4',
            COMPONENT5: 'component5',
            COMPONENT10: 'component10'
        }
    };

    dragAndDropPageObject.elements = {
        getComponentDropHelpers: function(slotId, position) {
            browser.waitForVisibility(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "']"));
            var hintElement = by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "'] .smartEditComponentX:nth-child(" + position + ") .overlayDropzone");
            browser.waitForVisibility(hintElement);
            return element.all(hintElement);
        },
        getParagraphComponentInComponentMenu: function() {
            return element(by.css(".smartEditComponent[data-smartedit-component-type='CMSParagraphComponent'] img"));
        },
        getAbstractCMSComponentInComponentMenu: function() {
            return element(by.css("y-infinite-scrolling .smartEditComponent[data-smartedit-component-type='AbstractCMSComponent'][data-smartedit-component-id='Component10'] img"));
        },
        getTopScrollHint: function() {
            return element(by.id('top_scroll_page'));
        },
        getBottomScrollHint: function() {
            return element(by.id('bottom_scroll_page'));
        },
        getDragArea: function() {
            return element(by.id('ySmartEditFrameDragArea'));
        },
        getOriginalComponentRect: function(componentId) {
            return storefront.elements.getComponentById(componentId).then(function(component) {
                return dragAndDropPageObject.elements.getElementBoundingClientRect(component);
            });
        },
        getIframeOffset: function() {
            return browser.executeScript('return window.smarteditJQuery(arguments[0]).offset()', element(by.id('ySmartEditFrame')));
        },
        getElementBoundingClientRect: function(el) {
            return browser.executeScript('return arguments[0].getBoundingClientRect()', el);
        }
    };

    dragAndDropPageObject.actions = {
        moveToStoreFront: function() {
            browser.switchToIFrame();
        },
        moveToParent: function() {
            browser.switchToParent();
        },
        /**
         * Across frames: CROSS origin drag and drop is disabled in Chrome.
         * When doing a drag and drop from component menu to the inner frame, we are using the component 'rect' offset and hovering the drag area to circumvent CROSS origin drag and drop disabled in Chrome.
         */
        addFromComponentMenuToSlotInPosition: function(targetSlotId, position, expectedElementsInSlotCount) {
            return browser.switchToIFrame().then(function() {
                return storefront.elements.getComponentsBySlotId(targetSlotId, expectedElementsInSlotCount).then(function(elements) {
                    return dragAndDropPageObject.actions.dragComponentInComponentMenuOverSlot(targetSlotId).then(function() {
                        var helperPosition = (position < elements.length) ? (position + 1) : position;
                        var helperIndex = (position < elements.length) ? 0 : 1;
                        return dragAndDropPageObject.elements.getComponentDropHelpers(targetSlotId, helperPosition).then(function(helpers) {
                            return dragAndDropPageObject.elements.getElementBoundingClientRect(helpers[helperIndex]).then(function(elementRect) {
                                return dragAndDropPageObject.actions.dragOverDragAreaAtLocation(elementRect).then(function() {
                                    return dragAndDropPageObject.actions.dropComponentFromComponentMenu({
                                        x: elementRect.left,
                                        y: elementRect.top
                                    });
                                });
                            });
                        });
                    });
                });
            });
        },
        moveToElement: function(element) {
            browser.actions()
                .mouseMove(element)
                .perform();
        },
        grabComponent: function(slotId, componentId) {
            _grabComponent(slotId, componentId);
        },
        grabComponentInComponentMenu: function() {
            _grabComponentInComponentMenu();
        },
        enableCloneOnDropAndGrabCustomizedComponentInComponentMenu: function() {
            _enableCloneOnDropAndGrabCustomizedComponentInComponentMenu();
        },
        grabCustomizedComponentInComponentMenu: function() {
            _grabCustomizedComponentInComponentMenu();
        },
        hoverOverElement: function(element) {
            return _hoverOverElement(element);
        },
        dropComponent: function(componentId, slotId) {
            _dropComponent(componentId, slotId);
        },
        dropComponentFromComponentMenu: function(position) {
            return _dropComponentFromComponentMenu(position);
        },
        isComponentInPosition: function(slot, component, position, expectedElementsInSlotCount) {
            return _isComponentInPosition(slot, component, position, expectedElementsInSlotCount);
        },
        isSlotEnabled: function(slotId) {
            var slot = storefront.elements.getComponentInOverlayById(slotId);
            return hasClass(slot, 'over-slot-enabled');
        },
        isSlotDisabled: function(slotId) {
            var slot = storefront.elements.getComponentInOverlayById(slotId);
            return hasClass(slot, 'over-slot-disabled');
        },
        hoverOverTopHintFromOuter: function() {
            return dragAndDropPageObject.elements.getElementBoundingClientRect(dragAndDropPageObject.elements.getTopScrollHint()).then(function(elementRect) {
                return dragAndDropPageObject.actions.dragOverDragAreaAtLocation(elementRect);
            });
        },
        hoverOverBottomHintFromOuter: function() {
            return dragAndDropPageObject.elements.getElementBoundingClientRect(dragAndDropPageObject.elements.getBottomScrollHint()).then(function(elementRect) {
                return dragAndDropPageObject.actions.dragOverDragAreaAtLocation(elementRect);
            });
        },
        scrollToBottomFromOuter: function() {
            return browser.executeScript('window.smarteditJQuery(document.scrollingElement).scrollTop(document.scrollingElement.scrollHeight);').then(function() {
                // force a hover to simulate user hover since we force the scrolling with .scrollTop() instead of hovering the bottom hint.
                return dragAndDropPageObject.actions.hoverOverBottomHintFromOuter();
            });
        },
        hoverOverTopHint: function() {
            var hint = dragAndDropPageObject.elements.getTopScrollHint();
            this.moveToElement(hint);
            browser.executeScript(simulateDragAndDropOperation, hint.getWebElement(), EVENT_TYPES.DRAG_ENTER);
        },
        hoverOverBottomHint: function() {
            var hint = dragAndDropPageObject.elements.getBottomScrollHint();
            this.moveToElement(hint);
            browser.executeScript(simulateDragAndDropOperation, hint.getWebElement(), EVENT_TYPES.DRAG_ENTER);
        },
        getPageVerticalScroll: function() {
            return _getCurrentPageVerticalScroll();
        },
        /**
         * Across frames: CROSS origin drag and drop is disabled in Chrome.
         * Using the component 'rect' offset and hovering the drag area to circumvent CROSS origin drag and drop disabled in Chrome.
         */
        dragComponentInComponentMenuOverSlot: function(slotId) {
            return browser.switchToIFrame().then(function() {
                return storefront.elements.getComponentLocationById(slotId).then(function(elementPosition) {
                    // first move in iframe to the original component position to have it added to the smarteditoverlay
                    // we can't just do a mouseMove to the slot because the 'drag area' is preventing us to do so.
                    // to avoid doing a toggle of the 'drag area' to be able to do a mouseMove, we just modify the document.scrollingElement.scrollTop value.
                    dragAndDropPageObject.actions.scrollToPosition(elementPosition.y);
                    return dragAndDropPageObject.elements.getOriginalComponentRect(slotId).then(function(elementRect) {
                        return dragAndDropPageObject.actions.dragOverDragAreaAtLocation({
                            left: elementRect.left + (elementRect.width / 2),
                            top: elementRect.top + (elementRect.height / 2)
                        });
                    });
                });
            });
        },
        dragOverDragAreaAtLocation: function(location) {
            return browser.switchToParent().then(function() {
                return dragAndDropPageObject.elements.getIframeOffset().then(function(dragAreaOffset) {
                    var dragEventLocation = {
                        x: location.left + dragAreaOffset.left,
                        y: location.top + dragAreaOffset.top
                    };
                    var dragArea = dragAndDropPageObject.elements.getDragArea();
                    return browser.executeScript(simulateDragAndDropOperation, dragArea.getWebElement(), EVENT_TYPES.DRAG_ENTER, dragEventLocation).then(function() {
                        return browser.executeScript(simulateDragAndDropOperation, dragArea.getWebElement(), EVENT_TYPES.DRAG_OVER, dragEventLocation).then(function() {
                            return browser.switchToIFrame();
                        });
                    });
                });
            });
        },
        scrollToPosition: function(value) {
            return browser.executeScript('window.smarteditJQuery(document.scrollingElement).scrollTop(arguments[0])', value);
        }
    };

    dragAndDropPageObject.assertions = {

        pageHasScrolledUp: function(oldScroll) {

            browser.waitUntil(function() {
                return dragAndDropPageObject.actions.getPageVerticalScroll().then(function(newScroll) {
                    return newScroll < oldScroll;
                });
            }, 'Expected page to scroll up');
        },

        pageHasScrolledDown: function(oldScroll) {

            browser.waitUntil(function() {
                return dragAndDropPageObject.actions.getPageVerticalScroll().then(function(newScroll) {
                    return newScroll > oldScroll;
                });
            }, 'Expected page to scroll down');
        }
    };

    var EVENT_TYPES = {
        DRAG_START: 'dragstart',
        DRAG_END: 'dragend',
        DROP: 'drop',
        DRAG_ENTER: 'dragenter',
        DRAG_OVER: 'dragover'
    };

    function _enableCloneOnDropForCustomizedComponents() {
        browser.click(componentMenu.elements.getCloneOnDropAction());
    }

    function _grabComponent(slotId, componentId) {
        browser.switchToIFrame();

        return contextualMenu.elements.getMoveButtonForComponentId(componentId).then(function(moveButton) {
            browser.actions()
                .mouseMove(storefront.elements.getComponentInOverlayById(slotId))
                .mouseMove(storefront.elements.getComponentInOverlayById(componentId))
                .mouseMove(moveButton)
                .mouseDown()
                .perform();

            browser.executeScript(simulateDragAndDropOperation, moveButton.getWebElement(), EVENT_TYPES.DRAG_START);
            return moveButton;
        });
    }

    function _grabComponentInComponentMenu() {
        browser.click(componentMenu.elements.getComponentMenuButton());

        var elementToDrag = dragAndDropPageObject.elements.getParagraphComponentInComponentMenu();
        browser.actions()
            .mouseMove(elementToDrag)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    function _enableCloneOnDropAndGrabCustomizedComponentInComponentMenu() {
        browser.click(componentMenu.elements.getComponentMenuButton());
        browser.click(componentMenu.elements.getComponentsTabHeaderSelector());

        _enableCloneOnDropForCustomizedComponents();

        componentMenu.actions.searchComponents(dragAndDropPageObject.structure.components.COMPONENT10);

        var elementToDrag = dragAndDropPageObject.elements.getAbstractCMSComponentInComponentMenu();
        browser.actions()
            .mouseMove(elementToDrag)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    function _grabCustomizedComponentInComponentMenu() {
        browser.click(componentMenu.elements.getComponentMenuButton());
        browser.click(componentMenu.elements.getComponentsTabHeaderSelector());

        componentMenu.actions.searchComponents(dragAndDropPageObject.structure.components.COMPONENT10);

        var elementToDrag = dragAndDropPageObject.elements.getAbstractCMSComponentInComponentMenu();
        browser.actions()
            .mouseMove(elementToDrag)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    // this method needs to be retired at some point when all of its usages are replaced by dropComponentFromComponentMenu or dropAtDroppableArea
    function _dropComponent(componentId, slotId) {
        var slot = storefront.elements.getComponentInOverlayById(slotId);
        browser.executeScript(simulateDragAndDropOperation, slot.getWebElement(), EVENT_TYPES.DROP).then(function() {
            browser.executeScript(simulateDragAndDropOperation, storefront.elements.getComponentInOverlayById(componentId).getWebElement(), EVENT_TYPES.DRAG_END);
        });
    }

    /**
     * Across frames: CROSS origin drag and drop is disabled in Chrome.
     * Using the component 'rect' offset and hovering the drag area to circumvent CROSS origin drag and drop disabled in Chrome.
     */
    function _dropComponentFromComponentMenu(position) {
        return browser.switchToParent().then(function() {
            var dragArea = dragAndDropPageObject.elements.getDragArea();
            return browser.executeScript(simulateDragAndDropOperation, dragArea.getWebElement(), EVENT_TYPES.DROP, position || {
                x: 0,
                y: 0
            }).then(function() {
                var elementToDrag = dragAndDropPageObject.elements.getAbstractCMSComponentInComponentMenu();
                browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_END);
                return browser.switchToIFrame();
            });
        });
    }

    function _hoverOverElement(element) {
        return browser.actions()
            .mouseMove(element)
            .perform()
            .then(function() {
                return browser.executeScript(simulateDragAndDropOperation, element.getWebElement(), EVENT_TYPES.DRAG_ENTER).then(function() {
                    return browser.executeScript(simulateDragAndDropOperation, element.getWebElement(), EVENT_TYPES.DRAG_OVER);
                });
            });
    }

    /*
     Unfortunately, Protractor's Chrome Driver is not compatible with HTML 5 drag and drop. If it's simulated (hover
     over component, mouse down, and then mouse move), nothing happens; the events are never triggered. Thus, we
     have to simulate the events.
     */
    function simulateDragAndDropOperation(element, operationType, position) {
        function createCustomEvent(type, position) {
            var event = document.createEvent("CustomEvent");
            event.initCustomEvent(type, true, true, null);
            event.dataTransfer = {
                data: {},
                setData: function(type, val) {
                    this.data[type] = val;
                },
                getData: function(type) {
                    return this.data[type];
                }
            };
            if (position) {
                event.pageX = position.x;
                event.pageY = position.y;
            } else {
                var elementRect = element.getBoundingClientRect();
                event.pageX = elementRect.left + Math.ceil((elementRect.right - elementRect.left) / 2);
                event.pageY = window.scrollY + elementRect.top + Math.ceil((elementRect.bottom - elementRect.top) / 2);
            }
            return event;
        }
        element.dispatchEvent(createCustomEvent(operationType, position));
    }

    // Helpers
    function _getCurrentPageVerticalScroll() {
        return browser.executeScript('return document.scrollingElement.scrollTop;');
    }

    function _isComponentInPosition(slotId, componentId, position, expectedElementsInSlotCount) {
        return storefront.elements.getComponentsBySlotId(slotId, expectedElementsInSlotCount)
            .then(function(components) {
                if (components.length > position) {
                    return browser.waitUntil(function() {
                        var component = components[position];
                        return component.getAttribute('data-smartedit-component-id');
                    }).then(function(attr) {
                        console.info("at position " + position + ", expected: " + componentId + ", got: " + attr);
                        return (attr === componentId);
                    });
                } else {
                    return false;
                }
            });
    }

    function hasClass(element, className) {
        return element.getAttribute('class').then(function(classes) {
            return classes.split(' ').indexOf(className) !== -1;
        });
    }

    module.exports = dragAndDropPageObject;

}());
