/**
 * Copyright (c) 2017 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.artemis.system;

import com.artemis.Aspect.Builder;
import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;

/**
 * An {@link EntitySystem} that will wait for an interval then queue all
 * entities to be updated over the duration of the next interval.
 */
public abstract class DispersedIntervalEntitySystem extends EntitySystem {
	protected final Queue<Integer> processingQueue = new Queue<Integer>();

	private float interval;
	private float timer;
	private float updateDelta;
	protected int entitiesPerUpdate;

	/**
	 * Constructor
	 * @param aspect The {@link Aspect} to match entities
	 * @param interval The interval in seconds
	 */
	public DispersedIntervalEntitySystem(Builder aspect, float interval) {
		super(aspect);
		this.interval = interval;
		this.timer = interval;
	}

	/**
	 * Updates an entity
	 * 
	 * @param entityId
	 *            The entity id
	 * @param delta
	 *            The delta since the last update
	 */
	protected abstract void update(int entityId, float delta);

	@Override
	protected void processSystem() {
		updateDelta += getWorld().getDelta();
		for (int i = 0; i < entitiesPerUpdate && processingQueue.size > 0; i++) {
			int nextEntityId = processingQueue.removeFirst();
			update(nextEntityId, updateDelta);
		}
		
		timer += getWorld().getDelta();
		if (timer >= interval) {
			updateDelta = timer;
			timer = timer % interval;

			int totalEntities = getEntityIds().size();
			
			entitiesPerUpdate = MathUtils.round((totalEntities + processingQueue.size) / interval);
			entitiesPerUpdate = Math.max(1, entitiesPerUpdate);
			
			for (int i = 0; i < totalEntities; i++) {
				processingQueue.addLast(getEntityIds().get(i));
			}
		}
	}

	/**
	 * Returns the interval of the system
	 * @return
	 */
	public float getInterval() {
		return interval;
	}

	/**
	 * Sets the interval of the system
	 * @param interval
	 */
	public void setInterval(float interval) {
		this.interval = interval;
	}
}
