package stuffplotter.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import stuffplotter.bindingcontracts.AccountModel;
import stuffplotter.client.services.AccountServiceAsync;
import stuffplotter.shared.Account;
import stuffplotter.views.events.EventDateSelectionPanel;
import stuffplotter.views.events.EventInfoPanel;
import stuffplotter.views.events.FriendSelectionPanel;

/**
 * Class that is a visitor for any panel that implements the EventSubmittable interface.
 * Populates the values for the given user for each page to help create an Event object.
 */
public class EventCreationPagePopulator implements EventCreationPageVisitor
{
	private String eventCreator;
	private List<String> userFriends;
	private AccountServiceAsync accountServices;
	
	/**
	 * Constructor for the EventCreationPagePopulator.
	 * @pre accountServices != null && userAccount != null;
	 * @post true;
	 * @param accountServices - the services to retrieve the account information for users.
	 * @param userAccount - the user account to populate the event creation pages for.
	 */
	public EventCreationPagePopulator(AccountServiceAsync accountServices, AccountModel userAccount)
	{
		this.eventCreator = userAccount.getUserFullName();
		this.userFriends = userAccount.getUserFriends();
		this.accountServices = accountServices;
	}
	
	/**
	 * Visit an EventInfoInputPanel to populate the event creator's name.
	 * @pre eventInfoPanel != null;
	 * @post true;
	 * @param eventInfoPanel - the EventInfoInputPanel to populate the creator's name with.
	 */
	@Override
	public void visit(EventInfoPanel eventInfoPanel)
	{
		eventInfoPanel.getEventInfoInputPanel().setCreator(this.eventCreator);
	}

	/**
	 * Visit an EventDateSelectionPanel.
	 * @pre dateSelectionPanel != null;
	 * @post true;
	 * @param dateSelectionPanel - the EventDateSelectionPanel.
	 */
	@Override
	public void visit(EventDateSelectionPanel dateSelectionPanel)
	{
		// Do nothing as there are no values to populate on this page.
	}

	/**
	 * Visit a FriendSelectionpanel to populate the friends with.
	 * @pre friendSelection != null;
	 * @post true;
	 * @param friendSelection - the EventInfoInputPanel to retrieve values from.
	 */
	@Override
	public void visit(final FriendSelectionPanel friendSelection)
	{
		this.accountServices.getAccounts(this.userFriends, new AsyncCallback<Map<String,Account>>()
		{
			@Override
			public void onSuccess(Map<String, Account> result)
			{
				List<AccountModel> friends = new ArrayList<AccountModel>();
				
				// for loop to create the List of friends
				for(Account account : result.values())
				{
					friends.add(account);
				}
				
				friendSelection.setFriendData(friends);
			}
			
			@Override
			public void onFailure(Throwable caught)
			{
				friendSelection.setErrorMessage();
			}
		});
	}
}
