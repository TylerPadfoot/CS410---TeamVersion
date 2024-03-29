package stuffplotter.presenters;

import stuffplotter.presenters.EventDateSelectionPresenter.EventDateSelectionView;
import stuffplotter.presenters.EventFriendSelectionPresenter.EventFriendSelectionView;
import stuffplotter.presenters.EventInfoPresenter.EventInfoView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class EventCreationPagedPresenter implements Presenter
{
	public interface EventCreationPagedView
	{
		/**
		 * Retrieve the EventInfoView which displays the event information.
		 * @pre true;
		 * @post true;
		 * @return the EventInfoView which displays the event information.
		 */
		public EventInfoView getEventInfoView();
		
		/**
		 * Retrieve the EventDateSelectionView which displays the date selection.
		 * @pre true;
		 * @post true;
		 * @return the EventDateSelectionView which displays the date selection.
		 */
		public EventDateSelectionView getDateSelectionView();
		
		/**
		 * Retrieve the EventFriendSelectionView which displays the friend selection.
		 * @pre true;
		 * @post true;
		 * @return the EventFriendSelectionView which displays the friend selection.
		 */
		public EventFriendSelectionView getFriendSelectionView();
		
		/**
		 * Displays the first view in the EventCreationPageView.
		 * @pre true;
		 * @post true;
		 */
		public void displayFirstView();
		
		/**
		 * Retrieve the EventCreationPagedView as a widget.
		 * @pre true;
		 * @post true;
		 * @return the EventCreationPagedView as a widget.
		 */
		public Widget asWidget();
	}
	
	private final EventCreationPagedView display;
	private final HandlerManager eventBus;
	
	/**
	 * Constructor for the EventCreationPagedPresenter.
	 * @pre display != null;
	 * @post true;
	 * @param eventBus - the event bus for the application. 
	 * @param display - the display associate with the EventCreationPagedPresenter.
	 */
	public EventCreationPagedPresenter(HandlerManager eventBus, EventCreationPagedView display)
	{
		this.eventBus = eventBus;
		this.display = display;
	}
	
	
	@Override
	public void go(HasWidgets container)
	{
		Presenter infoPresenter = new EventInfoPresenter(this.display.getEventInfoView());
		infoPresenter.go((HasWidgets) this.display);
		
		Presenter datePresenter = new EventDateSelectionPresenter(this.eventBus, this.display.getDateSelectionView());
		datePresenter.go((HasWidgets) this.display);
		
		Presenter friendPresenter = new EventFriendSelectionPresenter(this.display.getFriendSelectionView());
		friendPresenter.go((HasWidgets) this.display);
		
		this.display.displayFirstView();
		
		container.add(this.display.asWidget());
	}

}
