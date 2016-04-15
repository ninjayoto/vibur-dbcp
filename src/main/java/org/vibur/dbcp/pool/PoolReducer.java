/**
 * Copyright 2015 Simeon Malchev
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

package org.vibur.dbcp.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibur.dbcp.ViburDBCPConfig;
import org.vibur.dbcp.ViburDBCPException;
import org.vibur.dbcp.util.pool.ConnHolder;
import org.vibur.objectpool.reducer.SamplingPoolReducer;
import org.vibur.objectpool.reducer.ThreadedPoolReducer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.vibur.dbcp.util.ViburUtils.getPoolName;


/**
 * The pool reducer class - instantiated via reflection.
 *
 * @see org.vibur.dbcp.ViburDBCPDataSource
 *
 * @author Simeon Malchev
 */
@SuppressWarnings("unused")
public class PoolReducer extends SamplingPoolReducer<ConnHolder> {

    private static final Logger logger = LoggerFactory.getLogger(PoolReducer.class);

    private final ViburDBCPConfig config;

    public PoolReducer(ViburDBCPConfig config) {
        super(config.getPool(), config.getReducerTimeIntervalInSeconds(), SECONDS, config.getReducerSamples());
        this.config = config;
    }

    @Override
    protected void afterReduce(int reduction, int reduced, Throwable thrown) {
        if (thrown != null) {
            logger.warn("While trying to reduce pool {} by {} elements", getPoolName(config), reduction, thrown);
            if (!(thrown instanceof ViburDBCPException))
                terminate();
        }
        else
            logger.debug("Pool {}, intended reduction {} actual {}.", getPoolName(config), reduction, reduced);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for pool " + config.getName();
    }
}

