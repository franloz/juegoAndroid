package com.bombangrybirds.screens;

import static com.bombangrybirds.extra.Utils.WORLD_HEIGTH;
import static com.bombangrybirds.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombangrybirds.MainGame;

public class GameOverScreen extends BaseScreen {

    private Stage stage;
    private Image background;
    private Image gameover;
    private Image touch;


    public GameOverScreen(MainGame mainGame) {
        super(mainGame);
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH,WORLD_HEIGTH);
        this.stage = new Stage(fitViewport);
    }
    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH,WORLD_HEIGTH);
        this.stage.addActor(this.background);
    }

    public void addGameOver(){
        this.gameover = new Image(mainGame.assetManager.getGameOver());
        this.gameover.setPosition(0.8f, 2f);
        this.gameover.setSize(6.3f, 2f);
        this.stage.addActor(this.gameover);
    }

    public void addTouch() {
        this.touch = new Image(mainGame.assetManager.getTouch());
        this.touch.setPosition(3.6f, 0.5f);
        this.touch.setSize(1f, 1.3f);
        this.stage.addActor(this.touch);
    }

    @Override
    public void show() {
        addBackground();
        addGameOver();
        addTouch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.draw();
        boolean touch = Gdx.input.justTouched();
        if(touch){
            mainGame.setScreen(new GameScreen(mainGame));
        }
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
