package com.bombangrybirds;

import com.badlogic.gdx.Game;
import com.bombangrybirds.extra.AssetMan;
import com.bombangrybirds.screens.GameOverScreen;
import com.bombangrybirds.screens.GameScreen;
import com.bombangrybirds.screens.GetReadyScreen;


/*mini instrucciones:
 * el juego consiste en esquivar las bombas que están continuamente cayendo, para mover al personaje se debe pulsar en los
 * laterales de la pantalla, si se pulsa en la derecha el Heroe se desplazará hacia ese lado y al pulsar en el izquierdo se
 * desplazará hacia él, al llegar el contador a 10 el tiempo de espera de creación entre bombas disminuirá aumentando la dificulttad,
 * si se consigue llegar hasta el 20 el tiempo de espera de creación entre bombas volverá a disminuir, si sobrevive en este nivel
 * mucho tiempo será usted un PRO(si juega en ordenador, será más difícil el juego desde el principio)
 * */
public class MainGame extends Game {

	//pantalla del juego
	public GameScreen gameScreen;

	//pantalla inicial
	public GetReadyScreen getReadyScreen;

	//pantalla de GameOver
	public GameOverScreen gameOverScreen;

	//para usar los Asset
	public AssetMan assetManager;

	@Override
	public void create() {
		this.assetManager = new AssetMan();

		this.gameScreen = new GameScreen(this);
		this.gameOverScreen = new GameOverScreen(this);
		this.getReadyScreen = new GetReadyScreen(this);

		//al iniciar el juego salta la pantalla GetReady
		setScreen(this.getReadyScreen);


	}

}