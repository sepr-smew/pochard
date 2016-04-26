package com.superduckinvaders.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.superduckinvaders.game.DuckGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public void onModuleLoad () {
        super.onModuleLoad();
        com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent ev) {
                Gdx.graphics.setWindowedMode(ev.getWidth(),ev.getHeight());
            }
        });
    }

    @Override
    public GwtApplicationConfiguration getConfig () {
        int height = com.google.gwt.user.client.Window.getClientHeight();
        int width = com.google.gwt.user.client.Window.getClientWidth();
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(width, height);
        cfg.preserveDrawingBuffer = true;
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener () {
                return new DuckGame();
        }
}