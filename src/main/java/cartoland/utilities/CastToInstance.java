package cartoland.utilities;

import java.util.*;

public final class CastToInstance
{
	private CastToInstance()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Map modifiableMap(Object o)
	{
		return switch (o)
		{
			case HashMap hashMap -> hashMap;
			case TreeMap treeMap -> treeMap;
			case EnumMap enumMap -> enumMap;
			case IdentityHashMap identityHashMap -> identityHashMap;
			case WeakHashMap weakHashMap -> weakHashMap;
			case Hashtable hashtable -> hashtable;
			case Map map -> new HashMap<>(map);
			case null, default -> new HashMap<>();
		};
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Set modifiableSet(Object o)
	{
		return switch (o)
		{
			case HashSet hashSet -> hashSet;
			case TreeSet treeSet -> treeSet;
			case EnumSet enumSet -> enumSet;
			case Set set -> new HashSet<>(set);
			case null, default -> new HashSet<>();
		};
	}
}