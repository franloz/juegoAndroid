package com.bombangrybirds.actors;

import static com.bombangrybirds.extra.Utils.BIRDBOMB;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BirdBombs extends Actor {

    private static final float SPEED =-3f;//velocidad de caida de las bombas

    private Animation<TextureRegion> birdAnimation;
    private Vector2 position;

    private World world;

    private float stateTime;

    private Body body;

    private Fixture fixture;

    public BirdBombs(World world, Animation<TextureRegion> animation, Vector2 position) {
        this.birdAnimation = animation;
        this.position = position;
        this.world = world;
        stateTime = 0f;
        createBody();
        createFixture();

    }

    public void createBody() {
        //Creamos BodyDef
        BodyDef bodyDef = new BodyDef();
        //Position
        bodyDef.position.set(position);

        //tipo
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        //createBody de mundo
        this.body = this.world.createBody(bodyDef);
        float moveX = MathUtils.random(-1.4f,1.4f);//al caer la añado un movimiento en horizontal aleatorio
        this.body.setLinearVelocity(moveX,SPEED);

    }


    public void createFixture() {
        //Shape
        CircleShape circle = new CircleShape();
        //radio
        circle.setRadius(0.32f);

        //createFixture
        this.fixture = this.body.createFixture(circle, 8);
        this.fixture.setUserData(BIRDBOMB);//asocio el cuerpo con un identificador

        //dispose
        circle.dispose();
    }

    //se crea un método que nos diga si las bombas están fuera de la pantalla
    public boolean isOutOfScreen(){
        return this.body.getPosition().y <= -2f;
    }

    //se crea un método para detener el movimiento de las bombas y cambiar la animación a la de explosión
    public void stopBombs(Animation<TextureRegion> animation){
        this.body.setLinearVelocity(0,0);
        this.birdAnimation=animation;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(body.getPosition().x - 0.39f, body.getPosition().y - 0.31f);
        batch.draw(this.birdAnimation.getKeyFrame(stateTime, true), getX(), getY(), 0.78f, 0.78f);

        stateTime += Gdx.graphics.getDeltaTime();

    }


    public void detach() {
        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);

    }
}

