package com.bombangrybirds.screens;

import com.badlogic.gdx.Screen;
import com.bombangrybirds.MainGame;

public class BaseScreen implements Screen {

    protected MainGame mainGame;//creamos una variable de la clase MainGame para poder usarla en las clases q heredan de BaseScreen

    public BaseScreen(MainGame mainGame){
        this.mainGame = mainGame;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}


