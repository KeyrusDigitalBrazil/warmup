/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.widgets.itemcomments;

import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.COMPONENT_ADD_COMMENT_BUTTON;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.MODEL_COMMENTS;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.SETTING_COMMENTS_LIST_RENDERER;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.SETTING_DATE_FORMAT;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.SETTING_DEFAULT_COMMENTS_COMMENT_TYPE;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.SETTING_POPUP_POSITION;
import static com.hybris.backoffice.widgets.itemcomments.ItemCommentsController.SOCKET_IN_INPUT_ITEM;
import static com.hybris.cockpitng.testing.util.CockpitTestUtil.simulateEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zkoss.zk.ui.event.Events.ON_CLICK;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.admin.CockpitMainWindowComposer;
import com.hybris.cockpitng.core.Executable;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


@DeclaredInput(value = SOCKET_IN_INPUT_ITEM, socketType = ItemModel.class)
@DeclaredGlobalCockpitEvent(eventName = CockpitMainWindowComposer.HEARTBEAT_EVENT)
@DeclaredViewEvent(componentID = COMPONENT_ADD_COMMENT_BUTTON, eventName = ON_CLICK)
@NullSafeWidget
public class ItemCommentsControllerTest extends AbstractWidgetUnitTest<ItemCommentsController>
{
	private static final int NUMBER_COMENTS_IN_STUB = 4;
	private static final String BACKOFFICE_COMMENT = "backofficeComment";
	private static final String INPUT_ITEM_MODEL = "inputItemModel";

	@Spy
	@InjectMocks
	private ItemCommentsController itemCommentsController;

	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private CommentService commentService;
	@Mock
	private UserService userService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private NotificationService notificationService;
	@Mock
	private WidgetComponentRenderer<Div, Object, CommentModel> listRenderer;
	@Mock
	private Div commentsList;
	@Spy
	private Button showCommentsButton;
	@Spy
	private Label commentsCount;
	@Mock
	private Popup commentsPopup;
	@Mock
	private Div addCommentContainer;
	@Mock
	private ItemModel inputItem;
	@Mock
	private Button addCommentButton;
	@Mock
	private Textbox textbox;
	@Mock
	private Button okButton;
	@Mock
	private Button cancelButton;

	@Override
	protected ItemCommentsController getWidgetController()
	{
		return itemCommentsController;
	}

	@Test
	public void shouldNotRenderControllerWhenComponentIsNull()
	{
		// given
		final Component component = null;

		// when
		itemCommentsController.initialize(component);

		// then
		verify(itemCommentsController, times(0)).render();
	}

	@Test
	public void shouldRenderDisabledShowPopupButton()
	{
		// given
		final ItemModel itemModel = null;

		// when
		itemCommentsController.onInputItemReceive(itemModel);

		// then
		verify(itemCommentsController, times(1)).render();
		assertThat(showCommentsButton.isDisabled()).isTrue();
		assertThat(showCommentsButton.getEventListeners(Events.ON_CLICK)).isEmpty();
	}

	@Test
	public void shouldRenderHiddenCommentsCountLabel()
	{
		// given
		final ItemModel itemModel = null;

		// when
		itemCommentsController.onInputItemReceive(itemModel);

		// then
		verify(itemCommentsController, times(1)).render();
		assertThat(commentsCount.isVisible()).isFalse();
		assertThat(commentsCount.getEventListeners(Events.ON_CLICK)).isEmpty();
	}

	@Test
	public void shouldInitializeControllerWithCorrectParameters()
	{
		// given
		final String dateFormat = "yyyy";
		widgetSettings.put(SETTING_POPUP_POSITION, "at_pointer", String.class);
		widgetSettings.put(SETTING_DATE_FORMAT, dateFormat, String.class);
		widgetSettings.put(SETTING_COMMENTS_LIST_RENDERER, "defaultCommentsListRenderer", String.class);

		// when
		itemCommentsController.initialize(new Div());

		// then
		assertThat(itemCommentsController.getPopupPosition()).isEqualTo(PopupPosition.AT_POINTER);
	}

	@Test
	public void shouldReceiveInput()
	{
		// given
		final ItemModel item = createItemModelStub();
		when(permissionFacade.canReadInstance(any())).thenReturn(true);

		// when
		executeInputSocketEvent(SOCKET_IN_INPUT_ITEM, item);

		// then
		assertThat(itemCommentsController.getCommentsFromModel().size()).isEqualTo(item.getComments().size());
		verifyListIsRendered(NUMBER_COMENTS_IN_STUB);
	}

	@Test
	public void shouldOpenPopupAndScrollToLastCommentWhenShowPopupButtonWasClickedAndItemIsSelected()
	{
		// given
		final ItemModel item = createItemModelStub();
		itemCommentsController.setValue(INPUT_ITEM_MODEL, item);
		itemCommentsController.initialize(new Div());

		// when
		simulateEvent(showCommentsButton, ON_CLICK, null);

		// then
		verify(commentsPopup).open(any(Component.class), anyString());
		verify(itemCommentsController).scrollToLastComment();
	}

	@Test
	public void shouldNotOpenPopupWhenShowPopupButtonWasClickedAndNoItemIsSelected()
	{
		// given
		itemCommentsController.setValue(INPUT_ITEM_MODEL, null);
		itemCommentsController.initialize(new Div());

		// when
		simulateEvent(showCommentsButton, ON_CLICK, null);

		// then
		verify(commentsPopup, never()).open(any(Component.class), anyString());
	}

	@Test
	public void shouldNotOpenPopupWhenCommentsCountLabelWasClickedAndNoItemIsSelected()
	{
		// given
		itemCommentsController.setValue(INPUT_ITEM_MODEL, null);
		itemCommentsController.initialize(new Div());

		// when
		simulateEvent(commentsCount, ON_CLICK, null);

		// then
		verify(commentsPopup, never()).open(any(Component.class), anyString());
	}

	@Test
	public void shouldReactOnHeartbeat() throws ObjectNotFoundException
	{
		// given
		itemCommentsController.onInputItemReceive(createItemModelStub());
		when(objectFacade.reload(any(ItemModel.class))).thenReturn(createItemModelStub());
		when(permissionFacade.canReadInstance(any())).thenReturn(true);

		// when
		executeGlobalEvent(CockpitMainWindowComposer.HEARTBEAT_EVENT,
				new DefaultCockpitEvent(CockpitMainWindowComposer.HEARTBEAT_EVENT, null, null));

		// then
		verify(objectFacade).reload(any(Object.class));
		verifyListIsRendered(NUMBER_COMENTS_IN_STUB);
	}

	@Test
	public void shouldOpenTheAddNewCommentContainerWhenAddNewCommentButtonIsClicked()
	{
		// given
		final Div addCommentContainer = Mockito.mock(Div.class);
		doReturn(addCommentContainer).when(itemCommentsController).createAddCommentContainer(any(), any(), any());
		when(itemCommentsController.getAddCommentContainer()).thenReturn(addCommentContainer);

		// when
		itemCommentsController.showNewCommentSection();

		// then
		assertThat(addCommentContainer).isNotNull();
		verify(itemCommentsController.getAddCommentContainer()).detach();
		verify(itemCommentsController.getCommentsPopup()).appendChild(addCommentContainer);
	}

	@Test
	public void shouldAddNewCommentWhenOkButtonIsClicked() throws Exception
	{
		// given
		final Textbox textBox = new Textbox();
		textBox.setText("Sample comment");
		final List comments = new ArrayList();
		final int sizeBefore = comments.size();

		final CommentModel commentModel = mock(CommentModel.class);
		when(widgetModel.getValue(MODEL_COMMENTS, List.class)).thenReturn(comments);
		when(objectFacade.create(CommentModel._TYPECODE)).thenReturn(commentModel);
		when(objectFacade.save(any(CommentModel.class))).thenReturn(commentModel);
		when(inputItem.getComments()).thenReturn(comments);
		final DomainModel domainModel = mock(DomainModel.class);
		when(commentService.getDomainForCode(anyString())).thenReturn(domainModel);
		final ComponentModel componentModel = mock(ComponentModel.class);
		when(commentService.getComponentForCode(eq(domainModel), anyString())).thenReturn(componentModel);
		when(widgetSettings.getString(SETTING_DEFAULT_COMMENTS_COMMENT_TYPE)).thenReturn(BACKOFFICE_COMMENT);
		final CommentTypeModel commentTypeModel = mock(CommentTypeModel.class);
		when(commentService.getCommentTypeForCode(eq(componentModel), eq(BACKOFFICE_COMMENT))).thenReturn(commentTypeModel);
		when(itemCommentsController.getCommentsPopup()).thenReturn(commentsPopup);
		when(itemCommentsController.getInputItemModel()).thenReturn(inputItem);
		when(itemCommentsController.getAddCommentContainer()).thenReturn(addCommentContainer);
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(true);

		// when
		itemCommentsController.createOkButtonEventListener(textBox).onEvent(null);

		// then
		verify(objectFacade).save(commentModel);
		assertThat(comments.size()).isEqualTo(sizeBefore + 1);
		verify((CommentModel) comments.get(0)).setText(textBox.getText());
	}

	@Test
	public void shouldNotAddNewCommentWhenCancelButtonIsClicked() throws Exception
	{
		// given
		final Div addCommentContainer = Mockito.mock(Div.class);
		doReturn(addCommentContainer).when(itemCommentsController).createAddCommentContainer(any(), any(), any());

		// when
		itemCommentsController.showNewCommentSection();
		itemCommentsController.createCancelButtonListener().onEvent(null);

		// then
		verify(objectFacade, times(0)).save(any(CommentModel.class));
		verify(addCommentContainer).setVisible(false);
	}

	@Test
	public void shouldCloseAndOpenPopupOnPopupReload()
	{
		// given
		final Executable emptyExecutable = () -> {
			// do nothing
		};

		// when
		itemCommentsController.openPopup();
		itemCommentsController.runWithReopeningPopup(emptyExecutable);

		// then
		verify(itemCommentsController).closePopup();
		verify(itemCommentsController).openPopup();
	}

	@Test
	public void shouldScrollToLastCommentAfterRenderingAllComments()
	{
		// given
		when(itemCommentsController.getInputItemModel()).thenReturn(createItemModelStub());

		// when
		itemCommentsController.render();

		// then
		verify(itemCommentsController).scrollToLastComment();
	}

	@Test
	public void shouldHideAddNewCommentContainerAndShowButton() throws Exception
	{
		// given
		final EventListener cancelButtonListener = itemCommentsController.createCancelButtonListener();

		// when
		cancelButtonListener.onEvent(new Event("Event"));

		// then
		verify(itemCommentsController.getAddCommentContainer()).setVisible(false);
		verify(itemCommentsController.getAddCommentButton()).setVisible(true);
	}

	@Test
	public void shouldRenderCommentsIfNewCommentsAppeared() throws ObjectNotFoundException
	{
		// given
		when(itemCommentsController.getCommentsFromModel()).thenReturn(new ArrayList<>());
		when(itemCommentsController.getObjectFacade().reload(any(Object.class))).thenReturn(createItemModelStub());
		doNothing().when(itemCommentsController).setValue(any(), any());
		when(permissionFacade.canReadInstance(any())).thenReturn(true);

		// when
		itemCommentsController.loadNewCommentsIfPossible();

		// then
		verify(itemCommentsController).render();
	}

	@Test
	public void shouldNotRenderCommentsIfNewCommentsDidNotAppear() throws ObjectNotFoundException
	{
		// given
		when(itemCommentsController.getCommentsFromModel()).thenReturn(new ArrayList<>());
		when(itemCommentsController.getObjectFacade().reload(any(Object.class))).thenReturn(createItemModelWithoutCommentsStub());

		// when
		itemCommentsController.loadNewCommentsIfPossible();

		// then
		verify(itemCommentsController, times(0)).render();
		verify(itemCommentsController, times(0)).scrollToLastComment();
	}

	@Test
	public void shouldCreateAddCommentContainer()
	{
		// when
		final Div container = itemCommentsController.createAddCommentContainer(textbox, okButton, cancelButton);

		// then
		assertThat(container).isNotNull();
		assertThat(container.getChildren().size()).isEqualTo(3);
		assertThat(container.getFirstChild()).isInstanceOf(Textbox.class);
		assertThat(container.getChildren().get(1)).isInstanceOf(Button.class);
		assertThat(container.getLastChild()).isInstanceOf(Button.class);
		verify(itemCommentsController).createOkButtonEventListener(textbox);
		verify(itemCommentsController).createCancelButtonListener();
	}

	@Test
	public void shouldSaveItemItemComment() throws ObjectSavingException
	{
		// given
		final CommentModel commentModel = Mockito.mock(CommentModel.class);
		doNothing().when(itemCommentsController).addCommentToItem(commentModel);
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(true);

		// when
		itemCommentsController.tryToSaveItemComment(commentModel);

		// then
		verify(itemCommentsController.getObjectFacade()).save(commentModel);
		verify(itemCommentsController).addCommentToItem(commentModel);
	}

	@Test
	public void showNewCommentsSectionShouldRunWithPopupReload()
	{
		// when
		itemCommentsController.showNewCommentSection();

		// then
		verify(itemCommentsController).runWithReopeningPopup(any());
	}

	@Test
	public void cancelButtonEventShouldRunWithPopupReload() throws Exception
	{
		// given
		final EventListener listener = itemCommentsController.createCancelButtonListener();

		// when
		listener.onEvent(new Event("event"));

		// then
		verify(itemCommentsController).runWithReopeningPopup(any());
	}

	@Test
	public void okButtonEventShouldRunWithPopupReload() throws Exception
	{
		// given
		final EventListener listener = itemCommentsController.createOkButtonEventListener(textbox);

		// when
		listener.onEvent(new Event("event"));

		// then
		verify(itemCommentsController).runWithReopeningPopup(any());
	}

	@Test
	public void shouldReturnTrueWhenThereArePermissionsToCreateComment()
	{
		// given
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(true);

		// when
		final boolean canCreateComment = itemCommentsController.canCreateComment();

		// then
		assertThat(canCreateComment).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenThereIsNoPermissionToChangeWorkflowAttachment()
	{
		// given
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(false);

		// when
		final boolean canCreateComment = itemCommentsController.canCreateComment();

		// then
		assertThat(canCreateComment).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenThereIsNoPermissionToCreateCommentType()
	{
		// given
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(false);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(true);

		// when
		final boolean canCreateComment = itemCommentsController.canCreateComment();

		// then
		assertThat(canCreateComment).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenThereIsNoPermissionToChangeCommentType()
	{
		// given
		when(permissionFacade.canCreateTypeInstance(CommentModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canChangeType(CommentModel._TYPECODE)).thenReturn(false);
		when(permissionFacade.canChangeInstance(itemCommentsController.getInputItemModel())).thenReturn(true);

		// when
		final boolean canCreateComment = itemCommentsController.canCreateComment();

		// then
		assertThat(canCreateComment).isFalse();
	}

	private void verifyListIsRendered(final int times)
	{
		verify(listRenderer, times(times)).render(any(), any(), any(), any(), any());
	}

	private static ItemModel createItemModelStub()
	{
		final ItemModel itemModel = new ItemModel();
		final List<CommentModel> comments = new ArrayList<>();
		IntStream.range(0, NUMBER_COMENTS_IN_STUB).forEach(value -> comments.add(new CommentModel()));
		itemModel.setComments(comments);
		return itemModel;
	}

	private static ItemModel createItemModelWithoutCommentsStub()
	{
		final ItemModel itemModel = new ItemModel();
		itemModel.setComments(new ArrayList<>());
		return itemModel;
	}
}
