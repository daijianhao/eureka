/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.eureka2.metric;

import java.util.concurrent.Callable;

import com.netflix.eureka2.registry.SourcedEurekaRegistry;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.registry.PreservableEurekaRegistry;
import com.netflix.eureka2.registry.Source.Origin;
import com.netflix.servo.monitor.BasicGauge;
import com.netflix.servo.monitor.Counter;

/**
 * @author Tomasz Bak
 */
public class EurekaRegistryMetrics extends EurekaMetrics {

    private final Counter registrationsLocal;
    private final Counter registrationsReplicated;
    private final Counter registrationsTotal;

    private final Counter updatesLocal;
    private final Counter updatesReplicated;
    private final Counter updatesTotal;

    private final Counter unregistrationsLocal;
    private final Counter unregistrationsReplicated;
    private final Counter unregistrationsTotal;

    public EurekaRegistryMetrics() {
        super("eurekaServerRegistry");
        registrationsLocal = newCounter("registrationsLocal");
        registrationsReplicated = newCounter("registrationsReplicated");
        registrationsTotal = newCounter("registrationsTotal");

        updatesLocal = newCounter("updatesLocal");
        updatesReplicated = newCounter("updatesReplicated");
        updatesTotal = newCounter("updatesTotal");

        unregistrationsLocal = newCounter("unregistrationsLocal");
        unregistrationsReplicated = newCounter("unregistrationsReplicated");
        unregistrationsTotal = newCounter("unregistrationsTotal");
    }

    public void incrementRegistrationCounter(Origin origin) {
        switch (origin) {
            case LOCAL:
                registrationsLocal.increment();
                break;
            case REPLICATED:
                registrationsReplicated.increment();
                break;
        }
        registrationsTotal.increment();
    }

    public void incrementUnregistrationCounter(Origin origin) {
        switch (origin) {
            case LOCAL:
                unregistrationsLocal.increment();
                break;
            case REPLICATED:
                unregistrationsReplicated.increment();
                break;
        }
        unregistrationsTotal.increment();
    }

    public void incrementUpdateCounter(Origin origin) {
        switch (origin) {
            case LOCAL:
                updatesLocal.increment();
                break;
            case REPLICATED:
                updatesReplicated.increment();
                break;
        }
        updatesTotal.increment();
    }

    public void setRegistrySizeMonitor(final SourcedEurekaRegistry<InstanceInfo> registry) {
        Callable<Integer> registrySizeCallable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return registry.size();
            }
        };
        BasicGauge<Integer> gauge = new BasicGauge<>(monitorConfig("registrySize"), registrySizeCallable);
        register(gauge);
    }

    public void setSelfPreservationMonitor(final PreservableEurekaRegistry registry) {
        Callable<Integer> selfPreservationCallable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return registry.isInSelfPreservation() ? 1 : 0;
            }
        };
        BasicGauge<Integer> gauge = new BasicGauge<>(monitorConfig("selfPreservation"), selfPreservationCallable);
        register(gauge);
    }
}