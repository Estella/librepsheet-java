package com.repsheet.librepsheet;

import com.aaronbedra.orchard.CIDR;
import com.aaronbedra.orchard.OrchardException;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class BulkCIDRProcessor extends RecursiveTask<List<String>> {
    private static final int KEYSPACELENGTH = 3;
    private Set<String> blocks;
    private String block;
    private final String actor;

    public BulkCIDRProcessor(final Set<String> blocks, final String actor) {
        this.blocks = blocks;
        this.actor = actor;
    }

    public BulkCIDRProcessor(final String block, final String actor) {
        this.block = block;
        this.actor = actor;
    }

    @Override
    protected final List<String> compute() {
        List<String> results = new ArrayList<>();
        List<BulkCIDRProcessor> tasks = new ArrayList<>();

        if (blocks != null) {
            for (String b : blocks) {
                BulkCIDRProcessor task = new BulkCIDRProcessor(b, actor);
                task.fork();
                tasks.add(task);
            }
        } else {
            try {
                String[] parts = block.split(":");
                String cidr = StringUtils.join(Arrays.asList(parts).subList(0, parts.length - KEYSPACELENGTH), ":");
                if (CIDR.valueOf(cidr).contains(actor)) {
                    results.add(cidr);
                }
            } catch (OrchardException ignored) { }
        }
        computeTasks(results, tasks);
        return results;
    }

    protected final void computeTasks(final List<String> results, final List<BulkCIDRProcessor> tasks) {
        for (BulkCIDRProcessor result : tasks) {
            results.addAll(result.join());
        }
    }
}
