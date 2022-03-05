package com.bombangrybirds.actors;

import static com.bombangrybirds.extra.Utils.BIRDHEROE;
import static com.bombangrybirds.extra.Utils.WORLD_WIDTH;
import static com.bombangrybirds.screens.GameScreen.ortCamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BirdHeroe extends Actor {


    //se crean diferentes estados del juego
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DEAD = 1;
    private static final float JUMP_SPEED = 1f;

    //se controla el estado con un atributo
    public int state;

    //animacion
    private Animation<TextureRegion> birdAnimation;
    private Vector2 position;

    //se crean sonidos para el  heroe.
    private Sound jumpSound;//se cre sonido para el salto o movimiento del heroe
    private Sound deadSound;//se crea sonido para la muerte

    private World world;

    private float stateTime;

    private Body body;

    private Fixture fixture;

    public BirdHeroe(World world, Animation<TextureRegion> animation,Sound soundJump,Sound soundDead, Vector2 position) {
        this.birdAnimation = animation;
        this.position      = position;
        this.world         = world;
        this.jumpSound = soundJump;
        this.deadSound = soundDead;
        stateTime = 0f;
        state = STATE_NORMAL;
        createBody();
        createFixture();
    }


    public void createBody(){
        //Creamos BodyDef
        BodyDef bodyDef = new BodyDef();
        //Position
        bodyDef.position.set(position);

        //tipo
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        this.body = this.world.createBody(bodyDef);
    }


    public void createFixture(){
        //Shape
        CircleShape circle = new CircleShape();
        //radio
        circle.setRadius(0.22f);
        //createFixture
        this.fixture = this.body.createFixture(circle,3);

        this.fixture.setUserData(BIRDHEROE);//asocio el cuerpo con un identificador
        //dispose
        circle.dispose();
    }

    //para cambiar estado del heroe cuando colisione
    public void dead(){
        this.state = STATE_DEAD;
        this.stateTime = 0;
        this.deadSound.play();//sonido al morir el heroe
    }

    @Override
    public void act(float delta) {
        //se consigue la posicion en pixeles donde se clica
        float xPixel =Gdx.input.getX();
        float yPixel =Gdx.input.getY();

        //convierto la posicion en pixeles a una posicion en el world
        Vector3 vector=new Vector3(xPixel,yPixel,0);
        vector=ortCamera.unproject(vector);

        boolean jump = Gdx.input.justTouched();

        if(jump && this.state == STATE_NORMAL && vector.x>WORLD_WIDTH/2){//si se clicas en la mitad derecha de la pantalla y el Heroe está vivo  el BirdHeroe
            this.body.setLinearVelocity(2f, JUMP_SPEED);            ///se mueve hacia la derecha
            this.jumpSound.play();
        }
        if(jump && this.state == STATE_NORMAL && vector.x<WORLD_WIDTH/2){///si clicas en la mitad izquierda de la pantalla  y el Heroe esta vivo el BirdHeroe
            this.body.setLinearVelocity(-2f, JUMP_SPEED);             //se mueve hacia la izquierda
            this.jumpSound.play();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(body.getPosition().x-0.33f, body.getPosition().y-0.27f);
        batch.draw(this.birdAnimation.getKeyFrame(stateTime,true),getX(),getY(), 0.6f,0.6f);

        stateTime += Gdx.graphics.getDeltaTime();

    }


    public void detach(){

        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);

    }
}
