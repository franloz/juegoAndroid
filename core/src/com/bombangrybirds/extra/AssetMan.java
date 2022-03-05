package com.bombangrybirds.extra;

import static com.bombangrybirds.extra.Utils.ATLAS_MAP;
import static com.bombangrybirds.extra.Utils.BACKGROUND_IMAGE;
import static com.bombangrybirds.extra.Utils.FONT_FNT;
import static com.bombangrybirds.extra.Utils.FONT_PNG;
import static com.bombangrybirds.extra.Utils.GAMEOVER_IMAGE;
import static com.bombangrybirds.extra.Utils.GETREADY_IMAGE;
import static com.bombangrybirds.extra.Utils.MUSIC;
import static com.bombangrybirds.extra.Utils.SOUND_DEAD;
import static com.bombangrybirds.extra.Utils.SOUND_JUMP;
import static com.bombangrybirds.extra.Utils.TOUCH_IMAGE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

//Esta clase centraliza la gestión de los recursos gráficos como texturas, regiones, o animaciones.
public class AssetMan {

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public AssetMan() {
        this.assetManager = new AssetManager();
        assetManager.load(ATLAS_MAP, TextureAtlas.class);
        //2.1 Sound
        assetManager.load(SOUND_JUMP, Sound.class);//cargamos el sonido de salto
        assetManager.load(SOUND_DEAD, Sound.class);//cargamos el sonido de muerte
        //2.2 Music
        assetManager.load(MUSIC, Music.class);//cargamos la musica

        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_MAP);

    }

    public TextureRegion getTouch() {
        return this.textureAtlas.findRegion(TOUCH_IMAGE);
    }
    public TextureRegion getGetReady() {
        return this.textureAtlas.findRegion(GETREADY_IMAGE);
    }
    public TextureRegion getGameOver() {
        return this.textureAtlas.findRegion(GAMEOVER_IMAGE);
    }

    //Creamos un metodo que devuelva la parte de la imagen que corresponde al fondo.
    public TextureRegion getBackground() {
        return this.textureAtlas.findRegion(BACKGROUND_IMAGE);
    }

    public Animation<TextureRegion> getBirdHeroeAnimation(){
        return new Animation<TextureRegion>(0.33f,
                textureAtlas.findRegion("birdheroe1"),
                textureAtlas.findRegion("birdheroe2"),
                textureAtlas.findRegion("birdheroe3"));
    }
    public Animation<TextureRegion> getBirdBombAnimation(){
        return new Animation<TextureRegion>(0.33f,
                textureAtlas.findRegion("birdbomb1"),
                textureAtlas.findRegion("birdbomb2"),
                textureAtlas.findRegion("birdbomb3"));
    }

    public Animation<TextureRegion> getExplosionAnimation(){
        return new Animation<TextureRegion>(0.25f,
                textureAtlas.findRegion("explosion1"),
                textureAtlas.findRegion("explosion2"),
                textureAtlas.findRegion("explosion3"),
                textureAtlas.findRegion("explosion4"));
    }

    public Sound getJumpSound(){
        return this.assetManager.get(SOUND_JUMP);
    }

    public Sound getDeadSound(){
        return this.assetManager.get(SOUND_DEAD);
    }

    public Music getMusicBG(){
        return this.assetManager.get(MUSIC);
    }

    public BitmapFont getFont(){
        return new BitmapFont(Gdx.files.internal(FONT_FNT),Gdx.files.internal(FONT_PNG), false);
    }

}
