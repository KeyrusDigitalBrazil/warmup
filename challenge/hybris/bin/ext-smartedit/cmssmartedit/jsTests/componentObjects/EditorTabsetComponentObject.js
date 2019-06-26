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
module.exports = {
    EDITOR_TABSET_SELECTOR: 'editor-tabset',
    EDITOR_TABSET_TABS: 'ul.nav-tabs',
    EDITOR_DROPDOWN_MENU: 'ul.dropdown-menu',
    LINK_SELECTOR: 'a',
    TAB1_TAB_SELECTOR: 'li[data-tab-id="tab1"]',
    TAB2_TAB_SELECTOR: 'li[data-tab-id="tab2"]',
    TAB3_TAB_SELECTOR: 'li[data-tab-id="tab3"]',
    TAB4_TAB_SELECTOR: 'li[data-tab-id="tab4"]',
    TAB5_TAB_SELECTOR: 'li[data-tab-id="tab5"]',
    TAB6_TAB_SELECTOR: 'li[data-tab-id="tab6"]',
    TAB7_TAB_SELECTOR: 'li[data-tab-id="tab7"]',
    TAB1_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab1"]',
    TAB2_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab2"]',
    TAB3_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab3"]',
    TAB4_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab4"]',
    TAB5_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab5"]',
    TAB6_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab6"]',
    TAB7_TAB_CONTENT_SELECTOR: 'y-tab[data-tab-id="tab7"]',
    editorTabset: function() {
        return element(by.css(this.EDITOR_TABSET_SELECTOR));
    },
    editorTabsetTabs: function() {
        return this.editorTabset().element(by.css(this.EDITOR_TABSET_TABS));
    },
    tab1Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB1_TAB_SELECTOR));
    },
    tab1TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB1_TAB_CONTENT_SELECTOR));
    },
    tab1Link: function() {
        return this.tab1Tab().element(by.css(this.LINK_SELECTOR));
    },
    tab2Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB2_TAB_SELECTOR));
    },
    tab2Link: function() {
        return this.tab2Tab().element(by.css(this.LINK_SELECTOR));
    },
    tab2TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB2_TAB_CONTENT_SELECTOR));
    },
    tab3Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB3_TAB_SELECTOR));
    },
    tab3Link: function() {
        return this.tab3Tab().element(by.css(this.LINK_SELECTOR));
    },
    tab3TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB3_TAB_CONTENT_SELECTOR));
    },
    tab4Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB4_TAB_SELECTOR));
    },
    tab4TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB4_TAB_CONTENT_SELECTOR));
    },
    tab5Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB5_TAB_SELECTOR));
    },
    tab5TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB5_TAB_CONTENT_SELECTOR));
    },
    tab6Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB6_TAB_SELECTOR));
    },
    tab6TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB6_TAB_CONTENT_SELECTOR));
    },
    tab6DropdownMenu: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB6_TAB_SELECTOR));
    },
    tab6DropdownMenuLink: function() {
        return this.tab6DropdownMenu().element(by.css(this.LINK_SELECTOR));
    },
    tab7Tab: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB7_TAB_SELECTOR));
    },
    tab7TabContent: function() {
        return this.editorTabset().element(by.css(this.TAB7_TAB_CONTENT_SELECTOR));
    },
    tab7DropdownMenu: function() {
        return this.editorTabsetTabs().element(by.css(this.TAB7_TAB_SELECTOR));
    },
    tab7DropdownMenuLink: function() {
        return this.tab7DropdownMenu().element(by.css(this.LINK_SELECTOR));
    }
};
