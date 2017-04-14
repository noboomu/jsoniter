/**
 * 
 */
package com.jsoniter.views;


/**
 * @author jbauer
 *
 */
public class ObjectViewEntry
{
	public final Class<?> objectClass;
	public final Class<?> viewClass;
	/**
	 * @param objectClass
	 * @param viewClass
	 */
	public ObjectViewEntry(Class<?> objectClass, Class<?> viewClass)
	{ 
		this.objectClass = objectClass;
		this.viewClass = viewClass;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectClass == null) ? 0 : objectClass.hashCode());
		result = prime * result + ((viewClass == null) ? 0 : viewClass.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectViewEntry other = (ObjectViewEntry) obj;
		if (objectClass == null)
		{
			if (other.objectClass != null)
				return false;
		}
		else if (!objectClass.equals(other.objectClass))
			return false;
		if (viewClass == null)
		{
			if (other.viewClass != null)
				return false;
		}
		else if (!viewClass.equals(other.viewClass))
			return false;
		return true;
	}
	 
}