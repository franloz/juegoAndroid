package com.bombangrybirds.screens;

import static com.bombangrybirds.extra.Utils.*;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombangrybirds.MainGame;
import com.bombangrybirds.actors.BirdBombs;
import com.bombangrybirds.actors.BirdHeroe;


//se implementa la interfaz ContactListener para las colisiones
public class GameScreen extends BaseScreen implements ContactListener {

    private Stage stage;//escenario

    private Image background;//imagen de fondo

    private BirdHeroe birdHeroe;//actor protagonista

    private Array<BirdBombs> arrayBomb;//array de actores enemigos

    private float TIME_BOMB = 0.3f;//indica en cuanto tiempo se crean las bombas, no la hacemos final porque su valor cambiara para aumentar la dificultad del juego
    private float timeToCreateBomb;

    private World world;//para gestionar la parte fisica

    private Music music;//musica de fondo

    private int numCounter=0;//variable que hará de contador en este caso almacenará el tiempo que aguanta vivo el personaje

    //private Box2DDebugRenderer debugRenderer;//para poder ver representacion del mundo fisico
    public static OrthographicCamera ortCamera;//hago estatica la camara porque me hará falta en  la clase BirdHeroe
                                                // para convertir la posicion al clicar en pantalla de pixeles a metros(unidades del World)

    private OrthographicCamera fontCamera;//cámara para proyectar la puntuación
    private BitmapFont score;

    private Thread hilo;//hilo que contará el tiempo que el personaje permanece vivo

    public GameScreen(MainGame mainGame){
        super(mainGame);

        //se crea el mundo con dos parametros desplazamiento en x y en y(gravedad)
        this.world = new World(new Vector2(0,-10),true);

        //Le pasamos al mundo el objeto que implemente la interfaz contactListener (en este caso será la propia instancia de GameScreen)
        this.world.setContactListener(this);

        //Inicializamos el stage creando previamente nuestro viewport le pasamos el tamaño de nuestro mundo
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH,WORLD_HEIGTH);
        this.stage = new Stage(fitViewport);//le añadimos la camara al stage creo

        //Inicializamos el array de bombas enemigas y la variable que almacenará el tiempo
        this.arrayBomb = new Array();
        this.timeToCreateBomb = 0f;

        //inicializamos la música de fondo
        this.music = this.mainGame.assetManager.getMusicBG();

        //Cargamos la cámara
        this.ortCamera = (OrthographicCamera) this.stage.getCamera();
        //this.debugRenderer = new Box2DDebugRenderer();

        //texto de la puntuación
        prepareScore();
    }

    @Override
    public void show() {
        addBackground();
        addFloor();
        addRoof();
        addRightWall();
        addLeftWall();
        counter();//se lanza el contador que en este juego contará el tiempo que el jugador es capáz de permanecer vivo
        addBirdHeroe();
        //se reproduce la música cuando aparezca la pantalla
        //loop
        this.music.setLooping(true);
        //play
        this.music.play();
    }

    //Creamos un método para configurar el texto de la puntuación
    private void prepareScore(){
        //se carga la fuente de la puntuación y su tamaño
        this.score = this.mainGame.assetManager.getFont();
        this.score.getData().scale(1f);
        //se crea la cámara, y se le da el tamaño de la PANTALLA (EN PIXELES) y luego se actualiza
        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH,SCREEN_HEIGHT);
        this.fontCamera.update();
    }

    public void addBirdBombs(float delta){
        Animation<TextureRegion> birdSpriteBomb = mainGame.assetManager.getBirdBombAnimation();//animación de las bombas
        if(birdHeroe.state == BirdHeroe.STATE_NORMAL) {//si heroe esta vivo
            //Se acumula delta hasta que llegue al tiempo que hemos establecido para que cree la bomba
            this.timeToCreateBomb+=delta;
            //si el contador llega a 10 se disminuye el tiempo de espera para crear bombas aumentando la dificultad del juego
            if(numCounter>=10){
                TIME_BOMB=0.2f;
            }
            //si el contador llega a 20 se disminuye el tiempo de espera para crear bombas aumentando la dificultad del juego
            if(numCounter>=20){
                TIME_BOMB=0.15f;
            }
            //si el tiempo acumulado es mayor o igual que el tiempo que hemos establecido, se crea una bomba
            if(this.timeToCreateBomb >= TIME_BOMB) {
                //se le resta el tiempo a la variable acumulada para que vuelva el contador a 0.
                this.timeToCreateBomb-= TIME_BOMB;
                //se establece de forma aleatoria tanto la posición en x como en y
                float posRandomY = MathUtils.random(5.4f,4f);
                float posRandomX = MathUtils.random(0.5f,7.6f);
                BirdBombs bomb = new BirdBombs(this.world, birdSpriteBomb, new Vector2(posRandomX, posRandomY));
                arrayBomb.add(bomb);
                this.stage.addActor(bomb);
            }
        }
    }

    //Creamos un método para eliminar bombas que esten fuera de la pantalla
    public void removeBombs(){
        for (BirdBombs bomb : this.arrayBomb) {
            //Si el mundo no está bloqueado, es decir, que no esté actualizando la física en ese preciso momento
            if(!world.isLocked()) {
                //y la bomba está fuera de la pantalla
                if(bomb.isOutOfScreen()) {
                    //Eliminamos los recursos de esa bomba
                    bomb.detach();
                    //y la eliminamos del escenario
                    bomb.remove();
                    //la eliminamos del array
                    arrayBomb.removeValue(bomb,false);
                }
            }
        }
    }

    //añade birdHeroe
    public void addBirdHeroe(){
        Animation<TextureRegion> birdSpriteHeroe = mainGame.assetManager.getBirdHeroeAnimation();//se obtienen las animaciones de birdheroe de asset
        Sound soundBirdHeroeJump = this.mainGame.assetManager.getJumpSound();
        Sound soundBirdHeroeDead = this.mainGame.assetManager.getDeadSound();
        this.birdHeroe = new BirdHeroe(this.world,birdSpriteHeroe,soundBirdHeroeJump,soundBirdHeroeDead, new Vector2(3.4f ,0.6f ));
        this.stage.addActor(this.birdHeroe);//se añade el actor al stage
    }

    //techo
    public void addRoof(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,WORLD_HEIGTH,WORLD_WIDTH,WORLD_HEIGTH);
        body.createFixture(edge, 1);
        edge.dispose();
    }

    // pared derecha
    public void addRightWall(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(WORLD_WIDTH,0,WORLD_WIDTH,WORLD_HEIGTH);
        Fixture fixtureRifhtWall=body.createFixture(edge, 1);
        fixtureRifhtWall.setUserData(RIGHTWALL);//al cuerpo lo asocio con un identificador
        edge.dispose();
    }

    //pared izquierda
    public void addLeftWall(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,0,0,WORLD_HEIGTH);
        Fixture fixtureLeftWall=body.createFixture(edge, 1);
        fixtureLeftWall.setUserData(LEFTWALL);//al cuerpo lo asocio con un identificador
        edge.dispose();
    }

    //suelo
    private void addFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,0,WORLD_WIDTH,0);
        Fixture fixtureFloor=body.createFixture(edge, 1);
        fixtureFloor.setUserData(FLOOR);//al cuerpo lo asocio con un identificador
        edge.dispose();
    }
    //metodo que configura el fondo
    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getBackground());//a nuestro background le ponemos el fondo del atlas
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH,WORLD_HEIGTH);
        this.stage.addActor(this.background);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.getBatch().setProjectionMatrix(ortCamera.combined);

        //se añaden las bombas en función del tiempo (delta)
        addBirdBombs(delta);

        //antes de dibujar el mundo, se le pasa al batch, los datos de
        // la cámara del mundo, para que vuelva a representarlo en función del tamaño de este
        this.stage.getBatch().setProjectionMatrix(ortCamera.combined);
        //se actualice la físca de los actores que tiene adscritos
        this.stage.act();
        this.world.step(delta,6,2); //Porqué 6 y 2? Por que así lo dice la documentación.//creo q marca con que frecuencia se actualiza el world
        this.stage.draw();//el satge se dibuja con toto lo q le hemos añadido

        //Actualizamos la cámara para que aplique cualquier cambio en las matrices internas.
        this.ortCamera.update();

        // Se le pasa el mundo físico y las matrices de la camara (combined)
        //this.debugRenderer.render(this.world, this.ortCamera.combined);

        //se eliminan las bombas que vayan saliendose de la pantalla
        removeBombs();

        //se carga la matriz de proyección con los datos de la cámara de la fuente,
        // para que proyecte el texto con las dimensiones en píxeles
        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.score.draw(this.stage.getBatch(), ""+numCounter,SCREEN_WIDTH/2, 400);
        this.stage.getBatch().end();
    }

    public void counter(){//este método hará de contador el cual hará referencia a la marca del jugador
        hilo = new Thread(new Runnable() {//se lanza un hilo donde en un bucle infinito irá sumando 1 a numCounter cada 1 seg
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1400);
                        numCounter++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        hilo.start();
    }

    @Override
    public void hide() {
        //quitamos los recursos de los actores de memoria
        this.birdHeroe.detach();
        this.birdHeroe.remove();
        //se para la música cuando se oculte la pantalla
        this.music.stop();
    }

    @Override
    public void dispose() {
        //eliminamos los recursos del stage
        this.stage.dispose();
        //eliminamos los recursos que retiene world
        this.world.dispose();

    }

    //------------------colisiones-----------------------------
    //se crea un método auxiliar areColider, para determinar qué objetos han colisionado
    public boolean areColider(Contact contact, Object objA, Object objB){
        return (contact.getFixtureA().getUserData().equals(objA) && contact.getFixtureB().getUserData().equals(objB)) ||
                (contact.getFixtureA().getUserData().equals(objB) && contact.getFixtureB().getUserData().equals(objA));//objeto a choca con objeto b u objeto b choca con a
    }

    //Método que se llamará cada vez que se produzca cualquier contacto
    @Override
    public void beginContact(Contact contact) {

        if(!areColider(contact, BIRDHEROE,FLOOR)) {//si el BirdHeroe no choca contra el suelo sino q choca con otro objeto muere
            //se para el hilo del contador al morir
            if(hilo.isAlive()) {
                hilo.interrupt();
            }
            this.world = new World(new Vector2(0,0),true);//para parar el movimiento del bridheroe
            this.birdHeroe.dead();//cambiamos el estado del pájaro a Dead
            //recorremos el array de BirdBombs y detenemos la caida de los que están creados y le cambiamos la animación por la explosión
            for(BirdBombs bomb:this.arrayBomb){
                Animation<TextureRegion> birdSpriteBomb = mainGame.assetManager.getExplosionAnimation();
                bomb.stopBombs(birdSpriteBomb);
            }
            //se para la música
            this.music.stop();
            //secuencia de acciones que tiene lugar cuando el BirdHeroe muere
            this.stage.addAction(Actions.sequence(

                    Actions.delay(1.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            mainGame.setScreen(mainGame.gameOverScreen);

                        }
                    })
            ));
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
