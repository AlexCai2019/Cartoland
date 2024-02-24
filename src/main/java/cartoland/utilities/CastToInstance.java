package cartoland.utilities;

import java.util.*;

public final class CastToInstance
{
	private CastToInstance()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	@SuppressWarnings("rawtypes")
	public static Map modifiableMap(Object o)
	{
		return o != null ? switch (o)
		{
			case HashMap hashMap -> hashMap;
			case TreeMap treeMap -> treeMap;
			case EnumMap enumMap -> enumMap;
			default -> new HashMap<>();
		} : new HashMap<>();
	}

	@SuppressWarnings("rawtypes")
	public static Set modifiableSet(Object o)
	{
		return o != null ? switch (o)
		{
			case HashSet hashSet -> hashSet;
			case TreeSet treeSet -> treeSet;
			case EnumSet enumSet -> enumSet;
			default -> new HashSet<>();
		} : new HashSet<>();
	}
}