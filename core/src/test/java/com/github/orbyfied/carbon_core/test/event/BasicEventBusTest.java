package com.github.orbyfied.carbon_core.test.event;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.EventHandler;
import com.github.orbyfied.carbon.event.EventListener;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicEventBusTest {

    public static final EventBus EVENT_BUS = new EventBus();

        /* ---- 1 ---- */

    @Test
    public void testBasicListener() {

        EVENT_BUS.register(new MyListener());
        EVENT_BUS.bake(YourMomEvent.class);

        // ---- time
        long t1 = System.nanoTime();

        for (int i = 0; i < 1000; i++)
            EVENT_BUS.post(new YourMomEvent("hello " + i));

        // ---- time
        long t2 = System.nanoTime();
        long t   = t2 - t1;
        long tms = (t / 1_000_000);
        System.out.println("Time Elapsed: " + t + "ns (" + tms + "ms)");

    }

    public static int counter;

    public static class MyListener implements EventListener {

        @EventHandler
        public void onYourMomEvent(YourMomEvent event) {
//            System.out.println("my listener says \"" + event.getText() + "\"");
            counter++;
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

        ////////////////////////////////

        // required
        public static PipelineAccess<BusEvent> getPipeline(EventBus bus) {
            return BusEvent.createMonoPipeline(bus);
        }

    }

}
