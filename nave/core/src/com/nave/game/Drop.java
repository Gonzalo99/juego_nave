package com.nave.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {

    SpriteBatch batch;
    BitmapFont font;

    @Override
    public void create() {
            batch = new SpriteBatch();
            // Use LibGDX's default Arial font.
            font = new BitmapFont();
            this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
            super.render(); // important!
    }

    @Override
    public void dispose() {
            batch.dispose();
            font.dispose();
    }

}
