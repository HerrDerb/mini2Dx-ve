/**
 * Copyright (c) 2016 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.controller.deadzone;

import com.badlogic.gdx.math.Vector2;

/**
 * Implements an axis-based dead zone - suitable for tile-based games. See
 * <a href=
 * "http://www.gamasutra.com/blogs/JoshSutphin/20130416/190541/Doing_Thumbstick_Dead_Zones_Right.php">
 * Doing Thumbstick Dead Zones Right</a>.
 */
public class AxialDeadZone implements DeadZone {
	private final Vector2 rawValues = new Vector2(0f, 0f);
	private final Vector2 filteredValues = new Vector2(0f, 0f);

	private float threshold = 0.25f;
	private boolean dirty = true;

	@Override
	public void updateX(float x) {
		rawValues.x = x;
		dirty = true;
	}

	@Override
	public void updateY(float y) {
		rawValues.y = y;
		dirty = true;
	}

	@Override
	public float getX() {
		dirtyCheck();
		return filteredValues.x;
	}

	@Override
	public float getY() {
		dirtyCheck();
		return filteredValues.y;
	}

	@Override
	public float getDeadZone() {
		return threshold;
	}

	@Override
	public void setDeadZone(float deadZone) {
		this.threshold = deadZone;
	}

	private void dirtyCheck() {
		if (!dirty) {
			return;
		}
		if (Math.abs(rawValues.x) < threshold) {
			filteredValues.x = 0f;
		} else {
			filteredValues.x = rawValues.x;
		}
		if (Math.abs(rawValues.y) < threshold) {
			filteredValues.y = 0f;
		} else {
			filteredValues.y = rawValues.y;
		}
		dirty = false;
	}

	@Override
	public DeadZone copy() {
		return new AxialDeadZone();
	}
}
