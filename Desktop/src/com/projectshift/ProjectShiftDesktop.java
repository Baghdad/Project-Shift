package com.projectshift;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Author: Bogdanov Kirill
 * Date: 01.09.12
 * Time: 18:43
 */
public class ProjectShiftDesktop {
    public static void main(String[] args) {
        new LwjglApplication(new ProjectShift(), "Project SHIFT", 480, 320, true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
