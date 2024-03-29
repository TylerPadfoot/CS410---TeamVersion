package stuffplotter.presenters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import stuffplotter.bindingcontracts.AccountModel;
import stuffplotter.client.services.AccountServiceAsync;
import stuffplotter.client.services.ServiceRepository;
import stuffplotter.server.AchievementChecker;
import stuffplotter.server.LevelUpdater;
import stuffplotter.shared.Account;
import stuffplotter.shared.AccountStatistic;
import stuffplotter.signals.RefreshPageEvent;
import stuffplotter.signals.RefreshPageEventHandler;
import stuffplotter.signals.UpdateStatsEvent;
import stuffplotter.views.friends.FriendAchievementPopupPanel;
import stuffplotter.views.friends.FriendPanelView;
import stuffplotter.views.friends.FriendPopupPanel;
import stuffplotter.views.friends.PendingFriendPanel;


/**
 * Class for the Friends Page presenter.
 */
public class FriendsPagePresenter implements Presenter
{
	public interface FriendsView
	{
		/**
		 * Retrieves the get Add Friend Button
		 * @pre
		 * @post
		 * @return AddFriendButton
		 */
		public HasClickHandlers getAddFriendBtn();
		
		/**
		 * Retrieves the get serach friends button
		 * @pre
		 * @post
		 * @return SearchFriendBtn
		 */
		public HasClickHandlers getSearchFriendsBtn();
		
		/**
		 * Retrieves the textbox object
		 * @pre
		 * @post
		 * @return the textbox object
		 */
		public HasAllFocusHandlers getFriendTextBox();
		
		/**
		 * Gets the string in the friend text box
		 * @pre
		 * @post
		 * @return String written in the textbox field
		 */
		public String getFriendBoxText();
		
		/**
		 * Clears the Add Friend Text box
		 * @pre
		 * @post
		 */
		public void clearFriendBoxText();


		/**
		 * Retrieve the views containing the 
		 * @pre true;
		 * @post true;
		 * @return the list of friend views in the display.
		 */
		public List<FriendPanelView> getFriendPanels();

		/**
		 * Retrieve the views containing the 
		 * @pre true;
		 * @post true;
		 * @return the list of friend views in the display.
		 */
		public List<PendingFriendPanel> getPendingFriendPanels();

		/**
		 * Set the list of friends to display.
		 * @pre model != null;
		 * @post true;
		 * @param models - the list of friends to display. 
		 */
		public void setFriendData(List<AccountModel> models);

		/**
		 * Set the list of friends to display.
		 * @pre model != null;
		 * @post true;
		 * @param models - the list of friends to display. 
		 */
		public void setPendingData(List<AccountModel> models);
		
		/**
		 * Gets the popup panel
		 * @pre true;
		 * @post true;
		 * @return FriendPopupPanel - the popup panel
		 */
		public FriendPopupPanel getFriendPopupPanel();
		
		/**
		 * Set the list of friends and their stats to display
		 * @pre
		 * @post
		 * @param friendAccount
		 * @param friendStats
		 */
		public void setAccountAndStatsData(Account friendAccount, AccountStatistic friendStats);
		/**
		 * Gets the FriendAchievementPopupPanel
		 * @pre true;
		 * @post true;
		 * @return FriendAchievementPopupPanel - the FriendAchievementPopupPanel
		 */
		public FriendAchievementPopupPanel getFriendAchievementPopupPanel();

		/**
		 * Set the stats of their friend to display
		 * @pre
		 * @post
		 * @param friendAccount
		 * @param friendStats
		 */
		public void setStatsData(AccountStatistic stats);
		/**
		 * Retrieve the FriendsView as a Widget.
		 * @pre true;
		 * @post true;
		 * @return the FriendsView as a Widget.
		 */
		public Widget asWidget();

	}

	private Account appUser;
	private AccountStatistic appStats;
	private final ServiceRepository appServices;
	private final HandlerManager eventBus;
	private final FriendsView friendsView;
	private List<AccountModel> pendingFriends;
	private List<AccountModel> friends;

	/**
	 * Constructor for the FriendsPagePresenter.
	 * @pre @pre appServices != null && eventBus != null && display != null && user != null;
	 * @post true;
	 * @param appServices - the repository containing all the services available for the application.
	 * @param eventBus - the event bus for the application.
	 * @param display - the FriendsView to associate with the FriendsPagePresenter.
	 */
	public FriendsPagePresenter(ServiceRepository appServices, HandlerManager eventBus, FriendsView display, Account user)
	{
		this.appUser = user;
		this.appServices = appServices;
		this.eventBus = eventBus;
		this.friendsView = display;
		this.pendingFriends = new ArrayList<AccountModel>();
		this.friends = new ArrayList<AccountModel>();
		this.dataBindFriends();

	}

	/**
	 * Helper method to data bind the Friends to the view.
	 * @pre true;
	 * @post true;
	 */

	private void dataBindFriends()
	{

		this.appServices.getStatsService().getStats(this.appUser.getUserEmail(),new AsyncCallback<AccountStatistic>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						
					}

					@Override
					public void onSuccess(AccountStatistic result)
					{
						appStats = result;
					}
			
				});
		
		
		
		final List<String> userFriends = appUser.getUserFriends();
		final List<String> pendingUsers = appUser.getPendingFriends();
		
		AccountServiceAsync accountService = appServices.getAccountService();
		accountService.getAccounts(userFriends, new AsyncCallback<Map<String, Account>>(){

			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert("Failed to get Friend User Accounts");

			}

			@Override
			public void onSuccess(Map<String, Account> result)
			{
				List<AccountModel> userAccounts = new ArrayList<AccountModel>();
				for(String friend :userFriends)
				{
					userAccounts.add(result.get(friend));
				}

				Collections.sort(userAccounts, new Account());
				friends = userAccounts;
				friendsView.setFriendData(friends);
				
				bindFriendPanels();

			}});
		accountService.getAccounts(pendingUsers, new AsyncCallback<Map<String, Account>>(){

			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert("Failed to get Pending User Accounts");

			}

			@Override
			public void onSuccess(Map<String, Account> result)
			{
				List<AccountModel> userAccounts = new ArrayList<AccountModel>();
				for(String friend :pendingUsers)
				{
					userAccounts.add(result.get(friend));
				}

				Collections.sort(userAccounts, new Account());
				pendingFriends = userAccounts;
				friendsView.setPendingData(pendingFriends);
				bindPendingFriendPanels();
			}

		});

	}



	/**
	 * Bind friends view components to handlers
	 * @pre true
	 * @post true
	 */
	private void bind()
	{
		this.eventBus.addHandler(RefreshPageEvent.TYPE, new RefreshPageEventHandler()
		{
			@Override
			public void onRefreshPage(RefreshPageEvent event)
			{
				AccountServiceAsync accountService = appServices.getAccountService();
				accountService.getAccount(appUser.getUserEmail(), new AsyncCallback<Account>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Account result)
					{
						appUser = result;
						dataBindFriends();
					}
				});
				
				
			}
		});


		this.friendsView.getAddFriendBtn().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event)
			{
				String friendEmail = friendsView.getFriendBoxText();
				int stringLen = friendEmail.length();
				if(friendEmail.length()==0)
				{
					Window.alert("This field cannot be blank!");
				}	
				else if(friendEmail.equals("Example: stuffplotter001@gmail.com"))
				{
					Window.alert("Enter your friends email");
				}
				else if(friendEmail.contains(" "))
				{
					Window.alert("This field cannot contain and spaces!");
				}
				else if(stringLen<=10||!friendEmail.substring(stringLen-10,stringLen).equals("@gmail.com"))
				{
					Window.alert("Did forget to add '@gmail.com' at the end of your friend's email account?");
				}
				else if(containFriend(friends, friendEmail))
				{
					Window.alert("Hey, you already have "+friendEmail+" in your Friends List!");
				}
				else if(containFriend(pendingFriends, friendEmail))
				{
					Window.alert("Hey, don't you see "+friendEmail+"'s request below? Hit 'Confirm' if you want to add them as a friend.");
				}
				else{
					appServices.getAccountService().addFriend(appUser, friendEmail, new AsyncCallback<Void>(){
						@Override
						public void onFailure(Throwable caught)
						{
							Window.alert("Something went wrong when adding a friend. Please contact support.");
						}

						@Override
						public void onSuccess(Void result)
						{
							Window.alert("A notification has been sent to "+friendsView.getFriendBoxText()+"! Please await their confirmation. =D");
							friendsView.clearFriendBoxText();
							eventBus.fireEvent(new RefreshPageEvent());
						}
					});
				}

			}

			private boolean containFriend(List<AccountModel> friends, String friendEmail)
			{
				for(AccountModel account : friends)
				{
					if(account.getUserEmail().equals(friendEmail))
						return true;
				}
				return false;
			}
		});

		friendsView.getFriendTextBox().addFocusHandler(new FocusHandler(){

			@Override
			public void onFocus(FocusEvent event)
			{
				if(friendsView.getFriendBoxText().contains(" "))
					friendsView.clearFriendBoxText();
			}});
	}

	/**
	 * Binds the friends View and Remove buttons
	 * @pre
	 * @post
	 */
	private void bindFriendPanels()
	{
		List<FriendPanelView> friendsPanels = friendsView.getFriendPanels();
		for(final FriendPanelView panel: friendsPanels)
		{
			final String friendEmail = panel.getEmail();
			final String friendName = panel.getName();
			panel.getRemoveBtn().addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event)
				{
					if(Window.confirm("Are you sure you want to remove "+friendName+" from your Friends List (Don't worry, we won't tell them)?"))
					{
						AccountServiceAsync accountService = appServices.getAccountService();
						accountService.removeFriend(appUser, friendEmail , new AsyncCallback<Void>(){

							@Override
							public void onFailure(Throwable caught)
							{

							}

							@Override
							public void onSuccess(Void result)
							{
								for(AccountModel acc: friends)
								{
									if(acc.getUserEmail().equals(friendEmail))
										{
											friends.remove(acc);
											break;
										}
										
								}
								eventBus.fireEvent(new RefreshPageEvent());
							}

						});

					}


				}

			});
			
			
			appServices.getAccountService().getAccount(friendEmail, new AsyncCallback<Account>()
			{

				@Override
				public void onFailure(Throwable caught)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Account result)
				{
					final Account friendAccount = result;
					appServices.getStatsService().getStats(friendEmail, new AsyncCallback<AccountStatistic>()
					{

						@Override
						public void onFailure(Throwable caught)
						{
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(AccountStatistic result)
						{
							final AccountStatistic friendStats = result;
							panel.getViewBtn().addClickHandler(new ClickHandler(){

										@Override
										public void onClick(ClickEvent event)
										{
											friendsView.setAccountAndStatsData(friendAccount, friendStats);
											friendsView.getFriendPopupPanel().getCloseBtn().addClickHandler(new ClickHandler()
											{
												
												@Override
												public void onClick(ClickEvent event)
												{
													friendsView.getFriendPopupPanel().hide();
													
												}
											});
											friendsView.getFriendPopupPanel().getAchievementBtn().addClickHandler(new ClickHandler()
											{
												
												@Override
												public void onClick(ClickEvent event)
												{
													friendsView.setStatsData(friendStats);
													friendsView.getFriendAchievementPopupPanel().getBackButton().addClickHandler(new ClickHandler()
													{
														
														@Override
														public void onClick(ClickEvent event)
														{
															friendsView.getFriendAchievementPopupPanel().hide();
															friendsView.getFriendPopupPanel().show();
															
														}
													});
													friendsView.getFriendAchievementPopupPanel().getCloseButton().addClickHandler(new ClickHandler()
													{
														
														@Override
														public void onClick(ClickEvent event)
														{
															friendsView.getFriendAchievementPopupPanel().hide();
															
														}
													});
													friendsView.getFriendPopupPanel().hide();
													friendsView.getFriendAchievementPopupPanel().show();
													
												}
											});
											friendsView.getFriendPopupPanel().show();

										}

									});
						}
					});
					
				}
			});
			
			
			

		}
	}
	/**
	 * Binds the Buttons for the Pending Friends
	 * @pre
	 * @post
	 */
	private void bindPendingFriendPanels()
	{
		List<PendingFriendPanel> pendingFriendsPanels = friendsView.getPendingFriendPanels();
		for(final PendingFriendPanel panel: pendingFriendsPanels)
		{
			final String friendEmail = panel.getEmail();
			final String friendName = panel.getName();
			

			
			
			
			
			
			
			panel.getConfirmBtn().addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event)
				{
					
					AccountServiceAsync accountService = appServices.getAccountService();
					accountService.confirmFriendReq(appUser, friendEmail, new AsyncCallback<Void>(){

						@Override
						public void onFailure(Throwable caught)
						{
							Window.alert("Friend was not successfully added...");
						}

						@Override
						public void onSuccess(Void result)
						{
							appStats.incrementFriends();
							Window.alert(friendName+" successfully added to your friends list!!");
							
							for(AccountModel acc: pendingFriends)
							{
								if(acc.getUserEmail().equals(friendEmail))
									{
										pendingFriends.remove(acc);
										friends.add(acc);
										Collections.sort(friends, new Account());
										break;
									}
									
							}
							panel.setVisible(false);
							eventBus.fireEvent(new RefreshPageEvent());
							appStats.accept(new LevelUpdater().madeFriend());
							appStats.accept(new AchievementChecker());
							eventBus.fireEvent(new UpdateStatsEvent(appUser.getUserEmail()));
							appServices.getStatsService().getStats(friendEmail,new AsyncCallback<AccountStatistic>()
									{

										@Override
										public void onFailure(Throwable caught)
										{
											
										}

										@Override
										public void onSuccess(AccountStatistic result)
										{
											result.accept(new LevelUpdater().madeFriend());
											
										}
								
									});
							
							
							eventBus.fireEvent(new RefreshPageEvent());


						}

					});

					
				}});
			panel.getDenyBtn().addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event)
				{
					;
					AccountServiceAsync accountService = appServices.getAccountService();
					if(Window.confirm("Are you sure you want to deny this friend request (Don't worry, we won't tell them)?"))
					{
						panel.setVisible(false);
						accountService.declineFriendReq(appUser, friendEmail, new AsyncCallback<Void>(){

							@Override
							public void onFailure(Throwable caught)
							{
								Window.alert("Something went wrong while denying a friend request. Please contact support.");

							}

							@Override
							public void onSuccess(Void result)
							{
								for(AccountModel acc: pendingFriends)
								{
									if(acc.getUserEmail().equals(friendEmail))
										{
											pendingFriends.remove(acc);
											break;
										}
										
								}
								
							}

						});
					}
					
				}});
		}
	}




	/**
	 * Present the friends view
	 * @pre true;
	 * @post this.homeView.isVisible() == true;
	 */
	@Override
	public void go(HasWidgets container)
	{
		bind();
		container.add(this.friendsView.asWidget());
	}




	public void doRefresh()
	{

	}
}
