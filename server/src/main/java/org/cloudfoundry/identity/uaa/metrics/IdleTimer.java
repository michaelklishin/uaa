/*
 * ****************************************************************************
 *     Cloud Foundry
 *     Copyright (c) [2009-2017] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 * ****************************************************************************
 */

package org.cloudfoundry.identity.uaa.metrics;

/**
 * Calculates the time that a server is idle (no requests processing)
 * The idle time calculator starts as soon as this object is created.
 */
public class IdleTimer {

    private int inflightRequests = 0;
    private long idleTime = 0;
    private long lastIdleStart = System.currentTimeMillis();
    private final long startTime = System.currentTimeMillis();
    private long requestCount = 0;

    public synchronized void endRequest() {
        switch (--inflightRequests) {
            case 0:
                lastIdleStart = System.currentTimeMillis();
                break;
            case -1:
                throw new IllegalStateException("Illegal end request invocation, no request in flight");
            default:
                break;
        }
        requestCount++;
    }

    public synchronized void startRequest() {
        switch (++inflightRequests) {
            case 1:
                idleTime += (System.currentTimeMillis() - lastIdleStart);
                break;
            default:
                break;
        }

    }

    public int getInflightRequests() {
        return inflightRequests;
    }

    public synchronized long getIdleTime() {
        if (inflightRequests == 0) {
            return (System.currentTimeMillis() - lastIdleStart) + idleTime;
        } else {
            return idleTime;
        }
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getRunTime() {
        return System.currentTimeMillis() - startTime;
    }
}
