package Interface;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Extend this class you have to declare below methods
 * public static reinterpret_cast(byte[] , int);
 * public static reinterpret_cast(byte[]);
 * public static int size();
 */
public abstract class CastSupport {

	public CastSupport() {
		try {
			Method reinterpret_cast_b_i = getClass().getDeclaredMethod("reinterpret_cast", byte[].class, Integer.TYPE);
			Method reinterpret_cast_i = getClass().getDeclaredMethod("reinterpret_cast", byte[].class);
			Method size = getClass().getDeclaredMethod("size");

			if (verifyModifier(reinterpret_cast_b_i.getModifiers()) && verifyModifier(reinterpret_cast_i.getModifiers())
					&& verifyModifier(size.getModifiers()))
				if (reinterpret_cast_b_i.getReturnType().equals(getClass())
						&& reinterpret_cast_i.getReturnType().equals(getClass())
						&& size.getReturnType().equals(Integer.TYPE))
					return;

			throw new RuntimeException("illegal modifier");

		} catch (NoSuchMethodException | SecurityException e) {
			RuntimeException r = new RuntimeException("method not declared", e);
			throw r;
		}
	}

	private boolean verifyModifier(int modifier) {
		return Modifier.isStatic(modifier) && Modifier.isPublic(modifier);
	}

}
