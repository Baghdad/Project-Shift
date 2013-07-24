package org.annoyingkittens.projectshift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Author: Bogdanov Kirill
 * Date: 16.05.13
 * Time: 1:03
 */
public class Assets {
    public static TextureRegion tile;
    public static Animation kyraLeftRun;
    public static Animation kyraRightRun;
    public static Animation kyraIdleLeft;
    public static Animation kyraIdleRight;
    public static Animation kyraLeftJump;
    public static Animation kyraRightJump;
    public static Animation kyraLeftFall;
    public static Animation kyraRightFall;
    public static Animation kyraLeftDroop;
    public static Animation kyraRightDroop;
    public static Animation kyraLeftClimb;
    public static Animation kyraRightClimb;

    public static Texture kyraRunTexture;
    public static Texture kyraJumpTexture;
    public static Texture kyraFallTexture;
    public static Texture kyraClimbTexture;

    public static Texture items;
    public static TextureRegion soundOn;
    public static TextureRegion soundOff;

    public static TextureRegion[][] rightRunRegion;
    public static TextureRegion[][] leftRunRegion;
    public static TextureRegion[] rightIdleRegion;
    public static TextureRegion[] leftIdleRegion;
    public static TextureRegion[] rightJumpRegion;
    public static TextureRegion[] leftJumpRegion;
    public static TextureRegion[] rightFallRegion;
    public static TextureRegion[] leftFallRegion;
    public static TextureRegion[] rightClimbRegion;
    public static TextureRegion[] leftClimbRegion;
    public static Music shiftMusic;

    public static boolean soundEnabled = false;

    public static Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() {
        items = loadTexture("data/items.png");
        tile = new TextureRegion(loadTexture("data/tile.png"), 0, 0, 20, 20);
        soundOff = new TextureRegion(items, 0, 0, 64, 64);
        soundOn = new TextureRegion(items, 64, 0, 64, 64);

        kyraRunTexture = loadTexture("data/kyra_run.png");
        kyraJumpTexture = loadTexture("data/kyra_jump.png");
        kyraFallTexture = loadTexture("data/kyra_fall.png");
        kyraClimbTexture = loadTexture("data/kyra_climb.png");
        rightRunRegion = new TextureRegion(kyraRunTexture).split(300, 360);
        leftRunRegion = new TextureRegion(kyraRunTexture).split(300, 360);
        rightIdleRegion = new TextureRegion(kyraRunTexture).split(57, 360)[0];
        leftIdleRegion = new TextureRegion(kyraRunTexture).split(57, 360)[0];
        rightJumpRegion = new TextureRegion(kyraJumpTexture).split(121, 184)[0];
        leftJumpRegion = new TextureRegion(kyraJumpTexture).split(121, 184)[0];
        rightFallRegion = new TextureRegion(kyraFallTexture).split(81, 140)[0];
        leftFallRegion = new TextureRegion(kyraFallTexture).split(81, 140)[0];
        rightClimbRegion = new TextureRegion(kyraClimbTexture).split(60, 184)[0];
        leftClimbRegion = new TextureRegion(kyraClimbTexture).split(60, 184)[0];
        TextureRegion[] rightRun = new TextureRegion[30];
        TextureRegion[] leftRun = new TextureRegion[30];
        TextureRegion[] rightJump = new TextureRegion[9];
        TextureRegion[] leftJump = new TextureRegion[9];
        TextureRegion[] rightFall = new TextureRegion[4];
        TextureRegion[] leftFall = new TextureRegion[4];
        TextureRegion[] rightClimb = new TextureRegion[28];
        TextureRegion[] leftClimb = new TextureRegion[28];
        int count = 0;
        for (int i = 0; i < 28; i++) {
            if (i < 4) {
                rightFall[i] = rightFallRegion[i];
                leftFall[i] = leftFallRegion[i];
            }
            if (i < 9) {
                rightJump[i] = rightJumpRegion[i];
                leftJump[i] = leftJumpRegion[i];
            }
            rightClimb[i] = rightClimbRegion[i];
            leftClimb[i] = leftClimbRegion[i];
        }
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                rightRun[count] = rightRunRegion[i][j];
                leftRunRegion[i][j].flip(true, false);
                leftRun[count++] = leftRunRegion[i][j];
            }
        }
        for (TextureRegion region : leftJump) {
            region.flip(true, false);
        }
        for (TextureRegion region : leftFall) {
            region.flip(true, false);
        }
        for (TextureRegion region : leftClimb) {
            region.flip(true, false);
        }
        leftIdleRegion[0].flip(true, false);
        TextureRegion leftIdle = leftIdleRegion[0];
        TextureRegion rightIdle = rightIdleRegion[0];
        kyraIdleLeft = new Animation(0.5f, leftIdle);
        kyraIdleRight = new Animation(0.5f, rightIdle);
        kyraRightRun = new Animation(0.03f, rightRun);
        kyraLeftRun = new Animation(0.03f, leftRun);
        kyraRightJump = new Animation(0.1f, rightJump);
        kyraLeftJump = new Animation(0.1f, leftJump);
        kyraRightFall = new Animation(0.2f, rightFall);
        kyraLeftFall = new Animation(0.2f, leftFall);
        kyraRightDroop = new Animation(0.5f, rightJump[8]);
        kyraLeftDroop = new Animation(0.5f, leftJump[8]);
        kyraRightClimb = new Animation(0.1f, rightClimb);
        kyraLeftClimb = new Animation(0.1f, leftClimb);
        shiftMusic = Gdx.audio.newMusic(Gdx.files.internal("data/music/Dev_Environment.mp3"));
        shiftMusic.setLooping(true);
    }
}
