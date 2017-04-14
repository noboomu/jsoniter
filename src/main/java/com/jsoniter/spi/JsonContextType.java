/**
 * 
 */
package com.jsoniter.spi;

import java.lang.reflect.Type;

import com.jsoniter.output.JsonContext;

/**
 * @author jbauer
 *
 */
public class JsonContextType
{ 
	public Type objectType;
	public Class<? extends JsonContext> contextClass;
	
	public JsonContextType(Type objectType,Class<? extends JsonContext> contextClass)
	{
		this.objectType = objectType;
		this.contextClass = contextClass; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contextClass == null) ? 0 : contextClass.hashCode());
		result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
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
		JsonContextType other = (JsonContextType) obj;
		if (contextClass == null)
		{
			if (other.contextClass != null)
				return false;
		}
		else if (!contextClass.equals(other.contextClass))
			return false;
		if (objectType == null)
		{
			if (other.objectType != null)
				return false;
		}
		else if (!objectType.equals(other.objectType))
			return false;
		return true;
	}

}
