package td3.td2;

@FunctionalInterface
public interface ToString<T> {
    String convert(T source);
}
