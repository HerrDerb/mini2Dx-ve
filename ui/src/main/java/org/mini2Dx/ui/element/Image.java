/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ui.element;

import java.util.LinkedList;
import java.util.Queue;

import org.mini2Dx.core.graphics.TextureRegion;
import org.mini2Dx.core.serialization.annotation.ConstructorArg;
import org.mini2Dx.core.serialization.annotation.Field;
import org.mini2Dx.core.serialization.annotation.PostDeserialize;
import org.mini2Dx.ui.render.ImageRenderNode;
import org.mini2Dx.ui.render.ParentRenderNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * Wraps a {@link Texture} or {@link TextureRegion} as a {@link UiElement}
 */
public class Image extends UiElement {
	private static final String LOGGING_TAG = Image.class.getSimpleName();
	
	protected ImageRenderNode renderNode;
	private TextureRegion textureRegion;

	@Field
	private String texturePath;
	@Field(optional = true)
	private String atlas;
	@Field(optional = true)
	private boolean responsive = false;
	@Field(optional=true)
	private boolean flipX = false;
	@Field(optional=true)
	private boolean flipY = false;
	
	private String cachedTexturePath;

	/**
	 * Constructor. Generates a unique ID for this {@link Image}
	 */
	public Image() {
		super(null);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The unique ID for this {@link Image}
	 */
	public Image(@ConstructorArg(clazz = String.class, name = "id") String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The unique ID for this {@link Image}
	 * @param texturePath
	 *            The path for the {@link Texture} to be loaded by the
	 *            {@link AssetManager}
	 */
	public Image(String id, String texturePath) {
		super(id);
		setTexturePath(texturePath);
	}

	/**
	 * Constructor. Generates a unique ID for this {@link Image}
	 * 
	 * @param texture
	 *            The {@link Texture} to use
	 */
	public Image(Texture texture) {
		this(null, texture);
	}

	/**
	 * Constructor. Generates a unique ID for this {@link Image}
	 * 
	 * @param textureRegion
	 *            The {@link TextureRegion} to use
	 */
	public Image(TextureRegion textureRegion) {
		this(null, textureRegion);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The unique ID for this {@link Image}
	 * @param texture
	 *            The {@link Texture} to use
	 */
	public Image(String id, Texture texture) {
		super(id);
		this.textureRegion = new TextureRegion(texture);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The unique ID for this {@link Image}
	 * @param textureRegion
	 *            The {@link TextureRegion} to use
	 */
	public Image(String id, TextureRegion textureRegion) {
		super(id);
		this.textureRegion = textureRegion;
	}
	
	@PostDeserialize
	public void postDeserialize() {
		this.cachedTexturePath = texturePath;
	}

	@Override
	public void attach(ParentRenderNode<?, ?> parentRenderNode) {
		if (renderNode != null) {
			return;
		}
		renderNode = new ImageRenderNode(parentRenderNode, this);
		parentRenderNode.addChild(renderNode);
	}

	@Override
	public void detach(ParentRenderNode<?, ?> parentRenderNode) {
		if (renderNode == null) {
			return;
		}
		parentRenderNode.removeChild(renderNode);
		renderNode = null;
	}

	/**
	 * Returns the current {@link TextureRegion} for this {@link Image}
	 * 
	 * @param assetManager
	 *            The game's {@link AssetManager}
	 * @return Null if no {@link TextureRegion} has been set
	 */
	public TextureRegion getTextureRegion(AssetManager assetManager) {
		if (atlas != null) {
			if (texturePath != null) {
				TextureAtlas textureAtlas = assetManager.get(atlas, TextureAtlas.class);
				if(textureAtlas == null) {
					Gdx.app.error(LOGGING_TAG, "Could not find texture atlas " + atlas);
					return null;
				}
				AtlasRegion atlasRegion = textureAtlas.findRegion(texturePath);
				if(atlasRegion == null) {
					Gdx.app.error(LOGGING_TAG, "Could not find " + texturePath + " in texture atlas " + atlas);
					return null;
				}
				textureRegion = new TextureRegion(atlasRegion);
				cachedTexturePath = texturePath;
				texturePath = null;
			}
		} else if (texturePath != null) {
			textureRegion = new TextureRegion(assetManager.get(texturePath, Texture.class));
			cachedTexturePath = texturePath;
			texturePath = null;
		}
		return textureRegion;
	}

	/**
	 * Sets the current {@link TextureRegion} used by this {@link Image}
	 * 
	 * @param textureRegion
	 *            The {@link TextureRegion} to use
	 */
	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	/**
	 * Sets the current {@link Texture} used by this {@link Image}
	 * 
	 * @param texture
	 *            The {@link Texture} to use
	 */
	public void setTexture(Texture texture) {
		if (texture == null) {
			setTextureRegion(null);
		} else {
			setTextureRegion(new TextureRegion(texture));
		}
	}

	/**
	 * Returns the current texture path
	 * 
	 * @return Null if no path is used
	 */
	public String getTexturePath() {
		return cachedTexturePath;
	}

	/**
	 * Sets the current texture path. This will set the current
	 * {@link TextureRegion} by loading it via the {@link AssetManager}
	 * 
	 * @param texturePath
	 *            The path to the texture
	 */
	public void setTexturePath(String texturePath) {
		if(this.texturePath != null && this.texturePath.equals(texturePath)) {
			return;
		}
		
		this.cachedTexturePath = texturePath;
		this.texturePath = texturePath;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	/**
	 * Returns the atlas (if any) to look up the texture in
	 * @return Null by default
	 */
	public String getAtlas() {
		return atlas;
	}

	/**
	 * Sets the atlas to look up the texture in
	 * @param atlas Null if the texture should not be looked up via an atlas
	 */
	public void setAtlas(String atlas) {
		if(this.atlas != null && this.atlas.equals(atlas)) {
			return;
		}
		
		this.atlas = atlas;
		
		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	/**
	 * Returns if this {@link Image} should scale to the size of its parent
	 * 
	 * @return False by default
	 */
	public boolean isResponsive() {
		return responsive;
	}

	/**
	 * Sets if this {@link Image} should scale to the size of its parent
	 * @param responsive
	 */
	public void setResponsive(boolean responsive) {
		this.responsive = responsive;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public void setVisibility(Visibility visibility) {
		if (visibility == null) {
			return;
		}
		if (this.visibility == visibility) {
			return;
		}
		this.visibility = visibility;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}
	
	@Override
	public void syncWithRenderNode() {
		while (!effects.isEmpty()) {
			renderNode.applyEffect(effects.poll());
		}
		processDeferred();
	}

	@Override
	public void setStyleId(String styleId) {
		if (styleId == null) {
			return;
		}
		if (this.styleId.equals(styleId)) {
			return;
		}
		this.styleId = styleId;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;

		if (renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	/**
	 * Returns if the texture should be flipped horizontally during rendering
	 * @return
	 */
	public boolean isFlipX() {
		return flipX;
	}

	/**
	 * Sets if the texture should be flipped horizontally during rendering
	 * @param flipX True if the texture should be flipped
	 */
	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	/**
	 * Returns if the texture should be flipped vertically during rendering
	 * @return
	 */
	public boolean isFlipY() {
		return flipY;
	}
	
	/**
	 * Sets if the texture should be flipped vertically during rendering
	 * @param flipY True if the texture should be flipped
	 */
	public void setFlipY(boolean flipY) {
		this.flipY = flipY;
	}
}
