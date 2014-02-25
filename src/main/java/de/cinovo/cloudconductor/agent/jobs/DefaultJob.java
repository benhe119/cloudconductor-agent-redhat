package de.cinovo.cloudconductor.agent.jobs;

/*
 * #%L
 * Node Agent for cloudconductor framework
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.agent.AgentState;
import de.cinovo.cloudconductor.agent.exceptions.ExecutionError;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class DefaultJob implements AgentJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJob.class);
	
	
	@Override
	public void run() {
		// only run if no other blocking job is currently running
		if (AgentState.executionLock.tryLock()) {
			try {
				try {
					new PackageHandler().run();
				} catch (ExecutionError e) {
					if (e.getCause() instanceof CloudConductorException) {
						DefaultJob.LOGGER.error(e.getMessage(), e);
					} else {
						DefaultJob.LOGGER.error(e.getMessage());
					}
				}
				try {
					new ServiceHandler().run();
				} catch (ExecutionError e) {
					DefaultJob.LOGGER.error(e.getMessage(), e);
				}
			} finally {
				AgentState.executionLock.unlock();
			}
		}
	}
	
	@Override
	public long getInititalDelay() {
		return 0;
	}
	
	@Override
	public long getRepeatTimer() {
		return 2;
	}
	
	@Override
	public TimeUnit getRepeatTimerUnit() {
		return TimeUnit.MINUTES;
	}
	
}
