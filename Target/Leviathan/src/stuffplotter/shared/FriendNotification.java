package stuffplotter.shared;

public class FriendNotification extends Notification
{
	public enum FriendNotificationType
	{
		FRIENDREQUEST, FRIENDACCEPTED
	}

	private FriendNotificationType friendType;

	/**
	 * Constructor for a FriendNotification.
	 * @pre id >= 0 && type != null && fromUser != null && forUser != null;
	 * @post this.id == id && this.type.equals(type) &&
	 * this.notificationFrom.equals(fromUser) && this.notificationFor.equals(forUser);
	 * @param id - the ID of the user that sent the friend request.
	 * @param type - the type of the notification, should be FRIENDREQUEST.
	 * @param fromUser - the user that created the event.
	 * @param forUser - the guest invited to the event.
	 */
	public FriendNotification()
	{
		
	}
	
	public FriendNotification(FriendNotificationType type, String forUser, String fromUser)
	{
		super(NotificationType.FRIENDREQUEST, forUser, fromUser);
		this.friendType = type;
		this.setNotificationDisplay(type, fromUser);
	}


	public FriendNotificationType getFriendType()
	{
		return friendType;
	}

	public void setFriendType(FriendNotificationType type)
	{
		this.friendType = type;
	}
	
	public void setNotificationDisplay(FriendNotificationType type, String fromUser)
	{
		if(type.equals(FriendNotificationType.FRIENDREQUEST))
		{
			String display = "Someone from "+fromUser + " would like to add you as a friend on Stuff Plotter!";
			this.notificationDisplay = display;
		}else if(type.equals(FriendNotificationType.FRIENDACCEPTED))
		{
			String display = "You and "+fromUser+" are now officially friends!!";
			this.notificationDisplay = display;
		}
		
	}

	/**
	 * Serial version for FriendNotification.
	 */
	private static final long serialVersionUID = -4195766643137628383L;
}