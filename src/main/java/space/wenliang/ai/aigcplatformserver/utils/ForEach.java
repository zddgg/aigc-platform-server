package space.wenliang.ai.aigcplatformserver.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ForEach {

    public static <T> void forEach(Stream<? extends T> stream, BiConsumer<Integer, ? super T> action) {
        forEach(0, stream, action);
    }

    public static <T> void forEach(int startIndex, Stream<? extends T> stream, BiConsumer<Integer, ? super T> action) {
        Objects.requireNonNull(action);
        if (startIndex < 0) {
            startIndex = 0;
        }
        AtomicInteger index = new AtomicInteger();
        int finalStartIndex = startIndex;
        stream.forEach(element -> {
            index.getAndIncrement();
            if (index.get() > finalStartIndex) {
                action.accept(index.get() - 1, element);
            }
        });
    }

    public static <T> void forEach(Iterable<? extends T> elements, BiConsumer<Integer, ? super T> action) {
        forEach(0, elements, action);
    }

    public static <T> void forEach(int startIndex, Iterable<? extends T> elements, BiConsumer<Integer, ? super T> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);
        if (startIndex < 0) {
            startIndex = 0;
        }
        int index = 0;
        for (T element : elements) {
            index++;
            if (index <= startIndex) {
                continue;
            }
            action.accept(index - 1, element);
        }
    }
}
