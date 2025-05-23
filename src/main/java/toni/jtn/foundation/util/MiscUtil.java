package toni.jtn.foundation.util;

import java.util.LinkedHashSet;
import java.util.Set;

public class MiscUtil {
    @SafeVarargs
    public static <T> Set<T> linkedSet(T... objects) {
        var set = new LinkedHashSet<T>();
        for (T t : objects) {
            set.add(t);
        }
        return set;
    }
}
