package space.wenliang.ai.aigcplatformserver.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class Page<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8545996863226528798L;

    private long current = 1;

    private long pageSize = 10;

    private long total = 0;

    protected List<T> records = Collections.emptyList();

    public Page(long current, long pageSize, long total) {
        this.current = current;
        this.pageSize = pageSize;
        this.total = total;
    }
}
