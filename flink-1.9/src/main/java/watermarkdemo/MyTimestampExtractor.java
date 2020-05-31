package watermarkdemo;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.text.SimpleDateFormat;

/**
 * @author zjwpro@foxmail.com
 * @date 2019/4/21 3:28 PM
 * 自定义时间戳获取器
 */
public class MyTimestampExtractor implements AssignerWithPeriodicWatermarks<Tuple2<String, Long>> {
    private static final long serialVersionUID = 1L;

    /**
     * 到目前为止最大的时间戳
     */
    private long currentMaxTimestamp;

    /**
     * 最后的水印时间戳
     */
    private long lastEmittedWatermark = 10000L;

    /**
     * 在记录中看到的最大可见时间戳与要发出的水印的时间戳之间的（固定）间隔。
     */
    private final long maxOutOfOrderness;

    private Watermark a;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MyTimestampExtractor(Time maxOutOfOrderness) {
        if (maxOutOfOrderness.toMilliseconds() < 0) {
            throw new RuntimeException("Tried to set the maximum allowed " +
                    "lateness to " + maxOutOfOrderness + ". This parameter cannot be negative.");
        }
        this.maxOutOfOrderness = maxOutOfOrderness.toMilliseconds();
        this.currentMaxTimestamp = 0L;
    }

    public long getMaxOutOfOrdernessInMillis() {
        return maxOutOfOrderness;
    }

    @Override
    public final Watermark getCurrentWatermark() {
        a = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        return a;
    }

    @Override
    public final long extractTimestamp(Tuple2<String, Long> t, long previousElementTimestamp) {
        long timestamp = t.f1;
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
        System.out.println(("timestamp:" + t.f0 + "," + t.f1 + "|" + format.format(t.f1) + "," + currentMaxTimestamp + "|" +
                format.format(currentMaxTimestamp) + "," + a.toString()));
        return timestamp;
    }
}
