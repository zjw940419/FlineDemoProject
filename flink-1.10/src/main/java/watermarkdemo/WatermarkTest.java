package watermarkdemo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author zjwpro@foxmail.com
 * @date 2019/4/20 10:14 PM
 * mac nc -l
 *
 */
public class WatermarkTest {
    public static void main(String[] args) throws Exception {
        String hostName = "localhost";
        int port = 9000;
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(2000);
        env.setParallelism(1);
        //连接socket获取输入的数据
        DataStreamSource<String> input = env.socketTextStream(hostName, port, "\n");
        DataStream<Tuple2<String, Long>> raw
                = input.map(new MapFunction<String, Tuple2<String,Long>>() {
            @Override
            public Tuple2<String, Long> map(String s) throws Exception {
                String[] arr = s.split(",");
                String code = arr[0];
                Long time = Long.parseLong(arr[1]);
                return Tuple2.of(code, time);
            }
        }).assignTimestampsAndWatermarks(new MyTimestampExtractor(Time.seconds(10)));

        DataStream<String> window = raw.keyBy(1)
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                .apply(new WindowFunction<Tuple2<String, Long>, String, Tuple, TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple2<String, Long>> input, Collector<String> out) throws Exception {
                        LinkedList<Tuple2<String, Long>> data = new LinkedList<>();
                        for (Tuple2<String, Long> tuple2 : input) {
                            data.add(tuple2);
                        }
                        data.sort(new Comparator<Tuple2<String, Long>>() {
                            @Override
                            public int compare(Tuple2<String, Long> o1, Tuple2<String, Long> o2) {
                                return o1.f1.compareTo(o2.f1);
                            }
                        });
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String msg = String.format("key:%s,  window:[ %s  ,  %s ), elements count:%d, elements time range:[ %s  ,  %s ]", tuple.getField(0)
                                , format.format(new Date(window.getStart()))
                                , format.format(new Date(window.getEnd()))
                                , data.size()
                                , format.format(new Date(data.getFirst().f1))
                                , format.format(new Date(data.getLast().f1))
                        );
                        out.collect(msg);

                    }
                });
        window.print();
        env.execute();
    }
}
