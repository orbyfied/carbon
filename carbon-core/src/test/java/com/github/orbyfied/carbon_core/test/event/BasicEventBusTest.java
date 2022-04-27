package com.github.orbyfied.carbon_core.test.event;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.handler.BasicHandler;
import com.github.orbyfied.carbon.event.EventListener;
import com.github.orbyfied.carbon.event.util.Pipelines;
import org.junit.jupiter.api.Test;

public class BasicEventBusTest {

    public static final EventBus EVENT_BUS = new EventBus();

        /* ---- 1 ---- */

    @Test
    public void testBasicListener() {

        EVENT_BUS.withDefaultPipelineFactory((bus, event) -> Pipelines.mono(bus));
        EVENT_BUS.register(new MyListener());

        YourMomEvent event = new YourMomEvent("hello");
        Class<YourMomEvent> eventClass = YourMomEvent.class;

        // ---- time
        long t1 = System.nanoTime();

        for (int i = 0; i < 1_000_000_000; i++)
            EVENT_BUS.postUnsafe(eventClass, event);

        // ---- time
        long t2 = System.nanoTime();
        long t   = t2 - t1;
        long tms = (t / 1_000_000);
        System.out.println("Time Elapsed: " + t + "ns (" + tms + "ms)");

    }

    public static class MyListener implements EventListener {

        @BasicHandler
        public void onYourMomEvent(YourMomEvent event) {

        }

    }

    /**
     * Custom event.
     */
    public static class YourMomEvent extends BusEvent {

        private final String text;

        public YourMomEvent(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

    }

}
