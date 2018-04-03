// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package pushnotifications.proxies;

public class MessageView
{
	private final com.mendix.systemwideinterfaces.core.IMendixObject messageViewMendixObject;

	private final com.mendix.systemwideinterfaces.core.IContext context;

	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "PushNotifications.MessageView";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		Title("Title"),
		Message("Message"),
		Queued("Queued"),
		Badge("Badge"),
		TimeToLive("TimeToLive"),
		Sound("Sound"),
		LaunchImage("LaunchImage"),
		ActionName("ActionName"),
		ContextObjectGuid("ContextObjectGuid"),
		MessageView_Device("PushNotifications.MessageView_Device"),
		MessageView_Message("PushNotifications.MessageView_Message");

		private java.lang.String metaName;

		MemberNames(java.lang.String s)
		{
			metaName = s;
		}

		@Override
		public java.lang.String toString()
		{
			return metaName;
		}
	}

	public MessageView(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "PushNotifications.MessageView"));
	}

	protected MessageView(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject messageViewMendixObject)
	{
		if (messageViewMendixObject == null)
			throw new java.lang.IllegalArgumentException("The given object cannot be null.");
		if (!com.mendix.core.Core.isSubClassOf("PushNotifications.MessageView", messageViewMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a PushNotifications.MessageView");

		this.messageViewMendixObject = messageViewMendixObject;
		this.context = context;
	}

	/**
	 * @deprecated Use 'MessageView.load(IContext, IMendixIdentifier)' instead.
	 */
	@Deprecated
	public static pushnotifications.proxies.MessageView initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return pushnotifications.proxies.MessageView.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.getSudoContext() can be used to obtain sudo access).
	 */
	public static pushnotifications.proxies.MessageView initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		return new pushnotifications.proxies.MessageView(context, mendixObject);
	}

	public static pushnotifications.proxies.MessageView load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return pushnotifications.proxies.MessageView.initialize(context, mendixObject);
	}

	/**
	 * Commit the changes made on this proxy object.
	 */
	public final void commit() throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Commit the changes made on this proxy object using the specified context.
	 */
	public final void commit(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Delete the object.
	 */
	public final void delete()
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}

	/**
	 * Delete the object using the specified context.
	 */
	public final void delete(com.mendix.systemwideinterfaces.core.IContext context)
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}
	/**
	 * @return value of Title
	 */
	public final String getTitle()
	{
		return getTitle(getContext());
	}

	/**
	 * @param context
	 * @return value of Title
	 */
	public final String getTitle(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.Title.toString());
	}

	/**
	 * Set value of Title
	 * @param title
	 */
	public final void setTitle(String title)
	{
		setTitle(getContext(), title);
	}

	/**
	 * Set value of Title
	 * @param context
	 * @param title
	 */
	public final void setTitle(com.mendix.systemwideinterfaces.core.IContext context, String title)
	{
		getMendixObject().setValue(context, MemberNames.Title.toString(), title);
	}

	/**
	 * @return value of Message
	 */
	public final String getMessage()
	{
		return getMessage(getContext());
	}

	/**
	 * @param context
	 * @return value of Message
	 */
	public final String getMessage(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.Message.toString());
	}

	/**
	 * Set value of Message
	 * @param message
	 */
	public final void setMessage(String message)
	{
		setMessage(getContext(), message);
	}

	/**
	 * Set value of Message
	 * @param context
	 * @param message
	 */
	public final void setMessage(com.mendix.systemwideinterfaces.core.IContext context, String message)
	{
		getMendixObject().setValue(context, MemberNames.Message.toString(), message);
	}

	/**
	 * @return value of Queued
	 */
	public final Boolean getQueued()
	{
		return getQueued(getContext());
	}

	/**
	 * @param context
	 * @return value of Queued
	 */
	public final Boolean getQueued(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Boolean) getMendixObject().getValue(context, MemberNames.Queued.toString());
	}

	/**
	 * Set value of Queued
	 * @param queued
	 */
	public final void setQueued(Boolean queued)
	{
		setQueued(getContext(), queued);
	}

	/**
	 * Set value of Queued
	 * @param context
	 * @param queued
	 */
	public final void setQueued(com.mendix.systemwideinterfaces.core.IContext context, Boolean queued)
	{
		getMendixObject().setValue(context, MemberNames.Queued.toString(), queued);
	}

	/**
	 * @return value of Badge
	 */
	public final Integer getBadge()
	{
		return getBadge(getContext());
	}

	/**
	 * @param context
	 * @return value of Badge
	 */
	public final Integer getBadge(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Integer) getMendixObject().getValue(context, MemberNames.Badge.toString());
	}

	/**
	 * Set value of Badge
	 * @param badge
	 */
	public final void setBadge(Integer badge)
	{
		setBadge(getContext(), badge);
	}

	/**
	 * Set value of Badge
	 * @param context
	 * @param badge
	 */
	public final void setBadge(com.mendix.systemwideinterfaces.core.IContext context, Integer badge)
	{
		getMendixObject().setValue(context, MemberNames.Badge.toString(), badge);
	}

	/**
	 * @return value of TimeToLive
	 */
	public final Long getTimeToLive()
	{
		return getTimeToLive(getContext());
	}

	/**
	 * @param context
	 * @return value of TimeToLive
	 */
	public final Long getTimeToLive(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Long) getMendixObject().getValue(context, MemberNames.TimeToLive.toString());
	}

	/**
	 * Set value of TimeToLive
	 * @param timetolive
	 */
	public final void setTimeToLive(Long timetolive)
	{
		setTimeToLive(getContext(), timetolive);
	}

	/**
	 * Set value of TimeToLive
	 * @param context
	 * @param timetolive
	 */
	public final void setTimeToLive(com.mendix.systemwideinterfaces.core.IContext context, Long timetolive)
	{
		getMendixObject().setValue(context, MemberNames.TimeToLive.toString(), timetolive);
	}

	/**
	 * @return value of Sound
	 */
	public final String getSound()
	{
		return getSound(getContext());
	}

	/**
	 * @param context
	 * @return value of Sound
	 */
	public final String getSound(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.Sound.toString());
	}

	/**
	 * Set value of Sound
	 * @param sound
	 */
	public final void setSound(String sound)
	{
		setSound(getContext(), sound);
	}

	/**
	 * Set value of Sound
	 * @param context
	 * @param sound
	 */
	public final void setSound(com.mendix.systemwideinterfaces.core.IContext context, String sound)
	{
		getMendixObject().setValue(context, MemberNames.Sound.toString(), sound);
	}

	/**
	 * @return value of LaunchImage
	 */
	public final String getLaunchImage()
	{
		return getLaunchImage(getContext());
	}

	/**
	 * @param context
	 * @return value of LaunchImage
	 */
	public final String getLaunchImage(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.LaunchImage.toString());
	}

	/**
	 * Set value of LaunchImage
	 * @param launchimage
	 */
	public final void setLaunchImage(String launchimage)
	{
		setLaunchImage(getContext(), launchimage);
	}

	/**
	 * Set value of LaunchImage
	 * @param context
	 * @param launchimage
	 */
	public final void setLaunchImage(com.mendix.systemwideinterfaces.core.IContext context, String launchimage)
	{
		getMendixObject().setValue(context, MemberNames.LaunchImage.toString(), launchimage);
	}

	/**
	 * @return value of ActionName
	 */
	public final String getActionName()
	{
		return getActionName(getContext());
	}

	/**
	 * @param context
	 * @return value of ActionName
	 */
	public final String getActionName(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.ActionName.toString());
	}

	/**
	 * Set value of ActionName
	 * @param actionname
	 */
	public final void setActionName(String actionname)
	{
		setActionName(getContext(), actionname);
	}

	/**
	 * Set value of ActionName
	 * @param context
	 * @param actionname
	 */
	public final void setActionName(com.mendix.systemwideinterfaces.core.IContext context, String actionname)
	{
		getMendixObject().setValue(context, MemberNames.ActionName.toString(), actionname);
	}

	/**
	 * @return value of ContextObjectGuid
	 */
	public final Long getContextObjectGuid()
	{
		return getContextObjectGuid(getContext());
	}

	/**
	 * @param context
	 * @return value of ContextObjectGuid
	 */
	public final Long getContextObjectGuid(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Long) getMendixObject().getValue(context, MemberNames.ContextObjectGuid.toString());
	}

	/**
	 * Set value of ContextObjectGuid
	 * @param contextobjectguid
	 */
	public final void setContextObjectGuid(Long contextobjectguid)
	{
		setContextObjectGuid(getContext(), contextobjectguid);
	}

	/**
	 * Set value of ContextObjectGuid
	 * @param context
	 * @param contextobjectguid
	 */
	public final void setContextObjectGuid(com.mendix.systemwideinterfaces.core.IContext context, Long contextobjectguid)
	{
		getMendixObject().setValue(context, MemberNames.ContextObjectGuid.toString(), contextobjectguid);
	}

	/**
	 * @return value of MessageView_Device
	 */
	public final java.util.List<pushnotifications.proxies.Device> getMessageView_Device() throws com.mendix.core.CoreException
	{
		return getMessageView_Device(getContext());
	}

	/**
	 * @param context
	 * @return value of MessageView_Device
	 */
	@SuppressWarnings("unchecked")
	public final java.util.List<pushnotifications.proxies.Device> getMessageView_Device(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		java.util.List<pushnotifications.proxies.Device> result = new java.util.ArrayList<pushnotifications.proxies.Device>();
		Object valueObject = getMendixObject().getValue(context, MemberNames.MessageView_Device.toString());
		if (valueObject == null)
			return result;
		for (com.mendix.systemwideinterfaces.core.IMendixObject mendixObject : com.mendix.core.Core.retrieveIdList(context, (java.util.List<com.mendix.systemwideinterfaces.core.IMendixIdentifier>) valueObject))
			result.add(pushnotifications.proxies.Device.initialize(context, mendixObject));
		return result;
	}

	/**
	 * Set value of MessageView_Device
	 * @param messageview_device
	 */
	public final void setMessageView_Device(java.util.List<pushnotifications.proxies.Device> messageview_device)
	{
		setMessageView_Device(getContext(), messageview_device);
	}

	/**
	 * Set value of MessageView_Device
	 * @param context
	 * @param messageview_device
	 */
	public final void setMessageView_Device(com.mendix.systemwideinterfaces.core.IContext context, java.util.List<pushnotifications.proxies.Device> messageview_device)
	{
		java.util.List<com.mendix.systemwideinterfaces.core.IMendixIdentifier> identifiers = new java.util.ArrayList<com.mendix.systemwideinterfaces.core.IMendixIdentifier>();
		for (pushnotifications.proxies.Device proxyObject : messageview_device)
			identifiers.add(proxyObject.getMendixObject().getId());
		getMendixObject().setValue(context, MemberNames.MessageView_Device.toString(), identifiers);
	}

	/**
	 * @return value of MessageView_Message
	 */
	public final java.util.List<pushnotifications.proxies.Message> getMessageView_Message() throws com.mendix.core.CoreException
	{
		return getMessageView_Message(getContext());
	}

	/**
	 * @param context
	 * @return value of MessageView_Message
	 */
	@SuppressWarnings("unchecked")
	public final java.util.List<pushnotifications.proxies.Message> getMessageView_Message(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		java.util.List<pushnotifications.proxies.Message> result = new java.util.ArrayList<pushnotifications.proxies.Message>();
		Object valueObject = getMendixObject().getValue(context, MemberNames.MessageView_Message.toString());
		if (valueObject == null)
			return result;
		for (com.mendix.systemwideinterfaces.core.IMendixObject mendixObject : com.mendix.core.Core.retrieveIdList(context, (java.util.List<com.mendix.systemwideinterfaces.core.IMendixIdentifier>) valueObject))
			result.add(pushnotifications.proxies.Message.initialize(context, mendixObject));
		return result;
	}

	/**
	 * Set value of MessageView_Message
	 * @param messageview_message
	 */
	public final void setMessageView_Message(java.util.List<pushnotifications.proxies.Message> messageview_message)
	{
		setMessageView_Message(getContext(), messageview_message);
	}

	/**
	 * Set value of MessageView_Message
	 * @param context
	 * @param messageview_message
	 */
	public final void setMessageView_Message(com.mendix.systemwideinterfaces.core.IContext context, java.util.List<pushnotifications.proxies.Message> messageview_message)
	{
		java.util.List<com.mendix.systemwideinterfaces.core.IMendixIdentifier> identifiers = new java.util.ArrayList<com.mendix.systemwideinterfaces.core.IMendixIdentifier>();
		for (pushnotifications.proxies.Message proxyObject : messageview_message)
			identifiers.add(proxyObject.getMendixObject().getId());
		getMendixObject().setValue(context, MemberNames.MessageView_Message.toString(), identifiers);
	}

	/**
	 * @return the IMendixObject instance of this proxy for use in the Core interface.
	 */
	public final com.mendix.systemwideinterfaces.core.IMendixObject getMendixObject()
	{
		return messageViewMendixObject;
	}

	/**
	 * @return the IContext instance of this proxy, or null if no IContext instance was specified at initialization.
	 */
	public final com.mendix.systemwideinterfaces.core.IContext getContext()
	{
		return context;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj != null && getClass().equals(obj.getClass()))
		{
			final pushnotifications.proxies.MessageView that = (pushnotifications.proxies.MessageView) obj;
			return getMendixObject().equals(that.getMendixObject());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getMendixObject().hashCode();
	}

	/**
	 * @return String name of this class
	 */
	public static java.lang.String getType()
	{
		return "PushNotifications.MessageView";
	}

	/**
	 * @return String GUID from this object, format: ID_0000000000
	 * @deprecated Use getMendixObject().getId().toLong() to get a unique identifier for this object.
	 */
	@Deprecated
	public java.lang.String getGUID()
	{
		return "ID_" + getMendixObject().getId().toLong();
	}
}