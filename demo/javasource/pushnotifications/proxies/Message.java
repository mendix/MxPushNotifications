// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package pushnotifications.proxies;

public class Message
{
	private final com.mendix.systemwideinterfaces.core.IMendixObject messageMendixObject;

	private final com.mendix.systemwideinterfaces.core.IContext context;

	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "PushNotifications.Message";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		MessageId("MessageId"),
		To("To"),
		DeviceType("DeviceType"),
		Title("Title"),
		Message("Message"),
		Failed("Failed"),
		FailedReason("FailedReason"),
		FailedCount("FailedCount"),
		NextTry("NextTry"),
		Queued("Queued"),
		ActionName("ActionName"),
		ContextObjectGuid("ContextObjectGuid"),
		Message_Device("PushNotifications.Message_Device");

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

	public Message(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "PushNotifications.Message"));
	}

	protected Message(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject messageMendixObject)
	{
		if (messageMendixObject == null)
			throw new java.lang.IllegalArgumentException("The given object cannot be null.");
		if (!com.mendix.core.Core.isSubClassOf("PushNotifications.Message", messageMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a PushNotifications.Message");

		this.messageMendixObject = messageMendixObject;
		this.context = context;
	}

	/**
	 * @deprecated Use 'Message.load(IContext, IMendixIdentifier)' instead.
	 */
	@Deprecated
	public static pushnotifications.proxies.Message initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return pushnotifications.proxies.Message.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.getSudoContext() can be used to obtain sudo access).
	 */
	public static pushnotifications.proxies.Message initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		if (com.mendix.core.Core.isSubClassOf("PushNotifications.AppleMessage", mendixObject.getType()))
			return pushnotifications.proxies.AppleMessage.initialize(context, mendixObject);

		if (com.mendix.core.Core.isSubClassOf("PushNotifications.GoogleMessage", mendixObject.getType()))
			return pushnotifications.proxies.GoogleMessage.initialize(context, mendixObject);

		return new pushnotifications.proxies.Message(context, mendixObject);
	}

	public static pushnotifications.proxies.Message load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return pushnotifications.proxies.Message.initialize(context, mendixObject);
	}

	public static java.util.List<? extends pushnotifications.proxies.Message> load(com.mendix.systemwideinterfaces.core.IContext context, java.lang.String xpathConstraint) throws com.mendix.core.CoreException
	{
		java.util.List<pushnotifications.proxies.Message> result = new java.util.ArrayList<pushnotifications.proxies.Message>();
		for (com.mendix.systemwideinterfaces.core.IMendixObject obj : com.mendix.core.Core.retrieveXPathQuery(context, "//PushNotifications.Message" + xpathConstraint))
			result.add(pushnotifications.proxies.Message.initialize(context, obj));
		return result;
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
	 * @return value of MessageId
	 */
	public final String getMessageId()
	{
		return getMessageId(getContext());
	}

	/**
	 * @param context
	 * @return value of MessageId
	 */
	public final String getMessageId(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.MessageId.toString());
	}

	/**
	 * Set value of MessageId
	 * @param messageid
	 */
	public final void setMessageId(String messageid)
	{
		setMessageId(getContext(), messageid);
	}

	/**
	 * Set value of MessageId
	 * @param context
	 * @param messageid
	 */
	public final void setMessageId(com.mendix.systemwideinterfaces.core.IContext context, String messageid)
	{
		getMendixObject().setValue(context, MemberNames.MessageId.toString(), messageid);
	}

	/**
	 * @return value of To
	 */
	public final String getTo()
	{
		return getTo(getContext());
	}

	/**
	 * @param context
	 * @return value of To
	 */
	public final String getTo(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.To.toString());
	}

	/**
	 * Set value of To
	 * @param to
	 */
	public final void setTo(String to)
	{
		setTo(getContext(), to);
	}

	/**
	 * Set value of To
	 * @param context
	 * @param to
	 */
	public final void setTo(com.mendix.systemwideinterfaces.core.IContext context, String to)
	{
		getMendixObject().setValue(context, MemberNames.To.toString(), to);
	}

	/**
	 * Set value of DeviceType
	 * @param devicetype
	 */
	public final pushnotifications.proxies.DeviceType getDeviceType()
	{
		return getDeviceType(getContext());
	}

	/**
	 * @param context
	 * @return value of DeviceType
	 */
	public final pushnotifications.proxies.DeviceType getDeviceType(com.mendix.systemwideinterfaces.core.IContext context)
	{
		Object obj = getMendixObject().getValue(context, MemberNames.DeviceType.toString());
		if (obj == null)
			return null;

		return pushnotifications.proxies.DeviceType.valueOf((java.lang.String) obj);
	}

	/**
	 * Set value of DeviceType
	 * @param devicetype
	 */
	public final void setDeviceType(pushnotifications.proxies.DeviceType devicetype)
	{
		setDeviceType(getContext(), devicetype);
	}

	/**
	 * Set value of DeviceType
	 * @param context
	 * @param devicetype
	 */
	public final void setDeviceType(com.mendix.systemwideinterfaces.core.IContext context, pushnotifications.proxies.DeviceType devicetype)
	{
		if (devicetype != null)
			getMendixObject().setValue(context, MemberNames.DeviceType.toString(), devicetype.toString());
		else
			getMendixObject().setValue(context, MemberNames.DeviceType.toString(), null);
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
	 * @return value of Failed
	 */
	public final Boolean getFailed()
	{
		return getFailed(getContext());
	}

	/**
	 * @param context
	 * @return value of Failed
	 */
	public final Boolean getFailed(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Boolean) getMendixObject().getValue(context, MemberNames.Failed.toString());
	}

	/**
	 * Set value of Failed
	 * @param failed
	 */
	public final void setFailed(Boolean failed)
	{
		setFailed(getContext(), failed);
	}

	/**
	 * Set value of Failed
	 * @param context
	 * @param failed
	 */
	public final void setFailed(com.mendix.systemwideinterfaces.core.IContext context, Boolean failed)
	{
		getMendixObject().setValue(context, MemberNames.Failed.toString(), failed);
	}

	/**
	 * @return value of FailedReason
	 */
	public final String getFailedReason()
	{
		return getFailedReason(getContext());
	}

	/**
	 * @param context
	 * @return value of FailedReason
	 */
	public final String getFailedReason(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.FailedReason.toString());
	}

	/**
	 * Set value of FailedReason
	 * @param failedreason
	 */
	public final void setFailedReason(String failedreason)
	{
		setFailedReason(getContext(), failedreason);
	}

	/**
	 * Set value of FailedReason
	 * @param context
	 * @param failedreason
	 */
	public final void setFailedReason(com.mendix.systemwideinterfaces.core.IContext context, String failedreason)
	{
		getMendixObject().setValue(context, MemberNames.FailedReason.toString(), failedreason);
	}

	/**
	 * @return value of FailedCount
	 */
	public final Integer getFailedCount()
	{
		return getFailedCount(getContext());
	}

	/**
	 * @param context
	 * @return value of FailedCount
	 */
	public final Integer getFailedCount(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (Integer) getMendixObject().getValue(context, MemberNames.FailedCount.toString());
	}

	/**
	 * Set value of FailedCount
	 * @param failedcount
	 */
	public final void setFailedCount(Integer failedcount)
	{
		setFailedCount(getContext(), failedcount);
	}

	/**
	 * Set value of FailedCount
	 * @param context
	 * @param failedcount
	 */
	public final void setFailedCount(com.mendix.systemwideinterfaces.core.IContext context, Integer failedcount)
	{
		getMendixObject().setValue(context, MemberNames.FailedCount.toString(), failedcount);
	}

	/**
	 * @return value of NextTry
	 */
	public final java.util.Date getNextTry()
	{
		return getNextTry(getContext());
	}

	/**
	 * @param context
	 * @return value of NextTry
	 */
	public final java.util.Date getNextTry(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (java.util.Date) getMendixObject().getValue(context, MemberNames.NextTry.toString());
	}

	/**
	 * Set value of NextTry
	 * @param nexttry
	 */
	public final void setNextTry(java.util.Date nexttry)
	{
		setNextTry(getContext(), nexttry);
	}

	/**
	 * Set value of NextTry
	 * @param context
	 * @param nexttry
	 */
	public final void setNextTry(com.mendix.systemwideinterfaces.core.IContext context, java.util.Date nexttry)
	{
		getMendixObject().setValue(context, MemberNames.NextTry.toString(), nexttry);
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
	 * @return value of Message_Device
	 */
	public final pushnotifications.proxies.Device getMessage_Device() throws com.mendix.core.CoreException
	{
		return getMessage_Device(getContext());
	}

	/**
	 * @param context
	 * @return value of Message_Device
	 */
	public final pushnotifications.proxies.Device getMessage_Device(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		pushnotifications.proxies.Device result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.Message_Device.toString());
		if (identifier != null)
			result = pushnotifications.proxies.Device.load(context, identifier);
		return result;
	}

	/**
	 * Set value of Message_Device
	 * @param message_device
	 */
	public final void setMessage_Device(pushnotifications.proxies.Device message_device)
	{
		setMessage_Device(getContext(), message_device);
	}

	/**
	 * Set value of Message_Device
	 * @param context
	 * @param message_device
	 */
	public final void setMessage_Device(com.mendix.systemwideinterfaces.core.IContext context, pushnotifications.proxies.Device message_device)
	{
		if (message_device == null)
			getMendixObject().setValue(context, MemberNames.Message_Device.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.Message_Device.toString(), message_device.getMendixObject().getId());
	}

	/**
	 * @return the IMendixObject instance of this proxy for use in the Core interface.
	 */
	public final com.mendix.systemwideinterfaces.core.IMendixObject getMendixObject()
	{
		return messageMendixObject;
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
			final pushnotifications.proxies.Message that = (pushnotifications.proxies.Message) obj;
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
		return "PushNotifications.Message";
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